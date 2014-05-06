(ns befunge.turtles
  (:refer-clojure :exclude [print file-seq])
  (:use lust)
  
  (:require shiny)
  (:use penbra.opengl.core)
  (:require [shiny.texture :as tex])
  )
;;;
; important notes:
; However, spaces in the program source do not overwrite anything in Funge-Space; in essence the space character is transparent in source files. This becomes important when the i "Input File" instruction is used to include overlapping files.
; http://quadium.net/funge/spec98.html
; http://users.tkk.fi/~mniemenm/files/befunge/mycology/mycology-readme.txt
;;;
(def scanner (java.util.Scanner. System/in))
(defn read-in [] ‹System/in .read›)
;;;
(def-atom funge-space)
(def-atom dims)
(def ip (atom nil :validator #(do
                                (if % (#>record-ip %))
                                ;(if ‹% = [85 57]› (read-in))
                                true)))
(def-atom delta)
(def-atom stacks)
(def-atom toss)
(def-atom string-mode)
(def-atom storage-offset)
(def-atom output)
(def-atom tick-len 200)
(zutil.KeyCallback/on_press \P #(swap! tick-len #(case %, 5 50, 50 500, 500 2000, 2000 499, 499 49, 49 5, 500)))
(shiny/basic-font-tex "befunge/font.png")
;;;
(defn oob [[x y]]
  (or ‹‹x < ((@dims 0) 0)› or ‹x >= ((@dims 1) 0)››
      ‹‹y < ((@dims 0) 1)› or ‹y >= ((@dims 1) 1)››))
(defn find-next-insn [start delta]
  (loop [loc start
         semic-mode false]
    (record-ip loc)
    (if (oob loc)
      (recur
        (loop [loc ‹loc -+ delta›
               check false]
          (if (oob loc)
            (if check
              ‹loc ++ delta›
              (recur ‹loc -+ delta› false))
            (recur ‹loc -+ delta› true)
            ))
        semic-mode)
      (let [c (#>cget loc)]
        (if semic-mode
          (recur ‹loc ++ delta› (not ‹c = \;›))
          (case c
            \space (recur ‹loc ++ delta› false)
            \; (recur ‹loc ++ delta› true)
            loc))))))
(defn funge-print [v] (swap! output str v))
(defn fill-grid [lines]
  (aset! dims [[0 0] [0 0]])
  (aset! funge-space {})
  (aset! ip [0 0])
  (aset! delta [1 0])
  (aset! stacks [])
  (aset! toss [])
  (aset! string-mode false)
  (aset! output "")
  (aset! storage-offset [0 0])
  
  (doseq-indexed [y line lines]
    (doseq-indexed [x c line]
      (#>put [x y] c)))
  )
(defn fwd ; opt-args
  ([i] (swap! ip ++ ‹@delta *+ i›))
  ([] (fwd 1))
  )
;;;
(defn put [loc v]
  (swap! funge-space assoc loc (long' v))
  (aset! dims [‹(@dims 0) min+ loc› ‹(@dims 1) max+ ‹loc ++ 1››])
  )
(defn iget [loc] ‹(@funge-space loc) or (long' \space)›)
(defn cget [loc] (char (iget loc)))
;;;
(defn push [v] (swap! toss conj (long' v)))
(defn ipop [] (if (empty? @toss) 0 (apop! toss)))
(defn ipeek [] ‹(last @toss) or 0›)
(defn cpop  [] (char (ipop )))
(defn cpeek [] (char (ipeek)))
;;;
(defn exe [insn]
  (if @string-mode
    (case insn
      \" (aset! string-mode false)
      \space (do (push insn) (while ‹(cget @ip) = \space› (fwd)) (fwd -1))
      (push insn))
    (#>exe' insn)))
(defn- exe' [insn]
  (loop [insn insn]
  (case insn
  ;;;;; Code: Program Flow ;;;;;
    ; direction changing
    \^ (aset! delta [ 0 -1])
    \> (aset! delta [ 1  0])
    \v (aset! delta [ 0  1])
    \< (aset! delta [-1  0])
    \? (recur (rand-nth [\^ \> \v \<]))
    \r (swap! delta *+ -1)
    \[ (swap! delta (fn [[x y]] [   y (- x)]))
    \] (swap! delta (fn [[x y]] [(- y)   x ]))
    \x (let [y (ipop) x (ipop)] (aset! delta [x y]))
    ; flow control
    \space (do (aset! ip (find-next-insn @ip @delta)) (recur (cget @ip)))
    \# (fwd)
    \@ (aset! delta [0 0])
    \; (do (aset! ip (find-next-insn @ip @delta)) (recur (cget @ip)))
    \j (fwd (ipop))
    \q (throw (ex "would be System/exit with code "(ipop)))
    \k (let [n (ipop)
             next (find-next-insn ‹@ip ++ @delta› @delta)]
         (dotimes [_ n] (exe (cget next)))
         (if ‹n == 0› (recur \#)))
    ; decision making
    \! (let [v (ipop)] (push (if ‹v == 0› 1 0)))
    \` (let [b (ipop) a (ipop)] (push (if ‹a > b› 1 0)))
    \_ (let [v (ipop)] (recur (if ‹v == 0› \> \<)))
    \| (let [v (ipop)] (recur (if ‹v == 0› \v \^)))
    \w (let [b (ipop) a (ipop)] (if ‹a !== b› (recur (if ‹a < b› \[ \]))))
  ;;;;; Data: Cell Crunching ;;;;;
    ; integers & arith
    (\0 \1 \2 \3 \4 \5 \6 \7 \8 \9) (push ‹(int insn) - (int \0)›)
    (\a \b \c \d \e \f) (push ‹‹(int insn) - (int \a)› + 10›)
    \+ (let [b (ipop) a (ipop)] (push ‹a l+ b›))
    \- (let [b (ipop) a (ipop)] (push ‹a l- b›))
    \* (let [b (ipop) a (ipop)] (push ‹a l* b›))
    \/ (let [b (ipop) a (ipop)] (push (if ‹b == 0› 0 ‹a lD   b›)))
    \% (let [b (ipop) a (ipop)] (push (if ‹b == 0› 0 ‹a lrem b›)))
    ; strings
    \" (aset! string-mode true)
    \' (do (push (iget ‹@ip ++ @delta›)) (recur \#))
    \s (do (put ‹@ip ++ @delta› (ipop)) (recur \#))
    ; stack manipulation
    \$ (ipop)
    \: (push (ipeek))
    \\ (let [b (ipop) a (ipop)] (push b) (push a))
    \n (aset! toss [])
    ; stack stack manipulation
    \{ (let [n (ipop)
             transfer (if ‹n <= 0› [] (vec (reverse (for [_ (range n)] (ipop)))))]
         (if ‹n < 0› (dotimes [_ (- n)] (push 0)))
         (mapv push @storage-offset)
         (aset! storage-offset ‹@ip ++ @delta›)
         (swap! stacks conj @toss)
         (aset! toss transfer)
         )
    \} (if ‹(len @stacks) == 0›
         (recur \r)
       (let [n (ipop)
             transfer (if ‹n <= 0› () (reverse (for [_ (range n)] (ipop))))]
         (aset! toss (apop! stacks))
         (aset! storage-offset (let [y (ipop) x (ipop)] [x y]))
         (if ‹n < 0› (dotimes [_ (- n)] (ipop)))
         (doseq [v transfer] (push v))
         ))
    \u (if ‹(len @stacks) == 0›
         (recur \r)
         (let [n (ipop)]
           (if-not ‹n == 0›
             (if ‹n > 0›
               (let [real-top (get-set! toss (apop! stacks))
                     vs (doall (for [_ (range n)] (ipop)))
                     _ (swap! stacks conj (get-set! toss real-top))
                     _ (mapv push vs)])
               (let [vs (doall (for [_ (range n)] (ipop)))
                     real-top (get-set! toss (apop! stacks))
                     _ (mapv push vs)
                     _ (swap! stacks conj (get-set! toss real-top))])
               ))))
  ;;;;; Media: Communications and Storage ;;;;;
    ; funge-space storage
    \g (let [y (ipop) x (ipop)] (push (iget ‹[x y] ++ @storage-offset›)))
    \p (let [y (ipop) x (ipop) v (ipop)] (put ‹[x y] ++ @storage-offset› v))
    ; standard input/output
    \. (funge-print (str (ipop)" "))
    \, (funge-print (cpop))
    \& (push (loop [c (read-in)] (if (digit? c) (Long/parseLong (str (char c) ‹scanner .nextLong›)) (recur (read-in)))))
    \~ (push (read-in))
    ; file input/output
    ; http://quadium.net/funge/spec98.html#Fileio
    ; system information retrieval
    ; http://quadium.net/funge/spec98.html#Sysinfo
  ;;;;; Concurrent Funge-98 ;;;;;
    ;\t
    \z nil ; NOP
  ;;;;; unimplemented insns ;;;;;
    (do
      (throw (ex "WARNING: instruction "insn" not implemented"))
      (recur \r)))))
(defn tick []
  (exe (cget @ip))
  (fwd)
  )
;;;
(def-atom old-ips ‹nil rep 60›)
(defn record-ip [ip]
  (if ‹ip != (last @old-ips)›
    (if ‹@old-ips has ip›
      (swap! old-ips #(conj (pop %) ip))
      (swap! old-ips #(vec (rest (conj % ip)))))))
(defn run []
  (ae "fixme (easy)")
  (fill-grid (split-lines (slurp "C:/Users/vuntic/Desktop/mycology2")))
  (shiny/run
    :dims [‹81 * 7› 730] ;‹[0 200] ++ ‹[81 31] *+ [7 12]››
    :init
      (fn []
        (shiny/init-fn)
      
        (def font-tex (tex/mk "font.png"))
        )
    :draw
      (fn []
        (shiny/draw-fn)
      
        (let [ips (filter id @old-ips)
              ip (last ips)
              [ips ip'] (split-at ‹(len ips) - 1› ips) ;! should be split-at -1
              ips (map #(let [col ‹‹% + 1› dD ‹(len ips) + 1››]
                          [[‹‹0.75 + ‹col * 0.25›› rep 3› ‹‹col * 0.5› rep 3›] %2])
                    (range)
                    ips)
              ip' [[[0 0 0] [0 0.5 0]] (first ip')]
              ips (conj (vec ips) ip')
              rng-x [‹(ip 0) - 40› ‹(ip 0) + 40›]
              rng-y [‹(ip 1) - 15› ‹(ip 1) + 15›]
              ]
          (doseq [y (apply range-incl rng-y)]
            (let [s (apply str (map cget (for [x (apply range-incl rng-x)] [x y])))
                  colors (into {} (map #‹[‹‹((% 1) 0) - (ip 0)› + 40› (% 0)]› (filter #‹((% 1) 1) == y› ips)))]
              (shiny/basic-draw-ansi-colored-string! s colors :with #(gl,translatef 0 ‹‹‹y - (ip 1)› + 15› * 12› 0)))))
        (let [char-stack (str-join "" (map #(if ‹‹(int \ ) < %› and ‹% <= (int \~)›› (char %) (str "["%"]")) (reverse @toss)))]
          (shiny/basic-draw-string!
            (word-wrap 81
              (str
                "~~~~~ data: ~~~~~\n"
                "dims: "@dims" | IP: "@ip" | delta: "@delta"\n"
                "stack: "@toss"\n"
                "char-stack: "char-stack"\n"
                "output:\n"
                (str-join "\n" (take-last 18 (split-lines @output)))))
            :with #(gl,translatef 0 ‹‹31 * 12› + 20› 0)
            ))
        )
    )
  (while ‹delta != [0 0]›
    (if @tick-len (Thread/sleep @tick-len))
    (tick))
  (print "program ended!"))
;;;
(run)
;! current: implement non-cardinal out-of-bounds wrapping