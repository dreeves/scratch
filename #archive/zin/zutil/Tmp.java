package zutil;

import jin.geometry.Box;
import jin.geometry.ConvexHull;
import jin.geometry.Geometry;
import jin.geometry.UniformCapsule;
import jin.math.*;
import jin.physics.Body;
import java.util.*;

public class Tmp {
/*
static public Object launch_gl_f;
static public jin_ex.Rendering launch_gl(Object a, Object b) {return (jin_ex.Rendering)((clojure.lang.IFn)launch_gl_f).invoke(a, b);}
//static public jin_ex.Rendering launch_gl(Object a, Object b) {return new Jogljin_ex.Rendering((jin_ex.Rendering.Callback)a, (jin_ex.Rendering.EventCallback)b);}
*/
static public interface DrawShape {
	public Iterator<Vector3[]> getFaces();
	public Matrix4 getTransform();
	public Body getReferenceBody();
}
static public Object getDrawShape(final Geometry g) {
	if (g instanceof ConvexHull) {
		return new DrawShape() {
			public Iterator<Vector3[]> getFaces() {return ((ConvexHull)g).getFaces();}
			public Matrix4 getTransform() {return g.getTransform();}
			public Body getReferenceBody() {return g.getBody();}
		};
	} else if (g instanceof Box) {
		final List<Vector3> vertices = new ArrayList<Vector3>();
		vertices.add( new Vector3(  0.5,  0.5,  0.5));
		vertices.add( new Vector3( -0.5,  0.5,  0.5));
		vertices.add( new Vector3(  0.5, -0.5,  0.5));
		vertices.add( new Vector3( -0.5, -0.5,  0.5));
		vertices.add( new Vector3(  0.5,  0.5, -0.5));
		vertices.add( new Vector3( -0.5,  0.5, -0.5));
		vertices.add( new Vector3(  0.5, -0.5, -0.5));
		vertices.add( new Vector3( -0.5, -0.5, -0.5));
		return new DrawShape() {
			public Iterator<Vector3[]> getFaces() {return new ConvexHull(vertices).getFaces();}
			public Matrix4 getTransform() {return g.getTransform();}
			public Body getReferenceBody() {return g.getBody();}
		};
	} else if (g instanceof UniformCapsule) {
		UniformCapsule cap = (UniformCapsule)g;
		final List<Vector3> vertices = new ArrayList<Vector3>();
		//final List<Vector3> icoicosahedron = new ArrayList<Vector3>();
		
		// point on icosahedron
		//final double t = (1.0 + Math.sqrt(5.0))/ 2.0;
		//final double S = 1.0 / ( Math.sqrt(1+t*t)); 
		//icoicosahedron.add(new Vector3(-1,  t,  0));
		//icoicosahedron.add( new Vector3( 1,  t,  0));
		//icoicosahedron.add( new Vector3(-1, -t,  0));
		//icoicosahedron.add( new Vector3( 1, -t,  0));
		//icoicosahedron.add( new Vector3( 0, -1,  t));
		//icoicosahedron.add( new Vector3( 0,  1,  t));
		//icoicosahedron.add( new Vector3( 0, -1, -t));
		//icoicosahedron.add( new Vector3( 0,  1, -t));
		//icoicosahedron.add( new Vector3( t,  0, -1));
		//icoicosahedron.add( new Vector3( t,  0,  1));
		//icoicosahedron.add( new Vector3(-t,  0, -1));
		//icoicosahedron.add( new Vector3(-t,  0,  1));

		ConvexHull icosphere = buildIcosphere(1, 2);
		
		// scale to unit
		//for (Vector3 v: icoicosahedron)
		//	v.assign(v.multiply(S) );

		// add two icos to vertices
		Iterator<Vector3> iter = icosphere.getVertices();
		while(iter.hasNext()) {
			Vector3 v = iter.next();
			vertices.add( v.multiply(cap.getRadius()).add(0,0,cap.getLength()/2));
			vertices.add( v.multiply(cap.getRadius()).add(0,0,-cap.getLength()/2));
		}
			
		return new DrawShape() {
			public Iterator<Vector3[]> getFaces() {return new ConvexHull(vertices).getFaces();}
			public Matrix4 getTransform() {return g.getTransform();}
			public Body getReferenceBody() {return g.getBody();}
		};
	} else {
		throw new RuntimeException();
	}
}

static ConvexHull buildIcosphere(double r, int depth) {
	final List<Vector3> vertices = new ArrayList<Vector3>();
		//vertices.add(new Vector3( 1, 1, 1).normalize());
		//vertices.add(new Vector3(-1,-1, 1).normalize());
		//vertices.add(new Vector3(-1, 1,-1).normalize());
		//vertices.add(new Vector3( 1,-1,-1).normalize());
	// point on icosahedron
	final double t = (1.0 + Math.sqrt(5.0))/ 2.0;
	vertices.add(new Vector3(-1,  t,  0).normalize());
	vertices.add( new Vector3( 1,  t,  0).normalize());
	vertices.add( new Vector3(-1, -t,  0).normalize());
	vertices.add( new Vector3( 1, -t,  0).normalize());
	vertices.add( new Vector3( 0, -1,  t).normalize());
	vertices.add( new Vector3( 0,  1,  t).normalize());
	vertices.add( new Vector3( 0, -1, -t).normalize());
	vertices.add( new Vector3( 0,  1, -t).normalize());
	vertices.add( new Vector3( t,  0, -1).normalize());
	vertices.add( new Vector3( t,  0,  1).normalize());
	vertices.add( new Vector3(-t,  0, -1).normalize());
	vertices.add( new Vector3(-t,  0,  1).normalize());

	int n = 0;
	while (true) {
		ConvexHull hull = new ConvexHull(vertices);

		if (n>=depth)
			return hull;

		// for each face, add a new sphere support 
		// point in direction of the face normal
		Iterator<Vector3[]> iter = hull.getFaces();
		while(iter.hasNext()) {
			Vector3[] face = iter.next();
			Vector3 normal =face[1].sub(face[0]).cross(face[2].sub(face[1])).normalize();
			vertices.add(new Vector3(normal));
		}
		
		// depth level done
		n++;
	}
}

static public double[] spm(Vector3 l, Vector3 e, Vector3 n) {
	double d, c;
	double[] mat = new double[16];

	// These are c and d (corresponding to the tutorial)

	d = n.x*l.x + n.y*l.y + n.z*l.z;
	c = e.x*n.x + e.y*n.y + e.z*n.z - d;

	// Create the matrix. OpenGL uses column by column ordering

	mat[0]  = l.x*n.x+c; 
	mat[4]  = n.y*l.x; 
	mat[8]  = n.z*l.x; 
	mat[12] = -l.x*c-l.x*d;

	mat[1]  = n.x*l.y;    
	mat[5]  = l.y*n.y+c;
	mat[9]  = n.z*l.y; 
	mat[13] = -l.y*c-l.y*d;

	mat[2]  = n.x*l.z;    
	mat[6]  = n.y*l.z; 
	mat[10] = l.z*n.z+c; 
	mat[14] = -l.z*c-l.z*d;

	mat[3]  = n.x;    
	mat[7]  = n.y; 
	mat[11] = n.z; 
	mat[15] = -d;

	return mat;
}

}