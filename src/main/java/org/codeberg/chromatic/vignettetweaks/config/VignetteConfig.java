package org.codeberg.chromatic.vignettetweaks.config;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.codeberg.chromatic.vignettetweaks.data.VignetteResult;
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
    public static int type = 0;

    @Slider(
            title = "Strength",
            step = 1.0f
    )
    public static int strength = 50;

    @Slider(
            title = "Minimum Strength",
            step = 1.0f
    )
    public static float minimumStrength = 0f;

    @Slider(
            title = "Maximum Strength",
            step = 1.0f
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

        @Color(title = "Color", alpha = false)
        public static PolyColor color = new PolyColor(0xFFA05B23);

        @Dropdown(
                title = "Condition",
                options = { "Offhand", "Main hand", "Both" }
        )
        public static int condition = 2;

        @Slider(
                title = "Strength",
                step = 1.0f
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
        public static boolean enabled = true;

        @Color(title = "Color", alpha = false)
        public static PolyColor color = new PolyColor(0xFF142D5E);

        @Slider(title = "Threshold", step = 1.0F)
        public static int threshold = 50;

        @Slider(title = "Minimum Strength", step = 1.0F)
        public static int minimumStrength = 0;

        @Slider(title = "Maximum Strength", step = 1.0F)
        public static int maximumStrength = 100;

        public static VignetteResult evaluate(Player player) {
            float percent = (player.getAirSupply() / (float) player.getMaxAirSupply()) * 100f;

            if (!enabled || percent > threshold) return null;

            float strength = ColorUtil.scale(percent, threshold, minimumStrength, maximumStrength);

            return new VignetteResult(color, strength);
        }
    }

    @Accordion(title = "Hunger", subcategory = "Status")
    public static class Hunger {
        @Include
        public static boolean enabled = true;

        @Color(title = "Color", alpha = false)
        public static PolyColor color = new PolyColor(0xFF587653);

        @Slider(title = "Threshold", step = 1.0F)
        public static int threshold = 50;

        @Slider(title = "Minimum Strength", step = 1.0F)
        public static int minimumStrength = 0;

        @Slider(title = "Maximum Strength", step = 1.0F)
        public static int maximumStrength = 100;

        public static VignetteResult evaluate(Player player) {
            float percent = (player.getFoodData().getFoodLevel() / 20f) * 100f;

            if (!enabled || percent > threshold) return null;

            float strength = ColorUtil.scale(percent, threshold, minimumStrength, maximumStrength);

            return new VignetteResult(color, strength);
        }
    }

    @Accordion(title = "Health", subcategory = "Status")
    public static class Health {
        @Include
        public static boolean enabled = true;

        @Color(title = "Color", alpha = false)
        public static PolyColor color = new PolyColor(0xFFBB1313);

        @Slider(title = "Threshold", step = 1.0F)
        public static int threshold = 50;

        @Slider(title = "Minimum Strength", step = 1.0F)
        public static int minimumStrength = 0;

        @Slider(title = "Maximum Strength", step = 1.0F)
        public static int maximumStrength = 100;

        public static VignetteResult evaluate(Player player) {
            float percent = (player.getHealth() / player.getMaxHealth()) * 100f;

            if (!enabled || percent > threshold) return null;

            float strength = ColorUtil.scale(percent, threshold, minimumStrength, maximumStrength);

            return new VignetteResult(color, strength);
        }
    }

    private VignetteConfig() {
        super("vignettetweaks.json", "Vignette Tweaks", Category.QOL);

        hideIf("strength", () -> type == 1);

        hideIf("minimumStrength", () -> type != 1);
        hideIf("maximumStrength", () -> type != 1);
        hideIf("strengthMultiplier", () -> type != 1);

        addDependency("Health.color", "Health.enabled");
        addDependency("Health.threshold", "Health.enabled");
        addDependency("Health.minimumStrength", "Health.enabled");
        addDependency("Health.maximumStrength", "Health.enabled");

        addDependency("Hunger.color", "Hunger.enabled");
        addDependency("Hunger.threshold", "Hunger.enabled");
        addDependency("Hunger.minimumStrength", "Hunger.enabled");
        addDependency("Hunger.maximumStrength", "Hunger.enabled");

        addDependency("Air.color", "Air.enabled");
        addDependency("Air.threshold", "Air.enabled");
        addDependency("Air.minimumStrength", "Air.enabled");
        addDependency("Air.maximumStrength", "Air.enabled");

        addDependency("Totem.color", "Totem.enabled");
        addDependency("Totem.condition", "Totem.enabled");
        addDependency("Totem.strength", "Totem.enabled");
    }
}
