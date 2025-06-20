plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.10-SNAPSHOT" apply false
    //id("dev.kikugie.j52j") version "1.0.2" apply false // Enables asset processing by writing json5 files
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false // Publishes builds to hosting websites
}
stonecutter active "dev" /* [SC] DO NOT EDIT */

stonecutter.tasks {
    order(named("publishMods"))
}