package takns;

import lombok.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import takns.gui.*;
import takns.units.*;
import takns.sprites.*;

public class main {

/// state

static String current_status = "Initializing..";
static float aleph;

static BufferStrategy bufferStrategy;
static Image titleImage;
static VolatileImage image;
static UiComponent gameComponent = new UiComponent();
static InputHandler inputHandler;
static boolean noLogo = false;
static long lastTime = System.nanoTime() / 1000000;
static int passedTime = 0;

/// constants

static final int MOJANG_SPLASH_TIME = 800;
static final int GAME_SPLASH_TIME = 800;

static final int SCREEN_WIDTH = 320;
static final int SCREEN_HEIGHT = 240;
static final int SCALE = 2;

static final int TICKS_PER_SECOND = 25;

static final int PANEL_WIDTH = 64 + 8;
static final int MAX_TICKS_PER_FRAME = 10;

/// utils

static <T> java.util.List<T> l() {return new ArrayList<T>();}
static RuntimeException ex(String s) {return new RuntimeException(s);}
static GraphicsConfiguration graphics_configuration; // used by ImageConverter_convert
static sound.SoundEngine sound_engine;
static Random rand = new Random();
static void sleep(long ms) {
	try {Thread.sleep(ms);}
	catch (InterruptedException e) {}
}

/// main

public static void main(String[] args) {
    val me = new Canvas() {
        public void update(Graphics gr) {paint(gr);}
        public void paint(Graphics gr) {
            if (noLogo) return;

            if (image == null || image.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                image = createVolatileImage(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);
                val g = image.createGraphics(); intro.MojangLogo.render(g); g.dispose();
            }

            gr.drawImage(image, 0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        }
    };

    me.setIgnoreRepaint(true);
    me.setMinimumSize(new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE));
    me.setMaximumSize(new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE));
    me.setPreferredSize(new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE));
    me.setBounds(0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);

    val frame = new JFrame("Takns");
    frame.add(me);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);

    graphics_configuration = me.getGraphicsConfiguration();
    image = me.createVolatileImage(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);
    me.createBufferStrategy(2);
    bufferStrategy = me.getBufferStrategy();

    new Thread(new Runnable() {public void run() {
            { // setup
                long before = System.currentTimeMillis();
                Text.init();
                me.repaint();
                
                val game_starter_thread_is_done = new AtomicBoolean(false);
                val gst = new Thread(new Runnable() {public void run() {
                        sound_engine = new sound.SoundEngine();
                        sound_engine.start();

                        current_status = "Creating components..";
                        gameComponent.init(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

                        world.gameView.init(0, 0, SCREEN_WIDTH - PANEL_WIDTH, SCREEN_HEIGHT);
                        gameComponent.addComponent(world.gameView);

                        val panel = new PanelComponent();
                        panel.init(SCREEN_WIDTH - PANEL_WIDTH, 0, PANEL_WIDTH, SCREEN_HEIGHT);
                        gameComponent.addComponent(panel);

                        val hud = new HudComponent();
                        hud.init(0, 0, 0, 0);
                        gameComponent.addComponent(hud);

                        world.init();

                        current_status = "Starting up..";
                        sleep(GAME_SPLASH_TIME + MOJANG_SPLASH_TIME);

                        game_starter_thread_is_done.set(true);
                    }});
                gst.start();

                titleImage = new intro.TitleBuilder().buildTitleImage();
                long after = System.currentTimeMillis();

                sleep(MOJANG_SPLASH_TIME - (after - before));

                noLogo = true;
                while (!game_starter_thread_is_done.get()) {
                    if (image == null || image.validate(me.getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) 
                        image = me.createVolatileImage(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);

                    if (image != null) {
                        val g = image.createGraphics();
                        g.drawImage(titleImage, 0, 0, null);
                        g.setColor(new Color(0.1f, 0.1f, 0.2f, 0.8f));
                        g.fillRect(0, SCREEN_HEIGHT - 10, SCREEN_WIDTH, 10);
                        Text.drawString(current_status, g, 2, SCREEN_HEIGHT - 7);
                        g.setColor(new Color(1, 1, 1, 1.0f));
                        g.dispose();

                        val gr = bufferStrategy.getDrawGraphics();
                        gr.drawImage(image, 0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);

                        gr.dispose();
                    }

                    bufferStrategy.show();

                    sleep(20);
                }

                try {gst.join();} catch (InterruptedException e1) {}
            }

            inputHandler = new InputHandler(gameComponent, SCALE);
            me.addMouseMotionListener(inputHandler);
            me.addMouseListener(inputHandler);
            me.addKeyListener(inputHandler);
            Graphics2D g = null;
            if (image != null) g = image.createGraphics();

            while (true) {
                if (image == null || image.validate(me.getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                    if (g != null) {
                        g.dispose();
                        g = null;
                    }

                    image = me.createVolatileImage(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);
                    g = image.createGraphics();
                }

                synchronized (inputHandler.lock) {
                    world.hoveredComponent = gameComponent.getComponentAt(inputHandler.xMouse, inputHandler.yMouse);
                    
                    // Timer.advanceTime
                    long now = System.nanoTime() / 1000000;
                    long frameTime = now - lastTime;
                    if (frameTime < 0) throw ex("mojang: WARNING: Negative frame time detected, switching to average frame times.");
                    passedTime += frameTime;
                    lastTime = now;
                    int ticks = passedTime / (1000 / TICKS_PER_SECOND);
                    passedTime -= ticks * (1000 / TICKS_PER_SECOND);
                    
                    // updateTime
                    if (ticks > MAX_TICKS_PER_FRAME) ticks = MAX_TICKS_PER_FRAME;
                    for (int i = 0; i < ticks; i++) {
                        world.tick();
                        world.moveCamera(inputHandler.keys[KeyEvent.VK_UP], inputHandler.keys[KeyEvent.VK_DOWN], inputHandler.keys[KeyEvent.VK_LEFT], inputHandler.keys[KeyEvent.VK_RIGHT]);
                        gameComponent.tickAll();
                    }
                    aleph = (float) passedTime / (float) (1000 / TICKS_PER_SECOND);
                    
                    world.render();
                    gameComponent.renderAll(g);
                }

                val gr = bufferStrategy.getDrawGraphics();
                gr.drawImage(image, 0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
                gr.dispose();
                bufferStrategy.show();

                sleep(2);
            }
        }}).start();
}

/// mojang

static class FogOfWar {
	static final int VISIBLE_COLOR = 0x00000000;
	static final int SHADE_COLOR = 0x80000000;
	static final int HIDDEN_COLOR = 0xff000000;

	int[] litCorners = new int[65 * 65];
	int[] staticLitCorners = new int[65 * 65];

	int[] lightMap = new int[64 * 64];
	int[] revealMap = new int[64 * 64];
	int[] minimapPixels = new int[64 * 64];

	BufferedImage minimapImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB_PRE);

	FogOfWar() {minimapPixels = ((DataBufferInt)minimapImage.getRaster().getDataBuffer()).getData();}
	void tick() {
		for (int i = 0; i < 65 * 65; i++) {
			litCorners[i] &= 1;
			if (staticLitCorners[i] != 0) litCorners[i] = 3;
		}
	}
	void prepareRender() {
		for (int y = 0; y < 64; y++) {
			for (int x = 0; x < 64; x++) {
				int tile = 0;
				int tile2 = 0;
				for (int i = 0; i < 4; i++) {
					if ((litCorners[(x + (i & 1) + (y + (i >> 1)) * 65)] & 1) != 0) 
						tile += 1 << i;
					if ((litCorners[(x + (i & 1) + (y + (i >> 1)) * 65)] & 2) != 0) 
						tile2 += 1 << i;
				}

				lightMap[x + y * 64] = tile2;
				revealMap[x + y * 64] = tile;
				minimapPixels[x + y * 64] = tile2 != 0 ? VISIBLE_COLOR : tile == 0 ? HIDDEN_COLOR : SHADE_COLOR;
			}
		}
	}
	void revealStatic(int x, int y, int r) {
		float rr = r * 0.95f;
		for (int xx = x - r; xx <= x + r; xx++) {
			if (xx >= 0 && xx < 65) {
				for (int yy = y - r; yy <= y + r; yy++) {
					if (yy >= 0 && yy < 65) {
						int _x = xx - x;
						int _y = yy - y;
						if (xx < x) _x--;
						if (yy < y) _y--;
						int rad = _x * _x + _y * _y;
						if (rad < rr * rr) staticLitCorners[xx + yy * 65]++;
					}
				}
			}
		}
	}
	void unRevealStatic(int x, int y, int r) {
		float rr = r * 0.95f;
		for (int xx = x - r; xx <= x + r; xx++) {
			if (xx >= 0 && xx < 65) {
				for (int yy = y - r; yy <= y + r; yy++) {
					if (yy >= 0 && yy < 65) {
						int _x = xx - x;
						int _y = yy - y;
						if (xx < x) _x--;
						if (yy < y) _y--;
						int rad = _x * _x + _y * _y;
						if (rad < rr * rr) staticLitCorners[xx + yy * 65]--;
					}
				}
			}
		}
	}
	void reveal(int x, int y, int r) {
		float rr = r * 0.95f;
		for (int xx = x - r; xx <= x + r; xx++) {
			if (xx >= 0 && xx < 65) {
				for (int yy = y - r; yy <= y + r; yy++) {
					if (yy >= 0 && yy < 65) {
						int _x = xx - x;
						int _y = yy - y;
						if (xx < x) _x--;
						if (yy < y) _y--;
						int rad = _x * _x + _y * _y;
						if (rad < rr * rr) litCorners[xx + yy * 65] = 3;
					}
				}
			}
		}
	}
	boolean isVisible(Sprite sprite) {
		int xCorner = (sprite.x + sprite.xo + 16) / 16;
		int yCorner = (sprite.y + sprite.yo - sprite.z + 16) / 16;
		if (xCorner < 0 || yCorner < 0 || xCorner > 64 || yCorner > 64) return false;
		return litCorners[xCorner + yCorner * 65] == 3;
	}
	boolean isVisible(Unit unit) {
		int xCorner = (int) (unit.x + 16) / 16;
		int yCorner = (int) (unit.y - unit.z + 16) / 16;
		if (xCorner < 0 || yCorner < 0 || xCorner > 64 || yCorner > 64) return false;
		return litCorners[xCorner + yCorner * 65] == 3;
	}
	boolean isRevealed(int x, int y) {
		if (x < 0 || y < 0 || x >= 64 || y >= 64) return false;
		return revealMap[x + y * 64] != 0;
	}
	boolean isLit(int x, int y) {
		if (x < 0 || y < 0 || x >= 64 || y >= 64) return false;
		return lightMap[x + y * 64] != 0;
	}
}
static BufferedImage ImageConverter_convert(BufferedImage img, int transparency) {
    BufferedImage image = null;
    if (transparency==0) image = graphics_configuration.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.OPAQUE);
    else if (transparency==1) image = graphics_configuration.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.BITMASK);
    else if (transparency==2) image = graphics_configuration.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.TRANSLUCENT);
    else throw new IllegalArgumentException("!!!!");
    image.getRaster().setRect(img.getRaster());
    return image;
}
static BufferedImage ImageConverter_convert(int width, int height, int[] pixels, int transparency) {
    val image = new BufferedImage(width, height, transparency!=0 ? BufferedImage.TYPE_INT_ARGB_PRE : BufferedImage.TYPE_INT_RGB);
    image.setRGB(0, 0, width, height, pixels, 0, width);
    return ImageConverter_convert(image, transparency);
}
static class InputHandler implements MouseMotionListener, MouseListener, KeyListener {
	UiComponent component;
	int scale;
	UiComponent dragSource;
	int dragButton = -1;
	int xDragStart;
	int yDragStart;
	int xMouse = -100, yMouse = -100;
	UiComponent lastHovered = null;

	Object lock = new Object();
	boolean[] keys = new boolean[256];

	InputHandler(UiComponent component, int scale) {this.component = component; this.scale = scale;}
	public void mouseDragged(MouseEvent e) {synchronized (lock) {mouseMoved(e);}}
	public void mouseMoved(MouseEvent e) {
		synchronized (lock) {
			xMouse = e.getX() / scale;
			yMouse = e.getY() / scale;
			UiComponent hovered = component.getComponentAt(xMouse, yMouse);

			if (dragSource != null) {
				hovered = dragSource;
				int xd = xDragStart - xMouse;
				int yd = yDragStart - yMouse;
				if (xd * xd + yd * yd > 4) dragSource.drag(xMouse, yMouse, dragButton, xDragStart, yDragStart);
			} else {
				if (hovered != lastHovered) {
					if (lastHovered != null) lastHovered.mouseOut(xMouse, yMouse);
					if (hovered != null) hovered.mouseOver(xMouse, yMouse);

					lastHovered = hovered;
				}

				if (lastHovered != null) lastHovered.mouseMoved(xMouse, yMouse);
			}
		}
	}
	public void mouseClicked(MouseEvent e) {
		synchronized (lock) {
			mouseMoved(e);

			xMouse = e.getX() / scale;
			yMouse = e.getY() / scale;

			if (lastHovered != null) lastHovered.mouseClicked(xMouse, yMouse, e.getButton(), e.getClickCount());
		}
	}
	public void mouseEntered(MouseEvent e) {synchronized (lock) {mouseMoved(e);}}
	public void mouseExited(MouseEvent e) {
		synchronized (lock) {
			mouseMoved(e);
			xMouse = -999;
		}
	}
	public void mousePressed(MouseEvent e) {
		synchronized (lock) {
			xMouse = e.getX() / scale;
			yMouse = e.getY() / scale;
			int button = e.getButton();

			if (button != dragButton) stopDragging(xMouse, yMouse, dragButton);
			mouseMoved(e);
			startDragging(xMouse, yMouse, button);

			if (lastHovered != null) lastHovered.mousePressed(xMouse, yMouse, button);
		}
	}
	public void startDragging(int xMouse, int yMouse, int button) {
		synchronized (lock) {
			xDragStart = xMouse;
			yDragStart = yMouse;
			dragSource = component.getComponentAt(xMouse, yMouse);
			if (dragSource != null) {
				dragSource.startDrag(xMouse, yMouse, button);
				dragButton = button;
			}
		}
	}
	public void stopDragging(int xMouse, int yMouse, int button) {
		synchronized (lock) {
			if (dragSource != null) {
				dragSource.stopDrag(xMouse, yMouse, button);
				dragSource = null;
				dragButton = -1;
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		synchronized (lock) {
			mouseMoved(e);

			xMouse = e.getX() / scale;
			yMouse = e.getY() / scale;
			int button = e.getButton();

			stopDragging(xMouse, yMouse, button);

			if (lastHovered != null) lastHovered.mouseReleased(xMouse, yMouse, button);
		}
	}
	public void keyPressed(KeyEvent e) {
		synchronized (lock) {if (e.getKeyCode()<256) keys[e.getKeyCode()] = true;}
	}
	public void keyReleased(KeyEvent e) {
		synchronized (lock) {if (e.getKeyCode()<256) keys[e.getKeyCode()] = false;}
	}
	public void keyTyped(KeyEvent e) {}
}
static int[] noiseMap(int levels, int detailLevels) {
    int[] map = new int[1*1];
    
    for (int level = 0; level < levels; level++) {
        int size = (1 << level);
        int nextSize = 2 << level;
        int[] pixels = new int[nextSize * nextSize];
        
        int noise = 256 >> level;
        if (level < detailLevels) noise /= 20;
        int diagonalNoise = noise * 14 / 10;

        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++) {
                int h0 = map[((x) + (y) * size) & (size * size - 1)];
                int h1 = map[((x + 1) + (y) * size) & (size * size - 1)];
                int h2 = map[((x) + (y + 1) * size) & (size * size - 1)];
                int h3 = map[((x + 1) + (y + 1) * size) & (size * size - 1)];
                pixels[(x * 2) + (y * 2) * nextSize] = h0;
                pixels[(x * 2 + 1) + (y * 2) * nextSize] = ((h1 + h0) >> 1) + rand.nextInt(noise) - noise / 2;
                pixels[(x * 2) + (y * 2 + 1) * nextSize] = ((h2 + h0) >> 1) + rand.nextInt(noise) - noise / 2;
                pixels[(x * 2 + 1) + (y * 2 + 1) * nextSize] = ((h0 + h1 + h2 + h3) >> 2) + rand.nextInt(diagonalNoise) - diagonalNoise / 2;
            }
        map = pixels;
    }
    
    return map;
}
static class Side {
	static final int MAX_MONEY = 99999;

	Units units;
	FogOfWar fogOfWar;

	int money = 2000;

	int id = 0;
	int minimapColor = 0xffff0000;
						
	Side(int id) {
		this.id = id;
		
		if (id==1) minimapColor = 0xff00ff00;
		reset();
	}
	void addMoney(int moneyToAdd) {money += moneyToAdd; if (money > MAX_MONEY) money = MAX_MONEY;}
	void tick() {fogOfWar.tick(); units.tick();}
	void reset() {
		units = new Units(this);
		fogOfWar = new FogOfWar();
	}
	void chargeMoney(int cost) {
		money-=cost;
		if (money < 0) throw new IllegalStateException("Negative money!");
	}
}
static class Sprite implements Comparable<Sprite> { // is extended by CompoundSprite
	static final int LAYER_TERRAIN_SPLAT = -1;
	static final int LAYER_SHADOW = -1;
	static final int LAYER_NORMAL = 0;
	static final int LAYER_AIR = 1;
	static final int LAYER_HUD = 2;

	int x, y, z;
	int xo = -8, yo = -8, zo = 0;
	int w = 16, h = 16;
	int layer = Sprite.LAYER_NORMAL;
	Image image;
	float alpha = 1;

	public int compareTo(Sprite s) {
		if (s == this) return 0;
		if (layer < s.layer) return -1;
		if (layer > s.layer) return 1;
		if (y + zo+z < s.y + s.zo+s.z) return -1;
		if (y + zo+z > s.y + s.zo+s.z) return 1;
		return (hashCode() < s.hashCode()) ? -1 : 1;
	}
	void addTo(SortedSet<Sprite> visibleSprites) {visibleSprites.add(this);}
	void renderImageTo(Graphics2D g, int xOffs, int yOffs) {g.drawImage(image, x + xo+xOffs, y - z + yo+yOffs, null);}
}
static class CompoundSprite extends Sprite {
	ArrayList<Sprite> sprites = new ArrayList<Sprite>();

	CompoundSprite() {}
	CompoundSprite(CompoundSprite sprite) {
		this.x = sprite.x;
		this.y = sprite.y;
		this.w = sprite.w;
		this.h = sprite.h;
		
		this.xo = sprite.xo;
		this.yo = sprite.yo;
		this.zo = sprite.zo;
	}
	void addTo(SortedSet<Sprite> visibleSprites) {for (int i=0; i<sprites.size(); i++) sprites.get(i).addTo(visibleSprites);}
	void renderImageTo(Graphics2D g, int x, int y) {
		val sortedSprites = new TreeSet<Sprite>();
		sortedSprites.addAll(sprites);
		
		for (Sprite sprite : sortedSprites) sprite.renderImageTo(g, x-this.x, y-this.y);
	}
}
static int[] make_terrain_Map_tiles_for_intro() {
    terrain.ctiles.createTiles();
	sprites.Voxels.reduced = true;
	sprites.Voxels.buildVoxels();
    return new takns.terrain.Map().tiles;
}
static class world {
	static takns.terrain.Map map;
	static MapRenderer mapRenderer;
	static particles.ParticleSystem particleSystem;

	static Side playerSide = new Side(0);
	static Side monsterSide = new Side(1);
	
	static Side[] sides = {playerSide, monsterSide};
	static GameView gameView = new GameView(playerSide);

	static int cameraPanTime = 5;
	static int xCam, yCam;
	static int xCamTarget, yCamTarget;
	static int xCamStart, yCamStart;
	static int cameraMotionSteps = -1;
    
    // minimap
	static int[] minimap_pixels;
	static BufferedImage minimap_image;
	static void minimap_updateArea(int x1, int y1, int w, int h) {
        if (minimap_pixels == null) return;
		for (int y=y1; y<y1+h; y++)
			for (int x=x1; x<x1+w; x++)
				minimap_pixels[x+y*64] = terrain.ctiles.tiles[world.map.tiles[x+y*64]].terrain.color;
	}

	static void init() {
		current_status = "Building tiles..";
		terrain.ctiles.createTiles();
        current_status = "Building sprites..";
        Sprites.buildSprites();
        MonsterSprites.buildSprites();
		current_status = "Building voxels..";
		Voxels.reduced = false;
		Voxels.buildVoxels();
		sprites.side_BuildIcons = playerSide;

		current_status = "Building font..";
		Text.init();

		current_status = "Generating map..";
		boolean mapOk = false;
		int attempt = 0;
		do {
			if (attempt > 0) current_status = "Regenerating map.. (" + attempt + " maps rejected)";
			attempt++;

			map = new takns.terrain.Map();

            for (int i=0; i<sides.length; i++) sides[i].reset();

            particleSystem = new particles.ParticleSystem();
            mapRenderer = new MapRenderer(playerSide, SCREEN_WIDTH - PANEL_WIDTH, SCREEN_HEIGHT);

            if (!placePlayer(playerSide)) continue;
            if (!placeMonsters(monsterSide)) continue;

			mapOk = true;
		}
		while (!mapOk);

        minimap_image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        minimap_pixels = ((DataBufferInt) minimap_image.getRaster().getDataBuffer()).getData();
        minimap_updateArea(0, 0, 64, 64);
	}
	static boolean placeMonsters(Side side) {
		for (int i=0; i<64; i++) {
			int x = rand.nextInt(64);
			int y = rand.nextInt(64);
			if ((map.getUnitAt(x, y)==null) && (map.getTerrainTypeAt(x,y).passableFlags&terrain.PASSABLE_LAND)>0) 
				side.units.addUnit(new Slime(x, y));
		}
		
		return true;
	}
	static boolean placePlayer(Side side) {
		val ok = getRandomPosition(terrain.GRASS, 6, 6);
		if (!ok) return false;

		int gems = countTerrain(terrain.GEM, grp_xTile + 3, grp_yTile + 3, 8, 8);
		if (gems < 10) return false;

		for (int xx = grp_xTile + 2; xx < grp_xTile + 4; xx++)
			for (int yy = grp_yTile + 2; yy < grp_yTile + 4; yy++)
				map.tiles[xx + yy * map.width] = terrain.ctiles.TYPE_SLAB;

		val hq = new Headquarter();
		hq.buildAt(grp_xTile + 2, grp_yTile + 2);
		side.units.addUnit(hq);

		side.units.addUnit(new Harvester(grp_xTile + 1, grp_yTile + 3));
		side.units.addUnit(new Harvester(grp_xTile + 4, grp_yTile ));
		side.units.addUnit(new Car(grp_xTile + 2, grp_yTile + 5));
		side.units.addUnit(new Tank(grp_xTile + 5, grp_yTile + 4));
		side.units.addUnit(new Tank(grp_xTile + 3, grp_yTile + 4));

		centerCam((grp_xTile + 3) * 16, (grp_yTile + 3) * 16);

		return ok;
	}
	static int grp_xTile;
	static int grp_yTile;

	static UiComponent hoveredComponent;

	static int countTerrain(terrain.Terrain terrainType, int x, int y, int w, int h) {
		int count = 0;
		for (int xx = x - w; xx <= x + w; xx++)
			for (int yy = y - h; yy <= y + h; yy++)
				if (xx >= 0 && yy >= 0 && xx < map.width && yy < map.height) if (terrain.ctiles.tiles[map.tiles[xx + yy * map.width]].terrain == terrainType)
					count++;

		return count;
	}
	static boolean getRandomPosition(terrain.Terrain terrainType, int w, int h) {
		int tries = 0;
		findPos: do {
			tries++;
			int x = rand.nextInt(map.width - w);
			int y = rand.nextInt(map.height - h);
			for (int xx = x; xx < x + w; xx++)
				for (int yy = y; yy < y + h; yy++)
					if (terrain.ctiles.tiles[map.tiles[xx + yy * map.width]].terrain != terrainType) continue findPos;

			grp_xTile = x;
			grp_yTile = y;

			return true;
		}
		while (tries > 100);
		return false;
	}
	static void tick() {
		particleSystem.tick();
		for (int i=0; i<sides.length; i++) sides[i].tick();
		if (cameraMotionSteps != -1) {
			cameraMotionSteps++;
			if (cameraMotionSteps == cameraPanTime) cameraMotionSteps = -1;
		}

		for (int i=0; i<sides.length; i++) {
			sides[i].units.clearSeenEnemies();
			for (int j=0; j<sides.length; j++) {
				if (i==j) continue;
				sides[j].units.setVisible(sides[i]);
			}
		}
	}
	static void render() {
		playerSide.fogOfWar.prepareRender();
		particleSystem.render();
		for (int i=0; i<sides.length; i++) sides[i].units.render(playerSide.fogOfWar);

		if (cameraMotionSteps != -1) {
			float step = (cameraMotionSteps + aleph) / cameraPanTime;
			float x = xCamStart + (xCamTarget - xCamStart) * step;
			float y = yCamStart + (yCamTarget - yCamStart) * step;
			setCam((int) x, (int) y);
		}
	}
	static void centerCamSmooth(int x, int y) {
		cameraPanTime = 5;
		cameraMotionSteps = 0;
		xCamStart = xCam;
		yCamStart = yCam;
		xCamTarget = x - gameView.width / 2;
		yCamTarget = y - gameView.height / 2;
		if (xCamTarget < 0) xCamTarget = 0;
		if (yCamTarget < 0) yCamTarget = 0;
		if (xCamTarget >= map.width * 16 - gameView.width) xCamTarget = map.width * 16 - gameView.width - 1;
		if (yCamTarget >= map.height * 16 - gameView.height) yCamTarget = map.height * 16 - gameView.height - 1;
	}
	static void centerCam(int x, int y) {
		cameraMotionSteps = -1;
		setCam(x - gameView.width / 2, y - gameView.height / 2);
	}
	static void setCam(int x, int y) {
		xCam = x;
		yCam = y;

		if (xCam < 0) xCam = 0;
		if (yCam < 0) yCam = 0;
		if (xCam >= map.width * 16 - gameView.width) xCam = map.width * 16 - gameView.width - 1;
		if (yCam >= map.height * 16 - gameView.height) yCam = map.height * 16 - gameView.height - 1;
	}
	static void smoothCam(int x, int y) {
		cameraPanTime = 1;
		cameraMotionSteps = 0;
		xCamStart = xCam;
		yCamStart = yCam;
		xCamTarget = x;
		yCamTarget = y;
		if (xCamTarget < 0) xCamTarget = 0;
		if (yCamTarget < 0) yCamTarget = 0;
		if (xCamTarget >= map.width * 16 - gameView.width) xCamTarget = map.width * 16 - gameView.width - 1;
		if (yCamTarget >= map.height * 16 - gameView.height) yCamTarget = map.height * 16 - gameView.height - 1;
	}
	static void moveCam(int x, int y) {
		cameraMotionSteps = -1;
		setCam(xCam + x, yCam + y);
	}
	static void postRender(Graphics2D g) {for (int i=0; i<sides.length; i++) sides[i].units.postRender(g);}
	static void moveCamera(boolean u, boolean d, boolean l, boolean r) {
		if (!u && !d && !l && !r) return;
		int panSpeed = 16;
		int xd = 0;
		int yd = 0;
		if (u) yd-=panSpeed;
		if (d) yd+=panSpeed;
		if (l) xd-=panSpeed;
		if (r) xd+=panSpeed;
		smoothCam(xCam+xd, yCam+yd);
	}
}

}