package idk.mazegame;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.*;
import idk.mazegame.screens.PlayScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

/**
 * make grid/tile-based movement
 * make collisions
 * make a UI
 * make a menu
 * make the camera move when you walk into the entrance of a new room
 *
 * separate stuff into classes
 * -> put all the movement stuff, sprites, frame_counter, max_frames? into Player class
 *
 *
 * */
public class MazeGame extends Game implements InputProcessor {
	private SpriteBatch batch;
	private Sprite backgroundImage;
	private BitmapFont font;
	private String myText;
	private GlyphLayout layout;
	private Sound sound;
	private Music song1,song2;
	private OrthographicCamera camera;
	private Viewport viewport;

	private Enemy e2;
	private Player player, player2;
	private TiledMap map;
	private IsometricTiledMapRenderer renderer;
	private final float GAME_WORLD_WIDTH = 1600;
	private final float GAME_WORLD_HEIGHT = 900;
	private int inputDelay = 1;
	private int screenWidth;
	private int screenHeight;

	@Override
	public void create() {
		//setScreen(new PlayScreen());

		map =  new TmxMapLoader().load("tiledmaps/safeRoom.tmx");
		renderer = new IsometricTiledMapRenderer(map, 1.2f);
		TiledMapTileLayer entityLayer = (TiledMapTileLayer) map.getLayers().get(1);
		TiledMapTileLayer floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
		//entityLayer.
		//entityLayer.getCell(,).getTile().getId()


		batch = new SpriteBatch();
		backgroundImage = new Sprite(new Texture(Gdx.files.internal("testRoom.png")));
		backgroundImage.setSize(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT);

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

		font = new BitmapFont(Gdx.files.internal("myfont.fnt"));
		font.getData().setScale(0.4f);
		myText = "testroom";
		layout = new GlyphLayout();
		layout.setText(font, myText);

		sound = Gdx.audio.newSound(Gdx.files.internal("firered_0001_mono.wav"));
		song1 = Gdx.audio.newMusic(Gdx.files.internal("JRPG_town_loop.ogg"));
		song2 = Gdx.audio.newMusic(Gdx.files.internal("27 Black Market.mp3"));

		camera = new OrthographicCamera();
//		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);
		viewport = new ScreenViewport(camera);
		viewport.apply();
		camera.position.set(backgroundImage.getX()/2 + Gdx.graphics.getWidth()/2, backgroundImage.getY()/2 + Gdx.graphics.getHeight()/2,0);
		camera.position.set(304, -48,0);
		camera.zoom = 0.25f;

		player = new Player(Gdx.files.internal("player1Sprites.atlas"));
		player2 = new Player(Gdx.files.internal("enemy/player2Sprites.atlas"));
		player.getPlayerSprite().setPosition(310,-64);
		player2.getPlayerSprite().setPosition(290,-64);
		player2.setUp(Input.Keys.W);
		player2.setLeft(Input.Keys.A);
		player2.setDown(Input.Keys.S);
		player2.setRight(Input.Keys.D);
		e2 = new Enemy();
		e2.getEnemySprite().setPosition(128,0);

		song1.setLooping(true);
		song1.play();

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

		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(backgroundImage.getX()/2 + screenWidth/2, backgroundImage.getY()/2 + screenHeight/2,0);
		camera.position.set(304, -48,0);
	}

	@Override
	public void render() {

		if (inputDelay != 0) {
			inputDelay--;
			//Gdx.gl.glClearColor(0.08f, 0.72f, 2.48f, 1f);
			Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			renderer.setView(camera);
			renderer.render();
			renderer.getBatch().begin();

			//renderer.getBatch().setProjectionMatrix(camera.combined);
			//backgroundImage.draw(renderer.getBatch());
			e2.render((SpriteBatch) renderer.getBatch());
			player2.getPlayerSprite().draw(renderer.getBatch());
			player.getPlayerSprite().draw(renderer.getBatch());

			font.draw(renderer.getBatch(), myText, 107.5f, 63.5f, screenWidth, Align.topLeft, false );
			renderer.getBatch().end();
			super.render();

			return;
		}

		player.checkInput();
		player2.checkInput();

		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		//Gdx.gl.glClearColor(0.08f, 0.72f, 2.48f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//camera.update();
		renderer.render();
		renderer.getBatch().begin();

		//renderer.getBatch().setProjectionMatrix(camera.combined);
		//backgroundImage.draw(renderer.getBatch());
		e2.render((SpriteBatch) renderer.getBatch());
		player2.getPlayerSprite().draw(renderer.getBatch());
		player.getPlayerSprite().draw(renderer.getBatch());

		//font.draw(renderer.getBatch(), myText, 10f, screenHeight - 10f, screenWidth, Align.topLeft, false );
		font.draw(renderer.getBatch(), myText, 107.5f, 63.5f, screenWidth, Align.topLeft, false );
		renderer.getBatch().end();
		inputDelay = 1;
		super.render();


	}

	@Override
	public void dispose() {
		renderer.getBatch().dispose();
		backgroundImage.getTexture().dispose();
		player.dispose();
		player2.dispose();
		song1.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		player.idle();
		player2.idle();
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
