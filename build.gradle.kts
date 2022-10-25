plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "io.github.fisher2911"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://jitpack.io")
}


dependencies {
//    compileOnly("io.papermc.paper:paper-api:1.16.5-R0.2-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.2")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        relocate("org.spongepowered.configurate", "io.github.fisher2911.kindomgs.configurate.yaml")
        relocate("net.objecthunter.exp4j", "io.github.fisher2911.kingdoms.exp4j")
        relocate("net.kyori.adventure", "io.github.fisher2911.kingdoms.adventure")
        archiveFileName.set("Kingdoms.jar")

        dependencies {
            exclude(dependency("org.yaml:snakeyaml"))
        }
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = Charsets.UTF_8.name()
    }

}