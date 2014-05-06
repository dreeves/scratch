package takns;

import lombok.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Image;

import takns.main.*;
import static takns.main.*;
import takns.sprites.Sprites;
import takns.terrain.Map;

public class particles {

static class DebrisParticle extends Particle {
	static float DAMPEN = 0.85f;

	Sprite sprite;
	float x, y, z, xa, ya, za;
	float age, ageSpeed;
	int type = 0;
	
	DebrisParticle(int type, float x, float y, float z, float xa, float ya, float za) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.xa = xa;
		this.ya = ya;
		this.za = za;
		
		age = 0;
		
		ageSpeed = ((float)Math.random()*0.1f+0.1f)*8;
		
		sprite = new Sprite();
	}
	void init(ParticleSystem particleSystem) {particleSystem.addSprite(sprite);}
	void remove(ParticleSystem particleSystem) {particleSystem.removeSprite(sprite);}
	boolean tick() {
		x+=xa;
		y+=ya;
		z+=za;
		if (z<0) {
			z = 0;
			xa = xa*0.8f;
			ya = ya*0.8f;
			za = -za*0.8f;
		}
		xa*=DAMPEN;
		ya*=DAMPEN;
		za*=DAMPEN;
		za-=0.2;
		
		age+=ageSpeed;
		
		return age<8;
	}
	void render() {
		sprite.x = (int)(x+xa*aleph);
		sprite.y = (int)(y+ya*aleph);
		sprite.z = (int)(z+za*aleph);
		sprite.image = Sprites.debris[type][(int)age];
	}
}
static class Explosion extends Particle {
	float x, y, z, xa, ya, za;
	ParticleSystem particleSystem;

	Explosion(float x, float y, float z, float xa, float ya, float za) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xa = xa;
		this.ya = ya;
		this.za = za;
	}
	void init(ParticleSystem particleSystem) {this.particleSystem = particleSystem;}
	void remove(ParticleSystem particleSystem) {}
	boolean tick() {
		particleSystem.addParticle(new Splat((int) x, (int) y, Sprites.crater[rand.nextInt(8)]));

		int type = particleSystem.getMap().getTerrainTypeAtPixel(x, y).id;
		for (int i = 0; i < 64; i++) {
			float pow = rand.nextFloat() * rand.nextFloat() * 2 + 0.5f;

			float xa = (float) Math.sin(i * Math.PI * 2 / 16) * pow * 4 + this.xa;
			float ya = (float) Math.cos(i * Math.PI * 2 / 16) * pow * 0.5f * 4 + this.ya;
			float za = rand.nextFloat() * rand.nextFloat() * 8 + this.za;

			float x = this.x + xa * 2 / pow;
			float y = this.y + ya * 2 / pow;
			float z = this.z;

			particleSystem.addParticle(new DebrisParticle(type, x, y, z, xa, ya, za));
		}
		for (int i = 0; i < 16; i++) {
			float pow = rand.nextFloat();

			float xa = (float) Math.sin(i * Math.PI * 2 / 16) * pow * 4 + this.xa;
			float ya = (float) Math.cos(i * Math.PI * 2 / 16) * pow * 0.5f * 4 + this.ya;
			float za = rand.nextFloat() * rand.nextFloat() * rand.nextFloat() * 16 + this.za;

			float x = this.x + xa * 2;
			float y = this.y + ya * 2;
			float z = this.z;

			particleSystem.addParticle(new SmokeParticle(x, y, z, xa, ya, za, SmokeParticle.TYPE_WHITE));
		}

		return false;
	}
}
static class Missile extends Particle {
	float x, y, z, xa, ya, za;
	ParticleSystem particleSystem;
	Sprite sprite;
	Sprite shadowSprite;
	int age = 0;

	static Missile createMissile(float xStart, float yStart, float zStart, float xTarget, float yTarget) {
		float xd = xTarget - xStart;
		float yd = yTarget - yStart;
		float dir = (float) (Math.atan2(xd, yd));
		float dist = (float) Math.sqrt(xd * xd + yd * yd);
		float za = (float) Math.sqrt(dist + 1) / 2 - 2;

		float time = za / 0.2f;
		float topHeight = zStart + za * time + -0.2f * time * time * 0.5f;

		time += (float) Math.sqrt(topHeight / 0.1);

		float pow = dist / time;
		float xa = (float) Math.sin(dir) * pow;
		float ya = (float) Math.cos(dir) * pow;

		return new Missile(xStart, yStart, zStart, xa, ya, za);
	}
	Missile(float x, float y, float z, float xa, float ya, float za) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xa = xa;
		this.ya = ya;
		this.za = za;

		age = 0;

		sprite = new Sprite();
		shadowSprite = new Sprite();
		shadowSprite.layer = Sprite.LAYER_SHADOW;
	}
	void init(ParticleSystem particleSystem) {
		this.particleSystem = particleSystem;
		particleSystem.addSprite(sprite);
		particleSystem.addSprite(shadowSprite);
	}
	void remove(ParticleSystem particleSystem) {
		particleSystem.removeSprite(sprite);
		particleSystem.removeSprite(shadowSprite);
	}
	boolean tick() {
		if (Math.random() > age / 20.0) particleSystem.addParticle(new SmokeParticle(x, y, z, xa, ya, za, SmokeParticle.TYPE_WHITE));
		if (age == 0) {
			for (int i = 0; i < 8; i++) {
				float xxa = -xa * i / 7 + (float) (Math.random() * 2 - 1);
				float yya = -ya * i / 7 + (float) (Math.random() * 2 - 1);
				float zza = -za * i / 7 + (float) (Math.random() * 2 - 1);
				particleSystem.addParticle(new SmokeParticle(x, y, z, xxa, yya, zza, SmokeParticle.TYPE_WHITE));
			}
		}
		age++;

		x += xa;
		y += ya;
		z += za;
		if (z <= 0) {
			particleSystem.addParticle(new Explosion(x, y, z, xa * 0.2f, ya * 0.2f, 0));
			return false;
		}
		za -= 0.2;

		return true;
	}
	void render() {
		sprite.x = (int) (x + xa * aleph);
		sprite.y = (int) (y + ya * aleph);
		sprite.z = (int) (z + za * aleph);
		sprite.image = Sprites.bullet[5];

		shadowSprite.x = (int) (x + xa * aleph);
		shadowSprite.y = (int) (y + ya * aleph);
		shadowSprite.z = (int) (0);
		int i = (int) ((z + za * aleph) / 20) + 4;
		if (i > 7) i = 7;
		shadowSprite.image = Sprites.shadow[i];
	}
}
static class Particle { // is extended
	void init(ParticleSystem particleSystem) {}
	boolean tick() {return false;}
	void render() {}
	void remove(ParticleSystem particleSystem) {}
}
static class ParticleSystem {
	List<Particle> particles = new ArrayList<Particle>();

	void addParticle(Particle particle) {
		particle.init(this);
		particles.add(particle);
		particle.render();
	}
	void addSprite(Sprite sprite) {world.mapRenderer.sprites.add(sprite);}
	void removeSprite(Sprite sprite) {world.mapRenderer.sprites.remove(sprite);}
	void tick() {
		for (int i=0; i<particles.size(); i++) {
			val particle = particles.get(i);
			val alive = particle.tick(); 
			if (!alive) {
				particle.remove(this);
				particles.remove(i);
				i--;
			}
		}
	}
	void render() {for (int i=0; i<particles.size(); i++) particles.get(i).render();}
	Map getMap() {return world.map;}
}
static class SmokeParticle extends Particle {
	static final int TYPE_WHITE = 0;
	static final int TYPE_FIRE = 1;
	
	static float DAMPEN = 0.7f;

	Sprite sprite;
	float x, y, z, xa, ya, za;
	float age, ageSpeed;
	int type;
	BufferedImage[] images;
	
	SmokeParticle(float x, float y, float z, float xa, float ya, float za, int type) {
		this.type = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		this.xa = xa;
		this.ya = ya;
		this.za = za;
		this.type = type;
		
		if (type==TYPE_WHITE) images = Sprites.whiteSmoke;
		if (type==TYPE_FIRE) images = Sprites.fireSmoke;
		
		age = 0;
		
		ageSpeed = ((float)Math.random()*0.2f+0.06f)*2;
		if (type==TYPE_FIRE) ageSpeed*=0.8f;
		
		sprite = new Sprite();
		sprite.image = images[0];
	}
	void init(ParticleSystem particleSystem) {particleSystem.addSprite(sprite);}
	void remove(ParticleSystem particleSystem) {particleSystem.removeSprite(sprite);}
	boolean tick() {
		x+=xa;
		y+=ya;
		z+=za;
		if (z<0) {
			z = 0;
			xa = xa*0.8f;
			ya = ya*0.8f;
			za = -za*0.8f;
		}
		xa*=DAMPEN;
		ya*=DAMPEN;
		za*=DAMPEN;
		za+=0.1;
		if (type==TYPE_FIRE) za+=(7-age)/30.0f;
		
		age+=ageSpeed;
		
		return age<8;
	}
	void render() {
		sprite.x = (int)(x+xa*aleph);
		sprite.y = (int)(y+ya*aleph);
		sprite.z = (int)(z+za*aleph);
		sprite.image = images[(int)age];
	}
}
static class Splat extends Particle {
	int age;
	int life;
	Sprite sprite;
	
	Splat(int x, int y, Image image) {
		sprite = new Sprite();
		sprite.layer = Sprite.LAYER_TERRAIN_SPLAT;
		sprite.x = x;
		sprite.y = y;
		sprite.image = image;
		age = 0;
		life = 400;
	}
	void init(ParticleSystem particleSystem) {particleSystem.addSprite(sprite);}
	boolean tick() {
		sprite.alpha = 1-age/(float)life;
		age++;
		return age<life;
	}
	void remove(ParticleSystem particleSystem) {particleSystem.removeSprite(sprite);}
}

}