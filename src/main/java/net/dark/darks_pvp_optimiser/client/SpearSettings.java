package net.dark.darks_pvp_optimiser.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.network.chat.Component;

public final class SpearSettings {
    private SpearSettings() {}

    public static void addCategory(ConfigBuilder builder) {
        var config = SpearConfig.get();
        var defaults = new SpearConfig();
        var entries = builder.entryBuilder();
        var category = builder.getOrCreateCategory(Component.literal("Spear"));

        category.addEntry(entries.startAlphaColorField(Component.literal("Spear range color"), config.spearColor)
                .setDefaultValue(defaults.spearColor).setSaveConsumer(value -> config.spearColor = value).build());
        category.addEntry(entries.startAlphaColorField(Component.literal("Regular melee range color"), config.meleeColor)
                .setDefaultValue(defaults.meleeColor).setSaveConsumer(value -> config.meleeColor = value).build());
        category.addEntry(entries.startFloatField(Component.literal("Target line width"), config.lineWidth)
                .setDefaultValue(defaults.lineWidth).setMin(0.5f).setMax(10.0f)
                .setSaveConsumer(value -> config.lineWidth = value).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Accurate spear hitbox"), config.accurateHitbox)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Show the spear's targeting margin as an extra box, with the normal body box in white."),
                        Component.literal("Uses the held spear, or the first spear in your hotbar. Walls and attack range still apply."))
                .setSaveConsumer(value -> config.accurateHitbox = value).build());
        category.addEntry(entries.startAlphaColorField(Component.literal("Extra hitbox color"), config.marginColor)
                .setDefaultValue(defaults.marginColor)
                .setTooltip(Component.literal("Color of the extra box when this entity is not targeted."))
                .setSaveConsumer(value -> config.marginColor = value).build());
        category.addEntry(entries.startFloatField(Component.literal("Extra hitbox line width"), config.marginLineWidth)
                .setDefaultValue(defaults.marginLineWidth).setMin(0.5f).setMax(10.0f)
                .setSaveConsumer(value -> config.marginLineWidth = value).build());
        Runnable originalSave = builder.getSavingRunnable();
        builder.setSavingRunnable(() -> {
            if (originalSave != null) originalSave.run();
            config.save();
        });
    }
}
