package org.codeberg.chromatic.vignettetweaks.config.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import org.codeberg.chromatic.vignettetweaks.config.VignetteConfig;
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen;

public class VignetteTweaksModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Stops OneConfig's ModMenu shim from duplicating the entry.
        // I stole this snippet from Polyfrost/Chatting c9e2554
        if (!FabricLoader.getInstance().isModLoaded("modmenu")) return null;
        return s -> new OneConfigUIScreen(VignetteConfig.INSTANCE.id);
    }
}
