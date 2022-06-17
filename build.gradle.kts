/**
 * The majority of elements were at least inspired and mostly taken from JDA and lightly modified for this project.
 * You can find JDA's build.gradle.kts here: https://github.com/DV8FromTheWorld/JDA/blob/master/build.gradle.kts
 */

plugins {
    `java-library`
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("io.freefair.lombok") version "6.1.0"
}

val versionObject = Version(major = "1", minor = "0", revision = "0")

fun buildNumber(): String? {
    return System.getenv("system.build.number") ?: System.getProperty("system.build.number")
}
println("Build Number A " + System.getenv("system.build.number"))
println("Build Number B " + System.getProperty("system.build.number"))
println("Build Number C " + buildNumber())

project.version = "$versionObject" + if (buildNumber() != null) "_" + buildNumber() else ""
project.group = "pink.zak.discord"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation("com.github.ZakShearman:spring-boot-starter-discord:358b594e7f")
    implementation("net.dv8tion:JDA:5.0.0-alpha.9")
    implementation("com.sedmelluq:lavaplayer:1.3.77")
    implementation("com.sedmelluq:lavaplayer-natives:1.3.14")

    implementation("com.google.code.gson:gson:2.9.0")

    implementation("se.michaelthelin.spotify:spotify-web-api-java:7.1.0")
    implementation("com.google.apis:google-api-services-youtube:v3-rev20220418-1.32.1")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.springframework.data:spring-data-keyvalue")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.0.4")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

// Taken from JDA https://github.com/DV8FromTheWorld/JDA/blob/master/build.gradle.kts#L319
class Version(
    val major: String,
    val minor: String,
    val revision: String,
    val classifier: String? = null
) {
    companion object {
        fun parse(string: String): Version {
            val (major, minor, revision) = string.substringBefore("-").split(".")
            val classifier = if ("-" in string) string.substringAfter("-") else null
            return Version(major, minor, revision, classifier)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Version) return false
        return major == other.major
                && minor == other.minor
                && revision == other.revision
                && classifier == other.classifier
    }

    override fun toString(): String {
        return "$major.$minor.$revision" + if (classifier != null) "-$classifier" else ""
    }
}
