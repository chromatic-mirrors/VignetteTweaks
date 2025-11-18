import dev.deftu.gradle.utils.GameSide

plugins {
    id("java")
    alias(libs.plugins.toolkit)
    alias(libs.plugins.loom)
    alias(libs.plugins.bloom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.resources)
}

repositories {
    maven("https://repo.polyfrost.org/releases")
}

dependencies {
    compileOnly("cc.polyfrost:oneconfig-1.8.9-forge:0.2.2-alpha+")
    compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")

    implementation("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta+")
    shade("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta+")
}

toolkitLoomHelper {
    useTweaker("cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")

    useMixinRefMap(modData.id)
    useForgeMixin(modData.id)

    disableRunConfigs(GameSide.SERVER)
}