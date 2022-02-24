plugins {
    `java-library`
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("io.freefair.lombok") version "6.1.0"
}

group = "pink.zak.discord"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.5")
    implementation("com.sedmelluq:lavaplayer:1.3.77")
    implementation("com.sedmelluq:lavaplayer-natives:1.3.14")

    implementation("com.google.code.gson:gson:2.9.0")

    implementation("se.michaelthelin.spotify:spotify-web-api-java:7.0.0")
    implementation("com.google.apis:google-api-services-youtube:v3-rev20210915-1.32.1")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.springframework.data:spring-data-keyvalue")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:2.7.3")

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
