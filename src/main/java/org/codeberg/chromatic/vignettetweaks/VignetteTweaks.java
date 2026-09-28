package org.codeberg.chromatic.vignettetweaks;

import net.fabricmc.api.ClientModInitializer;
import org.codeberg.chromatic.vignettetweaks.config.VignetteConfig;

public class VignetteTweaks implements ClientModInitializer {
    public static final String ID = /*$ mod_id*/ "vignettetweaks";
    public static final String NAME = /*$ mod_name*/ "VignetteTweaks";
    public static final String VERSION = /*$ mod_version*/ "2.0.1";
    public static final String MC_VERSION = /*$ minecraft*/ "26.3";
    public static final String ICON = "/assets/" + ID + "/logo.png";

    @Override
    public void onInitializeClient() {
        VignetteConfig.INSTANCE.preload();
    }
}
