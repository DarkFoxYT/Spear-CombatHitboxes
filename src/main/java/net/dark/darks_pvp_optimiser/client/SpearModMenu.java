package net.dark.darks_pvp_optimiser.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.ModMenu;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public final class SpearModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) return null;
        return parent -> {
            if (FabricLoader.getInstance().isModLoaded("combat-hitboxes")) {
                return ModMenu.getConfigScreen("combat-hitboxes", parent);
            }
            var builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.literal("Spear+CombatHitboxes"));
            SpearSettings.addCategory(builder);
            return builder.build();
        };
    }
}
