(ns runesmith
  (:refer-clojure :exclude [print]) ;! bwah why do i have to repeat this... i think i can fix it by importing everything through rainboom? idk...
  (:use rainboom)
  (:import java.awt.image.BufferedImage)
  (:import java.awt.Rectangle)
  (:import zutil.Box) ;! replace Box with clojure deftype | (defn alter' [box fun & args] (.set box (apply fun @box args)))
  (:import zutil.Jump))
;! so you should replace this clipboard thing with an actual UI. Curses anyone?
;!) why are we passing rects around?
;
(defn bi-g          ([^BufferedImage me x y] ‹me .getRGB x y›)
                    ([^BufferedImage me [x y]] ‹me .getRGB x y›))
(defn bi-s           [^BufferedImage me x y v] ‹me .setRGB x y v›)
(defn bi-size        [^BufferedImage me] [‹me .getWidth› ‹me .getHeight›])
(defn bi-x           [^BufferedImage me] ‹me .getWidth›)
(defn bi-y           [^BufferedImage me] ‹me .getHeight›)
(defn bi-pixels ^ints[^BufferedImage me] (-> me (.getRaster) (.getDataBuffer) (.getData)))
(defn bi-seq         [^BufferedImage me] (seq (bi-pixels me)))
(defn bi-draw-square [^BufferedImage me x y rgb radi]
  (doseq [x' (range ‹‹x - radi› + 1› ‹x + radi›)
          y' (range ‹‹y - radi› + 1› ‹y + radi›)]
    (bi-s me x' y' rgb)))
(defn bi-ensure-:D   [^BufferedImage me]
  (if (instance? (Class/forName "[I") (-> me (.getRaster) (.getDataBuffer) (.getData)))
    me
    (let [r (BufferedImage. (bi-x me) (bi-y me) BufferedImage/TYPE_INT_ARGB)]
      ‹me .getRGB 0 0 (bi-x me) (bi-y me) (bi-pixels r) 0 (bi-x me)›
      r)))
;
(defprotocol Panel-P ;! whyyy do i need a protocol? :(
  (sub [this offset] [this offset dims'])
  (reback [this])
  (refetch [this])
  (write [this path])
  (write-std [this name]))
(deftype Panel [^BufferedImage img ; backing
                size
                offset
                screen-loc]
  Panel-P clojure.lang.IFn clojure.lang.Seqable
  (invoke [this [x y]] (.invoke this x y))
  (invoke [this x y] (bi-g img ‹x + (offset 0)› ‹y + (offset 1)›))
  (applyTo [this args] (clojure.lang.AFn/applyToHelper this args))
  (seq [this] (bi-seq ‹‹this .reback› .img›))
  
  (sub [this offset'] (.sub this offset' ‹size -'' offset'›))
  (sub [this offset' size'] (Panel. img size' ‹offset +'' offset'› ‹screen-loc +'' offset'›))
  (reback [this] (if ‹size = (bi-size img)› this (Panel.
    (let [[sx sy] (bi-size img)
          r (BufferedImage. sx sy BufferedImage/TYPE_INT_ARGB)]
      (dotimes [y sy] (dotimes [x sx] ‹r .setRGB x y (this x y)›))
      ;(dotimes [y sy] ‹bi .setRGB 0 y sx 1 ‹img .pixels› ‹‹oy * stride› + ox› stride›) ;~ aaaargh this line borkly :(
      r)
    size [0 0] screen-loc)))
  (refetch [this] (Panel. (#>get-screen (#>mk-rect screen-loc size)) size [0 0] screen-loc))
  (write [this name] (write-png ‹‹this .reback› .img› (str "pics/"name".png")))
  (write-std [this name]
    (let [secs ‹‹(nano-time) - #>start-time› lD 1000000000›]
      (.write this (str ‹secs iD 3600›"_"‹‹secs iD 60› mod 60›"_"‹secs mod 60›" "name)))))
(defn mk-panel [img screen-loc] (Panel. img (bi-size img) [0 0] screen-loc))
;
(defprotocol Pattern-P
  (matches [this panel])
  (find-in' [this panel last-at])
  (find-in [this panel])
  (find-all-in [this panel])
  (color= [this a b]))
(deftype Pattern [^BufferedImage img
                  ret-loc
                  ^ints misc ; [check-first-x check-first-y check-first-rgb fuzzy]
                  okay-errors]
  Pattern-P
  (matches [this panel'] ;! code dup, sigh
    ;(#>panels-write-std img ‹panel' .img›)
    (let [^Panel panel panel']
      (if (.color= this (aget misc 2) (panel (aget misc 0) (aget misc 1)))
        (loop [errors 0
               x 0
               y 0]
          (cond ;! better pixel-checking-order algo? (maybe: check three; check more if no good)
            ‹y >= (bi-y img)› true
            ‹x >= (bi-x img)› (recur errors 0 ‹y + 1›)
            ‹errors > okay-errors› nil
            ‹‹‹(bi-g img x y) bit& 0xf0f0f0› !== 0xf000f0› and (not (.color= this (bi-g img x y) (panel x y)))›
              (recur ‹errors + 1› ‹x + 1› y)
            :else (recur errors ‹x + 1› y))))))
  (find-in' [this panel' last-at]
    ;(#>panels-write-std img ‹panel' .img)
    (let [^Panel panel panel'
          bound-x ‹‹((.size panel) 0) - (bi-x img)› + 1›
          bound-y ‹‹((.size panel) 1) - (bi-y img)› + 1›]
      (loop [x ‹(last-at 0) + 1›
             y (last-at 1)]
        (cond
          ‹y >= bound-y› nil
          ‹x >= bound-x› (recur 0 ‹y + 1›)
          :else
            (or
              (if (.color= this (aget misc 2) (panel ‹x + (aget misc 0)› ‹y + (aget misc 1)›))
                (loop [errors 0
                       x' 0
                       y' 0]
                  (cond ;! better pixel-checking-order algo? (maybe: check three; check more if no good)
                    ‹y' >= (bi-y img)› [x y]
                    ‹x' >= (bi-x img)› (recur errors 0 ‹y' + 1›)
                    ‹errors > okay-errors› nil
                    ‹‹‹(bi-g img x' y') bit& 0xf0f0f0› !== 0xf000f0› and (not (.color= this (bi-g img x' y') (panel ‹x + x'› ‹y + y'›)))›
                      (recur ‹errors + 1› ‹x' + 1› y')
                    :else (recur errors ‹x' + 1› y'))))
              (recur ‹x + 1› y))))))
  (find-in [this big-img] ‹(find-in' this big-img [-1 0]) +'' ret-loc›)
  (find-all-in [this big-img]
    (loop [r []
           last-at [-1 0]]
      (if-let [pt (find-in' this big-img last-at)]
        (recur (conj r ‹pt +'' ret-loc›) pt)
        r)))
  (color= [this a b] ‹(#>rgb-dist a b) < (aget misc 3)›))
(defn mk-pattern [^BufferedImage img {:keys [ret-loc fuzzy check-first okayerr]}]
  (Pattern.
    img
    (vec (map int ‹‹ret-loc or [0.5 0.5]› *'' (bi-size img)›))
    (let [fuzzy' (int ‹‹fuzzy or 2.6› * 256›)]
      (if check-first
        (int-array (conj check-first (bi-g img check-first) fuzzy'))
        (let [v ((min-val-map (frequencies (bi-seq img))) 0) ;! seriously needs to be a better way to find minimums and locations than this
              [x y] (loop [x 0 y 0]
                      (cond
                        ‹y >= (bi-y img)› (recur ‹x + 1› 0)
                        ‹v == (bi-g img x y)› [x y]
                        :else (recur x ‹y + 1›)))]
          (int-array [x y v fuzzy']))))
    (if-not okayerr 0 (int (if ‹okayerr >= 1› okayerr ‹okayerr * (len (filter #‹‹% bit& 0xf0f0f0› !== 0xf000f0› (bi-seq img)))›)))))
;
(defn nw [vec] [0 0])
(defn ne [[a b]] [a 0])
(defn sw [[a b]] [0 b])
(defn se [vec] vec)
(defn center [panel] ‹‹panel .size› D'' [2 2]›)
;
(def ^Rectangle rs-loc nil)
(def ^Panel curr-screen nil)
(def ^Panel radar nil)
(def flaggy)
(def ticks 0)
(def myself [76 76])
(def rs-size [765 503])
(def ui-NW (nw rs-size))
(def ui-NE (ne rs-size))
(def ui-SW (sw rs-size))
(def ui-SE (se rs-size))
(def radar-size [152 152])
(def radar-loc ‹‹ui-NE +'' [-62 9]› -'' (ne radar-size)›)
;
(def JUMP (Jump.))
(defmacro label [& body] `(try (do ~@body) (catch Jump e#)))
;
(let [timer (java.util.Timer. true) ; has def
      mk-task #(proxy [java.util.TimerTask] [] (run [] (%)))]
  (defn run-in           [delay  task] ‹timer .schedule            (mk-task task) (long delay)›) ;! wish auto-coercion when calling methods (double->long)
  (defn run-repeat       [period task] ‹timer .schedule            (mk-task task) 0 (long period)›)
  (defn run-repeat-fixed [period task] ‹timer .scheduleAtFixedRate (mk-task task) 0 (long period)›))
(def VK-RIGHT java.awt.event.KeyEvent/VK_RIGHT) ;! static import plox?
(def VK-LEFT java.awt.event.KeyEvent/VK_LEFT)
(def VK-UP java.awt.event.KeyEvent/VK_UP)
(def VK-DOWN java.awt.event.KeyEvent/VK_DOWN)
(defn mk-rect
  ([x y width height] (Rectangle. x y width height))
  ([[x y] [width height]] (mk-rect x y width height)))
(defn mk-square [x y radius] (mk-rect ‹x - radius› ‹y - radius› ‹‹radius * 2› + 1› ‹‹radius * 2› + 1›))
(let [clipboard (.getSystemClipboard (java.awt.Toolkit/getDefaultToolkit))] ; has def
  (defn clipboard-text []
    (try (str (-> (.getContents clipboard nil) (.getTransferData java.awt.datatransfer.DataFlavor/stringFlavor)))
      (catch java.awt.datatransfer.UnsupportedFlavorException e nil)))
  (defn set-clipboard-text [s] (let [t (java.awt.datatransfer.StringSelection. s)] ‹clipboard .setContents t t›)))
(defn sleep [ms] (if ‹ms != 0› (Thread/sleep ms)))
(let [_ (Math/random) ; has def
      rand (j-get (j-field Math "randomNumberGenerator"))]
  (defn approx
    ([x] (approx x nil nil)) ;! optional args :(
    ([x min max] ‹x + (clamp ‹‹rand .nextGaussian› * x * 0.1› ‹min or ‹(abs x) * -0.25›› ‹max or ‹(abs x) * 0.25››)›)))
;(doto (Thread. #(loki "{loop sleepQuietly(Long.MAX_VALUE)}") "Thread-InterruptPeriodSetter") (.setDaemon true) (.start))
(defn to-point [^Rectangle v] [(.x v) (.y v)])
;
(defn panels-write-std [& imgs] (let [r (rand-nth (range 100 1000))] (dotimes [i (len imgs)] (.write-std (nth imgs i) (str "debug"r"-"i)))))
(defn rgb-dist [a b]
  (let [ar ‹(bit>> a 16) bit& 0xff›
        ag ‹(bit>> a  8) bit& 0xff›
        ab ‹       a     bit& 0xff›
        br ‹(bit>> b 16) bit& 0xff›
        bg ‹(bit>> b  8) bit& 0xff›
        bb ‹       b     bit& 0xff›]
    ‹(sq ‹ar - br›) + (sq ‹ag - bg›) + (sq ‹ab - bb›)›))
;
(defmacro def-pattern [name kwargs]
  ;! can make local? / can do other made-locals better? so the defs are actually visible?
  ;! any better way than just prefixing with ik?
  `(def ~(symbol (str "ik-"name)) (mk-pattern (bi-ensure-:D (read-image ~(str "res/"name".png"))) ~kwargs)))
(def-pattern radar-flag {:check-first [4 4] :fuzzy 15})
(def-pattern icon-bank {:okayerr 0.5})
(def-pattern icon-clan {:okayerr 0.5})
(def-pattern icon-mine {:okayerr 0.5})
(def-pattern icon-music {:okayerr 0.5})
(def-pattern icon-tree {:okayerr 0.5})
(def-pattern pointing-at-iron-rocks {})
(def-pattern mining-success-iron {})
(def-pattern empty-inv-slot {})
(def-pattern tree {:fuzzy 6})
(def-pattern upper-right {:ret-loc [1 0]})
;
(let [^java.awt.Robot robot (java.awt.Robot.)] ; has def
  (defn create-screen-capture [rect] (.createScreenCapture robot rect))
  (defn get-screen-pixel ([x y] (-> robot (.getPixelColor x y) (.getRGB)))
    ([[x y]] (get-screen-pixel x y)))
  (defn key-press [keycode] (.keyPress robot keycode))
  (defn key-release [keycode] (.keyRelease robot keycode))
  (defn mouse-move ([x y] (.mouseMove robot x y))
    ([[x y]] (mouse-move x y)))
  (letfn [(button-conv [button] (case button ; has def
                                  1 java.awt.event.InputEvent/BUTTON1_MASK
                                  2 java.awt.event.InputEvent/BUTTON2_MASK
                                  3 java.awt.event.InputEvent/BUTTON3_MASK))]
    (defn mouse-press   ([button] (.mousePress   robot (button-conv button))) ([] (mouse-press   1)))
    (defn mouse-release ([button] (.mouseRelease robot (button-conv button))) ([] (mouse-release 1))))
  (defn mouse-wheel [amount] (.mouseWheel robot amount)))
(defn mouse-loc [] (#‹[(.x %) (.y %)]› (-> (java.awt.MouseInfo/getPointerInfo) (.getLocation))))
(defn mouse-move-by [loc] (mouse-move ‹loc +'' (mouse-loc)›))
(defn click ([button] (mouse-press button) (mouse-release button)) ;! you really need to reimplement a bit of fn... http://clojuredocs.org/clojure_core/clojure.core/fn http://www.clodoc.org/doc/clojure.core/maybe-destructured
  ([] (click 1)))
(defn click-at [loc] (let [t (mouse-loc)] (mouse-move loc) (click) (run-in 50 #(mouse-move t))))
(defn drag-mouse [from to] (let [t (mouse-loc)] (mouse-move from) (mouse-press) (mouse-move to) (mouse-release) (mouse-move t)))
(defn click-rs
  ([loc] (click-rs curr-screen loc))
  ([panel loc] (click-at ‹‹panel .screen-loc› +'' loc›)))
(defn mouse-rs
  ([loc] (mouse-rs curr-screen loc))
  ([panel loc] (mouse-move ‹‹panel .screen-loc› +'' loc›)))
(defn key-tap ([key ms] (key-press key) (run-in ms #(key-release key)))
  ([key] (key-tap key 15)))
;
(defn get-screen [rect] (let [rect ‹rect or (mk-rect 0 0 1366 768)›] (mk-panel (create-screen-capture rect) (to-point rect)))) ;! dwargh optional params :(
(defn get-in-rs [rect] (get-screen (mk-rect ‹(to-point rect) +'' (to-point rs-loc)› [(.width rect) (.height rect)]))) ;` consider removing
(defn get-rs-screen []
  (or
    (if rs-loc (let [v (get-screen rs-loc)] (if ‹‹ik-upper-right .find-in v› = ui-NE› v))) ;! don't search the entire curr-screen for upper-right?
    (if-let [ne ‹ik-upper-right .find-in (get-screen nil)›]
      (do
        (def rs-loc (mk-rect ‹ne -'' ui-NE› rs-size))
        (print "finding rs loc..." rs-loc)
        (get-rs-screen))
      (do
        (def rs-loc nil)
        nil))))
(defn inv-slot [idx]
  (let [i-dim [4 7]
        i-s [42 36]
        i-orig ‹‹ui-SE +'' [-40 -41]› -'' ‹i-dim *'' i-s››]
    (-> curr-screen (.sub i-orig) (.sub ‹(remquot idx (i-dim 0)) *'' i-s› i-s))))
(defn refresh [] ;` consider removing / redoing
  (def curr-screen (get-rs-screen))
  (if-do rs-loc
    (def world ‹curr-screen .sub [4 4] [512 334]›)
    (def radar (get-in-rs (mk-rect radar-loc radar-size)))
    (def flaggy ‹ik-radar-flag .find-in radar›)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def start-time (nano-time))
;
(run-repeat-fixed 5000 #(let [t curr-screen] (if t (.write-std t "rs-scr"))))
;
(defn main []
  (def ticks ‹ticks + 1›)
  (refresh)
  (if-do ‹(clipboard-text) = "position_chrome"›
    (set-clipboard-text "")
    (run-in 1000 #(drag-mouse ‹(to-point rs-loc) +'' [319 -76]› ‹‹[1366 768] -'' rs-size› +'' [319 -76]›))
    (refresh))
  (if-do rs-loc
    (let [t ((fn [[x y]] [x (- y)]) ‹(mouse-loc) -'' ‹‹radar .screen-loc› +'' (center radar)››)]
      (if (every? #‹‹-70 <= %› and ‹% <= 70›› t)
        (print "mouse-loc" t)))
    (if-do ‹(rand) < 0.01›
      (click-rs ‹ui-NE +'' [-222 24]›)
      (key-tap VK-UP (approx 1000 950 nil)))
;    (doseq [[x y] ‹ik-tree .find-all-in radar›]
;      ‹‹radar .img› .draw-square x y 0x00ffff 2›)
;    (.write radar (str "tree"ticks))
;    (refresh)
    (if (seq tasks)
      (let [r ((#>tasks 0))]
        (if ‹r = :done› (def tasks (slice tasks 1))
        (if r (def tasks (assoc tasks 0 r))))))
    )
  (if ‹(clipboard-text) != "quit"› (run-in (approx 400) main)))
;
(defn mkt-go-to-icon [pattern name]
  #(let [loc ‹pattern .find-in radar›]
    (if-not loc
      (print "couldn't find" name "icon")
      (if ‹(dist loc myself) >= 15›
        (click-rs radar loc)
        (do
          (print "got to" name "icon")
          :done)))))
(defn mkt-go-by [[x y]]
  #(do
    (click-rs radar ‹(center radar) +'' [x (- y)]›) ; safe limit: (dist [0 0] [x y]) ** 2 < 4500
    #(if flaggy #(if-not-do flaggy :done))))
;
(def task-mine-falador-iron
  #(let [t (mouse-loc)] ;! current: work on this
    (mouse-rs world ‹(center world) +'' [30 0]›)
    (sleep 150)
    (print "ummmmm" ‹ik-pointing-at-iron-rocks .matches world›) ;! CURRENT: implement fetch-newly somehow; this means incorporating certain elements of GUI framework into things like Image
    (mouse-move t)
    ))
 ; ‹ik-mining-success-iron .matches ‹curr-screen .sub [9 444]››
 ; ‹ik-pointing-at-iron-rocks .matches world›
(def task-inv-full
  #(if ‹ik-empty-inv-slot .matches (inv-slot 27)›
    (print "inventory: not-full")
    (do (print "inventory: probably-full") :done)))
(def tasks [
    task-mine-falador-iron
    task-inv-full
    (mkt-go-to-icon ik-icon-mine "mine")
    (mkt-go-by [0 65])
    (mkt-go-by [48 48])
    (mkt-go-by [48 48])
    (mkt-go-by [0 50])
    (mkt-go-to-icon ik-icon-clan "clan")
    (mkt-go-by [20 40])
    (mkt-go-to-icon ik-icon-tree "tree")
    (mkt-go-by [20 60])
    (mkt-go-by [22 60])
  ])
;; ...
;(def task-goto-bank-icon (
;  task_gotoBank = (#Runnable)(=> {
;      icon := bankIcon.findIn(radar);
;      if ‹(dist icon myself) < 15› {
;        print("we should bank the ore");
;      } else {
;        print("trying to go to the bank");
;        if icon != null: clickMap(icon);
;      }
;    })/*,
;  task_ = (#Runnable)(=> {
;    })*/;
;Runnable task = task_invFull;

;//if at mining zone
;//  if see ore on ground
;//    grab ore
;//  else
;//    if at mining spot
;//      mine
;//    else
;//      goto mining spot
;//else
;//  goto mining zone

;; clickLoc := point[2];
;; for i to clickLoc.length {
;;   print("enter to pick loc", i);
;;   new Scanner(System.in).nextLine();
;;   clickLoc[i] = (mouse-loc)
;; }
;; print("got locs");
;; 
;; startingTime := mTime();
;; 
;; for (int cycles;; cycles++) {
;;   ...
;;   (click-rs clickLoc[cycles % clickLoc.length])
;;   sleep(approx(5000));
;; }
;; 
;; Point[] pathBack = [
;;     point(1268 - 1365, 104 - 95),
;;     point(1269 - 1365, 105 - 95),
;;     point(1246 - 1365, 113 - 95),
;;     point(1230 - 1365, 136 - 95),
;;     point(1219 - 1365, 141 - 95),
;;     point(1255 - 1365, 118 - 95),
;;   ];
;; 
;; for v : pathBack {
;;   screen := getAndWriteScreen(startingTime);
;;   
;;   locNE := upperRight.findIn(screen);
;;   if locNE == null {sleep(1000); continue}
;;   
;;   (click-rs ‹v +'' locNE›)
;;   
;;   sleep(approx(18500));
;; }