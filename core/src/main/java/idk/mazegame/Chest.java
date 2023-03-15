package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import idk.mazegame.EnemyAI.Constants;

public class Chest {
    private TextureAtlas textureAtlas;
    private Sprite chestSprite;
    private Body body;
    private boolean opened;
    private String text;
    public Chest(World world, float x, float y) {
        textureAtlas = new TextureAtlas("items/chest.atlas");
        chestSprite = new Sprite(textureAtlas.findRegion("chest",-1));
        chestSprite.setPosition(Gdx.graphics.getWidth()/2 - chestSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - chestSprite.getHeight()/2);
        this.body = createBody(world,x,y);
        this.body.setUserData("chest");
        opened =false;
    }

    private Body createBody(World world,float x, float y)
    {
        Body b;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + chestSprite.getWidth()/2), (y- chestSprite.getHeight()/2));
        b = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(chestSprite.getWidth()/2 / Constants.PPM +4, chestSprite.getHeight()/2 / Constants.PPM+2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 100f;
        Fixture fixture = b.createFixture(fixtureDef);
        shape.dispose();
        return b;
    }
    public Sprite getChestSprite() {
        return chestSprite;
    }
    public Body getBody()
    {
        return body;
    }
    public void open(Player p)
    {
        opened= true;
        text="opened by "+p.getPlayerNum();
        //create item add to to the player
        System.out.println("opened");
    }
    public boolean isOpened()
    {
        return opened;
    }
    public String getText()
    {
        return text;
    }
}