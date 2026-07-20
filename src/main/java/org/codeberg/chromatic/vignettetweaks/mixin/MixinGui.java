package org.codeberg.chromatic.vignettetweaks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.codeberg.chromatic.vignettetweaks.util.ColorUtil;
import org.codeberg.chromatic.vignettetweaks.config.VignetteConfig;
import org.codeberg.chromatic.vignettetweaks.util.VignetteResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//? if < 26.2 {
// import net.minecraft.client.gui.Gui;
// @Mixin(Gui.class)
//?} else {
import net.minecraft.client.gui.Hud;
@Mixin(Hud.class)
//?}
public class MixinGui {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(
            //? if >= 26.1 {
            method = "extractVignette",
            //?} else {
            // method = "renderVignette",
            //?}
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Mth;clamp(FFF)F"
            )
    )
    private float setStrength(float value, float min, float max) {
        if (VignetteConfig.type != 1) {
            min = max = VignetteConfig.strength / 100f;
        } else {
            value *= VignetteConfig.strengthMultiplier;
            min = VignetteConfig.minimumStrength / 100f;
            max = VignetteConfig.maximumStrength / 100f;
        }

        return Mth.clamp(value, min, max);
    }

    @ModifyArgs(
            //? if >= 26.1 {
            method = "extractVignette",
            //?} else {
            // method = "renderVignette",
            //?}
            at = @At(
                    value = "INVOKE",
                    //? if 1.21.1 {
                    // target = "Lnet/minecraft/client/gui/GuiGraphics;setColor(FFFF)V",
                    //?} else {
                    target = "Lnet/minecraft/util/ARGB;colorFromFloat(FFFF)I",
                    //?}
                    ordinal = 1
            )
    )
    private void setColor(Args args) {
        if (minecraft.player == null) return;

        VignetteResult best = null;

        best = pick(best, VignetteConfig.Health.evaluate(minecraft.player));
        best = pick(best, VignetteConfig.Hunger.evaluate(minecraft.player));
        best = pick(best, VignetteConfig.Air.evaluate(minecraft.player));
        best = pick(best, VignetteConfig.Totem.evaluate(minecraft.player));

        if (best == null || !best.isActive()) return;

        //? if >= 1.21.2 {
        args.set(1, ColorUtil.invert(best.color().getRedF(),   best.strength(), args.get(1)));
        args.set(2, ColorUtil.invert(best.color().getGreenF(), best.strength(), args.get(2)));
        args.set(3, ColorUtil.invert(best.color().getBlueF(),  best.strength(), args.get(3)));
        //?} else {
        // args.set(0, ColorUtil.invert(best.color().getRedF(),   best.strength(), args.get(0)));
        // args.set(1, ColorUtil.invert(best.color().getGreenF(), best.strength(), args.get(1)));
        // args.set(2, ColorUtil.invert(best.color().getBlueF(),  best.strength(), args.get(2)));
        //?}
    }

    @Unique
    private VignetteResult pick(VignetteResult a, VignetteResult b) {
        if (a == null) return b;
        if (b == null) return a;
        return (b.strength() > a.strength()) ? b : a;
    }

}
