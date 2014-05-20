set CLASSPATH=*;
java org.mozilla.javascript.tools.jsc.Main *.js
java Main
pause
del *.class
rem -extends JAVA-CLASS-NAME : is obvious
rem -implements JAVA-INTF-NAME : is obvious
rem -debug -g : Specifies that debug information should be generated. May not be combined with optimization at an optLevel greater than zero.
rem -nosource : Does not save the source in the class file.
rem -o OUTPUTFILE : Writes the class file to the given file (which should end in .class). The string outputFile must be a writable filename.
rem -d outputDirectory : Writes the class file to outputDirectory.
rem -opt OPTLEVEL : Optimizes at level optLevel, which must be an integer between -1 and 9. See Optimization for more details.
rem -package PACKAGENAME : Specifies the package to generate the class into.