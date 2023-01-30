package idk.mazegame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy {
    private TextureAtlas textureAtlas;
    private Sprite enemySprite;

    public Enemy(FileHandle enemyAtlas,String name) {
        textureAtlas = new TextureAtlas(enemyAtlas);
        enemySprite = new Sprite(textureAtlas.findRegion(name+"Down",0));
        enemySprite.setPosition(Gdx.graphics.getWidth()/2 - enemySprite.getWidth()/2, Gdx.graphics.getHeight()/2 - enemySprite.getHeight()/2);
       enemySprite.setScale(0.5f);
       
    }
    
    public void setScale(Float x)
    {
        enemySprite.setScale(x);
    }
    public void render(SpriteBatch b)
    {
        enemySprite.draw(b);
    }
   
    public Sprite getEnemySprite() {
        return enemySprite;
    }

    public void setEnemySprite(Sprite enemySprite) {
        this.enemySprite = enemySprite;
    }
}
