(defn take_label[sq](dl
  r <- (atom (transient []))
  (label (doseq [v sq] (swap! r conj! v)))
  (persistent! @r)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;; trash from kxlq
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 

; enable same-file parser extension:
  ; if there's a parsing error, say nothing until you actually attempt to execute that code
  ; if code is executed that modifies the parser, re-parse the rest of the file
; go back through it and ensure the parser really is extensible

; definition of symbol:
  ; A symbol... is a symbol.
  ; It's generally brief and human-friendly, although that isn't a technical requirement.
  ; In practice, you can rely on it being made of a string.
  ; Its contents are not entirely a comment: although fortran-style i-means-int is dumb, its contents _are_ used beyond simply identity comparison.
  ; clojure makes a distinction between "keywords" and "symbols"; keywords are symbols that evaluate to themselves and symbols are symbols that evaluate to something else. I might instead call those Symbol and ResolvingSymbol or maybe Identifier
; WE *ARE NOT* MAKING AN AUTO-OMNISCIENT SYSTEM (we can't; physics is unsolved) SO WE *CAN* HUMAN-INTERVENE FOR EACH SYSTEM TO INTERACT WITH AND THAT *IS* OKAY
; INSIDE-A-METHOD SHOULD NOT BE SPECIAL
; "kay. break time. So I don't really still feel like fixing clj. I really just want to have a nice happy rainbow language that works with anything (because it's been ported to everything) and is happy, and clj defines most of the infrastructure to do this easily for the JVM."
; so I looked at the top 60 clojure projects on github, and, while special characters are common in symbols, /\w+[^\s\w]+\w+/ is *rare* except for -
; clojure eval impl misc:
  ; wraps eval in (binding [Compiler/LOADER RT.makeClassLoader()]) and it has "if !LOADER.isBound()" commented out
  ; also, if ((meta form) :line) is non-nil it does (binding [that-value]) or something
  ; also throws new CompilerException(clojure.core/*file* @Compiler.LINE e) instead of any non-CompilerExceptions in eval
  ; for a Var o, if false_or_nil((meta o) :const) then o is evaluated as `('quote ~‹@o›)
; clojure macro impl: it passes in an extra &form and &env for cool but hacky reasons =)


;;;;;;;;; no this is bad... aaaaaagh how do i do this?
;;;;;;;;; hmm. remember when you were all "things can evaluate as 0-inf things! not just 1!" so what was that about, eh?
;;;;;;;;; (def VOID (Object.))
;;;;;;;;; y u no hav reedr macos?


;(def ^:private JUMP_CONTINUE (jutil.JumpContinue.))
;(defmacro continue [] `(throw JUMP_CONTINUE))
;(defmacro label [& body]
  ;`(try
  ;   (while true
  ;     (try
  ;       (dl ~@body (break))
  ;       (catch jutil.JumpContinue e#)))
  ;   (catch jutil.JumpBreak e#)))

(defn eval_tokens [tokens]
  (doseq [token tokens]
    (cond
      (isa token KSymbol)
        (if (<= (i\0) (int (. (. token name) charAt 0)) (i\9))
          (println "int of joy:\t\t\t" (Integer/parseInt (. token name)))
          (println "could not resolve symbol:\t" (str token))
          )
      (isa token Barrier)
        (println "barrier" (str token))
      (isa token String)
        (println "string literal of joy:\t\t" token)
      :else (throw (derx "unknown token" token))
      ))
  )

; WHERE WE WERE: we were about to define a resolve_symbol, but then we were bothered by the lisp-2-ness of this. Also, http://www.cs.utep.edu/cheon/cs3360/pages/haskell-syntax.html
  ; hmm. lisp-2 means functions are automatically applied; lisp-1 means a special function application operator
  ; alternatively: lisp-1 requires a function application operator; lisp-2 means the ability to declare vars so their value is automatically called
    ; in practice, lisp-2 has separate function and value namespaces and uses the function application operator as a function namespace specification operator
      ; while this is not completely without utility, it's kinda dumb. So, no.
  ; desire: both a function application operator and lisp-2 autocallability? i mean, clojure approves automacroability kinda?

; means no-thing, as opposed to nil, which means nil. like, the *concept* is that (list nil) == [nil] but (list VOID) == []
;(def VOID (Object.))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;; misc
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
  (defn rtit [& vs] (dl ; doc: "read-tokens interpret-token"
    v <- (apply str vs)
    (if (= v "")
      []
      (dl
        v0 <- (subs v 0 1)
        (if (special? (int (. v0 charAt 0)))
          (cons (interpret_token v0) (rtit (subs v 1)))
          [(interpret_token v)])))))
  (defn read_tokens [rdr on_eof] (dl
    ;!~ may resolve tokens in a different order than written
    ;! i wish i could write this as a generator - that would be SO nice
      ; it's safe to do that! i mean, you never really properly read more than one /[ALPHA|SYMBOLY]+/ or other thing at once
    A <- (StringBuilder.)
    B <- (StringBuilder.)
    (loop [state :symboly_A] (dl
      c <- (. rdr read)
      (if (eof? c)
        (concat (rtit A B) [(on_eof)])
        (dl
          rmacro <- (reader_macros c)
          (if rmacro
            (if (== (. A length) 0)
              (dl v <- (rmacro rdr c), (if (is v VOID) (recur :symboly_A) v))
              (if (reader_macro_whitespace_like? c)
                (dl v1 <- (rtit A B), v2 <- (rmacro rdr c), (if (is v2 VOID) v1 (concat v1 [v2])))
                (do (. rdr unread c) (concat (rtit A) (rtit B)))
                ))
            (case state
              :symboly_A (if (symboly? c)
                           (do (. A append (char c)) (recur state))
                           (do (. rdr unread c) (recur :alpha_A)))
              :alpha_A   (if (alpha? c)
                           (do (. A append (char c)) (recur state))
                           (do (. rdr unread c) (recur :symboly_B)))
              :symboly_B (if (symboly? c)
                           (do (. B append (char c)) (recur state))
                           (do (. rdr unread c) (recur :yield)))
              :yield (concat (rtit A) (rtit B))
              )
            )
          ))
      ))))




; the default system will be to lex 
musing on identifiers
so. conflict between restrictive and liberal identifier charsets.
One easy thing is to define load-string and load_string as identical and do other such stuff for other stuff.
but no: that path is critical failure if we ever must interact with an entity that has distinct things it names load-string and load_string
;;; old docs
; unfinished:
;   have an exception for "-foobar": lex it as "- foobar"?
;   oh my gosh operator precedence WHAT TO DO??
;   okay, i think if you just let . be a normal part of symbols and then do dynamic symbol tabling you can make java.lang.String work
;     WAIT NEVERMIND you want it for <expression>.x sigh...
;     ARGH there's also numbers
;     what if . is an infix macro where (. 'java 'lang) -> 'java.lang but (. 'java.lang 'String) -> class object for java.lang.String?
;       first check for being-a-class, next check for lhs-exists (and then access its member), else concatenate symbols with a . in the middle
;   thinking about how/if to give meaning to newlines and how to use , since clojure has it as whitespace. Maybe make , and newline equivalent?
;     that would be like c-style ";"s - seriously don't want to just find some other comment delimiter and use ;?
;   identifier resolution:
;     i mean you already have a process for identifier resolution; it's namespaces. The only issue is that you have to def each possibilty beforehand; you can't do it lazily. So make it lazyable?
;     since symbol resolution is custom and stuff, symbols containing such chars as / *can* resolve specially
;     numbers are just symbols that macro-resolution-time (or possibly some other time) resolve to numbers
;     ditto for % %1 %2 etc in #(...)
; quotes:
;   confused: # people DO use it in the middle, but rarely
;   foo-bar is DEEPLY used, and foobar- -foobar are pretty common
;   access to intepret-token (as interpret-symbol)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;; cljfix experiment
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
(ns cljfix
  (:refer-clojure :only [])
  (:require [clojure.core :as c])
  (:require clojure.string)
  (:require clojure.main)
  )

; simple groundwork for redefing clojure.core fns
(c/defmacro u [sym] `(d ~sym ~sym))
(c/defmacro d [a b]
  (c/let [ccsym (c/symbol "clojure.core" (c/name b))]
    `(do (def ~a ~ccsym) (c/reset-meta! (var ~a) (c/meta (var ~ccsym))))))

; simple fns
(d print println) (d print' print)
(d len count)
(d fold reduce) (d scan reductions)
(d aset! reset!)
; from lust but idk know why
(c/defmacro ? [test a b] `(if ~test ~a ~b)) ; (def ^:macro ? #'if)
(d id identity)
(d map-cat mapcat)
(def ^:macro do-if #'c/when)
(def ^:macro do-if-not #'c/when-not)
(def ^:macro do-if-let #'c/when-let)
(d me macroexpand) (d me1 macroexpand-1)

; now we write the code to launch the repl
(c/defn launch-repl-with [file]
  (c/remove-ns 'user)
  (ns user
    (:refer-clojure :only [])
    (:require [clojure.core :as c]) ; temporary
    (:use cljfix))
  (print "hey" file)
  (clojure.main/repl)
  )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;; FROM LUST and also utils i made since then
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
(defn ren [fl to] (. fl renameTo (file (. fl getParent) to)))
(defn subs_
  ([s a] (subs_ s a (. s length)))
  ([s a b] (. s substring (if (< a 0) (+ a (. s length)) a) (if (< b 0) (+ b (. s length)) b))))

(defn hgroup ; opt-args
  ([v] (partition-by identity v))
  ([f v] (partition-by f v)))
(def empty-queue clojure.lang.PersistentQueue/EMPTY)
(def cla-boolean Boolean/TYPE)
(def cla-byte    Byte/TYPE)
(def cla-char    Character/TYPE)
(def cla-short   Short/TYPE)
(def cla-int     Integer/TYPE)
(def cla-long    Long/TYPE)
(def cla-float   Float/TYPE)
(def cla-double  Double/TYPE)
(def cla-booleans (Class/forName "[Z"))
(def cla-bytes    (Class/forName "[B"))
(def cla-chars    (Class/forName "[C"))
(def cla-shorts   (Class/forName "[S"))
(def cla-ints     (Class/forName "[I"))
(def cla-longs    (Class/forName "[J"))
(def cla-floats   (Class/forName "[F"))
(def cla-doubles  (Class/forName "[D"))
(def bit& bit-and)
(def bit| bit-or)
;(def bit^ bit-xor)
(def bit! bit-not);(def bit~ bit-not)
(def bit>> bit-shift-right)
(def bit<< bit-shift-left)
;;;
(defn curly-infix-fn [f] (fn [reader char] (f (vec (LispReader/readDelimitedList \› reader true)))))
(def lr-macros  (make-array clojure.lang.IFn (+ (int \›) 10)))
(def lr-#macros (make-array clojure.lang.IFn (+ (int \›) 10)))
(doseq [[k v] {
    \' (clojure.lang.LispReader$VarReader.)
    \" (clojure.lang.LispReader$RegexReader.)
    \{ (clojure.lang.LispReader$SetReader.)

    ;TAKEN \> (fn [reader char] (let [sym (LispReader/read reader true nil true)] (eval `(declare ~sym)) sym))
    \( (clojure.lang.$$FnReader.)
    \‹ (clojure.lang.$$FnReader.)
    ;TAKEN \\ (fn [rdr ch] (int ((cache (clojure.lang.LispReader$CharacterReader.)) rdr ch)))
    }]
  (aset lr-#macros (int k) v))
(doseq [[k v] {
    \" (clojure.lang.LispReader$StringReader.)
    \; (clojure.lang.LispReader$CommentReader.)
    \' (clojure.lang.LispReader$WrappingReader. 'quote)
    \@ (clojure.lang.LispReader$WrappingReader. 'clojure.core/deref)
    \^ (clojure.lang.LispReader$MetaReader.)
    \` (clojure.lang.LispReader$SyntaxQuoteReader.)
    \~ (new-priv clojure.lang.LispReader$UnquoteReader)
    \( (clojure.lang.LispReader$ListReader.)
    \[ (clojure.lang.LispReader$VectorReader.)
    \{ (clojure.lang.LispReader$MapReader.)
    \\ (clojure.lang.LispReader$CharacterReader.)
    \% (new-priv clojure.lang.LispReader$ArgReader)
    \# (clojure.lang.LispReader$DispatchReader.)

    \‹ (curly-infix-fn (fn [[lh op rh]] `(~op ~lh ~rh)))
    ;\& (fn [reader char]
    ;     (if-not (= (clojure.lang.LispReader/read1 reader) (int \())
    ;       (ex))
    ;     (let [l (clojure.lang.$/readAnyDelimitedList reader true)]
    ;       l)) ; (let [l ...] l) done to emphasize that 'l just happens to be the right form of the data that we want
    ; EXCEPT YOUR COMMMENT IS WRONG! macros take lists and return lists, but we want to take contents and return contents?
    ; BUT WE MAYBE WANT THE STUFF BEFORE THE & FORM! HOWS CAN WE DO THAT??
    ;postfix-macro
    ;prefix-macro

    \) (clojure.lang.LispReader$UnmatchedDelimiterReader.)
    \] (clojure.lang.LispReader$UnmatchedDelimiterReader.)
    \} (clojure.lang.LispReader$UnmatchedDelimiterReader.)
    \› (clojure.lang.LispReader$UnmatchedDelimiterReader.)
    }]
  (aset lr-macros (int k) v))
(dot-priv LispReader "dispatchMacros" lr-#macros)
(dot-priv LispReader "macros" lr-macros)
;
(defn slice-fn [coll start end step] ; should be named slice? but isn't because we're still using `slice` for the macro?
  (let [start (if ‹start = 0› nil start)
        step (if ‹step = 1› nil step)]
    (if ‹(vector? coll) and (not step)›
      (if end
        (subvec coll start end)
        (subvec coll start))
      (let [t coll
            t (if start (drop start t) t)
            t (if end (take ‹end - ‹start or 0›› t) t)
            t (if step (take-nth step t) t)]
        t))))
(defmacro slice
    ;! a bunch of hardcoding because i'm too lame to implement split-all
    ; or maybe using parsing tools would be better?
    ;(defn split-all [v coll] (map-cat #(if ‹(first %) = v› (repeat ‹(len %) - 1› ()) [%]) (partition-by #‹% = v› coll)))
    ;(defn partition-by' [f coll]
    ;  (if-let [s (seq coll)]
    ;    (let [ffs (f (first s))
    ;          run (take-while #‹ffs = (f %)› s)]
    ;      (cons run (partition-by' f (drop (len run) s))))))
    ;[coll & rest] `(slice-fn ~coll ~@(map first (split-all '/ rest))))
  ([coll] coll)
  ([coll a]
    (if ‹a = '/›
      coll ; /
      `(slice-fn ~coll ~a nil nil))) ; start
  ([coll a b]
    ‹(if ‹a = '/›
      (if ‹b = '/›
        coll ; / /
        `(slice-fn ~coll nil ~b nil)) ; / end
      (if ‹b = '/› `(slice-fn ~coll ~a nil nil))) ; start /
    or (ae)›)
  ([coll a b c]
    ‹(if ‹a = '/›
      (if ‹b = '/›
        `(slice-fn ~coll nil nil ~c) ; / / step
        (if ‹c = '/› `(slice-fn ~coll nil ~b nil))) ; / end /
      (if ‹b = '/›
        (if ‹c = '/›
          `(slice-fn ~coll ~a ) ; start / /
          `(slice-fn ~coll nil ~b nil)))) ; start / end
    or (ae)›)
  ([coll a b c d]
    ‹(if ‹a = '/›
      (if ‹c = '/› `(slice-fn ~coll nil ~b ~d)) ; / end / step
      (if ‹b = '/›
        (if ‹c = '/›
          `(slice-fn ~coll ~a nil ~d) ; start / / step
          (if ‹d = '/› `(slice-fn ~coll ~a ~c nil))))) ; start / end /
    or (ae)›)
  ([coll a b c d e]
    (if ‹‹b = '/› and ‹d = '/››
      `(slice-fn ~coll ~a ~c ~e) ; start / end / step
      (ae))))
;
(aset (dot-priv LispReader "macros") #\‹
  (curly-infix-fn (fn [v]
    (cond
      ‹(len v) == 0› nil
      ‹(len v) == 1› (v 0)
      ‹(odd? (len v)) and (apply = (slice v 1 / / 2))›
        `(~(v 1) ~@(slice v / / 2))
      :else
        `(~(v 1) ~(v 0) ~@(slice v 2))
      ))))
;;;
(defn symstr [& args] (symbol (apply str args)))
(defmacro reqd [ns name] (require ns) (symstr ns"/"name))
;;;
(def != not=)
(def !== ‹not & ==›)
(def is-not ‹not & is›)
(def file (reqd clojure.java.io file))
(def split-lines (reqd clojure.string split-lines))
;;;
(defn range-incl [a b] (range a ‹b + 1›))
(defmacro defmulti' [name dispatch-fn & cases]
  `(do
     (defmulti ~name ~dispatch-fn)
     ~@(map #‹`(defmethod ~name ~@%)› cases)))
(defn file-seq [v] (clojure.core/file-seq (file v)))
(defn has [coll v] (some #{v} coll))
(defn quote-fn [x] `(quote ~x))
(defn re-fn [re] #(re-find re %))
(defn exec [cmd & {:keys [then]}] (dool ; safe-kwargs
  ;! check out http://docs.oracle.com/javase/1.4.2/docs/api/java/lang/Runtime.html#exec(java.lang.String, java.lang.String[], java.io.File)
    ; expose envp param
    ; expose dir param
  cmd <- (if (coll? cmd) (into-array String (map str cmd)) cmd)
  proc <- (. (Runtime/getRuntime) exec cmd nil nil)
  (if then (-> (Thread. #(let [t (. proc waitFor)] (then {:exit-status t}))) (.start)))
  {:destroy #(. proc destroy)
   :in (. proc getOutputStream)
   :out (. proc getInputStream)
   :err (. proc getErrorStream)}))
(defn hex [v] (format "%x" v))
(defn print- [v] (print "info:" v) v)
(defn digit? [c] ‹‹#\0 <= c› and ‹c <= #\9››)
(defn array? [v] (-> v (class) (.isArray)))
(defn apop! [atom] (dool, r <- (last @atom), (swap! atom pop), r))
(defn word-wrap [at s]
  (str-join "\n"
    (for [s (split-lines s)]
      (loop [r []
             s s]
        (if ‹(len s) > at›
          (recur (conj r (subs s 0 at)) (str "    "(subs s at)))
          (str-join "\n" (conj r s)))))))
(defmacro redef [sym v] `(alter-var-root #'~sym (fn [_#] ~v)))
(defmacro do-while [test & body]
  `(loop []
     ~@body
     (if ~test (recur))))
(defn del [fl]
  (if (. fl isDirectory) (mapv del (. fl listFiles)))
  (if (and (. fl exists) (not (. fl delete))) (throw (java.io.IOException. (str "Couldn't delete file "fl))))
  )
(defmacro undef [name] `(ns-unmap *ns* '~name))
(defmacro assoc-meta [name & rest] `(with-meta ~name (assoc (meta ~name) ~@rest))) ;! really should be a defn? ;! improve to allow (assoc-meta name :private) (like ^Integer and ^:private)
(defmacro defn-memo
  "defn plus memoizes with clojure.core/memoize"
  [& rest]
  `(doto (defn ~@rest) (alter-var-root memoize)))
(defmacro swallow [ex-class exp] `(try ~exp (catch ~ex-class e#)))
;! interesting. delete? or use?
;(defmacro exval [exp] `(try ~exp (catch Throwable e# e#)))
;(defmacro unexval [exp] `(if (isa ~exp Throwable) (throw ~exp) ~exp))
(defmacro if' [& body]
  (if ‹(len body) <= 3›
    `(if ~@body)
    `(cond ~@(if (even? (len body)) body (concat (butlast body) [:else (last body)])))))
(defmacro fortimes [bindings & body] (dool
  [i n] <- bindings
  n' <- (gensym)
  `(dool
     ~n' ~'<- (long ~n)
     (loop [~i 0
            r# (transient [])]
       (if ‹~i < ~n'›
         (recur
           (unchecked-inc ~i)
           (conj! r# (do ~@body))
           )
         (persistent! r#)
         ))
     )
  ))
;;;
; could be put in a math namespace
(defn get2fold [^long n]
  (if ‹n == 0›
    0
    (loop [r 1]
      (if ‹r < n›
        (recur ‹r * 2›)
        r))))
;;;
; clojure.contrib misc
(defn separate [f coll] [(filter f coll) (filter (complement f) coll)])
(defmacro- defnilsafe [nil-safe-name non-safe-name]
  `(defmacro ~nil-safe-name
     {:arglists '([~'x ~'form] [~'x ~'form ~'& ~'forms])}
     ([x# form#]
       `(let [~'i# ~x#] (when-not (nil? ~'i#) (~'~non-safe-name ~'i# ~form#))))
     ([x# form# & more#]
       `(~'~nil-safe-name (~'~nil-safe-name ~x# ~form#) ~@more#))))
(defnilsafe .?. ..)
(defnilsafe -?> ->)
(defnilsafe -?>> ->>)
(defnilsafe doto? doto)
(defmacro defalias ; opt-args ;! use this
  "Defines an alias for a var: a new var with the same root binding (if
  any) and similar metadata. The metadata of the alias is its initial
  metadata (as provided by def) merged into the metadata of the original."
  ([name orig]
    `(do
       (alter-meta!
        (if ‹#'~orig .hasRoot›
          (def ~name ‹#'~orig .getRoot›)
          (def ~name))
        ;! this, no. undo this.
        ;; When copying metadata, disregard {:macro false}.
        ;; Workaround for http://www.assembla.com/spaces/clojure/tickets/273
        #(conj (dissoc % :macro)
               (apply dissoc (meta #'~orig) (remove #{:macro} (keys %)))))
       #'~name))
  ([name orig doc]
    `(defalias ~(assoc-meta name :doc doc) ~orig)))
(defmacro import-static
  "Imports the named static fields and/or static methods of the class
  as (private) symbols in the current namespace.

  Example: 
      user=> (import-static java.lang.Math PI sqrt)
      nil
      user=> PI
      3.141592653589793
      user=> (sqrt 16)
      4.0

  Note: Static methods are defined as MACROS, not first-class fns."
  [cla & fields-and-methods]
  (let [only (set (map str fields-and-methods))
        statics #(set (map (memfn getName) (filter #(java.lang.reflect.Modifier/isStatic (. % getModifiers)) %)))
        fields-to-do  ((reqd clojure.set intersection) only (statics (. cla getFields )))
        methods-to-do ((reqd clojure.set intersection) only (statics (. cla getMethods)))
        import-field  #(sid `(def-      ~(symbol %) (. ~class ~(symbol %))))
        import-method #(sid `(defmacro- ~(symbol %) [& args#] `(. ~'~class ~'~(symbol %) ~args#)))
        ]
    `(do ~@(map import-field fields-to-do)
         ~@(map import-method methods-to-do))))
;;;
; because whatevs
(defn rl ; opt-args
  ([] (rl #>working-ns))
  ([v] (use v :reload)))
(defn rla ; opt-args
  ([] (rla working-ns))
  ([v] (use v :reload-all)))
;
(def repl-user-code (dool
  it <- ‹(first *command-line-args*) or ""›
  it <- (re-find #"src\\(.+)\.clj$" it)
  (if it (def working-ns (-> (second it) (.replace \\ \.) (symbol))))
  ‹it ? #(use working-ns) #‹››))
;;;
(ns temporary-ns
  (:refer-clojure :exclude [print file-seq])
  (:use lust)
  )
(require 'brand-z)
