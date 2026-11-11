package net.dark.darks_pvp_optimiser.client;

import net.dark.darks_pvp_optimiser.mixin.PlayerTargetAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.phys.EntityHitResult;

public record SpearTarget(Entity entity, int rgb) {
    public static final SpearTarget NONE = new SpearTarget(null, 0);

    public static AttackRange hotbarRange() {
        var player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) return null;
        var held = spearRange(player.getMainHandItem());
        if (held != null) return held;
        for (int slot = 0; slot < Inventory.getSelectionSize(); slot++) {
            var range = spearRange(player.getInventory().getItem(slot));
            if (range != null) return range;
        }
        return null;
    }

    private static AttackRange spearRange(ItemStack stack) {
        return stack.is(ItemTags.SPEARS) ? stack.get(DataComponents.ATTACK_RANGE) : null;
    }

    public static SpearTarget find(float tickProgress) {
        var client = Minecraft.getInstance();
        var player = client.player;
        var range = hotbarRange();
        if (range == null || client.getCameraEntity() != player) return NONE;
        var regular = PlayerTargetAccessor.regularTarget(player, player.blockInteractionRange(),
                player.entityInteractionRange(), tickProgress);
        if (regular instanceof EntityHitResult hit) return target(hit, SpearConfig.get().meleeColor);
        var spear = range.getClosesetHit(player, tickProgress, EntitySelector.CAN_BE_PICKED);
        if (spear instanceof EntityHitResult hit) return target(hit, SpearConfig.get().spearColor);
        return NONE;
    }

    private static SpearTarget target(EntityHitResult hit, int color) {
        return hit.getEntity() instanceof LivingEntity ? new SpearTarget(hit.getEntity(), color) : NONE;
    }

    public int color(Entity rendered, int original) {
        return rendered == entity ? rgb : original;
    }
}
