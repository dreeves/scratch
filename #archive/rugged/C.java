package rugged;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public class C
{
	public static void main(String[] args) {
		// Initialize OpenAL and clear the error bit.
		try {AL.create(null, 15, 22050, true);}
		catch (LWJGLException e) {e.printStackTrace(); return;}
		AL.checkError();

		//float[][] sourcePos = new float[3][3];
		//float[][] sourceVel = new float[3][3];
		int[] buffers = new int[3];
		buffers[0] = AL10.alGenBuffers();
		//buffers[1] = AL10.alGenBuffers();
		//buffers[2] = AL10.alGenBuffers();
		AL.checkError();

		WaveData waveFile0 = WaveData.create("rugged/battle.wav");
		AL10.alBufferData(buffers[0], waveFile0.format, waveFile0.data, waveFile0.samplerate);
		waveFile0.dispose();
		/*WaveData waveFile1 = WaveData.create("rugged/gun1.wav");
		AL10.alBufferData(buffers[1], waveFile1.format, waveFile1.data, waveFile1.samplerate);
		waveFile1.dispose();
		WaveData waveFile2 = WaveData.create("rugged/gun2.wav");
		AL10.alBufferData(buffers[2], waveFile2.format, waveFile2.data, waveFile2.samplerate);
		waveFile2.dispose();*/

		// Bind the buffers with the sources.
		int[] sources = new int[3];
		sources[0] = AL10.alGenSources();
		//sources[1] = AL10.alGenSources();
		//sources[2] = AL10.alGenSources();
		AL.checkError();
		
		AL10.alSourcei(sources[0], AL10.AL_BUFFER,   buffers[0]);
		AL10.alSourcef(sources[0], AL10.AL_PITCH,    1f);
		AL10.alSourcef(sources[0], AL10.AL_GAIN,     1f);
		AL10.alSource (sources[0], AL10.AL_POSITION, Buf.fromArr(new float[]{0, 10, 0}));
		AL10.alSource (sources[0], AL10.AL_VELOCITY, Buf.fromArr(new float[]{0, -200, 0}));
		AL10.alSourcei(sources[0], AL10.AL_LOOPING,  AL10.AL_TRUE);
		/*AL10.alSourcei(sources[0], AL10.AL_BUFFER,   buffers[0]);
		AL10.alSourcef(sources[0], AL10.AL_PITCH,    1f);
		AL10.alSourcef(sources[0], AL10.AL_GAIN,     1f);
		AL10.alSource (sources[0], AL10.AL_POSITION, Buf.fromArr(sourcePos[0]));
		AL10.alSource (sources[0], AL10.AL_VELOCITY, Buf.fromArr(sourceVel[0]));
		AL10.alSourcei(sources[0], AL10.AL_LOOPING,  AL10.AL_TRUE);

		AL10.alSourcei(sources[1], AL10.AL_BUFFER,   buffers[1]);
		AL10.alSourcef(sources[1], AL10.AL_PITCH,    1f);
		AL10.alSourcef(sources[1], AL10.AL_GAIN,     1f);
		AL10.alSource (sources[1], AL10.AL_POSITION, Buf.fromArr(sourcePos[1]));
		AL10.alSource (sources[1], AL10.AL_VELOCITY, Buf.fromArr(sourceVel[1]));
		AL10.alSourcei(sources[1], AL10.AL_LOOPING,  AL10.AL_FALSE);

		AL10.alSourcei(sources[2], AL10.AL_BUFFER,   buffers[2]);
		AL10.alSourcef(sources[2], AL10.AL_PITCH,    1f);
		AL10.alSourcef(sources[2], AL10.AL_GAIN,     1f);
		AL10.alSource (sources[2], AL10.AL_POSITION, Buf.fromArr(sourcePos[2]));
		AL10.alSource (sources[2], AL10.AL_VELOCITY, Buf.fromArr(sourceVel[2]));
		AL10.alSourcei(sources[2], AL10.AL_LOOPING,  AL10.AL_FALSE);*/

		AL.checkError();

		AL10.alListener(AL10.AL_POSITION,    Buf.fromArr(new float[]{0, 0, 0}));
		AL10.alListener(AL10.AL_VELOCITY,    Buf.fromArr(new float[]{0, -50, 0}));
		//Orientation of the listener. (first 3 elements are "at", second 3 are "up"). Also note that these should be units of '1'.
		AL10.alListener(AL10.AL_ORIENTATION, Buf.fromArr(new float[]{0, 0, -1, 0, 1, 0}));
			
				/*case 'p': AL10.alSourcePlay(source); break;
				case 's': AL10.alSourceStop(source); break;
				case 'h': AL10.alSourcePause(source); break;*/
				
		AL10.alSourcePlay(sources[0]);
		
		S.sleep(50000);
		/*while (true)
		for (int i = 1; i < 3; i++)
		if (AL10.alGetSourcei(sources[i], AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
			double theta = S.rand(360) * Math.PI / 180;
			sourcePos[i][0] = -(float)Math.cos(theta);
			sourcePos[i][1] = -(float)S.rand(2);
			sourcePos[i][2] = -(float)Math.sin(theta);
			AL10.alSource(sources[i], AL10.AL_POSITION, Buf.fromArr(sourcePos[i]));
			AL10.alSourcePlay(sources[i]);
		}*/

		//We have allocated memory for our buffers and sources which needs to be returned to the system. This function frees that memory.
		//killALData() {
			//AL10.alDeleteSources(source);
			//AL10.alDeleteBuffers(buffer);
	}
}