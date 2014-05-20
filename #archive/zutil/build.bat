mkdir bin

set JAR_CP=C:/code/bin/*;C:/code/lwjgl-2.8.3/jar/lwjgl-debug.jar;C:/code/lwjgl-2.8.3/jar/lwjgl_util.jar;C:/code/jbullet-plus/*;../bin/*

cd src
javac -d ../bin -cp %JAR_CP% clojure/lang/*.java zutil/*.java
cd ..

cd bin
jar cf ../zutil.jar *
cd ..

if 5%1 neq 50 pause

move /Y zutil.jar ../bin

rmdir bin /S /Q