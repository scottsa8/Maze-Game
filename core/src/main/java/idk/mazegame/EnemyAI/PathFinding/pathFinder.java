package idk.mazegame.EnemyAI.PathFinding;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
public class pathFinder 
{
    TileGraph tileGraph;

    float x;
    float y;
  
    float speed = 5f;
    float deltaX = 0;
    float deltaY = 0;
  
    Tile previousTile;
    Queue<Tile> pathQueue = new Queue<>();
  
    public pathFinder(TileGraph tileGraph, Tile start) {
      this.tileGraph = tileGraph;
      this.x = start.x;
      this.y = start.y;
      this.previousTile = start;
    }  
    public void step() {
      x += deltaX;
      y += deltaY;
      checkCollision();
    }
  
    /**
     * Set the goal City, calculate a path, and start moving.
     */
    public void setGoal(Tile goal) {
      GraphPath<Tile> graphPath = tileGraph.findPath(previousTile, goal);
      for (int i = 1; i < graphPath.getCount(); i++) {
        pathQueue.addLast(graphPath.get(i));
      }
      setSpeedToNextCity();
    }
  
    /**
     * Check whether Agent has reached the next City in its path.
     */
    private void checkCollision() {
      if (pathQueue.size > 0) {
        Tile target = pathQueue.first();
        if (Vector2.dst(x, y, target.x, target.y) < 5) {
          reachNextCity();
        }
      }
    }
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 0f, 0f, 1);
        shapeRenderer.circle(x, y, 5);
        shapeRenderer.end();
    
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(x, y, 5);
        shapeRenderer.end();
      }
    /**
     * Agent has collided with the next City in its path.
     */
    private void reachNextCity() {
  
      Tile nextCity = pathQueue.first();
  
      // Set the position to keep the Agent in the middle of the path
      this.x = nextCity.x;
      this.y = nextCity.y;
  
      this.previousTile = nextCity;
      pathQueue.removeFirst();
  
      if (pathQueue.size == 0) {
        reachDestination();
      } else {
        setSpeedToNextCity();
      }
    }
  
    /**
     * Set xSpeed and ySpeed to move towards next City on path.
     */
    private void setSpeedToNextCity() {
      Tile nextCity = pathQueue.first();
      float angle = MathUtils.atan2(nextCity.y - previousTile.y, nextCity.x - previousTile.x);
      deltaX = MathUtils.cos(angle) * speed;
      deltaY = MathUtils.sin(angle) * speed;
    }
  
    /**
     * Agent has reached the goal City.
     */
    private void reachDestination() {
      deltaX = 0;
      deltaY = 0;
  
      // Find a new goal City
      Tile newGoal;
      do {
        newGoal = tileGraph.tiles.random();
      } while (newGoal == previousTile);
  
      setGoal(newGoal);
    }
}
