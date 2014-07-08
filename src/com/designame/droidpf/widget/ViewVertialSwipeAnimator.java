package com.designame.droidpf.widget;

import com.designame.droidpf.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ViewAnimator;

public class ViewVertialSwipeAnimator extends ViewAnimator implements OnGestureListener {

	private static final int SWIPE_MIN_DISTANCE = 120;
   private static final int SWIPE_MAX_OFF_PATH = 250;
   private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureScanner;
	
	public ViewVertialSwipeAnimator(Context context, AttributeSet attrs) {
		super(context, attrs);
		gestureScanner = new GestureDetector(this);
		this.setInAnimation(this.getContext(), R.anim.swipe_up_in);
 	   	this.setOutAnimation(this.getContext(), R.anim.swipe_up_out);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(!gestureScanner.onTouchEvent(event))
			return super.onTouchEvent(event);
		return true;
	}
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		 try {
	           if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	               return false;
	           // right to left swipe
	           if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	               //Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
	               return false;
	           }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	               //Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
	               return false;
	           }
	           else if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
	               //Toast.makeText(getApplicationContext(), "Swipe up", Toast.LENGTH_SHORT).show();
	        	   this.setInAnimation(this.getContext(), R.anim.swipe_up_in);
	        	   this.setOutAnimation(this.getContext(), R.anim.swipe_up_out);
	        	   this.showNext();
	           }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
	               //Toast.makeText(getApplicationContext(), "Swipe down", Toast.LENGTH_SHORT).show();
	        	   this.setInAnimation(this.getContext(), R.anim.swipe_down_in);
	        	   this.setOutAnimation(this.getContext(), R.anim.swipe_down_out);
	        	   this.showPrevious();
	        	   
	           }
	       } catch (Exception e) {
	           // nothing
	       }

		 return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
