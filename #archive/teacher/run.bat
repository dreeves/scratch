del teacher.jar
java -ea -classpath vrelta.jar;jcommander-1.13.jar vrelta.comp.Vreltac -cp vrelta.jar -src src -bin bin
if %ERRORLEVEL% geq 1 goto err
cd bin
jar -cf ../teacher.jar ./*
cd ..
rmdir /S /Q bin
java -ea -cp teacher.jar;vrelta.jar;lwjgl/* teacher.Main
if %ERRORLEVEL% geq 1 goto err
java -cp "asm-4.0_RC1/lib/*" org.objectweb.asm.util.CheckClassAdapter Test.class
java Test
:err
pause