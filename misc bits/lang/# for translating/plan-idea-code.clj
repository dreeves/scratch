;ns game
(ns game)

;players = #{}
(def players (atom #{}))

;review_contracts = #{}
(def review_contracts (atom #{}))
;new_contracts = #{}
(def new_contracts (atom #{}))

;def propose(v):
(defn propose [v]
  ;print("a proposal!" v.map(\[k v]->[k['name] v['$]]))
  (println "a proposal!" (map (fn [[k v]] [(k :name) (v :$)]) v))
  ;if sum(v.vals().map(.['$])) != 0:
  (if (not (zero? (apply + (map #(% :$) (vals v)))))
    ;print("bad contract (bad sum):" v)
    (println "bad contract (bad sum):" v)
  ;else
    ;new_contracts conj= v
    (swap! new_contracts conj v)))
;def ^macro others(): `players.disj(~'this)
(defmacro others [] `(disj @players ~'this))
;def ^macro reviewables(): `review_contracts.filter(.[~'this])
(defmacro reviewables [] `(filter #(% ~'this) @review_contracts))
;def ^macro agree(contract): `(~contract[~'this]['agree] = true)
(defmacro agree [contract] `(reset! ((~contract ~'this) :agree) true))

;def initial_player(name): {'$ 1.0 'name name 'do nil}
(defn initial_player [name] {:$ (atom 1.0) :name name :do (atom nil)})
;NID = 0
(def NID (atom 0))
;def NEWB():
(defn NEWB []
  ;this = initial_player(str("NEWB_"++NID))
  (let [this (initial_player (str "NEWB_"(swap! NID inc)))]
  ;this['do] = \-> for reviewables(): agree(it)
  (reset! (this :do) #(doseq [v (reviewables)] (agree v)))
  ;this
  this))
;POLITE = initial_player("POLITE")
(def POLITE (initial_player "POLITE"))
;POLITE['do] = \->
(reset! (POLITE :do)
  ;this = POLITE
  #(let [this POLITE]
  ;for reviewables(): agree(it)
  (doseq [v (reviewables)] (agree v))
  ;for others(): propose({this {'$ it['$] 'agree true} it {'$ -it['$] 'agree false}})
  (doseq [v (others)] (propose {this {:$ @(v :$) :agree (atom true)} v {:$ (- @(v :$)) :agree (atom false)}}))
  ))
;GREEDY = initial_player("GREEDY")
(def GREEDY (initial_player "GREEDY"))
;GREEDY['do] = \->
(reset! (GREEDY :do)
  ;this = GREEDY
  #(let [this GREEDY]
  ;for reviewables(): if it[this]['$] >= 0: agree(it)
  (doseq [v (reviewables)] (if (>= ((v this) :$) 0) (agree v)))
  ;for others(): propose({this {'$ it['$] 'agree true} it {'$ -it['$] 'agree false}})
  (doseq [v (others)] (propose {this {:$ @(v :$) :agree (atom true)} v {:$ (- @(v :$)) :agree (atom false)}}))
  ))

;for [..6]: players conj= NEWB()
(dotimes [i 6] (swap! players conj (NEWB)))
;players conj= POLITE
(swap! players conj POLITE)
;players conj= GREEDY
(swap! players conj GREEDY)

;def print_players():
(defn print_players []
  ;print "name\t$"
  (println "name\t$")
  ;for players.sort_by(.['name]): print(str(it['name]"\t"it['$]))
  (doseq [v (sort-by :name @players)] (println (str (v :name)"\t"@(v :$)))))

;def main():
(defn main []
  ;rounds_failed = 0
  (def rounds_failed (atom 0))
  ;rounds = 0
  (def rounds (atom 0))
  ;while true:
  (loop []
    ;print(str("running round #"++rounds", starting with:"))
    (println (str "running round #"(swap! rounds inc)", starting with:"))
    ;print_players()
    (print_players)
    ;for players: (it['do])()
    (doseq [v @players] (@(v :do)))
    ;done_contracts = review_contracts
    (let [done_contracts @review_contracts]
    ;review_contracts = new_contracts
    (reset! review_contracts @new_contracts)
    ;new_contracts = #{}
    (reset! new_contracts #{})
    ;if rounds != 1 and done_contracts == []: break
    (if (and (empty? done_contracts) (not (== @rounds 1)))
      nil
    ;done_contracts .= filter(.vals().map(.['agree]).all())
    (let [done_contracts (filter (fn [v] (every? #(do @(% :agree)) (vals v))) done_contracts)]
    ;(done_contracts == []? ++ (= 0))(rounds_failed)
    (if (empty? done_contracts) (swap! rounds_failed inc) (reset! rounds_failed 0))
    ;if rounds_failed == 5: break
    (if (== @rounds_failed 5)
      nil
      (do
    ;for done_contracts:
    (doseq [v done_contracts]
      ;for [player v] in it:
      (doseq [[player v] v]
        ;r = player['$] + v['$]
        (let [r (+ @(player :$) (v :$))]
        ;if r < 0:
        (if (neg? r)
          (do
          ;print(player['name] "is dead!")
          (println (player :name) "is dead!")
          ;players disj= player
          (swap! players disj player)
          )
        ;else
          ;player['$] = r
          (reset! (player :$) r)
          ))
      ))
  (recur)))))))
  )

;main()
(main)