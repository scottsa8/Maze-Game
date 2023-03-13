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
import com.badlogic.gdx.utils.BooleanArray;

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
    private final int MAX_FRAMES = 3;
    private final int FRAME_SPEED = 3;
    private boolean dead = false;
    private int Health =100;
    private int p1XP;
    private int p2XP;

    public Enemy(World world,float x, float y, int type, int index, int xp1, int xp2) {
        p1XP =  xp1;
        p2XP =xp2;
        String enemyAtlas = getAtlas(type);
        textureAtlas = new TextureAtlas(enemyAtlas);
        enemySprite = new Sprite(textureAtlas.findRegion(name+"Right",0));
        enemySprite.setPosition(Gdx.graphics.getWidth()/2 - enemySprite.getWidth()/2, Gdx.graphics.getHeight()/2 - enemySprite.getHeight()/2);
        this.body = createBody(world,x,y);
        
        this.body.setUserData("enemy"+","+index);   
        System.out.println(this.body.getUserData())  ;   
    }
    public void updateUserData(int index)
    {
        this.body.setUserData("enemy"+","+index);        
    }
    private Body createBody(World world,float x, float y)
    {
        Body b;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + enemySprite.getWidth()/2), (y- enemySprite.getHeight()/2));
        b = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(enemySprite.getWidth()/2 / Constants.PPM, enemySprite.getHeight()/2 / Constants.PPM + 1);
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
    public void updateBody(float angle, Enemy e)
    {
        angle = (float) ((angle*180) / 3.14);
      //  System.out.println(angle);
        
        if (timer > FRAME_SPEED) {
            currentFrame++;
            timer = 0;
        }

        if (currentFrame >= MAX_FRAMES)
            currentFrame = 0;
        if(angle > 0 && angle < 90)
        {
            e.getEnemySprite().setRegion(textureAtlas.findRegion(name+"Up",currentFrame));
          //  System.out.println(textureAtlas.findRegion(name+"Right",currentFrame));
        }
        else if(angle>90 && angle <= 180)
        {
            e.getEnemySprite().setRegion(textureAtlas.findRegion(name+"Right",currentFrame));
          //  System.out.println(textureAtlas.findRegion(name+"Right",currentFrame));
            currentFrame++;
        }
        else if(angle >180)
        {
            e.getEnemySprite().setRegion(textureAtlas.findRegion(name+"Down",currentFrame));
            //System.out.println(textureAtlas.findRegion(name+"Down",currentFrame));
            currentFrame++;
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
        Steering AI = new Steering(e.getBody(),5);
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
			atlas = "demon.atlas";
			name="demon";
		}
		if(type ==3)
		{
            if(p1XP > 10 || p2XP>10)
            {
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
            else
            {
                atlas = "zombieSprites.atlas";
			    name="zombie";
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
    public void dispose(Enemy e)
    {
     
        
    }
    public void attack(Player p)
    {
        //player take damage.
    }
    public void die(Enemy e)
    {
        textureAtlas.dispose();
        e.enemySprite.getTexture().dispose();
        this.dead=true;
    }
    public void takeDamage(int damage)
    {
        Health = Health-damage;
        System.out.println("Health:"+Health);
        if(Health == 0)
        {
            die(this);
        }
    }
    public int getHealth()
    {
        return Health;
    }
    public boolean isDead()
    {
        return dead;
    }
  
}
