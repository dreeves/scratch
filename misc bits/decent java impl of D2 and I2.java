static class D2 {
	final double x, y;

	static D2 O = D2(0,0);
	D2(double x, double y) {this.x = x; this.y = y;}
	static D2 polar(double r, double theta) {return D2(r*cos(theta),r*sin(theta));}

	D2 add(D2 b) {return D2(x+b.x,y+b.y);}
	D2 sub(D2 b) {return D2(x-b.x,y-b.y);}
	D2 mul(D2 b) {return D2(x*b.x,y*b.y);}
	D2 div(D2 b) {return D2(x/b.x,y/b.y);}
	D2 add(I2 b) {return D2(x+b.x,y+b.y);}
	D2 sub(I2 b) {return D2(x-b.x,y-b.y);}
	D2 mul(I2 b) {return D2(x*b.x,y*b.y);}
	D2 div(I2 b) {return D2(x/b.x,y/b.y);}
	D2 add(double v) {return D2(x+v,y+v);}
	D2 sub(double v) {return D2(x-v,y-v);}
	D2 mul(double v) {return D2(x*v,y*v);}
	D2 div(double v) {return D2(x/v,y/v);}

	D2 absS() {return D2(abs(x),abs(y));}
	D2 normalize() {return this.mul(1/len());}
	double lenSq() {return x*x + y*y;}
	double len() {return sqrt(lenSq());}
	I2 round() {return I2((int)Math.round(x),(int)Math.round(y));}

	public String toString() {return ""+list(x,y);}
	}
static D2 D2(double x, double y) {return new D2(x,y);}
static D2 D2(I2 v) {return v.toD2();}
static class I2 {
	final int x, y;

	static I2 O = I2(0,0);
	I2(int x, int y) {this.x = x; this.y = y;}

	I2 add(I2 b) {return I2(x+b.x,y+b.y);}
	I2 sub(I2 b) {return I2(x-b.x,y-b.y);}
	I2 mul(I2 b) {return I2(x*b.x,y*b.y);}
	I2 div(I2 b) {return I2(x/b.x,y/b.y);}
	I2 add(int v) {return I2(x+v,y+v);}
	I2 sub(int v) {return I2(x-v,y-v);}
	I2 mul(int v) {return I2(x*v,y*v);}
	I2 div(int v) {return I2(x/v,y/v);}

	D2 toD2() {return D2(x,y);}
	}
static I2 I2(int x, int y) {return new I2(x,y);}