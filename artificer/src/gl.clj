(ns gl)
(require '[clojure.string :as str])
(use 'batteries)

(use '[clojure.walk :only (postwalk)])
(require 'run)

(System/setProperty "org.lwjgl.input.Mouse.allowNegativeMouseCoords" "true")

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; swing utils ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

;- (defn intersect-1d [[x x2] [x' x2']] ‹‹x ≤ x2'› and ‹x' ≤ x2››)
;- (defn intersect-2d [[x x2 y y2] [x' x2' y' y2']] ‹(intersect-1d [x y] [x' y']) and (intersect-1d [x2 y2] [x2' y2'])›)

(defn screen-size[] (d v ← (. (java.awt.Toolkit/getDefaultToolkit) getScreenSize) [(. v width) (. v height)]))
(defn _inset-borders [v] (d v ← (. v getInsets) [‹(. v right) + (. v left)› ‹(. v top) + (. v bottom)›]))
(defn _inset-topleft [v] (d v ← (. v getInsets) [(. v left) (. v top)]))
(defn inner-size [container] ‹[(. container getWidth) (. container getHeight)] -+ (_inset-borders container)›)
(defn inner-size! [container size] (d [w h] ← ‹size ++ (_inset-borders container)› (. container setSize w h)))
(defn center-window-on-screen! [window] (d
	[x y] ← ‹‹(screen-size) D+ 2› -+ ‹(inner-size window) D+ 2› -+ (_inset-topleft window)›
	(. window setLocation x y)))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; gl macro ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

; would really prefer to just hack the clojure symbol resolution process

; private
(def gl-containers (concat
	(map #(eval (symbol (str "org.lwjgl.opengl."%))) '[GL20 GL15 GL14 GL13 GL12 GL11 GL30 GL31 GL32 GL33 GL40 GL41 GL42 GL43 APPLEFloatPixels ARBDrawBuffers ARBFramebufferObject ARBHalfFloatPixel ARBTextureFloat ARBTextureRectangle ATITextureFloat EXTFramebufferObject EXTGeometryShader4 EXTTextureRectangle EXTTransformFeedback NVFloatBuffer])
	(map #(eval (symbol (str "org.lwjgl.util.glu."%))) '[GLU Project Registry MipMap])
	))
(def gl-sym (into {} (map #(d s ← (. % getName) [s (symbol (. (. % getDeclaringClass) getName) s)]) (concat (mapcat #(concat (. % getFields) (. % getMethods)) gl-containers))))) ; name=name implies value=value
(defn gl-resolve[v] (if ‹(symbol? v) and (¬ (. v getNamespace))› (d s ← (name v) ‹(gl-sym s) or (gl-sym (str "GL_"s)) or v›) v))

; public
(defmacro gl[& …] `(d ~@(postwalk gl-resolve …)))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; buffers ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

; private
(def primitive-type-table [
	[:byte   java.nio.ByteBuffer   #(org.lwjgl.BufferUtils/createByteBuffer   %) (Class/forName "[B") byte-array  ]
	[:char   java.nio.CharBuffer   #(org.lwjgl.BufferUtils/createCharBuffer   %) (Class/forName "[C") char-array  ]
	[:short  java.nio.ShortBuffer  #(org.lwjgl.BufferUtils/createShortBuffer  %) (Class/forName "[S") short-array ]
	[:int    java.nio.IntBuffer    #(org.lwjgl.BufferUtils/createIntBuffer    %) (Class/forName "[I") int-array   ]
	[:long   java.nio.LongBuffer   #(org.lwjgl.BufferUtils/createLongBuffer   %) (Class/forName "[J") long-array  ]
	[:float  java.nio.FloatBuffer  #(org.lwjgl.BufferUtils/createFloatBuffer  %) (Class/forName "[F") float-array ]
	[:double java.nio.DoubleBuffer #(org.lwjgl.BufferUtils/createDoubleBuffer %) (Class/forName "[D") double-array]])
(def arr-class-to-buf-ctor (into {} (map #‹[(% 3) (% 2)]› primitive-type-table)))
(def name-to-buf-ctor      (into {} (map #‹[(% 0) (% 2)]› primitive-type-table)))
(def buf-class-to-arr-ctor (into {} (map #‹[(% 1) (% 4)]› primitive-type-table)))

; api
(defn buf0[type len] ((name-to-buf-ctor type) len))
(defn buf[array] (doto ((arr-class-to-buf-ctor (class array)) (count array)) (.put array) (.rewind)))
(defn unbuf[buf] (d r ← ((buf-class-to-arr-ctor (class buf)) (. buf capacity)) (. buf get r) r))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; window private ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(defn base-init[] (gl
	(glEnable BLEND)
	(glBlendFunc SRC_ALPHA ONE_MINUS_SRC_ALPHA) ; "standard transparency blending function"
	(glDepthFunc LEQUAL)
	))
(defn pre-draw[] (gl
	(if (get-set! →window-size-dirty false) (d
		(glViewport 0 0 (→X) (→Y))
		;(@→projection-current)
		))
	
	(glClear ‹COLOR_BUFFER_BIT bit| DEPTH_BUFFER_BIT›)
	))
(defn post-draw[]
	(org.lwjgl.opengl.Display/update false)
	(org.lwjgl.opengl.Display/processMessages) ; separated from call to org.lwjgl.opengl.Display/update because the idea was to allow for faster keyboard polling than main rendering fps
	;- ukuku.gfx.KeyIn.poll()
	;- ukuku.gfx.Mice.poll()
	;- ukuku.gfx.KeyIn.flushEventQueue()
	)

(def window-size (atom [0 0]))
(def window-size-dirty (atom false))
(defn X[] (@window-size 0))
(defn Y[] (@window-size 1))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; window api ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(defn title[v] (. →window-jframe setTitle (str v)))

(defn window[& {:keys [init draw size title]}] (d init ← ‹init or #()› draw ← ‹draw or #()› size ← ‹size or [800 600]› title ← ‹title or "gl app"›
	(if →gl-thread (d
		(reset! →gl-quit true)
		(. window-jframe dispose)
		(. gl-thread join)
		))
	
	(def window-size (atom size))
	(def gl-quit (atom false))
	gl-canvas ← (java.awt.Canvas.)
	(def gl-thread (d
		t ← (Thread.
			#(if (¬ @gl-quit)
				(try
					(org.lwjgl.opengl.Display/setParent gl-canvas)
					(org.lwjgl.opengl.Display/create (org.lwjgl.opengl.PixelFormat.) (. (. (org.lwjgl.opengl.ContextAttribs. 4 2) withForwardCompatible true) withProfileCore true))
					(base-init) (init)
					;! implement fps limit and counter and stuff with better stuff later ; e.g. the while loop right there would probably be better less imperative
					(while (not @gl-quit)
						(pre-draw) (draw) (post-draw))
				(finally
					(org.lwjgl.opengl.Display/destroy)
					(run/in 0 #(. window-jframe dispose))
					))
				))
		(. t setName "opengl")
		(. t setDaemon true)
		t))
	(def window-jframe (d
		t ← (proxy [javax.swing.JFrame] [title] (dispose[] (reset! gl-quit true) (. gl-thread join) (proxy-super dispose)))
		(. t setDefaultCloseOperation javax.swing.JFrame/DISPOSE_ON_CLOSE)
		(. t add gl-canvas)
		; can't size a jframe correctly until the OS has given us insets for it but do want to size before showing: so we do it twice
		(inner-size! t @window-size) (center-window-on-screen! t)
		(. t setVisible true)
		(inner-size! t @window-size) (center-window-on-screen! t)
		; now that we're done initializing the jframe's size
		(. t addComponentListener
			(proxy [java.awt.event.ComponentAdapter] []
				(componentResized[event] (reset! window-size (inner-size t)) (reset! window-size-dirty true))))
		t))
	(. gl-thread start)
	nil))