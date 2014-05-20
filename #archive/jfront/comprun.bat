cd ..
javac -Xlint:unchecked jfront/*.java
java jfront.M
cd jfront
del *.class
pause