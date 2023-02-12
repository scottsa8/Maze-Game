package idk.mazegame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import idk.mazegame.EnemyAI.Constants;

public class Enemy {
    private TextureAtlas textureAtlas;
    private Sprite enemySprite;
    private Body body;
    public Enemy(FileHandle enemyAtlas,String name,World world,float x, float y) {
        textureAtlas = new TextureAtlas(enemyAtlas);
        enemySprite = new Sprite(textureAtlas.findRegion(name+"Down",0));
        enemySprite.setPosition(Gdx.graphics.getWidth()/2 - enemySprite.getWidth()/2, Gdx.graphics.getHeight()/2 - enemySprite.getHeight()/2);
        enemySprite.setScale(0.4f);
        this.body = createBody(world,x,y);
    }
    private Body createBody(World world,float x, float y)
    {
        Body b;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + enemySprite.getWidth()/2) / Constants.PPM, (y+ enemySprite.getHeight()/2) / Constants.PPM);
        
        b = world.createBody(bodyDef);
        System.out.println("body position:"+b.getPosition());
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(enemySprite.getWidth()/2 / Constants.PPM, enemySprite.getHeight()/2 / Constants.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = b.createFixture(fixtureDef);
        shape.dispose();
        return b;
    }
    public Body getBody()
    {
        return body;
    }
    public void updateBody(float x, float y)
    {
        body.setTransform(x, y,0);
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
