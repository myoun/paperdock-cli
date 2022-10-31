plugins {
    kotlin("multiplatform") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    application
}

group = "app.myoun.paperdock"
version = "0.1.0"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "paperdock.main"
            }
        }
    }


    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val commonMain by getting {
            resources.srcDirs("resources")
            dependencies {
                implementation("com.squareup.okio:okio:3.2.0")
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
                implementation("net.mamoe.yamlkt:yamlkt:0.12.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("io.ktor:ktor-client-core:2.1.3")
                implementation("io.ktor:ktor-client-cio:2.1.3")
            }
        }
        val nativeMain by getting
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
}

distributions {
    main {
        distributionBaseName.set("paperdock-cli")
        contents {
            into("") {
                val jvmJar by tasks.getting
                from(jvmJar)
            }
            into("lib/") {
                val main by kotlin.jvm().compilations.getting
                from(main.runtimeDependencyFiles)
            }
        }
    }
}

tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Zip> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    doFirst {
        manifest {
            val main by kotlin.jvm().compilations.getting
            attributes(
                "Main-Class" to "MainKt",
                "Class-Path" to main.runtimeDependencyFiles.files.joinToString(" ") { "lib/" + it.name }
            )
        }
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
    }
}


tasks {
    val thePackageTask = register("package", Copy::class) {
        duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.INCLUDE

        group = "package"
        description = "Copies the release exe and resources into one directory"

        from("$buildDir/processedResources/jvm/main") {
            include("**/*")
        }

        from("$buildDir/processedResources/native/main") {
            include("**/*")
        }

        from("$buildDir/bin/native/releaseExecutable") {
            include("**/*")
        }

        into("$buildDir/packaged")
        includeEmptyDirs = false
        dependsOn("processResources")
        dependsOn("assemble")
    }

    val zipTask = register<Zip>("packageToZip") {
        group = "package"
        description = "Copies the release exe and resources into one ZIP file."

        archiveFileName.set("packaged.zip")
        destinationDirectory.set(file("$buildDir/packagedZip"))

        from("$buildDir/packaged")

        dependsOn(thePackageTask)
    }

    named("build").get().dependsOn(zipTask.get())
}