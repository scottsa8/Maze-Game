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
    private String name = "";

    public Chest(World world, float x, float y, int type) {
        String chestAtlas = getAtlas(type);
        textureAtlas = new TextureAtlas(chestAtlas);
        chestSprite = new Sprite(textureAtlas.findRegion(name+"Right",0));
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
        shape.setAsBox(chestSprite.getWidth()/2 / Constants.PPM, chestSprite.getHeight()/2 / Constants.PPM + 1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = b.createFixture(fixtureDef);
        shape.dispose();
        return b;
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
}