(ns bullet
  (:refer-clojure :exclude [print file-seq])
  (:use lust)

  (:require shiny)
  (:use penbra.opengl.core)
  
  (:import com.bulletphysics.util.ObjectArrayList)
  (:import com.bulletphysics.collision.broadphase.BroadphaseInterface)
  (:import com.bulletphysics.collision.broadphase.DbvtBroadphase)
  (:import com.bulletphysics.collision.dispatch.CollisionDispatcher)
  (:import com.bulletphysics.collision.dispatch.CollisionObject)
  (:import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration)
  (:import com.bulletphysics.collision.shapes.BoxShape)
  (:import com.bulletphysics.collision.shapes.BvhTriangleMeshShape)
  (:import com.bulletphysics.collision.shapes.CollisionShape)
  (:import com.bulletphysics.collision.shapes.CompoundShape)
  (:import com.bulletphysics.collision.shapes.CylinderShapeX)
  (:import com.bulletphysics.collision.shapes.TriangleIndexVertexArray)
  (:import com.bulletphysics.dynamics.DiscreteDynamicsWorld)
  (:import com.bulletphysics.dynamics.RigidBody)
  (:import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver)
  (:import com.bulletphysics.dynamics.constraintsolver.HingeConstraint)
  (:import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver)
  (:import com.bulletphysics.dynamics.constraintsolver.SliderConstraint)
  (:import com.bulletphysics.dynamics.vehicle.DefaultVehicleRaycaster)
  (:import com.bulletphysics.dynamics.vehicle.RaycastVehicle)
  (:import com.bulletphysics.dynamics.vehicle.VehicleRaycaster)
  (:import com.bulletphysics.dynamics.vehicle.VehicleTuning)
  (:import com.bulletphysics.dynamics.vehicle.WheelInfo)
  (:import com.bulletphysics.linearmath.DebugDrawModes)
  (:import com.bulletphysics.linearmath.MatrixUtil)
  (:import com.bulletphysics.linearmath.Transform)
  (:import java.awt.event.KeyEvent)
  (:import java.nio.ByteBuffer)
  (:import java.nio.ByteOrder)
  (:import javax.vecmath.Vector3f)
  (:import org.lwjgl.LWJGLException)
  (:import org.lwjgl.input.Keyboard)
  (:import org.lwjgl.input.Mouse)
  (:import org.lwjgl.opengl.Display)
  (:import org.lwjgl.opengl.DisplayMode)
  (:import org.lwjgl.opengl.PixelFormat)
  )
;;;
(dool
;;;
(let dims [800 600])

(defn mouse-event-x [] (Mouse/getEventX))
(defn mouse-event-y [] ‹(dims 1) - 1 - (Mouse/getEventY)›)

(zutil.Bullet/initPhysics)

(shiny/run
  :dims dims
  :init
    (fn []
      (shiny/init-fn)
      
      (gl,enable :lighting)
      (gl,enable :light0)
      (gl,enable :light1)

      (gl,shade-model :smooth)
      (gl,clear-color 0.7 0.7 0.7 0)

      (gl,light :light0 :ambient  (buf (float-array [ 0.2  0.2  0.2  1.0])))
      (gl,light :light0 :diffuse  (buf (float-array [ 1.0  1.0  1.0  1.0])))
      (gl,light :light0 :specular (buf (float-array [ 1.0  1.0  1.0  1.0])))
      (gl,light :light0 :position (buf (float-array [ 1.0 10.0  1.0  0.0])))

      (gl,light :light1 :ambient  (buf (float-array [ 0.2   0.2  0.2  1.0])))
      (gl,light :light1 :diffuse  (buf (float-array [ 1.0   1.0  1.0  1.0])))
      (gl,light :light1 :specular (buf (float-array [ 1.0   1.0  1.0  1.0])))
      (gl,light :light1 :position (buf (float-array [-1.0 -10.0 -1.0  0.0])))

      (set! (zutil.Bullet/glutScreenWidth ) (dims 0))
      (set! (zutil.Bullet/glutScreenHeight) (dims 1))
      (zutil.Bullet/updateCamera)
      )
  :draw
    #(dool
      (shiny/draw-fn)
      
      (zutil.Bullet/do_move)
      (-?> zutil.Bullet/dynamicsWorld (.stepSimulation ‹(zutil.Bullet/getDeltaTimeMicroseconds) / 1000000›))
      (zutil.Bullet/renderme)
      (let mods (bit|
                  (if ‹(Keyboard/isKeyDown Keyboard/KEY_LSHIFT  ) or (Keyboard/isKeyDown Keyboard/KEY_RSHIFT  )› KeyEvent/SHIFT_DOWN_MASK 0)
                  (if ‹(Keyboard/isKeyDown Keyboard/KEY_LCONTROL) or (Keyboard/isKeyDown Keyboard/KEY_RCONTROL)› KeyEvent/CTRL_DOWN_MASK  0)
                  (if ‹(Keyboard/isKeyDown Keyboard/KEY_LMETA   ) or (Keyboard/isKeyDown Keyboard/KEY_RMETA   )› KeyEvent/ALT_DOWN_MASK   0)))
      (while (Keyboard/next)
        (if (Keyboard/getEventKeyState)
          (zutil.Bullet/specialKeyboard (Keyboard/getEventKey) (Mouse/getX) (Mouse/getY) mods))
        )
      (while (Mouse/next)
        (if ‹(Mouse/getEventButton) !== -1›
          (zutil.Bullet/mouseFunc
            (#(case %, 1 2, 2 1, %) (Mouse/getEventButton))
            (if (Mouse/getEventButtonState) 0 1)
            (mouse-event-x)
            (mouse-event-y)
            ))
        (zutil.Bullet/mouseMotionFunc
            (mouse-event-x)
            (mouse-event-y)
          ))
      )
  )
)