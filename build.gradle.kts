import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadow)
}

group = "me.noci.challenges"
version = project.property("plugin.version")!!

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)

    implementation(libs.paperlib)
    implementation(libs.xseries) { isTransitive = false }
    compileOnly(libs.kyori.adventure)

    compileOnly(files("libs/QuickUtils.jar"))
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    compileJava {
        options.release = 21
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    shadowJar {
        archiveFileName.set("${project.property("plugin.name")}-${project.version}.jar")

        if (project.hasProperty("pluginFolder")) {
            val pluginFolder = project.property("pluginFolder") as String?
            if (!pluginFolder.isNullOrBlank()) {
                destinationDirectory.set(file(pluginFolder))
            }
        }

        fun reloc(pkg: String) = relocate(pkg, "${project.group}.dependency.$pkg")
        reloc("com.cryptomorin.xseries")
        reloc("io.papermc.lib")

        minimize()
    }

    processResources {
        filter<ReplaceTokens>(
            "beginToken" to "\${",
            "endToken" to "}",
            "tokens" to mapOf(
                "plugin.name" to project.property("plugin.name"),
                "plugin.version" to version,
                "plugin.main" to project.property("plugin.main"),
                "plugin.authors" to project.property("plugin.authors")
            )
        )
    }
}