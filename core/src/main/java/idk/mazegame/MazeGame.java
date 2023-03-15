package idk.mazegame;

import java.util.ArrayList;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
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
import idk.mazegame.EnemyAI.CreateMapBounds;
import idk.mazegame.EnemyAI.Steering;
import static java.lang.Math.sqrt;

import java.lang.ProcessBuilder.Redirect;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

/**
 * bugs to fix:
 * fix grid-based movement animations
 * fix diagonal inputs
 * fix bug where if you release two input keys at just the right time - the movement gets locked and runs itself infinitely (might be fixed, not sure)
 *
 * improvements to make:
 * create a new renderer class that inherits IsometricTiledMapRenderer that has a Z-sorting function
 *
 * features to add:
 * make a UI
 * make a menu
 * create a new room when you walk into a doorway
 *
 * refactoring:
 * separate stuff into classes
 * */
public class MazeGame extends Game {
	public SpriteBatch batch;
	private BitmapFont font;
	private String myText, myRightText;
	private String healthText, staminaText, coinText, slot1Text, slot2Text,chestText,xpText,nextRoomText;
	private GlyphLayout layout;
	private Sound sound;
	private Music song1,song2;
	private OrthographicCamera camera;
	private Viewport viewport;

	private int max=8,min=4;
	private  Chest chest;
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private ArrayList<Steering> enemiesAI = new ArrayList<>();
	public static ArrayList<Projectile> entities = new ArrayList<>();

	private Player player, player2;
	private Steering target;
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
	public boolean debugger =false;
	private Boolean pressed =false;
	private ItemAttributes itemAttrs;
	private int xp;
	private int xp2;
	private int gained;
	private boolean colliding,hitting,hit,xp1Increased,xp2Increased=false;
	private int amount;
	private String[] attacking = new String[2];
	private int level1;
	private int level2;
	private Label LevelLable1;
	private Label LevelLable2;
	private Stage hudStage;
	private int bossCount;
	@Override
	public void create() {
		//setScreen(new PlayScreen());
		collsion();	
		map =  new TmxMapLoader().load("tiledmaps/safeRoom.tmx");
		renderer = new IsometricTiledMapRenderer(map, 1.2f);
		entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
		floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
		overlapLayer = (TiledMapTileLayer) map.getLayers().get(2);
		tile = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("tiledmaps/tileSprites.png")),32,32,16,16));
		

		floorLayer.getCell(23, 7).setTile(tile);
		floorLayer.getCell(24, 8).setTile(tile);

		batch = new SpriteBatch();

		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		float aspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();

//		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myfont.ttf"));
//		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
//		parameter.size = 12;
//		BitmapFont font12 = generator.generateFont(parameter); // font size 12 pixels
//		parameter.size = 32;
//		BitmapFont font32 = generator.generateFont(parameter); // font size 32
//		generator.dispose(); // don't forget to dispose to avoid memory leaks!

		font = new BitmapFont(Gdx.files.internal("UI/myfont.fnt"));
		font.getData().setScale(0.4f);
		myText = "testroom";
		myRightText = "no of rooms: " + roomCount;
		layout = new GlyphLayout();
		layout.setText(font, myText);
		chestText ="";
		healthText = "health: ";
		staminaText = "stamina: ";
		coinText = "coins: ";
		slot1Text = "slot 1:";
		slot2Text = "slot 2:";

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
		player.setUseSlot1(Input.Keys.CONTROL_RIGHT);
		player.setUseSlot2(Input.Keys.SHIFT_RIGHT);
		player2.setUp(Input.Keys.W);
		player2.setLeft(Input.Keys.A);
		player2.setDown(Input.Keys.S);
		player2.setRight(Input.Keys.D);
		player.setCoordinates(new Vector3(24,8,0));
		player2.setCoordinates(new Vector3(23,7,0));
		player.setDefaultValues();
		player2.setDefaultValues();
		p1 = player.createBody(world);
		p2 = player2.createBody(world);
		p1.setUserData("player1");
		p2.setUserData("player2");
		createEnemies();
		CreateMapBounds x = new CreateMapBounds(map,world);

		song1.setLooping(true);
		//song1.play();
		song1.setVolume(0.5f);

		//debug = new Box2DDebugRenderer(true, true, true, true, true, true);
		
//		long id = sound.play();
//		long ourSoundID = sound.loop(1.0f,1.0f,0.0f);
//
//		Timer.schedule(new Timer.Task() {
//			@Override
//			public void run() {
//				sound.pause(ourSoundID);
//			}
//		},10);
//
//		song2.play();
//		song2.setVolume(0.5f);
//
//		song2.setOnCompletionListener(new Music.OnCompletionListener() {
//			@Override
//			public void onCompletion(Music music) {
//				song1.play();
//				song1.setVolume(0.5f);
//			}
//		});
//
//		Timer.schedule(new Timer.Task() {
//			@Override
//			public void run() {
//				if(song2.isPlaying())
//					if(song2.getPosition() >= 10.0f)
//						song2.setVolume(song2.getVolume() - 0.125f);
//			}
//		},29,1,4);
//
//		sound.setVolume(id, 1.0f);
//		sound.setPitch(id, 2.0f);
//		sound.setPan(id, -1f, 1f);

		//Gdx.input.setInputProcessor(this);
		//creating the HUD for the player level and other stats 
		hudStage = new Stage();

		LevelLable1 = new Label("PLayer 1 Level: 1", new Label.LabelStyle(font,Color.GOLD));
		LevelLable1.setFontScale(1.5f);
		LevelLable2 = new Label("PLayer 2 Level: 1", new Label.LabelStyle(font,Color.WHITE));
		LevelLable2.setFontScale(1.5f);

		hudStage.addActor(LevelLable1);
		hudStage.addActor(LevelLable2);

		LevelLable1.setPosition(20, 60);
		LevelLable2.setPosition(20, 30);
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
				world.destroyBody(enemies.get(count).getBody());
				if (enemies.size() == 1)
				{	
					enemies.clear();
					enemiesAI.clear();
				}
				else
				{
					enemies.remove(count);
					enemiesAI.remove(count);
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
				for(int i=0;i<amount;i++)
				{
					world.destroyBody(enemiesAI.get(i).getBody());
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
				for(int i=0;i<amount;i++)
				{
					world.destroyBody(enemiesAI.get(i).getBody());
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
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(4));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(5));
		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(6));
		//renderer.getBatch().setProjectionMatrix(camera.combined);
		if(entities!= null)
		{
			for(int counter =0; counter<entities.size();counter++)
			{
				if(entities.get(counter).isHit() !=true)
				{
					entities.get(counter).update();
				}
			}
		}
		if(enemiesAI != null) //if there is enemies to render, render them if not skip
		{
			for(int i=0;i<enemies.size();i++)
			{
				if(enemies.get(i)!=null && enemiesAI.get(i)!= null)
				{
					enemiesAI.get(i).update(Gdx.graphics.getDeltaTime(),enemies.get(i));
					//enemies[i].getEnemySprite().setPosition(enemies[i].getBody().getPosition().x * Constants.PPM, enemies[i].getBody().getPosition().y* Constants.PPM);

					enemies.get(i).getEnemySprite().setPosition(enemies.get(i).getBody().getPosition().x -7 , enemies.get(i).getBody().getPosition().y - 7);
					drawHP(i);
					enemies.get(i).getEnemySprite().draw(renderer.getBatch());
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
				world.destroyBody(chest.getBody());
				chest= null;
			}	
			else
			{
				chest.getChestSprite().setPosition(chest.getBody().getPosition().x -7 , chest.getBody().getPosition().y - 7);
				chest.getChestSprite().draw(renderer.getBatch());
			}
		}


		player2.getPlayerSprite().draw(renderer.getBatch());
		player.getPlayerSprite().draw(renderer.getBatch());

		//font.draw(renderer.getBatch(), myText, 10f, screenHeight - 10f, screenWidth, Align.topLeft, false );

//		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
//		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));
//		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(4));
//		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(5));
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), myText, 107.5f, 63.5f, screenWidth, Align.topLeft, false );
		font.draw(renderer.getBatch(), myRightText, 437.5f, 63.5f, screenWidth, Align.topLeft, false );
		font.draw(renderer.getBatch(), myRightText, 437.5f, 63.5f, screenWidth, Align.topLeft, false );
		font.draw(renderer.getBatch(), "Xp Multiplier: "+ Integer.toString(Enemy.xpMulti), 437.5f, 53.5f, screenWidth, Align.topLeft, false );
		
		// player stat display#
		font.setColor(Color.YELLOW);
		font.draw(renderer.getBatch(), "Player 1: ", 107.5f, -80.5f, screenWidth, Align.topLeft, false);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), healthText + player.getHealth(), 107.5f, -90.5f, screenWidth, Align.topLeft, false);
		font.draw(renderer.getBatch(), slot1Text, 107.5f, -100.5f, screenWidth, Align.topLeft, true);
		font.setColor(player.getItemColor(1));
		font.draw(renderer.getBatch(), " "+player.getSlotName(1), 107.5f, -110.5f, screenWidth, Align.topLeft, true);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), slot2Text, 107.5f, -120.5f, screenWidth, Align.topLeft, true);
		font.setColor(player.getItemColor(2));
		font.draw(renderer.getBatch(), " "+player.getSlotName(2), 107.5f, -130.5f, screenWidth, Align.topLeft, true);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), staminaText + (int)player.getStamina(), 107.5f, -140.5f, screenWidth, Align.topLeft, false);
		font.draw(renderer.getBatch(), coinText + player.getCoin(), 107.5f, -150.5f, screenWidth, Align.topLeft, false);


		// player stat display

		font.draw(renderer.getBatch(), "Player 2: ", 457.5f, -80.5f, screenWidth, Align.topLeft, false);
		font.draw(renderer.getBatch(), healthText + player2.getHealth(), 457.5f, -90.5f, screenWidth, Align.topLeft, false);
		font.draw(renderer.getBatch(), slot1Text, 460.5f, -100.5f, screenWidth, Align.topLeft, false);
		font.setColor(player2.getItemColor(1));
		font.draw(renderer.getBatch(), player2.getSlotName(1), (460.5f - (player2.getSlotName(1).length()*2)), -110.5f, screenWidth, Align.topLeft, true);
		font.setColor(Color.WHITE);
		font.draw(renderer.getBatch(), slot2Text, 460.5f, -120.5f, screenWidth, Align.topLeft, false);
		font.setColor(player2.getItemColor(2));
		font.draw(renderer.getBatch(), player2.getSlotName(2), (460.5f - (player2.getSlotName(2).length() *2)), -130.5f, screenWidth, Align.topLeft, true);
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
			player.getPlayerSprite().draw(renderer.getBatch());
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(4));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(5));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(6));

			for(int i=0;i<enemies.size();i++)
			{
				
				enemies.get(i).getEnemySprite().draw(renderer.getBatch());
				drawHP(i);
			}
			player2.getPlayerSprite().draw(renderer.getBatch());
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
			player.getPlayerSprite().draw(renderer.getBatch());
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
			player2.getPlayerSprite().draw(renderer.getBatch());
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
			player.getPlayerSprite().draw(renderer.getBatch());
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
			player2.getPlayerSprite().draw(renderer.getBatch());
			if(chest!=null)
			{
				chest.getChestSprite().draw(renderer.getBatch());
			}
			renderer.getBatch().end();

		}

		if (((int) (player.getCoordinates().x)) == 24 && ((int) (player.getCoordinates().y)) == 0) {

			if((player.getLevel() >=15 && enemies.size()!=0) ||
				(player2.getLevel()>= 15 && enemies.size()!=0))
			{
				renderer.getBatch().begin();
				nextRoomText="you're level 15, kill all enemies to move on";
				font.setColor(Color.RED);
				font.draw(renderer.getBatch(), nextRoomText, 220.5f, -150,screenWidth, Align.topLeft, false);	
				Timer timer=new Timer();
				timer.scheduleTask(new Timer.Task() 
				{
					@Override
					public void run() 
					{
						nextRoomText= "";
					}
				},3f); 
				renderer.getBatch().end();
			}
			else
			{
				camera.position.set(304, -48,0);
				map.dispose();
				renderer.dispose();
				map = new TmxMapLoader().load("tiledmaps/safeRoom.tmx");
				myText = "saferoom";
				roomCount++;
				myRightText = "no of rooms: " + roomCount;
				renderer = new IsometricTiledMapRenderer(map, 1.2f);
				entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
				floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
				overlapLayer =  (TiledMapTileLayer) map.getLayers().get(2);
				player.getPlayerSprite().setPosition(366f,-35.5f);
				player.getCoordinates().set(24,14,0);
	
				for(int i=0;i<enemies.size();i++)
				{
					world.destroyBody(enemiesAI.get(i).getBody());
				}
				createChest();
				createEnemies();
				increaseXP(player, 10);
				increaseXP(player2,10);
			}
			 
		}

		if (((int) (player.getCoordinates().x)) == 24 && ((int) (player.getCoordinates().y)) == 15) {
			if((player.getLevel() >=15 && enemies.size()!=0) ||
			(player2.getLevel()>= 15 && enemies.size()!=0))
			{
				renderer.getBatch().begin();
				nextRoomText="you're level 15, kill all enemies to move on";
				font.setColor(Color.RED);
				font.draw(renderer.getBatch(), nextRoomText, 220.5f, -150,screenWidth, Align.topLeft, false);	
				Timer timer=new Timer();
				timer.scheduleTask(new Timer.Task() 
				{
					@Override
					public void run() 
					{
						nextRoomText= "";
					}
				},3f); 
				renderer.getBatch().end();
			}
			else
			{
				camera.position.set(304, -48,0);
				map.dispose();
				renderer.dispose();
				map = new TmxMapLoader().load("tiledmaps/testRoom.tmx");
				myText = "testroom";
				roomCount++;
				myRightText = "no of rooms: " + roomCount;
				renderer = new IsometricTiledMapRenderer(map, 1.2f);
				entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
				floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
				overlapLayer = (TiledMapTileLayer) map.getLayers().get(2);
				player.getPlayerSprite().setPosition(242.5f,-97f);
				player.getCoordinates().set(24,1,0);
	
				for(int i=0;i<enemies.size();i++)
				{
					world.destroyBody(enemiesAI.get(i).getBody());
				}
				createChest();
				createEnemies();
				increaseXP(player, 10);
				increaseXP(player2, 10);
			}
		
		}

		if (((int) (player.getCoordinates().x)) == 16 && ((int) (player.getCoordinates().y)) == 7) {
			if((player.getLevel() >=15 && enemies.size()!=0) ||
			(player2.getLevel()>= 15 && enemies.size()!=0))
			{
				renderer.getBatch().begin();
				nextRoomText="you're level 15, kill all enemies to move on";
				font.setColor(Color.RED);
				font.draw(renderer.getBatch(), nextRoomText, 220.5f, -150,screenWidth, Align.topLeft, false);	
				Timer timer=new Timer();
				timer.scheduleTask(new Timer.Task() 
				{
					@Override
					public void run() 
					{
						nextRoomText= "";
					}
				},3f); 
				renderer.getBatch().end();
			}
			else
			{
				camera.position.set(304, -48,0);
				map.dispose();
				renderer.dispose();
				map = new TmxMapLoader().load("tiledmaps/forestRoom.tmx");
				myText = "forestroom";
				roomCount++;
				myRightText = "no of rooms: " + roomCount;
				renderer = new IsometricTiledMapRenderer(map, 1.2f);
				entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
				floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
				overlapLayer = (TiledMapTileLayer) map.getLayers().get(2);
				player.getPlayerSprite().setPosition(357.5f,-97.25f);
				player.getCoordinates().set(30,7,0);
	
				for(int i=0;i<enemies.size();i++)
				{
					world.destroyBody(enemiesAI.get(i).getBody());
				}
				createChest();
				createEnemies();
				increaseXP(player, 10);
				increaseXP(player2, 10);
			}

		
		}
		
		if(debug !=null)
		{
			debug.render(world,camera.combined);
		}	

		level1 = player.getLevel();
		LevelLable1.setText("Player 1 Level: " + level1);
		level2 = player2.getLevel();
		LevelLable2.setText("Player 2 Level: " + level2);
		hudStage.getRoot().setPosition(650, 815);
		hudStage.act(Gdx.graphics.getDeltaTime());
		hudStage.draw();
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
		enemies.clear();
		enemiesAI.clear();
		p1enemies =0;
		p2enemies =0;
		int type;
		int type2;
		int bossType=-1;
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
			amount = (int)Math.floor(Math.random() *(max - min + 1) + min);
		}
		for(int i=0;i<amount;i++)
		{
			int x = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17); //random numbers for x and y offsets
			int y = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17);
			int gridX = x;
			int gridY = y;
			float realX = 298 + (gridX - gridY) * (9.5f);
			float realY = 166 - (gridX + gridY) * (4.75f);
			enemies.add(new Enemy(world, realX, realY, type,type2,i,bossType,roomCount));
			enemiesAI.add(enemies.get(i).addAI(enemies.get(i)));
			int randomPlayer=(int)Math.floor(Math.random() *(2 - 1 + 1) + 1);
			if (randomPlayer == 1) {
				enemies.get(i).setTarget(player);
				p1enemies++;
				target = new Steering(p1, 0);
			}
			else if (randomPlayer == 2) {
				enemies.get(i).setTarget(player2);
				p2enemies++;
				target = new Steering(p2, 0);
			}
			//Seek<Vector2> seek = new Seek<Vector2>(enemiesAI[i],target);
			//enemiesAI[i].setBehaviour(seek);
			 
			Arrive<Vector2> arriveSB = new Arrive<Vector2>(enemiesAI.get(i),target)
			.setTimeToTarget(1f)
			.setArrivalTolerance(1f)
			.setDecelerationRadius(5);
			enemiesAI.get(i).setBehaviour(arriveSB);
			
			 
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
						chest.open(player);
						increaseXP(player, 20);
						chestText="Chest opened by player 1";
						colliding=true;
					}
					if(contact.getFixtureB().getBody().getUserData()=="chest"&&
						contact.getFixtureA().getBody().getUserData()=="player2")
					{
						chest.open(player2);
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
