package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.BooleanArray;

import idk.mazegame.EnemyAI.Constants;
import idk.mazegame.EnemyAI.PathFinding.Node;
import idk.mazegame.EnemyAI.PathFinding.PathFindingSystem;
import idk.mazegame.EnemyAI.PathFinding.graphPath;
import idk.mazegame.EnemyAI.Steering;

public class Enemy {
    private TextureAtlas textureAtlas;
    private Sprite enemySprite;
    private Vector2 coords;
    private Vector2 pathCoords = new Vector2();
    private Body body;
    private String name="";
    private Player target;
    private int currentFrame = 0;
    private int timer = 0;
    private final int MAX_FRAMES = 3;
    private final int FRAME_SPEED = 3;
    private boolean dead = false, isMoving = false;
    private int Health =100;
    private GraphPath<Node> path;
    private int currentNode=0;
    private final float DIAG_MOD = 1f; //0.707 for normalization
    private final float ENEMY_SPEED = 9.5f;
    private float moveAmountX = 0f, moveAmountY = 0f;
    private Vector2 curPos = new Vector2();
    private Vector2 nextPos = new Vector2();
    private String direction = "Right";

    public Enemy(World world,float x, float y, int type, int index) {
        String enemyAtlas = getAtlas(type);
        textureAtlas = new TextureAtlas(enemyAtlas);
        enemySprite = new Sprite(textureAtlas.findRegion(name+direction,0));
        this.body = createBody(world,x,y);
        this.body.setUserData("enemy"+","+index);        
    }
    public GraphPath<Node> getPath()
    {
        return path;
    }
     public void setPath(Player p,Node currentPos)
    {
        Node playerNode;
        currentNode=0;
        playerNode =PathFindingSystem.VectorToNode(p.getVect2Coordinates());
		path = graphPath.getPath(currentPos, playerNode);
    }
    public void resetPath()
    {
        path.clear();
        currentNode=0;
        pathCoords= new Vector2();

    }
    public void updatePath(Player p,Vector2 currentPos)
    {
        if (isMoving) return;

        Node playerNode;
        Node node;
        playerNode = PathFindingSystem.VectorToNode(p.getVect2Coordinates());
        node = PathFindingSystem.VectorToNode(currentPos);
		path = graphPath.getPath(node, playerNode);
	    for(int c=0;c<path.getCount();c++)
			{
				System.out.println(path.get(c).tilePos);
			}
        currentNode = 0;
    }
    public Vector2 getCoordinates()
    {
        return coords;
    }
    public void setCoords(Vector2 coords) {
        this.coords = coords;
    }
    public void updateUserData(int index)
    {
        this.body.setUserData("enemy"+","+index);        
    }
    private Body createBody(World world,float x, float y)
    {
        Body b;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + enemySprite.getWidth()/2), (y- enemySprite.getHeight()/2));
        b = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(enemySprite.getWidth()/2 / Constants.PPM, enemySprite.getHeight()/2 / Constants.PPM + 1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = b.createFixture(fixtureDef);
        shape.dispose();
        return b;
    }
    public Body getBody()
    {
        return body;
    }
    public void updateBody()
    {
        if (isMoving) {
            //if (lastKeyedDirection == 6 || lastKeyedDirection == 4) playerSprite.translateX(-0.125f*moveAmountX);
            if (timer == 0) {
                body.setTransform(body.getPosition().set(body.getPosition().x + (0.0675f * moveAmountX), body.getPosition().y + (0.0675f * moveAmountY)), 0);
                currentFrame++;
                timer = 1;

                if (currentFrame % 4 == 0) {
                    enemySprite.setRegion(textureAtlas.findRegion(name + direction, currentFrame/4 - 1));
                }
            }

            if (currentFrame == 16) {
                isMoving = false;
                currentNode++;
                this.setCoords(path.get(currentNode).tilePos);
                path.get(currentNode-1).setOccupied(false);
                path.get(currentNode).setOccupied(true);
                curPos = null;
                nextPos = null;
                currentFrame = 0;
                return;
            }

            timer--;
            return;

//                if (targetX != moveAmountX)
//                    if (lastKeyedDirection == 6 || lastKeyedDirection == 4) targetX -= 0.125f*moveAmountX;
//                    targetX += 0.25f*moveAmountX;
//                if (targetY != moveAmountY)
//                    targetY += 0.25f*moveAmountY;
//
//                //animation
//                if(frameCounter == 5) {
//                    currentFrame++;
//                    if (lastKeyedDirection == 8 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 7))
//                        playerSprite.setRegion(textureAtlas.findRegion("playerUp", currentFrame));
//
//                    if (lastKeyedDirection == 2 && !(secondlastKeyedDirection == 3 || secondlastKeyedDirection == 1))
//                        playerSprite.setRegion(textureAtlas.findRegion("playerDown", currentFrame));
//
//                    if (lastKeyedDirection == 4 && !(secondlastKeyedDirection == 7 || secondlastKeyedDirection == 1))
//                        playerSprite.setRegion(textureAtlas.findRegion("playerLeft", currentFrame));
//
//                    if (lastKeyedDirection == 6 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 3))
//                        playerSprite.setRegion(textureAtlas.findRegion("playerRight", currentFrame));
//
//                    if (lastKeyedDirection == 9)
//                        //if (lastKeyedDirection == 9 || secondlastKeyedDirection == 9)
//                        playerSprite.setRegion(textureAtlas.findRegion("playerRUp", currentFrame));
//
//                    if (lastKeyedDirection == 7)
//                        //if (lastKeyedDirection == 7 || secondlastKeyedDirection == 7)
//                        playerSprite.setRegion(textureAtlas.findRegion("playerLUp", currentFrame));
//
//                    if (lastKeyedDirection == 3)
//                        //if (lastKeyedDirection == 3 || secondlastKeyedDirection == 3)
//                        playerSprite.setRegion(textureAtlas.findRegion("playerRDown", currentFrame));
//
//                    if (lastKeyedDirection == 1)
//                        //if (lastKeyedDirection == 1 || secondlastKeyedDirection == 1)
//                        playerSprite.setRegion(textureAtlas.findRegion("playerLDown", currentFrame));
//                    frameCounter = 0;
//                }
//
//                frameCounter++;
//
//            }
//
//            timer++;
//
//            //reset animation loop
//            if (currentFrame == 3) {
//                currentFrame = 0;
//            }
//
//            if (targetY == moveAmountY && targetX == moveAmountX) {
//                isMoving = false;
//
//                targetX = 0;
//                targetY = 0;
//                moveAmountX = 0;
//                moveAmountY = 0;
//                frameCounter = 5;
//
//            }
//            return;
        }

        try{
            curPos = path.get(currentNode).tilePos;
        }
        catch(Exception e)
        {
            System.out.println("first index out of bounds: " + curPos);
        }
        if(path.getCount() == currentNode+2)
        {
            //attack player (target)
            return;
        }
        else {
            try {
                nextPos = path.get(currentNode + 1).tilePos; //check if at the end
            } catch (Exception e) {
                System.out.println("second index out of bounds: " + nextPos);
                return;
            }
        }
        if (path.get(currentNode+1).isOccupied) return;
        path.get(currentNode+1).setOccupied(true);

        System.out.println("COUNTER:" + currentNode);
        System.out.println("currentNode:" + curPos);
        System.out.println("currentNode+1:" + nextPos);
        try {
            pathCoords.x = nextPos.x - curPos.x;
            pathCoords.y = nextPos.y - curPos.y;
        } catch (Exception e) {
            System.out.println("third index out of bounds: " + pathCoords);
            return;
        }

        System.out.println("nextPos:" + pathCoords);

        if (pathCoords.equals(new Vector2(0,1))) {
            moveAmountX = ENEMY_SPEED*DIAG_MOD;
            moveAmountY = (ENEMY_SPEED*DIAG_MOD)/2;
            direction = "Up";
            isMoving = true;

        }
        if (pathCoords.equals(new Vector2(-1,0))) {
            moveAmountX = -ENEMY_SPEED*DIAG_MOD;
            moveAmountY = (ENEMY_SPEED*DIAG_MOD)/2;
            direction = "Up";
            isMoving = true;
        }
        if (pathCoords.equals(new Vector2(1,0))) {
            moveAmountX = ENEMY_SPEED*DIAG_MOD;
            moveAmountY = (-ENEMY_SPEED*DIAG_MOD)/2;
            direction = "Down";
            isMoving = true;
        }
        if (pathCoords.equals(new Vector2(0,-1))) {
            moveAmountX = -ENEMY_SPEED*DIAG_MOD;
            moveAmountY = (-ENEMY_SPEED*DIAG_MOD)/2;
            direction = "Down";
            isMoving = true;
        }
        if (pathCoords.equals(new Vector2(-1,-1))) {
            moveAmountX = -ENEMY_SPEED*2;
            moveAmountY = 0;
            direction = "Left";
            isMoving = true;
        }
        if (pathCoords.equals(new Vector2(1,1))) {
            moveAmountX = ENEMY_SPEED*2;
            moveAmountY = 0;
            direction = "Right";
            isMoving = true;
            }
        if (pathCoords.equals(new Vector2(1,-1))) {
            moveAmountX = 0;
            moveAmountY = -ENEMY_SPEED;
            direction = "Down";
            isMoving = true;
            }
        if (pathCoords.equals(new Vector2(-1,1))) {
            moveAmountX = 0;
            moveAmountY = ENEMY_SPEED;
            direction = "Up";
            isMoving = true;
        }

//        body.setTransform(body.getPosition().set(body.getPosition().x + moveAmountX,body.getPosition().y + moveAmountY),0);
//        currentNode++;
//        this.setCoords(path.get(currentNode).tilePos);
//        curPos = null;
//        nextPos = null;

//            //player update position, decrement from move amount, once move amount == finished - set isMoving to false
//
////            if(targettile!=null){
////                yourobject.position.x += 1*delta;
////                if(yourobject.position.x>=targettile.position.x){
////                    yourobject.position.x = targettile.position.x;
////                    targettile = null;
////                }
////            }
////
    }
    public void setScale(Float x)
    {
        enemySprite.setScale(x);
    }
    public void render(SpriteBatch b)
    {
        enemySprite.draw(b);
    }
   
    public Sprite getEnemySprite() {
        return enemySprite;
    }

    public void setEnemySprite(Sprite enemySprite) {
        this.enemySprite = enemySprite;
    }
    public Steering addAI(Enemy e)
    {
        Steering AI = new Steering(e.getBody(),5);
        return AI;
    }
    private String getAtlas(int type)
    {
        String atlas ="";
		
		int type2=0;
		
		if(type ==1)
		{
			atlas = "zombieSprites.atlas";
			name="zombie";
		}
		if(type==2)
		{
			atlas = "demon.atlas";
			name="demon";
		}
		if(type ==3)
		{
			//make sure player xp > 10
			//decide to make imp/phantom (random number)
			type2 = (int)Math.floor(Math.random() *(2 - 1 + 1) + 1);
			if(type2==1)
			{
				atlas ="";
				name="";
			}
			else if(type2 == 2)
			{
				atlas ="";
				name="";
			}
		}
		atlas = "enemy/"+atlas;
        return atlas;
    }
    public int getTarget()
    {
        if(target.getPlayerNum() == 1)
        {
            return 1;
        }
        else
        {
            return 2;
        }
    }
    public void setTarget(Player p)
    {
        target = p;
    }
    public void dispose(Enemy e)
    {
     
        
    }
    public void attack(Player p)
    {
        //player take damage.
    }
    public void die(Enemy e)
    {
        textureAtlas.dispose();
        e.enemySprite.getTexture().dispose();
        this.dead=true;
    }
    public void takeDamage(int damage)
    {
        Health = Health-damage;
        System.out.println("Health:"+Health);
        if(Health == 0)
        {
            die(this);
        }
    }
    public int getHealth()
    {
        return Health;
    }
    public boolean isDead()
    {
        return dead;
    }

    public Node getCurrentNode() {
        return path.get(currentNode);
    }

    public Node getNextNode() {
        if (currentNode+1 >= path.getCount()) return path.get(currentNode);
        return path.get(currentNode+1);
    }
}
