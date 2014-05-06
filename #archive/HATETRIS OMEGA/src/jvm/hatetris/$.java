package hatetris;

import java.util.*;

public class $ {

static boolean bit_test(long x, long n) {return (x & (1L << n)) != 0;}
static long bit_set(long x, long n) {return x | (1L << n);}
static long bit_clear(long x, long n) {return x & ~(1L << n);}

static void assert_(boolean b) {if (!b) throw new AssertionError();}
static void println(Object o) {System.out.println(o);}

static public boolean bit_masks_collide(int[] mask0_a, int _0_row, int[] mask1_a, int _1_row, int _1_offset) {
	return (mask0_a[_0_row] & (mask1_a[_1_row] << _1_offset)) != 0;}
static public void bit_masks_union_mutate(int[] mask0_a, int _0_row, int[] mask1_a, int _1_row, int _1_offset) {
	mask0_a[_0_row] |= mask1_a[_1_row] << _1_offset;
	}

static public class Grid {
	public int begin_x, begin_y, end_x, end_y; // not offsets into 'data; offsets *for* 'data
	public int[] data; // starts in the south-west corner. like, [0b011 0b110 0b000] corresponds to ["---" "-XX" "XX-"]

	public Grid(int X, int Y) {
		assert_(X <= 29);
		this.begin_x = this.begin_y = 0;
		this.end_x = X;
		this.end_y = Y;
		this.data = new int[Y];
		}

	public boolean get(int x, int y) {y-=begin_y; x-=begin_x; return bit_test(data[y],x);}
	public void set(int x, int y, boolean v) {y-=begin_y; x-=begin_x; data[y] = (int)(v? bit_set(data[y],x) : bit_clear(data[y],x));}
	public Object range_x() {return clojure.lang.PersistentVector.create(begin_x,end_x);}
	public Object range_y() {return clojure.lang.PersistentVector.create(begin_y,end_y);}

	public Grid rotate_clockwise_90() {
		assert_simple();
		Grid r = new Grid(end_y,end_x);
		for (int y = 0; y < end_y; y++)
			for (int x = 0; x < end_x; x++)
				r.set(y,end_x-1 - x,get(x,y));
		return r;}
	public Grid trim() {
		assert_simple();

		int yn = -1; all:
		for (int y = 0; y < end_y; y++)
		for (int x = 0; x < end_x; x++)
			if (get(x,y)) {yn = y; break all;}
		int yp = -1; all:
		for (int y = end_y-1; y >= 0; y--)
		for (int x = 0; x < end_x; x++)
			if (get(x,y)) {yp = y; break all;}
		int xn = -1; all:
		for (int x = 0; x < end_x; x++)
		for (int y = 0; y < end_y; y++)
			if (get(x,y)) {xn = x; break all;}
		int xp = -1; all:
		for (int x = end_x-1; x >= 0; x--)
		for (int y = 0; y < end_y; y++)
			if (get(x,y)) {xp = x; break all;}

		Grid r = new Grid(xp+1,yp+1);
		r.begin_x = xn;
		r.begin_y = yn;
		for (int y = r.begin_y; y < r.end_y; y++)
			for (int x = r.begin_x; x < r.end_x; x++)
				r.set(x,y,get(x,y));
		return r;}

	public void assert_simple() {assert_(begin_x == 0 && begin_y == 0);}

	public String toString() {
		String r = "";
		for (int y = begin_y; y < end_y; y++) {
			for (int x = begin_x; x < end_x; x++) {
				r += get(x,end_y-1-y+begin_y)? "X" : "-";
			}
			r += "\n";
		}
		return r;}
}
static public class Well {
	public Grid grid;
	public Object piece_type;

	public Well(Grid grid) {this.grid = grid;}
}

}