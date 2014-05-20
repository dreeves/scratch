package rugged;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;
import org.lwjgl.LWJGLException;

public class AL
{
		//copy of Util.checkAlError()
	public static void checkError() {
		int err = AL10.alGetError();
		if (err != AL10.AL_NO_ERROR)
			throw new OpenALException(err);
	}
	
		//copies of most of the org.lwjgl.openal.AL functions
	public static void create() throws LWJGLException {org.lwjgl.openal.AL.create();}
	public static void create(String deviceArgs, int contextFreq, int contextRefresh, boolean contextSync) throws LWJGLException
		{org.lwjgl.openal.AL.create(deviceArgs, contextFreq, contextRefresh, contextSync);}
	public static void create(String deviceArgs, int contextFreq, int contextRefresh, boolean contextSync, boolean openDevice) throws LWJGLException
		{org.lwjgl.openal.AL.create(deviceArgs, contextFreq, contextRefresh, contextSync, openDevice);}
	public static void destroy() {org.lwjgl.openal.AL.destroy();}
	public static boolean isCreated() {return org.lwjgl.openal.AL.isCreated();}
}