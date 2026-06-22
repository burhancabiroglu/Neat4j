plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "com.cabir"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            artifactId = "neat4j"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "Neat4j"
            url = uri("https://maven.pkg.github.com/burhancabiroglu/Neat4j")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.token") as String? ?: System.getenv("REPO_TOKEN")
            }
        }
    }
}
