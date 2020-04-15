package me.gabrieltk.mkscaffold.templates

import com.yg.kotlin.inquirer.components.promptConfirm
import com.yg.kotlin.inquirer.components.promptInput
import com.yg.kotlin.inquirer.components.promptList
import com.yg.kotlin.inquirer.core.KInquirer
import java.io.File

inline fun <T> inlineMap(iterable: Iterable<T>, separator: String, crossinline out: (v: T) -> String)
        = iterable.joinToString(separator) { out(it) }

fun <T> forEachIndexed1(iterable: Iterable<T>, out: (i: Int, v: T) -> String): String {
    val sb = StringBuilder()
    iterable.forEachIndexed { i, it ->
        sb.append(out(i + 1, it))
    }
    return sb.toString()
}

    fun main(args: Array<String>) {
        val base = KInquirer.promptInput("Project Name:").replace(" ", "");
        var baseDir = base
        if(!baseDir.endsWith("/")) baseDir += "/"
        val r = plugin {
            projName = base
            prefix = KInquirer.promptInput("Plugin Prefix:")
            projDesc = KInquirer.promptInput("Project Description:")
            projPackage = KInquirer.promptInput("Project Package:")
            projMainClass = KInquirer.promptInput("Plugin Main Class Name:")
            authors = KInquirer.promptInput("Authors (comma-separated)").split(",").map { it.trim() }.toTypedArray()
            commands {
                /*+command {
                    name = "hello"
                    aliases = listOf("aa")
                    description = "Hello World"
                    permission = "hello.world"
                    usage = "/hello"
                }*/
                var newCommands = KInquirer.promptConfirm("Would you like to add a command?");
                while (newCommands) {
                    +command {
                        name = KInquirer.promptInput("Command Name:")
                        aliases = KInquirer.promptInput("Aliases (comma-separated):").split(",").map { it.trim() }
                        description = KInquirer.promptInput("Command Description:")
                        permission = KInquirer.promptInput("Command Permission:")
                        var pmesg:String? = KInquirer.promptInput("Command Permission Error Message (empty for default)")
                        if(pmesg!!.isEmpty()) pmesg = null
                        permissionMessage = pmesg
                        usage = KInquirer.promptInput("Command Usage:")
                    }
                    newCommands = KInquirer.promptConfirm("Add another command?")
                }

            }
            permissions {
                /*+permission {
                    name = "hello.world"
                    description = "Hello World"
                }*/
                var newPerms = KInquirer.promptConfirm("Would you like to add a permission?");
                while (newPerms) {
                    +permission {
                        name = KInquirer.promptInput("Permission Name:")
                        description = KInquirer.promptInput("Permission Description:")
                        children = KInquirer.promptInput("Child Perms (comma-separated, empty for none):").split(",").map { it.trim() }


                        default = BKPerm.valueOf(KInquirer.promptList("Default State", BKPerm.values().map {it.name}))
                    }
                    newPerms = KInquirer.promptConfirm("Add another permission?")
                }
            }
            gradleDependencies {
                //+inventoryFramework
                var newDep = KInquirer.promptConfirm("Would you like to add a maven dependency (ie. library)?");
                while (newDep) {
                    +mavenDep {
                        group = KInquirer.promptInput("Dependency Group:")
                        artifact = KInquirer.promptInput("Dependency Artifact:")
                        version = KInquirer.promptInput("Dependency Version")

                    }
                    newDep = KInquirer.promptConfirm("Add another Dependency?")
                }
            }


        }
        val paper = PaperPlugin(r)
        //paper.data.commands = commands
        val build = paper.mainClassFile()
        println(build)


        for (dir in paper.getDirTree()) {
            File(baseDir+dir).mkdirs()
        }
        for (file in paper.getFileTree()) {
            File(baseDir+file.key).writeText(file.value)
        }
    }
