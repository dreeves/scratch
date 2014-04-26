;! todo: edit ex and derp to get rid of extra frames ?

(ns batteries)
(require '[clojure.string :as str])
(use '[clojure.walk :only (prewalk postwalk)])

(defn flip[f] #(f %2 %))
(defmacro def+[names v] `(let [~'def+-t ~v] ~@(map #(do `(def ~% ~'def+-t)) names)))
; renamings
(def & comp)
(def isa (flip instance?)) ; should be same as ∈
(def ¬ not)
(def+ (≠ !=) (& ¬ =))
(def+ (≡ is) identical?)
(def+ (≢ ¬≡ is-not) (& ¬ ≡))
(def =n ==)
(def+ (≠n !=n) (& ¬ =n))
(def ≤ <=)
(def ≥ >=)
(def+ (∈ in) (flip contains?))
(def+ (∉ not-in) (& ¬ ∈))
(def ^:macro λ @#'fn)
(def+ (π PI) Math/PI)
(def+ (τ TAU) (* π 2))
(def floor #(int (Math/floor %)))
(def ceil #(int (Math/ceil %)))
(def round #(Math/round %))
(def bit& bit-and)
(def bit| bit-or)
;(def bit^ bit-xor)
(def+ (bit! bit¬) bit-not) ; bit~
(def bit>> bit-shift-right)
(def bit<< bit-shift-left)
(def ^:dynamic ** #(Math/pow % %2))
; unicode todo
; make ≈ regexy (and maybe ≉)

; hacky renamings
(def i int)
(defn smi[v] (set (map int v)))
;(def or' #(or % %2))
;(def and' #(and % %2))

; hacky utils to work around clojure lameness
(defmacro d[& body] `(do ~@((λ _d'[body] ;! should be inside defn of d
	(if (empty? body)
		body
		(let [v (first body) body (rest body)]
			(if (∈ (first body) #{'← '<-})
				(list `(let [~v ~(second body)] ~@(_d' (rest (rest body)))))
				(cons v (_d' body)))
			))) body)))
(defn static-form[form] (d sym ← (gensym) (eval `(def ~sym ~form)) sym)) ; ISSUE: no-eval
(defmacro static[exp] (static-form exp))
(defmacro doto?[v & …] `(if (nil? ~v) nil (doto ~v ~@…)))
(defmacro break[] `(throw ~(static-form `(misc.$$Break.)))) ; wtf, why do we need static-form
(defmacro label[& body] `(try (d ~@body) (catch misc.$$Break e#)))
(defn get-set![atom v] (clojure.lang.$/atom_get_set atom v))

; other hacky utils
(defn ex[& …] (Exception. (str/join " " …)))
(defn derp[& …] (apply ex "(UNPOLISHED)" …))
(defn println'[& …] (apply println …) (last …))
(defn conj-if[c v] (if v (conj c v) c))
;(defmacro ?[name & …] `(d t# ← ~name (if t# (t# ~@…))))
(defmacro .?[name & …] `(let [t# ~name] (if t# (. t# ~@…))))
;(defmacro do-ns[name & …] `(let [t# *ns*] (ns ~name) ~@… (in-ns (ns-name t#))))
(defn sym-ns[sym] (d t ← (. sym getNamespace) (if t (symbol t))))
(defn sym-name[sym] (if (. sym getNamespace) (symbol (name sym)) sym))
;(defmacro def'[sym v] `(intern ~(sym-ns sym) ~(sym-name sym) ~v))

; slots?
(defn overload[fns] (λ ([] ((fns 0))) ([a] ((fns 1) a)) ([a b] ((fns 2) a b)) ([a b c] ((fns 3) a b c)) ([a b c & …] (apply (fns (+ (count …) 3)) a b c …))))
(defn slot[get set] (overload [get set]))
(defn aslots[a] (overload [nil (partial aget a) (partial aset a)]))
(defn =gbindf[v f](d t ← (v) (v #(d (v t) (apply f %&))))) ; assign global binding callback-style

; unpolished utils
(def dict-by group-by)
(defn dict-by'[f & …] (zipmap (map f …) …))
(defn many-to-one[& …] (apply merge (map (λ [[ks v]] (zipmap ks (repeat v))) (partition 2 …))))
(defn transpose[coll-of-colls] (apply mapv vector coll-of-colls))
(defn windows[n sq] (lazy-seq (if (empty? (drop (- n 1) sq)) '() (cons (take n sq) (windows n (rest sq))))))

; reflection utils (all unpolished)
(defn jclass[v] (if (isa v Class) v (. v getClass)))
(defn jisstatic[v] (java.lang.reflect.Modifier/isStatic (. v getModifiers)))
(defn jsupers[c] (if (nil? c) '() (cons c (lazy-seq (jsupers (. c getSuperclass))))))
; TODO change to jfields or even jmembers or even jmap
(defn jfield[c name] (try (doto? (. c getDeclaredField name) (.setAccessible true)) (catch NoSuchFieldException e)))
(defn jmethods[c name] (filter #(= name (. % getName)) (mapcat #(. % getDeclaredMethods) (reverse (jsupers c)))))
(defn jmethod1[c name] (doto? (first (jmethods c name)) (.setAccessible true)))
; picks an arbitrary member out of the methods and fields named name
(defn jdot[o name & args](d
	c ← (jclass o)
	jf ← (jfield c name)
	jm ← (jmethod1 c name)
	jargs ← (object-array (if (and (≢ o c) (jisstatic (or jf jm))) (cons o args) args))
	(if jm (. jm invoke o jargs)
	(if jf (case (count jargs)
			0 (. jf get o)
			1 (d t ← (first args) (. jf set o t) t)
			(throw (ex)))
	(throw (ex)) )) ))

; reader macros
(def lr-macros  (partial jdot clojure.lang.LispReader "macros"))
(def lr-#macros (partial jdot clojure.lang.LispReader "dispatchMacros"))
(doseq [v [lr-macros lr-#macros]] (v (java.util.Arrays/copyOf (v) (+ (apply max (smi"‹›⌊⌋⌈⌉→")) 10) (class (v)))))
(def lr-macros  (aslots (lr-macros )))
(def lr-#macros (aslots (lr-#macros)))
(defn delimited-reader [f cp] (λ[rdr cu16] (f (vec (clojure.lang.LispReader/readDelimitedList (char cp) rdr true)))))
; bind some reader macros
(defn intchars[] (lr-macros (i\\) (λ[rdr cu16] (int ((static (clojure.lang.LispReader$CharacterReader.)) rdr cu16)))))
(intchars)
(lr-#macros \\ (clojure.lang.LispReader$CharacterReader.))
(lr-macros \→ (λ[rdr cu16] (d sym ← (clojure.lang.LispReader/read rdr true nil true) (if (¬ (resolve sym)) (eval `(def ~sym nil))) sym)))
(lr-#macros \> (lr-macros \→))
(lr-macros \‹ (delimited-reader (λ[[lh op rh]] `(~op ~lh ~rh)) \›))
(lr-#macros \( (clojure.lang.$$FnReader.))
(lr-#macros \‹ (clojure.lang.$$FnReader.))
(lr-macros \› (clojure.lang.LispReader$UnmatchedDelimiterReader.))
(lr-macros \⌋ (clojure.lang.LispReader$UnmatchedDelimiterReader.))
(lr-macros \⌉ (clojure.lang.LispReader$UnmatchedDelimiterReader.))
(lr-macros \⌈ (delimited-reader (λ[[v]] `(~'ceil ~v)) \⌉))
(lr-macros \⌊ (λ[rdr cu16] (list* (clojure.lang.$/readDelimitedList_2 rdr))))
; split
(defn split-all[v sq]
	(loop [r [] sq sq] (d
		r ← (conj r (take-while #(≠ % v) sq))
		sq ← (drop-while #(≠ % v) sq)
		(if (empty? sq) r (if (empty? (rest sq)) (conj r ()) (recur r (rest sq)))))))
(defn slice-fn[sq start end step] (d
	start ← (if ‹start = 0› nil start)
	step ← (if ‹step = 1› nil step)
	start ← (if ‹start and ‹start < 0›› ‹start + (count sq)› start)
	end   ← (if ‹end   and ‹end   < 0›› ‹end   + (count sq)› end  )
	(if ‹(vector? sq) and (not step)›
		(if end
			(subvec sq start end)
			(subvec sq start))
		(d	t ← sq
			t ← (if start (drop start t) t)
			t ← (if end (take ‹end - ‹start or 0›› t) t)
			t ← (if step (take-nth step t) t)
			t))))
(defmacro slice[sq & …] `(slice-fn ~sq ~@(map first (split-all '/ …))))
(def ^:macro _ @#'slice)
; full-featured ‹›
(lr-macros \‹ (delimited-reader (λ[v]
	(cond
		‹(count v) == 0› nil
		‹(count v) == 1› (v 0)
		‹(odd? (count v)) and (apply = (_ v 1 / / 2))›
			`(~(v 1) ~@(_ v / / 2))
		:else
			`(~(v 1) ~(v 0) ~@(_ v 2))
	)) \›))
; restore the reader to original charchar state
(defn charchars[] (lr-macros \\ (clojure.lang.LispReader$CharacterReader.)))

; math
(defmacro _def-arrayfn-helper[as lim get-a get-b]
  `(let [lim# ~lim]
     (loop [r# (transient [])
            ~'i 0]
       (if ‹~'i =n lim#›
         (persistent! r#)
         (recur
           (conj! r# (~as ~get-a ~get-b))
           ‹~'i + 1›)))))
(defmacro def-arrayfn[as f]
  `(defn ~as
     ([a# b#]
       (if ‹a# and b#›
         (if (vector? a#)
           (if (vector? b#)
             (if ‹(count a#) ≠n (count b#)›
               (throw (ex "error: calling arrayfn with" a# b# "which are vecs of different lengths"))
               (_def-arrayfn-helper ~as (count a#) (a# ~'i) (b# ~'i)))
             (_def-arrayfn-helper ~as (count a#) (a# ~'i) b#))
           (if (vector? b#)
             (_def-arrayfn-helper ~as (count b#) a# (b# ~'i))
             (~f a# b#))
           )))
     ([a# b# & rest#] (apply ~as (~as a# b#) rest#))
     ))
(def-arrayfn ++ +)
(def-arrayfn -+ -)
(def-arrayfn *+ *)
(def-arrayfn D+ /)
(def-arrayfn min+ min)
(def-arrayfn max+ max)
(defn avg+[& …] ‹(apply ++ …) D+ (count …)›)

; unpolished utils, deux
(defmacro while-letd[cnd & …] (d
	(if (¬ ‹(seq? cnd) and ‹(first cnd) = '←››) (throw (derp "while-letd unfinished")))
	[n v] ← (rest cnd)
	`(loop ~[n v] (if ‹~n ≢ nil› (d ~@… (recur ~v)))) ))
(defmacro if-letd[cnd & …] (d
	bind ← (atom nil)
	cnd ← (prewalk #(d (if ‹(seq? %) and ‹(first %) = '←›› (d [_ n v] ← % (reset! bind [n v]) n) %)) cnd)
	(if (¬ @bind) (throw (derp "if-letd unfinished")))
	`(let ~(d @bind) (if ~cnd ~@…)) ))

(charchars)