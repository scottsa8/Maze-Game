package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
    private Player target;
    private int currentFrame = 0;
    private int timer = 0;
    private final int MAX_FRAMES = 3;
    private final int FRAME_SPEED = 3;
    private boolean dead = false;
    private int health;
    private int damage;
    private int xpValue;
    private Color color;

    public Enemy(World world,float x, float y, int type,int type2, int index) {
        String enemyAtlas = getAtlas(type,type2);
        textureAtlas = new TextureAtlas(enemyAtlas);
        enemySprite = new Sprite(textureAtlas.findRegion(name+"Right",0));
        enemySprite.setPosition(Gdx.graphics.getWidth()/2 - enemySprite.getWidth()/2, Gdx.graphics.getHeight()/2 - enemySprite.getHeight()/2);
        this.body = createBody(world,x,y);
        
        this.body.setUserData("enemy"+","+index);   
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
    private String getAtlas(int type, int type2)
    {
        String atlas ="";
		if(type ==1)
		{
			atlas = "zombieSprites.atlas";
			name="zombie";
            damage=5;
            health =100;
            xpValue =5;
            color= Color.GREEN;
		}
		if(type==2)
		{
			atlas = "demon.atlas";
			name="demon";
            damage=10;
            health =100;
            xpValue =10;
            color= Color.RED;
		}
		if(type ==3)
		{
            if(type2==1)
            {
                atlas ="ghost.atlas";
                name="ghost";
                damage=20;
                health =120;
                xpValue =15;
                color= Color.BLACK;
            }
            else if(type2 == 2)
            {
                atlas ="impGolden.atlas";
                name="imp";
                health =120;
                damage=35;
                xpValue =50;
                color = Color.GOLD;
            }
        else
        {
            System.out.println("FUCK IM IN THE ELSE");
            atlas = "zombieSprites.atlas";
            name="zombie";
            damage=20;
            health =100;
            xpValue =5;
            color= Color.GREEN;
        }
		
		}
		atlas = "enemy/"+atlas;
        return atlas;
    }
    public int getXpValue()
    {
        return xpValue;
    }
    public int getTarget()
    {
        if(target.getPlayerNum() == 1)
        {
            return 1;
        }
        else
        {
            return 2;
        }
    }
    public void setTarget(Player p)
    {
        target = p;
    }
    public void dispose(Enemy e)
    {
        textureAtlas.dispose();
        enemySprite.getTexture().dispose();
    }
    public Color getHpColor()
    {
        if(color == null)
        {
            return Color.WHITE;
        }
        else
        {
            return color;
        }
    }
    public void attack(Player p)
    {
       p.setHealth(p.getHealth()-damage);
    }
    public void die(Enemy e)
    {
        textureAtlas.dispose();
        e.enemySprite.getTexture().dispose();
        this.dead=true;
    }
    public void takeDamage(int damage)
    {
        health = health-damage;
        if(health <= 0)
        {
            die(this);
        }
    }
    public int getHealth()
    {
        return health;
    }
    public boolean isDead()
    {
        return dead;
    }
  
}
