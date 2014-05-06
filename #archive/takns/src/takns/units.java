package takns;

import lombok.*;
import java.util.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;

import takns.main.*;
import static takns.main.*;
import takns.ai.*;
import takns.gui.*;
import takns.particles.*;
import takns.sound.*;
import takns.sprites.*;

public class units {

static final float[] SIN = { 0.0f, 0.38f, 0.71f, 0.92f, 1.0f, 0.92f, 0.71f, 0.38f, 0f, -0.38f, -0.71f, -0.92f, -1.0f, -0.92f, -0.71f, -0.38f, };
static final float[] COS = { 1.0f, 0.92f, 0.71f, 0.38f, 0f, -0.38f, -0.71f, -0.92f, -1.0f, -0.92f, -0.71f, -0.38f, 0.0f, 0.38f, 0.71f, 0.92f, };

static abstract class Unit implements SoundSource {
    float old_x, old_y;

	int cost;

	float x;
	float y;
	float z;
	float xo;
	float yo;
	float zo;
	Side side;
	boolean selected = false;

	int damage = 0;
	int maxDamage = 10;

	CompoundSprite sprite = new CompoundSprite();

	int xTile() {return (int)(old_x/16);}
	int yTile() {return (int)(old_y/16);}
	int oldRevealRadius = 0;
	int revealRadius = 0;
	float p = 0;
	int xNextTile = 0;
	int yNextTile = 0;

	int xTarget = 0;
	int yTarget = 0;

	int xOld = 0;
	int yOld = 0;
	boolean moving = false;
	boolean manualBlock = false;

	float selectTime = 0;

	void moveTo(int xDestination, int yDestination) {
		xTarget = xDestination;
		yTarget = yDestination;
	}
	void setSide(Side side) {this.side = side;}
	void init(Side side) {
		setSide(side);
		world.mapRenderer.sprites.add(sprite);
	}
	void remove(Units units) {
		if (revealRadius != 0 && side != null) {
			side.fogOfWar.unRevealStatic(xTile(), yTile(), revealRadius);
			if (!manualBlock) world.map.unblock(xTile(), yTile());
		}
	}
	void tick() {
		if (selected) selectTime--;

		if (damage >= maxDamage / 2) {
			if (rand.nextFloat()*3 < (damage / (float) maxDamage)-0.4f) {
				val xa = rand.nextFloat() * 2 - 1;
				val ya = rand.nextFloat() * 2 - 1;
				val za = rand.nextFloat() * 2 - 1;
				world.particleSystem.addParticle(new SmokeParticle(x + rand.nextFloat() * 12 - 6, y + rand.nextFloat() * 12 - 6, z + 6, xa, ya, za, SmokeParticle.TYPE_FIRE));
			}
		}

		xo = x;
		yo = y;
		zo = z;

		int _xt = (int) (x / 16);
		int _yt = (int) (y / 16);
		if ((_xt != xTile() || _yt != yTile() || oldRevealRadius!=revealRadius) && side != null) {
			if (oldRevealRadius != 0) {
				side.fogOfWar.unRevealStatic(xTile(), yTile(), oldRevealRadius);
				if (!manualBlock) world.map.unblock(xTile(), yTile());
			}
			old_x = x; old_y = y;
			oldRevealRadius = revealRadius;
			if (oldRevealRadius != 0) {
				side.fogOfWar.revealStatic(xTile(), yTile(), oldRevealRadius);
				if (!manualBlock) world.map.block(xTile(), yTile(), this);
			}
		}
	}
	void hide() {sprite.x = -1000;}
	void render() {sprite.x = (int) x;}
	void postRender(Graphics2D g) {
		if (selected) {
			float t = (selectTime - aleph);
			float a = 1 - t * 0.2f;
			if (a < 0) a = 0;
			if (a > 2) a = 2;
			if (a > 1) a = 1 - (a - 1) * 0.5f;

			if (t < 0) t = 0;
			g.setColor(new java.awt.Color(1, 1, 1, a));
			int s = (int) (t * 4);

			int r = 12 + s;
			g.drawRect((int) xo - r - world.xCam, (int) yo - r - world.yCam, r * 2 - 1, r * 2 - 1);
		}
	}
	void addToMinimap(int[] minimapPixels) {
		int x = (int) (xo / 16);
		int y = (int) (yo / 16);
		if (x >= 0 && y >= 0 && x < 64 && y < 64) minimapPixels[x + y * 64] = side.minimapColor;
	}
	boolean isInside(int x0, int y0, int x1, int y1) {
		x0 -= 12;
		y0 -= 12;
		x1 += 12;
		y1 += 12;
		return x >= x0 && y >= y0 && x <= x1 && y <= y1;
	}
	void select() {select(0);}
	void select(float delay) {
		sound_engine.addSound(new SelectionSound(delay*4f/TICKS_PER_SECOND), this);
		selectTime = 5 + delay * 4f;
		selected = true;
	}
	void unSelect() {selected = false;}
	float getDistanceSqr(int x0, int y0) {
		val xd = x0 - x;
		val yd = y0 - y;
		return xd * xd + yd * yd;
	}
	boolean shouldConnectToWall() {return false;}
	boolean isGroupSelectable() {return !isBuilding();}
	boolean isBuilding() {return false;}
	void renderImageTo(Graphics2D g, int x, int y) {sprite.renderImageTo(g, x, y);}
	abstract String getName();
	
	ButtonType[] getButtons() {return null;}
	int getCost() {return cost;}
	public float getXSoundPos() {return (x-world.xCam-world.gameView.width/2.0f)/(float)world.gameView.width;}
	public float getYSoundPos() {return (y-world.yCam-world.gameView.height/2.0f)/(float)world.gameView.height;}
}
static abstract class Building extends Unit {
	int width = 1;
	int height = 1;

	Sprite baseSprite = new Sprite();
	Sprite baseShadow = new Sprite();

	Image[] baseImage;
	Image image;
	Image shadowImage;

	Building(Image[] baseImage, Image shadowImage) {
		this.baseImage = baseImage;
		this.shadowImage = shadowImage;
		revealRadius = 8;
	}
	Building(Image image, Image shadowImage) {
		this.image = image;
		this.shadowImage = shadowImage;
		revealRadius = 8;
	}
	void buildAt(int xTile, int yTile) {
		xo = x = xTile * 16 + 8;
		yo = y = yTile * 16 + 8;
	}
	void setSide(Side side) {
		super.setSide(side);

		if (baseImage != null) image = baseImage[side.id];

        old_x = x; old_y = y;
		
		baseSprite.xo = -16;
		baseSprite.yo = -16-height*8;
		baseSprite.x = (int) x;
		baseSprite.zo = 8+(height-1)*16;
		baseSprite.y = (int) y-16+height*8;
		baseSprite.image = image;
		baseSprite.w = width * 16;
		baseSprite.h = height * 16;

		baseShadow.x = (int) x;
		baseShadow.y = (int) y;
		baseShadow.xo = -16;
		baseShadow.yo = -16;
		baseShadow.layer = Sprite.LAYER_SHADOW;
		baseShadow.image = shadowImage;
		baseShadow.w = width * 16;
		baseShadow.h = height * 16;

		if (width == 1) {
			baseSprite.xo += 4;
			baseShadow.xo += 4;
		}
		if (height == 1) {
			baseSprite.yo += 4;
			baseShadow.yo += 4;
		}

		sprite.sprites.add(baseSprite);
		sprite.sprites.add(baseShadow);
		sprite.x = (int) x;
		sprite.y = (int) y;
		sprite.w = width * 16;
		sprite.h = height * 16;
	}
	void init(Side side) {
		super.init(side);

		for (int x = xTile(); x < xTile() + width; x++)
			for (int y = yTile(); y < yTile() + height; y++) {
				side.fogOfWar.revealStatic(x, y, revealRadius);
				world.map.block(x, y, this);
			}
	}
	void updateWalls(int x, int y) {
		val u = world.map.getUnitAt(x, y);
		if (u == null) return;
		if (u instanceof Wall) ((Wall) u).updateImages();
	}
	void setImages(BufferedImage image, BufferedImage shadowImage) {
		this.image = image;
		this.shadowImage = shadowImage;

		baseSprite.image = image;
		baseShadow.image = shadowImage;
	}
	void remove(Units units) {
		super.remove(units);

		world.mapRenderer.sprites.remove(sprite);

		for (int x = xTile(); x < xTile() + width; x++)
			for (int y = yTile(); y < yTile() + height; y++) {
				side.fogOfWar.unRevealStatic(x, y, revealRadius);
				world.map.unblock(x, y);
			}
	}
	boolean isInside(int x0, int y0, int x1, int y1) {
		x0 -= 8 + (width - 1) * 16;
		y0 -= 8 + (height - 1) * 16;
		x1 += 8;
		y1 += 8;
		return x >= x0 && y >= y0 && x <= x1 && y <= y1;
	}
	void tick() {
		if (selected) selectTime--;

		xo = x;
		yo = y;
		zo = z;
	}
	void addToMinimap(int[] minimapPixels) {
		val x = (int) (xo / 16);
		val y = (int) (yo / 16);
		for (int xx = x; xx < x + width; xx++)
			for (int yy = y; yy < y + height; yy++)
				minimapPixels[xx + yy * 64] = side.minimapColor;
	}
	void postRender(Graphics2D g) {
		if (selected) {
			float t = (selectTime - aleph);
			float a = 1 - t * 0.2f;
			if (a < 0) a = 0;
			if (a > 2) a = 2;
			if (a > 1) a = 1 - (a - 1) * 0.5f;

			if (t < 0) t = 0;
			g.setColor(new java.awt.Color(1, 1, 1, a));
			val s = (int) (t * 4);

			val r = 12 + s;
			g.drawRect((int) xo - r - world.xCam, (int) yo - r - world.yCam, r * 2 - 1 + (width - 1) * 16, r * 2 - 1 + (height - 1) * 16);
		}
	}
	boolean isBuilding() {return true;}
	void renderImageTo(Graphics2D g, int x, int y) {sprite.renderImageTo(g, x - (width - 1) * 8, y - (height - 1) * 8);}
	boolean isSilo() {return false;}
	Building newInstance() {try {return this.getClass().newInstance();} catch (Exception e) {throw new RuntimeException(e);}} // TODO: Make this waaay more elegant!
}
static abstract class MoveableUnit extends Unit {
	PathFinder pathFinder = new PathFinder();
	static int[] travelCosts = new int[64 * 64];

	void updatePathfinder() {
		if (pathFinder.isPathing && !moving) {
			p = 0;
			pathFinder.continueFindingPath(150);
	
			if (!pathFinder.isPathing && pathFinder.pathP > 0) {
				val p = pathFinder.path[--pathFinder.pathP];
				xOld = (int) (x / 16) * 16 + 8;
				yOld = (int) (y / 16) * 16 + 8;
				xNextTile = (p & 63) * 16 + 8;
				yNextTile = (p >> 6) * 16 + 8;
				
				pathFound();
				moving = true;
	
				if (xOld == xNextTile && yOld == yNextTile)
					this.p = 1;
			}
		}
	}
	void moveTo(int xDestination, int yDestination) {
		super.moveTo(xDestination, yDestination);

		int[] map = world.map.tiles;
		Unit[] blockMap = world.map.blockMap;
		for (int i = 0; i < 64 * 64; i++) {
			if (!side.fogOfWar.isRevealed(i & 63, i / 64)) {
				travelCosts[i] = 50;
			} else {
				travelCosts[i] = terrain.ctiles.tiles[map[i]].terrain.travelCost;
				if ((terrain.ctiles.tiles[map[i]].terrain.passableFlags & terrain.PASSABLE_LAND)==0) travelCosts[i] = 0;

				val blockUnit = blockMap[i];
				if (blockUnit != null && blockUnit != this) {
					if (blockUnit.moving)
						travelCosts[i] += 15;
					else
						travelCosts[i] = 0;
				}
			}
		}
		
		if (moving)
			pathFinder.startFindingPath(travelCosts, (int) (xNextTile / 16), (int) (yNextTile / 16), xDestination, yDestination);
		else 
			pathFinder.startFindingPath(travelCosts, (int) (x / 16), (int) (y / 16), xDestination, yDestination);
	}
	void pathFound() {}
	boolean canTravelTo(int x, int y) {
		if (world.map.tiles[x + y * 64] < 15) return false;
		val blockUnit = world.map.blockMap[x + y * 64];
		if (blockUnit != null && blockUnit != this) return false;
		return true;
	}
}
static abstract class Vehicle extends MoveableUnit {
	Sprite baseSprite;
	Sprite baseShadow;

	Unit targetUnit;

	float za;
	float dira;
	float dir;
	float speed = 0;

	float vehicleSpeed = 0;

	int baseAngle;
	BufferedImage[][] baseImages;
	BufferedImage[] shadowImages;
	
	boolean aimingAtEnemy = false;
	int reloadTime = 0;
	
	Vehicle(BufferedImage[][] baseImages, BufferedImage[] shadowImages) {
		this.baseImages = baseImages;
		this.shadowImages = shadowImages;
		revealRadius = 5;
	}
	void setSide(Side side) {
		super.setSide(side);

		int w = baseImages[0][0].getWidth();
		int h = baseImages[0][0].getHeight();

		baseSprite = new Sprite();
		baseSprite.image = baseImages[side.id][0];
		baseSprite.xo = -w / 2;
		baseSprite.yo = -w / 2 - (h - w) + 2;
		baseSprite.zo = 6;

		baseShadow = new Sprite();
		baseShadow.layer = Sprite.LAYER_SHADOW;
		baseShadow.image = shadowImages[0];
		baseShadow.xo = -w / 2 + 1;
		baseShadow.yo = -w / 2 + 1;

		sprite.sprites.add(baseSprite);
		sprite.sprites.add(baseShadow);

		dir = (float) (Math.random() * 32);
		dira = 0;
		speed = 0;

		x = xOld = (int) (x / 16) * 16 + 8;
		y = yOld = (int) (y / 16) * 16 + 8;
	}
	void pathFound() {speed = 0;}
	void move() {
		float tileSpeed = world.map.getTerrainTypeAtPixel(x, y).travelCost / 10.0f;

		if (xNextTile != xOld && yNextTile != yOld) tileSpeed *= 1.41;

		p += speed / tileSpeed;
		while (p >= 1) {
			xOld = xNextTile;
			yOld = yNextTile;

			if (pathFinder.pathP > 0 && !pathFinder.isPathing) {
				int p = pathFinder.path[pathFinder.pathP - 1];
				int xx = (p & 63) * 16 + 8;
				int yy = (p >> 6) * 16 + 8;

				if (canTravelTo(xx / 16, yy / 16)) {
					xNextTile = xx;
					yNextTile = yy;
					world.map.block(xx / 16, yy / 16, this);
					pathFinder.pathP--;
					this.p--;
				} else {
					this.p = 0;
					speed = 0;
					moveTo(xTarget, yTarget);
				}
			} else {
				p = 0;
				speed = 0;
				x = xNextTile;
				y = yNextTile;
				moving = false;
				return;
			}
		}
	}
	void turn() {
		if (xNextTile != xOld || yNextTile != yOld)
			turnTowards(xNextTile, yNextTile);
	}
	void aimAt(int xt, int yt) {
		if (moving) return;
		turnTowards(xt, yt);
	}
	void turnTowards(int xt, int yt) {
		float tDir = ((int) (Math.atan2(yt - yOld, xt - xOld) * 16 / (Math.PI) + 8f));
		while (tDir < 0) tDir += 32;
		while (tDir > 31) tDir -= 32;

		float dDir = tDir - dir;
		while (dDir >= 16) dDir -= 32;
		while (dDir < -16) dDir += 32;

		dDir = dDir * 0.2f;
		if (dDir * dDir < 0.1) {
			dir = tDir;
			dDir = 0;
			aimingAtEnemy = true;
		}
		if (dDir != 0) speed *= 0.98f;
		if (dDir < -1) {speed *= 0.25f; dDir = -1;}
		if (dDir > 1) {speed *= 0.25f; dDir = 1;}
		dir += dDir;
		while (dir < 0) dir += 32;
		while (dir > 31) dir -= 32;
	}
	void tick() {
		super.tick();
		
		if (reloadTime > 0) reloadTime--;

		if (targetUnit != null && !side.units.seenEnemies.contains(targetUnit)) targetUnit = null;

		if ((targetUnit == null && rand.nextInt(10) == 0) || rand.nextInt(40) == 0)
			targetUnit = side.units.closestSeenEnemy(x, y, revealRadius);

		updatePathfinder();

		if (moving) {
			move();
			turn();

			speed *= 0.92f;
			speed += vehicleSpeed / 100.0f;

			x = xOld + (xNextTile - xOld) * p;
			y = yOld + (yNextTile - yOld) * p;
		}

		aimingAtEnemy = false;
		if (targetUnit!=null) aimAt((int)(targetUnit.x), (int)(targetUnit.y));

		baseAngle = (int) (dir / 2 + 0.5f) & 15;

		z += za;

		if (z < 0) {z = 0; za *= -0.4f;}

		za -= 0.8f;
	}
	void render() {
		super.render();

		int xx = (int) (xo + (x - xo) * aleph);
		int yy = (int) (yo + (y - yo) * aleph);
		int zz = (int) (zo + (z - zo) * aleph);

		sprite.x = xx;
		sprite.y = yy;
		sprite.z = zz;

		baseSprite.x = xx;
		baseSprite.y = yy;
		baseSprite.z = zz;
		baseSprite.image = baseImages[side.id][baseAngle];

		baseShadow.x = xx + zz / 2;
		baseShadow.y = yy + zz;
		baseShadow.image = shadowImages[baseAngle];
	}
}
static abstract class TurretVehicle extends Vehicle {
	Sprite turret;
	Sprite turretShadow;

	float tdira;
	float tdir;
	int turretAngle;

	BufferedImage[] turretImages;
	BufferedImage[] turretShadowImages;

	TurretVehicle(BufferedImage[][] baseImages, BufferedImage[] shadowImages, BufferedImage[] turretImages, BufferedImage[] turretShadowImages) {
		super(baseImages, shadowImages);
		this.turretImages = turretImages;
		this.turretShadowImages = turretShadowImages;
	}
	void setSide(Side side) {
		super.setSide(side);

		int w = baseImages[0][0].getWidth();
		int h = baseImages[0][0].getHeight();

		turret = new Sprite();
		turret.image = turretImages[0];
		turret.xo = -w / 2;
		turret.yo = -w / 2 - (h - w) + 2;
		turret.zo = 10;

		turretShadow = new Sprite();
		turretShadow.layer = Sprite.LAYER_SHADOW;
		turretShadow.image = turretShadowImages[0];
		turretShadow.xo = -w / 2;
		turretShadow.yo = -w / 2;

		sprite.sprites.add(turret);
		sprite.sprites.add(turretShadow);

		tdir = (float) (Math.random() * 32);
		tdira = 0;
	}
	void aimAt(int xt, int yt) {
		aimingAtEnemy = false;
		turnTurretTowards(xt, yt);
	}
	void turnTurretTowards(int xt, int yt) {
		float tDir = ((int) (Math.atan2(yt - y, xt - x) * 16 / (Math.PI) + 8f));
		while (tDir < 0) tDir += 32;
		while (tDir > 31) tDir -= 32;

		float dDir = tDir - tdir;
		while (dDir >= 16) dDir -= 32;
		while (dDir < -16) dDir += 32;

		dDir = dDir * 0.2f;
		if (dDir * dDir < 0.1) {
			tdir = tDir;
			dDir = 0;
			aimingAtEnemy = true;
		}

		if (dDir < -1) dDir = -1;
		if (dDir > 1) dDir = 1;
		tdir += dDir;
		while (tdir < 0) tdir += 32;
		while (tdir > 31)
			tdir -= 32;
	}	

	void tick() {
		super.tick();

		tdir += tdira;
		if (Math.random() < 0.01) tdira = (float) (Math.random() * 2 - 1) * 0.2f;
		while (tdir < 0) tdir += 32;
		while (tdir > 31) tdir -= 32;

		turretAngle = (int) (tdir / 2 + 0.5f) & 15;
		
		if (aimingAtEnemy && reloadTime==0 && targetUnit!=null) {
			reloadTime=40;
			shootAt(targetUnit);
		}
	}
	void shootAt(Unit target) {
		val xta = (int) (SIN[baseAngle] * 2.9f) + (int) (SIN[turretAngle] * -17.9f);
		val yta = (int) (-COS[baseAngle] * 2.9f) - (int) (COS[turretAngle] * -17.9f);
		world.particleSystem.addParticle(Missile.createMissile(x - xta, y - yta, 6, target.x, target.y));
	}
	void render() {
		super.render();

		val xta = (int) (SIN[baseAngle] * 2.9f) + (int) (SIN[turretAngle] * -4.9f);
		val yta = (int) (-COS[baseAngle] * 2.9f) - (int) (COS[turretAngle] * -4.9f);

		val xx = (int) (xo + (x - xo) * aleph);
		val yy = (int) (yo + (y - yo) * aleph);
		val zz = (int) (zo + (z - zo) * aleph);

		turret.x = xx - xta;
		turret.y = yy - yta;
		turret.z = zz + 4;
		turret.image = turretImages[turretAngle];

		turretShadow.x = xx + (zz + 4) / 2 - xta;
		turretShadow.y = yy + (zz + 4) - yta;
		turretShadow.image = turretShadowImages[turretAngle];
	}
}
static class Car extends Vehicle {
	Car(int xTile, int yTile) {
		super(Voxels.carBase, Voxels.carShadow);
		
		this.x = xTile*16;
		this.y = yTile*16;
		
		vehicleSpeed = 1.0f;
		revealRadius = 6;
	}
	String getName() {return "Car";}
}
static class Harvester extends Vehicle {
	static final int MAX_CARGO = 500;
	static final int DUMP_SPEED = 5;
	static final int GEM_LIFE = 100;

	int cargo = 0;
	int xLastSiloPos = 0;
	int yLastSiloPos = 0;
	int xLastHarvestPos = 0;
	int yLastHarvestPos = 0;

	boolean shouldAutofindTarget = false;

	Harvester(int xTile, int yTile) {
		super(Voxels.harvesterBase[0], Voxels.harvesterShadow);

		xLastSiloPos = xTile;
		yLastSiloPos = yTile;

		this.x = xTile * 16;
		this.y = yTile * 16;

		vehicleSpeed = 0.8f;
		revealRadius = 4;
	}
	void tick() {
		super.tick();

		if (nextToSilo()) {
			xLastSiloPos = xTile();
			yLastSiloPos = yTile();

			if (cargo > 0) {
				dumpMoney();
				return;
			}
		}

		if (moving || pathFinder.isPathing) return;

		if (cargo < MAX_CARGO) harvest();
	}
	void dumpMoney() {
		val xp = this.x + rand.nextFloat() * 8 - 4;
		val yp = this.y + rand.nextFloat() * 8 - 4;

		val xa = -SIN[baseAngle] * 0;
		val ya = COS[baseAngle] * 0;
		world.particleSystem.addParticle(new DebrisParticle(terrain.GEM.id, xp, yp, 6.0f, xa, ya, 3.0f));

		int toDump = cargo;
		if (toDump>DUMP_SPEED) toDump = DUMP_SPEED;
		cargo-=toDump;
		world.playerSide.addMoney(toDump);
		baseImages = Voxels.harvesterBase[1];
		if (cargo == 0) {
			baseImages = Voxels.harvesterBase[0];
			moveTo(xLastHarvestPos, yLastHarvestPos);
			shouldAutofindTarget = true;
		}
	}
	boolean nextToSilo() {
		if (isSilo(world.map.getUnitAt(xTile(), yTile() - 1))) return true;
		if (isSilo(world.map.getUnitAt(xTile(), yTile() + 1))) return true;
		if (isSilo(world.map.getUnitAt(xTile() - 1, yTile()))) return true;
		if (isSilo(world.map.getUnitAt(xTile() + 1, yTile()))) return true;
		return false;
	}
	boolean isSilo(Unit unit) {
		if (unit == null) return false;
		if (!unit.isBuilding()) return false;
		if (unit.side != side) return false;
		if (!((Building)unit).isSilo()) return false;

		return true;
	}
	void harvest() {
		int x = xTile();
		int y = yTile();

		if (x >= 0 && y >= 0 && x < 64 && y < 64) {
			int tile = world.map.getTile(x, y);

			if (terrain.ctiles.tiles[tile].terrain == terrain.GEM) {
				if (rand.nextInt(2) == 0) {
					val xp = this.x + rand.nextFloat() * 8 - 4;
					val yp = this.y + rand.nextFloat() * 8 - 4;

					val xa = -SIN[baseAngle] * 3;
					val ya = COS[baseAngle] * 3;

					world.particleSystem.addParticle(new DebrisParticle(terrain.GEM.id, xp, yp, 6.0f, xa, ya, 3.0f));
				}

				xLastHarvestPos = xTile();
				yLastHarvestPos = yTile();
				cargo++;
				shouldAutofindTarget = true;
				baseImages = Voxels.harvesterBase[1];
				if (cargo == MAX_CARGO) {
					baseImages = Voxels.harvesterBase[2];
					moveTo(xLastSiloPos, yLastSiloPos);
				}

				if (rand.nextInt(GEM_LIFE) == 0) {
					tile--;
					if (terrain.ctiles.tiles[tile].terrain != terrain.GEM) {
						tile = terrain.ctiles.TYPE_SAND;
						if (!moving && !pathFinder.isPathing) {}
					}
					world.map.setTile(x, y, tile);
				}
			} else {if (shouldAutofindTarget) findNewTarget();}
		}
	}
	void findNewTarget() {
		int r = 4;
		int closest = 0;
		int xTarget = -1;
		int yTarget = -1;

		for (int x = xTile() - r; x <= xTile() + r; x++) {
			for (int y = yTile() - r; y <= yTile() + r; y++) {
				if (x >= 0 && y >= 0 && x < 64 && y < 64) {
					int tile = world.map.getTile(x, y);
					if (terrain.ctiles.tiles[tile].terrain == terrain.GEM) {
						int xd = (x - xLastSiloPos);
						int yd = (y - yLastSiloPos);
						int distance = xd * xd + yd * yd;
						if (closest == 0 || distance < closest) {
							closest = distance;
							xTarget = x;
							yTarget = y;
						}
					}
				}
			}
		}

		if (xTarget != -1) {
			moveTo(xTarget, yTarget);
			shouldAutofindTarget = true;
		}
	}
	void moveTo(int xDestination, int yDestination) {
		super.moveTo(xDestination, yDestination);
		shouldAutofindTarget = false;
	}
	String getName() {return "Gem van";}
}
static class Headquarter extends Building {
	static ButtonType[] buildButtons =  {
		BuildButtonType.SLAB,
		BuildButtonType.WALL,
		BuildButtonType.WALL_TURRET,
		BuildButtonType.HQ,
	};
	
	Headquarter() {
		super(Voxels.hqBase, Voxels.hqShadow);
		width = 2;
		height = 2;
		
		cost = 500;
	}
	String getName() {return "HQ";}
	boolean isSilo() {return true;}
	ButtonType[] getButtons() {return buildButtons;}
}
static class Slime extends MoveableUnit {
	BufferedImage baseImages[];
	BufferedImage shadowImage;
	Sprite baseSprite;
	Sprite baseShadow;

	float xJumpSource, yJumpSource;
	int xJumpTarget, yJumpTarget;

	boolean jumping = false;
	int jumpTime = 0;
	int jumpDuration = 0;

	Slime(int xTile, int yTile) {
		this.x = xTile * 16;
		this.y = yTile * 16;
		
		old_x = x; old_y = y;
		
		xJumpTarget = xTile;
		yJumpTarget = yTile;

		revealRadius = 6;
		manualBlock = true;

		this.baseImages = MonsterSprites.blob;
		this.shadowImage = MonsterSprites.blobShadow;
	}
	void setSide(Side side) {
		super.setSide(side);

		int w = baseImages[0].getWidth();
		int h = baseImages[0].getHeight();

		baseSprite = new Sprite();
		baseSprite.image = baseImages[0];
		baseSprite.xo = -w / 2;
		baseSprite.yo = -w / 2 - (h - w) + 2;
		baseSprite.zo = 6;

		baseShadow = new Sprite();
		baseShadow.layer = Sprite.LAYER_SHADOW;
		baseShadow.image = shadowImage;
		baseShadow.xo = -w / 2 + 1;
		baseShadow.yo = -w / 2 - (h - w) + 2 + 1;

		sprite.sprites.add(baseSprite);
		sprite.sprites.add(baseShadow);

		x = xOld = (int) (x / 16) * 16 + 8;
		y = yOld = (int) (y / 16) * 16 + 8;
	}
	void init(Side side) {
		super.init(side);
		if (world.map.getUnitAt(xTile(), yTile()) != null) System.out.println("Jesus christ, batman!!");
		world.map.block(xTile(), yTile(), this);
	}
	void remove(Units units) {
		super.remove(units);
		world.map.unblock(xJumpTarget, yJumpTarget);
	}
	void tick() {
		super.tick();
		if (world.map.getUnitAt(xJumpTarget, yJumpTarget) != this) System.out.println("HOOLY CRAP!");

		if (!jumping) {
			if (rand.nextInt(10) == 0) findRandomTarget();
		} else {
			jumpTime++;
			if (jumpTime == jumpDuration) {
				x = xJumpTarget * 16 + 8;
				y = yJumpTarget * 16 + 8;
				z = 0;
				jumpTime = 0;
				jumping = false;
				baseSprite.image = baseImages[0];
			} else {
				val progress = jumpTime / (float) jumpDuration;
				x = xJumpSource + (xJumpTarget * 16 + 8 - xJumpSource) * progress;
				y = yJumpSource + (yJumpTarget * 16 + 8 - yJumpSource) * progress;
				z = (float) (Math.sin(progress * Math.PI) * jumpDuration);

				baseSprite.image = baseImages[1];
			}
		}
	}
	void findRandomTarget() {
		int x = xTile() + rand.nextInt(5) - 2;
		int y = yTile() + rand.nextInt(5) - 2;
		if (x >= 0 && y >= 0 && x < 64 && y < 64) {
			if (world.map.getUnitAt(x, y) != null) return;

			if ((world.map.getTerrainTypeAt(x, y).passableFlags & terrain.PASSABLE_LAND) == 0) return;


			world.map.unblock(xTile(), yTile());
			world.map.block(x, y, this);

			xJumpSource = this.x;
			yJumpSource = this.y;
			xJumpTarget = x;
			yJumpTarget = y;
			jumping = true;
			jumpTime = 0;

			int xd = xJumpTarget - xTile();
			int yd = yJumpTarget - yTile();

			jumpDuration = (int) (Math.sqrt(xd * xd + yd * yd) * 6);
		}
	}
	void render() {
		super.render();

		int xx = (int) (xo + (x - xo) * aleph);
		int yy = (int) (yo + (y - yo) * aleph);
		int zz = (int) (zo + (z - zo) * aleph);

		sprite.x = xx;
		sprite.y = yy;
		sprite.z = zz;

		baseSprite.x = xx;
		baseSprite.y = yy;
		baseSprite.z = zz;

		baseShadow.x = xx + zz / 2;
		baseShadow.y = yy + zz;
		baseShadow.image = shadowImage;
	}
	String getName() {return "Slime";}
}
static class Tank extends TurretVehicle {
	Tank(int xTile, int yTile) {
		super(Voxels.tankBase, Voxels.baseShadow, Voxels.turret, Voxels.turretShadow);
		
		x = xTile*16;
		y = yTile*16;
		
		vehicleSpeed = 0.5f;
	}
	String getName() {return "Tank";}
}
static class TurretWall extends Building {
	BufferedImage[] turretImages;
	BufferedImage[] turretShadowImages;
	
	Sprite turret, turretShadow;

	float tdira;
	float tdir;
	int turretAngle;
	int reloadTime;
	Unit targetUnit;
	boolean aimingAtEnemy;
	
	TurretWall() {
		super(Voxels.wallTurretBase, Voxels.wallTurretShadow);
		
		width = 1;
		height = 1;

		cost = 200;
		revealRadius = 4;
		
		turretImages = Voxels.turret;
		turretShadowImages = Voxels.turretShadow;
	}
	void setSide(Side side) {
		super.setSide(side);

		val w = turretImages[0].getWidth();
		val h = turretImages[0].getHeight();

		turret = new Sprite();
		turret.image = turretImages[0];
		turret.xo = -w / 2;
		turret.yo = -w / 2 - (h - w) + 2;
		turret.zo = 10;

		turretShadow = new Sprite();
		turretShadow.layer = Sprite.LAYER_SHADOW;
		turretShadow.image = turretShadowImages[0];
		turretShadow.xo = -w / 2;
		turretShadow.yo = -w / 2;

		sprite.sprites.add(turret);
		sprite.sprites.add(turretShadow);

		tdir = (float) (Math.random() * 32);
		tdira = 0;
		
		render();
	}
	void init(Side side) {
		super.init(side);

		updateWalls(xTile() - 1, yTile());
		updateWalls(xTile() + 1, yTile());
		updateWalls(xTile(), yTile() - 1);
		updateWalls(xTile(), yTile() + 1);	
		
		render();
	}
	void aimAt(int xt, int yt) {turnTurretTowards(xt, yt);}
	void turnTurretTowards(int xt, int yt) {
		float tDir = ((int) (Math.atan2(yt - y, xt - x) * 16 / (Math.PI) + 8f));
		while (tDir < 0) tDir += 32;
		while (tDir > 31) tDir -= 32;

		float dDir = tDir - tdir;
		while (dDir >= 16) dDir -= 32;
		while (dDir < -16) dDir += 32;

		dDir = dDir * 0.2f;
		if (dDir * dDir < 0.1) {
			tdir = tDir;
			dDir = 0;
			aimingAtEnemy = true;
		}

		if (dDir < -1) dDir = -1;
		if (dDir > 1) dDir = 1;
		tdir += dDir;
		while (tdir < 0) tdir += 32;
		while (tdir > 31)
			tdir -= 32;
	}	  

	void tick() {
		super.tick();

		if (reloadTime>0) reloadTime--;

		if (targetUnit != null && !side.units.seenEnemies.contains(targetUnit)) targetUnit = null;

		if ((targetUnit == null && rand.nextInt(10) == 0) || rand.nextInt(40) == 0) 
			targetUnit = side.units.closestSeenEnemy(x, y, revealRadius);
		aimingAtEnemy = false;
		if (targetUnit!=null) aimAt((int)(targetUnit.x), (int)(targetUnit.y));

		tdir += tdira;
		if (!aimingAtEnemy && Math.random() < 0.01) tdira = (float) (Math.random() * 2 - 1) * 0.2f;
		while (tdir < 0) tdir += 32;
		while (tdir > 31) tdir -= 32;

		turretAngle = (int) (tdir / 2 + 0.5f) & 15;
		
		if (aimingAtEnemy && reloadTime==0 && targetUnit!=null) {
			reloadTime=40;
			shootAt(targetUnit);
		}
	}
	void shootAt(Unit target) {
		val xta = (int) (0) + (int) (SIN[turretAngle] * -17.9f);
		val yta = (int) (0) - (int) (COS[turretAngle] * -17.9f);
		world.particleSystem.addParticle(Missile.createMissile(x - xta, y - yta, 6, target.x, target.y));
	}	

	void render() {
		super.render();

		int xta = (int) (+ SIN[turretAngle] * -4.9f);
		int yta = (int) (- COS[turretAngle] * -4.9f);

		int xx = (int) (xo + (x - xo) * aleph);
		int yy = (int) (yo + (y - yo) * aleph);
		int zz = (int) (zo + (z - zo) * aleph);

		turret.x = xx - xta;
		turret.y = yy - yta;
		turret.z = zz + 10;
		turret.image = turretImages[turretAngle];

		turretShadow.x = xx + (zz + 4) / 2 - xta;
		turretShadow.y = yy + (zz + 4) - yta;
		turretShadow.image = turretShadowImages[turretAngle];
	}
	String getName() {return "Turret";}
	boolean shouldConnectToWall() {return true;}
}
static class Units {
	List<Unit> units = new ArrayList<Unit>();
	List<Unit> selectedUnits = new ArrayList<Unit>();
	List<Unit> seenEnemies = new ArrayList<Unit>();
	Side side;

	Units(Side side) {this.side = side;}
	void addUnit(Unit unit) {unit.init(side); units.add(unit);}
	void tick() {
		for (int i = 0; i < units.size(); i++) units.get(i).tick();
            // .alive was removed
			/*if (!units.get(i).alive) {
				units.get(i).remove(this);
				units.remove(i);
				i--;
			}*/
	}
	void render(FogOfWar fogOfWar) {
		for (int i = 0; i < units.size(); i++) {
			val unit = units.get(i);
			if (fogOfWar.isVisible(unit)) {
				unit.addToMinimap(fogOfWar.minimapPixels);
				unit.render();
			} else unit.hide();
		}
	}
	void postRender(Graphics2D g) {
		for (int i = 0; i < units.size(); i++) {
			val unit = units.get(i);
			unit.postRender(g);
		}
	}
	void unSelectAll() {
		selectedUnits.clear();
		for (int i = 0; i < units.size(); i++) {
			val unit = units.get(i);
			unit.unSelect();
		}
	}
	void selectClosest(int x, int y) {
		float closestDistance = 0;
		Unit closestUnit = null;
		for (int i = 0; i < units.size(); i++) {
			val unit = units.get(i);
			if (side.fogOfWar.isVisible(unit) && unit.isInside(x, y, x, y)) {
				float distance = unit.getDistanceSqr(x, y);
				if (closestUnit == null || distance < closestDistance) {
					closestUnit = unit;
					closestDistance = distance;
				}
			}
		}

		if (closestUnit != null) {
			selectedUnits.add(closestUnit);
			closestUnit.select();
		}
	}
	boolean selectAll(int x0, int y0, int x1, int y1) {
		float delay = 0;
		boolean hasSelected = false;
		int unselectable = 0;
		Unit unselectableUnit = null;

		for (int i = 0; i < units.size(); i++) {
			val unit = units.get(i);
			if (side.fogOfWar.isVisible(unit) && unit.isInside(x0, y0, x1, y1)) {
				if (unit.isGroupSelectable()) {
					unit.select(delay);
					selectedUnits.add(unit);
					delay = (float) (Math.random());
					hasSelected = true;
				} else {
					unselectable++;
					unselectableUnit = unit;
				}
			}
		}

		if (!hasSelected && unselectable == 1) {
			unselectableUnit.select();
			selectedUnits.add(unselectableUnit);
			hasSelected = true;
		}

		return hasSelected;
	}
	void moveAllSelected(int xTarget, int yTarget) {
		for (int i = 0; i < units.size(); i++) {
			val unit = units.get(i);
			if (unit.selected) unit.moveTo(xTarget, yTarget);
		}
	}
	void clearSeenEnemies() {seenEnemies.clear();}
	void setVisible(Side side) {
		val fogOfWar = side.fogOfWar;
		for (int i = 0; i < units.size(); i++) {
			val unit = units.get(i);
			if (fogOfWar.isVisible(unit)) side.units.seenEnemies.add(unit);
		}
	}
	Unit closestSeenEnemy(float x, float y, float radius) {
		return closest(seenEnemies, x, y, radius);
   }

	Unit closest(List<Unit> units, float x, float y, float radius) {
		float closestD = -1;
		Unit closest = null;
		for (int i = 0; i < units.size(); i++) {
			Unit unit = units.get(i);
			float xd = (unit.x-x)/16.0f;
			float yd = (unit.y-y)/16.0f;
			if (xd<-radius || xd>radius || yd<-radius || yd>radius) continue;

			float d = xd*xd+yd*yd;
			if (d<radius*radius) {
				if (closestD==-1 || d<closestD) {
					closestD = d;
					closest = unit;
				}
			}
		}
		
		return closest;
	}
}
static class Wall extends Building {
	Wall() {
		super(Voxels.walls[0], Voxels.wallShadows[0]);
		width = 1;
		height = 1;
		revealRadius = 0;
		cost = 50;
	}
	void init(Side side) {
		super.init(side);

		updateImages();

		updateWalls(xTile() - 1, yTile());
		updateWalls(xTile() + 1, yTile());
		updateWalls(xTile(), yTile() - 1);
		updateWalls(xTile(), yTile() + 1);
	}
	void setSide(Side side) {
		super.setSide(side);

		setImages(Voxels.walls[10], Voxels.wallShadows[10]);
	}
	void updateImages() {
		val left = isWall(xTile() - 1, yTile());
		val right = isWall(xTile() + 1, yTile());
		val up = isWall(xTile(), yTile() - 1);
		val down = isWall(xTile(), yTile() + 1);

		val image = (up ? 1 : 0) + (left ? 2 : 0) + (down ? 4 : 0) + (right ? 8 : 0);
		setImages(Voxels.walls[image], Voxels.wallShadows[image]);
	}
	boolean isWall(int x, int y) {
		val u = world.map.getUnitAt(x, y);
		if (u == null) return false;
		return u.shouldConnectToWall();
	}
	boolean shouldConnectToWall() {return true;}
	void addToMinimap(int[] minimapPixels) {
		int x = (int) (xo / 16);
		int y = (int) (yo / 16);
		if (x >= 0 && y >= 0 && x < 64 && y < 64) minimapPixels[x + y * 64] = 0xff606060;
	}
	String getName() {return "Wall";}
}

}