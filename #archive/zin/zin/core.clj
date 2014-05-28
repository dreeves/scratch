(ns zin.core
  (:refer-clojure :exclude [print file-seq])
  (:use lust)

  (:require [shiny.core :as shiny])
  (:use openumbra.opengl.core)
  (:require [texture :as tex])

  (:import jin.math.Vector3)
  (:import jin.math.Matrix3)
  (:import jin.physics.Body)

  (:require [zin.physics.force :as force])
  )
;;;
(ddefn shadow-projection-matrix [^Vector3 l ^Vector3 e ^Vector3 n]
  "This is where the 'magic' is done:
   
   Multiply the current ModelView-Matrix with a shadow-projetion matrix.
   
   l is the position of the light source
   e is a point on within the plane on which the shadow is to be projected.  
   n is the normal vector of the plane.
   
   Everything that is drawn after this call is 'squashed' down to the plane. Hint: Gray or black color and no lighting looks good for shadows *g*"
  (zutil.Tmp/spm l e n))
;;;
(defn run []
  (def scene (jin.physics.DefaultScene.
               (jin.collision.SAP2.)
               (jin.physics.solver.NonsmoothNonlinearConjugateGradient. 44)
               (jin.physics.DisabledDeactivationPolicy.)))
  (. scene setTimestep 0.1)

  (def bfloor (doto (Body. "floor" (jin.geometry.Box. 1500 1 1500)) (.setPosition (Vector3. 0 -5 0   )) (.setFixed true)))
  (def bback  (doto (Body. "back"  (jin.geometry.Box. 200 200 20  )) (.setPosition (Vector3. 0 10 -200)) (.setFixed true)))
  (def bfront (doto (Body. "front" (jin.geometry.Box. 200 200 20  )) (.setPosition (Vector3. 0 10  200)) (.setFixed true)))
  (def bleft  (doto (Body. "left"  (jin.geometry.Box. 20 200 200  )) (.setPosition (Vector3. -200 10 0)) (.setFixed true)))
  (def bright (doto (Body. "right" (jin.geometry.Box. 20 200 200  )) (.setPosition (Vector3.  200 10 0)) (.setFixed true)))

  (def bbox0 (doto (Body. "box0" (jin.geometry.Box. 2 2 2)) (.setPosition 0 26 0) (.setAngularVelocity 1 0 0)))
  (def bbox1 (doto (Body. "box1" (jin.geometry.Box. 2 1 3)) (.setPosition 0 28 0) (.setAngularVelocity 0 0 1)))
  (def bbox2 (doto (Body. "box2" (jin.geometry.Box. 1 4 1)) (.setPosition 0 21 0) (.setAngularVelocity 0 1 0)))

  (def bcap1 (doto (Body. "cap1" (jin.geometry.UniformCapsule. 3.5 0.5)) (.setPosition 4 26 0) (.setAngularVelocity 0 0 25)))
  (def bcap2 (doto (Body. "cap2" (jin.geometry.UniformCapsule. 0.2 20 )) (.setPosition 8 26 0) (.setAngularVelocity 0 0 5)))

  (. scene addBody bfloor)
  (. scene addBody bback)
  (. scene addBody bfront)
  (. scene addBody bleft)
  (. scene addBody bright)
  (. scene addBody bbox0)
  (. scene addBody bbox1)
  (. scene addBody bbox2)
  (. scene addBody bcap1)
  (. scene addBody bcap2)

  (. scene addForce (force/gravity-force bbox0 (Vector3. 0 -1 0) 0.5))
  (. scene addForce (force/gravity-force bbox1 (Vector3. 0 -1 0) 0.5))
  (. scene addForce (force/gravity-force bbox2 (Vector3. 0 -1 0) 0.5))
  (. scene addForce (force/gravity-force bcap1 (Vector3. 0 -1 0) 0.5))
  (. scene addForce (force/gravity-force bcap2 (Vector3. 0 -1 0) 0.5))

  (def-atom to-draw (mapv #(zutil.Tmp/getDrawShape %) (mapcat #(iterator-seq (. % getGeometries)) [bback bfront bleft bright bfloor bbox0 bbox1 bbox2 bcap1 bcap2])))

  (shiny/run
    :dims [1024 (int ‹1024 / ‹16.0 / 9››)]
    :init
      (fn []
        (shiny/init-fn)
      
        (gl,enable :light0)
        ;(gl,shade-model :flat)
        (gl,light :light0 :ambient  (buf (float-array [ 0.5  0.5  0.5  1.0])))
        (gl,light :light0 :diffuse  (buf (float-array [ 1.0  0.0  0.0  1.0])))
        (gl,light :light0 :specular (buf (float-array [ 0.0  0.0  1.0  1.0])))
        (gl,light :light0 :position (buf (float-array [ 0.0  0.0  0.0  1.0])))
        )
    :draw
      #(dool
        (shiny/draw-fn)
      
        (. scene tick) ;! shouldn't be in the gl section; is physics stuff

        (let flux (cache (atom 1.0)))
        (swap! flux + 0.001)

        (let fx-2 (sin ‹@flux * 1›))
        (let fx-1 (sin ‹@flux * 2›))
        (let fx0 (sin ‹@flux * 3›))
        (let fx1 (sin ‹@flux * 5›))
        (let fx2 (sin ‹@flux * 8›))
        (let fx3 (cos ‹@flux * 13›))
        (let fx4 (cos ‹@flux * 21›))
        (let fx5 (cos ‹@flux * 34›))

        (gl,push-matrix)
          (let lah ‹5 + ‹fx2 * 5››)
          (glu,look-at
            ‹fx-1 * 20› lah ‹fx1 * 20›
            0 lah 0
            0 1 0)

          ;(gl,begin :triangles)
          ;  (gl,color-3d ‹‹0.8 * fx0› + 0.2› ‹‹0.2 * fx5› + 0.2› ‹‹0.2 * fx4› + 0.2›) (gl,vertex-3d ‹ 0.0 + fx0› 0.01 ‹ 2.0 + fx3›)
          ;  (gl,color-3d ‹‹0.2 * fx1› + 0.2› ‹‹0.8 * fx1› + 0.2› ‹‹0.2 * fx2› + 0.2›) (gl,vertex-3d ‹-2.0 + fx1› 0.01 ‹-2.0 + fx4›)
          ;  (gl,color-3d ‹‹0.2 * fx3› + 0.2› ‹‹0.2 * fx0› + 0.2› ‹‹0.8 * fx2› + 0.2›) (gl,vertex-3d ‹ 1.0 + fx2› 0.01 ‹-2.0 + fx5›)
          ;(gl,end)

          (gl,color-3d 0 0.8 0.6)
          (gl,polygon-mode :front-and-back :line)
            (let sqr (fn [x z]
                       (gl,begin :quads)
                         (gl,vertex-3d ‹x + 0› 1 ‹z + 0›)
                         (gl,vertex-3d ‹x + 1› 1 ‹z + 0›)
                         (gl,vertex-3d ‹x + 1› 1 ‹z + 1›)
                         (gl,vertex-3d ‹x + 0› 1 ‹z + 1›)
                       (gl,end)))
            (doseq [x (range-incl -10 10)
                    z (range-incl -10 10)]
              (sqr x z))
          (gl,polygon-mode :front-and-back :fill)

          (gl,enable :lighting)
          (doseq [shape @to-draw]
            (gl,push-matrix)
            (gl,mult-matrix (-> shape (.getTransform) (.toArray) (buf)))

            ;`(if (-> shape (.getReferenceBody) (.deactivated))
            ;`  (gl,light :light0 :ambient  (buf (float-array [ 1.5  1.5  2.0  1.0])))
            ;`  ;(gl,light :light0 :diffuse  (buf (float-array [ 0.8  0.0  0.8  1.0])))
            ;`  ;(gl,light :light0 :specular (buf (float-array [ 0.5  0.5  0.5  1.0])))
            ;`  ;(gl,light :light0 :position (buf (float-array [-1.5  1.0 -4.0  1.0])))
            ;`)

            ;(gl,push-matrix)
            ;(gl,mult-matrix (Matrix4/pack ‹shape .getTransform›)) ; presumably this is a double[]; you'll have to put in buf code

            (doseq [face (iterator-seq ‹shape .getFaces›)]
              (gl,begin :polygon)
                (let [vf (vec face)
                      n (-> (vf 1) (.sub (vf 0)) (.cross ‹(vf 2) .sub (vf 1)›) (.normalize))] ;! "compute normal" move to function
                  (doseq [v face]
                    (gl,normal-3d ‹n .x› ‹n .y› ‹n .z›)
                    ;(gl,tex-coord-2f 1.0 1.0)
                    ;(gl,color-3d ‹v .a1› ‹v .a2› ‹v .a3›)
                    (gl,color-3d (sin ‹v .x›) (sin ‹v .y›) (sin ‹v .z›))
                    (gl,vertex-3d ‹v .x› ‹v .y› ‹v .z›)
                    ;`(gl,tex-coord-2f 0.0 1.0)
                    ))
              (gl,end))

            ;`(gl,polygon-mode :front :line)
            ;`(gl,line-width 1.7)
            ;`(gl,disable :lighting)
            ;`(gl,push-matrix)
            ;`  (gl,scaled 1.01 1.01 1.01)
            ;`  (doseq [face (iterator-seq ‹shape .getFaces›)]
            ;`    (gl,begin :polygon)
            ;`      (let [vf (vec face)
            ;`            n (-> (vf 1) (.sub (vf 0)) (.cross ‹(vf 2) .sub (vf 1)›) (.normalize))] ;! "compute normal" move to function
            ;`        (doseq [v face]
            ;`          (gl,normal-3d ‹n .x› ‹n .y› ‹n .z›)
            ;`          ;(gl,tex-coord-2f 1.0 1.0)
            ;`          (gl,color-3d 0.2 0.2 0.2)
            ;`          (gl,vertex-3d ‹v .x› ‹v .y› ‹v .z›)
            ;`          (gl,tex-coord-2f 0.0 1.0)))
            ;`  (gl,end))
            ;`(gl,pop-matrix)
            ;`(gl,enable :lighting)

            (gl,pop-matrix)
            )
          (gl,disable :lighting)
          
          ; draw shadows
          ;(gl,mult-matrix (buf (shadow-projection-matrix (Vector3. 75 350 -75) (Vector3. 0 -20 0) (Vector3. 0 -1 0))))
          ;(gl,color-3d 0.85 0.85 0.85)
          ;(doseq [shape @to-draw]
          ;  (gl,push-matrix)
          ;    (gl,mult-matrix (-> shape (.getTransform) (.toArray) (buf)))
          ;    (gl,polygon-mode :front-and-back :fill)
          ;    (doseq [face (iterator-seq ‹shape .getFaces›)]
          ;      (gl,begin :polygon)
          ;      (doseq [v face]
          ;        (gl,vertex-3d ‹v .x› ‹v .y› ‹v .z›))
          ;      (gl,end))
          ;  (gl,pop-matrix))
        (gl,pop-matrix)
        )
    )
  )
;;;
(run)