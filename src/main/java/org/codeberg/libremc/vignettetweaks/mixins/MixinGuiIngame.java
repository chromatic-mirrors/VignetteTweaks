package org.codeberg.libremc.vignettetweaks.mixins;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.MathHelper;
import org.codeberg.libremc.vignettetweaks.VignetteTweaks;
import org.codeberg.libremc.vignettetweaks.config.VignetteConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Redirect(
            method = "renderVignette", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;clamp_float(FFF)F")
    )
    private float setVignetteOpacity(float num, float min, float max) {
        VignetteConfig config = VignetteTweaks.INSTANCE.config;

        if (config.enabled) {
            if (!config.vignetteType) {
                min = max = config.vignetteOpacity;
            } else {
                num = config.vignetteOpacityMultiplier;
                min = config.vignetteMinimumOpacity;
                max = config.vignetteMaximumOpacity;
            }
        }

        return MathHelper.clamp_float(num, min, max);
    }
}
