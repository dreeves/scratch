del runesmith.jar
java -ea -cp "jars/*" vrelta.comp.Vreltac -cp "jars/vrelta.jar" -src src -bin bin -conwidth 80
if %ERRORLEVEL% geq 1 goto err
cd bin
jar -cf ../runesmith.jar ./*
cd ..
rmdir /S /Q bin
java -ea -cp "runesmith.jar;jars/vrelta.jar" runesmith.Main
if %ERRORLEVEL% geq 1 goto err
goto end
:err
pause
:end