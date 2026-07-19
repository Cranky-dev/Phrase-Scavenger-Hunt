plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.yourname.chatscavengerhunt" // <-- Updated here
version = "1.0"

repositories {
    mavenCentral()
    maven("https://papermc.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
}

tasks {
    assemble { dependsOn(shadowJar) }
}
