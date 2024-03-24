package edu.binghamton.cs.csterdroids;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

//import com.example.myapp.GameActivity;

import java.util.Random;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;



class Boulder {
    float x, y, dx, dy, side;
    float width, height;
    Bitmap stickerBitmap;

    Boulder(Bitmap bitmap) {
        stickerBitmap = bitmap;
        side = Math.max(stickerBitmap.getWidth(), stickerBitmap.getHeight());
    }

    public void update() {
        x += dx;
        y += dy;

        // Check for collisions with screen boundaries
        if (x < 0 || x > width) {
            dx = -dx; // Reverse in direction with double speed
        }
        if (y < 0 || y > height) {
            dy = -dy; // Reverse the direction in y-axis
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(stickerBitmap, x - side / 2, y - side / 2, paint);

    }
}

class ObjectThrown {
    float x, y, dx, dy, side;
    float width, height;
    Bitmap objectBitmap;

    ObjectThrown(Bitmap bitmap) {
        objectBitmap = bitmap;
        side = Math.max(objectBitmap.getWidth(), objectBitmap.getHeight());
    }

    public void update() {
        x += dx;
        y += dy;

        // Check for collisions with screen boundaries
        if (x < 0 || x > width) {
            dx = -dx; // Reverse in direction with double speed
        }
        if (y < 0 || y > height) {
            dy = -dy; // Reverse the direction in y-axis
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(objectBitmap, x - side / 2, y - side / 2, paint);

    }
}

class Ship {
    float x, y, ht, wd;
    float faceRadius;
    Bitmap eyeBitmap;
    Bitmap mouthBitmap;
    //Bitmap eyebrowBitmap;
    Bitmap hairBitmap;
    float sideLength;



    public Ship(Context context) {
        // Load sticker images
        // eyeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.eye_sticker);
        Bitmap originalMouthBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mouth_sticker);
        // Scale down the mouthBitmap to desired size
        float scaleFactor = 0.03f; // Adjust the scale factor as needed
        mouthBitmap = Bitmap.createScaledBitmap(originalMouthBitmap, (int)(originalMouthBitmap.getWidth() * scaleFactor), (int)(originalMouthBitmap.getHeight() * scaleFactor), false);

    }

    public void draw(Canvas canvas, Paint paint, String direction) {
        // Draw face sticker
        float faceLeft = x - faceRadius;
        float faceTop = y - faceRadius;
        canvas.drawBitmap(mouthBitmap, faceLeft, faceTop, paint);

        // Calculate positions for other stickers based on face position

        float mouthLeft = x - mouthBitmap.getWidth() / 2;
        float mouthTop = y + faceRadius / 4;


        // Draw stickers
        canvas.drawBitmap(mouthBitmap, mouthLeft, mouthTop, paint);

    }

    public void updateship(String direction) {
        // Update monster position based on direction
        if (direction.equals("left")) {
            x -= 20;
        } else if (direction.equals("right")) {
            x += 20;
        } else if (direction.equals("up")) {
            y -= 20;
        } else if (direction.equals("down")) {
            y += 20;
        }

        // Check for collisions with screen boundaries
        if (x < 0) {
            x = wd;
        } else if (x > wd) {
            x = 0;
        }

        if (y < 0) {
            y = ht;
        } else if (y > ht) {
            y = 0;
        }
    }
}


public class MainActivity extends AppCompatActivity {

    AsteroidView asteroidView;
    Button leftButton, rightButton, upButton, downButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        asteroidView = new AsteroidView(this);
        setContentView(asteroidView);

    }

    private void startGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }


    static class AsteroidView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;

        int score = -5;
        private float initialTouchX, initialTouchY;
        boolean paused = false;
        Canvas canvas;
        private static final int SWIPE_THRESHOLD = 100;
        String dir="down";
        Paint paint;
        int y;
        int posx, posy;
        int dx, dy;
        int height, width;
        Boulder[] b;
        private long resumeTimeMillis = 0;


        Ship ship;

        private long thisTimeFrame;
        Bitmap backgroundBitmap;
        Bitmap stickerBitmap;
        int screenWidth, screenHeight;

        ObjectThrown[] objectsThrown;
        Bitmap objectBitmap;


        public AsteroidView(Context context) {
            super(context);
            backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_image);
            // Get screen dimensions
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            screenWidth = displayMetrics.widthPixels;
            screenHeight = displayMetrics.heightPixels;
            // Scale the background image to fit the screen
            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenWidth, screenHeight, true);
            ourHolder = getHolder();
            paint = new Paint();


             //stickerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boulder_image);

            Bitmap boulderMouthBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boulder_image);
            // Scale down the mouthBitmap to desired size
            float scaleFactor = 0.03f; // Adjust the scale factor as needed
            stickerBitmap = Bitmap.createScaledBitmap(boulderMouthBitmap, (int)(boulderMouthBitmap.getWidth() * scaleFactor), (int)(boulderMouthBitmap.getHeight() * scaleFactor), false);



            b = new Boulder[5]; // Initialize the Boulder array
            for (int i = 0; i < b.length; i++) {
                b[i] = new Boulder(stickerBitmap);
            }

            ship = new Ship(context);
            ship.x = 300; // Set initial x position of the ship
            ship.y = 300; // Set initial y position of the ship
            ship.sideLength = 50;


            objectBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.thrown_object);
            float scaleFactor1 = 0.1f; // Adjust the scale factor as needed
            objectBitmap = Bitmap.createScaledBitmap(objectBitmap, (int)(objectBitmap.getWidth() * scaleFactor1), (int)(objectBitmap.getHeight() * scaleFactor1), false);
            objectsThrown = new ObjectThrown[15]; // Adjust the size as needed
            for (int i = 0; i < objectsThrown.length; i++) {
                objectsThrown[i] = new ObjectThrown(objectBitmap);
            }



        }


            public void moveShipLeft() {
                ship.x -= 20; // Adjust the movement speed as needed
                // Invalidate the view to trigger onDraw and redraw the ship
                invalidate();
            }

            public void moveShipRight() {
                ship.x += 20; // Adjust the movement speed as needed
                // Invalidate the view to trigger onDraw and redraw the ship
                invalidate();
            }

            public void moveShipUp() {
                ship.y -= 20; // Adjust the movement speed as needed
                // Invalidate the view to trigger onDraw and redraw the ship
                invalidate();
            }

            public void moveShipDown() {
                ship.y += 20; // Adjust the movement speed as needed
                // Invalidate the view to trigger onDraw and redraw the ship
                invalidate();
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                //if (event.getAction() == android.view.MotionEvent.ACTION_DOWN)
                  //  paused = !paused;


                    if (paused) {
                        // If the game is paused and user taps the screen, resume the game
                        resumeTimeMillis = System.currentTimeMillis();
                        paused = false;
                        return true; // Consume the event
                    }
                /*if (event.getAction() == MotionEvent.ACTION_DOWN && paused) {
                    // If the game is paused and user taps the screen, resume the game
                    paused = false;
                    return true; // Consume the event
                }*/



                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialTouchX = event.getX();
                        initialTouchY = event.getY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        float finalTouchX = event.getX();
                        float finalTouchY = event.getY();

                        float deltaX = finalTouchX - initialTouchX;
                        float deltaY = finalTouchY - initialTouchY;

                        if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                                if (deltaX > 0) {
                                    dir = "right";
                                } else {
                                    dir = "left";
                                }
                            }
                        } else { // Vertical swipe
                            if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
                                if (deltaY > 0) {
                                    dir = "down";
                                } else {
                                    dir = "up";
                                }
                            }
                        }
                        return true;
                }
                return super.onTouchEvent(event);

                //return super.onTouchEvent(event);
            }

        @Override
        public void run() {
            Random r = new Random();
            b = new Boulder[10];
            objectsThrown = new ObjectThrown[10];
            posx = 50;
            posy = 50;
            dx = 20;
            dy = 45;

            for (int i = 0; i < 10; ++i) {
                b[i] = new Boulder(stickerBitmap);
                b[i].x = r.nextInt(50);
                b[i].y = r.nextInt(50);
                b[i].dx = r.nextInt(30) - 15;
                b[i].dy = r.nextInt(30) - 15;
                b[i].side = 95;
            }

            for (int i = 0; i < 10; ++i) {
                objectsThrown[i] = new ObjectThrown(objectBitmap);
                objectsThrown[i].x = r.nextInt(50);
                objectsThrown[i].y = r.nextInt(50);
                objectsThrown[i].dx = r.nextInt(30) - 15;
                objectsThrown[i].dy = r.nextInt(30) - 15;
                objectsThrown[i].side = 95;
            }



            while (playing) {
                if (!paused) {
                    update();
                }

                draw();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }



        }

        int i=0;
        Random r = new Random();

        //private long resumeTimeMillis = 0; // Variable to store the time when the game is resumed
        private long avoidedCollisionTimeSeconds = 0; // Variable to store the number of seconds collision was avoided
        public void update() {
            // Check if the game is not paused
            if (!paused) {
                // Update ship position
                ship.updateship(dir);

                if (resumeTimeMillis > 0) {
                    long elapsedTimeMillis = System.currentTimeMillis() - resumeTimeMillis;
                    avoidedCollisionTimeSeconds += elapsedTimeMillis;
                }

                int activeBoulders = 0; // Count of active boulders

                // Check for collision with boulders
                for (int i = 0; i < 10; ++i) {
                    if (isCollision(ship, b[i])) {
                        b[i].x = -100; // Move the boulder off-screen
                        b[i].y = -100;
                        ++score; // Increase the score when a boulder is destroyed

                        if (score == 10) { // If the score reaches 10
                            paused = true;
                        }
                    }

                    if (isCollision(ship, objectsThrown[i])) {
                        objectsThrown[i].x = -100; // Move the boulder off-screen
                        objectsThrown[i].y = -100;
                        //++score; // Increase the score when a boulder is destroyed

                        //if (score == 10) { // If the score reaches 10
                            paused = true;

                    }
                }


                //Boulder[] stickers;/*
                for (Boulder sticker : b) {
                    sticker.update();
                }
                for (ObjectThrown sticker1 : objectsThrown) {
                    sticker1.update();
                }

                // Update boulders position
                for (int i = 0; i < 10; ++i)
                    b[i].update();

                for (int i = 0; i < objectsThrown.length; i++) {
                    objectsThrown[i].update();
                }
                System.out.println("Hello");


                long currentTimeMillis1 = System.currentTimeMillis();
                if (currentTimeMillis1 - resumeTimeMillis > 3000) {
                    for (int i = 0; i < 6; ++i) {
                        if (objectsThrown[i].x < 0 || objectsThrown[i].x > width || objectsThrown[i].y < 0 || objectsThrown[i].y > height) {
                            // Reset the boulder position
                            objectsThrown[i].x = r.nextInt(50);
                            objectsThrown[i].y = r.nextInt(50);
                            objectsThrown[i].dx = r.nextInt(30) - 15;
                            objectsThrown[i].dy = r.nextInt(30) - 15;
                            objectsThrown[i].side = 95;

                            break; // Create only one boulder at a time
                        }
                        if (b[i].x < 0 || b[i].x > width || b[i].y < 0 || b[i].y > height) {
                            // Reset the boulder position
                            b[i].x = r.nextInt(50);
                            b[i].y = r.nextInt(50);
                            b[i].dx = r.nextInt(30) - 15;
                            b[i].dy = r.nextInt(30) - 15;
                            b[i].side = 95;

                            break; // Create only one boulder at a time
                        }
                    }
                    resumeTimeMillis = currentTimeMillis1; // Update the last creation time
                }


                // Create new boulder after every 8 seconds
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - resumeTimeMillis > 3000) {
                    for (int i = 0; i < 6; ++i) {
                        if (b[i].x < 0 || b[i].x > width || b[i].y < 0 || b[i].y > height) {
                            // Reset the boulder position
                                b[i].x = r.nextInt(50);
                                b[i].y = r.nextInt(50);
                                b[i].dx = r.nextInt(30) - 15;
                                b[i].dy = r.nextInt(30) - 15;
                                b[i].side = 95;

                                break; // Create only one boulder at a time
                        }
                    }
                    resumeTimeMillis = currentTimeMillis; // Update the last creation time
                }


            }


        }



        // Method to check collision between ship and boulder
        private boolean isCollision(Ship ship, Boulder boulder) {
            // Calculate distance between ship and boulder's center

            if (boulder == null) {
                return false;
            }

            float dx = ship.x - boulder.x;
            float dy = ship.y - boulder.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            // Check if the distance is less than the sum of ship's half side length and boulder's radius
            return distance < (ship.sideLength*3)/5  + boulder.side;
        }

        private boolean isCollision(Ship ship, ObjectThrown boulder) {
            // Calculate distance between ship and boulder's center

            if (boulder == null) {
                return false;
            }

            float dx = ship.x - boulder.x;
            float dy = ship.y - boulder.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            // Check if the distance is less than the sum of ship's half side length and boulder's radius
            return distance < (ship.sideLength*3)/5  + boulder.side;
        }


        public void draw() {

            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                width = canvas.getWidth();
                height = canvas.getHeight();

                if(width < 0)
                    width = width*-1;
                if(height < 0)
                    height = height*-1;

                // Draw the background color
                canvas.drawColor(Color.argb(255, 0, 0, 0));

                canvas.drawBitmap(backgroundBitmap, 0, 0, null);

                for (Boulder sticker : b) {
                    sticker.draw(canvas, paint);
                }

                for (ObjectThrown object : objectsThrown) {
                    object.draw(canvas, paint);
                }

                // Draw the score text
                Paint textPaint = new Paint();
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(40);
                long avoidedSeconds = avoidedCollisionTimeSeconds / 1000; // Convert milliseconds to seconds
                canvas.drawText("Collision Avoided: " + avoidedSeconds + " seconds", 50, 50, textPaint);

                Paint textPaint1 = new Paint();
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(40);
                long avoidedSeconds1 = avoidedCollisionTimeSeconds / 1000; // Convert milliseconds to seconds
                canvas.drawText("Collision Avoided: " + avoidedSeconds1 + " seconds", 50, 50, textPaint1);

                // Draw the score on the canvas
                canvas.drawText("Boulders Destroyed: " + score, 50, 100, textPaint);



                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 0, 255));
                canvas.drawLine(0, 0, 300, y, paint);


                ship.ht = height;
                ship.wd = width;
                // canvas.drawCircle(posx, posy, 30l, paint);
                for (int i = 0; i < 5; ++i) {
                    //b[i].width = r.nextInt(width);
                    //b[i].height = r.nextInt(height);
                    b[i].width = width;
                    b[i].height = height;
                    b[i].draw(canvas, paint);
                }

                for (int i = 0; i < 5; ++i) {
                    //b[i].width = r.nextInt(width);
                    //b[i].height = r.nextInt(height);
                    objectsThrown[i].width = width;
                    objectsThrown[i].height = height;
                    objectsThrown[i].draw(canvas, paint);
                }

                // canvas.drawCircle(b.x, b.y, 50, paint);

                ship.draw(canvas, paint,"down");

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        asteroidView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        asteroidView.pause();
    }

}
