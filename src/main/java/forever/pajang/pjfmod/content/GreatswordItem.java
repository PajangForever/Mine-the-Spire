package forever.pajang.pjfmod.content;

import forever.pajang.pjfmod.PajangForeversMod;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public class GreatswordItem extends Item {
    public static final double ARC_DEGREES = 120.0;
    private static final double ARC_COS = Math.cos(Math.toRadians(ARC_DEGREES / 2.0));
    private static final double SWEEP_RANGE = 3.5;
    private static final double ATTACK_DAMAGE_BONUS = 18.0;
    private static final double ATTACK_SPEED_BONUS = -3.5;

    public GreatswordItem(Properties properties) {
        super(properties);
    }

    private static ItemAttributeModifiers createAttributeModifiers() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(PajangForeversMod.id("greatsword_attack_damage"), ATTACK_DAMAGE_BONUS, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(PajangForeversMod.id("greatsword_attack_speed"), ATTACK_SPEED_BONUS, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

//    @Override
//    public InteractionResult use(Level level, Player player, InteractionHand hand) {
//        player.startUsingItem(hand);
//        return InteractionResult.CONSUME;
//    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BLOCK;
    }



    public static boolean isGreatsword(ItemStack stack) {
        return stack.is(ModItems.GREATSWORD);
    }

    public static boolean isBlocking(Player player) {
        return player.isUsingItem() && isGreatsword(player.getUseItem());
    }

    public static boolean isBlockableDamage(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_SHIELD);
    }

    public static boolean isInFrontArc(Player player, Vec3 sourcePos) {
        Vec3 forward = player.getLookAngle();
        Vec3 toSource = sourcePos.subtract(player.position());
        double forwardLen = Math.hypot(forward.x, forward.z);
        double toLen = Math.hypot(toSource.x, toSource.z);
        if (forwardLen < 1.0E-6 || toLen < 1.0E-6) {
            return false;
        }
        double dot = (forward.x * toSource.x + forward.z * toSource.z) / (forwardLen * toLen);
        return dot >= ARC_COS;
    }

    public static void damageBlockingItem(Player player) {
        ItemStack stack = player.getUseItem();
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    public static void performSweep(Player player, LivingEntity primaryTarget) {
        if (player.level().isClientSide()) {
            return;
        }
        if (player.getAttackStrengthScale(0.5f) < 0.9f) {
            return;
        }

        double rangeSqr = SWEEP_RANGE * SWEEP_RANGE;
        Vec3 forward = player.getLookAngle();
        List<LivingEntity> entities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(SWEEP_RANGE, 0.5, SWEEP_RANGE),
                entity -> entity != player && entity != primaryTarget && entity.isAlive() && !player.isAlliedTo(entity)
        );

        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (LivingEntity entity : entities) {
            if (player.distanceToSqr(entity) > rangeSqr) {
                continue;
            }
            Vec3 toTarget = entity.position().subtract(player.position());
            double forwardLen = Math.hypot(forward.x, forward.z);
            double toLen = Math.hypot(toTarget.x, toTarget.z);
            if (forwardLen < 1.0E-6 || toLen < 1.0E-6) {
                continue;
            }
            double dot = (forward.x * toTarget.x + forward.z * toTarget.z) / (forwardLen * toLen);
            if (dot < ARC_COS) {
                continue;
            }
            entity.hurt(player.damageSources().playerAttack(player), damage);
        }
    }
}
