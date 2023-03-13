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

    public Chest(World world, float x, float y) {
        textureAtlas = new TextureAtlas("enemy/zombieSprites.atlas");
        chestSprite = new Sprite(textureAtlas.findRegion("zombieDown",1));
        chestSprite.setPosition(Gdx.graphics.getWidth()/2 - chestSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - chestSprite.getHeight()/2);
        this.body = createBody(world,x,y);
    }

    private Body createBody(World world,float x, float y)
    {
        Body b;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + chestSprite.getWidth()/2), (y- chestSprite.getHeight()/2));
        b = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(chestSprite.getWidth()/2 / Constants.PPM +1, chestSprite.getHeight()/2 / Constants.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
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
}