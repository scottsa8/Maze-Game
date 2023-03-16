package idk.mazegame.EnemyAI.PathFinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

public class Graph implements IndexedGraph<Node>{
    private Array<Node> nodes;

    public Graph(Array<Node> nodes)
    {
        this.nodes = nodes;
    }

    @Override
    public Array<Connection<Node>> getConnections(Node fromNode) {
        return fromNode.connections;
    }

    @Override
    public int getIndex(Node node) {
      return node.index;
    }

    @Override
    public int getNodeCount() {
         return nodes.size;
    }
    public Array<Node> getNodes()
    {
        return nodes;
    }
}
