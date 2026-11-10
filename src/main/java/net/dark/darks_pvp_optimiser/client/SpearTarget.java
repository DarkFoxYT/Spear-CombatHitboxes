package net.dark.darks_pvp_optimiser.client;

import net.dark.darks_pvp_optimiser.mixin.PlayerTargetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttackRangeComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public record SpearTarget(Entity entity, int rgb) {
    public static final SpearTarget NONE = new SpearTarget(null, 0);

    public static SpearTarget find(float tickProgress) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        if (player == null || player.isSpectator() || client.getCameraEntity() != player) return NONE;
        var inventory = player.getInventory();
        AttackRangeComponent previous = null;
        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            var stack = inventory.getStack(i);
            if (!stack.isIn(ItemTags.SPEARS)) continue;
            var range = stack.get(DataComponentTypes.ATTACK_RANGE);
            if (range == null || range.equals(previous)) continue;
            if (previous == null) {
                var regular = PlayerTargetAccessor.regularTarget(player, player.getBlockInteractionRange(),
                        player.getEntityInteractionRange(), tickProgress);
                if (regular instanceof EntityHitResult hit) return target(hit, 0xff0000);
            }
            HitResult result = range.getHitResult(player, tickProgress, EntityPredicates.CAN_HIT);
            if (result instanceof EntityHitResult hit) return target(hit, 0xff9000);
            previous = range;
        }
        return NONE;
    }

    private static SpearTarget target(EntityHitResult hit, int rgb) {
        return hit.getEntity() instanceof LivingEntity ? new SpearTarget(hit.getEntity(), rgb) : NONE;
    }

    public int color(Entity rendered, int original) {
        return rendered == entity ? (original & 0xff000000) | rgb : original;
    }
}
