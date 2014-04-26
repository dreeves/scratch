(ns artificer)
(require '[clojure.string :as str])
(use 'batteries)

(use '[gl :only (gl buf0 buf unbuf)])
(require 'run)

; skipped: mipmaps
; bug: window blinks in at upper-left sometimes

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; misc ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(defn r[] (eval '(d (use 'artificer :reload-all) (main))))

(defn sys-time[] ‹(double (System/nanoTime)) / 1000000000›)
(def load-time (sys-time))
(defn app-time[] ‹(sys-time) - load-time›)

(require '[clojure.java.io :as io])
(defn read-image[file] (javax.imageio.ImageIO/read (io/file file)))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; gl misc ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(defn make-buffer[target data usage] (gl
	id ← (glGenBuffers)
	(glBindBuffer target id)
	(glBufferData target data usage)
	id))
(defn make-texture[file] (gl
	img ← (read-image file)
	[w h] ← [(. img getWidth) (. img getHeight)]
	data ← (buf (. img getRGB 0 0 w h nil 0 w))
	id ← (glGenTextures)
	(glBindTexture TEXTURE_2D id)
    (glTexParameteri TEXTURE_2D TEXTURE_MIN_FILTER LINEAR) ; options: NEAREST, LINEAR, mipmap stuff
    (glTexParameteri TEXTURE_2D TEXTURE_MAG_FILTER LINEAR)
    (glTexParameteri TEXTURE_2D TEXTURE_WRAP_S CLAMP_TO_EDGE) ; options: CLAMP_TO_EDGE, REPEAT
    (glTexParameteri TEXTURE_2D TEXTURE_WRAP_T CLAMP_TO_EDGE)
    (glTexImage2D TEXTURE_2D 0 RGBA8 w h 0 BGRA UNSIGNED_BYTE data)
    id))
(defn make-shader[type code] (gl
	id ← (glCreateShader type)
	(glShaderSource id code)
	(glCompileShader id)
	(if (= (glGetShaderi id COMPILE_STATUS) FALSE)
		(d	(println "shader compilation error")
			(println (glGetShaderInfoLog id 8192))
			(glDeleteShader id)
			0)
		id)))
(defn make-program[vertex-shader fragment-shader with] (gl
	id ← (glCreateProgram)
	(glAttachShader id vertex-shader)
	(glAttachShader id fragment-shader)
	(if with (with id))
	(glLinkProgram id)
	(if (= (glGetProgrami id LINK_STATUS) FALSE) (d
		(println "program linking error")
		(println (glGetProgramInfoLog id 8192))
		(glDeleteProgram id)
		))
	(glValidateProgram id)
	(if (= (glGetProgrami id VALIDATE_STATUS) FALSE) (d
		(println "program validation error")
		(println (glGetProgramInfoLog id 8192))
		(glDeleteProgram id)
		))
	id))

(def vertex-glsl (str/join "\n" [
	"#version 410 core"
	"in vec4 in_Position;"
	"in vec4 in_Color;"
	"in vec2 in_TextureCoord;"
	"out vec4 pass_Color;"
	"out vec2 pass_TextureCoord;"
	"void main(void) {"
	"	gl_Position = in_Position;"
	"	pass_Color = in_Color;"
	"	pass_TextureCoord = in_TextureCoord;"
	"}"
	]))
(def fragment-glsl (str/join "\n" [
	"#version 410 core"
	"uniform sampler2D texture_diffuse;"
	"in vec4 pass_Color;"
	"in vec2 pass_TextureCoord;"
	"out vec4 out_Color;"
	"void main(void) {"
	"	out_Color = pass_Color * texture(texture_diffuse, pass_TextureCoord);"
	"}"
	]))

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; <edge> ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;

(def sin-factor (atom 0))

(def vaoId)
(def vboId)
(def vboiId)
(def indicesCount 6)

(def pId)

(def texIds)

(defn init-quad[] (gl
	vertices ← (buf (float-array (map float [
		;x  y z w   r g b a   s t
		-1  1 0 1   1 0 0 1   0 0
		-1 -1 0 1   0 1 0 1   0 1
		 1 -1 0 1   0 0 1 1   1 1
		 1  1 0 1   1 0 1 1   1 0
		])))
	(def vaoId (glGenVertexArrays))
	(glBindVertexArray vaoId)
	(def vboId (make-buffer ARRAY_BUFFER vertices STATIC_DRAW))
	(glVertexAttribPointer 0 4 FLOAT false ‹‹4 + 4 + 2› * 4› 0) ; position
	(glVertexAttribPointer 1 4 FLOAT false ‹‹4 + 4 + 2› * 4› ‹0 + ‹4 * 4››) ; color
	(glVertexAttribPointer 2 2 FLOAT false ‹‹4 + 4 + 2› * 4› ‹0 + ‹4 * 4› + ‹4 * 4››) ; texcoord

	indices ← (buf (byte-array (map byte [0 1 2   2 3 0])))
	(def vboiId (make-buffer ELEMENT_ARRAY_BUFFER indices STATIC_DRAW))
	))
(defn init[] (gl
	(init-quad)
	(def pId (make-program (make-shader VERTEX_SHADER vertex-glsl) (make-shader FRAGMENT_SHADER fragment-glsl) (λ[id]
		(glBindAttribLocation id 0 "in_Position")
		(glBindAttribLocation id 1 "in_Color")
		(glBindAttribLocation id 2 "in_TextureCoord")
		)))
	(def texIds [(make-texture "res/stGrid1.png") (make-texture "res/stGrid2.png")])
	))

(defn draw[] (gl
	(glUseProgram pId)

	(glActiveTexture TEXTURE0)
	(glBindTexture TEXTURE_2D (texIds (int ‹@sin-factor + 0.2›)))

	(glBindVertexArray vaoId)
	(glEnableVertexAttribArray 0)
	(glEnableVertexAttribArray 1)
	(glEnableVertexAttribArray 2)

	(glBindBuffer ELEMENT_ARRAY_BUFFER vboiId)

	(glDrawElements TRIANGLES indicesCount UNSIGNED_BYTE 0)

	(glBindBuffer ELEMENT_ARRAY_BUFFER 0)
	(glDisableVertexAttribArray 0)
	(glDisableVertexAttribArray 1)
	(glDisableVertexAttribArray 2)
	(glBindVertexArray 0)

	(glUseProgram 0)
	))

(defn main[] (d
	(if →sin-fn (run/cancel sin-fn))
	(def sin-fn (run/every 0.01 #(reset! sin-factor ‹‹(Math/sin ‹(app-time) * 5›) / 2› + 0.5›)))
	(gl/window :size [320 320] :title "@_@" :init init :draw draw)
	))

;(def vertex-glsl (str/join "\n" [
;	"#version 110"
;	"attribute vec2 position;"
;	"varying vec2 texcoord;"
;	"void main() {"
;	"    gl_Position = vec4(position, 0.0, 1.0);"
;	"    texcoord = vec2(position.x,-position.y) * vec2(0.5) + vec2(0.5);"
;	"}"
;	]))
;(def fragment-glsl (str/join "\n" [
;	"#version 110"
;	"uniform float fade_factor;"
;	"uniform sampler2D textures[2];"
;	"varying vec2 texcoord;"
;	"void main() {"
;	"    gl_FragColor = mix("
;	"        texture2D(textures[0], texcoord),"
;	"        texture2D(textures[1], texcoord),"
;	"        fade_factor);"
;	"}"
;	]))
;
;;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~; <edge> ;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;
;
;; buffer
;(def g-resources-vertex-buffer-id (atom 0))
;(def g-resources-element-buffer-id (atom 0))
;; texture
;(def g-resources-texture-ids (atom [0 0]))
;; shader
;(def g-resources-vertex-shader (atom 0))
;(def g-resources-fragment-shader (atom 0))
;(def g-resources-program (atom 0))
;(def g-resources-uniforms-fade-factor (atom 0))
;(def g-resources-uniforms-textures (atom [0 0]))
;(def g-resources-attributes-position (atom 0))
;(def g-resources-fade-factor (atom 0.0))
;
;(def g-vertex-buffer-data (buf (float-array [-1 -1, 1 -1, 1 1, -1 1])))
;;(def g-vertex-buffer-data (buf (float-array [0 0, 400 0, 400 300, 0 300])))
;(def g-element-buffer-data (buf (short-array (map short [0 1 2 3]))))
;
;(defn init[] (gl
;	; buffers
;	(reset! g-resources-vertex-buffer-id (make-buffer ARRAY_BUFFER g-vertex-buffer-data))
;	(reset! g-resources-element-buffer-id (make-buffer ELEMENT_ARRAY_BUFFER g-element-buffer-data))
;
;	; textures
;	(reset! g-resources-texture-ids [(make-texture "res/hello1.png") (make-texture "res/hello2.png")])
;
;	; shaders
;	(reset! g-resources-vertex-shader (make-shader VERTEX_SHADER vertex-glsl))
;	(reset! g-resources-fragment-shader (make-shader FRAGMENT_SHADER fragment-glsl))
;	(reset! g-resources-program (make-program @g-resources-vertex-shader @g-resources-fragment-shader nil))
;	(reset! g-resources-uniforms-textures [(glGetUniformLocation @g-resources-program "textures[0]")
;										   (glGetUniformLocation @g-resources-program "textures[1]")])
;	(reset! g-resources-attributes-position (glGetUniformLocation @g-resources-program "position"))
;	))
;
;(defn draw[] (gl
;	(glUseProgram @g-resources-program)
;
;	(glUniform1f @g-resources-uniforms-fade-factor @g-resources-fade-factor)
;
;	(glActiveTexture TEXTURE0)
;	(glBindTexture TEXTURE_2D (@g-resources-texture-ids 0))
;	(glUniform1i (@g-resources-uniforms-textures 0) 0)
;	(glActiveTexture TEXTURE1)
;	(glBindTexture TEXTURE_2D (@g-resources-texture-ids 1))
;	(glUniform1i (@g-resources-uniforms-textures 1) 1)
;
;	;(glEnable TEXTURE_2D)
;	;(glBindTexture TEXTURE_2D (@g-resources-texture-ids 0))
;	(glBegin QUADS)
;		(glVertex2f -1  -1)
;		(glVertex2f  1  -1)
;		(glVertex2f  1   1)
;		(glVertex2f -1   1)
;	(glEnd)
;	;(glColor4f 1 1 1 @g-resources-fade-factor)
;	;(glBindTexture TEXTURE_2D (@g-resources-texture-ids 1))
;	;(glPushMatrix)
;	;	(glBegin QUADS)
;	;		(glTexCoord2f 0 0) (glVertex2f  400 -300)
;	;		(glTexCoord2f 1 0) (glVertex2f -400 -300)
;	;		(glTexCoord2f 1 1) (glVertex2f -400  300)
;	;		(glTexCoord2f 0 1) (glVertex2f  400  300)
;	;	(glEnd)
;	;(glPopMatrix)
;
;	;(glBindBuffer ARRAY_BUFFER @g-resources-vertex-buffer-id)
;	;(glVertexAttribPointer @g-resources-attributes-position 2 FLOAT false ‹4 * 2› 0)
;	;(glEnableVertexAttribArray @g-resources-attributes-position)
;
;	;(glBindBuffer ELEMENT_ARRAY_BUFFER @g-resources-element-buffer-id)
;	;(glDrawElements TRIANGLE_STRIP 4 UNSIGNED_SHORT 0)
;
;	;(glDisableVertexAttribArray @g-resources-attributes-position)
;
;	;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~;
;
;	;(glUseProgram @g-resources-program)
;
;	;(glUniform1f @g-resources-uniforms-fade-factor @g-resources-fade-factor)
;
;	;(glActiveTexture TEXTURE0)
;	;(glBindTexture TEXTURE_2D (@g-resources-texture-ids 0))
;	;(glUniform1i (@g-resources-uniforms-textures 0) 0)
;	;(glActiveTexture TEXTURE1)
;	;(glBindTexture TEXTURE_2D (@g-resources-texture-ids 1))
;	;(glUniform1i (@g-resources-uniforms-textures 1) 1)
;
;	;(glBindBuffer ARRAY_BUFFER @g-resources-vertex-buffer-id)
;	;(glVertexAttribPointer @g-resources-attributes-position 2 FLOAT false ‹4 * 2› 0)
;	;(glEnableVertexAttribArray @g-resources-attributes-position)
;
;	;(glBindBuffer ELEMENT_ARRAY_BUFFER @g-resources-element-buffer-id)
;	;(glDrawElements TRIANGLE_STRIP 4 UNSIGNED_SHORT 0)
;
;	;(glDisableVertexAttribArray @g-resources-attributes-position)
;
;	(glUseProgram 0)
;	))
;
;(defn main[] (gl
;	(def app-token (Object.)) ;! horrible hack
;	t ← app-token
;	(run-every 0.01 (λ[] (if ‹app-token ≡ t› (reset! g-resources-fade-factor ‹‹(Math/sin ‹(app-time) * 5›) / 2› + 0.5›))))
;	(gl/window :size [400 300] :title "@_@"
;		:init (λ[]
;			(make-resources)
;			)
;		:draw (λ[]
;			(glClear ‹COLOR_BUFFER_BIT bit| DEPTH_BUFFER_BIT›)
;			(render)
;			))
;	))