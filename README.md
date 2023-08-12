### blankaspect/regexSearch

This repository contains the source code and resources of the RegexSearch application.  The repo conforms to the Maven
standard directory layout \(ie, the source code is in `src/main/java` and the resources are in `src/main/resources`\).

The Java version of the source code is 17.

The source files in this repo have an expected tab width of 4.

#### Consolidation of codebase, 2023-08-12

Previously, packages that were shared between Blank Aspect projects resided in a separate `common` repository.  The
common code that is used by this project has now been moved into this project, and the `common` repo has been deleted.

----

The Kotlin-DSL-based Gradle build script extends the `compileJava` and `jar` tasks of the `java` plug-in and defines two
tasks for launching the application:
* `runMain` for use after `compileJava`, and
* `runJar` for use after `jar`.

----

The contents of this repository are covered by two licences:

* You may use the contents of the `uk.blankaspect.regexsearch` package \(including resources\) under the terms of the
GPL version 3 license.
* You may use all other contents of this repository under the terms of the MIT license.
