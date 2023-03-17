package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import idk.mazegame.EnemyAI.Constants;

public class Chest {
    private TextureAtlas textureAtlas;
    private TextureAtlas textureAtlas2;
    private Sprite chestSprite;
    private Sprite itemSprite;
    private Body body;
    private boolean opened = false;
    private Item generated;
    public Chest(World world, float x, float y) {
        textureAtlas = new TextureAtlas("items/chest.atlas");
        chestSprite = new Sprite(textureAtlas.findRegion("chest",-1));
        chestSprite.setPosition(Gdx.graphics.getWidth()/2 - chestSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - chestSprite.getHeight()/2);
        this.body = createBody(world,x,y);
        this.body.setUserData("chest");
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
    public void open(Player p, ItemAttributes attrs)
    {
        opened= true;
        generated = new Item(attrs, p.getLevel());
        String path = generated.getPath();
        Double pathIndex = Double.valueOf(path);
 
        textureAtlas2 = new TextureAtlas("items/items.atlas");
        itemSprite = new Sprite(textureAtlas2.findRegion("item",(int)Math.round(pathIndex)));
        itemSprite.setPosition(Gdx.graphics.getWidth()/2 - chestSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - chestSprite.getHeight()/2);
        
        p.addToInv(generated);
    }
    public boolean isOpened()
    {
        return opened;
    }
    public Sprite getItemSprite()
    {
        return itemSprite;
    }
}
