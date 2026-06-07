package forever.pajang.minethespire.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.item.OriginalRelicItem;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CombatState {
    public static final int DURATION = 10 * 20; // at 10s exit combat state if no combat operations
    public static final int DETECT_TICK_REMAINS = 5 * 20; // at 5s detect if all hostiles died
    public static final int SYNC_INTERVAL = 20;
    public static final double DETECT_RADIUS = 64.0;
    public static final MapCodec<CombatState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("combat_ticks").forGetter(CombatState::combatTicks),
            Codec.list(UUIDUtil.CODEC).fieldOf("hostiles").forGetter(CombatState::hostiles)
    ).apply(instance, CombatState::new));

    private int combatTicks = 0;
    private final Set<UUID> hostiles = new HashSet<>();
    private boolean dirty = false;

    public CombatState() {}

    private CombatState(int combatTicks, Collection<UUID> hostiles) {
        this.combatTicks = combatTicks;
        this.hostiles.addAll(hostiles);
    }

    public void markDirty() {
        dirty = true;
    }

    public void clearDirty() {
        dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean inCombat() {
        return combatTicks > 0;
    }


    private void hostilize(LivingEntity target) {
        combatTicks = DURATION;
        if (target != null) {
            hostiles.add(target.getUUID());
        }
    }

    public boolean tick() {
        boolean willExit = combatTicks == 1;

        if (combatTicks > 0) {
            combatTicks--;
        }

        if (willExit) {
            markDirty();
            hostiles.clear();
            return true;
        } else return false;
    }

    private boolean isHostile(UUID uuid) {
        return inCombat() && hostiles.contains(uuid);
    }

    public List<UUID> hostiles() {
        return List.copyOf(hostiles);
    }

    public int combatTicks() {
        return combatTicks;
    }

    public Set<LivingEntity> getHostiles(LivingEntity owner) {
        if (!inCombat()) {
            return Set.of();
        }
        Set<LivingEntity> hostileEntities = new HashSet<>();
        forEachValidHostile(owner, hostileEntities::add);
        return hostileEntities;
    }

    public void forEachValidHostile(LivingEntity owner, Consumer<LivingEntity> operation) {
        Level level = owner.level();
        boolean isServer = !level.isClientSide();
        for (UUID uuid : List.copyOf(this.hostiles)) {
            Entity entity = level.getEntity(uuid);
            if (entity instanceof LivingEntity livingEntity && entity != owner && entity.isAlive() && !(entity.isRemoved()) && livingEntity.distanceToSqr(owner) < DETECT_RADIUS * DETECT_RADIUS) {
                operation.accept(livingEntity);
            } else if (isServer) {
                hostiles.remove(uuid);
                markDirty();
            }
        }
    }

    public Set<LivingEntity> getHostilesIf(LivingEntity owner, Predicate<LivingEntity> condition) {
        return getHostiles(owner).stream().filter(condition).collect(Collectors.toSet());
    }

    public boolean anyHostile(LivingEntity owner, Predicate<LivingEntity> condition) {
        return getHostiles(owner).stream().anyMatch(condition);
    }

    public static void onAttack(LivingEntity attacker, Entity target) {
        if (attacker.level().isClientSide() || !(target instanceof LivingEntity livingTarget) || attacker == livingTarget) {
            return;
        }

        bothHostilize(attacker, livingTarget);
    }

    public static void onHurt(LivingEntity victim, DamageSource source) {
        if (victim.level().isClientSide()) {
            return;
        }

        Entity attacker = source.getEntity();
        if (attacker == null) {
            attacker = source.getDirectEntity();
        }

        if (attacker instanceof LivingEntity livingAttacker && livingAttacker != victim) {
            bothHostilize(victim, livingAttacker);
        }
    }

    private static void bothHostilize(LivingEntity first, LivingEntity second) {
        hostilize(first, second);
        hostilize(second, first);
    }

    private static void hostilize(LivingEntity entity, LivingEntity target) {
        CombatState state = entity.getData(ModAttachments.COMBAT_STATE);
        boolean willEnter = !state.inCombat();
        state.hostilize(target);
        if (willEnter) {
            onEnterCombat(entity);
        }
        state.markDirty();
    }

    public static void tickEntity(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }

        CombatState state = entity.getData(ModAttachments.COMBAT_STATE);
        boolean exited = state.tick();
        if (exited) {
            onExitCombat(entity);
        }
        if (state.combatTicks == DETECT_TICK_REMAINS) {
            state.forEachValidHostile(entity, ignored -> {});
            if (state.hostiles.isEmpty()) {
                state.combatTicks = 0;
                state.markDirty();
                onExitCombat(entity);
            }
        }
        if (state.isDirty() || shouldSyncCombatTicks(entity, state)) {
            entity.syncData(ModAttachments.COMBAT_STATE);
            state.clearDirty();
        }
    }

    private static boolean shouldSyncCombatTicks(LivingEntity entity, CombatState state) {
        return state.inCombat() && entity.tickCount % SYNC_INTERVAL == 0;
    }

    public static void onEnterCombat(LivingEntity owner) {
        OriginalRelicItem.ringOfTheSnakeBoostSpeed(owner);
        ChargeBallManager.get(owner).tryCreateForCombatEntry();
    }

    public static void onExitCombat(LivingEntity owner) {
        OriginalRelicItem.burningBloodHeal(owner);
    }

    public static Optional<LivingEntity> getRandomHostile(LivingEntity owner, Predicate<LivingEntity> condition) {
        CombatState state = owner.getData(ModAttachments.COMBAT_STATE);
        List<LivingEntity> entities = state.getHostiles(owner).stream().filter(condition).toList();
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(entities.get(owner.getRandom().nextInt(entities.size())));
    }

    public static Optional<LivingEntity> getRandomHostileIn(LivingEntity owner, double radius) {
        return getRandomHostile(owner, entity -> entity.distanceToSqr(owner) < radius * radius);
    }

    public static boolean isInCombat(LivingEntity entity) {
        return entity.getData(ModAttachments.COMBAT_STATE).inCombat();
    }

    public static boolean isHostileTo(LivingEntity owner, LivingEntity target) {
        return owner.getData(ModAttachments.COMBAT_STATE).isHostile(target.getUUID());
    }

}
