plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.10-SNAPSHOT" apply false
    //id("dev.kikugie.j52j") version "1.0.2" apply false // Enables asset processing by writing json5 files
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false // Publishes builds to hosting websites
}
stonecutter active "dev" /* [SC] DO NOT EDIT */

stonecutter.tasks {
    order("publishModrinth")
    //order("publishCurseforge")
}

//region Run Active Configurations
tasks.register("stonecutterRunActiveClient") {
    group = "stonecutter"
    description = "Runs the active project client"
    dependsOn(":${stonecutter.current!!.project}:runClient")
}

tasks.register("stonecutterRunActiveServer") {
    group = "stonecutter"
    description = "Runs the active project server"
    dependsOn(":${stonecutter.current!!.project}:runServer")
}

tasks.register("generateIdeaRunConfigs") {
    group = "stonecutter-impl"
    description = "Generates IntelliJ run configurations for Run Active Client/Server"
    doLast {
        val ideaDir = file("${rootProject.projectDir}/.idea/runConfigurations")
        ideaDir.mkdirs()
        val template = file("${rootProject.projectDir}/idea_config.xml").readText()

        val clientConfig = template
            .replace("%ENTRY_NAME%", "Run Active Client")
            .replace("%FOLDER_NAME%", "Stonecutter")
            .replace("%TASK_NAME%", ":stonecutterRunActiveClient")
        file("${ideaDir}/Stonecutter_runActiveClient.xml").writeText(clientConfig)

        val serverConfig = template
            .replace("%ENTRY_NAME%", "Run Active Server")
            .replace("%FOLDER_NAME%", "Stonecutter")
            .replace("%TASK_NAME%", ":stonecutterRunActiveServer")
        file("${ideaDir}/Stonecutter_runActiveServer.xml").writeText(serverConfig)
    }
}

gradle.projectsEvaluated {
    tasks["generateIdeaRunConfigs"].actions.forEach { action ->
        action.execute(tasks["generateIdeaRunConfigs"])
    }
}
//endregion