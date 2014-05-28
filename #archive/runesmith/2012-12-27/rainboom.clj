(ns rainboom
  (:refer-clojure :exclude [print])
  (:import zutil.UncheckedLong)
  (:require clojure.string))

;! lang want-list
; a nicer form than (.x bg) or (-> bg (.x)) or (.. bg (x)), like bg.x
; compile-time func overloading; rand-nth is silly!
; type-hint inference
; "inline let"; `let` that works until the end of the current scope-loid or possibly fn-loid
; "def invoke(this x y): pixels[(offset[1]+y)*X + offset[0]+x]" vs "(invoke [this x y] (aget pixels ‹‹‹(aget offset 1) + y› * (dims 0)› + ‹(aget offset 0) + x››))"

;(set! *warn-on-reflection* true)

(defmacro defmacro' [a b] `(defmacro ~a [& ~'body] (cons '~b ~'body)))
;
(def != not=)
(defn !== [a b] (not (== a b)))
(def is identical?)
(def id identity)
(def cmp compare)
(def len count)
(def fold reduce)
(def scan reductions)
(def map-cat mapcat)
(defn abs [^Double n] (Math/abs n))
(def sqrt #(Math/sqrt %))
(def ^:dynamic ** #(Math/pow % %2))
(def nano-time #(System/nanoTime))
(def floor #(int (Math/floor %)))
(def ceil #(int (Math/ceil %)))
(def print' clojure.core/print)
(def print println) ;! copy python-style print sometime
(defmacro' if-do when)
(defmacro' if-not-do when-not)
(defmacro' if-let-do when-let)
(defn sum [v] (apply + v))
;
(def i+ unchecked-add-int)
(def i- unchecked-subtract-int)
(def i* unchecked-multiply-int)
(def iD unchecked-divide-int)
(def irem unchecked-remainder-int)
(def i_ unchecked-negate-int)
(defn l+ [a b] (. zutil.UncheckedLong unchecked_long_add       a b))
(defn l- [a b] (. zutil.UncheckedLong unchecked_long_subtract  a b))
(defn l* [a b] (. zutil.UncheckedLong unchecked_long_multiply  a b))
(defn lD [a b] (. zutil.UncheckedLong unchecked_long_divide    a b))
(defn lrem[a b](. zutil.UncheckedLong unchecked_long_remainder a b))
(defn l_ [a]   (. zutil.UncheckedLong unchecked_long_negate    a  ))
(def bit& bit-and)
(def bit| bit-or)
;(def bit^ bit-xor)
;(def bit~ bit-not)
(def bit! bit-not)
(def bit>> bit-shift-right)
(def bit<< bit-shift-left)
;
(defn both [f x] [x (f x)])
(defn flip [f] #(f %2 %))
(defmacro defmulti' [name dispatch-fn & cases] ; allow docstrings and stuff
  `(do
    (defmulti ~name ~dispatch-fn)
    ~@(map (fn [v] `(defmethod ~name ~@v)) cases)))
(defmacro defs [prefix suffixs bodys] `(do ~@(map (fn [suffix body] `(def ~(symbol (str prefix"-"suffix)) ~body)) suffixs bodys)))
(defn ae [& more] (Thread/dumpStack) (throw (AssertionError. (clojure.string/join " " more))))
(defmacro def-let [[var name] & more] `(def ~(symbol name) (let [~var ~name] ~@more)))
(defn j-get [^java.lang.reflect.Field field] (.get field nil))
(defn j-set [^java.lang.reflect.Field field v] (.set field nil v))
(defn j-field [^Class class field-name] (doto (.getDeclaredField class field-name) (.setAccessible true)))
(defn read-image [path] (javax.imageio.ImageIO/read (java.io.File. path)))
(defn write-png [bi path] (javax.imageio.ImageIO/write bi "png" (java.io.File. path)))
(defn min-val-map [map] (apply min-key #(.val %) map))
(defn clamp [x min' max'] (max min' (min x max')))
(defmacro do-let [v & rest] (if (symbol? v) `(let [~v ~(rest 0)] ~@(drop 1 rest)) `(do v ~(do-let rest))))
(defn hex [v] (format "%x" v))
(defn print- [v] (print "info:" v) v)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; curly-infix ; except ‹3 + 4› instead of {3 + 4}
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def lispReader-macros         (make-array clojure.lang.IFn (+ (int \›) 10)))
(def lispReader-dispatchMacros (make-array clojure.lang.IFn (+ (int \›) 10)))
(let [field (j-field clojure.lang.LispReader "macros")]
  (System/arraycopy (j-get field) 0 lispReader-macros 0 256)
  (j-set field lispReader-macros))
(let [field (j-field clojure.lang.LispReader "dispatchMacros")]
  (System/arraycopy (j-get field) 0 lispReader-dispatchMacros 0 256)
  (j-set field lispReader-dispatchMacros))
(aset lispReader-dispatchMacros \> (fn [reader char] (let [sym (clojure.lang.LispReader/read reader true nil true)] (eval `(declare ~sym)) sym)))
(aset lispReader-dispatchMacros 0x28 (clojure.lang.NestableFnReader.)) ; int \( == 0x28
(aset lispReader-dispatchMacros (int \‹) (clojure.lang.NestableFnReader.))
(aset lispReader-macros (int \›) (clojure.lang.LispReader$UnmatchedDelimiterReader.))
(defn iterate-curly-infix [f] (aset lispReader-macros (int \‹) (fn [reader char] (f (vec (clojure.lang.LispReader/readDelimitedList \› reader true))))))
(iterate-curly-infix (fn [[lh op rh]] `(~op ~lh ~rh)))
;
(defn slice-fn ; should be named slice? but isn't because we're still using `slice` for the macro?
  ([coll end] (slice-fn coll nil end nil))
  ([coll start end] (slice-fn coll start end nil))
  ([coll start end step]
    (let [start (if ‹start = 0› nil start)
          step (if ‹step = 1› nil step)]
      (if ‹(vector? coll) and (not step)›
        (if end
          (subvec coll start end)
          (subvec coll start))
        (let [t coll
              t (if start (drop start t) t)
              t (if end (take ‹end - start› t) t)
              t (if ‹step and ‹step != 1›› (map first (partition-all step t)) t)]
          t)))))
(defmacro slice
    ; a bunch of hardcoding because i'm too lame to implement split-all
    ; or maybe using parsing tools would be better?
    ;(defn split-all [v coll] (map-cat #(if ‹(first %) = v› (repeat ‹(len %) - 1› ()) [%]) (partition-by #‹% = v› coll)))
    ;(defn partition-by' [f coll]
    ;  (if-let [s (seq coll)]
    ;    (let [ffs (f (first s))
    ;          run (take-while #‹ffs = (f %)› s)]
    ;      (cons run (partition-by' f (drop (count run) s))))))
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
(iterate-curly-infix (fn [vec]
  (cond
    ‹(len vec) == 0› nil
    ‹(len vec) == 1› (vec 0)
    (and (symbol? (vec 1)) ‹‹(str (vec 1)) .charAt 0› = \.›)
      `(~(vec 1) ~(vec 0) ~@(slice vec 2))
    ‹(odd? (len vec)) and (apply = (slice vec 1 / / 2))›
      `(~(vec 1) ~@(slice vec / / 2))
    :else `(nfx ~@vec))))
;
(def sq #‹% * %›)
(defn quotrem [a b] [‹a iD b› ‹a irem b›])
(defn remquot [a b] [‹a irem b› ‹a iD b›])
(defn +'' [& vecs] ‹(every? id vecs) and (vec (apply map + vecs))›)
(defn -'' [& vecs] ‹(every? id vecs) and (vec (apply map - vecs))›)
(defn *'' [& vecs] ‹(every? id vecs) and (vec (apply map * vecs))›)
(defn D'' [& vecs] ‹(every? id vecs) and (vec (apply map / vecs))›)
(defn dist [a b] (sqrt (sum (map sq ‹a -'' b›))))
;
(print "you rock! woohoo~")