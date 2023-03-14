package idk.mazegame.EnemyAI.PathFinding;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathFindingSystem
{
    public static Graph graph;
    public static DistanceHeuristic dh;
    private TiledMapTileLayer floorLayer;
    private TiledMapTileLayer blockLayer;
    private static Array<Node> nodes = new Array<>();

    public PathFindingSystem()
    {
        dh = new DistanceHeuristic();
    }
    public void generateGraph(TiledMap map)
    {
        int count =0;
        floorLayer = (TiledMapTileLayer) map.getLayers().get(0);
        blockLayer = (TiledMapTileLayer) map.getLayers().get(1);
        for(int row=0;row<=32;row++)
        {
            for(int col=0;col<=32;col++)
            {
                float x = -((32-row - col) * 7.5f - 159f); //+ 38f
                float y = ((col + 32-row) * 3.75f + 13f); //- 41f
                if(floorLayer.getCell(row, col) != null)
                    if(blockLayer.getCell(row-1, col+1) == null)
                    {
                        nodes.add(new Node(new Vector2(x,y),new Vector2(row,col),count));
                        count++;
                    }
            }
        }

        for(Node node:nodes)
        {
            for(int i=0;i<nodes.size;i++)
            {
                if((node.tilePos.x + 1 == nodes.get(i).tilePos.x && node.tilePos.y == nodes.get(i).tilePos.y)
                || (node.tilePos.x - 1 == nodes.get(i).tilePos.x && node.tilePos.y == nodes.get(i).tilePos.y)
                || (node.tilePos.x == nodes.get(i).tilePos.x && node.tilePos.y +1 == nodes.get(i).tilePos.y)
                || (node.tilePos.x == nodes.get(i).tilePos.x && node.tilePos.y -1 == nodes.get(i).tilePos.y)
                || (node.tilePos.x + 1 == nodes.get(i).tilePos.x && node.tilePos.y + 1 == nodes.get(i).tilePos.y) //connections for diagonal movements
                || (node.tilePos.x + 1 == nodes.get(i).tilePos.x && node.tilePos.y - 1 == nodes.get(i).tilePos.y)
                || (node.tilePos.x - 1 == nodes.get(i).tilePos.x && node.tilePos.y + 1 == nodes.get(i).tilePos.y)
                || (node.tilePos.x - 1 == nodes.get(i).tilePos.x && node.tilePos.y - 1 == nodes.get(i).tilePos.y))
                {
                    node.connections.add(new NodeConn(node,nodes.get(i)));
                }
            }
        }
        
        graph = new Graph(nodes);

    }
    static public Node VectorToNode(Vector2 position)
    {
        for(int i=0;i<nodes.size;i++)
        {
            if(nodes.get(i).tilePos.equals(position))
            {
                return nodes.get(i);
            }
        }
        return null;
    }
}







