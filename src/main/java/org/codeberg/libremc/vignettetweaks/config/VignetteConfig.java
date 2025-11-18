package org.codeberg.libremc.vignettetweaks.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.DualOption;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import org.codeberg.libremc.vignettetweaks.VignetteTweaks;

public class VignetteConfig extends Config {
    @DualOption(
            name = "Type",
            left = "Static",
            right = "Amplified"
    )
    public boolean vignetteType = false;

    @Slider(
            name = "Vignette Opacity",
            min = 0f, max = 100f
    )
    public float vignetteOpacity = 50f;

    @Slider(
            name = "Vignette Minimum Opacity",
            min = 0f, max = 100f
    )
    public float vignetteMinimumOpacity = 0f;

    @Slider(
            name = "Vignette Maximum Opacity",
            min = 0f, max = 100f
    )
    public float vignetteMaximumOpacity = 100f;

    @Slider(
            name = "Vignette Opacity Multiplier",
            min = 0.1f, max = 10f
    )
    public float vignetteOpacityMultiplier = 1f;

    public VignetteConfig() {
        super(new Mod(VignetteTweaks.MOD_NAME, ModType.HUD), VignetteTweaks.MOD_ID + ".json");
        initialize();
        hideIf("vignetteOpacity", () -> vignetteType);

        hideIf("vignetteMinimumOpacity", () -> !vignetteType);
        hideIf("vignetteMaximumOpacity", () -> !vignetteType);
        hideIf("vignetteOpacityMultiplier", () -> !vignetteType);
    }
}
