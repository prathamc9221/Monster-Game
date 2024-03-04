package edu.binghamton.cs.csterdroids;

// GameActivity.java

import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of GameView and set it as the content view
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the game when the activity is resumed
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game when the activity is paused
        gameView.pause();
    }
}

// GameView.java


