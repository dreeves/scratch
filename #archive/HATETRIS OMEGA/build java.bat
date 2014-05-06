mkdir tmp

cd src/jvm
javac -cp %CLOJURE%;%LOMBOK% -d ../../tmp hatetris/*.java jutil/*.java
cd ../..

cd tmp
jar cf ../j_hatetris.jar *
cd ..

if 5%1 equ 5 pause

move /Y j_hatetris.jar bin

rmdir tmp /S /Q