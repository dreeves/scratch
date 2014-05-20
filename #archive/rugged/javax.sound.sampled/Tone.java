package rugged;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;
 
public class Tone
{ 
	public static void main(String[] args)
	{
		int hz = 441; //Base frequency (neglecting harmonic) of the tone in cycles per second
		int msecs = 4000; //The number of milliseconds to play the tone.
		int volume = 100; //Volume, form 0 (mute) to 100 (max).
		boolean addHarmonic = true; //Whether to add an harmonic, one octave up.
		try {
			float frequency = 44100;
			byte[] buf = new byte[addHarmonic? 2 : 1];
			AudioFormat af = new AudioFormat(frequency, 8, buf.length, true, false);
			SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
			sdl.open(af);
			sdl.start();
			for (int i = 0; i < msecs * frequency / 1000; i++) {
				double angle = i / (frequency / hz) * 2.0 * Math.PI;
				buf[0] = (byte)(Math.sin(angle) * volume);
				if (addHarmonic) buf[1] = (byte)(Math.sin(2 * angle) * volume * 0.6);
				sdl.write(buf, 0, buf.length);
			}
			sdl.drain();
			sdl.stop();
			sdl.close();
		} catch (LineUnavailableException e) {}		
	}
}