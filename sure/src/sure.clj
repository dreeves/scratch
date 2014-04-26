(ns sure)
(require '[clojure.string :as str])
(use 'batteries)
(require '[ansi_console :as esc])
(require 'run)
(use 'clojure.data.priority-map)

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; misc general ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;
;;;;;  THE FOLLOWING MATERIAL IS DUPLICATED ELSEWHERE  ;;;;;

(d nsn ← (. *ns* getName) (defn r[] (eval `(d (use '~nsn :reload-all) ~'(main)))))

(defn sys-time[] ‹(double (System/nanoTime)) / 1000000000›)
(def load-time (sys-time))
(defn app-time[] ‹(sys-time) - load-time›)

;;;;; un-dup

(defn interpose-out[sep coll] (concat [sep] (interpose sep coll) [sep]))

(defn init-loops[] (if →loops (mapv run/cancel @loops)) (def loops (atom [])))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; misc specific ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(defn namegen[]
	(apply str (map #(rand-nth (case % \a "abcdefghijklmnopqrstuvwxyz" \c "bcdfghjklmnpqrstvwxz" \v "aeiouy" \0 "0123456789" " "))
		(rand-nth [
			"  a  "
			" c v "
			"cv cv"
			" cvc "
			"cvcvc"
			"cv  a"
			"a  cv"
			" cv0 "
			"a  00"
			]))))
(defn in-bounds[[x y]] (and ‹0 ≤ x› ‹x < 13› ‹0 ≤ y› ‹y < 9›))
(defn rand-choice[choices]
	(if-letd ‹‹sum ← (apply + (map first choices))› ≠ 0› (d
		choice ← (atom ⌊(rand sum)⌋)
		((first (drop-while (λ[[w v]] (swap! choice - w) ‹@choice ≥ 0›) choices)) 1)
		)))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; main ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(def bob-soul-bits [
	(λ[] [[⌊(rand 10)⌋ :attack] [⌊(rand 10)⌋ :move] [⌊(rand 10)⌋ :mine]])
	])

(defn bobgen[p] (d
	sb ← (atom ((rand-nth bob-soul-bits))) {
	:name (esc/fg (rand-nth "12359abc") (namegen))
	:spirit (atom 10)
	:pos (atom p)
	:soul (λ[grid-up grid-down self]
		(if (¬ @(self :body)) (d
			near ← (filter #(in-bounds ‹@(self :pos) ++ %›) [[1 0] [-1 0] [0 1] [0 -1]])
			near-bobs ← (filter #‹@(grid-up ‹@(self :pos) ++ %›)› near)
			near-space ← (filter #(¬ @(grid-up ‹@(self :pos) ++ %›)) near)
			sb ← @sb
			sb ← (if (seq near-bobs) sb (remove #‹(% 1) = :attack› sb))
			sb ← (if (seq near-space) sb (remove #‹(% 1) = :move› sb))
			(case (rand-choice sb)
				:attack [:attack (rand-nth near-bobs)]
				:move [:move (rand-nth near-space)]
				:mine [:mine]
				nil
				))))
	:soulbit sb
	:soulstr #‹(apply format "%d>%dm%d" (map #(% 0) @sb))›
	:body (atom nil)
	:age (atom 0)
	}))

; todo: know if run/every isn't going as fast as expected?
; observation: optimal tactic depends on regen rate, is somewhere around {0.02: 0 1 1, 0.05: 0 1 2}

(defn main[] (d
	(init-loops)

	(def fast-count (atom 0))

	(def grid-up (atom {}))
	(def grid-down (atom {}))
	(dotimes [y 9] (dotimes [x 13] (swap! grid-up   assoc [x y] (atom nil))))
	(dotimes [y 9] (dotimes [x 13] (swap! grid-down assoc [x y] (atom 50))))

	(def souls (atom []))

	(d ; display
	(def cell-str [
		#(if-letd ‹v ← @(@grid-up %)› (v :name) "     ")
		#(d t ← @(@grid-up %) (if t
			(d t ← @(t :spirit) (if ‹t < 10›
				(format (str"q"(esc/fg :cyan "%4.2f")) (float t))
				(format (str"q"(esc/fg :cyan+ "%4d")) ⌊t⌋)
				)) "     "))
		#(if-let [v @(@grid-up %)] ((v :soulstr)) "     ")
		#(if-let [v @(@grid-up %)] (d a ← ⌊@(v :age)⌋ (esc/color (cond ‹a < 10› :black ‹a < 30› :black+ ‹a < 100› :grey ‹a < 200› :white :else :black) (cond ‹a < 200› :black :else :grey) (format "a%4d" a))) "     ")
		#(format (str"*"(esc/fg :cyan "%3d")"*") ⌊@(@grid-down %)⌋)
		])
	(swap! loops conj (run/repeat 0.1 (λ[]
		; format and print
		(print (esc/go-to 0 0))
		(println (str/join (map #(str % "\n")
			(apply concat (interpose-out
				[(str/join (interpose-out "-" (repeat 13 "-----")))]
				(map #(map #(str/join (interpose-out "|" %)) %)
					; map grid to strings
					(for [y (range 9)] (for [i (range 5)] (for [x (range 13)] ((cell-str i) [x y]))))
					)
				))
			)))
		)))
	(swap! loops conj (run/repeat 5 (λ[] (print esc/clear))))
	)

	; physics
	(run/in 0 (λ me[]
		(dotimes [y 9] (dotimes [x 13] (swap! (@grid-down [x y]) + 0.04)))
		(doseq [soul @souls] (swap! (soul :age) + 0.1))
		; random bob gen
		(if ‹(rand) < 0.2› (d
			pos ← [⌊(rand 13)⌋ ⌊(rand 9)⌋]
			(swap! (@grid-up pos) #(or % (d t ← (bobgen pos) (swap! souls conj t) t)))
			))
		; run souls
		(doseq [soul @souls] (if (¬ ‹@(soul :body) and ‹(@(soul :body) 0) = :tired››) (d
			action ← ((soul :soul) @grid-up @grid-down soul)
			(if action (reset! (soul :body) action))
			)))
		; run death
		(swap! souls #(remove (λ[soul] (d
			(if ‹@(soul :spirit) < 0› (throw (derp "negative spirit!" soul)))
			r ← ‹@(soul :spirit) < 0.01›
			(if r (reset! (@grid-up @(soul :pos)) nil))
			r)) %))
		; run soul actions
		(doseq [soul @souls] (d
			body ← (soul :body)
			pos ← (soul :pos)
			(cond
				; clear to wait?
				(¬ @body) nil
				‹(@body 0) = :attack› (d
					atk ← ‹@pos ++ (@body 1)›
					(if ‹‹@(soul :spirit) ≥ 1› and @(@grid-up atk)› (d
						(swap! (soul :spirit) - 1)
						(swap! (@(@grid-up atk) :spirit) #(max ‹% - %2› 0) 2)
						(reset! body [:tired (atom 1/10)])
					) (d
						(reset! body nil)
					)))
				‹(@body 0) = :move› (d
					move ← ‹@pos ++ (@body 1)›
					(if ‹‹@(soul :spirit) ≥ 0.1› and (¬ @(@grid-up move))› (d
						(swap! (soul :spirit) - 0.1)
						(reset! (@grid-up move) soul)
						(reset! (@grid-up @pos) nil)
						(reset! pos move)
						))
					(reset! body nil)
					)
				‹(@body 0) = :mine› (d
					(if ‹‹@(soul :spirit) ≥ 0.1› and ‹@(@grid-down @pos) ≥ 1›› (d
						(swap! (soul :spirit) - 0.1)
						(swap! (@grid-down @pos) - 1)
						(swap! (soul :spirit) + 1)
						))
					(reset! body nil)
					)
				‹(@body 0) = :tired›
					(if ‹(swap! (@body 1) - 1/10) ≤ 0›
						(reset! body nil))
				:else (throw (derp "unknown action" soul))
				)))
		;
		(run/in (if ‹@fast-count ≤ 0› 0.1 (d (swap! fast-count dec) 0)) me)
		))
	nil))

(defn j[n] (swap! →fast-count + n))