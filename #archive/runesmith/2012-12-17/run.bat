mkdir log
cd log
del *.png
cd ..

java -cp .;%CLOJURE%;bin/* clojure.main -e "(use 'runesmith) (main)" -r
pause