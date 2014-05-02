; downright hacky utils
(deftype OStr [s] Object (toString [me] s)) ; ideally should be inside defn of `object`
(defn object ([] (Object.)) ([v] (OStr. (str v))))
(defmacro doseq_indexed[[iname name coll] & rest]
  `(doseq [[~iname ~name] (map vector (range) ~coll)] ~@rest))
(defmacro doseq_flat[let_exprs & body] (dl
  q <- (#>transpose (partition 2 let_exprs))
  `(doseq [~(vec (q 0)) (map vector ~@(q 1))] ~@body)))
(defmacro for'[vec & body] `(let [r# (atom [])] (doseq ~vec (swap! r# conj (dl ~@body))) @r#))

; reflection
;static Set<String> jslots(Class c) {return set(map(list((Object[])c.getDeclaredFields()),fn1("getName")).toArray());}
(defn jnew[c & args](dl
  r <- (atom nil)
  (label (doseq [v (. c getDeclaredConstructors)]
    (try (dl (reset! r (. v newInstance (object-array args))) (break))
      (catch IllegalArgumentException e))
    ))
  (if (nil? @r) (throw (IllegalArgumentException.)))
  @r))

; and a bunch of reasonable names for working with deques
(defn dq
  ([] (ArrayDeque.))
  ([sq] (if (isa sq ArrayDeque) sq (#>dq_add_all (dq) sq)))
  )
(defn dq_push       [^ArrayDeque me v] (. me addLast v))
(defn dq_pop        [^ArrayDeque me] (. me removeLast))
(defn dq_peek       [^ArrayDeque me] (. me getLast))
(defn dq_push_tail  [^ArrayDeque me v] (. me addFirst v))
(defn dq_pop_tail   [^ArrayDeque me] (. me removeFirst))
(defn dq_peek_tail  [^ArrayDeque me] (. me getFirst))
(defn dq_pop_       [^ArrayDeque me] (. me pollLast))
(defn dq_peek_      [^ArrayDeque me] (. me peekLast))
(defn dq_pop_tail_  [^ArrayDeque me] (. me pollFirst))
(defn dq_peek_tail_ [^ArrayDeque me] (. me peekFirst))
(defn dq_empty      [^ArrayDeque me] (. me isEmpty))
(defn dq_add_all    [^ArrayDeque me sq] (doseq [v sq] (dq_push me v)) me)
(defn dq_len        [^ArrayDeque me] (. me size))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; `read` and reader macros ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

; tokens
(deftype KSymbol [name] Object
  (toString [me]
    (if (= name "\n")
      "<NL>"
      (if (every? #{\t \space} name)
        (str "<"(-> name (.replace " " "_") (.replace "\t" "t"))">")
        name
        )))
  (equals [me other] (and (isa other KSymbol) (is name (. other name)))) ; simple for now; ideally intern the symbols themselves? idk.
  (hashCode [me] (+ (. name hashCode) 0x1234))
  )
(defn ksymbol [name] (KSymbol. (. name intern)))
(def SPACE (ksymbol " "))
(def NEWLINE (ksymbol "\n"))
(defn line_separator? [v] (and (isa v KSymbol) (not (is v SPACE)) (every? #{\t \space} (. v name))))

; helpers
(defmacro _k_pop [] `(dl v <- (last @~'stack) (swap! ~'stack pop) v))
(defmacro _k_push [v] `(swap! ~'stack conj ~v))
(defmacro _k_last_len [] '(first (last @stack)))
(defmacro _k_concat_last [& vs] `(dl t <- (_k_pop) (_k_push [(first t) (concat (second t) ~@vs)])))

; read. calls reader macros in a loop, then has whitespace symbols convert stuff into lists (INDENT/DEDENT, but a tree instead of flat)
(defn k_read "reads a single form" [rdr] (dl
  ; read a line, then read lines until the first nonindented line
  forms <- (cons ((#>reader_macro (i\newline)) rdr (i\newline))
             (take-while
               #(not (and (isa % KSymbol) (== (. (. % name) length) 0)))
               (repeatedly (fn me[] (dl ch <- (read1 rdr)
                                        v <- ((reader_macro ch) rdr ch)
                                        (if (is v rdr) (me) v))))
               ))

  ; ensure tabs and spaces are not too mixed
  pairs <- (map (fn [[[a] b]] [(. a name) b]) (partition 2 (partition-by line_separator? forms)))
  (doseq [[a b] (windows 2 (map first pairs))]
    (if (not (or (. b startsWith a) (. a startsWith b))) (throw (derx "bad mixed tabs and spaces"))))

  ; convert indentation to lists
  pairs <- (map (fn [[a b]] [(. a length) b]) pairs)
  stack <- (atom [])
  (doseq [[len sq] (concat pairs [[0 '()]])]
    (while (and (seq @stack) (< len (_k_last_len))) (dl
      t <- (_k_pop)
      (if (or (empty? stack) (> len (_k_last_len)))
        (_k_push [len `(~(second t))])
        (_k_concat_last [(second (_k_pop))])
        )
      ))
    (if (or (empty? @stack) (> len (first prev)))
      (_k_push [len sq])
      (_k_concat_last [NEWLINE] sq))
    )

  (assert (= (len @stack) 1))
  (first @stack)))

;(rmdef (mi"\n\r") [rdr ch]
;  dent <- (gobble #{\space \t })
;  nls <- (gobble #{\n \r })
;  ch <- (read1 rdr)
;  (if (is (reader_macro ch) this) )
;  )
;(rmdef (mi" \t\u000b\u000c") [rdr ch]
;  ; eat whitespace and comments
;  (loop [ch ch]
;    (if (is (reader_macro ch) this)
;      (recur (read1 rdr))
;      (if (== ch (i\/)) (dl
;        ch2 <- (read1 rdr)
;        (if (== ch2 (i\*))
;          (loop [ch1 0 ch2 0 nested 1]
;            (cond
;              (or (and (== ch1 (i\*)) (== ch2 (i\/))) (== ch2 -1))
;                (if (> nested 1)
;                  (recur ch2 (read1 rdr) (- nested 1))
;                  nil)
;              (and (== ch1 (i\/)) (== ch2 (i\*)))
;                (recur ch2 (read1 rdr) (+ nested 1))
;              :else
;                (recur ch2 (read1 rdr) nested)
;              ))
;          (dl (unread1 rdr ch2) (unread1 rdr ch)))
;        ) (unread1 rdr ch))))
;  ; return SPACE only if 
;  ch <- (read1 rdr)
;  (if (is (reader_macro ch) (reader_macro (i\n)))
;    ((reader_macro ch) rdr ch)
;    (dl (unread1 rdr ch) SPACE)
;    ))

; spec: ?except we want it to be extensible?
; unused: ,\
; "whenever asked to read, start with a newline" doesn't work when you have multiple forms on the same line
; um. WHAT TO DO???
; OH DEAR, THIS IS HORRIBLE, I FORGOT SOMETHING MAJOR: python "def f():\n return [\n]\n" is legal!
; FILE = { WHITESPACE | SYMBOL | STRING } EOF
  ; WHITESPACE = ( /[ \t\u000b\u000c\n\r]/ | ? /* until */ or EOF (nestable) ? | ? // or # or ; until /(\n|\r|EOF)/ ? )+
    ; if this did not contain any /[\n\r]/: return SPACE
    ; else: return a symbol of (match /.*[\n\r]([ \t]*).*/ all the whitespace)
    ; later, we'll interpret this symbols to give us python-style indentation
    ; except, interprets lines `SPACE a` then `b` as `INDENT a DEDENT b` instead of error
    ; and, mixed tabs and spaces is an error if it causes dependence on the width of a tab (like python -tt)
  ; STRING = '"' { ? string body stuff ? } '"'
  ; SYMBOL = ALPHA | OTHER | SPECIAL
    ; ALPHA = /[0-9A-Za-z$_ UNICODE_BEYOND_0xff]+/
    ; OTHER = /[+-*/%&|=<>!?:@~]+/
    ; SPECIAL = /['`^.()[\]{}]/
; FILE = { LINE_SEPARATOR | SYMBOL | STRING | SPACE } EOF

;(defn launch_repl_and_load [file]
;  (remove-ns 'user)
;  (ns user
;    (:refer-clojure :only [])
;    (:use kxlq))
;  (println "hey" file)
;  )

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; kxlq ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;
(activate_kxlq_mode):

/*
/*jo+three
***
jo +three
;j0-argle*/
*/

; test: bad mixed tabs and spaces
; test: indentation of all sorts

rprint add_val(add_val(6 7 8) 9)
[2 [3 4] "kimchi" inc 9.05 7] -> list
rprint list
rprint [[1 2] [3 4]][0][1]

;alpha[5]
;{ ansible    rotunda,
;}
;"str"

;not-a-comment

;a,b
