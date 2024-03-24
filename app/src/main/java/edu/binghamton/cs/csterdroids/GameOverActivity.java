package edu.binghamton.cs.csterdroids;
/*
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        TextView gameOverTextView = findViewById(R.id.game_over_text);
        if (gameOverTextView != null) {
            gameOverTextView.setText("Game Over");
        }
    }
}
*/

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        TextView gameOverTextView = findViewById(R.id.game_over_text);
        if (gameOverTextView != null) {
            gameOverTextView.setText("Game Over");
            // Set OnClickListener to the TextView
            gameOverTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click event to return to another screen
                    returnToMainScreen();
                }
            });
        }
    }

    // Method to return to another screen (main screen in this case)
    private void returnToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        // Add flags to clear the back stack and start the main activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        System.out.println("Hello");
        finish(); // Finish the current activity to remove it from the back stack
    }
}
