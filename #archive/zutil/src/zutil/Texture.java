package zutil;

public class Texture {
	public int id; // The GL texture ID
	public final int height; // The height of the image
	public final int width; // The width of the image
	public final Object gl_dims; // the POT dims of the texture
	public final int depth;
	
	public Texture(int id, int height, int width, Object gl_dims, int depth) {
		this.id          = id         ;
		this.height      = height     ;
		this.width       = width      ;
		this.gl_dims  = gl_dims ;
		this.depth       = depth      ;
	}
}