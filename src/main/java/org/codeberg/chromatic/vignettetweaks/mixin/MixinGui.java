package org.codeberg.chromatic.vignettetweaks.mixin;

import net.minecraft.client.Minecraft;
import org.codeberg.chromatic.vignettetweaks.util.ColorUtil;
import org.codeberg.chromatic.vignettetweaks.config.VignetteConfig;
import org.codeberg.chromatic.vignettetweaks.util.VignetteResult;
//? if < 1.21.11
// import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.resources.Identifier;
//? if 1.8.9 {
// import net.minecraft.client.gui.GuiElement;
// import net.minecraft.client.render.Window;
// import net.minecraft.client.render.platform.GlStateManager;
// import org.lwjgl.opengl.GL11;
//?} else
import net.minecraft.world.entity.Entity;
//? if >= 26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} elif > 1.8.9 {
// import net.minecraft.client.gui.GuiGraphics;
//?}
//? if >= 1.21.2
import net.minecraft.util.ARGB;
//? if >= 1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?} elif >= 1.21.2 {
// import net.minecraft.client.renderer.RenderType;
//?} elif > 1.8.9 {
// import com.mojang.blaze3d.platform.GlStateManager;
// import com.mojang.blaze3d.systems.RenderSystem;
//?}

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

    @Shadow
    @Final
    private static Identifier VIGNETTE_LOCATION;

    //? if 1.8.9 {
    // @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isFancyGraphicsEnabled()Z"))
    // private boolean renderOnFastGraphics(boolean original) {
    //     return original || VignetteConfig.enabled;
    // }
    //?} elif < 1.21.11 {
    // @ModifyExpressionValue(method = "renderCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;useFancyGraphics()Z"))
    // private boolean renderOnFastGraphics(boolean original) {
    //     return original || VignetteConfig.enabled;
    // }
    //?}

    @WrapOperation(
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
    private float setStrength(float value, float min, float max, Operation<Float> original) {
        if (VignetteConfig.enabled) {
            if (VignetteConfig.type != 1) {
                min = max = VignetteConfig.strength / 100f;
            } else {
                value *= VignetteConfig.strengthMultiplier;
                min = VignetteConfig.minimumStrength / 100f;
                max = VignetteConfig.maximumStrength / 100f;
            }
        }

        return original.call(value, min, max);
    }

    @Inject(
            //? if >= 26.1 {
            method = "extractVignette",
            //?} else {
            // method = "renderVignette",
            //?}
            at = @At("TAIL")
    )
    //? if 1.8.9 {
    // private void renderStatusVignette(float tickDelta, Window window, CallbackInfo ci) {
    //?} elif >= 26.1 {
    private void renderStatusVignette(GuiGraphicsExtractor graphics, Entity entity, CallbackInfo ci) {
    //?} else {
    // private void renderStatusVignette(GuiGraphics graphics, Entity entity, CallbackInfo ci) {
    //?}
        if (!VignetteConfig.enabled || minecraft.player == null) return;

        VignetteResult best = null;

        best = pick(best, VignetteConfig.Health.evaluate(minecraft.player));
        best = pick(best, VignetteConfig.Hunger.evaluate(minecraft.player));
        best = pick(best, VignetteConfig.Air.evaluate(minecraft.player));
        //? if >1.8.9
        best = pick(best, VignetteConfig.Totem.evaluate(minecraft.player));

        if (best == null || !best.isActive()) return;

        float r = ColorUtil.invert(best.color().getRedF(), best.strength());
        float g = ColorUtil.invert(best.color().getGreenF(), best.strength());
        float b = ColorUtil.invert(best.color().getBlueF(), best.strength());

        //? if >= 1.21.6 {
        graphics.blit(RenderPipelines.VIGNETTE, VIGNETTE_LOCATION, 0, 0, 0f, 0f, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight(), ARGB.colorFromFloat(1f, r, g, b));
        //?} elif >= 1.21.2 {
        // graphics.blit(RenderType::vignette, VIGNETTE_LOCATION, 0, 0, 0f, 0f, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight(), ARGB.colorFromFloat(1f, r, g, b));
        //?} elif > 1.8.9 {
        // RenderSystem.disableDepthTest();
        // RenderSystem.depthMask(false);
        // RenderSystem.enableBlend();
        // RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        // graphics.setColor(r, g, b, 1f);
        // graphics.blit(VIGNETTE_LOCATION, 0, 0, -90, 0f, 0f, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());
        // RenderSystem.depthMask(true);
        // RenderSystem.enableDepthTest();
        // graphics.setColor(1f, 1f, 1f, 1f);
        // RenderSystem.defaultBlendFunc();
        // RenderSystem.disableBlend();
        //?} else {
        // GlStateManager.disableDepthTest();
        // GlStateManager.depthMask(false);
        // GlStateManager.blendFuncSeparate(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
        // GlStateManager.color4f(r, g, b, 1f);
        // minecraft.getTextureManager().bind(VIGNETTE_LOCATION);
        // GuiElement.drawTexture(0, 0, 0f, 0f, window.getWidth(), window.getHeight(), window.getWidth(), window.getHeight());
        // GlStateManager.depthMask(true);
        // GlStateManager.enableDepthTest();
        // GlStateManager.color4f(1f, 1f, 1f, 1f);
        // GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        //?}
    }

    @Unique
    private VignetteResult pick(VignetteResult a, VignetteResult b) {
        if (a == null) return b;
        if (b == null) return a;
        return (b.strength() > a.strength()) ? b : a;
    }

}
