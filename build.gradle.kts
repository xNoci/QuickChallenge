import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadow)
}

var javaVersion = 25

group = "me.noci.challenges"
version = project.property("plugin.version")!!

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)

    implementation(libs.xseries) { isTransitive = false }
    implementation(libs.kyori.adventure.serializer)
    compileOnly(libs.kyori.adventure.api)

    compileOnly(files("libs/QuickUtils.jar"))
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.shadowJar {
    archiveFileName.set("${project.property("plugin.name")}-${project.version}.jar")

    fun relocate(pkg: String) = relocate(pkg, "${project.group}.dependency.$pkg")
    relocate("com.cryptomorin.xseries")

    minimize()
}

tasks.processResources {
    filter<ReplaceTokens>(
        "beginToken" to "\${",
        "endToken" to "}",
        "tokens" to mapOf(
            "plugin.name" to project.property("plugin.name"),
            "plugin.version" to version,
        )
    )
}
