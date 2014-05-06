package takns;

import lombok.*;
import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;

import takns.main.*;
import static takns.main.*;
import takns.units.*;
import takns.synth.*;

public class gui {

static class BuildButton extends UiComponent {
	BufferedImage image;
	int cost;
	ButtonType buildType;
	
	BuildButton(ButtonType buildType) {
		this.image = buildType.getImage();
		if (image==null) throw new IllegalArgumentException("No image for "+buildType);
		this.cost = buildType.getCost();
		this.buildType = buildType;
	}
	static final Color fillColor = new Color(0xff808080);
	static final Color selectColor = new Color(0xff707080);

	void render(Graphics2D g) {
		val pressed = buildType.isActive(); 
		fillBlock(g, x, y, 28, 24, pressed?selectColor:fillColor, pressed);
		
		int xo = image.getWidth()-16;
		int yo = image.getHeight()-16;
		g.drawImage(image, x +(width-16)/2-xo/2, y + 4-2-yo/2+(pressed?1:0), null);

		val costString = "§3"+cost;
		
		Text.drawString(costString, g, x+14-(costString.length()-2)*3, y+18);
	}
	static final Color upColor = new Color(0.0f, 0.0f, 0.0f, 0.7f);
	static final Color downColor = new Color(1.0f, 1.0f, 1.0f, 0.7f);

	void fillBlock(Graphics2D g, int x, int y, int w, int h, Color color, boolean down) {
		g.setColor(color);
		g.fillRect(x - 1, y - 1, w + 2, h + 2);
		g.setColor(down?downColor:upColor);
		g.fillRect(x, y, w + 1, h + 1);
		g.setColor(down?upColor:downColor);
		g.fillRect(x - 1, y - 1, w + 1, h + 1);
		g.setColor(color);
		g.fillRect(x, y, w, h);
	}
	void mousePressed(int button) {if (button==1) buildType.activate();}
	String getToolTip() {return buildType.getToolTip();}
}
static class BuildButtonType extends ButtonType {
	static final BuildButtonType SLAB = new BuildButtonType(new SlabState(), "Slabs are required for other buildings");
	static final BuildButtonType WALL = new BuildButtonType(new BuildingState(new Wall(), true), "Walls provide protection");
	static final BuildButtonType WALL_TURRET = new BuildButtonType(new BuildingState(new TurretWall()), "Turrets shoot at enemies");
	static final BuildButtonType HQ = new BuildButtonType(new BuildingState(new Headquarter()), "Headquarters produce other buildings");

	State state;
	String toolTip;
	int cost;
	BufferedImage image;

	BuildButtonType(State state, String toolTip) {
		this.state = state;
		this.image = state.getImage();
		if (image==null) throw new IllegalArgumentException("!!!");
		this.cost = state.getCost();
		this.toolTip = toolTip;
	}
	void activate() {world.gameView.setState(state);}
	boolean isActive() {return world.gameView.getState() == state;}
	String getToolTip() {return toolTip;}
	int getCost() {return cost;}
	BufferedImage getImage() {return image;}
}
static class BuildingState extends State {
	boolean repeat;
	Building buildingTemplate;
	int cost = 50;

	BuildingState(Building buildingTemplate) {this(buildingTemplate, false);}
	BuildingState(Building buildingTemplate, boolean repeat) {
		this.buildingTemplate = buildingTemplate;
		this.repeat = repeat;
		cost = buildingTemplate.getCost();
	}
	Building build(int x, int y) {
		val building = buildingTemplate.newInstance();
		building.buildAt(x, y);
		return building;
	}
	void mousePressed(int button) {
		if (button == 3) {
			int xMouse = gameView.xMouse + world.xCam;
			int yMouse = gameView.yMouse + world.yCam;

			int xTile = (xMouse) / 16;
			int yTile = (yMouse) / 16;

			if (canBuild(xTile, yTile)) {
				world.playerSide.chargeMoney(cost);
				val unit = build(xTile, yTile);
				world.playerSide.units.addUnit(unit);
			}
			if (!repeat) gameView.setState(null);
		}

		if (button == 1) gameView.setState(null);
	}
	boolean canBuild(int x, int y) {
		if (world.playerSide.money < cost) return false;
		if (x < 0 || y < 0 || x > 63 || y > 63) return false;

		for (int xx = 0; xx < buildingTemplate.width; xx++)
			for (int yy = 0; yy < buildingTemplate.height; yy++) {
				if (terrain.ctiles.tiles[world.map.getTile(x + xx, y + yy)].terrain != terrain.SLAB) return false;
				if (!world.playerSide.fogOfWar.isLit(x + xx, y + yy)) return false;
				if (world.map.getUnitAt(x + xx, y + yy) != null) return false;
			}

		return true;
	}
	void render(Graphics2D g) {
		if (!gameView.hovered) return;

		int xMouse = gameView.xMouse + world.xCam;
		int yMouse = gameView.yMouse + world.yCam;

		int xTile = (xMouse) / 16;
		int yTile = (yMouse) / 16;

		if (canBuild(xTile, yTile)) {
			g.setColor(new Color(0, 1, 0, 0.5f));
			g.drawRect(xTile * 16 - world.xCam, yTile * 16 - world.yCam, buildingTemplate.width * 16 - 1, buildingTemplate.height * 16 - 1);
		} else {
			g.setColor(new Color(1, 0, 0, 0.5f));
			g.drawRect(xTile * 16 - world.xCam, yTile * 16 - world.yCam, buildingTemplate.width * 16 - 1, buildingTemplate.height * 16 - 1);
		}
	}
	int getCost() {return cost;}
	BufferedImage image;
	BufferedImage getImage() {
		if (image==null) image = sprites.icons_getBufferedImage(buildingTemplate);
		return image;
	}
}
static abstract class ButtonType {
	abstract int getCost();

	abstract void activate();

	abstract String getToolTip();
	
	boolean isActive() {return false;}
	abstract BufferedImage getImage();
}
static class GameView extends UiComponent {
	boolean selecting = false;
	int xSelect0, xSelect1;
	int ySelect0, ySelect1;
	Side side;
	State state = null;
	
	GameView(Side side) {this.side = side;}
	void render(Graphics2D g) {
		world.mapRenderer.render(g);
		world.postRender(g);

		if (state!=null) state.render(g);

		if (selecting) {
			g.setColor(new Color(1, 1, 1, 1.0f));
			int x = xSelect0 < xSelect1 ? xSelect0 : xSelect1;
			int y = ySelect0 < ySelect1 ? ySelect0 : ySelect1;
			int w = xSelect0 < xSelect1 ? xSelect1 - xSelect0 : xSelect0 - xSelect1;
			int h = ySelect0 < ySelect1 ? ySelect1 - ySelect0 : ySelect0 - ySelect1;
			g.drawRect(x, y, w, h);
		}
	}
	void tick() {
		if (state!=null)
			state.tick();
	}
	void drag(int button, int xStart, int yStart) {
		if (button == 2) world.moveCam(xLastDrag - xMouse, yLastDrag - yMouse);
		
		if (state!=null) {
			state.drag(button, xStart, yStart);
			return;
		}
		
		if (button == 1) {
			xSelect0 = xStart;
			ySelect0 = yStart;
			xSelect1 = xMouse;
			ySelect1 = yMouse;
			selecting = true;
		}
	}
	void mousePressed(int button) {
		selecting = false;
		if (button == 1) {
			side.units.unSelectAll();
			setState(null);
		}
		
		if (state!=null) {
			state.mousePressed(button);
			return;
		}
		
		int xMouse = this.xMouse + world.xCam;
		int yMouse = this.yMouse + world.yCam;

		if (button == 3) side.units.moveAllSelected(xMouse / 16, yMouse / 16);
	}
	void mouseReleased(int button) {
		if (button == 1) {
			side.units.unSelectAll();

			if (selecting) {
				int x0 = xSelect0 < xSelect1 ? xSelect0 : xSelect1;
				int y0 = ySelect0 < ySelect1 ? ySelect0 : ySelect1;
				int x1 = xSelect0 < xSelect1 ? xSelect1 : xSelect0;
				int y1 = ySelect0 < ySelect1 ? ySelect1 : ySelect0;
				val selected = side.units.selectAll(x0 + world.xCam, y0 + world.yCam, x1 + world.xCam, y1 + world.yCam);
				if (!selected) side.units.selectClosest(xMouse + world.xCam, yMouse + world.yCam);
				selecting = false;
			} else side.units.selectClosest(xMouse + world.xCam, yMouse + world.yCam);
		}
	}
	void setState(State state) {
		this.state = state;
		if (state!=null) state.init(this);
	}
	State getState() {return state;}
}
static class HudComponent extends UiComponent {
	void render(Graphics2D g) {
		Text.drawString("Money §3"+world.playerSide.money, g, 2, 2);
		
		if (world.hoveredComponent!=null) {
			val toolTip = world.hoveredComponent.getToolTip();
			
			if (toolTip!=null && toolTip!="") {
				g.setColor(new Color(0.1f, 0.1f, 0.2f, 0.8f));
				g.fillRect(0, SCREEN_HEIGHT - 10, world.gameView.width, 10);
				g.setColor(new Color(1, 1, 1, 1.0f));
				
				Text.drawString(toolTip, g, (world.gameView.width-toolTip.length()*6)/2, SCREEN_HEIGHT - 7);
			}
		}
	}
}
static class MapRenderer {
	int width;
	int height;
	Side side;
	int xCam, yCam;

	List<Sprite> sprites = new ArrayList<Sprite>();
	SortedSet<Sprite> visibleSprites = new TreeSet<Sprite>();

	MapRenderer(Side side, int width, int height) {
		this.width = width;
		this.height = height;

		this.side = side;
	}
	void render(Graphics2D g) {
		xCam = world.xCam;
		yCam = world.yCam;
		
		renderMap(g);
		renderSprites(g);
		renderReveal(g);
		renderHide(g);
	}
	void renderSprites(Graphics2D g) {
		visibleSprites.clear();

		for (int i = 0; i < sprites.size(); i++) {
			val sprite = sprites.get(i);
			if (sprite.x + sprite.xo < xCam + width+16 && sprite.y + sprite.yo - sprite.z < yCam + height+16)
				if (sprite.x + sprite.xo > xCam - sprite.w-16 && sprite.y + sprite.yo - sprite.z > yCam - sprite.h-16)
					if (side.fogOfWar.isVisible(sprite)) sprite.addTo(visibleSprites);
		}

		float lastAlpha = 1;
		for (Sprite sprite : visibleSprites) {
			if (sprite.alpha!=lastAlpha) {
				if (sprite.alpha<1) g.setComposite(AlphaComposite.SrcOver.derive(sprite.alpha));
				else g.setComposite(AlphaComposite.SrcOver);
				lastAlpha = sprite.alpha;
			}
			g.drawImage(sprite.image, sprite.x - xCam + sprite.xo, sprite.y - sprite.z - yCam + sprite.yo, null);
		}
		g.setComposite(AlphaComposite.SrcOver);
	}
	void renderMap(Graphics2D g) {
		for (int y = (yCam) / 16; y <= (yCam + height) / 16; y++) {
			for (int x = (xCam) / 16; x <= (xCam + width) / 16; x++) {
				if (side.fogOfWar.revealMap[x+y*64]!=0)
					g.drawImage(terrain.ctiles.tiles[world.map.tiles[x + y * 64]].image, x * 16 - xCam, y * 16 - yCam, null);
			}
		}
	}
	void renderReveal(Graphics2D g) {
		for (int y = (yCam) / 16; y <= (yCam + height) / 16; y++) {
			for (int x = (xCam) / 16; x <= (xCam + width) / 16; x++) {
				if (side.fogOfWar.lightMap[x+y*64]!=15 && side.fogOfWar.revealMap[x+y*64]!=0)
					g.drawImage(terrain.ctiles.tiles[256-32+side.fogOfWar.lightMap[x+y*64]].image, x * 16 - xCam, y * 16 - yCam, null);
			}
		}
	}
	void renderHide(Graphics2D g) {
		for (int y = (yCam) / 16; y <= (yCam + height) / 16; y++) {
			for (int x = (xCam) / 16; x <= (xCam + width) / 16; x++) {
				if (side.fogOfWar.revealMap[x+y*64]!=15)
					g.drawImage(terrain.ctiles.tiles[256-16+side.fogOfWar.revealMap[x+y*64]].image, x * 16 - xCam, y * 16 - yCam, null);
			}
		}
	}	
}
static class MinimapComponent extends UiComponent {
	Side side;
	
	MinimapComponent(Side side) {this.side = side;}
	void render(Graphics2D g) {
		g.setColor(new Color(0, 0, 0, 0.7f));
		g.drawRect(x - 1, y - 1, width, height);
		g.setColor(new Color(1, 1, 1, 0.7f));
		g.drawRect(x, y, width, height);

		g.drawImage(world.minimap_image, x, y, null);
		g.drawImage(side.fogOfWar.minimapImage, x, y, null);

		g.setColor(new Color(1, 1, 1, 0.75f));
		g.drawRect(x - 1 + (world.xCam + 8) / 16, y - 1 + (world.yCam + 8) / 16, world.gameView.width / 16 + 2, world.gameView.height / 16 + 1);
		g.setColor(new Color(1, 1, 1, 1.0f));
	}
	void drag(int button, int xStart, int yStart) {if (button==1) world.centerCam((xMouse) * 16, (yMouse) * 16);}
	void mousePressed(int button) {
		if (button==1) world.centerCamSmooth((xMouse) * 16, (yMouse) * 16);
		if (button==3) side.units.moveAllSelected(xMouse, yMouse);
	}
}
static class PanelComponent extends UiComponent {
	BufferedImage image;
	Side side;
	UiComponent buttonHolder;

	void init(int x, int y, int width, int height) {
		super.init(x, y, width, height);
		side = world.playerSide;

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		val distortion = new PerlinNoise(5);
		val source = new PerlinNoise(5);

		val synth = new Emboss(new Distort(distortion, source));

		double[] noise = synth.create(width, height);

		int[] pixels = new int[width * height];

		for (int yy = 0; yy < height; yy++) {
			for (int xx = 0; xx < width; xx++) {
				double n0 = noise[xx + yy * width];
				int r = (int) (98 + n0 * 3);
				int g = (int) (98 + n0 * 3);
				int b = (int) (98 + n0 * 3);
				
				if (xx == 0) {
					r/=3;
					g/=3;
					b/=3;
				}
				pixels[xx + yy * width] = (255 << 24) | (r << 16) | (g << 8) | b;
			}
		}
		
		image.setRGB(0, 0, width, height, pixels, 0, width);

		val minimap = new MinimapComponent(side);
		minimap.init(x + 4, y + 4, 64, 64);
		addComponent(minimap);

		buttonHolder = new UiComponent();
		buttonHolder.init(x, y + 64, width, height - 64);
		addComponent(buttonHolder);
	}
	Unit lastSelectedUnit = null;

	void render(Graphics2D g) {
		g.drawImage(image, x, y, null);

		val selectedUnits = side.units.selectedUnits;

		if (selectedUnits.size() == 1) {
			val unit = selectedUnits.get(0);
			if (unit != lastSelectedUnit) selectUnit(unit);
			fillBlock(g, x + width / 2 - 32 + 8, y + 64 + 8 + 32 - 32 + 2, 48, 48);
			renderSingleSelectedUnit(g, unit);
		} else {
			if (lastSelectedUnit != null) selectUnit(null);

			if (selectedUnits.size() > 1) renderSelectedUnits(g, selectedUnits);
		}
	}
	void selectUnit(Unit unit) {
		lastSelectedUnit = unit;
		world.gameView.setState(null);
		buttonHolder.removeAllComponents();
		if (unit == null) return;

		ButtonType[] buttons = unit.getButtons();
		if (buttons != null) {
			for (int i = 0; i < buttons.length; i++) {
				int xx = i % 2;
				int yy = i / 2;

				val buildButton = new BuildButton(buttons[i]);
				buildButton.init(x + 4 + 8 - 6 + xx * 32, y + 130 + yy * 26, 28, 24);
				buttonHolder.addComponent(buildButton);
			}
		}
	}
	void fillBlock(Graphics2D g, int x, int y, int w, int h) {
		g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.7f));
		g.fillRect(x, y, w + 1, h + 1);
		g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.7f));
		g.fillRect(x - 1, y - 1, w + 1, h + 1);
	}
	void renderSingleSelectedUnit(Graphics2D g, Unit unit) {
		unit.renderImageTo(g, x + width / 2, y + 64 + 8 + 24);
		Text.drawString(unit.getName(), g, x + (width - unit.getName().length() * 6) / 2, y + 64 + 50);
	}
	void renderSelectedUnits(Graphics2D g, List<Unit> selectedUnits) {
		int p = 0;
		int y = 0;

		int columns = 2;
		if (selectedUnits.size() > 2 * 2) columns = 3;
		if (selectedUnits.size() > 3 * 4) columns = 4;
		int columnSize = 64 / columns;
		int rows = (selectedUnits.size() - 1) / columns;
		int rowSize = (48 + 16) * 4 / (rows + 8);

		fillBlock(g, x + width / 2 - 32, y + 64 + 8 + 32 - 32 + 2, 64, rows * rowSize + rowSize + 16);

		while (p < selectedUnits.size()) {
			int rowLength = selectedUnits.size() - p;
			if (rowLength > columns) rowLength = columns;
			for (int x = 0; x < rowLength; x++) {
				val unit = selectedUnits.get(p++);
				unit.renderImageTo(g, this.x + width / 2 - (rowLength - 1) * columnSize / 2 + x * columnSize, this.y + 64 + 8 + rowSize / 2 + 8 + y * rowSize + 2);
			}
			y++;
		}
	}
}
static class SlabState extends State {
	int cost = 20;
	
	void mousePressed(int button) {
		if (button == 3) {
			int xMouse = gameView.xMouse + world.xCam;
			int yMouse = gameView.yMouse + world.yCam;

			int xTile = (xMouse) / 16;
			int yTile = (yMouse) / 16;

			if (canBuild(xTile, yTile)) {
				world.playerSide.chargeMoney(cost);
				world.map.setTile(xTile, yTile, terrain.ctiles.TYPE_SLAB);
			}
		}

		if (button == 1) {
			gameView.setState(null);
			return;
		}
	}
	boolean canBuild(int x, int y) {
		if (world.playerSide.money<cost) return false;
		if (x < 0 || y < 0 || x >= 64 || y >= 64) return false;
		if (terrain.ctiles.tiles[world.map.getTile(x, y)].terrain != terrain.GRASS) return false;
		if (!world.playerSide.fogOfWar.isLit(x, y)) return false;

		if (x > 0 && terrain.ctiles.tiles[world.map.getTile(x-1, y)].terrain == terrain.SLAB) return true;
		if (x < 63 && terrain.ctiles.tiles[world.map.getTile(x+1, y)].terrain == terrain.SLAB) return true;
		if (y > 0 && terrain.ctiles.tiles[world.map.getTile(x, y-1)].terrain == terrain.SLAB) return true;
		if (y < 63 && terrain.ctiles.tiles[world.map.getTile(x, y+1)].terrain == terrain.SLAB) return true;
		
		return false;
	}
	void render(Graphics2D g) {
		if (!gameView.hovered) return;
		
		int xMouse = gameView.xMouse + world.xCam;
		int yMouse = gameView.yMouse + world.yCam;

		int xTile = (xMouse) / 16;
		int yTile = (yMouse) / 16;

		if (canBuild(xTile, yTile)) {
			g.setColor(new Color(0, 1, 0, 0.5f));
			g.drawRect(xTile*16-world.xCam, yTile*16-world.yCam, 15, 15);
		} else {
			g.setColor(new Color(1, 0, 0, 0.5f));
			g.drawRect(xTile*16-world.xCam, yTile*16-world.yCam, 15, 15);
		}
	}
	int getCost() {return cost;}
	BufferedImage getImage() {return terrain.ctiles.tiles[terrain.ctiles.TYPE_SLAB].image;}
}
static abstract class State {
	GameView gameView;
	
	void init(GameView gameView) {this.gameView = gameView;}
	void tick() {}
	void mousePressed(int button) {}
	void mouseReleased(int button) {}
	void render(Graphics2D g) {}
	void drag(int button, int xStart, int yStart) {}
	abstract int getCost();

	abstract BufferedImage getImage();
}
static class Text {
	static String[] data = {
" ###  ####   #### ####  ##### #####  #### #   # #####     # #   # #     #   # #   #  ###  ####   ###  ####   #### ##### #   # #   # #   # #   # #   # ##### ",
"#   # #   # #     #   # #     #     #     #   #   #       # #  #  #     ## ## ##  # #   # #   # #   # #   # #       #   #   # #   # #   #  # #   # #     #  ",
"##### ####  #     #   # ###   ###   #  ## #####   #       # ###   #     # # # # # # #   # ####  #   # ####   ###    #   #   #  # #  # # #   #     #     #   ",
"#   # #   # #     #   # #     #     #   # #   #   #   #   # #  #  #     #   # #  ## #   # #     #  #  #  #      #   #   #   #  # #  # # #  # #    #    #    ",
"#   # ####   #### ####  ##### #      #### #   # #####  ###  #   # ##### #   # #   #  ###  #      ## # #   # ####    #    ###    #    # #  #   #   #   ##### ",

" ###    #   ####  ####  #   # #####  #### #####  ###   ###   #     # #   # #  ##  #     # #       ##   ##    ###   ###     #   #      #  ",
"#   #  ##       #     # #   # #     #         # #   # #   #  #     # #  ##### ## #     #   #     #       #   #       #    #     #     #  ",
"#   #   #    ###  ####  ##### ####  ####     #   ###   ####  #           # #    #     #     #    #       #   #       #   #       #    #  ",
"#   #   #   #         #     #     # #   #   #   #   #     #             #####  # ##  #       #   #       #   #       #    #     #     #  ",
" ###  ##### ##### ####      # ####   ###    #    ###  ####   #           # #  #  ## #         #   ##   ##    ###   ###     #   #      #  ",

"                                           #          ###   ##### ",
"                    #    # #   #     #     #     ###    #   #   # ",
"             ###   ###    #                            #    #   # ",
"       #            #    # #         #           ###        #   # ",
" #     #                       #     #                 #    ##### ",
		};

	static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#%/\\()[]<>| .,-+*:;'=?_";

	static BufferedImage[][] images = new BufferedImage[chars.length()][8];
	static BufferedImage[][] imageChars = new BufferedImage[256][8];

	static void init() {
		for (int col = 0; col < 8; col++) {
			int charCount = 0;
			for (int row = 0; row < data.length / 5; row++) {
				for (int ch = 0; ch < data[row * 5].length() / 6; ch++) {
					int[] pixels = new int[6 * 6];
					for (int y = 0; y < 5; y++) {
						for (int x = 0; x < 5; x++) {
							char c = data[row * 5 + y].charAt(ch * 6 + x);
							if (c == '#') {
								int r = (255 - (y) * 8) * ((col >> 0) & 1);
								int g = (255 - (y) * 8) * ((col >> 1) & 1);
								int b = (255 - (y) * 8) * ((col >> 2) & 1);
								pixels[x + y * 6] = 0xff000000 + (r << 16) + (g << 8) + (b);
								pixels[x + y * 6 + 1] = 0x90000000;
								pixels[x + y * 6 + 7] = 0x90000000;
								pixels[x + y * 6 + 6] = 0x90000000;
							}
						}
					}

					images[charCount][col] = ImageConverter_convert(6, 6, pixels, 2);
					charCount++;
				}
			}
		}

		for (int i = 0; i < 256; i++) imageChars[i] = images[chars.indexOf((chars.indexOf(i) >= 0)? i : '_')];
	}
	static void drawString(String string, Graphics g, int x, int y) {
		char[] chs = string.toUpperCase().toCharArray();
		g.setColor(new Color(1.0f, 0.5f, 0.2f, 0.3f));
		int col = 7;
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] == 0xa7) { // '§'
				col = chs[++i] - '0';
				continue;
			}
			if (chs[i] >= 0 && chs[i] < 256 && col >= 0 && col < 8)
				g.drawImage(imageChars[chs[i]][col], x, y, null);
			x += 6;
		}
	}
}
static class UiComponent { // is extended
	int x, y, width, height;
	int xMouse;
	int yMouse;
	boolean mouseOver = false;
	List<UiComponent> components = new ArrayList<UiComponent>();

	int xLastDrag = 0;
	int yLastDrag = 0;
	boolean[] mouseDown = new boolean[16];
	boolean hovered = false;

	void init(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	boolean isOver(int xMouse, int yMouse) {
		if (xMouse < x || yMouse < y || xMouse > x + width || yMouse > y + height) return false;
		return true;
	}
	void addComponent(UiComponent component) {components.add(component);}
	void removeAllComponents() {components.clear();}
	final void tickAll() {
		tick();
		for (int i = 0; i < components.size(); i++) {
			val component = components.get(i);
			component.tickAll();
		}
	}
	void tick() {}
	final void renderAll(Graphics2D g) {
		render(g);
		for (int i = 0; i < components.size(); i++) {
			val component = components.get(i);
			component.renderAll(g);
		}
	}
	void render(Graphics2D g) {}
	UiComponent getComponentAt(int xMouse, int yMouse) {
		if (!isOver(xMouse, yMouse)) return null;

		for (int i = 0; i < components.size(); i++) {
			val component = components.get(i);
			if (component.isOver(xMouse, yMouse)) return component.getComponentAt(xMouse, yMouse);
		}

		return this;
	}
	void setMousePos(int xMouse, int yMouse) {
		this.xMouse = xMouse - x;
		this.yMouse = yMouse - y;
	}
	void startDrag(int xMouse, int yMouse, int button) {
		xLastDrag = xMouse;
		yLastDrag = yMouse;
		setMousePos(xMouse, yMouse);
		startDrag(button);
	}
	void stopDrag(int xMouse, int yMouse, int button) {
		setMousePos(xMouse, yMouse);
		stopDrag(button);
	}
	void drag(int xMouse, int yMouse, int button, int xStart, int yStart) {
		setMousePos(xMouse, yMouse);
		drag(button, xStart - x, yStart - y);
		xLastDrag = xMouse;
		yLastDrag = yMouse;
	}
	void mouseClicked(int xMouse, int yMouse, int button, int count) {
		setMousePos(xMouse, yMouse);
		mouseClicked(button, count);
	}
	void mousePressed(int xMouse, int yMouse, int button) {
		if (button < 16) mouseDown[button] = true;
		setMousePos(xMouse, yMouse);
		mousePressed(button);
	}
	void mouseReleased(int xMouse, int yMouse, int button) {
		if (button < 16) mouseDown[button] = false;
		setMousePos(xMouse, yMouse);
		mouseReleased(button);
	}
	void mouseOver(int xMouse, int yMouse) {
		hovered = true;
		setMousePos(xMouse, yMouse);
		mouseOver = true;
		mouseOver();
	}
	void mouseOut(int xMouse, int yMouse) {
		hovered = false;
		setMousePos(xMouse, yMouse);
		mouseOver = false;
		mouseOut();
	}
	void mouseMoved(int xMouse, int yMouse) {
		setMousePos(xMouse, yMouse);
		mouseMoved();
	}
	void startDrag(int button) {}
	void stopDrag(int button) {}
	void drag(int button, int xStart, int yStart) {}
	void mouseClicked(int button, int count) {}
	void mousePressed(int button) {}
	void mouseReleased(int button) {}
	void mouseOver() {}
	void mouseOut() {}
	void mouseMoved() {}
	String getToolTip() {return null;}
}

}