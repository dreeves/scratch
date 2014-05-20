del classviewer.jar
java -ea -classpath "./*" vrelta.comp.Vreltac -cp vrelta.jar;jCFL-2.0.161.jar -src src -bin bin
if %ERRORLEVEL% geq 1 goto err
cd bin
jar -cf ../classviewer.jar ./*
cd ..
rmdir /S /Q bin
call run.bat
:err
pause