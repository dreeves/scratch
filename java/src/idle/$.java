package idle;

public class $ {

//===--------------------------------------------===// api //===--------------------------------------------===//

// returns time in seconds since last input event
// returns 0 if the api call fails
public static double idle_time() {
	User32.LASTINPUTINFO t = new User32.LASTINPUTINFO(); return User32.$.GetLastInputInfo(t)? (Kernel32.$.GetTickCount() - t.dwTime)/1000.0 : 0.0;}

//===--------------------------------------------===// private //===--------------------------------------------===//

// export Kernel32.GetTickCount
public interface Kernel32 extends com.sun.jna.win32.StdCallLibrary {Kernel32 $ = (Kernel32)com.sun.jna.Native.loadLibrary("kernel32", Kernel32.class);
	public int GetTickCount();}
// export User32.GetLastInputInfo and User32.LASTINPUTINFO
public interface User32 extends com.sun.jna.win32.StdCallLibrary {User32 $ = (User32)com.sun.jna.Native.loadLibrary("user32", User32.class);
	public boolean GetLastInputInfo(LASTINPUTINFO out);
	public static class LASTINPUTINFO extends com.sun.jna.Structure {protected java.util.List getFieldOrder() {return java.util.Arrays.asList("cbSize","dwTime");}
		public int cbSize=8; public int dwTime;}
	}

}