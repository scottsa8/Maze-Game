package idk.mazegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.*;

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
public class MazeGame extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
	private Sprite backgroundImage;
	private BitmapFont font;
	private String myText;
	private GlyphLayout layout;
	private Sound sound;
	private Music song1,song2;
	private OrthographicCamera camera;
	private final float GAME_WORLD_WIDTH = 1600;
	private final float GAME_WORLD_HEIGHT = 900;
	private Viewport viewport;

	private TextureAtlas textureAtlas;
	private Sprite testSprite;
	private TextureRegion textureRegion;
	private int currentFrame = 1;
	private int timer = 0;
	private final int MAX_FRAMES = 4;
	private final float PLAYER_SPEED = 6f;
	private final int FRAME_SPEED = 3;
	private int lastKeyedDirection = 0;
	private int secondlastKeyedDirection = 0;
	private int inputDelay = 1;
	private int screenWidth;
	private int screeenHeight;

	@Override
	public void create() {
		batch = new SpriteBatch();
		backgroundImage = new Sprite(new Texture(Gdx.files.internal("testRoom.png")));
		backgroundImage.setSize(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT);

		screenWidth = Gdx.graphics.getWidth();
		screeenHeight = Gdx.graphics.getHeight();

		float aspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
		font = new BitmapFont(Gdx.files.internal("myfont.fnt"));
		myText = "testroom";
		layout = new GlyphLayout();
		layout.setText(font, myText);

		sound = Gdx.audio.newSound(Gdx.files.internal("firered_0001_mono.wav"));
		song1 = Gdx.audio.newMusic(Gdx.files.internal("03 Underground.mp3"));
		song2 = Gdx.audio.newMusic(Gdx.files.internal("27 Black Market.mp3"));

		camera = new OrthographicCamera();
//		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);
		viewport = new ScreenViewport(camera);
		viewport.apply();
		camera.position.set(backgroundImage.getX()/2 + Gdx.graphics.getWidth()/2, backgroundImage.getY()/2 + Gdx.graphics.getHeight()/2,0);


		textureAtlas = new TextureAtlas(Gdx.files.internal("charSprites.atlas"));
		textureRegion = textureAtlas.findRegion("playerDown", 0);
		testSprite = new Sprite(textureRegion);
		testSprite.setPosition(Gdx.graphics.getWidth()/2 - testSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - testSprite.getHeight()/2);
		testSprite.setScale(4f);

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
		camera.position.set(backgroundImage.getX()/2 + screenWidth/2, backgroundImage.getY()/2 + screeenHeight/2,0);
	}

	@Override
	public void render() {

		if (inputDelay != 0) {
			inputDelay--;
			//Gdx.gl.glClearColor(0.08f, 0.72f, 2.48f, 1f);
			Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			batch.begin();

			batch.setProjectionMatrix(camera.combined);
			backgroundImage.draw(batch);
			testSprite.draw(batch);

			font.draw(batch, myText, 10f, screeenHeight - 10f, screenWidth, Align.topLeft, false );
			batch.end();
			return;
		}

		secondlastKeyedDirection = lastKeyedDirection;

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {

			if (timer > FRAME_SPEED) {
				currentFrame++;
				timer = 0;
			}

			if (currentFrame >= MAX_FRAMES)
				currentFrame = 0;

			if (secondlastKeyedDirection == 4) {

				if (currentFrame == 2)
					testSprite.setRegion(textureAtlas.findRegion("playerLeft", 0));

				if (currentFrame == 3)
					testSprite.setRegion(textureAtlas.findRegion("playerLeft", 2));

				if (currentFrame == 0 || currentFrame == 1)
					testSprite.setRegion(textureAtlas.findRegion("playerLeft", currentFrame));

			}

			testSprite.translateX(-PLAYER_SPEED);

			timer++;
			lastKeyedDirection = 4;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {

			if (timer > FRAME_SPEED) {
				currentFrame++;
				timer = 0;
			}

			if (currentFrame >= MAX_FRAMES)
				currentFrame = 0;

			if (secondlastKeyedDirection == 6) {

				if (currentFrame == 2)
					testSprite.setRegion(textureAtlas.findRegion("playerRight", 0));

				if (currentFrame == 3)
					testSprite.setRegion(textureAtlas.findRegion("playerRight", 2));

				if (currentFrame == 0 || currentFrame == 1)
					testSprite.setRegion(textureAtlas.findRegion("playerRight", currentFrame));

			}

			testSprite.translateX(+PLAYER_SPEED);
			timer++;
			lastKeyedDirection = 6;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {

			if (timer > FRAME_SPEED) {
				currentFrame++;
				timer = 0;
			}

			if (currentFrame >= MAX_FRAMES)
				currentFrame = 0;

			if (secondlastKeyedDirection == 2) {

				if (currentFrame == 2)
					testSprite.setRegion(textureAtlas.findRegion("playerDown", 0));

				if (currentFrame == 3)
					testSprite.setRegion(textureAtlas.findRegion("playerDown", 2));

				if (currentFrame == 0 || currentFrame == 1)
					testSprite.setRegion(textureAtlas.findRegion("playerDown", currentFrame));
			}

			testSprite.translateY(-PLAYER_SPEED);
			timer++;
			lastKeyedDirection = 2;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP) && !(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {

			if (timer > FRAME_SPEED) {
				currentFrame++;
				timer = 0;
			}

			if (currentFrame >= MAX_FRAMES)
				currentFrame = 0;

			if (secondlastKeyedDirection == 8) {

				if (currentFrame == 2)
					testSprite.setRegion(textureAtlas.findRegion("playerUp", 0));

				if (currentFrame == 3)
					testSprite.setRegion(textureAtlas.findRegion("playerUp", 2));

				if (currentFrame == 0 || currentFrame == 1)
					testSprite.setRegion(textureAtlas.findRegion("playerUp", currentFrame));

			}

			testSprite.translateY(+PLAYER_SPEED);
			timer++;
			lastKeyedDirection = 8;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP) && Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			if (timer > FRAME_SPEED) {
				currentFrame++;
				timer = 0;
			}

			if (currentFrame >= MAX_FRAMES)
				currentFrame = 0;

			if (currentFrame == 2)
				testSprite.setRegion(textureAtlas.findRegion("playerRUp", 0));

			if (currentFrame == 3)
				testSprite.setRegion(textureAtlas.findRegion("playerRUp", 2));

			if (currentFrame == 0 || currentFrame == 1)
				testSprite.setRegion(textureAtlas.findRegion("playerRUp", currentFrame));

			testSprite.translate(+(PLAYER_SPEED*0.707f), +(PLAYER_SPEED*0.707f));
			timer++;
			lastKeyedDirection = 9;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP) && Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			if (timer > FRAME_SPEED) {
				currentFrame++;
				timer = 0;
			}

			if (currentFrame >= MAX_FRAMES)
				currentFrame = 0;

			if (currentFrame == 2)
				testSprite.setRegion(textureAtlas.findRegion("playerLUp", 0));

			if (currentFrame == 3)
				testSprite.setRegion(textureAtlas.findRegion("playerLUp", 2));

			if (currentFrame == 0 || currentFrame == 1)
				testSprite.setRegion(textureAtlas.findRegion("playerLUp", currentFrame));

			testSprite.translate(-(PLAYER_SPEED*0.707f), +(PLAYER_SPEED*0.707f));
			timer++;
			lastKeyedDirection = 7;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			if (timer > FRAME_SPEED) {
				currentFrame++;
				timer = 0;
			}

			if (currentFrame >= MAX_FRAMES)
				currentFrame = 0;

			if (currentFrame == 2)
				testSprite.setRegion(textureAtlas.findRegion("playerRDown", 0));

			if (currentFrame == 3)
				testSprite.setRegion(textureAtlas.findRegion("playerRDown", 2));

			if (currentFrame == 0 || currentFrame == 1)
				testSprite.setRegion(textureAtlas.findRegion("playerRDown", currentFrame));

			testSprite.translate(+(PLAYER_SPEED*0.707f), -(PLAYER_SPEED*0.707f));
			timer++;
			lastKeyedDirection = 3;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			if (timer > FRAME_SPEED) {
				currentFrame++;
				timer = 0;
			}

			if (currentFrame >= MAX_FRAMES)
				currentFrame = 0;

			if (currentFrame == 2)
				testSprite.setRegion(textureAtlas.findRegion("playerLDown", 0));

			if (currentFrame == 3)
				testSprite.setRegion(textureAtlas.findRegion("playerLDown", 2));

			if (currentFrame == 0 || currentFrame == 1)
				testSprite.setRegion(textureAtlas.findRegion("playerLDown", currentFrame));

			testSprite.translate(-(PLAYER_SPEED*0.707f), -(PLAYER_SPEED*0.707f));
			timer++;
			lastKeyedDirection = 1;
		}

		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		//Gdx.gl.glClearColor(0.08f, 0.72f, 2.48f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//camera.update();

		batch.begin();

		batch.setProjectionMatrix(camera.combined);
		backgroundImage.draw(batch);
		testSprite.draw(batch);

		font.draw(batch, myText, 10f, screeenHeight - 10f, screenWidth, Align.topLeft, false );
		batch.end();
		inputDelay = 1;
	}

	@Override
	public void dispose() {
		batch.dispose();
		backgroundImage.getTexture().dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (lastKeyedDirection == 8 && (secondlastKeyedDirection != 9 || secondlastKeyedDirection != 7))
			testSprite.setRegion(textureAtlas.findRegion("playerUp", 0));

		if (lastKeyedDirection == 2 && (secondlastKeyedDirection != 3 || secondlastKeyedDirection != 1))
			testSprite.setRegion(textureAtlas.findRegion("playerDown", 0));

		if (lastKeyedDirection == 4 && (secondlastKeyedDirection != 7 || secondlastKeyedDirection != 1))
			testSprite.setRegion(textureAtlas.findRegion("playerLeft", 0));

		if (lastKeyedDirection == 6 && (secondlastKeyedDirection != 9 || secondlastKeyedDirection != 3))
			testSprite.setRegion(textureAtlas.findRegion("playerRight", 0));

		if (lastKeyedDirection == 9 || secondlastKeyedDirection == 9)
			testSprite.setRegion(textureAtlas.findRegion("playerRUp", 0));

		if (lastKeyedDirection == 7 || secondlastKeyedDirection == 7)
			testSprite.setRegion(textureAtlas.findRegion("playerLUp", 0));

		if (lastKeyedDirection == 3 || secondlastKeyedDirection == 3)
			testSprite.setRegion(textureAtlas.findRegion("playerRDown", 0));

		if (lastKeyedDirection == 1 || secondlastKeyedDirection == 1)
			testSprite.setRegion(textureAtlas.findRegion("playerLDown", 0));

		currentFrame = 1;
		timer = 0;

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
