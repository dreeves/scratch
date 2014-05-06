package takns;

import lombok.*;
import java.util.*;

import javax.sound.sampled.*;

import takns.main.*;
import static takns.main.*;

public class sound {

static class PanSource implements SoundSource {
	float pan;

	PanSource(float pan) {this.pan = pan;}
	public float getXSoundPos() {return pan;}
	public float getYSoundPos() {return 0;}
}
static class SelectionSound extends Sound {
	int step = 0;
	int delayTicks = 0;
	int attackTicks, sustainTicks, decayTicks;
	
	SelectionSound(float delay) {
		delayTicks = (int)(delay*SoundEngine.SAMPLE_RATE);
		
		attackTicks = (int)(0.001f*SoundEngine.SAMPLE_RATE);
		sustainTicks = (int)(0.04f*SoundEngine.SAMPLE_RATE);
		decayTicks = (int)(0.02f*SoundEngine.SAMPLE_RATE);
	}
	boolean read(float[] buf, int bufferSize) {
		for (int i=0; i<bufferSize; i++) {
			if (step<delayTicks) {
				buf[i] = 0;
			} else {
				float volume = 1;
				
				if (step>delayTicks+attackTicks+sustainTicks) 
					volume = 1-(step-(delayTicks+attackTicks+sustainTicks))/(float)decayTicks;
				if (step<delayTicks+attackTicks) 
					volume = (step-delayTicks)/(float)attackTicks;
				
				if (volume<0) buf[i] = 0;
				else buf[i] = (sin(step, 1000)+saw(step, 2000)+square(step, 3000)*0.4f)*volume*0.03f;
			}
			step++;
		}
		return step<(delayTicks+attackTicks+sustainTicks+decayTicks);
	}
}
static class Sinewave extends Sound {
	int step = 0;
	boolean read(float[] buf, int bufferSize) {
		for (int i=0; i<bufferSize; i++) {
			float samp = (saw(step+i, 50))*(saw(step+i,20)*0.25f+0.35f);

			buf[i] = samp;
		}
		step+=bufferSize;
		return true;
	}
}
static class Sound { // is extended
	final float saw(float phase, float rate) {
		phase/=SoundEngine.SAMPLE_RATE;
		phase*=rate;
		phase-=(int)phase;
		if (phase<0.5f) return phase*4-1;
		return 1-(phase-0.5f)*4;
	}
	final float square(float phase, float rate) {
		phase/=SoundEngine.SAMPLE_RATE;
		phase*=rate*2;
		return (((int)phase)&1)*2-1;
	}
	final float sin(float phase, float rate) {return (float)(Math.sin(phase*Math.PI*2*rate/SoundEngine.SAMPLE_RATE)); }
	final float noise() {return rand.nextFloat()*2-1; }
	float noise;
	final float noise(float pitch) {
		float noiseTarget=rand.nextFloat()*2-1;
		noise += (noiseTarget-noise)*pitch;
		return noise; 
	}
	boolean read(float[] lBuf, int bufferSize) {return false;}
}
static class SoundEngine implements Runnable {
	static final float SAMPLE_RATE = 44100.0f;
	static final int BUFFER_SIZE = (int) (SAMPLE_RATE * 50 / 1000);
	static final int MIN_BUFFER_SIZE = (int) (SAMPLE_RATE * 10 / 1000);

	boolean running = false;
	SourceDataLine dataLine;
	List<StereoSound> sounds = new ArrayList<StereoSound>();

	void start() {
		try {
			val audioFormat = new AudioFormat(SAMPLE_RATE, 16, 2, true, true);
			dataLine = AudioSystem.getSourceDataLine(audioFormat);
			dataLine.open(audioFormat, (int)(SAMPLE_RATE*0.1f));
			dataLine.start();

			running = true;

			val thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
		}
		catch (Exception e) {
			e.printStackTrace();
			running = false;
		}
	}
	void addSound(Sound sound, float pan) {addSound(new StereoSound(sound, pan));}
	void addSound(StereoSound sound) {
		if (!running) return;
		synchronized (sounds) {sounds.add(sound);}
	}
	void addSound(Sound sound, SoundSource source) {addSound(new StereoSound(sound, source));}
	public void run() {
		float[] lBuf = new float[BUFFER_SIZE];
		float[] rBuf = new float[BUFFER_SIZE];
		byte[] data = new byte[BUFFER_SIZE * 4];

		while (running) {
			while (dataLine.available() < MIN_BUFFER_SIZE * 2 * 2) {
				try {Thread.sleep(1);}
				catch (InterruptedException e) {e.printStackTrace();}
			}

			int toRead = dataLine.available();
			if (toRead>BUFFER_SIZE) toRead = BUFFER_SIZE;

			Arrays.fill(lBuf, 0, toRead, 0);
			Arrays.fill(rBuf, 0, toRead, 0);
			mix(sounds, lBuf, rBuf, toRead);
			for (int i = 0; i < toRead; i++) {
				if (lBuf[i]>1) lBuf[i] = 1;
				if (rBuf[i]>1) rBuf[i] = 1;
				if (lBuf[i]<-1) lBuf[i] = -1;
				if (rBuf[i]<-1) rBuf[i] = -1;
				int lSamp = (int) ((lBuf[i]) * 32760);
				int rSamp = (int) ((rBuf[i]) * 32760);

				data[i * 4 + 0] = (byte) ((lSamp >> 8) & 0xff);
				data[i * 4 + 1] = (byte) ((lSamp) & 0xff);
				data[i * 4 + 2] = (byte) ((rSamp >> 8) & 0xff);
				data[i * 4 + 3] = (byte) ((rSamp) & 0xff);
			}
			dataLine.write(data, 0, toRead * 2 * 2);
		}
	}
	void mix(List<StereoSound> sounds, float[] lBuf, float[] rBuf, int bufferSize) {
		synchronized (sounds) {
			for (int i = 0; i < sounds.size(); i++) {
				val sound = sounds.get(i);
				val alive = sound.read(lBuf, rBuf, bufferSize);
				if (!alive) sounds.remove(i--);
			}
		}
	}
}
static interface SoundSource {
	float getXSoundPos();
	float getYSoundPos();
}
static class StereoSound {
	static final float SOUND_FALLOFF = 6.0f;
	
	Sound sound;
	SoundSource soundSource;
	float lPan = -999f;
	float rPan = -999f;
	
	StereoSound(Sound sound, float pan) {this(sound, new PanSource(pan));}
	StereoSound(Sound sound, SoundSource soundSource) {
		this.sound = sound;
		this.soundSource = soundSource;
	}
	static float[] buf = new float[1];
	boolean read(float[] lBuf, float[] rBuf, int bufferSize) {
		if (buf.length<bufferSize) buf = new float[bufferSize];
		
		val alive = sound.read(buf, bufferSize);
		
		float panTarget = soundSource.getXSoundPos();
		
		float volume = 1;
		if (panTarget<-1) volume+=(panTarget+1)*SOUND_FALLOFF;
		if (panTarget>1) volume-=(panTarget-1)*SOUND_FALLOFF;
		float ySound = soundSource.getYSoundPos();
		if (ySound<-1) volume+=(ySound+1)*SOUND_FALLOFF;
		if (ySound>1) volume-=(ySound-1)*SOUND_FALLOFF;
		
		if (panTarget<-1) panTarget = -1;
		if (panTarget>1) panTarget = 1;
		
		float lPanT = (1-panTarget)*volume;
		float rPanT = (1+panTarget)*volume;
		if (lPanT<0) lPanT = 0;
		if (rPanT<0) rPanT = 0;
		if (lPanT>1) lPanT = 1;
		if (rPanT>1) rPanT = 1;
		
		if (lPan==-999f) lPan = lPanT;
		if (rPan==-999f) rPan = rPanT;

		if (lPan<=0 && lPanT<=0 && rPan<=0 && rPanT<=0) return alive;
		
		for (int i=0; i<bufferSize; i++) {
			lPan += (lPanT-lPan)*0.001f;
			rPan += (rPanT-rPan)*0.001f;
			lBuf[i]+=buf[i]*lPan;
			rBuf[i]+=buf[i]*rPan;
		}
		
		return alive;
	}
}

}