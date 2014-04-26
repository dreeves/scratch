mkdir bin

cd src
javac -d ../bin -cp %CLOJURE%;../bin/* clojure/lang/*.java misc/*.java
cd ..

cd bin
jar cf ../_.jar *
cd ..

rmdir bin /S /Q

pause