package idk.mazegame.EnemyAI.PathFinding;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class OneBlock implements Connection<Tile> {
    Tile fromTile;
    Tile toTile;
    float cost;
  
    public OneBlock(Tile fromCity, Tile toCity){
      this.fromTile = fromCity;
      this.toTile = toCity;
      cost = Vector2.dst(fromCity.x, fromCity.y, toCity.x, toCity.y);
    }
  
    public void render(ShapeRenderer shapeRenderer){
      shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
      shapeRenderer.setColor(0, 0, 0, 1);
      shapeRenderer.rectLine(fromTile.x, fromTile.y, toTile.x, toTile.y, 4);
      shapeRenderer.end();
    }
  
    @Override
    public float getCost() {
      return cost;
    }
  
    @Override
    public Tile getFromNode() {
      return fromTile;
    }
  
    @Override
    public Tile getToNode() {
      return toTile;
    }
}
