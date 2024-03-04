package edu.binghamton.cs.csterdroids;

import android.graphics.Canvas;
import android.graphics.Paint;

public class boulder {
    float x, y, dx, dy, side;
    float width, height;

    /*public void update()
    {
        x += dx;
        y += dy;
        if (x < 0) dx = -dx;
        if (y < 0) dy = -dy;
        if (x > width) dx = -dx;
        if (y > height) dy = -dy;
    }*/

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

    float halfSide = side / 2;


    public void draw(Canvas canvas, Paint paint)
    {
       //canvas.drawCircle(x, y, diameter, paint);
        canvas.drawRect(x - side, y - side, x + side, y + side, paint);
    }

}

/*
*
* public class Boulder {
    float x, y, dx, dy, sideLength;
    float width, height;

    public void update() {
        x += dx;
        y += dy;

        // Check for collisions with screen boundaries
        if (x < 0 || x > width) {
            dx = -dx; // Reverse the direction in x-axis
        }
        if (y < 0 || y > height) {
            dy = -dy; // Reverse the direction in y-axis
        }

        // Check for collisions with other squares (assuming another square's position is stored in otherX and otherY)
        float halfSide = sideLength / 2;

        // Check for collision in x-axis
        if (x - halfSide < otherX + halfSide &&
            x + halfSide > otherX - halfSide &&
            y - halfSide < otherY + halfSide &&
            y + halfSide > otherY - halfSide) {

            // Reverse the direction in x-axis
            dx = -dx;
        }

        // Check for collision in y-axis
        if (x - halfSide < otherX + halfSide &&
            x + halfSide > otherX - halfSide &&
            y - halfSide < otherY + halfSide &&
            y + halfSide > otherY - halfSide) {

            // Reverse the direction in y-axis
            dy = -dy;
        }
    }

    public void setOtherSquarePosition(float otherX, float otherY) {
        this.otherX = otherX;
        this.otherY = otherY;
    }

    // ... (rest of the class remains the same)

    private float otherX, otherY;
}

* */