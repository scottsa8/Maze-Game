package idk.mazegame.menus;

import javax.swing.event.ChangeListener;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import idk.mazegame.MazeGame;

public class GameMenu extends Game implements Screen{
    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private boolean started=false;
 
    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
            //stage.act(Gdx.graphics.getDeltaTime());
            stage.act();
            stage.draw();
    }
    @Override
    public void resize(int width, int height) {
       stage.getViewport().update(width, height,true);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {

    }


    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        started=false;
    }
    public boolean getStarted()
    {
        return started;
    }
    @Override
    public void create() {
       
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        skin = new Skin(Gdx.files.internal("UI/uiskin.json"), 
        new TextureAtlas(Gdx.files.internal("UI/uiskin.atlas")));
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        Gdx.input.setInputProcessor(stage);
        //creating table 
        Table menuTable = new Table();
        menuTable.center().padTop(200);
        menuTable.setFillParent(true);
        menuTable.top();

        TextButton startGame = new TextButton("Start Game", skin);
        startGame.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                started = true;
            }
        });

        menuTable.add(startGame);
        stage.addActor(menuTable);
    }
    @Override
    public void render() {
        if(started == true)
        {
           //set focus window to a new game
           Gdx.input.setInputProcessor(null);
           //Gdx.input.setInputProcessor(new MazeGame());
           new MazeGame().create();
           this.dispose();
           
        }
        else
        {
            render(0);
        }
        
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hide'");
    }
}
