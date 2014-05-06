;;! Make bomb explosion remove obstacles
;;! Make bomb explosions uncover goodies
;;! Make bomb explosions kill the player
;;! Limit player movement rate
;;! Add status bar at the side/top whatever
;;! Change from random to specified mazes

(ns asplode
  (:use rainboom)
  (:import zutil.Box)
  (:require clojure.set)
  (:require clojure.string))
  
; jargon & minor utils
(def is identical?)
(defn alter' [box fun & args] (.set box (apply fun @box args)))
(defn +'' [a b] (vec (map + a b)))
(defn *'' [a b] (vec (map * a b)))

; so we do use a clock, but it's game-world time not outside time (i'm sorry mister earlier coder. you were smart.)

(defn read-image [path] (javax.imageio.ImageIO/read (java.io.File. path)))
(defn dims [img] [(.getWidth img nil) (.getHeight img nil)])
(def sprite-sheet (read-image "explosion.png"))
(def background (read-image "cavefloor.jpg"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Constants
;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ticks-per-second 30)
(def tick-len (/ 1.0 ticks-per-second))
(def board-x 15) (def board-y 10) ; cells
(def cell-x 32) (def cell-y 32) ; pixels
(def explosion-lifetime 0.5) ; seconds
(def base-fuse-time 1.0) ; seconds

(defrecord Movement [dir])
(defrecord LayBomb [])
(defrecord TogglePaused [])
(def key-map {java.awt.event.KeyEvent/VK_LEFT  (Movement. [-1 0])
              java.awt.event.KeyEvent/VK_DOWN  (Movement. [0 1])
              java.awt.event.KeyEvent/VK_UP    (Movement. [0 -1])
              java.awt.event.KeyEvent/VK_RIGHT (Movement. [1 0])
              java.awt.event.KeyEvent/VK_SPACE (LayBomb.)
              java.awt.event.KeyEvent/VK_PAUSE (TogglePaused.)
              java.awt.event.KeyEvent/VK_P     (TogglePaused.)})

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; some of the state
;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def clock (Box. 0)) ; milliseconds
(def clockspeed (Box. 1)) ; speed 1 is normal; 0 is paused
(def game-map (Box. {}))
(def tickables (Box. [])) ;! -> a priority queue? (sorted-set?)
(def player (Box.))
(defn setplayer [v] (.set player v) v)

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn add-to-tickables [v] (alter' tickables conj v) v)
(defn make-explosion "Creates an explosion from the specified bomb." [bomb]
;  {:type ::explosion
;   :loc (:loc bomb)
;   :
  {:type ::expanding-explosion
   :loc (:loc bomb)
   :expanding [[-1 0] [0 -1] [1 0] [0 1]]
   :strength (bomb :strength)
   :expires-in (add-to-tickables {:v (Box. explosion-lifetime) :f (Box. #())})}) ;! CURRENT: item data structure

(defn make-random-locations "Creates a random collection of locations on the grid specified by [width height]" [[width height] density exclusions]
  (vec (clojure.set/difference
        (into #{} (for [i (range 0 (* density width height))]
                    [(rand-int width) (rand-int height)]))
        exclusions)))

(defn initial-game-map "Returns a new game with a random maze of the given density" [density]
  (let [player-loc (vec (map #(int (/ % 2)) [board-x board-y]))
        wall-locs (make-random-locations [board-x board-y] density [player-loc])
        obstacle-locs (make-random-locations [board-x board-y] density (concat [player-loc] wall-locs))] 
    (group-by :loc (vec (concat [(setplayer {:type ::player :loc player-loc})]
                 (map #(do {:type ::wall :loc %}) wall-locs)
                 (map #(do {:type ::obstacle :loc %}) obstacle-locs))))))

(derive ::wall ::impassable)
(derive ::obstacle ::impassable)

(defn items-at [loc] (@game-map loc))
(defn in-bounds? "Returns true when the proposed location is on the board" [[x y]]
  (and (< x board-x)
       (> x -1)
       (< y board-y)
       (> y -1)))

(defn impassable? [loc] (or (not (in-bounds? loc)) (some #(isa? (% :type) ::impassable) (items-at loc))))

(defn expanded-explosion "Returns a new explosion in direction dir based on the specified explosion" [explosion dir]
  (if (> (explosion :strength) 0)
    (let [dest (+'' (explosion :loc) dir)]
      (if (not (impassable? dest))
        {:type ::expanding-explosion
         :loc dest
         :expanding [dir]
         :strength (- (explosion :strength) 1)
         :expires-in (:expires-in explosion)}))))

(defn explosion-expansions "Returns the expansions of a given explosion" [v] (remove nil? (map #(expanded-explosion v %) (v :expanding))))

(defmulti update-item :type)
(defmethod update-item ::expanding-explosion [v]
  (alter' game-map #(update-in % [(v :loc)] #(remove #(is % v) %)))
  (alter' game-map #(update-in % [(v :loc)] #(conj % (assoc v :type ::expanded-explosion))))
  (doseq [v (explosion-expansions v)] (alter' game-map #(update-in % [(v :loc)] #(conj % v)))))
(defmethod update-item ::expanded-explosion [v]
  (if (<= @((v :expires-in) :v) 0)
    (alter' game-map #(update-in % [(v :loc)] #(remove #(is % v) %)))))
(defmethod update-item :default [v])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Graphics stuff
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn color [r g b] (java.awt.Color. (float r) (float g) (float b)))

(defn item-bounds [item]
  {:x (* (first (item :loc)) cell-x)
   :y (* (second (item :loc)) cell-y)
   :width (dec cell-x)
   :height (dec cell-y)})

(defn bounds-center [bounds]
  {:x (+ (/ (bounds :width) 2) (bounds :x))
   :y (+ (/ (bounds :height) 2) (bounds :y))})

(defn draw-text "Draws the text text in color color at location loc" [g text color loc]
  (doto g
    (.setColor color)
    (.drawString text
                 (int (first loc))
                 (int (second loc)))))

(defn draw-centered-text "Draws text in color centered at the loc" [g text color loc]
  (let [font (.getFont g) 
        m (.getFontMetrics g font)
        h (.getHeight m)
        w (.stringWidth m text)
        cx (- (first loc) (/ w 2))
        cy (+ (second loc) (/ h 2))]
    (draw-text g text color [cx cy])))

(defn draw-item-text "Draws text text in color color bounded by the item." [g item color text]
  (let [bounds (item-bounds item)
        center (bounds-center bounds)]
    (draw-centered-text g text color [(center :x) (center :y)])))

(defmulti get-color :type)
(defmethod get-color ::wall [o] (color 1 0 0))
(defmethod get-color ::player [o] (color 0 1 0))
(defmethod get-color ::expanding-explosion [o] (color 1 0.75 0))
(defmethod get-color ::obstacle [o] (color 0 0.25 1))
(defmethod get-color :default [o] (color 0.5 0.25 0.25))

(defmulti get-text-color :type)
(defmethod get-text-color ::bomb [o] (color 1 1 1))
(defmethod get-text-color :default [o] (color 0 0 1))

(derive ::wall ::rectangular)
(derive ::obstacle ::rectangular)
(derive ::player ::rectangular)
(derive ::bomb ::round)
(derive ::expanded-explosion ::round)
(derive ::expanding-explosion ::round)

(defn paint-round-item [g v]
  (let [bounds (item-bounds v)] 
    (.fillOval 
     g
     (bounds :x)
     (bounds :y)
     (bounds :width)
     (bounds :height))))

(defmulti paint-item (fn [g item] (item :type)))
(defmethod paint-item ::rectangular [g v]
  (.setColor g (get-color v))
  (let [bounds (item-bounds v)] 
    (.fillRoundRect
      g
      (bounds :x)
      (bounds :y)
      (bounds :width)
      (bounds :height)
      (/ cell-x 5)
      (/ cell-y 5))))
(defmethod paint-item ::expanded-explosion [g v]
  (let [bnd (item-bounds v)]
    (.drawImage g sprite-sheet (bnd :x) (bnd :y) (+ (bnd :x) (bnd :width)) (+ (bnd :y) (bnd :height)) 0 32 16 48 nil)))
(defmethod paint-item ::bomb [g v]
  (.setColor g (color 0 0 0))
  (paint-round-item g v)
  (draw-item-text g v (get-text-color v) (format "%.1f" @((v :fuse) :v))))
(defmethod paint-item ::round [g v]
  (.setColor g (get-color v))
  (paint-round-item g v))
(defmethod paint-item :default [g v]
  (.setColor g (get-color v))
  (paint-round-item g v)
  (draw-item-text g v (get-text-color v) "xx"))

(defn paint "Paints the game" [g]
  (let [bg-dims (dims background)
        bg-x (bg-dims 0) ;! eww sigh; i want to be able to write `bg.x`!
        bg-y (bg-dims 1)]
    (doseq [x [0 1 2]
            y [0 1 2]]
      (.drawImage g background (* bg-x x) (* bg-y y) (* bg-x (+ x 1)) (* bg-y (+ y 1)) 0 0 bg-x bg-y nil)))
  (doseq [[key vec] @game-map] (doseq [item vec]
    (doto g (paint-item item)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Timer handlers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn handle-timer-event "Handles the firing of the timer event." [e panel]
  (alter' clock #(+ % (* @clockspeed 1000 tick-len)))
  (when (not= @clockspeed 0)
    (doseq [{:keys [v f]} @tickables] (alter' v #(- % tick-len)))
    (doseq [{:keys [v f]} @tickables] (if (<= @v 0) (@f)))
    (alter' tickables #(filter #(> @(:v %) 0) %))
    (doseq [[key vec] @game-map] (doseq [item vec] (update-item item))))
  (.repaint panel))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Swing stuff
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti handle-keypress-fn class)
(defmethod handle-keypress-fn Movement [v]
  (let [pla @player
        loc (pla :loc)
        dest (+'' loc (:dir v))
        dest (if (impassable? dest) loc dest)]
    (alter' game-map #(update-in % [loc] #(vec (remove #(is % pla) %))))
    (alter' game-map #(update-in % [dest] #(conj % (setplayer {:type ::player :loc dest}))))))
(defmethod handle-keypress-fn LayBomb [v]
  (alter' game-map #(update-in % [(:loc @player)]
    #(conj %
      ((fn [v] (.set ((v :fuse) :f) #(alter' game-map #(update-in % [(v :loc)] #(replace {v (make-explosion v)} %)))) v)
        {:type ::bomb
         :loc (:loc @player)
         :fuse (add-to-tickables {:v (Box. base-fuse-time) :f (Box.)})
         :strength 2})))))
(defmethod handle-keypress-fn TogglePaused [v] (alter' clockspeed #(- 1 %)))
(defmethod handle-keypress-fn :default [v] (println "Ignoring keypress"))

(defn handle-keypress [keycode] (handle-keypress-fn (key-map keycode)))

(defn main []
  (let [frame (javax.swing.JFrame. "Asplosion-lon-ia")
        panel (proxy [javax.swing.JPanel] []
                (getPreferredSize []
                  (java.awt.Dimension.
                    (* board-x cell-x)
                    (* board-y cell-y)))
                (paintComponent [g] (proxy-super paintComponent g) (paint g)))
        timer (new javax.swing.Timer (* 1000 tick-len) (proxy [java.awt.event.ActionListener] [] (actionPerformed [e] (handle-timer-event e panel))))]
    (.set game-map (initial-game-map 0.25))
    (.add frame panel)
    (.setFocusable panel true)
    (.addKeyListener panel (proxy [java.awt.event.KeyListener] []
                             (keyPressed [e] (dosync (handle-keypress (.getKeyCode e))) (.repaint panel))
                             (keyReleased [e])
                             (keyTyped [e])))
    (.setDefaultCloseOperation frame javax.swing.JFrame/EXIT_ON_CLOSE) ; DISPOSE_ON_CLOSE
    (.addWindowListener frame
      (proxy [java.awt.event.WindowAdapter] []
        (windowClosed [e] (println "Window closed"))
        (windowClosing [e] (println "Window closing") (.stop timer))
        (windowIconified [e] (println "Window iconified"))))
    (.pack frame)
    (.show frame)
    (.start timer)))

(main)
