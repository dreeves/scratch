package takns;

import lombok.*;
import java.util.*;

import takns.main.*;
import static takns.main.*;

public class synth {

static class Distort extends Synth {
	Synth source;
	Synth distort;

	Distort(Synth source, Synth distort) {
		this.source = source;
		this.distort = distort;
	}
	double getValue(double x, double y) {return source.getValue(x + distort.getValue(x, y), y);}
}
static class Emboss extends Synth {
	Synth synth;

	Emboss(Synth synth) {this.synth = synth;}
	double getValue(double x, double y) {return synth.getValue(x, y) - synth.getValue(x + 1, y + 1);}
}
// Based on Improved Noise by Ken Perlin See http://mrl.nyu.edu/~perlin/noise/
static class ImprovedNoise extends Synth {
	int[] p = new int[512];

	double scale;

	ImprovedNoise() {
		for (int i = 0; i < 256; i++) p[i] = i;

		for (int i = 0; i < 256; i++) {
			int j = rand.nextInt(256 - i) + i;
			int tmp = p[i];
			p[i] = p[j];
			p[j] = tmp;

			p[i + 256] = p[i];
		}
	}
	double noise(double x, double y, double z) {
		int X = (int) Math.floor(x) & 255, Y = (int) Math.floor(y) & 255, Z = (int) Math.floor(z) & 255;

		x -= Math.floor(x);
		y -= Math.floor(y);
		z -= Math.floor(z);

		double u = fade(x), v = fade(y), w = fade(z);
		int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z, B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z;
		return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z), grad(p[BA], x - 1, y, z)), lerp(u, grad(p[AB], x, y - 1, z), grad(p[BB], x - 1, y - 1, z))), lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1), grad(p[BA + 1], x - 1, y, z - 1)), lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
	}
	double fade(double t) {return t * t * t * (t * (t * 6 - 15) + 10);}
	double lerp(double t, double a, double b) {return a + t * (b - a);}
	double grad(int hash, double x, double y, double z) {
		int h = hash & 15;
		double u = h < 8 ? x : y, v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}
	double getValue(double x, double y) {return noise(x, y, 0);}
}
static class PerlinNoise extends Synth {
	ImprovedNoise[] noiseLevels;
	int levels;

	PerlinNoise(int levels) {
		this.levels = levels;
		noiseLevels = new ImprovedNoise[levels];
		for (int i = 0; i < levels; i++) noiseLevels[i] = new ImprovedNoise();
	}
	double getValue(double x, double y) {
		double value = 0;
		double pow = 1;

		for (int i = 0; i < levels; i++) {
			value += noiseLevels[i].getValue(x / pow, y / pow) * pow;
			pow *= 2;
		}

		return value;
	}
}
static class Rotate extends Synth {
	Synth synth;
	double sin;
	double cos;

	Rotate(Synth synth, double angle) {
		this.synth = synth;

		sin = Math.sin(angle);
		cos = Math.cos(angle);
	}
	double getValue(double x, double y) {return synth.getValue(x * cos + y * sin, y * cos - x * sin);}
}
static class Scale extends Synth {
	Synth synth;
	double xScale;
	double yScale;

	Scale(Synth synth, double xScale, double yScale) {
		this.synth = synth;
		this.xScale = 1.0 / xScale;
		this.yScale = 1.0 / yScale;
	}
	double getValue(double x, double y) {return synth.getValue(x * xScale, y * yScale);}
}
static abstract class Synth {
	abstract double getValue(double x, double y);

	double[] create(int width, int height) {
		double[] result = new double[width * height];
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				result[x + y * width] = getValue(x, y);
		return result;
	}
}


}