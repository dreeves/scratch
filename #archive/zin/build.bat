mkdir bin

cd src
javac -d ../bin -cp C:/code/bin/*;C:/code/lwjgl-2.8.3/jar/*;C:/code/jbullet-plus/*;. clojure/lang/*.java zutil/*.java bullet/*.java jin/collision/*.java jin/geometry/*.java jin/geometry/contact/*.java jin/geometry/util/*.java jin/math/*.java jin/physics/*.java jin/physics/constraint/*.java jin/physics/constraint/contact/*.java jin/physics/constraint/joint/*.java jin/physics/force/*.java jin/physics/solver/*.java jin/physics/solver/experimental/*.java jin/util/*.java
cd ..

cd bin
jar cf ../zutil.jar *
cd ..

if 5%1 neq 50 pause

move /Y zutil.jar ../bin

rmdir bin /S /Q