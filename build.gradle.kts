import net.ornithemc.ploceus.api.PloceusGradleExtensionApi

plugins {
    id("dev.kikugie.loom-back-compat")
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT" apply false
    id("ploceus") version "1.17.4" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

val isOrnithe = sc.current.version == "1.8.9"
val ploceus = if (isOrnithe) {
    pluginManager.apply("net.fabricmc.fabric-loom-remap")
    pluginManager.apply("ploceus")

    configurations.configureEach {
        exclude(group = "org.lwjgl.lwjgl")
    }

    extensions.getByType<PloceusGradleExtensionApi>().apply {
        setIntermediaryGeneration(2)
    }
} else {
    null
}
val loader = if (isOrnithe) "ornithe" else "fabric"

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    else -> JavaVersion.VERSION_25
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    fun strictMaven(repos: List<String>, vararg groups: String) = exclusiveContent {
        repos.forEach { forRepository { maven(it) } }
        filter { groups.forEach(::includeGroup) }
    }

    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://maven.cloverclient.com/releases", "CloverClient", "pl.tomgirl")

    strictMaven("https://repo.polyfrost.org/releases", "Polyfrost Releases", "org.polyfrost.oneconfig", "org.jetbrains.skiko")
    strictMaven("https://repo.polyfrost.org/snapshots", "Polyfrost Snapshots", "org.polyfrost")
    strictMaven("https://maven.google.com/", "androidx.savedstate")
    strictMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "DevAuth", "me.djtheredstoner")
    strictMaven("https://maven.bawnorton.com/releases", "MixinSquared", "com.github.bawnorton.mixinsquared")
    strictMaven("https://repo.hypixel.net/repository/Hypixel/", "Hypixel", "net.hypixel")
    strictMaven("https://maven.terraformersmc.com/releases", "Terraformers", "com.terraformersmc")
    strictMaven(listOf("https://repo1.maven.org/maven2/", "https://central.sonatype.com/repository/maven-snapshots/"), "net.kyori")

}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    if (isOrnithe) {
        mappings(ploceus!!.layeredMappings {
            mappings("net.ornithemc:feather-gen2:${sc.current.version}+build.${sc.properties["deps.feather_build"] as String}:v2") {
                containsUnpick()
            }
            mappings(rootProject.file("mappings/feather-overrides.tiny"))
        })
    } else {
        loomx.applyMojangMappings()
    }

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

    modImplementation("org.polyfrost.oneconfig:${sc.current.version}-$loader:${property("deps.oneconfig")}")

    if (!isOrnithe) {
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    }
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run")
        jvmArguments.add("-Dmixin.debug.export=true")
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

sourceSets {
    val ducks = create("ducks") {
        compileClasspath += sourceSets["main"].compileClasspath
    }

    main {
        compileClasspath += ducks.output
    }
}

tasks {
    jar {
        val projectName = project.name
        inputs.property("projectName", projectName)

        from("LICENSE") {
            rename { "${it}_${projectName}" }
        }
    }

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

        val hasFabricApi = !isOrnithe
        filesMatching("fabric.mod.json") {
            expand(props)
            if (!hasFabricApi) filter { line -> if ("\"fabric-api\"" in line) "" else line }
        }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}

publishMods {
    val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
        ?.asList().orEmpty().map { it.toString() }

    val changelogText = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."

    file = loomx.modJar.get().archiveFile
    changelog.set(changelogText)

    type.set(
        when (version.toString()) {
            in "beta" -> BETA
            in "alpha" -> ALPHA
            else -> STABLE
        }
    )

    modLoaders.add(loader)

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = property("publish.modrinth.token").toString()
        minecraftVersions.addAll(compatibleVersions)

        requires("oneconfig")
        if (!isOrnithe) requires("fabric-api")
    }
}
