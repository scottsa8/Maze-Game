package idk.mazegame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import idk.mazegame.EnemyAI.Constants;
import idk.mazegame.EnemyAI.Steering;

public class Enemy {
    private TextureAtlas textureAtlas;
    private Sprite enemySprite;
    private Body body;
    private String name="";
    private int target=-1;
    private int currentFrame = 0;
    private int timer = 0;
    private final int MAX_FRAMES = 4;
    private final int FRAME_SPEED = 3;

    public Enemy(World world,float x, float y, int type) {
        String enemyAtlas = getAtlas(type);
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
        bodyDef.position.set((x + enemySprite.getWidth()/2), (y+ enemySprite.getHeight()/2));
        
        b = world.createBody(bodyDef);
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
    public void updateBody(float angle)
    {
        System.out.println(angle);
        
        if (timer > FRAME_SPEED) {
            currentFrame++;
            timer = 0;
        }

        if (currentFrame >= MAX_FRAMES)
            currentFrame = 0;
        if(angle > 0 && angle < 90)
        {
            enemySprite.setRegion(textureAtlas.findRegion(name+"Right",currentFrame));
            System.out.println(textureAtlas.findRegion(name+"Right",currentFrame));
        }
        else
        {
            
        }
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
    public Steering addAI(Enemy e)
    {
        Steering AI = new Steering(e.getBody(),3);
        return AI;
    }
    private String getAtlas(int type)
    {
        String atlas ="";
		
		int type2=0;
		
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
			type2 = (int)Math.floor(Math.random() *(2 - 1 + 1) + 1);
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
        return atlas;
    }
    public int getTarget()
    {
        if(target==-1)
        {
            setTarget();
            return target;
        }
        else
        {
            return target;
        }
    }
    private void setTarget()
    {
        target = (int)Math.floor(Math.random() *(2 - 1 + 1) + 1);
    }
    public void dispose()
    {
        this.dispose();
    }
}
