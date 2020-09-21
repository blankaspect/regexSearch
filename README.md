### blankaspect/regexSearch

This repository contains the source code and resources of the RegexSearch application except for packages that are
shared between Blank Aspect projects, which can be found in the
[blankaspect/common](https://github.com/blankaspect/common) repository.  Both repos conform to the Maven standard
directory layout \(ie, the source code is in `src/main/java` and the resources are in `src/main/resources`\).

Not all the classes of the `common` packages are required to build the RegexSearch application.  The JAR file that is
created by the `jar` task of the Gradle build script, `build.gradle.kts`, includes only the classes of the `common`
packages that are required by the application.  The Gradle script expects to find the `common` project in the parent
directory of the `RegexSearch` project:
```
..
|
+-- common
|
+-- RegexSearch
    |
    +-- build.gradle.kts
```

The Java version of the source code is 1.8 \(Java SE 8\).

All the source files in this repo have a tab width of 4. 

----

The complete source code of the RegexSearch application is distributed, along with an executable JAR and an installer,
through SourceForge:  
<http://regexsearch.sourceforge.net/>

The release tags of this repository and [blankaspect/common](https://github.com/blankaspect/common) mark the points at
which the source code of the repos is synchronised with the source code of the current distribution of the application.

----

You may use the contents of this repository under the terms of the GPL version 3 license.  You may use the contents of
the [blankaspect/common](https://github.com/blankaspect/common) repository under the terms of the MIT license.
