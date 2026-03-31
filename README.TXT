DnDFile README.TXT

DnD is a Java Swing application that manages AD&D characters.
I wrote it to familiarize myself with various less-often used parts of the Java SDK,
such as tree controls, drag and drop, the print system, etc.
Also I wrote it because an AD&D character is a complex data structure
that makes good practice for object oriented data modeling,
specifically with some of the JDK 1.5+ features - generics, enums, etc.
The program uses and requires JDK 1.6.

To run the program, unpack the ZIP to the directory of your choice.
Make sure you have JDK 1.6 installed and in your path.
To start the program:

java -jar DnD.jar [-font X] [-file FILENAME]

All parameters (-font & -file) are optional.

With -font, X is the size of the font you want to use.
X is an adjustment, + or -, which will grow or shrink the sizes of all the default fonts.
Examples:
	-font -1	makes all fonts 1 point smaller
	-font 4		makes all fonts 4 points larger

FILENAME is the path and filename of a character file to load on startup.

I've run and tested it on Linux, Win 2k and Win XP.

It is useful to create a file type for the "dnd" file extension
(or whatever extension you want to use) and map it to this program.
Launching Java from the Windows file extension mapper is not convenient,
so I've included a simple CMD file to make it easier.
The batch file takes one argument: the name of the file to load.
A quick look at this simple batch file should make it self-evident.

The program is small and easy to use.
I've included a few sample characters to play with and get a feel for the program.
Its GUI should be self-explanatory, but here are some helpful notes to get you started.

Overall:

Each portion of a character has a tabbed pane in the main window.
If you hover the pointer over a tab it pops up a more detailed explanation of the pane.
Click on a tab to view or edit that information.

The application window is resizable. It uses a GridBagLayout for all the panels,
which attempts to automatically compensate for the window and font size in a reasonable way.

Adjustments for ability scores, race abilities, class abilities etc. are handled automatically.
This also includes experience points, levels, and % bonuses.
Adjustments come from the original AD&D Players Handbook.
Adjustments for ability scores from 19-25 come from Deities & Demigods.
For example, the "Add XPoints" button takes the given amount of XPoints,
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

Saving & loading characters:

The program uses a compact binary format for saving characters.
A 1st level character typically requires about 1 kB disk space,
and even high level characters rarely need more than 4 kB.

Printing:

Printing uses the Java SDK's built-in print support.
It should work on any printer, at any resolution, local or networked, on any operating system.
I've successfully tested it on a couple of printers (one laser, one inkjet).
The character sheet is a 2 column layout similar to what I have used for nearly 30 years.

Feedback:

If you have any feedback, contact me via email: mike@mclements.net
