package idk.mazegame;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class ShapeMaker 
{
    public static Body createCircle(Vector2 pos,Vector2 bodyPos, boolean bullet, World world)
    {
        
        BodyDef circle = new BodyDef();
        circle.bullet = bullet;
        circle.type = BodyDef.BodyType.StaticBody;
        circle.position.set(pos);
        Body circleBody = world.createBody(circle);

        CircleShape shape = new CircleShape();
        shape.setPosition(bodyPos);
        shape.setRadius(2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = circleBody.createFixture(fixtureDef);
        shape.dispose();
        
        return circleBody;

    }    
    public static Body createSquare(Vector2 pos,Vector2 bodyPos, boolean bullet, World world)
    {
        BodyDef square = new BodyDef();
        square.bullet = bullet;
        square.type = BodyDef.BodyType.StaticBody;
        square.position.set(pos);
        Body squareBody = world.createBody(square);

        PolygonShape shape= new PolygonShape();
        shape.setAsBox(3,3,bodyPos,0 );
        
        shape.getVertex(1, bodyPos);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = squareBody.createFixture(fixtureDef);
        shape.dispose();
        
        return squareBody;
    }
}
