package rugged;

import org.lwjgl.BufferUtils;
import java.nio.*;

public class Buf
{
	public static    IntBuffer newFromArr(   int[] a) {return (   IntBuffer)BufferUtils.   createIntBuffer(a.length).put(a).flip();}
	public static   ByteBuffer newFromArr(  byte[] a) {return (  ByteBuffer)BufferUtils.  createByteBuffer(a.length).put(a).flip();}
	public static   CharBuffer newFromArr(  char[] a) {return (  CharBuffer)BufferUtils.  createCharBuffer(a.length).put(a).flip();}
	public static   LongBuffer newFromArr(  long[] a) {return (  LongBuffer)BufferUtils.  createLongBuffer(a.length).put(a).flip();}
	public static  FloatBuffer newFromArr( float[] a) {return ( FloatBuffer)BufferUtils. createFloatBuffer(a.length).put(a).flip();}
	public static  ShortBuffer newFromArr( short[] a) {return ( ShortBuffer)BufferUtils. createShortBuffer(a.length).put(a).flip();}
	public static DoubleBuffer newFromArr(double[] a) {return (DoubleBuffer)BufferUtils.createDoubleBuffer(a.length).put(a).flip();}
	
	private static ThreadLocal<ReusableBuffers> buffers = new ThreadLocal<ReusableBuffers>() {protected ReusableBuffers initialValue() {return new ReusableBuffers();}};
	
	public static ByteBuffer getByte(int len) {
		if (len <= 64 || (len>>1 == 0 && len <= 128) || (len>>2 == 0 && len <= 256) || (len>>3 == 0 && len <= 512)) {
			ReusableBuffers bufs = buffers.get();
			if (len <= 64) return bufs.bb1[len-1] == null? (bufs.bb1[len-1] = BufferUtils.createByteBuffer(len)) : bufs.bb1[len-1];
			if (len <= 128) return bufs.bb2[(len>>1)-33] == null? (bufs.bb2[(len>>1)-33] = BufferUtils.createByteBuffer(len)) : bufs.bb2[(len>>1)-33];
			if (len <= 256) return bufs.bb4[(len>>2)-33] == null? (bufs.bb4[(len>>2)-33] = BufferUtils.createByteBuffer(len)) : bufs.bb4[(len>>2)-33];
			if (len <= 512) return bufs.bb8[(len>>3)-33] == null? (bufs.bb8[(len>>3)-33] = BufferUtils.createByteBuffer(len)) : bufs.bb8[(len>>3)-33];
			return null; //won't get here
		} else return BufferUtils.createByteBuffer(len);
	}
	public static   CharBuffer   getChar(int len) {return getByte(len<<1).  asCharBuffer();}
	public static  ShortBuffer  getShort(int len) {return getByte(len<<1). asShortBuffer();}
	public static    IntBuffer    getInt(int len) {return getByte(len<<2).   asIntBuffer();}
	public static  FloatBuffer  getFloat(int len) {return getByte(len<<2). asFloatBuffer();}
	public static   LongBuffer   getLong(int len) {return getByte(len<<3).  asLongBuffer();}
	public static DoubleBuffer getDouble(int len) {return getByte(len<<3).asDoubleBuffer();}
	
	public static    IntBuffer fromArr(   int[] a) {return (   IntBuffer)((   IntBuffer)   getInt(a.length).clear()).put(a).flip();}
	public static   ByteBuffer fromArr(  byte[] a) {return (  ByteBuffer)((  ByteBuffer)  getByte(a.length).clear()).put(a).flip();}
	public static   CharBuffer fromArr(  char[] a) {return (  CharBuffer)((  CharBuffer)  getChar(a.length).clear()).put(a).flip();}
	public static   LongBuffer fromArr(  long[] a) {return (  LongBuffer)((  LongBuffer)  getLong(a.length).clear()).put(a).flip();}
	public static  FloatBuffer fromArr( float[] a) {return ( FloatBuffer)(( FloatBuffer) getFloat(a.length).clear()).put(a).flip();}
	public static  ShortBuffer fromArr( short[] a) {return ( ShortBuffer)(( ShortBuffer) getShort(a.length).clear()).put(a).flip();}
	public static DoubleBuffer fromArr(double[] a) {return (DoubleBuffer)((DoubleBuffer)getDouble(a.length).clear()).put(a).flip();}

	private static class ReusableBuffers
	{
		public ByteBuffer[] bb1 = new ByteBuffer[64]; //up to 64 bytes
		public ByteBuffer[] bb2 = new ByteBuffer[32]; //up to 64 chars/shorts
		public ByteBuffer[] bb4 = new ByteBuffer[32]; //up to 64 ints/floats
		public ByteBuffer[] bb8 = new ByteBuffer[32]; //up to 64 longs/doubles
	}
}