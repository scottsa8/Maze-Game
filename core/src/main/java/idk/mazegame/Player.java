package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private TextureAtlas textureAtlas;
    private Sprite playerSprite;
    private Vector2 coordinates;

    private int currentFrame = 0;
    private int timer = 0;
    private int lastKeyedDirection = 0;
    private int secondlastKeyedDirection = 0;
    private int up = Input.Keys.UP,down = Input.Keys.DOWN,left = Input.Keys.LEFT,right = Input.Keys.RIGHT;
    private boolean inputIsLocked; // to use for certain where player input is disabled: tile-based movement, cutscenes, stamina, debuff

    private final int MAX_FRAMES = 4;
    private final float PLAYER_SPEED = 2f;
    private final int FRAME_SPEED = 3;

    public Player(FileHandle atlasfile) {
        textureAtlas = new TextureAtlas(atlasfile);
        playerSprite = new Sprite(textureAtlas.findRegion("playerDown",0));
        playerSprite.setPosition(Gdx.graphics.getWidth()/2 - playerSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - playerSprite.getHeight()/2);
        //playerSprite.setScale(4f);
    }

    public void walk(int direction) {
        secondlastKeyedDirection = lastKeyedDirection;

        if (direction == 4) {

            if (timer > FRAME_SPEED) {
                currentFrame++;
                timer = 0;
            }

            if (currentFrame >= MAX_FRAMES)
                currentFrame = 0;

            if (secondlastKeyedDirection == 4)
                playerSprite.setRegion(textureAtlas.findRegion("playerLeft", currentFrame));

            playerSprite.translateX(-PLAYER_SPEED);
            timer++;
            lastKeyedDirection = 4;
            return;
        }

        if (direction == 6) {

            if (timer > FRAME_SPEED) {
                currentFrame++;
                timer = 0;
            }

            if (currentFrame >= MAX_FRAMES)
                currentFrame = 0;

            if (secondlastKeyedDirection == 6)
                playerSprite.setRegion(textureAtlas.findRegion("playerRight", currentFrame));

            playerSprite.translateX(+PLAYER_SPEED);
            timer++;
            lastKeyedDirection = 6;
            return;
        }

        if (direction == 2) {

            if (timer > FRAME_SPEED) {
                currentFrame++;
                timer = 0;
            }

            if (currentFrame >= MAX_FRAMES)
                currentFrame = 0;

            if (secondlastKeyedDirection == 2)
                playerSprite.setRegion(textureAtlas.findRegion("playerDown", currentFrame));

            playerSprite.translateY(-PLAYER_SPEED);
            timer++;
            lastKeyedDirection = 2;
            return;
        }

        if (direction == 8) {

            if (timer > FRAME_SPEED) {
                currentFrame++;
                timer = 0;
            }

            if (currentFrame >= MAX_FRAMES)
                currentFrame = 0;

            if (secondlastKeyedDirection == 8)
                playerSprite.setRegion(textureAtlas.findRegion("playerUp", currentFrame));

            playerSprite.translateY(+PLAYER_SPEED);
            timer++;
            lastKeyedDirection = 8;
            return;
        }

        if (direction == 9) {
            if (timer > FRAME_SPEED) {
                currentFrame++;
                timer = 0;
            }

            if (currentFrame >= MAX_FRAMES)
                currentFrame = 0;

            playerSprite.setRegion(textureAtlas.findRegion("playerRUp", currentFrame));

            playerSprite.translate(+(PLAYER_SPEED*0.707f), +(PLAYER_SPEED*0.707f));
            timer++;
            lastKeyedDirection = 9;
            return;
        }

        if (direction == 7) {
            if (timer > FRAME_SPEED) {
                currentFrame++;
                timer = 0;
            }

            if (currentFrame >= MAX_FRAMES)
                currentFrame = 0;

            playerSprite.setRegion(textureAtlas.findRegion("playerLUp", currentFrame));

            playerSprite.translate(-(PLAYER_SPEED*0.707f), +(PLAYER_SPEED*0.707f));
            timer++;
            lastKeyedDirection = 7;
            return;
        }

        if (direction == 3) {
            if (timer > FRAME_SPEED) {
                currentFrame++;
                timer = 0;
            }

            if (currentFrame >= MAX_FRAMES)
                currentFrame = 0;

            playerSprite.setRegion(textureAtlas.findRegion("playerRDown", currentFrame));

            playerSprite.translate(+(PLAYER_SPEED*0.707f), -(PLAYER_SPEED*0.707f));
            timer++;
            lastKeyedDirection = 3;
            return;
        }

        if (direction == 1) {
            if (timer > FRAME_SPEED) {
                currentFrame++;
                timer = 0;
            }

            if (currentFrame >= MAX_FRAMES)
                currentFrame = 0;

            playerSprite.setRegion(textureAtlas.findRegion("playerLDown", currentFrame));

            playerSprite.translate(-(PLAYER_SPEED*0.707f), -(PLAYER_SPEED*0.707f));
            timer++;
            lastKeyedDirection = 1;
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

    public void checkInput() {
        if (Gdx.input.isKeyPressed(left) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(right) || Gdx.input.isKeyPressed(down)))
            walk(4);
        if (Gdx.input.isKeyPressed(right) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(down) || Gdx.input.isKeyPressed(left)))
            walk(6);
        if (Gdx.input.isKeyPressed(down) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(right) || Gdx.input.isKeyPressed(left)))
            walk(2);
        if (Gdx.input.isKeyPressed(up) && !(Gdx.input.isKeyPressed(down) || Gdx.input.isKeyPressed(right) || Gdx.input.isKeyPressed(left)))
            walk(8);
        if (Gdx.input.isKeyPressed(up) && Gdx.input.isKeyPressed(right) && !(Gdx.input.isKeyPressed(down) || Gdx.input.isKeyPressed(left)))
            walk(9);
        if (Gdx.input.isKeyPressed(up) && Gdx.input.isKeyPressed(left) && !(Gdx.input.isKeyPressed(down) || Gdx.input.isKeyPressed(right)))
            walk(7);
        if (Gdx.input.isKeyPressed(down) && Gdx.input.isKeyPressed(right) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(left)))
            walk(3);
        if (Gdx.input.isKeyPressed(down) && Gdx.input.isKeyPressed(left) && !(Gdx.input.isKeyPressed(up) || Gdx.input.isKeyPressed(right)))
            walk(1);
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
}
