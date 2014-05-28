(ns zin.physics.force
  (:refer-clojure :exclude [print file-seq])
  (:use lust)

  (:import jin.math.Vector3)
  (:import jin.physics.Body)
  )
;;;
(defn gravity-force
  "acts on body's centre of mass; proportional to body's total mass; is 9.8m/s^2 downwards unless otherwise specified"
  ([body] (gravity-force (Vector3. 0 -1 0) 9.8))
  ([body dir mag]
    (reify jin.physics.force.Force
      (apply ^void[this dt]
        (. body applyForce (Vector3.) (-> body (.state) (.anisotropicmass) (.multiply (. dir multiply mag))) dt)
        )))
  )
(defn impulse-force
  "acts only once. 'For instance, this type of force can be used to produce explosion like effects.'"
  ;! MEMORY LEAK (maybe): a way to actually remove forces that aren't going to do anything again??
  [body point dir mag]
  (dool
    (let dir (. dir normalize))
    (let done (atom false))
    (reify jin.physics.force.Force
      (apply ^void[this dt]
        (do-if-not @done
          (. body applyForce point (. dir multiply ‹mag / dt›) dt)
          (aset! done true))
        ))
    ))