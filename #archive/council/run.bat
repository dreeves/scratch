del council.jar
java -ea -classpath vrelta.jar;jcommander-1.13.jar vrelta.comp.Vreltac -cp vrelta.jar;lwjgl/* -src src -bin bin
if %ERRORLEVEL% geq 1 goto err
cd bin
jar -cf ../council.jar ./*
cd ..
rmdir /S /Q bin
java -ea -cp council.jar;vrelta.jar;lwjgl/* -Djava.library.path=lwjgl\native\windows council.Main
if %ERRORLEVEL% geq 1 goto err
::goto end
:err
pause
:end