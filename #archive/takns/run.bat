mkdir tmp

cd src
javac -cp .;%LOMBOK% -d ../tmp takns/*.java
cd ..

cd tmp
jar cf ../takns.jar *
cd ..

::if 5%1 equ 5 pause

::move /Y jutil.jar bin

rmdir tmp /S /Q

java -cp .;takns.jar;%LOMBOK% takns.main

pause