; http://clojuredocs.org/clojure_core/clojure.core/comp
; http://clojuredocs.org/clojure_core/clojure.core/partial

(defn jmethods[c name] (filter #(= name (. % getName)) (mapcat #(. % getDeclaredMethods) (reverse (jsupers c)))))
(defn jmethods[c name] (filter #(= name (. % getName)) (apply concat (map #(. % getDeclaredMethods) (reverse (jsupers c))))))
(defn jmethods[c name] (filterv #(= name (. % getName)) (apply concat (mapv #(. % getDeclaredMethods) (reverse (jsupers c))))))
(defn jmethods[c name] (filterv #(= name (. % getName)) (apply concatv (mapv #(. % getDeclaredMethods) (reversev (jsupers c))))))
(defn jmethods[c name] (vfilterv #(= name (. % getName)) (apply vconcatv (vmapv #(. % getDeclaredMethods) (vreversev (jsupers c))))))

(defn concatv[& sqs] )
(defn reversev[sq](dl r <- (transient []) i <- (atom )))

(defn vfilterv[pred v])
(defn vconcatv[& vs])
(defn vmapv[f v])
(defn vreversev[v])

(defn filterv[pred coll]
  (-> (reduce (fn [v o] (if (pred o) (conj! v o) v))
              (transient [])
              coll)
      persistent!))
(defn mapv
  ([f coll]
     (-> (reduce (fn [v o] (conj! v (f o))) (transient []) coll)
         persistent!))
  ([f c1 c2]
     (into [] (map f c1 c2)))
  ([f c1 c2 c3]
     (into [] (map f c1 c2 c3)))
  ([f c1 c2 c3 & colls]
     (into [] (apply map f c1 c2 c3 colls))))
(defn concat
  ([] (lazy-seq nil))
  ([x] (lazy-seq x))
  ([x y]
    (lazy-seq
      (let [s (seq x)]
        (if s
          (if (chunked-seq? s)
            (chunk-cons (chunk-first s) (concat (chunk-rest s) y))
            (cons (first s) (concat (rest s) y)))
          y))))
  ([x y & zs]
     (let [cat (fn cat [xys zs]
                 (lazy-seq
                   (let [xys (seq xys)]
                     (if xys
                       (if (chunked-seq? xys)
                         (chunk-cons (chunk-first xys)
                                     (cat (chunk-rest xys) zs))
                         (cons (first xys) (cat (rest xys) zs)))
                       (when zs
                         (cat (first zs) (next zs)))))))]
       (cat (concat x y) zs))))