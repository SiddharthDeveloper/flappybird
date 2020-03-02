package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    // Texture is just like image..
    private Texture background;
    private Texture gameover;
    private Texture[] birds;
    private Texture topTube;
    private Texture bottomTube;

    /*
    shapeRenderer is used to draw shapes..
    just imagine that bird is circle
    so we detect collision..
     */
    //ShapeRenderer shapeRenderer;
    // just track the state of bird like bird or bird2..
    private int flapState = 0;
    private int score = 0;
    private int scoringTube = 0;
    // To  keep track the state of the game..
    private int gameState = 0;
    // number of tubes 4 bcz so in big screen
    // we can se 2 or max 3 otherwise it will show so many tubes..
    private int numberOfTubes = 4;

    // to adjust the falling speed..
    private float gravity = 2;
    // bird y stand for v ertical position to move the bird..
    // bird y always stand for centre so no need to change his position..
    private float birdY = 0;
    private float velocity = 0;
    private float gap = 400;
    private float maxTubeOffset;
    // TO move the tubes..
    private float tubeVelocity = 4;
    private float[] tubeX = new float[numberOfTubes];
    private float[] tubeOffset = new float[numberOfTubes];
    private float distanceBetweenTubes;

    //to assign the bird shape
    // so we detect bird touch tube or not..
    private Circle birdCircle;
    private BitmapFont font;
    /*
    used for tubes imagine that tube is rectangle
    so when circle(bird) touch rectangle collision
    detect..
     */
    private Rectangle[] topTubeRectangles;
    private Rectangle[] bottomTubeRectangles;


    /*
    basically random number is used for
    to generate tube in different size
    other all tubes has same size..
     */
    private Random randomGenerator;


    // predefined  Method Basically this method runs when app is run..
    @Override
    public void create() {
        //predefined method..
        batch = new SpriteBatch();
        // to set background image..
        background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        //shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        // texture array for define two birds to change state..
        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        // assign the tubes for background..
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];


        startGame();


    }

    private void startGame() {
        // To  birdy is used to get  the height according to screen size
        // to jump the bird..
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
           //to show  no of tubes it look screen is running
        for (int i = 0; i < numberOfTubes; i++) {

            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();

        }

    }

    //predefined method this is used for repeated task..
    @Override
    public void render() {
        /*
        first background create hoga then
        tube create hogi varna tube ke upper
        background over lay karega..
         */

        batch.begin();
        //to set background according to screen of the phone ..
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {

                score++;

                Gdx.app.log("Score", String.valueOf(score));

                if (scoringTube < numberOfTubes - 1) {

                    scoringTube++;

                } else {

                    scoringTube = 0;

                }

            }
            // Just Touch is used to check what happen when Screen is tapped touch..
            if (Gdx.input.justTouched()) {

                // Speed of Bird..
                velocity = -20;

            }

            for (int i = 0; i < numberOfTubes; i++) {

                // to show tubes continuously other after complete loop tube is not showing..
                // just shift everything  in right after completing loop....
                if (tubeX[i] < -topTube.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                } else {

                    tubeX[i] = tubeX[i] - tubeVelocity;


                }
                 /*
                 Basically tube ko design karna hai jitni screen ki height ha uske half me
                 or kitne duri par tube hogi ye bhi set karna ha ek uppar vali tube ha or
                 ek niche vali tube ha dono ko screen ke half or center of the screen karna ha ..
                  */
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            }

        // to stop going the bird on the bottom on the screen
            if (birdY > 0) {
                // basically to increase the falling speed with the help of gravity variable..
                velocity = velocity + gravity;
                birdY -= velocity;

            } else {

                gameState = 2;

            }

        } else if (gameState == 0) {

            if (Gdx.input.justTouched()) {

                gameState = 1;


            }

        } else if (gameState == 2) {

            batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

            if (Gdx.input.justTouched()) {

                gameState = 1;
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = 0;


            }

        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }


        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);

        font.draw(batch, String.valueOf(score), 100, 200);

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);


        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);
        //shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for (int i = 0; i < numberOfTubes; i++) {

            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

         // to detect collision when bird touch tubes..
            if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

                gameState = 2;

            }

        }

        batch.end();

        //shapeRenderer.end();


    }


}
