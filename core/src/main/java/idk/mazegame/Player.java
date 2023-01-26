package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private TextureAtlas textureAtlas;
    private Sprite playerSprite;
    private int currentFrame = 0;
    private int timer = 0;
    private final int MAX_FRAMES = 4;
    private final float PLAYER_SPEED = 6f;
    private final int FRAME_SPEED = 3;
    private int lastKeyedDirection = 0;
    private int secondlastKeyedDirection = 0;
    private int inputDelay = 1;

    public Player() {
        textureAtlas = new TextureAtlas(Gdx.files.internal("player1Sprites.atlas"));
        playerSprite = new Sprite(textureAtlas.findRegion("playerDown",0));
        playerSprite.setPosition(Gdx.graphics.getWidth()/2 - playerSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - playerSprite.getHeight()/2);
        playerSprite.setScale(4f);
    }

    public void walk(int direction) {
        secondlastKeyedDirection = lastKeyedDirection;

        /**
         * args: direction, current frame
         *
         * if down (2)
         * if up (8)
         * if left (4)
         * if right (6)
         * if rup (9)
         * if lup (7)
         * if rdown (3)
         * if ldown (1)
         */
    }

    public void idle(int direction) {
        ;
    }

    public void dispose() {
        ;
    }
}
