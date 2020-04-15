package me.gabrieltk.mkscaffold.cli

import me.gabrieltk.mkscaffold.templates.*
import com.github.ajalt.mordant.TermColors
import com.yg.kotlin.inquirer.components.promptConfirm
import com.yg.kotlin.inquirer.components.promptInput
import com.yg.kotlin.inquirer.components.promptList
import com.yg.kotlin.inquirer.core.KInquirer

import me.gabrieltk.mkscaffold.templates.server.BasicInfo
import me.gabrieltk.mkscaffold.templates.server.PaperPlugin
import me.gabrieltk.mkscaffold.templates.server.ProjectTemplate
import me.gabrieltk.mkscaffold.templates.server.WaterfallPlugin
import java.io.File

enum class ProjectTypes() {
    Paper(),
    Waterfall()
}
fun buildProjectGenerator(type: ProjectTypes, info: BasicInfo):ProjectTemplate? {
    return when(type){
        ProjectTypes.Paper ->  PaperPlugin(info)
        ProjectTypes.Waterfall -> WaterfallPlugin(info)
        else -> null
    }
}
fun main(args: Array<String>) {
    val colors = TermColors()
    val base = KInquirer.promptInput("Project Name:").replace(" ", "");
    var baseDir = base
    if(!baseDir.endsWith("/")) baseDir += "/"
    var projectType = ProjectTypes.valueOf(KInquirer.promptList("Which Project type would you like to use?", ProjectTypes.values().map {it.name}));
    val generator: ProjectTemplate = buildProjectGenerator(projectType, info = BasicInfo(name = base))!!
    //paper.data.commands = commands
    println(colors.green("Project Generated Sucessfully at ${baseDir}. "))
    println(colors.blue("Hope to see you soon :))"))


    for (dir in generator.getDirTree()) {
        File(baseDir+dir).mkdirs()
    }
    for (file in generator.getFileTree()) {
        File(baseDir+file.key).writeText(file.value)
    }
}
