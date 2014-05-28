(ns zilch
  (:refer-clojure :exclude [print file-seq])
  (:use lust)
  )

(defn bsubs? [a b]
  (cond
    (empty? a) true
    (empty? b) false
    ‹(first a) > (first b)› (bsubs? a (rest b))
    ‹(first a) < (first b)› false
    :else (bsubs? (rest a) (rest b))
    ))

; a combo or set of combos is represented by len -> [expected-value zilch-chance]

(def-atom tmpg6 [0 0])

(def goal
  (fn [len] (dool
    dl <- ‹len - 1›
    (case len
      0 (goal 6)
      1 (avg+
          [‹100 + (goal dl)› 0]
          [‹ 50 + (goal dl)› 0]
          [0 1]
          [0 1]
          [0 1]
          [0 1])
      2 (avg+ )
      ))))