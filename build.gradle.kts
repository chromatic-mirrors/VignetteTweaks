plugins {
    id("dev.kikugie.loom-back-compat")
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

version = "${property("mod.version")}+mc${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")

    maven(url = "https://central.sonatype.com/repository/maven-snapshots/") {
        name = "central-snapshots"
        mavenContent { snapshotsOnly() }
    }
    mavenCentral()

    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.polyfrost.org/snapshots")
    maven("https://maven.fabricmc.net/releases")
    maven("https://redirector.kotlinlang.org/maven/compose-dev")
    google()
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    fun ocfg(vararg modules: String) {
        for (it in modules) modImplementation("org.polyfrost.oneconfig:${it}:${property("deps.oneconfig") as String}")
    }

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

    ocfg("${sc.current.version}-fabric", "commands", "config", "config-impl", "events", "internal", "ui", "utils", "hud")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${sc.properties["deps.fabric_api"] as String}")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        jvmArguments.add("-Dmixin.debug.export=true")
        runDirectory.set(File("../../run"))
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
        }

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    register<Copy>("buildAndCollect") {
        description = "Build & Collect"
        group = "build"

        from(loomx.modJar.map { it.archiveFile }, loomx.modSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

publishMods {
    file = loomx.modJar.get().archiveFile
    changelog = project.rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."

    val projectVersion = project.version.toString().lowercase()
    type = when {
        "beta" in projectVersion -> BETA
        "alpha" in projectVersion -> ALPHA
        else -> STABLE
    }

    modLoaders.add("fabric")

    val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
        ?.asList().orEmpty().map { it.toString() }

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = findProperty("modrinth.token").toString()

        minecraftVersions.addAll(compatibleVersions)

        requires("oneconfig")
    }
}