package org.codeberg.chromatic.vignettetweaks.config;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.codeberg.chromatic.vignettetweaks.util.VignetteResult;
import org.codeberg.chromatic.vignettetweaks.VignetteTweaks;
import org.codeberg.chromatic.vignettetweaks.util.ColorUtil;
import org.polyfrost.compose.render.PolyColor;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.*;

public class VignetteConfig extends Config {

    public static final VignetteConfig INSTANCE = new VignetteConfig();

    @Dropdown(
            title = "Type",
            options = { "Static", "Amplified" }
    )
    public static int type = 1;

    @Slider(
            title = "Strength"
    )
    public static int strength = 50;

    @Slider(
            title = "Minimum Strength"
    )
    public static float minimumStrength = 0f;

    @Slider(
            title = "Maximum Strength"
    )
    public static float maximumStrength = 100f;

    @Slider(
            title = "Strength Multiplier",
            min = 0.1f, max = 10f, step = 0.1f
    )
    public static float strengthMultiplier = 1f;

    @Accordion(title = "Totem", subcategory = "Status")
    public static class Totem {
        @Include
        public static boolean enabled = false;

        @DependsOn("Totem.enabled")
        @Color(title = "Color", alpha = false)
        public static PolyColor color = new PolyColor(0xFFA05B23);

        @DependsOn("Totem.enabled")
        @Dropdown(
                title = "Condition",
                options = { "Offhand", "Main hand", "Both" }
        )
        public static int condition = 2;

        @DependsOn("Totem.enabled")
        @Slider(
                title = "Strength"
        )
        public static int strength = 50;

        public static VignetteResult evaluate(Player player) {
            boolean hasMain = player.getMainHandItem().is(Items.TOTEM_OF_UNDYING);
            boolean hasOff = player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);

            boolean missing = switch (condition) {
                case 0 -> !hasOff;
                case 1 -> !hasMain;
                case 2 -> !(hasMain || hasOff);
                default -> false;
            };

            if (!enabled || !missing) return null;

            return new VignetteResult(color, strength / 100f);
        }
    }

    @Accordion(title = "Air", subcategory = "Status")
    public static class Air {
        @Include
        public static boolean enabled = false;

        @DependsOn("Air.enabled")
        @Color(title = "Color", alpha = false)
        public static PolyColor color = new PolyColor(0xFF142D5E);

        @DependsOn("Air.enabled")
        @Slider(title = "Threshold")
        public static int threshold = 50;

        @DependsOn("Air.enabled")
        @Slider(title = "Minimum Strength")
        public static int minimumStrength = 0;

        @DependsOn("Air.enabled")
        @Slider(title = "Maximum Strength")
        public static int maximumStrength = 100;

        public static VignetteResult evaluate(Player player) {
            float percent = (player.getAirSupply() / (float) player.getMaxAirSupply()) * 100f;
            return scaled(enabled, color, percent, threshold, minimumStrength, maximumStrength);
        }
    }

    @Accordion(title = "Hunger", subcategory = "Status")
    public static class Hunger {
        @Include
        public static boolean enabled = false;

        @DependsOn("Hunger.enabled")
        @Color(title = "Color", alpha = false)
        public static PolyColor color = new PolyColor(0xFF587653);

        @DependsOn("Hunger.enabled")
        @Slider(title = "Threshold")
        public static int threshold = 50;

        @DependsOn("Hunger.enabled")
        @Slider(title = "Minimum Strength")
        public static int minimumStrength = 0;

        @DependsOn("Hunger.enabled")
        @Slider(title = "Maximum Strength")
        public static int maximumStrength = 100;

        public static VignetteResult evaluate(Player player) {
            float percent = (player.getFoodData().getFoodLevel() / 20f) * 100f;
            return scaled(enabled, color, percent, threshold, minimumStrength, maximumStrength);
        }
    }

    @Accordion(title = "Health", subcategory = "Status")
    public static class Health {
        @Include
        public static boolean enabled = false;

        @DependsOn("Health.enabled")
        @Color(title = "Color", alpha = false)
        public static PolyColor color = new PolyColor(0xFFBB1313);

        @DependsOn("Health.enabled")
        @Slider(title = "Threshold")
        public static int threshold = 50;

        @DependsOn("Health.enabled")
        @Slider(title = "Minimum Strength")
        public static int minimumStrength = 0;

        @DependsOn("Health.enabled")
        @Slider(title = "Maximum Strength")
        public static int maximumStrength = 100;

        public static VignetteResult evaluate(Player player) {
            float percent = (player.getHealth() / player.getMaxHealth()) * 100f;
            return scaled(enabled, color, percent, threshold, minimumStrength, maximumStrength);
        }
    }

    private static VignetteResult scaled(boolean enabled, PolyColor color, float percent,
                                         float threshold, float min, float max) {
        if (!enabled || percent > threshold) return null;
        return new VignetteResult(color, ColorUtil.scale(percent, threshold, min, max));
    }

    private VignetteConfig() {
        super(VignetteTweaks.ID + ".json", VignetteTweaks.ICON, VignetteTweaks.NAME, Category.VISUALS);

        hideIf("strength", () -> type == 1);

        hideIf("minimumStrength", () -> type != 1);
        hideIf("maximumStrength", () -> type != 1);
        hideIf("strengthMultiplier", () -> type != 1);
    }
}
