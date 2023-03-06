package idk.mazegame.EnemyAI.PathFinding;

import com.badlogic.gdx.ai.pfa.Connection;

public class NodeConn implements Connection<Node>
{
    private Node fromNode;
    private Node toNode;
    
    public NodeConn(Node fromNode, Node toNode)
    {
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    @Override
    public float getCost() {
       return 1;
    }

    @Override
    public Node getFromNode() {
        return fromNode;
    }

    @Override
    public Node getToNode() {
        return toNode;
    }
}
