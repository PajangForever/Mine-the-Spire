package forever.pajang.minethespire.content.specials;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forever.pajang.minethespire.ConfigTheSpire;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.item.OriginalRelicItem;
import forever.pajang.minethespire.content.item.RelicItem;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CombatState {
    public static final MapCodec<CombatState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("combat_ticks").forGetter(CombatState::getTickRemains),
            Codec.list(UUIDUtil.CODEC).fieldOf("hostiles").forGetter(state -> List.copyOf(state.getHostileUUIDs()))
    ).apply(instance, CombatState::new));

    private LivingEntity owner;
    private int combatTicks = 0;
    private int lastTicks = 0;
    private final Set<UUID> hostiles = new HashSet<>();
    private boolean dirty = false;

    public CombatState(IAttachmentHolder holder) {
        if (holder instanceof LivingEntity) {
            this.owner = (LivingEntity) holder;
        } else {
            throw new IllegalArgumentException("Trying to attach CombatState to non-LivingEntity");
        }
    }

    private CombatState(int combatTicks, Collection<UUID> hostiles) {
        this.combatTicks = combatTicks;
        this.hostiles.addAll(hostiles);
    }

    public void markDirty() {
        dirty = true;
    }

    protected void clearDirty() {
        dirty = false;
    }

    protected boolean isDirty() {
        return dirty;
    }

    public boolean inCombat() {
        return combatTicks > 0;
    }

    public void addHostile(LivingEntity target) {
        if (target != null && isValid(target)) {
            updateCombatTicks(getMaxTicks());
            hostiles.add(target.getUUID());
        }
    }

    public boolean removeHostile(LivingEntity target) {
        if (target != null) {
            markDirty();
            return hostiles.remove(target.getUUID());
        } else return false;
    }

    private boolean isHostile(UUID uuid) {
        return inCombat() && hostiles.contains(uuid);
    }

    public Set<UUID> getHostileUUIDs() {
        return Set.copyOf(hostiles);
    }

    public Set<LivingEntity> getHostiles() {
        if (!inCombat()) {
            return Set.of();
        }
        Set<LivingEntity> hostileEntities = new HashSet<>();
        forEachValidHostile(hostileEntities::add);
        return hostileEntities;
    }

    public void forEachValidHostile(Consumer<LivingEntity> operation) {
        Level level = owner.level();
        boolean isServer = !level.isClientSide();
        for (UUID uuid : List.copyOf(this.hostiles)) {
            Entity entity = level.getEntity(uuid);
            if (entity instanceof LivingEntity livingEntity && isValid(livingEntity)) {
                operation.accept(livingEntity);
            } else if (isServer) {
                hostiles.remove(uuid);
                markDirty();
            }
        }
    }

    private boolean isValid(LivingEntity entity) {
        return entity != owner && entity.isAlive() && !entity.isRemoved();
    }

    private void updateCombatTicks(int combatTicks) {
        this.combatTicks = combatTicks;
        markDirty();
    }

    public int getTickRemains() {
        return combatTicks;
    }

    protected void tick() {
        if (hostiles.isEmpty() && combatTicks > getQuickExitTicks()) {
            updateCombatTicks(getQuickExitTicks());
            onHostilesExtinct();
        }

        if (combatTicks == getMaxTicks() && lastTicks <= 0) {
            onEnterCombat();
        } else if (combatTicks == 1) {
            onExitCombat();
        }
        if (isDirty()) {
            owner.syncData(ModAttachments.COMBAT_STATE);
            clearDirty();
        }
        lastTicks = combatTicks;

        if (combatTicks > 0) {
            combatTicks--;
        }
    }

    protected void onEnterCombat() {
        OriginalRelicItem.ringOfTheSnakeBoostSpeed(owner);
        OriginalRelicItem.crackedCoreSummonFirstBall(owner);
        RelicItem.akabekoApplyVigor(owner);
        markDirty();
    }

    protected void onExitCombat() {
        OriginalRelicItem.burningBloodHeal(owner);
        hostiles.clear();
        markDirty();
    }

    protected void onHostilesExtinct() {

    }

    public static CombatState get(LivingEntity owner) {
        CombatState combatState = owner.getData(ModAttachments.COMBAT_STATE);
        combatState.owner = owner;
        return combatState;
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

    public static void onRemoved(LivingEntity victim) {
        if (victim.level().isClientSide()) {
            return;
        }
        CombatState.get(victim).forEachValidHostile(hostile -> CombatState.get(hostile).removeHostile(victim));
    }

    private static void bothHostilize(LivingEntity first, LivingEntity second) {
        CombatState.get(first).addHostile(second);
        CombatState.get(second).addHostile(first);
    }

    private static int getMaxTicks() {
        return ConfigTheSpire.MAX_COMBAT_TICKS.getAsInt();
    }

    private static int getQuickExitTicks() {
        return ConfigTheSpire.QUICK_EXIT_COMBAT_TICKS.getAsInt();
    }

    public static void tickEntity(LivingEntity entity) {
        CombatState.get(entity).tick();
    }

    public static Optional<LivingEntity> getRandomHostile(LivingEntity owner, Predicate<LivingEntity> condition) {
        CombatState state = get(owner);
        List<LivingEntity> entities = state.getHostiles().stream().filter(condition).toList();
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(entities.get(owner.getRandom().nextInt(entities.size())));
    }

    public static Optional<LivingEntity> getRandomHostileIn(LivingEntity owner, double radius) {
        return getRandomHostile(owner, entity -> entity.distanceToSqr(owner) < radius * radius);
    }

    public static boolean isInCombat(LivingEntity entity) {
        return get(entity).inCombat();
    }

    public static boolean isHostileTo(LivingEntity owner, LivingEntity target) {
        return get(owner).isHostile(target.getUUID());
    }

}
