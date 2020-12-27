/*====================================================================*\

Minimal Gradle build script : RegexSearch

\*====================================================================*/

// Plug-ins

plugins {
	id("java")
}

//----------------------------------------------------------------------

// Functions

fun _path(vararg components : String) : String =
		components.map { it.replace('/', File.separatorChar) }.joinToString(separator = File.separator)

//----------------------------------------------------------------------

// Properties

val packageName		= "regexsearch"
val mainClassName	= "uk.blankaspect.${packageName}.App"

val commonDir		= _path("..", "common")
val commonSourceDir	= _path(commonDir, "src", "main", "java")

val jarDir		= _path("${buildDir}", "bin")
val jarFilename	= "regexSearch.jar"

//----------------------------------------------------------------------

// Java version

val javaVersion	= JavaVersion.VERSION_1_8
java {
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}

//----------------------------------------------------------------------

// Compile all classes from source

tasks.compileJava {
	options.sourcepath = files(
		commonSourceDir
	)
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
