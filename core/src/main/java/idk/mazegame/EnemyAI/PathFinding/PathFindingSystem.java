package idk.mazegame.EnemyAI.PathFinding;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathFindingSystem 
{
    public static Graph graph;
    public static DistanceHeuristic dh;
    
    public PathFindingSystem()
    {
        dh = new DistanceHeuristic();
    }
    public void generateGraph()
    {
        Array<Node> nodes = new Array<>();
        int count =0;

        for(int row=32;row>=0;row--)
        {
            for(int col = 32;col>=0;col--)
            {
                float x= (row-col) * Gdx.graphics.getWidth();
                float y= (col + row) * Gdx.graphics.getHeight();
                System.out.println(x);
                nodes.add(new Node(new Vector2(x,y),new Vector2(row,col),count));
                count++;
            }
        }

        for(Node node:nodes)
        {
            for(int i=0;i<nodes.size;i++)
            {
                if((node.tilePos.x + 1 == nodes.get(i).tilePos.x && node.tilePos.y == nodes.get(i).tilePos.y)
                || (node.tilePos.x - 1 == nodes.get(i).tilePos.x && node.tilePos.y == nodes.get(i).tilePos.y)
                || (node.tilePos.x == nodes.get(i).tilePos.x && node.tilePos.y +1 == nodes.get(i).tilePos.y)
                || (node.tilePos.x == nodes.get(i).tilePos.x && node.tilePos.y -1 == nodes.get(i).tilePos.y))
                {
                    node.connections.add(new NodeConn(node,nodes.get(i)));
                }
            }
        }
    
        graph = new Graph(nodes);
      
    }

}







