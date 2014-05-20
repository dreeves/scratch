@echo off
java -cp "lib/*" vrelta.comp.Vreltac -cp "lib/*" -src src -bin bin -conwidth 80
if %ERRORLEVEL% geq 1 goto err
cd bin
jar -cf ../test.jar ./*
cd ..
echo ---------------------------------
java -cp "test.jar;lib/*" test.Main
:err
rmdir /S /Q bin
pause