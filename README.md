# DnDFile
Dungeons &amp; Dragons Character App

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

**WHAT**

DnD is a Java Swing application that manages AD&D characters.
The program uses and requires JDK 1.6 or any later version.

**HOW**

To run the program, after building it, go to the **bin** directory.
Make sure you have JDK 1.6 installed and in your path.
To start the program:

java -jar DnD.jar [-font X] [-file FILENAME]

All parameters (-font & -file) are optional.

With -font, X is the size of the font you want to use.
X is an adjustment, + or -, which will grow or shrink the sizes of all the default fonts.
Examples:
	-font -1	makes all fonts 1 point smaller
	-font +4		makes all fonts 4 points larger

FILENAME is the path and filename of a character file to load on startup.

I've run and tested it on Linux and Windows, but it should work on Mac too.

It is useful to create a file type for the "dnd" file extension
(or whatever extension you want to use) and map it to this program.
Launching Java from the Windows file extension mapper is not convenient,
so I've included a simple CMD file to make it easier.
The batch file takes one argument: the name of the file to load.
A quick look at this simple batch file should make it self-evident.

The program is small and easy to use.
I've included a few sample characters to play with and get a feel for the program.
Its GUI should be self-explanatory, but here are some helpful notes to get you started.

**DETAILS**

Each portion of a character has a tabbed pane in the main window.
If you hover the pointer over a tab it pops up a more detailed explanation of the pane.
Click on a tab to view or edit that information.

The application window is resizable. It uses a GridBagLayout for all the panels,
which attempts to automatically compensate for the window and font size in a reasonable way.

Adjustments for ability scores, race abilities, class abilities etc. are handled automatically.
This also includes experience points, levels, and % bonuses.
Adjustments come from the original AD&D Players Handbook.
Adjustments for ability scores from 19-25 come from Deities & Demigods.

For example, on any character class tab, the "Add XPoints" button takes the given amount of XPoints,
adds the % bonus (if any), adds that to the character's total,
and if that goes into the next level of experience, it handles this as well.

Buttons with RED text automatically set adjustments or other data.
This will override data that was manually set, which is why the RED text is a warning.

Double-clicking is supported in list boxes & tree controls; it edits the given item.

There is no arbitrary limit to a character's size -
GUI elements such as list boxes, trees, etc. have no preset limit.
Yet individual entry fields are expected to be short - say 40 characters or less.
The program will handle strings of any size, but long data is truncated on printouts.

Some forms of data entry don't take effect until you use "Apply All".
"Apply All" takes what is in the GUI and applies it to the character in memory.
It does NOT write it to disk.
"Apply All" is done automatically before you save or print a character.

"Revert All" reverts to the last EDITED change of a character.
That is, in memory - NOT the last one saved to disk.
If you want to revert to the last saved version, open it without saving the current one.

Character class info:

There is one pane for each character class (Fighter, Cleric, etc.).
Classes that are not enabled have their tab text greyed out (but not disabled).
This way you can see at a glance which classes a character has.
To enable (or disable) a class for a character, click on its tab, then {un}check the Enabled checkbox.

Character equipment:

Equipment is a tree of items that can be nested to any depth.
The panel supports drag and drop to move or copy items (move is the default).
When you drag an item, its entire subtree goes with it (whether move or copy).
To copy an item, hold down the Ctrl key while dragging (before you drop).
This way you can indicate which items are contained within other items.

Saving & loading characters:

The program uses a compact binary format for saving characters.
A 1st level character typically requires about 1 kB disk space,
and even high level characters rarely need more than 4 kB.

Printing:

Printing uses the Java SDK's built-in print support.
It should work on any printer, at any resolution, local or networked, on any operating system.
I've successfully tested it on several printers (laser and inkjet).
The character sheet is a 2 column layout similar to what I have used for nearly 30 years.

Feedback:

If you have any feedback, contact me via email: mrc@mclements.net

There is 1 sample character file for testing this program: test1.dnd.
It is a character from a campaign back in the 1980s when I was in college.
You can load, save, edit, etc.

**DEVELOPMENT**

One area still under development is generating new characters.
I plan for this to be a full one-click experience for characters of any type,
including full equipment lists appropriate to the race & class,
intelligently randomized.
This feature would greatly reduce my workload as a dungeon master,
having to generate NPCs.
So far, it does a lot but it's not complete.
What it does: ability scores & adjustments, picking the best class based on scores,
picking a compatible race, pick a gender and a name,
equipment appropriate to the class, including weapons and armor.
The rest must be done manually, but this saves a lot of time.

I wrote this program long ago, back when Ant was commonly used.
So it's built with Ant, not Maven.
It's easy to import into Eclipse and set up to use its Ant builder.

This program uses 2 fonts when printing: Garamond and DejaVu Sans.
Both are freely available.
If you don't have them installed, the printouts won't look right.

The app has 2 command-line parameters: -font N and -file FILE

N is an integer that adjusts relative font size, positive or negative.
Java Swing was made back when monitors were lower res with less DPI than they have today.
Thus, its default font sizes are too small for modern monitors.
Typically, "-font +4" works well, but you can use any size adjustment.

FILE is the full path and filename of a character file to load on startup.
This makes it possible to create desktop icon wrappers around this app,
so you can double-click a file and have this app pop up and open it.

The build requires Junit 4.8.2 (file junit-4.8.2.jar).
It looks for an env var JUNIT_HOME for the directory to find it.

Thus: export JUNIT_HOME=/apps/junit (or wherever you put it in your local filesystem).

Example build: ant clean all

Example run: java -jar DnD.jar -font +4
