plugins {
    kotlin("jvm") version "2.0.20"
}

group = "com.venitymc.PowerSkywars"
version = "1.0-SNAPSHOT"
description = "A Skywars Plugin for PowerNukkitX"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.daporkchop.net/releases")
    maven("https://repo.daporkchop.net/snapshots")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation("org.cloudburstmc.netty:netty-transport-raknet:1.0.0.CR3-SNAPSHOT")
    implementation("net.daporkchop:leveldb-mcpe-jni:0.0.10-SNAPSHOT")
    implementation("com.github.AkmalFairuz:PowerNukkitX:5bf1f8a6ea")
}

tasks.jar {
    destinationDirectory.set(layout.buildDirectory)
}

tasks.test {
    useJUnitPlatform()
}