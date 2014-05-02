package zutil;

public class N { // Native

// C:\code\libs\openjdk\jdk\src\share\classes\sun\misc\Unsafe.java

@SuppressWarnings("unchecked")
static <T> T getUnsafe() {try {
	// avoids the symbol sun.misc.Unsafe, which gives a warning for each occurence
	java.lang.reflect.Field f = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
	f.setAccessible(true);
	return (T)f.get(null);
	} catch (Exception e) {throw new RuntimeException(e);}}
public static final sun.misc.Unsafe unsafe = getUnsafe();

public static byte   _byte  (long ptr) {return unsafe.getByte   (ptr);}
public static short  _short (long ptr) {return unsafe.getShort  (ptr);}
public static char   _char  (long ptr) {return unsafe.getChar   (ptr);}
public static int    _int   (long ptr) {return unsafe.getInt    (ptr);}
public static long   _long  (long ptr) {return unsafe.getLong   (ptr);}
public static float  _float (long ptr) {return unsafe.getFloat  (ptr);}
public static double _double(long ptr) {return unsafe.getDouble (ptr);}
public static long    ptr   (long ptr) {return unsafe.getAddress(ptr);}
public static void _byte  (long ptr, byte   x) {unsafe.putByte   (ptr, x);}
public static void _short (long ptr, short  x) {unsafe.putShort  (ptr, x);}
public static void _char  (long ptr, char   x) {unsafe.putChar   (ptr, x);}
public static void _int   (long ptr, int    x) {unsafe.putInt    (ptr, x);}
public static void _long  (long ptr, long   x) {unsafe.putLong   (ptr, x);}
public static void _float (long ptr, float  x) {unsafe.putFloat  (ptr, x);}
public static void _double(long ptr, double x) {unsafe.putDouble (ptr, x);}
public static void  ptr   (long ptr, long   x) {unsafe.putAddress(ptr, x);}

public static long malloc(long len) {return unsafe.allocateMemory(len);}
public static long realloc(long ptr, long len) {return unsafe.reallocateMemory(ptr, len);} // um. "The resulting native pointer will be aligned for all value types.". Also will just do allocateMemory if ptr = 0
public static long calloc(long len) {long r = malloc(len); memset(r, 0, len); return r;}
public static void free(long ptr) {unsafe.freeMemory(ptr);} // does nothing if ptr = 0
public static void memcpy(long src_ptr, long dest_ptr, long len) {unsafe.copyMemory(null, src_ptr, null, dest_ptr, len);} // ptr and len parameters should be 0 mod 8 or 4 or 2 // this may actually be memmove, but I'd rather not take my chances
public static void memset(long ptr, int value, long len) {unsafe.setMemory(null, ptr, len, (byte)value);}

public static int page_size() {return unsafe.pageSize();} // "Report the length of a native memory page (whatever that is). This value will always be a power of two."
public static final int ADDRESS_SIZE = unsafe.addressSize(); // ptr length; always 4 or 8

// do we need to mix with normal java code ??
// I can define my own new original object layout in memory... but I can't garbage collect it. Because I don't have a garbage collector.
// idea: value arrays; like have class Point {int a; int b;} and Point[] be ptr[] pointing to Points but Point:v[] be actual Point[]... ooo.
// sigh. I guess the only thing I can do this is speed optimizations T-T

}