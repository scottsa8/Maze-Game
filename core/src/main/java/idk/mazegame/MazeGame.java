package idk.mazegame;

import com.badlogic.gdx.*;
import com.badlogic.gdx.ai.steer.*;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.*;
//import idk.mazegame.screens.PlayScreen;

import idk.mazegame.EnemyAI.Constants;
import idk.mazegame.EnemyAI.Steering;

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
	private SpriteBatch batch;
	private BitmapFont font;
	private String myText;
	private GlyphLayout layout;
	private Sound sound;
	private Music song1,song2;
	private OrthographicCamera camera;
	private Viewport viewport;

	private int amount;
	private int max=8,min=4;
	private Enemy enemies[];
	private Steering enemiesAI;

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
	private int screenWidth, screenHeight, playerX, playerY;
	private int logDelay = 60;
	World world = new World(new Vector2(0,0), false);
	private Body p1;
	private Box2DDebugRenderer debug;

	@Override
	public void create() {
		//setScreen(new PlayScreen());

		map =  new TmxMapLoader().load("tiledmaps/testRoom.tmx");
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
		layout = new GlyphLayout();
		layout.setText(font, myText);

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

		player = new Player(Gdx.files.internal("sprites/player1Sprites.atlas"));
		player2 = new Player(Gdx.files.internal("sprites/player2Sprites.atlas"));
		player.getPlayerSprite().setPosition(310,-64); //310, -64  [10px left, goes left 1 tile 10 px up, goes up 2 tiles]
		player2.getPlayerSprite().setPosition(290,-64);
		//player2.getPlayerSprite().setPosition(184,-69);
		//player2.getPlayerSprite().setPosition(300,-9);
		player2.setUp(Input.Keys.W);
		player2.setLeft(Input.Keys.A);
		player2.setDown(Input.Keys.S);
		player2.setRight(Input.Keys.D);
		player.setCoordinates(new Vector3(24,8,0));
		player2.setCoordinates(new Vector3(23,7,0));

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(player.getPlayerSprite().getX(), player.getPlayerSprite().getY());
		p1 = world.createBody(bodyDef);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(player.getPlayerSprite().getWidth()/2, player.getPlayerSprite().getHeight()/2);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		Fixture fixture = p1.createFixture(fixtureDef);

		target = new Steering(p1, 1);
		
		String atlas ="";
		String name="";
		int type2=0;
		int type = 1;//(int)Math.floor(Math.random() *(3 - 1 + 1) + 1);
		if(type ==1)
		{
			atlas = "zombieSprites.atlas";
			name="zombie";
		}
		if(type==2)
		{
			atlas = "";
			name="skeleton";
		}
		if(type ==3)
		{
			//make sure player xp > 10
			//decide to make imp/phantom (random number)
			type = (int)Math.floor(Math.random() *(2 - 1 + 1) + 1);
			if(type2==1)
			{
				atlas ="";
				name="";
			}
			else if(type2 == 2)
			{
				atlas ="";
				name="";
			}
		}
		atlas = "enemy/"+atlas;

		amount = (int)Math.floor(Math.random() *(max - min + 1) + min); //random amount of enemies between 4-8 (needs tweaking)
		enemies = new Enemy[amount];
		for(int i=0;i<amount;i++)
		{
			int x = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17); //random numbers for x and y offsets
			int y = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17);

			int gridX = x - 17;
			int gridY = y - 17;
			float realX = 292 + (gridX - gridY) * (9.5f);
			float realY = -21 - (gridX + gridY) * (4.75f);
			System.out.println("REAL:"+realX+":"+realY);
			enemies[i] = new Enemy(Gdx.files.internal("enemy/zombieSprites.atlas"),"zombie",world, realX, realY); //include a name to set the default image easier
			enemiesAI = new Steering(enemies[i].getBody(),3);			
			System.out.println("SPRITE:"+enemies[i].getEnemySprite().getX()+"Y:"+enemies[i].getEnemySprite().getY()); //prints x and Y for debugging
			System.out.println("BODY:"+enemies[i].getBody().getPosition().x+"Y:"+enemies[i].getBody().getPosition().y); //prints x and Y for debugging		}
		}
		song1.setLooping(true);
		//song1.play();
		song1.setVolume(0.5f);

		debug = new Box2DDebugRenderer(true, true, true, true, true, true);
		
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
		Arrive<Vector2> arriveSB = new Arrive<Vector2>(enemiesAI,target)
		.setTimeToTarget(1f)
		.setArrivalTolerance(1f)
		.setDecelerationRadius(5);
		enemiesAI.setBehaviour(arriveSB);

		shape.dispose();

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(304, -48,0);
	}

	@Override
	public void render() {
		world.step(1/30f, 6, 2);

		if (inputDelay == 0) {
			player.update(floorLayer, entityLayer);
			player2.update(floorLayer, entityLayer);
			enemiesAI.update(Gdx.graphics.getDeltaTime());
			//player.getPlayerSprite().setPosition(p1.getPosition().x, p1.getPosition().y);
			inputDelay = MAX_INPUT_DELAY;
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
		for(int i=0;i<amount;i++)
		{
			enemiesAI.update(Gdx.graphics.getDeltaTime());
			//enemies[i].getEnemySprite().setPosition(enemies[i].getBody().getPosition().x * Constants.PPM, enemies[i].getBody().getPosition().y* Constants.PPM);
			enemies[i].getEnemySprite().setPosition(enemies[i].getBody().getPosition().x - 15, enemies[i].getBody().getPosition().y - 15);

			enemies[i].getEnemySprite().draw(renderer.getBatch());
			
		}
		player2.getPlayerSprite().draw(renderer.getBatch());
		player.getPlayerSprite().draw(renderer.getBatch());

		//font.draw(renderer.getBatch(), myText, 10f, screenHeight - 10f, screenWidth, Align.topLeft, false );

//		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
//		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));
//		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(4));
//		renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(5));

		font.draw(renderer.getBatch(), myText, 107.5f, 63.5f, screenWidth, Align.topLeft, false );
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

			renderer.getBatch().end();

		}

		if (((int) (player.getCoordinates().x)) == 24 && ((int) (player.getCoordinates().y)) == 0) {

//			for (int i = 0; i < 304; i++){
//				camera.translate(1,0);
//				camera.update();
//			}
			camera.position.set(304, -48,0);
			map.dispose();
			renderer.dispose();
			map = new TmxMapLoader().load("tiledmaps/safeRoom.tmx");
			myText = "saferoom";
			renderer = new IsometricTiledMapRenderer(map, 1.2f);
			entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
			floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
			overlapLayer =  (TiledMapTileLayer) map.getLayers().get(2);
			player.getPlayerSprite().setPosition(366f,-35.5f);
			player.getCoordinates().set(24,14,0);

			for(int i=0;i<amount;i++)
			{
				int x = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17); //random numbers for x and y offsets
				int y = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17);

				int gridX = x - 17;
				int gridY = y - 17;
				enemies[i].getEnemySprite().setPosition(
						292 + (gridX - gridY) * (9.5f),
						-21 - (gridX + gridY) * (4.75f)); //this needs adjusting so they spawn in the board
			}
		}

		if (((int) (player.getCoordinates().x)) == 24 && ((int) (player.getCoordinates().y)) == 15) {
			camera.position.set(304, -48,0);
			map.dispose();
			renderer.dispose();
			map = new TmxMapLoader().load("tiledmaps/testRoom.tmx");
			myText = "testroom";
			renderer = new IsometricTiledMapRenderer(map, 1.2f);
			entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
			floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
			overlapLayer = (TiledMapTileLayer) map.getLayers().get(2);
			player.getPlayerSprite().setPosition(242.5f,-97f);
			player.getCoordinates().set(24,1,0);

			for(int i=0;i<amount;i++)
			{
				int x = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17); //random numbers for x and y offsets
				int y = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17);

				int gridX = x - 17;
				int gridY = y - 17;
				enemies[i].getEnemySprite().setPosition(
						292 + (gridX - gridY) * (9.5f),
						-21 - (gridX + gridY) * (4.75f)); //this needs adjusting so they spawn in the board
			}
		}

		if (((int) (player.getCoordinates().x)) == 16 && ((int) (player.getCoordinates().y)) == 7) {
			camera.position.set(304, -48,0);
			map.dispose();
			renderer.dispose();
			map = new TmxMapLoader().load("tiledmaps/forestRoom.tmx");
			myText = "forestroom";
			renderer = new IsometricTiledMapRenderer(map, 1.2f);
			entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
			floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
			overlapLayer = (TiledMapTileLayer) map.getLayers().get(2);
			player.getPlayerSprite().setPosition(357.5f,-97.25f);
			player.getCoordinates().set(30,7,0);

			for(int i=0;i<amount;i++)
			{
				int x = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17); //random numbers for x and y offsets
				int y = (int)Math.floor(Math.random() *(29 - 17 + 1) + 17);

				int gridX = x - 17;
				int gridY = y - 17;

				enemies[i].getEnemySprite().setPosition(
						292 + (gridX - gridY) * (9.5f),
						-21 - (gridX + gridY) * (4.75f)); //this needs adjusting so they spawn in the board
			}
		}
		debug.render(world,camera.combined);
	}

	@Override
	public void dispose() {
		renderer.getBatch().dispose();
		map.dispose();
		player.dispose();
		player2.dispose();
		song1.dispose();
	}
}
