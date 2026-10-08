# DnDFile
Dungeons &amp; Dragons Character App

**WHAT**

DnD is a Java Swing application that manages AD&D characters.
The program uses and requires JDK 1.6 or any later version.
It runs on any computer having Java: Linux, Windows or Mac.

**QUICK START**

My website has info on this program and how to get started:

https://mclements.net/blogWP/index.php/2026/04/22/dd-character-app/

**WHY**

Years ago I had been doing Java programming with advanced features like weak references and reflection.
When Java 1.5 came out with Generics, I wanted to learn the ins and outs of using them,
and how they were different from C++ templates.
Also, since most of my Java experience was server-side,
I wanted to learn some new areas like the Swing and Printing classes.
And, having been an avid Dungeon Master since I was a kid,
I wanted to write a program to manage all the characters I had built and campaigned over the years.

The complex structure of a D&D character was the perfect opportunity to
model with java.util classes to really learn how to combine them and use Generics.

**DEVELOPMENT**

I wrote this program long ago, back when Ant was commonly used.
So it's built with Ant, not Maven.
It's easy to import into Eclipse and set up to use its Ant builder.

This program uses 2 fonts when printing: Garamond and DejaVu Sans.
Both are freely available.
If you don't have them installed, the printouts won't look right.

The build requires Junit 4.8.2 (file junit-4.8.2.jar).
It looks for an env var JUNIT_HOME for the directory to find it.

Thus: export JUNIT_HOME=/apps/junit (or wherever you put it in your local filesystem).

Example build: ant clean all

Example run: java -jar DnD.jar -font +4
