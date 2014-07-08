package com.designame.android.widget.joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {
        // =========================================
        // Private Members
        // =========================================
        private final boolean D = false;
        private final String TAG = "JoystickView";
        
        private Paint dbgPaint1;
        private Paint dbgPaint2;
        
        private Paint circlePaint;
        private Paint handlePaint;
        private int innerPadding;
        private int radius;
        private int handleRadius;
        private int handleInnerBoundaries;
        private JoystickMovedListener moveListener;
        private JoystickClickedListener clickListener;

        //# of pixels movement required between reporting to the listener
        private float moveResolution;

        private boolean yAxisInverted;
        
        //Max range of movement in user coordinate system
        public final static int CONSTRAIN_BOX = 0;
        public final static int CONSTRAIN_CIRCLE = 1;
        private int movementConstraint;
        private float movementRange;

        public final static int COORDINATE_CARTESIAN = 0;               //Regular cartesian coordinates
        public final static int COORDINATE_DIFFERENTIAL = 1;    //Uses polar rotation of 45 degrees to calc differential drive paramaters
        private int userCoordinateSystem;
        
        //Records touch pressure for click handling
        private float touchPressure;
        private boolean clicked;
        private float clickThreshold;
        
        //Last touch point in view coordinates
        private float touchX, touchY;
        
        //Last reported position in view coordinates (allows different reporting sensitivities)
        private float reportX, reportY;
        
        //Handle center in view coordinates
        private float handleX, handleY;
        
        //Center of the view in view coordinates
        private int px, py;

        //Size of the view in view coordinates
        private int dx, dy;

        //Cartesian coordinates of last touch point - joystick center is (0,0)
        private int cartX, cartY;
        
        //Polar coordinates of the touch point from joystick center
        private double radial;
        private double angle;
        
        //User coordinates of last touch point
        private int userX, userY;

        // =========================================
        // Constructors
        // =========================================

        public JoystickView(Context context) {
                super(context);
                initJoystickView();
        }

        public JoystickView(Context context, AttributeSet attrs) {
                super(context, attrs);
                initJoystickView();
        }

        public JoystickView(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
                initJoystickView();
        }

        // =========================================
        // Initialization
        // =========================================

        private void initJoystickView() {
                setFocusable(true);

                dbgPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
                dbgPaint1.setColor(Color.RED);
                dbgPaint1.setStrokeWidth(1);
                dbgPaint1.setStyle(Paint.Style.STROKE);
                
                dbgPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                dbgPaint2.setColor(Color.GREEN);
                dbgPaint2.setStrokeWidth(1);
                dbgPaint2.setStyle(Paint.Style.STROKE);
                
                circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                circlePaint.setColor(Color.GRAY);
                circlePaint.setStrokeWidth(1);
                circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

                handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                handlePaint.setColor(Color.DKGRAY);
                handlePaint.setStrokeWidth(1);
                handlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

                innerPadding = 10;
                
                setMovementRange(10);
                setMoveResolution(1.0f);
                setClickThreshold(0.4f);
                setYAxisInverted(true);
                setUserCoordinateSystem(COORDINATE_CARTESIAN);
        }

        public void setUserCoordinateSystem(int userCoordinateSystem) {
                if (userCoordinateSystem < COORDINATE_CARTESIAN || movementConstraint > COORDINATE_DIFFERENTIAL)
                        Log.e(TAG, "invalid value for userCoordinateSystem");
                else
                        this.userCoordinateSystem = userCoordinateSystem;
        }
        
        public int getUserCoordinateSystem() {
                return userCoordinateSystem;
        }
        
        public void setMovementConstraint(int movementConstraint) {
                if (movementConstraint < CONSTRAIN_BOX || movementConstraint > CONSTRAIN_CIRCLE)
                        Log.e(TAG, "invalid value for movementConstraint");
                else
                        this.movementConstraint = movementConstraint;
        }
        
        public int getMovementConstraint() {
                return movementConstraint;
        }
        
        public boolean isYAxisInverted() {
                return yAxisInverted;
        }
        
        public void setYAxisInverted(boolean yAxisInverted) {
                this.yAxisInverted = yAxisInverted;
        }
        
        /**
         * Set the pressure sensitivity for registering a click
         * @param clickThreshold threshold 0...1.0f inclusive. 0 will cause clicks to never be reported, 1.0 is a very hard click
         */
        public void setClickThreshold(float clickThreshold) {
                if (clickThreshold < 0 || clickThreshold > 1.0f)
                        Log.e(TAG, "clickThreshold must range from 0...1.0f inclusive");
                else
                        this.clickThreshold = clickThreshold;
        }
        
        public float getClickThreshold() {
                return clickThreshold;
        }
        
        public void setMovementRange(float movementRange) {
                this.movementRange = movementRange;
        }
        
        public float getMovementRange() {
                return movementRange;
        }
        
        public void setMoveResolution(float moveResolution) {
                this.moveResolution = moveResolution;
        }
        
        public float getMoveResolution() {
                return moveResolution;
        }
        
        // =========================================
        // Public Methods 
        // =========================================

        public void setOnJostickMovedListener(JoystickMovedListener listener) {
                this.moveListener = listener;
        }
        
        public void setOnJostickClickedListener(JoystickClickedListener listener) {
                this.clickListener = listener;
        }
        
        // =========================================
        // Drawing Functionality 
        // =========================================

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                // Here we make sure that we have a perfect circle
                int measuredWidth = measure(widthMeasureSpec);
                int measuredHeight = measure(heightMeasureSpec);
                int d = Math.min(measuredWidth, measuredHeight);

                handleRadius = (int)(d * 0.25);
                handleInnerBoundaries = handleRadius;
                
                px = d / 2;
                py = d / 2;
                dx = d;
                dy = d;
                radius = Math.min(px, py) - handleInnerBoundaries;

                setMeasuredDimension(d, d);
        }

        private int measure(int measureSpec) {
                int result = 0;
                // Decode the measurement specifications.
                int specMode = MeasureSpec.getMode(measureSpec);
                int specSize = MeasureSpec.getSize(measureSpec);
                if (specMode == MeasureSpec.UNSPECIFIED) {
                        // Return a default size of 200 if no bounds are specified.
                        result = 200;
                } else {
                        // As you want to fill the available space
                        // always return the full available bounds.
                        result = specSize;
                }
                return result;
        }

        @Override
        protected void onDraw(Canvas canvas) {
                int px = getMeasuredWidth() / 2;
                int py = getMeasuredHeight() / 2;
                int radius = Math.min(px, py);

                // Draw the background
                canvas.drawCircle(px, py, radius - innerPadding, circlePaint);

                // Draw the handle
                handleX = touchX + px;
                handleY = touchY + py;
                canvas.drawCircle(handleX, handleY, handleRadius, handlePaint);

                if (D) {
                        canvas.drawCircle(handleX, handleY, 3, dbgPaint1);
                        
                        if ( movementConstraint == CONSTRAIN_CIRCLE ) {
                                canvas.drawCircle(px, py, this.radius, dbgPaint1);
                        }
                        else {
                                canvas.drawRect(px-handleRadius, py-handleRadius, px+handleRadius, py+handleRadius, dbgPaint1);
                        }
                        
                        //Origin to touch point
                        canvas.drawLine(px, py, handleX, handleY, dbgPaint2);
                        
                        canvas.drawText(String.format("(%d,%d)", cartX, cartY), handleX-20, handleY-7, dbgPaint2);
                        canvas.drawText("("+ String.format("%.0f, %.1f", radial, angle * 57.2957795) + (char) 0x00B0 + ")", handleX-20, handleY+15, dbgPaint2);
                }

//              Log.d(TAG, String.format("touch(%f,%f)", touchX, touchY));
//              Log.d(TAG, String.format("onDraw(%.1f,%.1f)\n\n", handleX, handleY));
                canvas.save();
        }

        // Constrain touch within a box
        
        private void constrainBox() {
                touchX = Math.max(Math.min(touchX, radius), -radius);
                touchY = Math.max(Math.min(touchY, radius), -radius);
        }

        // Constrain touch within a circle
        private void constrainCircle() {
                float diffX = touchX;
                float diffY = touchY;
                double radial = android.util.FloatMath.sqrt((diffX*diffX) + (diffY*diffY));
                if ( radial > radius ) {
                        touchX = (int)((diffX / radial) * radius);
                        touchY = (int)((diffY / radial) * radius);
                }
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
               
	        		int actionType = event.getAction();
	                if (actionType == MotionEvent.ACTION_MOVE) {
	                        // Translate touch position to center of view
	                        touchX = (event.getX() - px);
	                        touchY = (event.getY() - py);
	
	                        reportOnMoved();
	                        invalidate();
	                        
	                        touchPressure = event.getPressure();
	                        reportOnPressure();
	                } 
	                else if (actionType == MotionEvent.ACTION_UP) {
	                        returnHandleToCenter();
	                        //Log.d(TAG, "X:" + touchX + "|Y:" + touchY);
	                }
                
                return true;
        }

        private void reportOnMoved() {
                if ( movementConstraint == CONSTRAIN_CIRCLE )
                        constrainCircle();
                else
                        constrainBox();

                calcUserCoordinates();

                if (moveListener != null) {
                        boolean rx = Math.abs(touchX - reportX) >= moveResolution;
                        boolean ry = Math.abs(touchY - reportY) >= moveResolution;
                        if (rx || ry) {
                                this.reportX = touchX;
                                this.reportY = touchY;
                                
//                              Log.d(TAG, String.format("moveListener.OnMoved(%d,%d)", (int)userX, (int)userY));
                                moveListener.OnMoved(userX, userY);
                        }
                }
        }

        private void calcUserCoordinates() {
                //First convert to cartesian coordinates
                cartX = (int)(touchX / radius * movementRange);
                cartY = (int)(touchY / radius * movementRange);
                
                radial = Math.sqrt((cartX*cartX) + (cartY*cartY));
                angle = Math.atan2(cartY, cartX);
                
                //Invert Y axis if requested
                if ( !yAxisInverted )
                        cartY  *= -1;
                
                if ( userCoordinateSystem == COORDINATE_CARTESIAN ) {
                        userX = cartX;
                        userY = cartY;
                }
                else if ( userCoordinateSystem == COORDINATE_DIFFERENTIAL ) {
                        userX = cartY + cartX / 4;
                        userY = cartY - cartX / 4;
                        
                        if ( userX < -movementRange )
                                userX = (int)-movementRange;
                        if ( userX > movementRange )
                                userX = (int)movementRange;

                        if ( userY < -movementRange )
                                userY = (int)-movementRange;
                        if ( userY > movementRange )
                                userY = (int)movementRange;
                }
                
        }
        
        //Simple pressure click
        private void reportOnPressure() {
//              Log.d(TAG, String.format("touchPressure=%.2f", this.touchPressure));
                if ( clickListener != null ) {
                        if ( clicked && touchPressure < clickThreshold ) {
                                clickListener.OnReleased();
                                this.clicked = false;
//                              Log.d(TAG, "reset click");
                                invalidate();
                        }
                        else if ( !clicked && touchPressure >= clickThreshold ) {
                                clicked = true;
                                clickListener.OnClicked();
//                              Log.d(TAG, "click");
                                invalidate();
                                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        }
                }
        }

        private void returnHandleToCenter() {

                Handler handler = new Handler();
                final int numberOfFrames = 5;
                final double intervalsX = (0 - touchX) / numberOfFrames;
                final double intervalsY = (0 - touchY) / numberOfFrames;

                for (int i = 0; i < numberOfFrames; i++) {
                        final int j = i;
                        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        touchX += intervalsX;
                                        touchY += intervalsY;
                                        
                                        reportOnMoved();
                                        invalidate();
                                        
                                        if (moveListener != null && j == numberOfFrames - 1) {
                                                moveListener.OnReturnedToCenter();
                                        }
                                }
                        }, i * 40);
                }

                if (moveListener != null) {
                        moveListener.OnReleased();
                }
        }
}