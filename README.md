# DnDFile
Dungeons &amp; Dragons Character App

Years ago I had been doing Java programming with advanced features like weak references and reflection.
When Java 1.5 came out with Generics, I wanted to learn the ins and outs of using them,
and how they were different from C++ templates.
Also, since most of my Java experience was server-side,
I wanted to learn some new areas like the Swing and Printing classes.
And, having been an avid Dungeon Master since I was a kid,
I wanted to write a program to manage all the characters I had built and campaigned over the years.

The complex structure of a D&D character was the perfect opportunity to
model with java.util classes to really learn how to combine them and use Generics.

The character equipment list is a drag & drop tree,
so you can indicate which items are contained within other items.

There is 1 sample character file for testing this program: test1.dnd.
It is a character from a campaign back in the 1980s when I was in college.
You can load, save, edit, etc.

One area still remaining to develop is generating new characters.
I plan for this to be a full one-click experience for characters of any type,
including full equipment lists appropriate to the race & class,
intelligently randomized.
This feature would greatly reduce my workload as a dungeon master,
having to generate NPCs.

I wrote this program long ago, back when Ant was commonly used.
So it's built with Ant, not Maven.
It's easy to import into Eclipse and set up to use its Ant builder.

This program uses 2 fonts when printing: Garamond and DejaVu Sans.
Both are freely available.
If you don't have them installed, the printouts won't look right.

The app has 1 command-line parameter: -font N.
N is an integer that adjusts relative font size, positive or negative.
Java Swing was made back when monitors were lower res with less DPI than they have today.
Thus, its default font sizes are too small for modern monitors.
Typically, "-font +4" works well, but you can use any size adjustment.

The build requires Junit 4.8.2 (file junit-4.8.2.jar).
It looks for an env var JUNIT_HOME for the directory to find it.

Thus: export JUNIT_HOME=/apps/junit (or wherever you put it in your local filesystem).

Example build: ant clean all

Example run: java -jar DnD.jar -font +4
