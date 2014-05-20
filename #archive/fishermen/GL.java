package fishermen;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;

public enum GL
{
	PERSPECTIVE_CORRECTION_HINT(GL11.GL_PERSPECTIVE_CORRECTION_HINT),
	POINT_SMOOTH_HINT          (GL11.GL_POINT_SMOOTH_HINT          ),
	LINE_SMOOTH_HINT           (GL11.GL_LINE_SMOOTH_HINT           ),
	POLYGON_SMOOTH_HINT        (GL11.GL_POLYGON_SMOOTH_HINT        ),
	FOG_HINT                   (GL11.GL_FOG_HINT                   ),
	
	NICEST   (GL11.GL_NICEST   ),
	DONT_CARE(GL11.GL_DONT_CARE),
	FASTEST  (GL11.GL_FASTEST  ),
	
	POINTS        (GL11.GL_POINTS        ),
	LINES         (GL11.GL_LINES         ),
	LINE_STRIP    (GL11.GL_LINE_STRIP    ),
	LINE_LOOP     (GL11.GL_LINE_LOOP     ),
	TRIANGLES     (GL11.GL_TRIANGLES     ),
	TRIANGLE_STRIP(GL11.GL_TRIANGLE_STRIP),
	TRIANGLE_FAN  (GL11.GL_TRIANGLE_FAN  ),
	QUADS         (GL11.GL_QUADS         ),
	QUAD_STRIP    (GL11.GL_QUAD_STRIP    ),
	POLYGON       (GL11.GL_POLYGON       ),
	
	NEAREST              (GL11.GL_NEAREST              ),
	LINEAR               (GL11.GL_LINEAR               ),
	LINEAR_MIPMAP_NEAREST(GL11.GL_LINEAR_MIPMAP_NEAREST),
	
	COLOR_ARRAY          (GL11.GL_COLOR_ARRAY          ),
	EDGE_FLAG_ARRAY      (GL11.GL_EDGE_FLAG_ARRAY      ),
	FOG_COORD_ARRAY      (GL15.GL_FOG_COORD_ARRAY      ),
	INDEX_ARRAY          (GL11.GL_INDEX_ARRAY          ), //for (en)(dis)ableClientState
	NORMAL_ARRAY         (GL11.GL_NORMAL_ARRAY         ),
	SECONDARY_COLOR_ARRAY(GL14.GL_SECONDARY_COLOR_ARRAY),
	TEXTURE_COORD_ARRAY  (GL11.GL_TEXTURE_COORD_ARRAY  ),
	VERTEX_ARRAY         (GL11.GL_VERTEX_ARRAY         );
	
	public final int id;
	GL(int id) {this.id = id;}
	
//////////////////////////////////////////////////////////////// not-enum part ////////////////////////////////////////////////////////////////

	public static void loadIdentity() {GL11.glLoadIdentity();}
	public static void pushMatrix() {GL11.glPushMatrix();}
	public static void popMatrix() {GL11.glPopMatrix();}
	
	public static void enable(int capability) {GL11.glEnable(capability);}
	public static void disable(int capability) {GL11.glDisable(capability);}
	public static void enableClientState(GL cap) {GL11.glEnableClientState(cap.id);}
	public static void disableClientState(GL cap) {GL11.glDisableClientState(cap.id);}
	
	public static void begin(GL mode) {GL11.glBegin(mode.id);}
	public static void end() {GL11.glEnd();}
	
	//public static void callList(DisplayList dl) {GL11.glCallList(dl.address);}
	//public static void callLists(DisplayList... dls) {for (DisplayList dl : dls) GL11.glCallList(dl.address);}
	
	public static void hint(GL target, GL hint) {GL11.glHint(target.id, hint.id);}
	
	public static void bindTexture2D(Texture tex) {GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.address);}
	
	public static void translate(float x, float y, float z) {GL11.glTranslatef(x, y, z);}
	public static void translate(double x, double y, double z) {GL11.glTranslated(x, y, z);}
	public static void rotate(float angle, float x, float y, float z) {GL11.glRotatef(angle, x, y, z);}
	
	public static void color3f(float r, float g, float b) {GL11.glColor3f(r, g, b);}
	public static void color4f(float r, float g, float b, float a) {GL11.glColor4f(r, g, b, a);}
	public static void vertex3f(float x, float y, float z) {GL11.glVertex3f(x, y, z);}
	public static void texCoord2f(float u, float v) {GL11.glTexCoord2f(u, v);}
	
	public static void beginOrtho()
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL.pushMatrix();
		GL.loadIdentity();
		GL11.glOrtho(0, M.width, 0, M.height, -1, 1); //Creates a new orthographic viewing volume
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL.pushMatrix();
		GL.loadIdentity();
	}

	public static void endOrtho()
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL.popMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL.popMatrix();
	}
}

/*class DisplayList
{
	public final int address;
	public DisplayList(int address) {this.address = address;}
	public DisplayList() {address = GL11.glGenLists(1); S.claim(address != 0);}
	public void delete() {GL11.glDeleteLists(address, 1);}
}*/