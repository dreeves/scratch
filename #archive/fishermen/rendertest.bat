cd ..
set CLASSPATH=fishermen\jars\kryonet-1.01\*;fishermen\jars\*;fishermen\jars\lwjgl\*;
javac -implicit:class fishermen/*.java
java -Djava.library.path=fishermen\jars\lwjgl\native\windows fishermen.Klient -rendertest
pause