package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import idk.mazegame.EnemyAI.Constants;
import idk.mazegame.EnemyAI.PathFinding.PathFindingSystem;

public class Player {
    private int playerNum;
    private TextureAtlas textureAtlas;
    private Sprite playerSprite;
    private Vector3 coordinates, tmpCoords = new Vector3(0, 0, 0);

    private int currentFrame = 0, frameCounter = 0;
    private int timer = 0;
    private int lastKeyedDirection = 0;
    private int secondlastKeyedDirection = 0;
    private int up = Input.Keys.W, down = Input.Keys.S, left = Input.Keys.A, right = Input.Keys.D;
    private int useSlot1 = Input.Keys.CONTROL_RIGHT, useSlot2 = Input.Keys.SHIFT_RIGHT, useSlot3 = Input.Keys.ENTER;
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
    private Sprite attackSprite;
    private TextureAtlas attackAtlas = new TextureAtlas("items/slash.atlas");
    private int ammo = 0;
    private Boolean dead =false;

    public Player(FileHandle atlasfile, ItemAttributes gameAttrs, int num) {
        playerNum = num;
        setDefaultValues();
        textureAtlas = new TextureAtlas(atlasfile);
        playerSprite = new Sprite(textureAtlas.findRegion("playerDown", 0));
        playerSprite.setPosition(Gdx.graphics.getWidth() / 2 - playerSprite.getWidth() / 2, Gdx.graphics.getHeight() / 2 - playerSprite.getHeight() / 2);
        coordinates = new Vector3(0, 0, 0);
        //Attributes only generated once.
        itemAttrs = gameAttrs;
        inv = new Inventory(itemAttrs);

        slots[0] = new Item(itemAttrs, 4, 0);
        slots[1] = new Item(itemAttrs);
        slots[2] = new Item(itemAttrs);
    }

    // PLAYER STATS
    private int maxHealth;
    private int health;
    private double stamina;
    private int coin;
    public void checkForDeath()
    {
        if(health <= 0)
        {
            dead = true;
            health=0;
        }
    }
    public boolean isDead()
    {
        return dead;
    }
    public void takeDamage(int damage)
    {
        if(slots[1].getDefence() != 0 || slots[2].getDefence()!=0)
        {
            double temp = slots[1].getDefence();
            double temp2 = slots[2].getDefence();
            if(temp==0 && temp2!=0)
            {
                //use the defence item -- durability
                health =- damage/(int)temp;
            }
            else
            {
                health =- damage/(int)temp2;
            }
        }
        else
        {
            health-=damage;
        }
    }
    public int getPlayerNum()
    {
        return playerNum;
    }
    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getCoin() {
        return coin;
    }

    public String getSlotName(int slot) {
        if(slot == 0){  
            return slots[0].name;     
        }else if (slot == 1) {
            return slots[1].name;
        } else {
            return slots[2].name;
        }
    }

    public double getSlotDurability(int slot) {
        if (slot == 1) {
            return slots[1].getDurability();
        } else {
            return slots[2].getDurability();
        }
    }

    public void addToInv(Item item) {
        inv.inventoryAdd(item, 0);
        slotsCheck();
    }

    public Color getItemColor(int slot){
        if(slot ==1){
            return slots[1].getItemColor(slots[1]);
        } else {
            return slots[2].getItemColor(slots[2]);
        }

      
    }

    public void setStamina(double stamina) {
        if (stamina <= 100)
            this.stamina = stamina;
    }

    public double getStamina() {
        return stamina;
    }


    public void setDefaultValues() {

        maxHealth = 100;
        health = 100;
        stamina = 0;
        coin = 0;
    }

    public Body createBody(World world) {
        this.world = world;
        Body b;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(getPlayerSprite().getX() + 7.5f, getPlayerSprite().getY() + 4f);
        b = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getPlayerSprite().getWidth() / 2 / Constants.PPM, getPlayerSprite().getHeight() / 2 / Constants.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        Fixture fixture = b.createFixture(fixtureDef);
        shape.dispose();
        body = b;
        return body;
    }

    public void increaseXP(int amount) {
        level.increaseXP(amount);
    }

    public int displayXP() {
        int xp = level.getXP();
        return xp;
    }

    public int getLevel() {
        return level.getLevel();
    }

    public void walk(int direction) {
        secondlastKeyedDirection = lastKeyedDirection;

        if (timer > FRAME_SPEED) {
            timer = 0;
        }

        if (currentFrame >= MAX_FRAMES)
            ;

        if (direction == 9) {
            lastKeyedDirection = 9;

            moveAmountX = PLAYER_SPEED * DIAG_MOD;
            moveAmountY = (PLAYER_SPEED * DIAG_MOD) / 2;
            tmpCoords.set(0, 1, 0);

            isMoving = true;

            return;
        }

        if (direction == 7) {
            lastKeyedDirection = 7;

            moveAmountX = -PLAYER_SPEED * DIAG_MOD;
            moveAmountY = (PLAYER_SPEED * DIAG_MOD) / 2;
            tmpCoords.set(-1, 0, 0);

            isMoving = true;

            return;
        }

        if (direction == 3) {
            lastKeyedDirection = 3;

            moveAmountX = PLAYER_SPEED * DIAG_MOD;
            moveAmountY = (-PLAYER_SPEED * DIAG_MOD) / 2;
            tmpCoords.set(1, 0, 0);

            isMoving = true;

            return;
        }

        if (direction == 1) {
            lastKeyedDirection = 1;

            moveAmountX = -PLAYER_SPEED * DIAG_MOD;
            moveAmountY = (-PLAYER_SPEED * DIAG_MOD) / 2;
            tmpCoords.set(0, -1, 0);

            isMoving = true;

            return;
        }

        if (direction == 4) {
            lastKeyedDirection = 4;

            if (secondlastKeyedDirection == 4) {

                moveAmountX = -PLAYER_SPEED * 2;
                moveAmountY = 0;
                tmpCoords.set(-1, -1, 0);

                isMoving = true;
            }

            return;
        }

        if (direction == 6) {
            lastKeyedDirection = 6;

            if (secondlastKeyedDirection == 6) {

                moveAmountX = PLAYER_SPEED * 2;
                moveAmountY = 0;
                tmpCoords.set(1, 1, 0);

                isMoving = true;
            }

            return;
        }

        if (direction == 2) {
            lastKeyedDirection = 2;

            if (secondlastKeyedDirection == 2) {

                moveAmountX = 0;
                moveAmountY = -PLAYER_SPEED;
                tmpCoords.set(+1, -1, 0);

                isMoving = true;
            }

            return;
        }

        if (direction == 8) {
            lastKeyedDirection = 8;

            if (secondlastKeyedDirection == 8) {

                moveAmountX = 0;
                moveAmountY = PLAYER_SPEED;
                tmpCoords.set(-1, 1, 0);

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

        timer = 0;

    }

    public void slotsCheck() { //Checks if any of the slots are empty and if any item in the inventory can fill its gap
  
        if (slots[1].type < 0) {
            slot1Remove();
        }

        if (slots[2].type < 0) {
            slot2Remove();
        }

        if (slots[0].type < 0) {
            slot3Remove();
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

        if (slots[0].name.equals("Empty")) {
            Item foundItem = inv.getFirstSingle();
            if (foundItem != null) {
                //2. Add item found to slot 3
                slots[0] = foundItem;
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

    public void slot3Remove() { //Removes the item from the second slot of the player
        //Remove item from slot 3
        slots[0] = new Item(itemAttrs, 4, 0);

        //Try to fill slot
        slotsCheck();
    }

    public void update(TiledMapTileLayer floorLayer, TiledMapTileLayer entityLayer) {
        getBody().setTransform(getBody().getPosition().set(getPlayerSprite().getX() + 7.5f, getPlayerSprite().getY() + 4f), 0);
        if(MazeGame.debugger==true)
        {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT)) { //TESTING - Adds a sword to the inventory
                inv.inventoryAdd(new Item(itemAttrs, 0, 1), 0);
                slotsCheck();
            }
    
            if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_RIGHT)) { //TESTING - Prints out the inventory contents
                inv.inventoryAdd(new Item(itemAttrs, 4,3), 0);
                slotsCheck();
                inv.printInventory();
            }
        }
        //item usage
        if (Gdx.input.isKeyJustPressed(useSlot1)) {
            slots[1].useItem();
            if (slots[1].type == 0) {
                if (attackCircle == null) {
                    String x = slots[1].name;
                    meleeAttack(x, (int)slots[1].getDamage());
                }
            } else if (slots[1].type == 1) {
                if(ammo<1){
                    rangeAttack(slots[1].name,1,(int)slots[1].getDamage());
                }
            } else if (slots[1].type == 2) {
                if(ammo<1){
                    rangeAttack(slots[1].name,0,(int)slots[1].getDamage());
                }
            } else if (slots[1].type == 3) {
                //Shield action
            }
            slotsCheck();
        }

        if (Gdx.input.isKeyJustPressed(useSlot2)) {
            slots[2].useItem();
            if (slots[2].type == 0) {
                if(attackCircle ==null)
                {
                    String x = slots[2].name;
                    meleeAttack(x, (int)slots[2].getDamage());
                }
            } else if (slots[2].type == 1) {
                if(ammo<1){
                    rangeAttack(slots[2].name,1,(int)slots[2].getDamage());
                }
            } else if (slots[2].type == 2) {
                if(ammo<1){
                    rangeAttack(slots[2].name,0,(int)slots[2].getDamage());
                }
            } else if (slots[2].type == 3) {
                //Shield action
            }
            slotsCheck();
        }

        if ((Gdx.input.isKeyJustPressed(useSlot3))) {
            if (slots[0].name.equals("Empty")) {
            } else {
                if(getSlotName(0).contains("God potion"))
                {
                    maxHealth = maxHealth+20;
                    health= maxHealth;
                    slots[0].useItem();
                }
                else
                {
                    int newHealth = health + (int)slots[0].getDurability();
                    if (newHealth > maxHealth) newHealth = maxHealth;
                    setHealth(newHealth);
                    slots[0].useItem();
                }
            }
            slotsCheck();
        }


        if(inputIsLocked == true) {
            return;
        }

        if(isMoving == true) {
             if (timer == 2) {
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
                        playerSprite.setRegion(textureAtlas.findRegion("playerRUp", currentFrame));

                    if (lastKeyedDirection == 7)
                        playerSprite.setRegion(textureAtlas.findRegion("playerLUp", currentFrame));

                    if (lastKeyedDirection == 3)
                        playerSprite.setRegion(textureAtlas.findRegion("playerRDown", currentFrame));

                    if (lastKeyedDirection == 1)
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
                    if (!PathFindingSystem.VectorToNode(new Vector2(coordinates.x, coordinates.y + 1f)).isOccupied())
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
                    if (!PathFindingSystem.VectorToNode(new Vector2(coordinates.x -1f, coordinates.y)).isOccupied())
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
                    if (!PathFindingSystem.VectorToNode(new Vector2(coordinates.x + 1f, coordinates.y)).isOccupied())
                        if (!PathFindingSystem.VectorToNode(new Vector2(coordinates.x, coordinates.y - 1f)).isOccupied())
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
                    if (!PathFindingSystem.VectorToNode(new Vector2(coordinates.x, coordinates.y - 1f)).isOccupied())
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
                    if (!PathFindingSystem.VectorToNode(new Vector2(coordinates.x - 1f, coordinates.y - 1f)).isOccupied())
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
                    if (!PathFindingSystem.VectorToNode(new Vector2(coordinates.x + 1f, coordinates.y + 1f)).isOccupied())
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
                    if (!PathFindingSystem.VectorToNode(new Vector2(coordinates.x + 1f, coordinates.y - 1f)).isOccupied())
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
                    if (!PathFindingSystem.VectorToNode(new Vector2(coordinates.x - 1f, coordinates.y + 1f)).isOccupied())
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

    public void setUseSlot3(int use) { useSlot3 = use; }

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
    public void meleeAttack(String name,int damage)
    {
        Vector2 pos = new Vector2();
        attackSprite = new Sprite(attackAtlas.findRegion("slash",5));

        if (lastKeyedDirection == 8 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 7)) {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM + 2.5f, getPlayerSprite().getHeight()/2 / Constants.PPM +6);
            attackSprite.setRotation(45);
            attackSprite.setFlip(true, true);
        }
        if (lastKeyedDirection == 2 && !(secondlastKeyedDirection == 3 || secondlastKeyedDirection == 1)) {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM + 2.5f, getPlayerSprite().getHeight()/2 / Constants.PPM - 5.5f);
            attackSprite.setRotation(45);
        }
        if (lastKeyedDirection == 4 && !(secondlastKeyedDirection == 7 || secondlastKeyedDirection == 1)) {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM - 7.5f, getPlayerSprite().getHeight()/2 / Constants.PPM);
            attackSprite.setRotation(135);
        }
        if (lastKeyedDirection == 6 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 3)) {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM + 12, getPlayerSprite().getHeight()/2 / Constants.PPM);
            attackSprite.setRotation(135);
        }
        if (lastKeyedDirection == 9) {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM+7, getPlayerSprite().getHeight()/2 / Constants.PPM +2);
            attackSprite.setFlip(true,true);
        }
        if (lastKeyedDirection == 7) {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM-3, getPlayerSprite().getHeight()/2 / Constants.PPM +2);
            //attackSprite.setRotation(90);
            attackSprite.setFlip(true,false);
        }
        if (lastKeyedDirection == 3) {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM + 7f, getPlayerSprite().getHeight()/2 / Constants.PPM -3);
            attackSprite.setRotation(90);
        }
        if (lastKeyedDirection == 1) {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM - 2.5f, getPlayerSprite().getHeight()/2 / Constants.PPM -3);
        }
          
        attackCircle = ShapeMaker.createSquare(new Vector2(getPlayerSprite().getX() + 7.5f, getPlayerSprite().getY() + 4f),pos, true, world);
        attackCircle.setUserData(name.toString());

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
                },0.1f);  
    }
    public Sprite getAttackSprite()
    {
        return attackSprite;
    }
    public Body getAttackBody()
    {
        return attackCircle;
    }
    public void rangeAttack(String name,int range, int damage)
    {
        Body dest;
        int offset =0;
        int speed=0;
        float timeout=1f;
        if(range==0) //short range
        {
            offset =0;
            speed=1;
            timeout=1.2f;
        }
        else if(range ==1)//long range
        {
            offset=30;
            speed=2;
            timeout=0.7f;
        }
        Vector2 pos = new Vector2();
        Vector2 dir = new Vector2();
        if (lastKeyedDirection == 8 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 7))
        {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM, getPlayerSprite().getHeight()/2 / Constants.PPM +(30+offset));
            dir = new Vector2(0,1);
        }
        if (lastKeyedDirection == 2 && !(secondlastKeyedDirection == 3 || secondlastKeyedDirection == 1))
        {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM, getPlayerSprite().getHeight()/2 / Constants.PPM -(30+offset));
            dir = new Vector2(0,2);  
        }
        if (lastKeyedDirection == 4 && !(secondlastKeyedDirection == 7 || secondlastKeyedDirection == 1))
        {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM -(30+offset), getPlayerSprite().getHeight()/2 / Constants.PPM);
            dir = new Vector2(2,0);  
        }
        if (lastKeyedDirection == 6 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 3))
        {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM + (30+offset), getPlayerSprite().getHeight()/2 / Constants.PPM);
            dir = new Vector2(1,0);
        }
        if (lastKeyedDirection == 9)
        {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM + (30+offset), getPlayerSprite().getHeight()/2 / Constants.PPM);
            dir = new Vector2(1,0);
        }
        if (lastKeyedDirection == 7)
        {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM -(30+offset), getPlayerSprite().getHeight()/2 / Constants.PPM);
            dir = new Vector2(2,0);   
        }
        if (lastKeyedDirection == 3)
        {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM + (30+offset), getPlayerSprite().getHeight()/2 / Constants.PPM);
            dir = new Vector2(1,0);
        }
        if (lastKeyedDirection == 1)
        {
            pos = new Vector2(getPlayerSprite().getWidth()/2 / Constants.PPM -(30+offset), getPlayerSprite().getHeight()/2 / Constants.PPM);
            dir = new Vector2(2,0); 
        }

            dest = ShapeMaker.createSquare(new Vector2(getPlayerSprite().getX() + 7.5f, getPlayerSprite().getY() + 4f),
            pos, true, world);
            dest.setUserData("dest");
            
            MazeGame.entities.add(new Projectile(world,new Vector2(getPlayerSprite().getX() + 7.5f, getPlayerSprite().getY() + 4f),dir,ammo,
            name.toString(),playerNum,damage,speed));
          
            dir= new Vector2(0,0);
            ammo++;
            Timer timer=new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    try{
                        world.destroyBody(dest);
                    }
                    catch(Exception e){};
                }
            },timeout);  
            Timer timer1=new Timer();
            timer1.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    try{
                       MazeGame.entities.get(0).setHit(true);
                    }
                    catch(Exception e){};
                }
            },3f);  
    }
    public void resetAmmo()
    {
        ammo=0;
    }
    public Vector2 getVect2Coordinates() {
        return new Vector2(coordinates.x, coordinates.y);
    }
}