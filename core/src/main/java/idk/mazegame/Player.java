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
    private Vector3 coordinates;

    private int currentFrame = 0;
    private int timer = 0;
    private int lastKeyedDirection = 0;
    private int secondlastKeyedDirection = 0;
    private int up = Input.Keys.UP, down = Input.Keys.DOWN, left = Input.Keys.LEFT, right = Input.Keys.RIGHT;
    private boolean inputIsLocked = false, isMoving = false; // to use for certain parts where player input is disabled: tile-based movement, cutscenes, stamina, debuff
    private float moveAmountX = 0f, moveAmountY = 0f;
    private final int MAX_FRAMES = 4;
    private final float PLAYER_SPEED = 9.5f;
    private final int FRAME_SPEED = 3;
    private final float DIAG_MOD = 1f; //0.707 for normalization

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

        timer++;

        if (direction == 9) {

            playerSprite.setRegion(textureAtlas.findRegion("playerRUp", currentFrame));

            playerSprite.translate(+(PLAYER_SPEED*DIAG_MOD), +(PLAYER_SPEED*DIAG_MOD)/2);
            coordinates.y += 1f;

            lastKeyedDirection = 9;
            return;
        }

        if (direction == 7) {

            playerSprite.setRegion(textureAtlas.findRegion("playerLUp", currentFrame));

            playerSprite.translate(-(PLAYER_SPEED*DIAG_MOD), +(PLAYER_SPEED*DIAG_MOD)/2);
            coordinates.x -= 1f;

            lastKeyedDirection = 7;
            return;
        }

        if (direction == 3) {

            playerSprite.setRegion(textureAtlas.findRegion("playerRDown", currentFrame));

            playerSprite.translate(+(PLAYER_SPEED*DIAG_MOD), -(PLAYER_SPEED*DIAG_MOD)/2);
            coordinates.x += 1f;

            lastKeyedDirection = 3;
            return;
        }

        if (direction == 1) {

            playerSprite.setRegion(textureAtlas.findRegion("playerLDown", currentFrame));

            playerSprite.translate(-(PLAYER_SPEED*DIAG_MOD), -(PLAYER_SPEED*DIAG_MOD)/2);
            coordinates.y -= 1f;
            //coordinates.y -= 1;

            lastKeyedDirection = 1;
            return;
        }

        if (direction == 4) {

            if (secondlastKeyedDirection == 4) {
                playerSprite.setRegion(textureAtlas.findRegion("playerLeft", currentFrame));
                playerSprite.translateX(-PLAYER_SPEED*2);
                coordinates.x -= 1f;
                coordinates.y -= 1f;
            }

            lastKeyedDirection = 4;
            return;
        }

        if (direction == 6) {

            if (secondlastKeyedDirection == 6) {
                playerSprite.setRegion(textureAtlas.findRegion("playerRight", currentFrame));
                playerSprite.translateX(+PLAYER_SPEED*2);
                coordinates.x += 1f;
                coordinates.y += 1f;
            }

            lastKeyedDirection = 6;
            return;
        }

        if (direction == 2) {

            if (secondlastKeyedDirection == 2) {
                playerSprite.setRegion(textureAtlas.findRegion("playerDown", currentFrame));
                playerSprite.translateY(-PLAYER_SPEED);
                coordinates.x += 1f;
                coordinates.y -= 1f;
            }

            lastKeyedDirection = 2;
            return;
        }

        if (direction == 8) {

            if (secondlastKeyedDirection == 8) {
                playerSprite.setRegion(textureAtlas.findRegion("playerUp", currentFrame));
                playerSprite.translateY(+PLAYER_SPEED);
                coordinates.x -= 1f;
                coordinates.y += 1f;
            }

            lastKeyedDirection = 8;
            return;
        }

        //isMoving = true;

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

        if (lastKeyedDirection == 9 || secondlastKeyedDirection == 9)
            playerSprite.setRegion(textureAtlas.findRegion("playerRUp", 0));

        if (lastKeyedDirection == 7 || secondlastKeyedDirection == 7)
            playerSprite.setRegion(textureAtlas.findRegion("playerLUp", 0));

        if (lastKeyedDirection == 3 || secondlastKeyedDirection == 3)
            playerSprite.setRegion(textureAtlas.findRegion("playerRDown", 0));

        if (lastKeyedDirection == 1 || secondlastKeyedDirection == 1)
            playerSprite.setRegion(textureAtlas.findRegion("playerLDown", 0));

        currentFrame = 1;
        timer = 0;

    }

    public void checkInput(TiledMapTileLayer floorLayer, TiledMapTileLayer entityLayer) {

        if(inputIsLocked == true) {
            return;
        }

        if(isMoving == true) {
            //player update position, decrement from move amount, once move amount == finished - set isMoving to false

//            if (timer < 1) {
//                timer++;
//                return;
//            }
            playerSprite.translate(0.25f*moveAmountX,0.25f*moveAmountY);

            if (moveAmountX > 0)
                moveAmountX -= 0.25f*moveAmountX;
            if (moveAmountY > 0)
                moveAmountY -= 0.25f*moveAmountY;

            if (lastKeyedDirection == 8 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 7))
                playerSprite.setRegion(textureAtlas.findRegion("playerUp", currentFrame));

            if (lastKeyedDirection == 2 && !(secondlastKeyedDirection == 3 || secondlastKeyedDirection == 1))
                playerSprite.setRegion(textureAtlas.findRegion("playerDown", currentFrame));

            if (lastKeyedDirection == 4 && !(secondlastKeyedDirection == 7 || secondlastKeyedDirection == 1))
                playerSprite.setRegion(textureAtlas.findRegion("playerLeft", currentFrame));

            if (lastKeyedDirection == 6 && !(secondlastKeyedDirection == 9 || secondlastKeyedDirection == 3))
                playerSprite.setRegion(textureAtlas.findRegion("playerRight", currentFrame));

            if (lastKeyedDirection == 9 || secondlastKeyedDirection == 9)
                playerSprite.setRegion(textureAtlas.findRegion("playerRUp", currentFrame));

            if (lastKeyedDirection == 7 || secondlastKeyedDirection == 7)
                playerSprite.setRegion(textureAtlas.findRegion("playerLUp", currentFrame));

            if (lastKeyedDirection == 3 || secondlastKeyedDirection == 3)
                playerSprite.setRegion(textureAtlas.findRegion("playerRDown", currentFrame));

            if (lastKeyedDirection == 1 || secondlastKeyedDirection == 1)
                playerSprite.setRegion(textureAtlas.findRegion("playerLDown", currentFrame));

            currentFrame++;
            timer = 0;

            if (currentFrame == 4) {
                isMoving = false;
                currentFrame = 0;
            }

            return;
        }

        if (Gdx.input.isKeyPressed(up) && Gdx.input.isKeyPressed(right) && !(Gdx.input.isKeyPressed(down) || Gdx.input.isKeyPressed(left))) {
            if (entityLayer.getCell( (int)(coordinates.x - 1f), (int)(coordinates.y + 2f)) == null)
                if (floorLayer.getCell( (int)(coordinates.x), (int)(coordinates.y + 1f)) != null)
                    walk(9);
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
