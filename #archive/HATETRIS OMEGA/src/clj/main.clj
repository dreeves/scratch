(ns main
  (:import hatetris.$)
  (:import hatetris.$$Grid)
  (:import hatetris.$$Well)
  (:require [clojure.string :as str])
  )

;//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// kxlq HACKY //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

(defn isa [v class] (instance? class v))
(def is identical?)
(defn i [v] (int v))
(deftype OStr [s] Object (toString [me] s)) ; ideally should be inside defn of `object`
(defn object ([] (Object.)) ([v] (OStr. (str v))))
(defn ex [& args] (Exception. (str/join " " args)))
(defn derx "derp ex" [& args] (apply ex "(UNPOLISHED)" args))
(defmacro static [exp] (let [sym (gensym)] (eval `(def ~sym ~exp)) sym)) ; ISSUE: no-eval
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
(defn dict_by [f & vs] (zipmap (map f vs) vs))
(defn many_to_one [& args] (apply merge (map (fn [[ks v]] (zipmap ks (repeat v))) (partition 2 args))))
(defn transpose [coll_of_colls] (apply mapv vector coll_of_colls))
(defn windows [n sq] (lazy-seq (if (empty? (drop n sq)) '() (cons (take n sq) (windows n (rest sq))))))
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
      (throw (derx "dot_priv fail: "obj"|"name"|"static"|"cla"|"args"|"member)))))
(defn new_priv [cla & args]
  (let [ctor (let [s (. cla getDeclaredConstructors)]
               (case (count s)
                 0 nil
                 1 (doto (first s) (.setAccessible true))
                 ))]
    (apply jcall ctor args)))
(defmacro for' [vec & body] `(let [r# (atom [])] (doseq ~vec (swap! r# conj (dl ~@body))) @r#))
(aset (dot_priv clojure.lang.LispReader "dispatchMacros") (i\>)
  (fn [rdr cu16] (let [sym (clojure.lang.LispReader/read rdr true nil true)] (eval `(declare ~sym)) sym)))
(defmacro doseq_indexed [[iname name coll] & rest]
  `(doseq [[~iname ~name] (map vector (range) ~coll)] ~@rest))
(defmacro doseq_flat [let_exprs & body] (dl
  q <- (#>transpose (partition 2 let_exprs))
  `(doseq [~(vec (q 0)) (map vector ~@(q 1))] ~@body)))
(def _BREAK (jutil.$$Break.))
(defmacro break [] `(throw _BREAK))
(defmacro label [& body] `(try (dl ~@body) (catch jutil.$$Break e#)))

;//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// S0 H4CKY, D0 N0T S4V3 //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

(defn +'' [a b] (vec (map + a b)))
(defn *'' [a b] (vec (map * a b)))

;//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// hacky utils //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

(defmacro dprint [& args] `(println "DEBUG:" ~@(map #(dl[(str %) %]) args)))
(defn color [r g b] (java.awt.Color. (float r) (float g) (float b)))
(defn min_by [f sq] (dl
  a <- (first sq)
  sq <- (rest sq)
  (if (empty? sq)
    a
    (min_by f (cons
      (dl b <- (first sq) (if (< (f a) (f b)) a b))
      (rest sq))))))
(defn mask_to_grid [strings _true] (dl
  strings <- (vec (reverse strings))
  r <- ($$Grid. (count (strings 0)) (count strings))
  (dotimes [y (. r end_y)]
    (dotimes [x (. r end_x)]
      (. r set x y (= (. (strings y) charAt x) _true))
      ))
  r))
(defn piece [rot & strings] (dl
  (assert (<= rot 4))
  r0 <- (mask_to_grid strings \X)
  r1 <- (. r0 rotate_clockwise_90)
  r2 <- (. r1 rotate_clockwise_90)
  r3 <- (. r2 rotate_clockwise_90)
  (vec (take rot (map #(. % trim) [r0 r1 r2 r3])))))

;//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// constants //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

(def PIECES (dict_by :name
  {:name :S :v (piece 4
    "X--"
    "XX-"
    "-X-")}
  {:name :Z :v (piece 4
    "-X-"
    "XX-"
    "X--")}
  {:name :I :v (piece 4
    "-X--"
    "-X--"
    "-X--"
    "-X--")}
  {:name :L :v (piece 4
    "-X-"
    "-X-"
    "-XX")}
  {:name :J :v (piece 4
    "-X-"
    "-X-"
    "XX-")}
  {:name :O :v (piece 1
    "XX"
    "XX")}
  {:name :T :v (piece 4
    "---"
    "XXX"
    "-X-")}
  ))
(def WELL_X 10)
(def WELL_Y 20)
(def BAR 4)

;//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// <edge> //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

(defn basic_well [] ($$Well. ($$Grid. WELL_X WELL_Y)))
(defn piece_in_well [well piece_type] {:type piece_type :rot 0 :x (/ (. (. well grid) end_x) 2) :y (- (. (. well grid) end_y) 5)})

(defn piece_mask [piece] (((piece :type) :v) (piece :rot)))

(defn validate_grid_and_piece [mask_a piece] (dl
  (. mask_a assert_simple)
  mask_b <- (piece_mask piece)
  is_subrange <- (fn [[a b] [c d]] (and (<= c a) (<= b d)))
  (label
    (if (not (is_subrange (+'' [(piece :x) (piece :x)] (. mask_b range_x)) (. mask_a range_x))) (break))
    (if (not (is_subrange (+'' [(piece :y) (piece :y)] (. mask_b range_y)) (. mask_a range_y))) (break))
    (doseq [row (range (. mask_b begin_y) (. mask_b end_y))]
      (if ($/bit_masks_collide (. mask_a data) (+ (piece :y) row) (. mask_b data) (- row (. mask_b begin_y)) (+ (piece :x) (. mask_b begin_x)))
        (break)))
    piece)
  ))
(defn union_grid_and_piece [mask_a piece] (dl
  mask_b <- (piece_mask piece)
  (doseq [row (range (. mask_b begin_y) (. mask_b end_y))]
    ($/bit_masks_union_mutate (. mask_a data) (+ (piece :y) row) (. mask_b data) (- row (. mask_b begin_y)) (+ (piece :x) (. mask_b begin_x)))
    )
  ))

(defn moves [well piece_type]
  []
  )
(defn well_piece_type [well]
  (or
    (. well piece_type)
    (set! (. well piece_type)
      (first (min_by second
        (for' [[_ piece_type] PIECES]
          moves <- (moves well piece_type)
          [piece_type (if (= (count moves) 0) 0 1)])))
        )))

(defn play [well] (dl
  fill_tetris_square <- (fn [g well x y] (. g fillRect (+ (* x 40) 2) (+ (* (- (. (. well grid) end_y) 1 y) 40) 2) 36 36))
  wellg <- (. well grid)
  piece <- (atom nil)
  make_piece <- #(dl (reset! piece (piece_in_well well (well_piece_type well))))
  (make_piece)
  paint <- (fn [g] (dl
    (. g setColor (color 0 0 0))
    (. g fillRect 0 0 400 800)
    (dotimes [y (. wellg end_y)]
      (dotimes [x (. wellg end_x)]
        (if (. wellg get x y) (dl
          (. g setColor (color 0 0 1))
          (fill_tetris_square g well x y)
          ))
        ))
    t <- (((@piece :type) :v) (@piece :rot))
    (doseq [y (range (. t begin_y) (. t end_y))]
      (doseq [x (range (. t begin_x) (. t end_x))]
        (if (. t get x y) (dl
          (. g setColor (color 1 0 0))
          (fill_tetris_square g well (+ x (@piece :x)) (+ y (@piece :y)))
          ))
        ))
    ))
  panel <- (proxy [javax.swing.JPanel] [] (paintComponent [g] (proxy-super paintComponent g) (paint g)))
  (. panel setPreferredSize (java.awt.Dimension. (* 40 (. wellg end_x)) (* 40 (. wellg end_y))))
  (. panel setFocusable true)
  (. panel addKeyListener
    (proxy [java.awt.event.KeyListener] []
      (keyPressed [e] (dl
        left   <- (fn [piece] (assoc piece :x (dec (piece :x))))
        right  <- (fn [piece] (assoc piece :x (inc (piece :x))))
        down   <- (fn [piece] (assoc piece :y (dec (piece :y))))
        rotate <- (fn [piece] (assoc piece :rot (mod (inc (piece :rot)) (count ((piece :type) :v)))))
        (cond
          (= (. e getKeyCode) java.awt.event.KeyEvent/VK_LEFT ) (dl t <- (validate_grid_and_piece wellg (left @piece)) (if t (reset! piece t)))
          (= (. e getKeyCode) java.awt.event.KeyEvent/VK_RIGHT) (dl t <- (validate_grid_and_piece wellg (right @piece)) (if t (reset! piece t)))
          (= (. e getKeyCode) java.awt.event.KeyEvent/VK_UP   ) (dl t <- (validate_grid_and_piece wellg (rotate @piece)) (if t (reset! piece t)))
          (= (. e getKeyCode) java.awt.event.KeyEvent/VK_DOWN ) (dl t <- (validate_grid_and_piece wellg (down @piece)) (if t (reset! piece t)
            (dl (union_grid_and_piece wellg @piece) (make_piece))))
          )
        (. panel repaint)))
      (keyReleased [e])
      (keyTyped [e])))
  frame <- (javax.swing.JFrame. "HATETRIS OMEGA")
  (. frame setDefaultCloseOperation javax.swing.JFrame/DISPOSE_ON_CLOSE)
  (. frame add panel)
  (. frame pack)
  (. frame setLocationRelativeTo nil)
  (. frame show)
  ))

(play (basic_well))