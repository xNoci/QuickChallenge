import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "me.noci.challenges"
version = project.property("plugin.version")!!

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") } //PaperMC
}

dependencies {
    implementation(libs.paperlib)

    compileOnly(files("libs/QuickUtils.jar"))
    compileOnly(libs.paper)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveFileName.set("${project.property("plugin.name")}-${project.version}.jar")
        isEnableRelocation = true
        relocationPrefix = "me.noci.challenges.dep"
        minimize()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
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