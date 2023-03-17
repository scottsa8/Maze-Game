package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
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
    private boolean dead = false,isMoving = false;
    private int health;
    private int damage;
    private int xpValue;
    private Color color;
    private boolean boss;
    public static int xpMulti;
    private GraphPath<Node> path;
    private int currentNode=0;
    private final float DIAG_MOD = 1f; //0.707 for normalization
    private final float ENEMY_SPEED = 9.5f;
    private float moveAmountX = 0f, moveAmountY = 0f;
    private Vector2 curPos = new Vector2();
    private Vector2 nextPos = new Vector2();
    private String direction = "Right";
    private int attackDelay = 10, moveDelay = 20;

    public Enemy(World world,float x, float y, int type,int type2, int index,int bossType,int roomCount) {
        String enemyAtlas = getAtlas(type,type2,bossType,roomCount);
        textureAtlas = new TextureAtlas(enemyAtlas);
        enemySprite = new Sprite(textureAtlas.findRegion(name+direction,0));
        enemySprite.setPosition(Gdx.graphics.getWidth()/2 - enemySprite.getWidth()/2, Gdx.graphics.getHeight()/2 - enemySprite.getHeight()/2);
        boss=false;
        if(type2 ==3)
        {
            enemySprite.setScale(1.2f);
        }
        this.body = createBody(world,x,y,type2);
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
        currentNode = 0;
        attackDelay = 10;
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
    private Body createBody(World world,float x, float y, int type2)
    {
        Body b;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + enemySprite.getWidth()/2), (y- enemySprite.getHeight()/2));
        b = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        if(type2 ==3)
        {
            shape.setAsBox(enemySprite.getWidth()/2 / Constants.PPM + 3, enemySprite.getHeight()/2 / Constants.PPM + 5);
        }
        else
        {
            shape.setAsBox(enemySprite.getWidth()/2 / Constants.PPM, enemySprite.getHeight()/2 / Constants.PPM + 1);
        }
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = b.createFixture(fixtureDef);
        shape.dispose();
        return b;
    }
    public boolean isBoss()
    {
        return boss;
    }
    public Body getBody()
    {
        return body;
    }
    public void updateBody()
    {
        if (isMoving) {
            if (moveDelay == 0) {
                if (timer == 0) {
                    body.setTransform(body.getPosition().set(body.getPosition().x + (0.0675f * moveAmountX), body.getPosition().y + (0.0675f * moveAmountY)), 0);
                    currentFrame++;
                    timer = 1;

                    if (currentFrame % 4 == 0) {
                        enemySprite.setRegion(textureAtlas.findRegion(name + direction, currentFrame / 4 - 1));
                    }
                }
                if (currentFrame == 16) {
                    isMoving = false;
                    currentNode++;
                    this.setCoords(path.get(currentNode).tilePos);
                    path.get(currentNode - 1).setOccupied(false);
                    path.get(currentNode).setOccupied(true);
                    curPos = null;
                    nextPos = null;
                    currentFrame = 0;
                    moveDelay = 20;
                    return;
                }
                timer--;
                return;
            }
            moveDelay--;
            return;
        }
        try{
            curPos = path.get(currentNode).tilePos;
        }
        catch(Exception e)
        {
            return;
        }
        if(path.getCount() == currentNode+2)
        {

            if (attackDelay == 0) {
                attack(target);
                attackDelay = 10;
                return;
            }

            attackDelay--;
            return;
        }
        else {
            try {
                nextPos = path.get(currentNode + 1).tilePos; //check if at the end
            } catch (Exception e) {
                return;
            }
        }
        if (path.get(currentNode+1).isOccupied) return;
        path.get(currentNode+1).setOccupied(true);
        try {
            pathCoords.x = nextPos.x - curPos.x;
            pathCoords.y = nextPos.y - curPos.y;
        } catch (Exception e) {
            return;
        }

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
    private String getAtlas(int type, int type2,int bossType,int roomCount)
    {
      
        if(roomCount<=15){
            xpMulti=0;
        }else{
            xpMulti=roomCount/10;
        }
        String atlas ="";
        if(type ==-1 && type2 ==3)
        {
            if(bossType ==1)
            {
                atlas = "skeletonKing.atlas";
                name="skeleton";
                damage=10;
                health =250;
                xpValue =100;
                color= Color.FIREBRICK;
            }
            else if(bossType ==2)
            {
                atlas = "impPink.atlas";
                name="imp";
                damage=8;
                health =200;
                xpValue =80;
                color= Color.PINK;
            }
            boss=true;
        }
		if(type ==1)
		{
			atlas = "zombieSprites.atlas";
			name="zombie";
            damage=1;
            health =100;
            xpValue =5 *xpMulti;
            color= Color.GREEN;
		}
		else if(type==2)
		{
			atlas = "demon.atlas";
			name="demon";
            damage=3;
            health =100;
            xpValue =10*xpMulti;
            color= Color.RED;
		}
        else if(type ==3)
        {
            atlas = "skeleton.atlas";
			name="skeleton";
            damage=3;
            health =100;
            xpValue =10*xpMulti;
            color= Color.WHITE;
        }      
		else if(type ==4)
		{
            if(type2==1)
            {
                atlas ="ghost.atlas";
                name="ghost";
                damage=4;
                health =120;
                xpValue =15*xpMulti;
                color= Color.BLACK;
            }
            else if(type2 == 2)
            {
                atlas ="impGolden.atlas";
                name="imp";
                health =120;
                damage=3;
                xpValue =50*xpMulti;
                color = Color.GOLD;
            }
        else
        {
            atlas = "zombieSprites.atlas";
            name="zombie";
            damage=1;
            health =100;
            xpValue =5*xpMulti;
            color= Color.GREEN;
        }
		
		}
		atlas = "enemy/"+atlas;
        return atlas;
    }
    public int getXpValue()
    {
        return xpValue;
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
    public void dispose()
    {
        textureAtlas.dispose();
        enemySprite.getTexture().dispose();
    }
    public Color getHpColor()
    {
        if(color == null)
        {
            return Color.WHITE;
        }
        else
        {
            return color;
        }
    }
    public void attack(Player p)
    {
       p.takeDamage(damage);;
    }
    public void die(Enemy e)
    {
        this.dispose();
        this.dead=true;
    }
    public void takeDamage(int damage)
    {
        health = health-damage;
        if(health <= 0)
        {
            die(this);
        }
//        int gridX = (int) path.get(currentNode).tilePos.x;
//        int gridY = (int) (32 - path.get(currentNode).tilePos.y);
//        float realX = 307 + (gridX - gridY) * (9.5f);
//        float realY = 180 - (gridX + gridY) * (4.75f);
//        body.setTransform(realX, realY, 0);
    }
    public int getHealth()
    {
        return health;
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
