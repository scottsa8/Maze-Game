package idk.mazegame;

import java.util.ArrayList;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.*;
import idk.mazegame.EnemyAI.PathFinding.Node;
import idk.mazegame.EnemyAI.PathFinding.PathFindingSystem;
import static java.lang.Math.sqrt;

import java.nio.file.AtomicMoveNotSupportedException;

// {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. 

public class MazeGame extends Game {
	public SpriteBatch batch;
	private BitmapFont font;
	private String myText, myRightText;
	private String staminaText, coinText, slot1Text, slot2Text,chestText,xpText,nextRoomText,bagText;
	private GlyphLayout layout;
	private Sound sound;
	private Music song1,song2;
	private OrthographicCamera camera;
	private Viewport viewport;

	private  Chest chest;
	private ArrayList<Enemy> enemies = new ArrayList<>();
	public static ArrayList<Projectile> entities = new ArrayList<>();

	private Player player, player2;
	private TiledMap map;
	private IsometricTiledMapRenderer renderer;
	private TiledMapTileLayer entityLayer, floorLayer, overlapLayer;
	private StaticTiledMapTile tile, tmpTile;
	private Vector3 tmpCoords; //buffer
	private final float GAME_WORLD_WIDTH = 1600;
	private final float GAME_WORLD_HEIGHT = 900;
	private final int MAX_INPUT_DELAY = 1;
	private int inputDelay = MAX_INPUT_DELAY;
	private int screenWidth, screenHeight, playerX, playerY, roomCount = 0;
	private int logDelay = 60;
	static World world = new World(new Vector2(0,0), false);
	private Body p1, p2;
	private Box2DDebugRenderer debug;
	private int p1enemies,p2enemies;
	public static boolean debugger =false;
	private Boolean pressed =false;
	private ItemAttributes itemAttrs;
	private int xp;
	private int xp2;
	private int gained;
	private boolean colliding,hitting,hit,xp1Increased,xp2Increased=false;
	private ShapeRenderer shaper;
	private PathFindingSystem pathFinder;
	private String[] attacking = new String[2];
	private String nextRoom = "";
	private String[] roomList = {"saferoom", "testroom", "forestroom", "lake", "cave", "barren"};
	private int level1;
	private int level2;
	private Label LevelLable1;
	private Label LevelLable2;
	private int HP1;
	private int HP2;
	private Label player1HP;
	private Label player2HP;
	private Stage hudStage;
	private Stage Healt;
	private int bossCount;
	@Override
	public void create() {
		//setScreen(new PlayScreen());
		pathFinder = new PathFindingSystem();
		map =  new TmxMapLoader().load("tiledmaps/safeRoom.tmx");
		pathFinder.generateGraph(map);
		renderer = new IsometricTiledMapRenderer(map, 1.2f);
		entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
		floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
		overlapLayer = (TiledMapTileLayer) map.getLayers().get(2);
		tile = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("tiledmaps/tileSprites.png")),32,32,16,16));
		collsion();	

		floorLayer.getCell(23, 7).setTile(tile);
		floorLayer.getCell(24, 8).setTile(tile);

		batch = new SpriteBatch();

		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		float aspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();

		font = new BitmapFont(Gdx.files.internal("UI/myfont.fnt"));
		font.getData().setScale(0.4f);
		myText = "testroom";
		myRightText = "no of rooms: " + roomCount;
		layout = new GlyphLayout();
		layout.setText(font, myText);
		chestText ="";
		bagText = "bag: ";
		staminaText = "stamina: ";
		coinText = "coins: ";
		slot1Text = "slot 1: ";
		slot2Text = "slot 2: ";

		sound = Gdx.audio.newSound(Gdx.files.internal("sound/firered_0001_mono.wav"));
		song1 = Gdx.audio.newMusic(Gdx.files.internal("sound/JRPG_town_loop.ogg"));
		song2 = Gdx.audio.newMusic(Gdx.files.internal("sound/27 Black Market.mp3"));

		camera = new OrthographicCamera();
//		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);
		viewport = new ScreenViewport(camera);
		viewport.apply();
//		camera.position.set(backgroundImage.getX()/2 + Gdx.graphics.getWidth()/2, backgroundImage.getY()/2 + Gdx.graphics.getHeight()/2,0);
		//camera.position.set(848, -48,0);
		camera.position.set(304, -48,0);
		camera.zoom = 0.25f;

		itemAttrs = new ItemAttributes();
		player = new Player(Gdx.files.internal("sprites/player1Sprites.atlas"),itemAttrs,1);
		player2 = new Player(Gdx.files.internal("sprites/player2Sprites.atlas"),itemAttrs,2);
		player.getPlayerSprite().setPosition(310,-64); //310, -64  [10px left, goes left 1 tile 10 px up, goes up 2 tiles]
		player2.getPlayerSprite().setPosition(290,-64);
		//player2.getPlayerSprite().setPosition(184,-69);
		//player2.getPlayerSprite().setPosition(300,-9);
		player.setUseSlot1(Input.Keys.Q);
		player.setUseSlot2(Input.Keys.E);
		player.setUseSlot3(Input.Keys.F);
		player2.setUp(Input.Keys.UP);
		player2.setLeft(Input.Keys.LEFT);
		player2.setDown(Input.Keys.DOWN);
		player2.setRight(Input.Keys.RIGHT);
		player.setCoordinates(new Vector3(24,8,0));
		player2.setCoordinates(new Vector3(23,7,0));
		player.setDefaultValues();
		player2.setDefaultValues();
		p1 = player.createBody(world);
		p2 = player2.createBody(world);
		p1.setUserData("player1");
		p2.setUserData("player2");
		createEnemies();

		song1.setLooping(true);
		//song1.play();
		song1.setVolume(0.5f);

		shaper = new ShapeRenderer();
		//creating the HUD for the player level and other stats 
		hudStage = new Stage();
		Healt = new Stage();

		LevelLable1 = new Label("PLayer 1 Level: 1", new Label.LabelStyle(font,Color.GOLD));
		LevelLable1.setFontScale(1.5f);
		LevelLable2 = new Label("PLayer 2 Level: 1", new Label.LabelStyle(font,Color.WHITE));
		LevelLable2.setFontScale(1.5f);

		player1HP = new Label("Health: ", new Label.LabelStyle(font,Color.GOLD));
		player1HP.setFontScale(1.5f);
		player2HP = new Label("Health: ", new Label.LabelStyle(font,Color.WHITE));
		player2HP.setFontScale(1.5f);

		hudStage.addActor(LevelLable1);
		hudStage.addActor(LevelLable2);
		Healt.addActor(player1HP);
		Healt.addActor(player2HP);

		LevelLable1.setPosition(20, 60);
		LevelLable2.setPosition(20, 30);
		player1HP.setPosition(11, 14);
		player2HP.setPosition(1415, 14);
	}
	

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(304, -48,0);
	}

	@Override
	public void render() {
		world.step(1/10f, 6, 2);

		if (inputDelay == 0) {

			player.update(floorLayer, entityLayer);
			player2.update(floorLayer, entityLayer);
			inputDelay = MAX_INPUT_DELAY;
		}
	
		if(enemies != null)
		{
			boolean awayFromAll = true;
			for (Enemy enemy : enemies) {
				double x1 = enemy.getEnemySprite().getX();
				double x2 = player.getPlayerSprite().getX();
				double y1 = enemy.getEnemySprite().getY();
				double y2 = player.getPlayerSprite().getY();

				double distance = sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
				if (distance < 10)
					awayFromAll = false;
			}
			if (awayFromAll)
				player.setStamina(player.getStamina() + 0.05);
		}

		if(enemies != null)
		{
			boolean awayFromAll = true;
			for (Enemy enemy : enemies) {
				double x1 = enemy.getEnemySprite().getX();
				double x2 = player2.getPlayerSprite().getX();
				double y1 = enemy.getEnemySprite().getY();
				double y2 = player2.getPlayerSprite().getY();

				double distance = sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
				if (distance < 10)
					awayFromAll = false;
			}
			if (awayFromAll)
				player2.setStamina(player2.getStamina() + 0.05);
		}
		for(int count =0;count<enemies.size();count++)
		{
			if(enemies.get(count).isDead() == true)
			{	
				if(enemies.get(count).getTarget() ==1)
				{
					increaseXP(player, enemies.get(count).getXpValue());
				}
				else
				{
					increaseXP(player2, enemies.get(count).getXpValue());
				}
				enemies.get(count).getCurrentNode().setOccupied(false);
				enemies.get(count).getNextNode().setOccupied(false);
				world.destroyBody(enemies.get(count).getBody());
				if (enemies.size() == 1)
				{	
					enemies.clear();
				}
				else
				{
					enemies.remove(count);
					for(int update=0;update<enemies.size();update++)
					{
						enemies.get(update).updateUserData(update);
					}
				}
			}
		}
		for(int i=0;i<entities.size();i++)
		{
			if(entities.get(i).isHit() == true)
			{	
				world.destroyBody(entities.get(i).getBody());
					entities.clear();
					player.resetAmmo();
					player2.resetAmmo();
			}			
		}
		if(Gdx.input.isKeyJustPressed(Keys.K))
		{
			if(pressed==true)
			{
				debug = new Box2DDebugRenderer(false, false, false, false, false, false);
				debugger = false;
				pressed = false;
			}
			else
			{
				debug = new Box2DDebugRenderer(true, true, true, true, true, true);
				debugger = true;
				pressed = true;
			}
		}
		if(debugger == true)
		{
			if(Gdx.input.isKeyJustPressed(Keys.L))
			{
				for(int i=0;i<enemies.size();i++)
				{
					enemies.get(i).getCurrentNode().setOccupied(false);
					enemies.get(i).getNextNode().setOccupied(false);
					world.destroyBody(enemies.get(i).getBody());
				}
				createEnemies();
			}
			if(Gdx.input.isKeyPressed(Keys.J))
			{
				increaseXP(player, 100);
				increaseXP(player2, 100);
			}
			if(Gdx.input.isKeyJustPressed(Keys.U))
			{
				for(int i=0;i<enemies.size();i++)
				{
					enemies.get(i).getCurrentNode().setOccupied(false);
					enemies.get(i).getNextNode().setOccupied(false);
					world.destroyBody(enemies.get(i).getBody());
				}
				enemies.clear();
			}
			if(Gdx.input.isKeyPressed(Keys.Y))
			{
				roomCount+=5;
			}
		}
		try{
			floorLayer.getCell((int) (player.getCoordinates().x), (int) (player.getCoordinates().y)).setTile(tile);
		}
		catch (NullPointerException e){
			Gdx.app.log("Block:", "null");
		}

		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		//Gdx.gl.glClearColor(0.08f, 0.72f, 2.48f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//camera.update();
		renderer.setView(camera);
		//renderer.render();
		renderer.getBatch().begin();
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(0));
		renderer.getBatch().end();
		if(debugger==true)
		{
			shaper.begin(ShapeRenderer.ShapeType.Line);
			for(int ads=0;ads<PathFindingSystem.graph.getNodeCount();ads++)
			{
				if (PathFindingSystem.graph.getNodes().get(ads).isOccupied) {
					shaper.setColor(255f, 0f, 0f, 0f);
					shaper.polygon(PathFindingSystem.graph.getNodes().get(ads).p.getVertices());
					continue;
				}
	
				shaper.setColor(255f, 255f, 255f, 255f);
				shaper.polygon(PathFindingSystem.graph.getNodes().get(ads).p.getVertices());
			}
			shaper.end();
		}
		renderer.getBatch().begin();
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(4));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(5));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(6));
		if(entities!= null)
		{
			for(int counter =0; counter<entities.size();counter++)
			{
				if(entities.get(counter).isHit() !=true)
				{
					entities.get(counter).update();
					entities.get(counter).getSprite().draw(renderer.getBatch());
				}
			}
		}
		for (int c = 0; c < enemies.size(); c++) {
			if (enemies.get(c).getPath().getCount() > 0) {
				if (enemies.get(c).getTarget() == 1) {
					if (enemies.get(c).getPath().get(enemies.get(c).getPath().getCount() - 1).tilePos.equals(player.getVect2Coordinates())) {
					} else {
						enemies.get(c).updatePath(player, enemies.get(c).getCoordinates());
					}
				} else if (enemies.get(c).getTarget() == 2) {
					if (enemies.get(c).getPath().get(enemies.get(c).getPath().getCount() - 1).tilePos.equals(player2.getVect2Coordinates())) {
					} else {
						enemies.get(c).updatePath(player2, enemies.get(c).getCoordinates());
					}
				}
			}
		}
		if (enemies != null) //if there is enemies to render, render them if not skip
		{
			for (int i = 0; i < enemies.size(); i++) {
				if (enemies.get(i) != null) {
					enemies.get(i).updateBody();
					enemies.get(i).getEnemySprite().setPosition(enemies.get(i).getBody().getPosition().x - 7, enemies.get(i).getBody().getPosition().y - 7);
					enemies.get(i).getEnemySprite().draw(renderer.getBatch());
					drawHP(i);
				}
			}
		}
		renderer.getBatch().end();
		if(chestText != "")
		{
			renderer.getBatch().begin();
			font.setColor(Color.CYAN);
			font.draw(renderer.getBatch(), chestText, 250.5f, -150, screenWidth, Align.topLeft, false);	
			font.setColor(Color.WHITE);
			Timer timer=new Timer();
			timer.scheduleTask(new Timer.Task() 
			{
				@Override
				public void run() 
				{
					chestText= "";
				}
			},1f); 
			renderer.getBatch().end();
		}
		if(xp1Increased || xp2Increased)
		{
			renderer.getBatch().begin();
			if(xp1Increased && xp2Increased)
			{
				xpText ="both players" + " gained "+gained+"xp";
				font.setColor(Color.CHARTREUSE);
				font.draw(renderer.getBatch(), xpText, 107.5f, 30.5f,screenWidth, Align.topLeft, false);	
			}
			else
			{
				if(xp1Increased)
				{
					xpText ="player 1" + " gained "+gained+"xp";
					font.setColor(Color.YELLOW);
					font.draw(renderer.getBatch(), xpText, 107.5f, 30.5f,screenWidth, Align.topLeft, false);	
				}
				else if(xp2Increased)
				{
					xpText ="player 2" + " gained "+gained+"xp";
					font.setColor(Color.WHITE);
					font.draw(renderer.getBatch(), xpText, 107.5f, 30.5f,screenWidth, Align.topLeft, false);	
				}
			}
			font.setColor(Color.WHITE);
			Timer timer=new Timer();
			timer.scheduleTask(new Timer.Task() 
			{
				@Override
				public void run() 
				{
					xpText= "";
					gained=0;
					xp1Increased=false;
					xp2Increased=false;
				}
			},0.5f);
			renderer.getBatch().end(); 
		}
		
		renderer.getBatch().begin();
		

		if(chest!=null)
		{
			if(chest.isOpened() == true)
			{
				chest.getItemSprite().draw(renderer.getBatch());
				world.destroyBody(chest.getBody());
				chest= null;
			}	
			else
			{
				chest.getChestSprite().setPosition(chest.getBody().getPosition().x -7 , chest.getBody().getPosition().y - 7);
				chest.getChestSprite().draw(renderer.getBatch());
			}
		}

		if(player.isDead()==false){player.getPlayerSprite().draw(renderer.getBatch());}else{}
		if(player2.isDead()==false){player2.getPlayerSprite().draw(renderer.getBatch());}else{}

		if(player.getAttackSprite()!=null && player.getAttackBody() != null)
		{	
			player.getAttackSprite().setPosition(player.getAttackBody().getPosition().x - player.getAttackSprite().getWidth()/2, 
			player.getAttackBody().getPosition().y - player.getAttackSprite().getHeight()/2);
			player.getAttackSprite().draw(renderer.getBatch());
		}	
		if(player2.getAttackSprite() != null)
		{
			player2.getAttackSprite().draw(renderer.getBatch());
		}
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), myText, 107.5f, 63.5f, screenWidth, Align.topLeft, false );
		font.draw(renderer.getBatch(), myRightText, 437.5f, 63.5f, screenWidth, Align.topLeft, false );
		font.draw(renderer.getBatch(), myRightText, 437.5f, 63.5f, screenWidth, Align.topLeft, false );
		font.draw(renderer.getBatch(), "Xp Multiplier: "+ Integer.toString(Enemy.xpMulti), 437.5f, 53.5f, screenWidth, Align.topLeft, false );
		
		// player1 stat display
		font.setColor(Color.YELLOW);
		font.draw(renderer.getBatch(), "Player 1: ", 107.5f, -70.5f, screenWidth, Align.topLeft, false);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), slot1Text + player.getSlotDurability(1) + "%", 107.5f, -80.5f, screenWidth, Align.topLeft, true);
		font.setColor(player.getItemColor(1));
		font.draw(renderer.getBatch(), " "+player.getSlotName(1), 107.5f, -90.5f, screenWidth, Align.topLeft, true);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), slot2Text + player.getSlotDurability(2) + "%", 107.5f, -100.5f, screenWidth, Align.topLeft, true);
		font.setColor(player.getItemColor(2));
		font.draw(renderer.getBatch(), " "+player.getSlotName(2), 107.5f, -110.5f, screenWidth, Align.topLeft, true);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), bagText ,107.5f,-120.5f,screenWidth,Align.topLeft,false);
		font.setColor(Color.RED);
		font.draw(renderer.getBatch(), player.getSlotName(0), 107.5f, -130.5f, screenWidth, Align.topLeft, true);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), staminaText + (int)player.getStamina(), 107.5f, -140.5f, screenWidth, Align.topLeft, false);
		font.draw(renderer.getBatch(), coinText + player.getCoin(), 107.5f, -150.5f, screenWidth, Align.topLeft, false);

		// player2 stat display

		font.draw(renderer.getBatch(), "Player 2: ", 457.5f, -70.5f, screenWidth, Align.topLeft, false);
		font.draw(renderer.getBatch(), slot1Text + player2.getSlotDurability(1) + "%", 445.5f, -80.5f, screenWidth, Align.topLeft, false);
		font.setColor(player2.getItemColor(1));
		font.draw(renderer.getBatch(), player2.getSlotName(1), (460.5f - (player2.getSlotName(1).length()*2)), -90.5f, screenWidth, Align.topLeft, true);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), slot2Text + player2.getSlotDurability(2) + "%", 445.5f, -100.5f, screenWidth, Align.topLeft, false);
		font.setColor(player2.getItemColor(2));
		font.draw(renderer.getBatch(), player2.getSlotName(2), (460.5f - (player2.getSlotName(2).length() *2)), -110.5f, screenWidth, Align.topLeft, true);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), bagText ,445.5f,-120.5f,screenWidth,Align.topLeft,false);
		font.setColor(Color.RED);
		font.draw(renderer.getBatch(), player2.getSlotName(0), (460.5f - (player2.getSlotName(0).length() *2)), -130.5f, screenWidth, Align.topLeft, true);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), staminaText + (int)player2.getStamina(), 455.5f, -140.5f, screenWidth, Align.topLeft, false);
		font.draw(renderer.getBatch(), coinText + player2.getCoin(), 455.5f, -150.5f, screenWidth, Align.topLeft, false);
	
		
		if(debugger!=false)
		{
			font.draw(renderer.getBatch(),"Player1 enemies:"+p1enemies  , 107.5f, 55.5f, screenWidth, Align.topLeft, false );
			font.draw(renderer.getBatch(),"Player2 enemies:"+p2enemies  , 107.5f, 45.5f, screenWidth, Align.topLeft, false );

			xp = player.getLevel();
			xp2 = player2.getLevel();
			font.draw(renderer.getBatch(),"Player1 Level:"+xp, 107.5f, 35.5f, screenWidth, Align.topLeft, false );
			font.draw(renderer.getBatch(),"Player2 Level:"+xp2, 107.5f, 25.5f, screenWidth, Align.topLeft, false );
			font.draw(renderer.getBatch(),"Attacking: "+attacking[0] +" "+attacking[1]+"hp", 107.5f, 15.5f, screenWidth, Align.topLeft, false );
		}
	
		renderer.renderTileLayer(overlapLayer);
		renderer.getBatch().end();
		inputDelay--;
		logDelay--;
		super.render();
		//Gdx.app.log("Current block id", String.valueOf((int) (player.getCoordinates().y)));

		//renderer.renderObject(floorLayer.getCell(17,17).getTile().);
//		camera.position.add(-1f,-1f,0);
//		camera.translate(1,0);
//		camera.update();

		if (entityLayer.getCell((int) (player.getCoordinates().x), (int) (player.getCoordinates().y)) != null) {
//			tmpTile = (StaticTiledMapTile) entityLayer.getCell((int) (player.getCoordinates().x), (int) (player.getCoordinates().y)).getTile();
//			overlapLayer.getCell((int) (player.getCoordinates().x), (int) (player.getCoordinates().y)).setTile(tmpTile);
			renderer.getBatch().begin();

			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(0));
			if(player.isDead()==false){player.getPlayerSprite().draw(renderer.getBatch());}else{}
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(4));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(5));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(6));

			for(int i3=0;i3<enemies.size();i3++)
			{
				
				enemies.get(i3).getEnemySprite().draw(renderer.getBatch());
				drawHP(i3);
			}
			if(player2.isDead()==false){player2.getPlayerSprite().draw(renderer.getBatch());}else{}
			if(chest!=null)
			{
				chest.getChestSprite().draw(renderer.getBatch());
			}
			renderer.getBatch().end();
		}
		if (entityLayer.getCell((int) (player.getCoordinates().x - 1), (int) (player.getCoordinates().y)) != null) {
//			tmpTile = (StaticTiledMapTile) entityLayer.getCell((int) (player.getCoordinates().x - 1), (int) (player.getCoordinates().y)).getTile();
//			overlapLayer.getCell((int) (player.getCoordinates().x - 1), (int) (player.getCoordinates().y)).setTile(tmpTile);
			renderer.getBatch().begin();

			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(0));
			if(player.isDead()==false){player.getPlayerSprite().draw(renderer.getBatch());}else{}
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(4));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(5));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(6));
			for(int i4=0;i4<enemies.size();i4++)
			{
				if(enemies.get(i4)!=null)
				{
					enemies.get(i4).getEnemySprite().draw(renderer.getBatch());
					drawHP(i4);
				}
			}
			if(player2.isDead()==false){player2.getPlayerSprite().draw(renderer.getBatch());}else{}
			if(chest!=null)
			{
				chest.getChestSprite().draw(renderer.getBatch());
			}
		
			renderer.getBatch().end();

		}
		if (entityLayer.getCell((int) (player.getCoordinates().x), (int) (player.getCoordinates().y + 1)) != null) {
//			tmpTile = (StaticTiledMapTile) entityLayer.getCell((int) (player.getCoordinates().x), (int) (player.getCoordinates().y + 1)).getTile();
//			overlapLayer.getCell((int) (player.getCoordinates().x), (int) (player.getCoordinates().y + 1)).setTile(tmpTile);
			renderer.getBatch().begin();

			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(0));
			if(player.isDead()==false){player.getPlayerSprite().draw(renderer.getBatch());}else{}
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(4));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(5));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(6));

			for(int i=0;i<enemies.size();i++)
			{
				if(enemies.get(i)!=null)
				{
					enemies.get(i).getEnemySprite().draw(renderer.getBatch());
					drawHP(i);
				}
			}
			if(player2.isDead()==false){player2.getPlayerSprite().draw(renderer.getBatch());}else{}
			if(chest!=null)
			{
				chest.getChestSprite().draw(renderer.getBatch());
			}
			renderer.getBatch().end();

		}

		renderer.getBatch().begin();
		for(int i2=0;i2<enemies.size();i2++)
		{
			enemies.get(i2).getEnemySprite().draw(renderer.getBatch());

		}
		renderer.getBatch().end();//remove this later

		if (((int) (player.getCoordinates().x)) == 24 && ((int) (player.getCoordinates().y)) == 0) nextRoom = "down";
		if (((int) (player.getCoordinates().x)) == 24 && ((int) (player.getCoordinates().y)) == 15) nextRoom = "up";
		if (((int) (player.getCoordinates().x)) == 16 && ((int) (player.getCoordinates().y)) == 7) nextRoom = "left";
		if (((int) (player.getCoordinates().x)) == 31 && ((int) (player.getCoordinates().y)) == 7) nextRoom = "right";

		if (nextRoom != "") {
			if((player.getLevel() >=15 && enemies.size()!=0) ||
				(player2.getLevel()>= 15 && enemies.size()!=0))
			{
				renderer.getBatch().begin();
				nextRoomText="you're level 15, kill all enemies to move on";
				font.setColor(Color.RED);
				font.draw(renderer.getBatch(), nextRoomText, 220.5f, -150,screenWidth, Align.topLeft, false);	
				Timer timer2=new Timer();
				timer2.scheduleTask(new Timer.Task() 
				{
					@Override
					public void run() 
					{
						nextRoomText= "";
					}
				},3f); 
				renderer.getBatch().end();
			}
			else{
				camera.position.set(304, -48,0);
				map.dispose();
				renderer.dispose();
				PathFindingSystem.graph.getNodes().clear();
				myText = roomList[(int) (Math.random() * 6)];
				map = new TmxMapLoader().load("tiledmaps/" + myText + ".tmx");
				//map = new TmxMapLoader().load("tiledmaps/testroom.tmx");
				pathFinder.generateGraph(map);
				roomCount++;
				myRightText = "no of rooms: " + roomCount;
				renderer = new IsometricTiledMapRenderer(map, 1.2f);
				entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
				floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
				overlapLayer =  (TiledMapTileLayer) map.getLayers().get(2);
	
				if (nextRoom == "down") {
					player.getPlayerSprite().setPosition(366f,-35.5f);
					player.getCoordinates().set(24,14,0);
					player2.getPlayerSprite().setPosition(357f,-30.5f);
					player2.getCoordinates().set(23,14,0);
				}
				if (nextRoom == "up") {
					player.getPlayerSprite().setPosition(242.5f,-97f);
					player.getCoordinates().set(24,1,0);
					player2.getPlayerSprite().setPosition(252f,-102f);
					player2.getCoordinates().set(25,1,0);
				}
				if (nextRoom == "left") {
					player.getPlayerSprite().setPosition(357.5f,-97.25f);
					player.getCoordinates().set(30,7,0);
					player2.getPlayerSprite().setPosition(367f,-92.25f);
					player2.getCoordinates().set(30,8,0);
				}
				if (nextRoom == "right") {
					player.getPlayerSprite().setPosition(231.5f,-35.5f);
					player.getCoordinates().set(17,7,0);
					player2.getPlayerSprite().setPosition(222.5f,-40f);
					player2.getCoordinates().set(17,6,0);
				}
	
				for(int i=0;i<enemies.size();i++)
				{
					//enemies.get(i).getCurrentNode().setOccupied(false);
					//enemies.get(i).getNextNode().setOccupied(false);
					world.destroyBody(enemies.get(i).getBody());
				}
	
				createChest();
				createEnemies();
				increaseXP(player, 10);
				increaseXP(player2,10);
				nextRoom = "";
				return;
			}

		
		}		
		if(debug !=null)
		{
			debug.render(world,camera.combined);
		}	
		player.checkForDeath();
		player2.checkForDeath();

		level1 = player.getLevel();
		LevelLable1.setText("Player 1 Level: " + level1);
		level2 = player2.getLevel();
		LevelLable2.setText("Player 2 Level: " + level2);
		HP1 = player2.getHealth();
		player1HP.setText("Health: " + HP1);
		HP2 = player.getHealth();
		player2HP.setText("Health: " + HP2);
		hudStage.getRoot().setPosition(650, 815);
		Healt.getRoot().setPosition(1, 750);
		Healt.act(Gdx.graphics.getDeltaTime());
		hudStage.act(Gdx.graphics.getDeltaTime());
		hudStage.draw();
		Healt.draw();
	}
		
	@Override
	public void dispose() {
		renderer.getBatch().dispose();
		map.dispose();
		player.dispose();
		player2.dispose();
		song1.dispose();
	}
	public void createChest() {
		if(chest !=null)
		{
			if(chest.getBody()!=null)
			{
				world.destroyBody(chest.getBody());
			}
			chest = null;
		}
		int x = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17); //random numbers for x and y offsets
		int y = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17);
		int gridX = x;
		int gridY = y;
		float realX = 298 + (gridX - gridY) * (9.5f);
		float realY = 166 - (gridX + gridY) * (4.75f);
		chest = new Chest(world, realX, realY);
	}
	public void drawHP(int index)
	{
		font.setColor(enemies.get(index).getHpColor());
		font.getData().setScale(0.2f);
		font.draw(renderer.getBatch(), "HP: "+Integer.toString(enemies.get(index).getHealth()),enemies.get(index).getBody().getPosition().x -7 , (enemies.get(index).getBody().getPosition().y - 7) +
		enemies.get(index).getEnemySprite().getHeight()+4);
	
		font.setColor(Color.WHITE);
		font.getData().setScale(0.4f);
	}
	public void increaseXP(Player p,int amount)
	{
		int x= p.getPlayerNum();
		if(x==1)
		{
			xp1Increased =true;
		}
		else
		xp2Increased=true;
		gained =amount;
		p.increaseXP(amount);
	}
	public void createEnemies()
	{
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).resetPath();
		}
		enemies.clear();
		p1enemies =0;
		p2enemies =0;
		int type;
		int type2;
		int bossType=-1;
		int amount;
		if(player.getLevel() > 10 || player2.getLevel()>10)
		{
			type=(int)Math.floor(Math.random() *(4 - 1 + 1) + 1);
			type2 = (int)Math.floor(Math.random() *(2 - 1 + 1) + 1);
			if(roomCount>10)
			{
				bossCount++;
				System.out.println("bossCount:"+bossCount);
				if(bossCount == 5)
				{
					type =-1;
					type2=3;
					bossCount=0;
					bossType =(int)Math.floor(Math.random() *(2 - 1 + 1) + 1);
					System.out.println("bossCount:"+type2);
				}
			}
		}
		else
		{
			type =(int)Math.floor(Math.random() *(3 - 1 + 1) + 1);
			type2=0;
		}
		if(type2 ==3)
		{
			amount =1;
		}
		else
		{
			if(roomCount<=20)
			{
				amount = (int)Math.floor(Math.random() *(6 - 3 + 1) + 3);
			}
			else
			{
				amount = (int)Math.floor(Math.random() *(8 - 4 + 1) + 4);
			}
			
		}
		for(int i=0;i<amount;i++)
		{
			int randomNode = (int) (Math.random() * PathFindingSystem.graph.getNodeCount());
			Node spawnPos = PathFindingSystem.graph.getNodes().get(randomNode);
			if (spawnPos.tilePos.equals(player.getVect2Coordinates()) || spawnPos.tilePos.equals(player2.getVect2Coordinates())) 
			PathFindingSystem.graph.getNodes().get((int) (Math.random() * (PathFindingSystem.graph.getNodeCount() - randomNode)));
			int gridX = (int) spawnPos.tilePos.x;
			int gridY = (int) (32 - spawnPos.tilePos.y);
			float realX = 307 + (gridX - gridY) * (9.5f);
			float realY = 180 - (gridX + gridY) * (4.75f);
			enemies.add(new Enemy(world, realX, realY, type,type2,i,bossType,roomCount));
			enemies.get(i).setCoords(spawnPos.tilePos);
			int randomPlayer=(int)Math.floor(Math.random() *(2 - 1 + 1) + 1);
			if (randomPlayer == 1) {
				enemies.get(i).setTarget(player);
				p1enemies++;
				enemies.get(i).setPath(player, spawnPos);
			}
			else if (randomPlayer == 2) {
				enemies.get(i).setTarget(player2);
				p2enemies++;
				enemies.get(i).setPath(player2, spawnPos);
			} 
		}
	}
	public void collsion()
	{
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				try{
					if(contact.getFixtureB().getBody().getUserData().toString().contains("enemy")&&
					contact.getFixtureA().getBody().getUserData()=="player1")
					{
						String[] x =contact.getFixtureB().getBody().getUserData().toString().split(",");
						int y = Integer.parseInt(x[1]);
						enemies.get(y).attack(player2);
						colliding=true;
					}
					if(contact.getFixtureB().getBody().getUserData().toString().contains("enemy")&&
						contact.getFixtureA().getBody().getUserData()=="player2")
					{
						String[] x =contact.getFixtureB().getBody().getUserData().toString().split(",");
						int y = Integer.parseInt(x[1]);
						enemies.get(y).attack(player);
						colliding=true;
					}
					if(contact.getFixtureB().getBody().getUserData()=="chest"&&
						contact.getFixtureA().getBody().getUserData()=="player1")
					{
						chest.open(player, itemAttrs);
						increaseXP(player, 20);
						chestText="Chest opened by player 1";
						colliding=true;
					}
					if(contact.getFixtureB().getBody().getUserData()=="chest"&&
						contact.getFixtureA().getBody().getUserData()=="player2")
					{
						chest.open(player2, itemAttrs);
						increaseXP(player2, 20);
						chestText="Chest opened by player 2";
						colliding=true;
					}
					if(contact.getFixtureB().getBody().getUserData().toString().contains("enemy")&&
					(contact.getFixtureA().getBody().getUserData().toString().contains("attack")))
					{		
						
						String[] x =contact.getFixtureB().getBody().getUserData().toString().split(",");
						String[] p = contact.getFixtureA().getBody().getUserData().toString().split(",");
						int y = Integer.parseInt(x[1]);
						String weapon = p[1];
						int play = Integer.parseInt(p[2]);
						int damage = Integer.parseInt(p[3]);
						enemies.get(y).takeDamage(damage);
						attacking[0] = x[0]+y;
						attacking[1] = Integer.toString(enemies.get(y).getHealth());
						hitting= true;
					}
					else if(contact.getFixtureA().getBody().getUserData().toString().contains("enemy")&&
					(contact.getFixtureB().getBody().getUserData().toString().contains("attack")))
					{
					
						String[] x =contact.getFixtureA().getBody().getUserData().toString().split(",");
						String[] p = contact.getFixtureB().getBody().getUserData().toString().split(",");
						int y = Integer.parseInt(x[1]);
						String weapon = p[1];
						int play = Integer.parseInt(p[2]);
						int damage = Integer.parseInt(p[3]);
						enemies.get(y).takeDamage(damage);
						attacking[0] = x[0]+y;
						attacking[1] = Integer.toString(enemies.get(y).getHealth());
						hitting= true;
					}
					if(contact.getFixtureA().getBody().getUserData().toString().contains("proj") &&
					contact.getFixtureB().getBody().getUserData().toString().contains("enemy"))
					{		
						String[] x =contact.getFixtureB().getBody().getUserData().toString().split(",");
						int y = Integer.parseInt(x[1]);
						String[] p = contact.getFixtureA().getBody().getUserData().toString().split(",");
						entities.get(0).setHit(true);		
						String weapon = p[2];
						int play = Integer.parseInt(p[3]);
						int damage = Integer.parseInt(p[4]);
						enemies.get(y).takeDamage(damage);
						attacking[0] = x[0]+y;
						attacking[1] = Integer.toString(enemies.get(y).getHealth());		
						hitting= true;
					}
					else if(contact.getFixtureB().getBody().getUserData().toString().contains("proj") &&
					contact.getFixtureA().getBody().getUserData().toString().contains("enemy"))
					{		
						String[] x =contact.getFixtureA().getBody().getUserData().toString().split(",");
						int y = Integer.parseInt(x[1]);
						String[] p = contact.getFixtureB().getBody().getUserData().toString().split(",");
						entities.get(0).setHit(true);		
						String weapon = p[2];
						int play = Integer.parseInt(p[3]);
						int damage = Integer.parseInt(p[4]);
						enemies.get(y).takeDamage(damage);
						attacking[0] = x[0]+y;
						attacking[1] = Integer.toString(enemies.get(y).getHealth());		
						hitting= true;		

					}
					if(contact.getFixtureB().getBody().getUserData().toString().contains("proj") &&
					contact.getFixtureA().getBody().getUserData()=="dest")
					{
						String[] x =contact.getFixtureB().getBody().getUserData().toString().split(",");
						int y = Integer.parseInt(x[1]);
						entities.get(y).setHit(true);
					}
					else if(contact.getFixtureA().getBody().getUserData().toString().contains("proj") &&
					contact.getFixtureB().getBody().getUserData()=="dest")
					{
						String[] x =contact.getFixtureA().getBody().getUserData().toString().split(",");
						int y = Integer.parseInt(x[1]);
						entities.get(y).setHit(true);
					}
				}	
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			@Override
			public void endContact(Contact contact) {
				colliding=false;
				hitting=false;
				hit =false;
			}
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}
		});
	}
}
