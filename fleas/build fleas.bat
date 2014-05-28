"C:\Program Files\Java\jdk1.6.0_21\bin\javac.exe" -target 1.5 src/flea/*.java
cd src
jar cf ../fleas.jar ./*
cd ..
pause