package idk.mazegame.EnemyAI.PathFinding;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;

public class graphPath{
    public static GraphPath<Node> getPath(Node entityPos, Node targetPos)
    {
        Node start = entityPos;
        Node end = targetPos;
        Graph graph = PathFindingSystem.graph;
        DistanceHeuristic dh = PathFindingSystem.dh;
    
        GraphPath<Node> path =  new DefaultGraphPath<Node>();
        new IndexedAStarPathFinder<>(graph).searchNodePath(start, end, dh, path);
        return path;
    }
}
