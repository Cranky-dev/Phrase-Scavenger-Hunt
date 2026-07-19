plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.cranky-dev.chatscavengerhunt"
version = "1.0"

repositories {
    mavenCentral()
    // Explicitly declaring the modern PaperMC repository with full protocol definitions
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    // Failover mirror repository just in case the primary times out
    maven {
        url = uri("https://sonatype.org")
    }
}

dependencies {
    // 1.21.11 Target Dependency
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
}

tasks {
    assemble { dependsOn(shadowJar) }
}
