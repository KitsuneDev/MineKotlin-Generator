package me.gabrieltk.mkscaffold.templates.server

import com.autodsl.annotation.AutoDsl
import me.gabrieltk.mkscaffold.utils.BKPerm
import me.gabrieltk.mkscaffold.utils.inlineMap




class WaterfallPlugin(val basicInfo: BasicInfo) : ServerTemplate(basicInfo) {

    //val data: MCServerProject
    fun buildGradle(): String {
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
            // permissionMessage = 'You may not test this command!'
            ${if (it.permissionMessage != null)  "permissionMessage = '${it.permissionMessage}'" else ""}
        } 
            """
        }}
        // ...
    }

    permissions {
        ${inlineMap(data.permissions, "\n") { 
            """
        'testplugin.setstate' {
            description = '${it.description}'
            ${if (it.children != null)  "children = ['testplugin.setstate']" else ""}
            ${if (it.default != null)  "setDefault(\'${it.default.name}\')" else ""}
            //setDefault('OP') // 'TRUE', 'FALSE', 'OP' or 'NOT_OP'
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
   
    ${inlineMap(data.gradleDependencies ?: listOf(), "\n") {
            """
    implementation '${it.group}:${it.artifact}:${it.version}'
        """
        }}
    compileOnly "io.github.waterfallmc:waterfall-api:1.15-SNAPSHOT"
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

    fun mainClassFile():String {
        return """
package ${data.projPackage}



import net.md_5.bungee.api.plugin.Plugin;
import java.util.function.Consumer


class ${data.projMainClass}: Plugin() {
    
	override fun onLoad() {
        super.onLoad()
	}
    override fun onEnable() {
        logger.info("Enabling ${data.projName}...");
        super.onEnable() 
    }
    
	override fun onDisable() {
        super.onDisable()
	}
} 
        """
    }

    override fun getDirTree(): Array<String>{
        return arrayOf(
            "src/main/java",
            "src/main/kotlin/"+data.projPackage.replace(".", "/")
        )
    }
    override fun getFileTree(): Map<String, String>{
        return mapOf("build.gradle" to buildGradle(),
                    "gradle.properties" to "kotlin.code.style=official",
                    "settings.gradle" to "rootProject.name = '${data.projName}'",
                    "src/main/kotlin/"+(data.projPackage+"."+data.projMainClass).replace(".", "/")+".kt" to mainClassFile()
        )

    }
}