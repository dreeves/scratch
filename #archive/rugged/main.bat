del *.class
cd ..
set CLASSPATH=rugged\lwjgl\*;
javac rugged/*.java
java -Djava.library.path=rugged\lwjgl\native\windows rugged/C
pause