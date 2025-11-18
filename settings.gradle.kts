pluginManagement {
    repositories {
        maven("https://maven.deftu.dev/releases")
        maven("https://maven.deftu.dev/snapshots")
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")

        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = extra["mod.name"].toString()