package me.gabrieltk.mkscaffold.templates.server

import com.autodsl.annotation.AutoDsl
import com.yg.kotlin.inquirer.components.promptConfirm
import com.yg.kotlin.inquirer.components.promptInput
import com.yg.kotlin.inquirer.components.promptList
import com.yg.kotlin.inquirer.core.KInquirer
import me.gabrieltk.mkscaffold.utils.BKPerm


interface ProjectTemplate {
    //val data: MCServerProject
    fun getDirTree():Array<String>
    fun getFileTree():Map<String, String>


}
@AutoDsl("project")
data class BasicInfo(val name: String)

open class ServerTemplate : ProjectTemplate {
    var data: MCServerProject

    constructor(info: BasicInfo){
        data = plugin {
            projName = info.name
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
                        usage = KInquirer.promptInput("Command Usage:", "/${name}")
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
    }
    override fun getDirTree():Array<String>{
        return arrayOf()
    }
    override fun getFileTree():Map<String, String>{
        return mapOf()
    }

}