package idk.mazegame;

import java.lang.invoke.VolatileCallSite;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Driver;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Projectile 
{
    public Vector2 position;
    public float velocity;
    public Vector2 direction;
    private  Body body;
    private boolean hit;
    private TextureAtlas textureAtlas;
    private Sprite projSprite;
    public Projectile(World world, Vector2 pos,Vector2 direction,int index, String name, int playerNum, int damage, int speed)
    {
        textureAtlas = new TextureAtlas("items/magic.atlas");
        projSprite = new Sprite(textureAtlas.findRegion("magic",0));
        projSprite.setPosition(pos.x, pos.y);

        position = new Vector2(pos);
        velocity=speed;
        this.direction = direction;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos);
        body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = body.createFixture(fixtureDef);
        body.setUserData("proj"+","+index+","+name.toString()+","+playerNum+","+damage);
        shape.dispose();

      
    }
    public Sprite getSprite()
    {
        return projSprite;
    }
    public void update()
    {
        if(direction.x == 1 && direction.y ==0)
        position.x +=velocity;
        if(direction.x==2 && direction.y ==0)
        position.x -=velocity;
        if(direction.y ==1 && direction.x ==0)
        position.y += velocity;
        if(direction.y ==2 && direction.x ==0)
        position.y -=velocity;
 
      body.setTransform(position.x, position.y, 0);
      projSprite.setPosition(position.x, position.y);
    }
    public Body getBody()
    {
        return body;
    }
    public void setHit(boolean t)
    {
        hit=t;
    }
    public boolean isHit()
    {
        return hit;
    }
    public void updateUserData(int index)
    {
        this.body.setUserData("proj"+","+index);        
    }
}
