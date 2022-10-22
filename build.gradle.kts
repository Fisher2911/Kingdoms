plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "io.github.fisher2911"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}


dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.1-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("net.objecthunter:exp4j:0.4.8")
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