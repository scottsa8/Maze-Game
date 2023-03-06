package idk.mazegame.EnemyAI.PathFinding;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;

public class graphPath{
    public static GraphPath<Node> getPath(Vector2 entityPos, Vector2 targetPos)
    {
        Node start = null;
        Node end = null;
        Graph graph = PathFindingSystem.graph;
        DistanceHeuristic dh = PathFindingSystem.dh;
        for(Node node:graph.getNodes())
        {
            if(Math.abs(node.pos.x - entityPos.x) < 8
            && Math.abs(node.pos.y - entityPos.y) < 8)
            {
                start = node;
            }
            if(Math.abs(node.pos.x - targetPos.x) < 8
            && Math.abs(node.pos.y - targetPos.y) < 8)
            {
                start = node;
            }
        }
       // System.out.println(entityPos + " , " + start.pos + " || " + targetPos + " , " + end.pos);
    
        GraphPath<Node> path =  new DefaultGraphPath<Node>();
        new IndexedAStarPathFinder<>(graph).searchNodePath(start, end, dh, path);
        return path;
    }
}