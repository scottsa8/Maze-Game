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

import javax.xml.soap.Text;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

/**
 * Solving end frame for walk cycle - create an int variable which takes a value that was sent from the last direction
 *
 * dont even need to do this, cuz its gonna be tiled-based movement anyway
 * */
public class MazeGame extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
	private Sprite sprite1;
	private Sprite playerSprite;
	private BitmapFont font;
	private String myText;
	private GlyphLayout layout;
	private Texture image;
	private TextureRegion[] regions;
	private Sprite sprite;
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
	private int MAX_FRAMES = 4;
	private Animation<TextureRegion> animation;
	private final float PLAYER_SPEED = 6f;
	private final int FRAME_SPEED = 3;
	private int lastKeyedDirection = 0;
	private int secondlastKeyedDirection = 0;
	private int inputDelay = 1;

	@Override
	public void create() {
		batch = new SpriteBatch();
		sprite1 = new Sprite(new Texture(Gdx.files.internal("bg.png")));
		sprite1.setSize(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT);

		float aspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
//		font = new BitmapFont(Gdx.files.internal("myfont.fnt"));
//		myText = "I took one, one cause you left me\n" + "Two, two for my family\n" + "Three, three for my heartache";
//		layout = new GlyphLayout();
//		layout.setText(font, myText);
		image = new Texture("newcharSprites.png");
		regions = new TextureRegion[80];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 8; j++) {
				regions[i*7 + j] = new TextureRegion(image, j * 16,i * 16,16,16);
			}
		}

		animation = new Animation<TextureRegion>(0.3f,regions);

		playerSprite = new Sprite(regions[18]);
		playerSprite.setPosition(Gdx.graphics.getWidth()/2 - playerSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - playerSprite.getHeight()/2);
		sprite = new Sprite(image);
		sprite.setPosition(Gdx.graphics.getWidth()/2 - image.getWidth()/2, Gdx.graphics.getHeight()/2 - image.getHeight()/2.2f);
		sprite.setScale(0.5f);
		sound = Gdx.audio.newSound(Gdx.files.internal("firered_0001_mono.wav"));
//		long id = sound.play();
		song1 = Gdx.audio.newMusic(Gdx.files.internal("03 Underground.mp3"));
		song2 = Gdx.audio.newMusic(Gdx.files.internal("27 Black Market.mp3"));
		camera = new OrthographicCamera();
//		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);
		//viewport = new FitViewport(GAME_WORLD_HEIGHT * aspectRatio, GAME_WORLD_HEIGHT, camera);
		//viewport = new FillViewport(GAME_WORLD_HEIGHT * aspectRatio, GAME_WORLD_HEIGHT, camera);
		viewport = new StretchViewport(GAME_WORLD_HEIGHT * aspectRatio, GAME_WORLD_HEIGHT, camera);
		//viewport = new ExtendViewport(GAME_WORLD_WIDTH * aspectRatio, GAME_WORLD_HEIGHT, camera);
		//viewport = new ScreenViewport(camera);
		viewport.apply();
		camera.position.set(sprite1.getX()/2 + Gdx.graphics.getWidth()/2, sprite1.getY()/2 + Gdx.graphics.getHeight()/2,0);
//		long ourSoundID = sound.loop(1.0f,1.0f,0.0f);

//		Timer.schedule(new Timer.Task() {
//			@Override
//			public void run() {
//				sound.pause(ourSoundID);
//			}
//		},10);

		//song2.play();
		//song2.setVolume(0.5f);

		textureAtlas = new TextureAtlas(Gdx.files.internal("charSprites.atlas"));
		textureRegion = textureAtlas.findRegion("playerDown", 0);
		testSprite = new Sprite(textureRegion);
		testSprite.setPosition(Gdx.graphics.getWidth()/2 - sprite.getWidth()/2, Gdx.graphics.getHeight()/2 - sprite.getHeight()/2);
		testSprite.setScale(4);

		song2.setOnCompletionListener(new Music.OnCompletionListener() {
			@Override
			public void onCompletion(Music music) {
				song1.play();
				song1.setVolume(0.5f);
			}
		});

		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				if(song2.isPlaying())
					if(song2.getPosition() >= 10.0f)
						song2.setVolume(song2.getVolume() - 0.125f);
			}
		},29,1,4);

//		sound.setVolume(id, 1.0f);
//		sound.setPitch(id, 2.0f);
//		sound.setPan(id, -1f, 1f);

		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(sprite1.getX()/2 + Gdx.graphics.getWidth()/2, sprite1.getY()/2 + Gdx.graphics.getHeight()/2,0);
	}

	@Override
	public void render() {

		if (inputDelay != 0) {
			inputDelay--;
			Gdx.gl.glClearColor(0.08f, 0.72f, 2.48f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			batch.begin();

			batch.setProjectionMatrix(camera.combined);
			sprite1.draw(batch);
			testSprite.draw(batch);

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

			playerSprite.translateX(-1f);
			playerSprite.setRegion(regions[20]);
			camera.translate(-1f,0f);
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

			playerSprite.translateX(+1f);
			playerSprite.setRegion(regions[16]);
			camera.translate(+1f,0f);
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

			playerSprite.translateY(-1f);
			playerSprite.setRegion(regions[18]);
			camera.translate(0f,-1f);
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

			playerSprite.translateY(+1f);
			playerSprite.setRegion(regions[14]);
			camera.translate(0f,+1f);
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

			playerSprite.translateY(+1f);
			playerSprite.setRegion(regions[14]);
			camera.translate(0f,+PLAYER_SPEED);
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

			playerSprite.translateY(+1f);
			playerSprite.setRegion(regions[14]);
			camera.translate(0f,+PLAYER_SPEED);
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

			playerSprite.translateY(+1f);
			playerSprite.setRegion(regions[14]);
			camera.translate(0f,+PLAYER_SPEED);
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

			playerSprite.translateY(+1f);
			playerSprite.setRegion(regions[14]);
			camera.translate(0f,+PLAYER_SPEED);
			timer++;
			lastKeyedDirection = 1;
		}


		//Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glClearColor(0.08f, 0.72f, 2.48f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//camera.update();

		batch.begin();

		batch.setProjectionMatrix(camera.combined);
		sprite1.draw(batch);
		testSprite.draw(batch);

		//font.draw(batch, myText, 0, Gdx.graphics.getHeight()/2 + layout.height/2, Gdx.graphics.getWidth(), Align.center, false );
		//batch.draw(image, Gdx.graphics.getWidth()/2 - image.getWidth()/2, Gdx.graphics.getHeight()/2 - image.getHeight()/2 ); // x 140 y 210
		//batch.draw(regions[18], Gdx.graphics.getWidth()/2 - image.getWidth()/2, Gdx.graphics.getHeight()/2 - image.getHeight()/2);
		//batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getWidth()/2, sprite.getHeight()/2, sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation() );
		//playerSprite.draw(batch);
		batch.end();
		inputDelay = 1;
	}

	@Override
	public void dispose() {
		batch.dispose();
		image.dispose();
		sprite1.getTexture().dispose();
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
