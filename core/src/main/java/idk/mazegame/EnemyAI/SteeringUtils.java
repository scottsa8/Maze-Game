package idk.mazegame.EnemyAI;

import com.badlogic.gdx.math.Vector2;

public class SteeringUtils 
{
    public static float vectorToAngle(Vector2 vector){return (float)Math.atan2(-vector.x,vector.y);}
    public static Vector2 angleVector (Vector2 ouVector, float angle)
    {
        ouVector.x = -(float)Math.sin(angle);
        ouVector.y = (float)Math.cos(angle);
        return ouVector;
    }
}
