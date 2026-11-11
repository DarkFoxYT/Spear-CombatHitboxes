package net.dark.darks_pvp_optimiser.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.dark.darks_pvp_optimiser.client.SpearSettings;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"me.sootysplash.box.ModMenu", "me.truewulf.box.ModMenu"}, remap = false)
abstract class CombatSettingsMixin {
    @Inject(method = "*",
            at = @At(value = "INVOKE", target = "Lme/shedaniel/clothconfig2/api/ConfigBuilder;build()Lnet/minecraft/client/gui/screens/Screen;", remap = true))
    private static void darks$addSpearSettings(CallbackInfoReturnable<Screen> cir,
                                               @Local ConfigBuilder builder) {
        SpearSettings.addCategory(builder);
    }
}
