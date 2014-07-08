package com.designame.droidpf.widget;


import com.designame.droidpf.DroidPFChannelController;
import com.designame.droidpf.DroidPFSingleChannel;
import com.designame.droidpf.R;
import com.designame.droidpf.DroidPFSingleChannel.IRLegoSingleAction;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

public class DroidPFImageButton extends ImageButton  {

	private int channel;
	private String action;
	private boolean red;
	private boolean revert;
	
	public DroidPFImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		revert = false;
		init(attrs);
	}

	private void init(AttributeSet attrs) { 
		TypedArray a=getContext().obtainStyledAttributes(
		         attrs,
		         R.styleable.Channel);
		
		action = a.getString(R.styleable.Channel_action);
		red = a.getBoolean(R.styleable.Channel_red, false);

	    //Don't forget this
	    a.recycle();
	}
	
	
	public void setChannel(int channel) {
		this.channel = channel;
	}


	

	public boolean isRevert() {
		return revert;
	}

	public void setRevert(boolean revert) {
		this.revert = revert;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {	
			MotionThread t = new MotionThread(this, event);
			t.start();
			return false;	    	
		}
		
		private class MotionThread extends Thread
		{
			private DroidPFImageButton v;
			private MotionEvent motion;
			
			public MotionThread(DroidPFImageButton v, MotionEvent motion) {
				super();
				this.v = v;
				this.motion = motion;
			}

			@Override
			public void run() {
				
    		 	DroidPFSingleChannel.IRLegoSingleAction singleAction;
    		 	
    		 	int action = motion.getAction();
				switch(action)
		    	{
		    		case MotionEvent.ACTION_DOWN :
		    		case MotionEvent.ACTION_POINTER_DOWN :
		    			 //final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                         //final int pointerId = motion.getPointerId(pointerIndex);
		                    	 
		    			singleAction = revert?(v.action.equals("forward")?IRLegoSingleAction.FORWARD7:IRLegoSingleAction.BACKWARD7):(v.action.equals("forward")?IRLegoSingleAction.BACKWARD7:IRLegoSingleAction.FORWARD7);
		            	DroidPFChannelController.getInstance().setChannelSingleValue(v.channel, v.red, singleAction);
		                    	
		                    	
		                    	
		                    
		                
		                break;
		    		case MotionEvent.ACTION_UP:		 
		    		case MotionEvent.ACTION_POINTER_UP:
		    			DroidPFChannelController.getInstance().setChannelSingleValue(v.channel, v.red, IRLegoSingleAction.BRAKE);
		                break;    	

		        
			}
		}
    
    }
	
	
	
}
