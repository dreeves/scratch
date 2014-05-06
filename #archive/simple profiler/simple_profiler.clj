; next things to do:
; reduce reliance on globals; enable profiling of multiple threads
; refactor from busy-loop to a proper timer or such

; simple test: (def q (atom [])) (def example (Thread. #(loop [i 0] (swap! q conj i) (if (> (count @q) 1000000) (reset! q [])) (recur (inc i))))) (. example start)

(ns simple_profiler
  (:require [clojure.string :as str]))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; copied directly from kxlq.clj ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;
; somewhat hacky utils that make clojure less icky to use

  (defn isa [v class] (instance? class v))
  (defn _dl_helper [body] ;! not inside defn of dl
    (if (empty? body)
      body
      (let [v (first body)
            body (rest body)]
        (cond
          (= (first body) '<-)
            (list
              `(let [~v ~(second body)]
                 ~@(_dl_helper (rest (rest body)))))
          (= v 'if<-)
            (list
              `(let [~(first body) (if ~(second body) ~(nth body 2) ~(first body))]
                 ~@(_dl_helper (rest (rest (rest body))))))
          :else
            (cons v (_dl_helper body)))
        )))
  (defmacro dl [& body] `(do ~@(_dl_helper body)))
  (defn jcall [member & args]
    (let [static (or (isa member java.lang.reflect.Constructor) (not (== (bit-and (. member getModifiers) java.lang.reflect.Modifier/STATIC) 0)))
          obj  (if static nil (first args))
          args (if static args (rest args))]
      (condp instance? member
        java.lang.reflect.Field
          (case (count args)
            0 (. member get obj)
            1 (let [v (first args)] (. member set obj v) v)
            )
        java.lang.reflect.Method
          (. member invoke obj (object-array args))
        java.lang.reflect.Constructor
          (. member newInstance (object-array args))
        )))
  (defn dot_priv [obj name & args]
    (let [static (isa obj Class)
          cla (if static obj (class obj))
          args (if static args (cons obj args))
          field (try (doto (. cla getDeclaredField name) (.setAccessible true)) (catch NoSuchFieldException e))
          method (let [s (filter #(= (. % getName) name) (. cla getDeclaredMethods))]
                   (case (count s)
                     0 nil
                     1 (doto (first s) (.setAccessible true))
                     ))
          member (or field method)]
      (if member
        (apply jcall member args)
        (throw (Exception. (str"dot_priv fail: "obj"|"name"|"static"|"cla"|"args"|"member))))))

  ; #>sym declares the sym so it's not an error if it hasn't been declared yet! such a hacky reader macro.
  (aset (dot_priv clojure.lang.LispReader "dispatchMacros") (int\>)
    (fn [rdr cu16] (let [sym (clojure.lang.LispReader/read rdr true nil true)] (eval `(declare ~sym)) sym)))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; api ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

  (defn profile[thread] (dl
    t <- (register-thread thread)
    (reset! (t :prof-thread) (Thread. #(dl
      time_a <- (#>stime)
      (while (not @(t :do-quit)) (dl
        time_b <- (- (stime) time_a)
        time_r <- (* time_b 2)
        (while (<= (count @(t :distribution)) (- time_r 1))
          (swap! (t :distribution) conj 0))
        (if (<= (count @(t :distribution)) time_r)
          (swap! (t :distribution) conj 1)
          (swap! (t :distribution) assoc (- (count @(t :distribution)) 1) (+ (last @(t :distribution)) 1))) ; t.distribution[-1] += 1
          )
        (doseq [v (take 10 (seq (. thread getStackTrace)))] (swap! (t :results) assoc v (inc (or (@(t :results) v) 0))))
        ))
      ))
    (. @(t :prof-thread) setName "simple-profiler")
    (. @(t :prof-thread) setDaemon true)
    (. @(t :prof-thread) start)
    ))
  (defn finish[thread] (dl
    t <- (register-thread thread)
    (swap! all-threads dissoc thread)

    (reset! (t :do-quit) true)
    (. @(t :prof-thread) join)

    max <- (second (apply max-key second @(t :results)))
    r <- (filter #(> (second %) (/ max 10)) @(t :results))
    r <- (sort-by second r)

    (str/join "\n" (concat
      (map #(str (second %)": "(#>pretty-frame (first %))) r)
      [""]
      (map-indexed #(str "time "%": "%2) @(t :distribution))
      [""]))
    ))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; state ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

  (def all-threads (atom {})) ; :: {thread: {:prof-thread, :do-quit, :distribution, :results}}

  ;(def prof-thread (atom nil)) ; thread used to do the profiling
  ;(def do-quit (atom false)) ; set true when you call (finish)
  ;(def distribution (atom [])) ; one kind of results
  ;(def results (atom {})) ; other kind

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; misc ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

  (defn register-thread[thread] (dl
    r <- (@all-threads thread)
    (if r
      r
      ((swap! all-threads assoc thread
         {:prof-thread (atom nil)
          :do-quit (atom false)
          :distribution (atom [])
          :results (atom {})}) thread)
      )))

  (defn stime[] (/ (System/nanoTime) 1000000000.0))
  (defn pretty-frame[frame] (dl
    names <- (str/split (. frame getClassName) #"\.")
    (str (str/join (map #(. (subs % 0 1) toUpperCase) (butlast names)))"."(last names)"."(. frame getMethodName)":"(. frame getLineNumber))
    ))