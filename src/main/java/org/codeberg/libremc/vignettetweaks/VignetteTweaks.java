package org.codeberg.libremc.vignettetweaks;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.codeberg.libremc.vignettetweaks.config.VignetteConfig;

@Mod(
        modid = VignetteTweaks.MOD_ID,
        name = VignetteTweaks.MOD_NAME,
        version = VignetteTweaks.MOD_VERSION,
        clientSideOnly = true
)
public class VignetteTweaks {
    public static final String MOD_ID = "@MOD_ID@";
    public static final String MOD_NAME = "@MOD_NAME@";
    public static final String MOD_VERSION = "@MOD_VERSION@";

    @Mod.Instance(MOD_ID)
    public static VignetteTweaks INSTANCE;

    public VignetteConfig config;

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        config = new VignetteConfig();
    }
}
