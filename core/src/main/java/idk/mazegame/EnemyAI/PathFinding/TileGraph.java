package idk.mazegame.EnemyAI.PathFinding;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
public class TileGraph implements IndexedGraph<Tile>{
    TileHueristic hueristic = new TileHueristic();
    Array<Tile> tiles = new Array<>();
    Array<OneBlock> blockArray = new Array<>();
  
    /** Map of Tiles to Blocks starting in that Tile. */
    ObjectMap<Tile, Array<Connection<Tile>>> map = new ObjectMap<>();
  
    private int lastNodeIndex = 0;
  
    public void addNode(Tile t){
      t.index = lastNodeIndex;
      lastNodeIndex++;
  
      tiles.add(t);
    }
  
    public void connectNodes(Tile fromTile, Tile toTile){
      OneBlock blocks = new OneBlock(fromTile, toTile);
      if(!map.containsKey(fromTile)){
        map.put(fromTile, new Array<Connection<Tile>>());
      }
      map.get(fromTile).add(blocks);
      blockArray.add(blocks);
    }
  
    public GraphPath<Tile> findPath(Tile start, Tile Goal){
      GraphPath<Tile> Path = new DefaultGraphPath<>();
      new IndexedAStarPathFinder<>(this).searchNodePath(start, Goal, hueristic, Path);
      return Path;
    }
  
    @Override
    public int getIndex(Tile node) {
      return node.index;
    }
  
    @Override
    public int getNodeCount() {
      return lastNodeIndex;
    }
  
    @Override
    public Array<Connection<Tile>> getConnections(Tile fromNode) {
      if(map.containsKey(fromNode)){
        return map.get(fromNode);
      }
  
      return new Array<>(0);
    }
}
