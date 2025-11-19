plugins {
    kotlin("jvm") version "2.2.20"
    application
}

group = "org.koenighotze"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.mockito:mockito-core:5.5.0")

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
    }
}
kotlin {
    jvmToolchain(21)
}
application  {
    mainClass.set("org.koenighotze.echoserver.MainKt")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "org.koenighotze.echoserver.MainKt")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get().filter { it.exists() }.map { if (it.isDirectory) it else zipTree(it) }
    })
}