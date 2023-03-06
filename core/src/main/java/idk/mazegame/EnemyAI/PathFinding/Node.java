package idk.mazegame.EnemyAI.PathFinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Node{
    public Vector2 pos;
    public Vector2 tilePos;
    public int index;
    public Array<Connection<Node>> connections = new Array<>();
    public Polygon p;
    
    public Node(Vector2 pos, Vector2 tilePos, int index)
    {
        this.pos = pos;
        this.tilePos = tilePos;
        this.index = index;
        p = new Polygon(new float[]{
            pos.x + 4, pos.y,
            pos.x, pos.y + 2,
            pos.x +4, pos.y +4,
            pos.x+8,pos.y+2
        });
    }
}
