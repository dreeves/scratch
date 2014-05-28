del runesmith.jar
java -ea -classpath vrelta.jar;jcommander-1.13.jar vrelta.comp.Vreltac -cp vrelta.jar -src src -bin bin -conwidth 80
if %ERRORLEVEL% geq 1 goto err
cd bin
jar -cf ../runesmith.jar ./*
cd ..
rmdir /S /Q bin
java -ea -cp runesmith.jar;vrelta.jar runesmith.Main
if %ERRORLEVEL% geq 1 goto err
goto end
:err
pause
:end