java -cp .;%CLOJURE%;%LWJGL%;bin/* -Djava.library.path=%LWJGL_NATIVE% clojure.main -e "(use 'asplode)" -r
pause