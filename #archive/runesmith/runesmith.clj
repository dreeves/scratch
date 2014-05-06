;;; intro

(ns runesmith
  (:use clojure.java.io)
  (:require [clojure.string :as str])
  (:import java.awt.image.BufferedImage))

; jargon
; fix: location, size, rotation; all such info . currently defined as [[x y] [w h]]
(defn isa [v class] (instance? class v))
(def is identical?)
(def !== #(not (== % %2)))

; (COPIED DIRECTLY FROM KXLQ, YOU MONSTER)
(deftype OStr [s] Object (toString [me] s)) ; ideally should be inside defn of `object`
(defn object ([] (Object.)) ([v] (OStr. (str v))))
;(defn ae [& args] (AssertionError. (str/join " " args)))
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
(def _BREAK (jutil.$$Break.))
(defmacro break [] `(throw _BREAK))
(defmacro label [& body] `(try (dl ~@body) (catch jutil.$$Break e#)))
(defn many_to_one [& args] (merge (map (fn [[ks v]] (zipmap ks (repeat v))) (partition 2 args))))
(defn transpose [coll-of-colls] (apply mapv vector coll-of-colls))
(defn windows [n sq] (lazy-seq (if (empty? (drop n sq)) '() (cons (take n sq) (windows n (rest sq))))))
(defmacro doseq-indexed [[iname name coll] & rest]
  `(dlseq [[~iname ~name] (map vector (range) ~coll)] ~@rest))
(defmacro doseq-flat [let-exprs & body] (dl
  q <- (transpose (partition 2 let-exprs))
  `(dlseq [~(vec (q 0)) (map vector ~@(q 1))] ~@body)))
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
(aset (dot_priv clojure.lang.LispReader "dispatchMacros") (int\>)
  (fn [rdr cu16] (let [sym (clojure.lang.LispReader/read rdr true nil true)] (eval `(declare ~sym)) sym)))

; other ?hacky? utils
(def i+ unchecked-add-int)
(def i- unchecked-subtract-int)
(def i* unchecked-multiply-int)
(def iD unchecked-divide-int)
(def irem unchecked-remainder-int)
(def i_ unchecked-negate-int)
(defn l+   [a b] (jutil.$/unchecked_l_add a b))
(defn l-   [a b] (jutil.$/unchecked_l_sub a b))
(defn l*   [a b] (jutil.$/unchecked_l_mul a b))
(defn lD   [a b] (jutil.$/unchecked_l_div a b))
(defn lrem [a b] (jutil.$/unchecked_l_rem a b))
(defn l_   [a  ] (jutil.$/unchecked_l_neg a  ))
(defn d+   [a b] (jutil.$/unchecked_d_add a b))
(defn d-   [a b] (jutil.$/unchecked_d_sub a b))
(defn d*   [a b] (jutil.$/unchecked_d_mul a b))
(defn dD   [a b] (jutil.$/unchecked_d_div a b))
(defn drem [a b] (jutil.$/unchecked_d_rem a b))
(defn d_   [a  ] (jutil.$/unchecked_d_neg a  ))
(defn f+   [a b] (jutil.$/unchecked_f_add a b))
(defn f-   [a b] (jutil.$/unchecked_f_sub a b))
(defn f*   [a b] (jutil.$/unchecked_f_mul a b))
(defn fD   [a b] (jutil.$/unchecked_f_div a b))
(defn frem [a b] (jutil.$/unchecked_f_rem a b))
(defn f_   [a  ] (jutil.$/unchecked_f_neg a  ))
(def sq #(* % %))
(def ^:dynamic ** #(Math/pow % %2)) (alter-meta! #'** dissoc :dynamic)
(defn quotrem [a b] [(iD a b) (irem a b)])
(defn remquot [a b] [(irem a b) (iD a b)])
(defn nano_time [] (System/nanoTime))
(defn sec_time [] (/ (nano_time) 1000000000.0))
(defn clamp [x min' max'] (max min' (min x max')))
(defn read_image [path] (javax.imageio.ImageIO/read (file path)))
(defn write_png [bi path] (javax.imageio.ImageIO/write bi "png" (file path)))
(defn min_val_map [m] (apply min-key #(. % val) m))
(defn dist [a b] (Math/sqrt (apply + (map sq (#>-+ a b)))))
;
(defn before      [^String v c] (let [i (. v indexOf     (int c))] (if (== i -1) v  (subs v 0 i))))
(defn before-last [^String v c] (let [i (. v lastIndexOf (int c))] (if (== i -1) v  (subs v 0 i))))
(defn  after      [^String v c] (let [i (. v indexOf     (int c))] (if (== i -1) "" (subs v (+ i 1)))))
(defn  after-last [^String v c] (let [i (. v lastIndexOf (int c))] (if (== i -1) "" (subs v (+ i 1)))))
;
(defmacro def-vecmap:helper [as len get-a get-b]
  `(let [lim# ~len]
     (loop [r# (transient [])
            ~'i 0]
       (if (== ~'i lim#)
         (persistent! r#)
         (recur
           (conj! r# (~as ~get-a ~get-b))
           (+ ~'i 1))))))
(defmacro def-vecmap [as f]
  `(defn ~as
     ([a# b#]
       (if (and a# b#)
         (if (vector? a#)
           (if (vector? b#)
             (if (!== (count a#) (count b#))
               (throw (derx "error: calling vecmap with" a# b# "which are vecs of different lengths"))
               (def-vecmap:helper ~as (count a#) (a# ~'i) (b# ~'i)))
             (def-vecmap:helper ~as (count a#) (a# ~'i) b#))
           (if (vector? b#)
             (def-vecmap:helper ~as (count b#) a# (b# ~'i))
             (~f a# b#))
           )))
     ([a# b# & rest#] (apply ~as (~as a# b#) rest#))
     ))
(def-vecmap ++ +)
(def-vecmap -+ -)
(def-vecmap *+ *)
(def-vecmap D+ /)
(def-vecmap min+ min)
(def-vecmap max+ max)
(defn avg+ [& args] (D+ (apply ++ args) (count args)))

; for repl convenience
(defn ex [] (eval '(pst *e)))

; pretend extension methods for BufferedImage
(defn bi [x y] (BufferedImage. x y BufferedImage/TYPE_INT_ARGB))
(defn bi_rgb ([^BufferedImage me x y] (. me getRGB x y))
             ([^BufferedImage me [x y]] (. me getRGB x y)))
(defn bi_rgb= [^BufferedImage me x y v] (. me setRGB x y v))
(defn bi_len [^BufferedImage me] [(. me getWidth) (. me getHeight)])
(defn bi_x [^BufferedImage me] (. me getWidth))
(defn bi_y [^BufferedImage me] (. me getHeight))
(defn bi_pixels ^ints[^BufferedImage me] (-> me (.getRaster) (.getDataBuffer) (.getData)))
(defn bi_draw_square [^BufferedImage me x y rgb radi]
  (doseq [x' (range (+ (- x radi) 1) (+ x radi))
          y' (range (+ (- y radi) 1) (+ y radi))]
    (bi_rgb= me x' y' rgb)))
(defn bi_ensure_int_array [^BufferedImage me]
  (if (isa (-> me (.getRaster) (.getDataBuffer) (.getData)) (Class/forName "[I"))
    me
    (let [r (bi (bi_x me) (bi_y me))]
      (. me getRGB 0 0 (bi_x me) (bi_y me) (bi_pixels r) 0 (bi_x me))
      r)))

; robot
(def ^java.awt.Robot _robot (java.awt.Robot.))
(defn create_screen_capture [rect] (. _robot createScreenCapture (let [[[a b] [c d]] rect] (java.awt.Rectangle. a b c d))))
(defn get_screen_pixel ([x y] (-> _robot (.getPixelColor x y) (.getRGB)))
  ([[x y]] (get_screen_pixel x y)))
(defn key_press   [keycode] (. _robot keyPress   keycode))
(defn key_release [keycode] (. _robot keyRelease keycode))
(defn mouse_move ([x y] (. _robot mouseMove x y))
  ([[x y]] (mouse_move x y)))
(def _button_conv #(case %
                    1 java.awt.event.InputEvent/BUTTON1_MASK
                    2 java.awt.event.InputEvent/BUTTON2_MASK
                    3 java.awt.event.InputEvent/BUTTON3_MASK))
(defn mouse_press   ([button] (. _robot mousePress   (_button_conv button))) ([] (mouse_press   1))) ; opt_args
(defn mouse_release ([button] (. _robot mouseRelease (_button_conv button))) ([] (mouse_release 1))) ; opt_args
(defn mouse_wheel [amount] (. _robot mouseWheel amount))
(defn mouse_loc [] (#(dl [(. % x) (. % y)]) (. (java.awt.MouseInfo/getPointerInfo) getLocation)))
(defn mouse_move_by [loc] (mouse_move (++ loc (mouse_loc))))
(defn click ([button] (mouse_press button) (mouse_release button))
  ([] (click 1)))
(defn click_at
  ([loc] (let [t (mouse_loc)] (mouse_move loc) (click) (#>run_in' 15 #(mouse_move t))))
  ([panel loc] (click_at (++ ((. panel fix) 0) loc))))
(defn drag_mouse [from to] (if (not= from to) (let [t (mouse_loc)] (mouse_move from) (mouse_press) (mouse_move to) (mouse_release) (mouse_move t))))
(defn key_tap ([key ms] (key_press key) (#>run_in ms #(key_release key)))
  ([key] (key_tap key 15)))

;;; custom datastructures

; define Panel type
(deftype Panel [fix
                ^BufferedImage img ; backing
                offset]
  clojure.lang.IFn clojure.lang.Seqable
  (invoke [me [x y]] (. me invoke x y))
  (invoke [me x y] (bi_rgb img (+ x (offset 0)) (+ y (offset 1))))
  (applyTo [me args] (clojure.lang.AFn/applyToHelper me args))
  (seq [me] (seq (bi_pixels (. (#>p_reback me) img))))
  (toString [me] (#>write_std me "debug") "Panel")
  )
(defn panel [img screen_loc] (runesmith.Panel. [screen_loc (bi_len img)] img [0 0]))
(defn nil_panel [size] (runesmith.Panel. [[0 0] size] nil [0 0]))
(defn p_sub
  ([me offset'] (p_sub me offset' (-+ ((. me fix) 1) offset')))
  ([me offset' size'] (runesmith.Panel. [(++ ((. me fix) 0) offset') size'] (. me img) (++ (. me offset) offset')))
  )
(defn p_reback [me] (if (= ((. me fix) 1) (bi_len (. me img))) me (panel
  (let [[sx sy] ((. me fix) 1)
        r (bi sx sy)]
    (dotimes [y sy] (dotimes [x sx] (. r setRGB x y (me x y))))
    ;(dotimes [y sy] (. bi setRGB 0 y sx 1 (. (. me img) pixels) (+ (* oy stride) ox) stride)) ;~ aaaargh this line borkly :(
    r)
  ((. me fix) 0))))
(defn p_refetch
  ([me] (panel (#>create_screen_capture (. me fix)) ((. me fix) 0)))
  ([me panel] (#>p_refetch_at me (-+ ((. me fix) 0) ((. panel fix) 0))))
  )
(defn p_refetch_at [me panel loc] (p_reback (p_sub panel loc ((. me fix) 1))))
(defn p_center [me] (D+ ((. me fix) 1) [2 2]))

; define Pattern type
(deftype Pattern [^BufferedImage img
                  ret_loc
                  check_first
                  check_first_rgb
                  fuzzy
                  okay_errors
                  is_text]) ; only works for matches... sigh...
;! better pixel-checking-order algo? (maybe: check three; check more if no good)
(defn pattern [^BufferedImage img {:keys [ret_loc fuzzy check_first okayerr is_text]}] (dl
  check_first <- (or check_first
    (dl v <- ((min_val_map (frequencies (bi_pixels img))) 0) ;! seriously needs to be a better way to find minimums and locations than this
      (if (== (bit-and v 0xffffff) 0xff00ff) (throw (derx)))
      (loop [x 0 y 0]
        (cond
          (>= y (bi_y img)) (recur (+ x 1) 0)
          (== v (bi_rgb img x y)) [x y]
          :else (recur x (+ y 1))))))
  (Pattern.
    img
    (vec (map int (*+ (or ret_loc [0.5 0.5]) (bi_len img))))
    check_first
    (bi_rgb img check_first)
    (int (* (or fuzzy 2.6) 256))
    (if-not okayerr 0 (int (if (>= okayerr 1) okayerr (* okayerr (count (filter #(!== (bit-and % 0xffffff) 0xff00ff) (bi_pixels img)))))))
    is_text)))
(defn x_color= [^long misc3 ^long a ^long b] (< (#>rgb_dist a b) misc3))
(defn x_matches_now [me panel] (#>x_matches me (-> panel (p_sub [0 0] (bi_len (. me img))) (p_refetch me))))
(defn x_matches [me panel'] (dl
  [misc0 misc1] <- (. me check_first)
  misc2 <- (. me check_first_rgb)
  misc3 <- (. me fuzzy)
  ;(#>write_std' (. me img) (. panel' img))
  (let [^Panel panel panel']
    (if (x_color= misc3 misc2 (panel misc0 misc1))
      (if (. me is_text) ; sigh; code dup; but i feel like this is best since this stuff sucks
        (throw (derx))
        ;(loop [errors 0
        ;       x 2
        ;       y 0]
        ;  (if'
        ;    (>= y (bi_y (. me img))) true
        ;    (>= x (- (bi_x (. me img)) 2)) (recur errors 2 (+ y 1))
        ;    ( errors > (. me okay_errors)) nil
        ;    (not (or
        ;        (== (bit-and (bi_rgb (. me img) x y) 0xffffff) 0xff00ff)
        ;        (x_color= misc3 (bi_rgb (. me img) x y) (panel (- x 2) y))
        ;        (x_color= misc3 (bi_rgb (. me img) x y) (panel (- x 1) y))
        ;        (x_color= misc3 (bi_rgb (. me img) x y) (panel  x      y))
        ;        (x_color= misc3 (bi_rgb (. me img) x y) (panel (+ x 1) y))
        ;        (x_color= misc3 (bi_rgb (. me img) x y) (panel (+ x 2) y))))
        ;      (recur (+ errors 1) (+ x 1) y)
        ;    :else (recur errors (+ x 1) y)))
        (loop [errors 0
               x 0
               y 0]
          (cond
            (>= y (bi_y (. me img))) true
            (>= x (bi_x (. me img))) (recur errors 0 (+ y 1))
            (> errors (. me okay_errors)) nil
            (and (!== (bit-and (bi_rgb (. me img) x y) 0xffffff) 0xff00ff) (not (x_color= misc3 (bi_rgb (. me img) x y) (panel x y))))
              (recur (+ errors 1) (+ x 1) y)
            :else (recur errors (+ x 1) y)))
        )))))
(defn x_find_in' [me panel last_at] (dl
  [misc0 misc1] <- (. me check_first)
  misc2 <- (. me check_first_rgb)
  misc3 <- (. me fuzzy)
  ;(#>write_std' (. me img) (. panel img)
  bound_x <- (+ (- (((. panel fix) 1) 0) (bi_x (. me img))) 1)
  bound_y <- (+ (- (((. panel fix) 1) 1) (bi_y (. me img))) 1)
  (loop [x (+ (last_at 0) 1)
         y (last_at 1)]
    (cond
      (>= y bound_y) nil
      (>= x bound_x) (recur 0 (+ y 1))
      :else
        (or
          (if (x_color= misc3 misc2 (panel (+ x misc0) (+ y misc1)))
            (loop [errors 0
                   x' 0
                   y' 0]
              (cond
                (>= y' (bi_y (. me img))) [x y]
                (>= x' (bi_x (. me img))) (recur errors 0 (+ y' 1))
                (> errors (. me okay_errors)) nil
                (and (!== (bit-and (bi_rgb (. me img) x' y') 0xffffff) 0xff00ff) (not (x_color= misc3 (bi_rgb (. me img) x' y') (panel (+ x x') (+ y y')))))
                  (recur (+ errors 1) (+ x' 1) y')
                :else (recur errors (+ x' 1) y'))))
          (recur (+ x 1) y))))))
(defn x_find_in [me big_img] (++ (x_find_in' me big_img [-1 0]) (. me ret_loc)))
(defn x_find_all_in [me big_img]
  (loop [r []
         last_at [-1 0]]
    (if-let [pt (x_find_in' me big_img last_at)]
      (recur (conj r (++ pt (. me ret_loc))) pt)
      r)))

;;; runesmith data

(def rs_default (nil_panel [765 503]))
(def screen_size [1366 768])

; generate patterns from ./patterns
(doseq [f (. (file "patterns") listFiles)] (dl
  ;! Wrapping a macro in a function, like say (fn [& args] (eval `(def ~@args))): is there any clean way to avoid the eval?
  ;! any better way than just prefixing with ik?
  [name tokens] <- (str/split (str/replace (before-last (. f getName) \.) ";" ":") #"\s+" 2)
  kwargs <- (load-string (str "{"tokens"}"))
  (if (not= (. name charAt 0) \#)
    (eval `(def ~(symbol (str "ik_"name)) (pattern (bi_ensure_int_array (read_image ~f)) ~kwargs))))))

; mutables
(def is_paused (atom false))
(def rs_visible (atom nil))
(def ^Panel rs (atom rs_default))
(def ^Panel radar (atom nil))
(def ^String world (atom nil))
(def flaggy (atom nil))
(def ticks (atom 0))

;;; runesmith functions

(defn nw [vec] [0 0])
(defn ne [[a b]] [a 0])
(defn sw [[a b]] [0 b])
(defn se [vec] vec)
(defn UI_NW [] (nw ((. @rs fix) 1)))
(defn UI_NE [] (ne ((. @rs fix) 1)))
(defn UI_SW [] (sw ((. @rs fix) 1)))
(defn UI_SE [] (se ((. @rs fix) 1)))

(def _timer (java.util.Timer. true))
(defn _mk_task [f] (proxy [java.util.TimerTask] [] (run [] (f))))
(defn run_in           [delay  task] (. _timer schedule            (_mk_task task) (long delay)))
(defn run_repeat       [period task] (. _timer schedule            (_mk_task task) 0 (long period)))
(defn run_repeat_fixed [period task] (. _timer scheduleAtFixedRate (_mk_task task) 0 (long period)))
(let [t (java.util.Timer. true) ;! remove, sigh...
      mk_task #(proxy [java.util.TimerTask] [] (run [] (%)))]
  (defn run_in'           [delay  task] (. t schedule            (mk_task task) (long delay)))
  (defn run_repeat'       [period task] (. t schedule            (mk_task task) 0 (long period)))
  (defn run_repeat_fixed' [period task] (. t scheduleAtFixedRate (mk_task task) 0 (long period))))
(defn sleep [ms] (if (not= ms 0) (Thread/sleep ms)))
(defn approx ; opt-args
  ([x] (approx x nil nil))
  ([x min max]
    (+ x
      (clamp
        (* (. (static (dl (Math/random) (dot_priv Math "randomNumberGenerator"))) nextGaussian) x 0.1)
        (or min (* (Math/abs x) -0.25))
        (or max (* (Math/abs x) 0.25))))))
;(doto (Thread. #(loki "{loop sleepQuietly(Long.MAX_VALUE)}") "Thread-InterruptPeriodSetter") (.setDaemon true) (.start))
(defn past [time] (> (sec_time) time))
(defn write_log [imgoid name] (write_png (if (isa imgoid BufferedImage) imgoid (. (p_reback imgoid) img)) (str "log/"name".png")))
(defn write_std [imgoid name] (dl
  secs <- (lD (- (nano_time) #>start_time) 1000000000)
  (write_log imgoid (str (iD secs 3600)"_"(mod (iD secs 60) 60)"_"(mod secs 60)" "name))))
(defn write_std' [& imgs] (dl r <- (rand-nth (range 100 1000)) (dotimes [i (count imgs)] (write_std (nth imgs i) (str "debug"r"-"i)))))
(defn rgb_dist [^long a ^long b] (dl
  ar <- (bit-and (bit-shift-right a 16) 0xff)
  ag <- (bit-and (bit-shift-right a  8) 0xff)
  ab <- (bit-and                  a     0xff)
  br <- (bit-and (bit-shift-right b 16) 0xff)
  bg <- (bit-and (bit-shift-right b  8) 0xff)
  bb <- (bit-and                  b     0xff)
  r <- (- ar br)
  g <- (- ag bg)
  b <- (- ab bb)
  (+ (* r r) (* g g) (* b b))))

; image-searching functions
(defn msg_is_last [v] (x_matches v (p_sub @rs [9 444])))
(defn inv_slot [idx] (p_sub @rs (++ [557 210] (*+ (remquot idx 4) [42 36])) [42 36]))

;;; main

(def start_time (nano_time))

(run_repeat_fixed 5000 #(if @rs_visible (write_std @rs "rs-scr")))
;(zutil.KeyCallback/on_press \P #(swap! is_paused not))

(defn main []
  (swap! ticks inc)
  (label
    (if @rs_visible (swap! rs p_refetch))
    (if-not (and @rs_visible (x_matches ik_rs_anchor (p_sub @rs (-+ (UI_NE) [(bi_x (. ik_rs_anchor img)) 0]) (bi_x (. ik_rs_anchor img))))) (dl
      scr <- (p_refetch (nil_panel screen_size))
      (if (reset! rs_visible (x_find_in ik_rs_anchor scr))
        (dl
          (swap! rs p_refetch_at scr (-+ @rs_visible (UI_NE)))
          ;! current: i wish i could just move rs to the new location and then let the refetching be handled by the earlier check?
          (println "found rs loc:" (. @rs fix)))
        (reset! rs rs_default))))
    (if @rs_visible (dl
      (reset! world (p_sub @rs [4 4] [512 334]))
      (reset! radar (p_refetch (p_sub @rs [551 9] [152 152])))
      (reset! flaggy (x_find_in ik_radar_flag @radar))))
    (if @rs_visible (dl
      (if (compare-and-set! (static (atom nil)) nil true) (dl ; reposition the window just once
        (drag_mouse (++ ((. @rs fix) 0) [319 -76]) (++ (-+ screen_size (UI_SE)) [319 -76]))
        (break)))
      t <- ((fn [[x y]] [x (- y)]) (-+ (mouse_loc) (++ ((. @radar fix) 0) (p_center @radar)))) ;! clean up use of fix
      (if (every? #(<= -80 % 80) t) (println "mouse-loc" t))
      (if (< (rand) 0.01) (dl
        (click_at @rs [543 24])
        (key_tap java.awt.event.KeyEvent/VK_UP (approx 1000 950 nil))))
  ;    (doseq [[x y] (find_all_in ik_tree @radar)]
  ;      (. (. @radar img) draw_square x y 0x00ffff 2))
  ;    (write @radar (str "tree"@ticks))
  ;    (refresh)
      (if (and (not @is_paused) (seq @#>tasks)) (dl
        (loop [r ((first @tasks))]
   ;`       (println "loop!" r (first @tasks))
          (cond
            (= r :done) (swap! tasks #(rest %))
            (fn? r) (recur [r])
            (vector? r) (dl (swap! tasks #(vec (concat r (rest %)))) (recur ((first r))))
            :else nil
            )))))))
  (run_in (approx 400) main))

(defn mkt_go_to_icon [pattern name]
  #(let [loc (x_find_in pattern @radar)]
    (if-not loc
      (print "help! I need to go to" name "icon")
      (if (>= (dist loc [76 76]) 15)
        (click_at @radar loc)
        (dl
          (print "got to" name "icon")
          :done)))))
(defn mkt_go_by [[x y]]
  (fn []
    (print "clicking on radar at" [x y])
    (click_at @radar (++ (p_center @radar) [x (- y)])) ; safe limit: (dist [0 0] [x y]) ** 2 < 4500
    (sleep 500) ;! bad :(
    (fn [] (if @flaggy #(if-not @flaggy :done)))))
(defn mkt_click_wait_for_ore [loc mining_success_ore]
  (fn [] (let [lim (+ (sec_time) (approx 3))]
    (click_at @world (++ (p_center @world) loc))
    (fn [] (cond
      (past lim) :done
      (msg_is_last ik_swing_your_pick)
        #(if (or (past lim) (msg_is_last mining_success_ore)) :done)
      (msg_is_last ik_no_ore_available)
        (dl (sleep (approx 1000)) :done)
      )))))

(def task_falador_mine_iron
  ((fn me [tasks']
    #(if (x_matches ik_empty_inv_slot (inv_slot 27))
      (dl
        (print "inventory: not-full")
        [(first tasks') (me (rest tasks'))])
      (dl (print "inventory: probably-full") :done)))
    (cycle [
      (mkt_click_wait_for_ore [40 0] ik_mining_success_iron)
      (mkt_click_wait_for_ore [0 40] ik_mining_success_iron)
      ])))
(def tasks (atom [
    (mkt_go_by [-42 -59])
    (mkt_go_by [-8 -70])
    (mkt_go_by [0 -70])
    (mkt_go_by [0 -70])
    (mkt_go_by [0 -70])
    (mkt_go_by [-50 -50])
    (mkt_go_by [-50 -50])
    (mkt_go_by [0 -70])
    :start
    ;` #(dl (println "um hi...") :done)
    ;` (mkt_go_by [0 10])
    task_falador_mine_iron ;! why is this not doing this after the go_bys finish? (note: ;`)
    (mkt_go_to_icon ik_icon_mine "mine")
    (mkt_go_by [0 65])
    (mkt_go_by [48 48])
    (mkt_go_by [48 48])
    (mkt_go_by [0 50])
    (mkt_go_to_icon ik_icon_clan "clan")
    (mkt_go_by [10 50])
    (mkt_go_to_icon ik_icon_tree "tree")
    (mkt_go_by [20 60])
    (mkt_go_by [18 60])
    (mkt_go_to_icon ik_icon_bank "bank")
  ]))
(swap! tasks (fn [v] (vec (rest (drop-while #(not= % :start) v)))))
nil