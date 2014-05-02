mkdir bin

cd src
javac -d ../bin -cp %CLOJURE%;../bin/*;../../dep/* image/*.java idle/*.java
cd ..

cd bin
jar cf ../_.jar *
cd ..

rmdir bin /S /Q

pause