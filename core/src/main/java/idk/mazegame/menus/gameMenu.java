package idk.mazegame.menus;

import javax.swing.event.ChangeListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import idk.mazegame.GameScreen;
import idk.mazegame.MazeGame;
//import javafx.scene.control.CheckBox;

public class gameMenu implements Screen{
    private final MazeGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private Texture logo;
    private Image logoImage;
    private TextButton startGame;
    private TextButton quitGame;

    public gameMenu(final MazeGame game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Skin skin = new Skin(Gdx.files.internal("UI/uiskin.json"), new TextureAtlas(Gdx.files.internal("UI/uiskin.atlas")));
        skin = new Skin(Gdx.files.internal("UI/uiskin.json"));
        new TextureAtlas(Gdx.files.internal("UI/uiskin.atlas"));
        Texture logo = new Texture(Gdx.files.internal("logo.png"));
        Image logoImage = new Image(logo);
        logoImage.setSize(640f, 640f);
        logoImage.setZIndex(0);
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera), game.batch);
        Gdx.input.setInputProcessor(stage);

        //creating table 
        Table table = new Table();
        table.center().padTop(200);
        table.setFillParent(true);

        startGame = new TextButton("Start Game", skin);
        startGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });
        quitGame = new TextButton("Quit Game", skin);
        quitGame.setZIndex(1);
        quitGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }

        });
    }



    @Override
    public void show() {

        Table menuTable = new Table();
        menuTable.center().padTop(200);
        menuTable.setFillParent(true);
        menuTable.top();
        menuTable.add(logoImage).center().top().padTop(50);
        menuTable.row();
        menuTable.add(startGame).height(75).width(200);
        menuTable.row().height(25);
        menuTable.add(quitGame).height(75).width(200);
        menuTable.center();
        stage.addActor(menuTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f,0.1f,0.2f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //stage.act(Gdx.graphics.getDeltaTime());
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hide'");
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispose'");
    }
}
