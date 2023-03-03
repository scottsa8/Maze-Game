package idk.mazegame.EnemyAI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


public class CreateMapBounds 
{
    private TiledMapTileLayer collisionLayer;
    public CreateMapBounds(TiledMap map, World world)
    {
        collisionLayer = (TiledMapTileLayer) map.getLayers().get(1);
        for(int y=0;y<32;y++)
        {
            for(int x=0;x<32;x++)
            {
                if(collisionLayer.getCell(x, y) != null) {
                    float realX = 298 + (x - y) * (9.5f);
			        float realY = 166 - (x + y) * (4.75f);
                    createBody(world, realX, realY);
                }
            }
        }
    }
       private void createBody(World world,float x, float y)
    {
        Body b;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((x + collisionLayer.getTileWidth()/2), (y- collisionLayer.getTileHeight()/2));
        
        b = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(collisionLayer.getTileWidth()/2 / Constants.PPM, collisionLayer.getTileWidth()/2 / Constants.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = b.createFixture(fixtureDef);
        shape.dispose();
        
    }
}
