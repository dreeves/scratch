;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;  THE FOLLOWING MATERIAL IS DUPLICATED ELSEWHERE  ;;;;; in sure
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; i think this version is buggy and broken
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns run (:refer-clojure :exclude [repeat]))
(require '[clojure.string :as str])
(use '[batteries :exclude [in]])

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; private ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(def timer-name (atom 0))
(def timer-pool (java.util.concurrent.Executors/newScheduledThreadPool 10
	(proxy [java.util.concurrent.ThreadFactory] [] (newThread[f] (d r ← (Thread. f (str "pool timer - "(swap! timer-name inc))) (. r setDaemon true) r)))))
(defn ex-wrap[f] (misc.$/_c_ex_wrap_ f (λ[ex] (d
	here ← (atom nil)
	(. ex setStackTrace (into-array (take-while #(d r ← (. (. % getClassName) endsWith "_c_ex_wrap_") (if r (reset! here %)) r) (vec (. ex getStackTrace)))))
	(print (str "Exception in thread \""(. (Thread/currentThread) getName)"\" "))
	(. ex printStackTrace)
	(println (str "\t at <pool> ("here")"))
	))))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; api ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(defn in    [sec f] (. timer-pool schedule               (ex-wrap f)   (long ‹sec * 1000000000›) java.util.concurrent.TimeUnit/NANOSECONDS))
(defn repeat[sec f] (. timer-pool scheduleAtFixedRate    (ex-wrap f) 0 (long ‹sec * 1000000000›) java.util.concurrent.TimeUnit/NANOSECONDS))
(defn every [sec f] (. timer-pool scheduleWithFixedDelay (ex-wrap f) 0 (long ‹sec * 1000000000›) java.util.concurrent.TimeUnit/NANOSECONDS))

(defn cancel[future] (. future cancel false))