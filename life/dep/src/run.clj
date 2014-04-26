(ns run (:refer-clojure :exclude [repeat]))
(require '[clojure.string :as str])
(use '[batteries :exclude [in]])

; todo:
; break.
; !! variable loop time

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; private ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(def timer-name (atom 0))
(def timer-pool (java.util.concurrent.Executors/newScheduledThreadPool 10
	(proxy [java.util.concurrent.ThreadFactory] [] (newThread[f] (d r ← (Thread. f (str "run pool "(swap! timer-name inc))) (. r setDaemon true) r)))))
(defn ex-wrap[f self] (misc.$/_c_ex_wrap_ (λ[]
	(try
		(f)
		(catch Exception e
			(try
				(→cancel @self)
				(. e setStackTrace (into-array (drop-last 2 (take-while #(¬ (. (. % getClassName) endsWith "_c_ex_wrap_")) (vec (. e getStackTrace))))))
				(binding [*out* *err*]
					(print (str "Exception in thread \""(. (Thread/currentThread) getName)"\" ")) (. *err* flush)
					(. e printStackTrace)
					(println "\tat <pool>")
					)
				(catch Exception e (. e printStackTrace)))
			))
	)))
(defn schedule[sec f j] (d
	self ← (atom nil)
	f ← (ex-wrap f self)
	r ← (j f (long ‹sec * 1000000000›) java.util.concurrent.TimeUnit/NANOSECONDS)
	(reset! self r)
	r))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; api ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(defn in    [sec f] (schedule sec f (λ[f s tu] (. timer-pool schedule               f   s tu))))
(defn repeat[sec f] (schedule sec f (λ[f s tu] (. timer-pool scheduleAtFixedRate    f 0 s tu))))
(defn every [sec f] (schedule sec f (λ[f s tu] (. timer-pool scheduleWithFixedDelay f 0 s tu))))

(defn cancel[future] (. future cancel false))