/*====================================================================*\

Gradle build script : RegexSearch

\*====================================================================*/

// Plug-ins

plugins {
    java
}

//----------------------------------------------------------------------

// Functions

fun _path(vararg components : String) : String =
        components.map { it.replace('/', File.separatorChar) }.joinToString(separator = File.separator)

//----------------------------------------------------------------------

// Properties

val javaVersion = 17

val packageName     = "regexsearch"
val mainClassName   = "uk.blankaspect.${packageName}.App"

val buildDir    = layout.buildDirectory.get().getAsFile().toString()
val jarDir      = _path(buildDir, "bin")
val jarFilename = "regexSearch.jar"

//----------------------------------------------------------------------

// Compile

tasks.compileJava {
    options.release.set(javaVersion)
}

//----------------------------------------------------------------------

// Create executable JAR

tasks.jar {
    destinationDirectory.set(file(jarDir))
    archiveFileName.set(jarFilename)
    manifest {
        attributes(
            "Application-Name" to project.name,
            "Main-Class"       to mainClassName
        )
    }
}

//----------------------------------------------------------------------

// Run main class

tasks.register<JavaExec>("runMain") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(mainClassName)
}

//----------------------------------------------------------------------

// Run executable JAR

tasks.register<JavaExec>("runJar") {
    classpath = files(tasks.jar)
}

//----------------------------------------------------------------------
