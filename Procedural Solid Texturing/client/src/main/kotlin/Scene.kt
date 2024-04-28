import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL //# GL# we need this for the constants declared ˙HUN˙ a constansok miatt kell
import kotlin.js.Date
import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec1
import vision.gears.webglmath.Vec2
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4
import vision.gears.webglmath.Mat4
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos

class Scene (
  val gl : WebGL2RenderingContext)  : UniformProvider("scene") {

  val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
  val vsQuad = Shader(gl, GL.VERTEX_SHADER, "quad-vs.glsl")
  val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")
  val fsBackground = Shader(gl, GL.FRAGMENT_SHADER, "background-fs.glsl")

  val texturedProgram = Program(gl, vsTextured, fsTextured)
  
  val texturedQuadGeometry = TexturedQuadGeometry(gl)

  val gameObjects = ArrayList<GameObject>()

  val jsonLoader = JsonLoader()

  val shadowMatrix by Mat4()
  val shadowColor by Vec3()

  val sphereMeshes = jsonLoader.loadMeshes(gl,
    "media/sphere/sphere.json",
    Material(texturedProgram).apply{
    this["lightWoodColor"]?.set(0.8f, 0.7f, 0.4f);
    this["darkWoodColor"]?.set(0.4f, 0.26f, 0.14f);
    this["freq"]?.set(7.0f);
    this["noiseFreq"]?.set(20.0f);
    this["noiseExp"]?.set(1.5f);
    this["noiseAmp"]?.set(20.0f);
    }
  )

  init{
    gameObjects += GameObject(*sphereMeshes)
  }


  // LABTODO: replace with 3D camera
  val camera = PerspectiveCamera().apply{
    update()
  }

  fun resize(canvas : HTMLCanvasElement) {
    gl.viewport(0, 0, canvas.width, canvas.height)//#viewport# tell the rasterizer which part of the canvas to draw to ˙HUN˙ a raszterizáló ide rajzoljon
    camera.setAspectRatio(canvas.width.toFloat()/canvas.height)
  }

  val timeAtFirstFrame = Date().getTime()
  var timeAtLastFrame =  timeAtFirstFrame

  init{
    //LABTODO: enable depth test
    gl.enable(GL.DEPTH_TEST)
  }

  @Suppress("UNUSED_PARAMETER")
  fun update(keysPressed : Set<String>) {
    val timeAtThisFrame = Date().getTime() 
    val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
    val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
    timeAtLastFrame = timeAtThisFrame

    //LABTODO: move camera
    camera.move(dt, keysPressed)
    
    gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)//## red, green, blue, alpha in [0, 1]
    gl.clearDepth(1.0f)//## will be useful in 3D ˙HUN˙ 3D-ben lesz hasznos
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)//#or# bitwise OR of flags

    gl.enable(GL.BLEND)
    gl.blendFunc(
      GL.SRC_ALPHA,
      GL.ONE_MINUS_SRC_ALPHA)

    shadowMatrix.set()
    shadowColor.set(1f, 1f, 1f)

    gameObjects.forEach{ it.move(dt, t, keysPressed, gameObjects) }

    gameObjects.forEach{ it.update() }
    gameObjects.forEach{ it.draw(this, camera) }
    shadowColor.set(0f, 0f, 0f)
    shadowMatrix.set(
      1f, 0f, 0f, 0f, 
      0f, 0f, 0f, 0f, 
      0f, 0f, 1f, 0f,
      0f, 0f, 0f, 1f)
    gameObjects.forEach{ it.draw(this, camera) }
  }
}
