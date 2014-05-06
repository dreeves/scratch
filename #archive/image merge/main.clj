(ns main)
(use 'clojure.java.io)

(defn px[a r g b] (bit-or (bit-shift-left a 24) (bit-shift-left r 16) (bit-shift-left g 8) (bit-shift-left b 0)))
(defn split[i] [(bit-and (bit-shift-right i 16) 0xff) (bit-and (bit-shift-right i 8) 0xff) (bit-and (bit-shift-right i 0) 0xff)])
(defn alpha[i] (bit-and (bit-shift-right i 24) 0xff))

(defn read-image[v] (javax.imageio.ImageIO/read (file v)))
(defn write-image[a b] (javax.imageio.ImageIO/write a "png" (file b)))

(def ia (read-image "a.png"))
(def ib (read-image "b.png"))
(def ic (java.awt.image.BufferedImage. 128 128 java.awt.image.BufferedImage/TYPE_INT_ARGB))

(dotimes [x 128] (dotimes [y 128] (let [a (alpha (. ia getRGB x y)) [r g b] (split (. ib getRGB x y))] (. ic setRGB x y (px a r g b)))))

(write-image ic "c.png")