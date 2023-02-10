package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;

public class Player {
    private TextureAtlas textureAtlas;
    private Sprite playerSprite;
    private Vector3 coordinates, tmpCoords = new Vector3(0,0,0);

    private int currentFrame = 0;
    private int timer = 0;
    private int lastKeyedDirection = 0;
    private int secondlastKeyedDirection = 0;
    private int up = Input.Keys.UP, down = Input.Keys.DOWN, left = Input.Keys.LEFT, right = Input.Keys.RIGHT;
    private boolean inputIsLocked = false, isMoving = false, nextStep = false; // to use for certain parts where player input is disabled: tile-based movement, cutscenes, stamina, debuff
    private float moveAmountX = 0f, moveAmountY = 0f, targetX = 0, targetY = 0;
    private final int MAX_FRAMES = 4;
    private final float PLAYER_SPEED = 9.5f;
    private final int FRAME_SPEED = 3;
    private final float DIAG_MOD = 1f; //0.707 for normalization
    private final int MAX_INPUT_DELAY = 1;
    private int inputDelay = MAX_INPUT_DELAY;

    public Player(FileHandle atlasfile) {
        textureAtlas = new TextureAtlas(atlasfile);
        playerSprite = new Sprite(textureAtlas.findRegion("playerDown",0));
        playerSprite.setPosition(Gdx.graphics.getWidth()/2 - playerSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - playerSprite.getHeight()/2);
        coordinates = new Vector3(0,0, 0);
        //playerSprite.setScale(4f);
    }

    public void walk(int direction) {
//        Gdx.app.log("player X", String.valueOf(getPlayerSprite().getX()));
//        Gdx.app.log("player Y", String.valueOf(getPlayerSprite().getY()));
        secondlastKeyedDirection = lastKeyedDirection;

        if (timer > FRAME_SPEED) {
            currentFrame++;
            timer = 0;
        }

        if (currentFrame >= MAX_FRAMES)
            currentFrame = 0;

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

        currentFrame = 1;
        timer = 0;

    }

    public void update(TiledMapTileLayer floorLayer, TiledMapTileLayer entityLayer) {

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
                currentFrame++;
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

                if (nextStep == false) {
                    currentFrame = 3;
                    nextStep = true;
                }
                else if (nextStep == true) {
                    currentFrame = 1;
                    nextStep = false;
                }

                idle();
                targetX = 0;
                targetY = 0;
                moveAmountX = 0;
                moveAmountY = 0;

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
        }
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

}
