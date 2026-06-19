package org.codeberg.chromatic.vignettetweaks;

import net.fabricmc.api.ClientModInitializer;
import org.codeberg.chromatic.vignettetweaks.config.VignetteConfig;

public class VignetteTweaks implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VignetteConfig.INSTANCE.preload();
    }
}
