package idk.mazegame.EnemyAI.PathFinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class DistanceHeuristic implements Heuristic<Node>{

    @Override
    public float estimate(Node node, Node endNode) {
      return node.pos.dst(endNode.pos);
    }
    
}
