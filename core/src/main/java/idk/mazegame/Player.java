package idk.mazegame;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture.AsynchronousCompletionTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;

import idk.mazegame.EnemyAI.Constants;

public class Player {
    private TextureAtlas textureAtlas;
    private Sprite playerSprite;
    private Vector3 coordinates, tmpCoords = new Vector3(0,0,0);

    private int currentFrame = 0,  frameCounter = 0;
    private int timer = 0;
    private int lastKeyedDirection = 0;
    private int secondlastKeyedDirection = 0;
    private int up = Input.Keys.UP, down = Input.Keys.DOWN, left = Input.Keys.LEFT, right = Input.Keys.RIGHT;
    private int useSlot1 = Input.Keys.Q, useSlot2 = Input.Keys.E;
    private boolean inputIsLocked = false, isMoving = false, nextStep = false; // to use for certain parts where player input is disabled: tile-based movement, cutscenes, stamina, debuff
    private float moveAmountX = 0f, moveAmountY = 0f, targetX = 0, targetY = 0;
    private final int MAX_FRAMES = 4;
    private final float PLAYER_SPEED = 9.5f;
    private final int FRAME_SPEED = 3;
    private final float DIAG_MOD = 1f; //0.707 for normalization
    private final int MAX_INPUT_DELAY = 1;
    private int inputDelay = MAX_INPUT_DELAY;
    private ItemAttributes itemAttrs;
    private Inventory inv;
    private Item[] slots = new Item[3];
    private Body body;
    private Leveling level = new Leveling();
    private World world;
    private Body attackCircle;

    public Player(FileHandle atlasfile, ItemAttributes gameAttrs) {
        textureAtlas = new TextureAtlas(atlasfile);
        playerSprite = new Sprite(textureAtlas.findRegion("playerDown",0));
        playerSprite.setPosition(Gdx.graphics.getWidth()/2 - playerSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - playerSprite.getHeight()/2);
        coordinates = new Vector3(0,0, 0);
        //playerSprite.setScale(4f);

        //Attributes only generated once.
        itemAttrs = gameAttrs;
        inv = new Inventory(itemAttrs);

        slots[1] = new Item(itemAttrs);
        slots[2] = new Item(itemAttrs);
       
    }

    public Body createBody(World world) {
        this.world = world;
        Body b;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(getPlayerSprite().getX() + 7.5f, getPlayerSprite().getY() + 4f);
        b = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getPlayerSprite().getWidth()/2 / Constants.PPM, getPlayerSprite().getHeight()/2 / Constants.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = b.createFixture(fixtureDef);
        shape.dispose();
        body = b;
        return body;
    }
    public void increaseXP(int amount){
        level.increaseXP(amount);
    }

    public int displayXP(){
        int xp = level.getXP();
        return xp;
    }

    public void walk(int direction) {
//        Gdx.app.log("player X", String.valueOf(getPlayerSprite().getX()));
//        Gdx.app.log("player Y", String.valueOf(getPlayerSprite().getY()));
        secondlastKeyedDirection = lastKeyedDirection;

        if (timer > FRAME_SPEED) {
            //currentFrame++;
            timer = 0;
        }

        if (currentFrame >= MAX_FRAMES)
            //currentFrame = 0;
            ;

        if (direction == 9) {
            lastKeyedDirection = 9;

//            playerSprite.setRegion(textureAtlas.findRegion("playerRUp", currentFrame));
//            playerSprite.translate(+(PLAYER_SPEED*DIAG_MOD), +(PLAYER_SPEED*DIAG_MOD)/2);
//            coordinates.y += 1f;

            moveAmountX = PLAYER_SPEED*DIAG_MOD;
            moveAmountY = (PLAYER_SPEED*DIAG_MOD)/2;
            tmpCoords.set(0,1,0);

            isMoving = true;

            return;
        }

        if (direction == 7) {
            lastKeyedDirection = 7;

//            playerSprite.setRegion(textureAtlas.findRegion("playerLUp", currentFrame));
//            playerSprite.translate(-(PLAYER_SPEED*DIAG_MOD), +(PLAYER_SPEED*DIAG_MOD)/2);
//            coordinates.x -= 1f;

            moveAmountX = -PLAYER_SPEED*DIAG_MOD;
            moveAmountY = (PLAYER_SPEED*DIAG_MOD)/2;
            tmpCoords.set(-1,0,0);

            isMoving = true;

            return;
        }

        if (direction == 3) {
            lastKeyedDirection = 3;

//            playerSprite.setRegion(textureAtlas.findRegion("playerRDown", currentFrame));
//            playerSprite.translate(+(PLAYER_SPEED*DIAG_MOD), -(PLAYER_SPEED*DIAG_MOD)/2);
//            coordinates.x += 1f;

            moveAmountX = PLAYER_SPEED*DIAG_MOD;
            moveAmountY = (-PLAYER_SPEED*DIAG_MOD)/2;
            tmpCoords.set(1,0,0);

            isMoving = true;

            return;
        }

        if (direction == 1) {
            lastKeyedDirection = 1;

//            playerSprite.setRegion(textureAtlas.findRegion("playerLDown", currentFrame));
//            playerSprite.translate(-(PLAYER_SPEED*DIAG_MOD), -(PLAYER_SPEED*DIAG_MOD)/2);
//            coordinates.y -= 1f;

            moveAmountX = -PLAYER_SPEED*DIAG_MOD;
            moveAmountY = (-PLAYER_SPEED*DIAG_MOD)/2;
            tmpCoords.set(0,-1,0);

            isMoving = true;

            return;
        }

        if (direction == 4) {
            lastKeyedDirection = 4;

            if (secondlastKeyedDirection == 4) {
//                playerSprite.setRegion(textureAtlas.findRegion("playerLeft", currentFrame));
//                playerSprite.translateX(-PLAYER_SPEED*2);
//                coordinates.x -= 1f;
//                coordinates.y -= 1f;

                moveAmountX = -PLAYER_SPEED*2;
                moveAmountY = 0;
                tmpCoords.set(-1,-1,0);

                isMoving = true;
            }

            return;
        }

        if (direction == 6) {
            lastKeyedDirection = 6;

            if (secondlastKeyedDirection == 6) {
//                playerSprite.setRegion(textureAtlas.findRegion("playerRight", currentFrame));
//                playerSprite.translateX(+PLAYER_SPEED*2);
//                coordinates.x += 1f;
//                coordinates.y += 1f;

                moveAmountX = PLAYER_SPEED*2;
                moveAmountY = 0;
                tmpCoords.set(1,1,0);

                isMoving = true;
            }

            return;
        }

        if (direction == 2) {
            lastKeyedDirection = 2;

            if (secondlastKeyedDirection == 2) {
//                playerSprite.setRegion(textureAtlas.findRegion("playerDown", currentFrame));
//                playerSprite.translateY(-PLAYER_SPEED);
//                coordinates.x += 1f;
//                coordinates.y -= 1f;

                moveAmountX = 0;
                moveAmountY = -PLAYER_SPEED;
                tmpCoords.set(+1,-1,0);

                isMoving = true;
            }

            return;
        }

        if (direction == 8) {
            lastKeyedDirection = 8;

            if (secondlastKeyedDirection == 8) {
//                playerSprite.setRegion(textureAtlas.findRegion("playerUp", currentFrame));
//                playerSprite.translateY(+PLAYER_SPEED);
//                coordinates.x -= 1f;
//                coordinates.y += 1f;

                moveAmountX = 0;
                moveAmountY = PLAYER_SPEED;
                tmpCoords.set(-1,1,0);

                isMoving = true;
            }
        }
    }

    public void idle() {

        if (lastKeyedDirection == 8 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 7))
            playerSprite.setRegion(textureAtlas.findRegion("playerUp", 0));

        if (lastKeyedDirection == 2 && !(secondlastKeyedDirection == 3 || secondlastKeyedDirection == 1))
            playerSprite.setRegion(textureAtlas.findRegion("playerDown", 0));

        if (lastKeyedDirection == 4 && !(secondlastKeyedDirection == 7 || secondlastKeyedDirection == 1))
            playerSprite.setRegion(textureAtlas.findRegion("playerLeft", 0));

        if (lastKeyedDirection == 6 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 3))
            playerSprite.setRegion(textureAtlas.findRegion("playerRight", 0));

        if (lastKeyedDirection == 9)
            playerSprite.setRegion(textureAtlas.findRegion("playerRUp", 0));

        if (lastKeyedDirection == 7)
            playerSprite.setRegion(textureAtlas.findRegion("playerLUp", 0));

        if (lastKeyedDirection == 3)
            playerSprite.setRegion(textureAtlas.findRegion("playerRDown", 0));

        if (lastKeyedDirection == 1)
            playerSprite.setRegion(textureAtlas.findRegion("playerLDown", 0));

        //currentFrame = 1;
        timer = 0;

    }

    public void slotsCheck() { //Checks if any of the slots are empty and if any item in the inventory can fill its gap
      //  System.out.println(slots);

        if (slots[1].type < 0) {
            slot1Remove();
        }

        if (slots[2].type < 0) {
            slot2Remove();
        }

        if (slots[1].name.equals("Fist")) {
            //1. a. If so, check inventory for item to fill slot
            Item foundItem = inv.getFirstItem();
            if (foundItem != null) {
                //2. Add item found to slot 1
                slots[1] = foundItem;
                //3. Remove item found from inventory
                inv.inventoryRemove(foundItem);
            }
        }

        if (slots[2].name.equals("Fist")) {
            //1. a. If so, check inventory for item to fill slot
            Item foundItem = inv.getFirstItem();
            if (foundItem != null) {
                //2. Add item found to slot 2
                slots[2] = foundItem;
                //3. Remove item found from inventory
                inv.inventoryRemove(foundItem);
            }
        }
    }


    public void slot1Add(Item itemToAdd) { //Adds the specified item to the first slot of the player
        //1. Add item to slot 1
        slots[1] = itemToAdd;
        //2. Remove item from inventory
        inv.inventoryRemove(itemToAdd);
    }

    public void slot2Add(Item itemToAdd) { //Adds the specified item to the second slot of the player
        //1. Add item to slot 2
        slots[2] = itemToAdd;
        //2. Remove item from inventory
        inv.inventoryRemove(itemToAdd);
    }

    public void slot1Remove() { //Removes the item from the first slot of the player
        //Remove item from slot 1
        slots[1] = new Item(itemAttrs);

        //Try to fill slot
        slotsCheck();
    }

    public void slot2Remove() { //Removes the item from the second slot of the player
        //Remove item from slot 2
        slots[2] = new Item(itemAttrs);

        //Try to fill slot
        slotsCheck();
    }

    public void update(TiledMapTileLayer floorLayer, TiledMapTileLayer entityLayer) {
        getBody().setTransform(getBody().getPosition().set(getPlayerSprite().getX() + 7.5f, getPlayerSprite().getY() + 4f),0);
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT)) { //TESTING - Adds a sword to the inventory
            inv.inventoryAdd(new Item(itemAttrs, 0, 1), 0);
            slotsCheck();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_RIGHT)) { //TESTING - Prints out the inventory contents
            inv.printInventory();
        }

        //item usage
        if (Gdx.input.isKeyPressed(useSlot1)) {
            slots[1].useItem();
           if(attackCircle == null)
            {
                meleeAttack();   
            }

            slotsCheck();
        }
        if (Gdx.input.isKeyJustPressed(useSlot2)) {
            slots[2].useItem();
            System.out.println("Item 2 used");
            slotsCheck();
        }


        if(inputIsLocked == true) {
            return;
        }

        if(isMoving == true) {
            //player update position, decrement from move amount, once move amount == finished - set isMoving to false

//            if(targettile!=null){
//                yourobject.position.x += 1*delta;
//                if(yourobject.position.x>=targettile.position.x){
//                    yourobject.position.x = targettile.position.x;
//                    targettile = null;
//                }
//            }
//
            if (timer == 2) {
                //currentFrame++;
                timer = 0;
            }

            if (timer == 0) {

                if (lastKeyedDirection == 6 || lastKeyedDirection == 4) playerSprite.translateX(-0.125f*moveAmountX);
                playerSprite.translateX(0.25f*moveAmountX);
                playerSprite.translateY(0.25f*moveAmountY);

                if (targetX != moveAmountX)
                    if (lastKeyedDirection == 6 || lastKeyedDirection == 4) targetX -= 0.125f*moveAmountX;
                    targetX += 0.25f*moveAmountX;
                if (targetY != moveAmountY)
                    targetY += 0.25f*moveAmountY;

                //animation
                if(frameCounter == 5) {
                    currentFrame++;
                    if (lastKeyedDirection == 8 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 7))
                        playerSprite.setRegion(textureAtlas.findRegion("playerUp", currentFrame));

                    if (lastKeyedDirection == 2 && !(secondlastKeyedDirection == 3 || secondlastKeyedDirection == 1))
                        playerSprite.setRegion(textureAtlas.findRegion("playerDown", currentFrame));

                    if (lastKeyedDirection == 4 && !(secondlastKeyedDirection == 7 || secondlastKeyedDirection == 1))
                        playerSprite.setRegion(textureAtlas.findRegion("playerLeft", currentFrame));

                    if (lastKeyedDirection == 6 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 3))
                        playerSprite.setRegion(textureAtlas.findRegion("playerRight", currentFrame));

                    if (lastKeyedDirection == 9)
                        //if (lastKeyedDirection == 9 || secondlastKeyedDirection == 9)
                        playerSprite.setRegion(textureAtlas.findRegion("playerRUp", currentFrame));

                    if (lastKeyedDirection == 7)
                        //if (lastKeyedDirection == 7 || secondlastKeyedDirection == 7)
                        playerSprite.setRegion(textureAtlas.findRegion("playerLUp", currentFrame));

                    if (lastKeyedDirection == 3)
                        //if (lastKeyedDirection == 3 || secondlastKeyedDirection == 3)
                        playerSprite.setRegion(textureAtlas.findRegion("playerRDown", currentFrame));

                    if (lastKeyedDirection == 1)
                        //if (lastKeyedDirection == 1 || secondlastKeyedDirection == 1)
                        playerSprite.setRegion(textureAtlas.findRegion("playerLDown", currentFrame));
                    frameCounter = 0;
                }

                frameCounter++;

            }

            timer++;

            //reset animation loop
            if (currentFrame == 3) {
                currentFrame = 0;
            }

            if (targetY == moveAmountY && targetX == moveAmountX) {
                isMoving = false;
                coordinates.add(tmpCoords);
                tmpCoords.set(0,0,0);

//                if (nextStep == false) {
//                    currentFrame = 3;
//                    nextStep = true;
//                }
//                else if (nextStep == true) {
//                    currentFrame = 1;
//                    nextStep = false;
//                }

                targetX = 0;
                targetY = 0;
                moveAmountX = 0;
                moveAmountY = 0;
                frameCounter = 5;

            }
            return;
        }

        // collision checking
        if (Gdx.input.isKeyPressed(up) && Gdx.input.isKeyPressed(right) && !(Gdx.input.isKeyPressed(down) || Gdx.input.isKeyPressed(left))) {
            if (entityLayer.getCell( (int)(coordinates.x - 1f), (int)(coordinates.y + 2f)) == null)
                if (floorLayer.getCell( (int)(coordinates.x), (int)(coordinates.y + 1f)) != null)
                    walk(9);
                else {
                    lastKeyedDirection = 9;
                    secondlastKeyedDirection = lastKeyedDirection;
                    idle();
                }
            else {
                lastKeyedDirection = 9;
                secondlastKeyedDirection = lastKeyedDirection;
                idle();
            }


            return;
        }
        if (Gdx.input.isKeyPressed(up) && Gdx.input.isKeyPressed(left) && !(Gdx.input.isKeyPressed(down) || Gdx.input.isKeyPressed(right))) {
            if (entityLayer.getCell( (int)(coordinates.x - 2f), (int)(coordinates.y + 1f)) == null)
                if (floorLayer.getCell( (int)(coordinates.x - 1f), (int)(coordinates.y)) != null)
                    walk(7);
                else {
                    lastKeyedDirection = 7;
                    secondlastKeyedDirection = lastKeyedDirection;
                    idle();
                }
            else {
                lastKeyedDirection = 7;
                secondlastKeyedDirection = lastKeyedDirection;
                idle();
            }

            return;
        }
        if (Gdx.input.isKeyPressed(down) && Gdx.input.isKeyPressed(right) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(left))) {
            if (entityLayer.getCell( (int)(coordinates.x), (int)(coordinates.y + 1f)) == null)
                if (floorLayer.getCell( (int)(coordinates.x + 1f), (int)(coordinates.y)) != null)
                    walk(3);
                else {
                    lastKeyedDirection = 3;
                    secondlastKeyedDirection = lastKeyedDirection;
                    idle();
                }
            else {
                lastKeyedDirection = 3;
                secondlastKeyedDirection = lastKeyedDirection;
                idle();
            }

            return;
        }
        if (Gdx.input.isKeyPressed(down) && Gdx.input.isKeyPressed(left) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(right))) {
            if (entityLayer.getCell( (int)(coordinates.x - 1f), (int)(coordinates.y)) == null)
                if (floorLayer.getCell( (int)(coordinates.x), (int)(coordinates.y - 1f)) != null)
                    walk(1);
                else {
                    lastKeyedDirection = 1;
                    secondlastKeyedDirection = lastKeyedDirection;
                    idle();
                }
            else {
                lastKeyedDirection = 1;
                secondlastKeyedDirection = lastKeyedDirection;
                idle();
            }

            return;
        }
        if (Gdx.input.isKeyPressed(left) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(right) || Gdx.input.isKeyPressed(down))) {
            if (entityLayer.getCell( (int)(coordinates.x - 2f), (int)(coordinates.y)) == null)
                if (floorLayer.getCell( (int)(coordinates.x - 1f), (int)(coordinates.y - 1f)) != null)
                    walk(4);
                else {
                    lastKeyedDirection = 4;
                    secondlastKeyedDirection = lastKeyedDirection;
                    idle();
                }
            else {
                lastKeyedDirection = 4;
                secondlastKeyedDirection = lastKeyedDirection;
                idle();
            }

            return;
        }
        if (Gdx.input.isKeyPressed(right) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(down) || Gdx.input.isKeyPressed(left))) {
            if (entityLayer.getCell( (int)(coordinates.x), (int)(coordinates.y + 2f)) == null)
                if (floorLayer.getCell( (int)(coordinates.x + 1f), (int)(coordinates.y + 1f)) != null)
                    walk(6);
                else {
                    lastKeyedDirection = 6;
                    secondlastKeyedDirection = lastKeyedDirection;
                    idle();
                }
            else {
                lastKeyedDirection = 6;
                secondlastKeyedDirection = lastKeyedDirection;
                idle();
            }

            return;
        }
        if (Gdx.input.isKeyPressed(down) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(right) || Gdx.input.isKeyPressed(left))) {
            if (entityLayer.getCell( (int)(coordinates.x), (int)(coordinates.y)) == null)
                if (floorLayer.getCell( (int)(coordinates.x + 1f), (int)(coordinates.y - 1f)) != null)
                    walk(2);
                else {
                    lastKeyedDirection = 2;
                    secondlastKeyedDirection = lastKeyedDirection;
                    idle();
                }
            else {
                lastKeyedDirection = 2;
                secondlastKeyedDirection = lastKeyedDirection;
                idle();
            }

            return;
        }
        if (Gdx.input.isKeyPressed(up) && !(Gdx.input.isKeyPressed(down) || Gdx.input.isKeyPressed(right) || Gdx.input.isKeyPressed(left))) {
            if (entityLayer.getCell( (int)(coordinates.x - 2f), (int)(coordinates.y + 2f)) == null)
                if (floorLayer.getCell( (int)(coordinates.x - 1f), (int)(coordinates.y + 1f)) != null)
                    walk(8);
                else {
                    lastKeyedDirection = 8;
                    secondlastKeyedDirection = lastKeyedDirection;
                    idle();
                }
            else {
                lastKeyedDirection = 8;
                secondlastKeyedDirection = lastKeyedDirection;
                idle();
            }

            return;
        }
        //if no input pressed for a few ticks -> idle(), reset frame counter
        idle();
    }

    public void dispose() {
        textureAtlas.dispose();
        playerSprite.getTexture().dispose();
        
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public void setPlayerSprite(Sprite playerSprite) {
        this.playerSprite = playerSprite;
    }

    public void setUseSlot1(int use) { useSlot1 = use; }

    public void setUseSlot2(int use) { useSlot2 = use; }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public Vector3 getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Vector3 coordinates) {
        this.coordinates = coordinates;
    }

    public float getMoveAmountX() {
        return moveAmountX;
    }

    public float getMoveAmountY() {
        return moveAmountY;
    }

    public Body getBody() {
        return body;
    }
    public void meleeAttack()
    {
        Vector2 pos = new Vector2();
        if (lastKeyedDirection == 8 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 7))
        pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM, getPlayerSprite().getHeight()/2 / Constants.PPM +6);
        if (lastKeyedDirection == 2 && !(secondlastKeyedDirection == 3 || secondlastKeyedDirection == 1))
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM, getPlayerSprite().getHeight()/2 / Constants.PPM -6);
        if (lastKeyedDirection == 4 && !(secondlastKeyedDirection == 7 || secondlastKeyedDirection == 1))
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM -6, getPlayerSprite().getHeight()/2 / Constants.PPM);
        if (lastKeyedDirection == 6 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 3))
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM + 6, getPlayerSprite().getHeight()/2 / Constants.PPM);
        if (lastKeyedDirection == 9)
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM+6, getPlayerSprite().getHeight()/2 / Constants.PPM +6);
        if (lastKeyedDirection == 7)
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM-6, getPlayerSprite().getHeight()/2 / Constants.PPM +6);
        if (lastKeyedDirection == 3)
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM +6, getPlayerSprite().getHeight()/2 / Constants.PPM -6);
        if (lastKeyedDirection == 1)
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM -6, getPlayerSprite().getHeight()/2 / Constants.PPM -6);
               
        BodyDef bodyDef = new BodyDef();
        bodyDef.bullet = true;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(getPlayerSprite().getX() + 7.5f, getPlayerSprite().getY() + 4f);
       
        attackCircle = world.createBody(bodyDef);
        attackCircle.setUserData("attack");

        CircleShape shape = new CircleShape();
        shape.setPosition(pos);
        shape.setRadius(2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = attackCircle.createFixture(fixtureDef);
        shape.dispose();

        Timer timer=new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        try{
                            world.destroyBody(attackCircle);
                            attackCircle=null;
                        }
                      catch(Exception e){};
                    }
                },0.01f);  
            
    }
}
