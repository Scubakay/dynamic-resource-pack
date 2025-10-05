plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.11-SNAPSHOT" apply false
    //id("dev.kikugie.j52j") version "1.0.2" apply false // Enables asset processing by writing json5 files
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false // Publishes builds to hosting websites
}
stonecutter active "dev" /* [SC] DO NOT EDIT */

stonecutter.tasks {
    order("publishModrinth")
    //order("publishCurseforge")
}

val generateClientConfig = false
val generateServerConfig = true

//region "Run Active" Configurations
if (generateClientConfig) {
    tasks.register("stonecutterRunActiveClient") {
        group = "stonecutter"
        description = "Runs the active project client"
        dependsOn(":${stonecutter.current!!.project}:runClient")
    }
}

if (generateServerConfig) {
    tasks.register("stonecutterRunActiveServer") {
        group = "stonecutter"
        description = "Runs the active project server"
        dependsOn(":${stonecutter.current!!.project}:runServer")
    }
}

tasks.register("generateIdeaRunConfigs") {
    group = "stonecutter-impl"
    description = "Generates IntelliJ run configurations for Run Active Client/Server"
    doLast {
        val ideaDir = file("${rootProject.projectDir}/.idea/runConfigurations")
        ideaDir.mkdirs()

        val activeProject = stonecutter.current!!.project
        val projectDisplay = rootProject.name.replace(' ', '_')
        val clientModuleName = "$projectDisplay.$activeProject.client"
        val serverModuleName = "$projectDisplay.$activeProject.main"
        val runDir = "\$PROJECT_DIR\$/versions\\$activeProject/../../run/"
        val loomCache = "versions\\$activeProject\\.gradle\\loom-cache\\launch.cfg"
        val projectPath = rootProject.projectDir.absolutePath.replace("\\", "/")

        val template = file("${rootProject.projectDir}/idea_config.xml").readText()

        if (generateClientConfig) {
            val clientConfig = template
                .replace("%ENTRY_NAME%", "Run Active Client")
                .replace("%MAIN_CLASS%", "net.fabricmc.devlaunchinjector.Main")
                .replace("%MODULE_NAME%", clientModuleName)
                .replace("%PROGRAM_PARAMETERS%", "")
                .replace(
                    "%VM_PARAMETERS%",
                    "-Dfabric.dli.config=$projectPath/$loomCache -Dfabric.dli.env=client -Dmixin.debug.export=true -Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotClient"
                )
                .replace("%WORKING_DIRECTORY%", runDir)
            file("${ideaDir}/Stonecutter_runActiveClient.xml").writeText(clientConfig)
        }

        if (generateServerConfig) {
            val serverConfig = template
                .replace("%ENTRY_NAME%", "Run Active Server")
                .replace("%MAIN_CLASS%", "net.fabricmc.devlaunchinjector.Main")
                .replace("%MODULE_NAME%", serverModuleName)
                .replace("%PROGRAM_PARAMETERS%", "")
                .replace(
                    "%VM_PARAMETERS%",
                    "-Dfabric.dli.config=$projectPath/$loomCache -Dfabric.dli.env=server -Dmixin.debug.export=true -Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotServer"
                )
                .replace("%WORKING_DIRECTORY%", runDir)
            file("${ideaDir}/Stonecutter_runActiveServer.xml").writeText(serverConfig)
        }
    }
}

gradle.projectsEvaluated {
    tasks["generateIdeaRunConfigs"].actions.forEach { action ->
        action.execute(tasks["generateIdeaRunConfigs"])
    }
}
//endregion