package me.gabrieltk.mkscaffold.templates.server

import com.autodsl.annotation.AutoDsl
import me.gabrieltk.mkscaffold.utils.BKPerm
import me.gabrieltk.mkscaffold.utils.inlineMap

@AutoDsl("plugin")
data class MCServerProject (val projName: String, val projMainClass:String, val projDesc: String, val projPackage:String,val gradleDependencies: List<GradleDependency>?,val authors: Array<String>,
                             val prefix: String, val permissions: List<MCPermission>, var commands: List<MCCommand>)
@AutoDsl("permission")
data class MCPermission(val name: String, val description: String, val children: List<String>?, val default: BKPerm?)
@AutoDsl("command")
data class MCCommand(val name:String, val description: String, val usage:String, val permission:String, val permissionMessage: String?, val aliases: List<String>?)
@AutoDsl("mavenDep")
data class GradleDependency(val group: String, val artifact: String, val version: String)
//com.github.stefvanschie.inventoryframework:IF:0.5.18
val inventoryFramework = GradleDependency(
    group = "com.github.stefvanschie.inventoryframework",
    artifact = "IF",
    version = "0.5.18"
)

class PaperPlugin(val basicInfo: BasicInfo) : ServerTemplate(basicInfo) {

    //val data: MCServerProject
    private fun buildGradleOld(): String {
        return """
plugins {
    id 'java'
    id 'org.jetbrains.kotlin.jvm' version '1.3.61'
    id 'com.github.johnrengelman.shadow' version '5.2.0'
    id 'net.minecrell.plugin-yml.bukkit' version '0.3.0'
}

group "${data.projPackage}"
version '1.0-SNAPSHOT'

sourceCompatibility = 1.8

bukkit {
    // Default values can be overridden if needed
    // name = 'TestPlugin'
    // version = '1.0'
    // description = 'This is a test plugin'
    // website = 'https://example.com'
    // author = 'Notch'

    // Plugin main class (required)
    main = "${data.projPackage+"."+data.projMainClass}"

    // API version (should be set for 1.13+)
    apiVersion = '1.15'

    // Other possible properties from plugin.yml (optional)
    load = 'STARTUP' // or 'POSTWORLD'
    authors = [${data.authors.joinToString(separator = ",", prefix = "'", postfix = "'")}]
    depend = []
    softDepend = []
    loadBefore = []
    prefix = '${data.prefix}'
    defaultPermission = 'TRUE' // 'TRUE', 'FALSE', 'OP' or 'NOT_OP'

    commands {
    ${inlineMap(data.commands, "\n") {
            """
        ${it.name} {
            description = '${it.description}'
            aliases = [${it.aliases?.joinToString(separator = ",", prefix = "'", postfix = "'")}]
            permission = '${it.permission}'
            usage = '${it.usage}'
            //permissionMessage = 'You may not test this command!'
            ${if (it.permissionMessage != null)  "permissionMessage = '${it.permissionMessage}'" else ""}
        } 
            """
        }}
        // ...
    }

    permissions {
        ${inlineMap(data.permissions, "\n") { 
            """
        '${it.name}' {
            description = '${it.description}'
            ${if (it.children != null)  "children = [${it.children.joinToString(separator = ",", prefix = "'", postfix = "'")}]" else ""}
            ${if (it.default != null)  "setDefault(\'${it.default.name}\')" else ""} 
            // 'TRUE', 'FALSE', 'OP' or 'NOT_OP'
        }
            """
        }}
    }
}

shadowJar {
    relocate 'kotlin', "${data.projPackage}.portalgateway.kotlin"
    relocate "com.github.hazae41", "${data.projPackage}.deps.kotlintools" 
    ${inlineMap(data.gradleDependencies ?: listOf(), "\n") {
            """
    relocate "${it.group}", "${data.projPackage}.deps.kotlintools" 
            """
        }}
    //relocate "com.github.stefvanschie.inventoryframework", "${data.projPackage}.deps.kotlintools"
    dependencies {
        include(dependency {
            it.moduleGroup == "org.jetbrains.kotlin"
        })
            include(dependency {
                it.moduleGroup == "com.github.hazae41"
            })
        /*include(dependency {
            it.moduleGroup == "com.github.stefvanschie.inventoryframework"
        })*/
${inlineMap(data.gradleDependencies ?: listOf(), "\n")  {
            """
    include(dependency {
        it.moduleGroup == "${it.group}"
    })
        """
        }}
    }
}
build.finalizedBy shadowJar
repositories {
    mavenCentral()
    maven {
        url "https://papermc.io/repo/repository/maven-public/"
    }
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    implementation 'com.github.hazae41:mc-kutils:3.3.2'
    //implementation 'com.github.stefvanschie.inventoryframework:IF:0.5.18'
    ${inlineMap(data.gradleDependencies ?: listOf(), "\n") {
            """
    implementation '${it.group}:${it.artifact}:${it.version}'
        """
        }}
    compileOnly "com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}

compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
"""
    }

    private fun buildGradle(): String {
        return """
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Command
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission
import me.gabrieltk.mcrun.*

repositories {
    mavenLocal()

    mavenCentral()
    jcenter()
    maven("https://papermc.io/repo/repository/maven-public/")

    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://cdn.rawgit.com/Rayzr522/maven-repo/master/")
    maven("https://maven.sk89q.com/repo/")
    maven("https://ci.ender.zone/plugin/repository/everything/")
}

plugins {
    java
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.70"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("me.gabrieltk.mcrun") version "1.1"

}
group = "${data.projPackage}"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

configure<MCRunPluginExtension>() {
    mcVersion = "1.15.2"
    acceptEula = false //Change it to True Only if you (the user) accepts the Minecraft Eula
}

tasks.named<RunPaperTask>("paperPrepare"){
    dependsOn("shadowJar")
    //finalizedBy("exportDeps")
    pluginFiles = listOf("build/libs/${data.projName}-1.0-SNAPSHOT-all.jar"
        // You may add a Directory with your plugin dependencies here.
        // Any files here will be copied to the plugins directory
    )
}

tasks.generateBukkitPluginDescription {
    bukkit {
        // Default values can be overridden if needed
        // name = "TestPlugin"
        // version = "1.0"
        // description = "This is a test plugin"
        // website = "https://example.com"
        // author = "Notch"
        // Plugin main class (required)
        main = "${data.projPackage}.${data.projMainClass}"
        // API version (should be set for 1.13+)
        apiVersion = "1.15"

        // Other possible properties from plugin.yml (optional)
        load = PluginLoadOrder.STARTUP // or "POSTWORLD"
        authors = listOf(${data.authors.joinToString(separator = ",", prefix = "\"", postfix = "\"")})
        depend = listOf()
        softDepend = listOf()
        loadBefore = listOf()
        prefix = "${data.prefix}"
           
        commands {
        ${inlineMap(data.commands, "\n") {
            """
            create("${it.name}") {
                description = "${it.description}"
                aliases = listOf(${it.aliases?.joinToString(separator = ",", prefix = "\"", postfix = "\"")})
                permission = "${it.permission}"
                usage = "${it.usage}"
                //permissionMessage = "You may not test this command!'"
                ${if (it.permissionMessage != null) "permissionMessage = \"${it.permissionMessage}\"" else ""}
            } 
            """
        }}
        }

        permissions {
        ${inlineMap(data.permissions, "\n") {
            """
            create("${it.name}") {
                description = "${it.description}"
                ${if (it.children != null) "children = listOf(${it.children.joinToString(
                separator = ",",
                prefix = "\"",
                postfix = "\""
            )}]" else ""}
                ${if (it.default != null) "default = Permission.Default.${it.default} // \"TRUE\", \"FALSE\", \"OP\" or \"NOT_OP\"" else ""} 
            // 'TRUE', 'FALSE', 'OP' or 'NOT_OP'
            }
            """
        }}
    }
    }
}

tasks.shadowJar {
    relocate("kotlin", "${data.projPackage}")
    relocate("hazae41", "${data.projPackage}")
    //relocate("xyz.upperlevel.spigot.book", "${data.projPackage}")
    //relocate("co.aikar.commands", "${data.projPackage}")
    //relocate("com.github.JOO200", "${data.projPackage}")
    //relocate("com.github.stefvanschie.inventoryframework", "${data.projPackage}")
    ${inlineMap(data.gradleDependencies ?: listOf(), "\n") {
            """
    relocate("${it.group}", "${data.projPackage}") 
            """
        }}
    dependencies {
        exclude("com.massivecraft")
        //it.moduleGroup == "" || it.moduleGroup == "xyz.upperlevel.spigot.book"
        exclude("com.destroystokyo.paper")

        exclude("org.jetbrains.annotations")


    }
}



dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.hazae41:mc-kutils:3.3.2")
    //implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")
    //implementation("com.github.JOO200:commands:master-SNAPSHOT") //Using a fork to allow Kotlin Metadata Recursive Annotations. 2f552f7ffc-1
    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    ${inlineMap(data.gradleDependencies ?: listOf(), "\n") {
            """
    implementation("${it.group}:${it.artifact}:${it.version}")
            """
        }}
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    testImplementation("junit:junit:4.12")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}



        """.trimIndent()
    }

    fun mainClassFile(): String {
        return """
package ${data.projPackage}



import hazae41.minecraft.kutils.bukkit.listen
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Consumer


class ${data.projMainClass}: JavaPlugin() {
    override fun onEnable() {
        logger.info("Loading ${data.projName}...");
        super.onEnable()
        
        
    }
} 
        """
    }

    override fun getDirTree(): Array<String> {
        return arrayOf(
            "src/main/java",
            "src/main/kotlin/" + data.projPackage.replace(".", "/")
        )
    }

    override fun getFileTree(): Map<String, String> {
        return mapOf(
            "build.gradle.kts" to buildGradle(),
            "gradle.properties" to "kotlin.code.style=official",
            "settings.gradle" to "rootProject.name = '${data.projName}'",
            "src/main/kotlin/" + (data.projPackage + "." + data.projMainClass).replace(
                ".",
                "/"
            ) + ".kt" to mainClassFile()
        )

    }
}