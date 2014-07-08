package com.designame.droidpf.widget;


import com.designame.android.widget.joystick.JoystickClickedListener;
import com.designame.android.widget.joystick.JoystickMovedListener;
import com.designame.android.widget.joystick.JoystickView;
import com.designame.droidpf.DroidPFChannelController;
import com.designame.droidpf.DroidPFSingleChannel;
import com.designame.droidpf.R;
import com.google.gson.Gson;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewAnimator;
import android.view.View.OnClickListener;

public class DroidPFChannelView extends LinearLayout implements OnClickListener,OnItemSelectedListener{

	private static final int INDEX_SETTINGS = 2; 
	private int channel;
	private boolean invertBlue;
	private boolean invertRed;
	private boolean invertAxis;
	private int control;
	
	private ViewAnimator animator;
	private Spinner controlSpinner,channelSpinner;
	
	
	
	
	public DroidPFChannelView(Context context, AttributeSet attrs) {
		super(context,attrs);
		if(!this.isInEditMode())
		{
			
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    inflater.inflate(R.layout.widget_channel,this, true);
	    	
			this.setOrientation(LinearLayout.VERTICAL);
	    	animator = (ViewAnimator)this.findViewById(R.id.animator);
		    animator.setInAnimation(this.getContext(), R.anim.swipe_up_in);
		    animator.setOutAnimation(this.getContext(), R.anim.swipe_up_out);

		    this.findViewById(R.id.TitleChannel).setOnClickListener(this);
		    

		    
		    // Create an ArrayAdapter for control spinner
		    channelSpinner = (Spinner) findViewById(R.id.channelSpinner);
	
		    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
			         R.array.channelItem, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			channelSpinner.setAdapter(adapter);
			channelSpinner.setOnItemSelectedListener(this);
		    
		    
			// Create an ArrayAdapter for control spinner
		    controlSpinner = (Spinner) findViewById(R.id.controlSpinner);
	
		    adapter = ArrayAdapter.createFromResource(this.getContext(),
			         R.array.controlItem, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			controlSpinner.setAdapter(adapter);
			controlSpinner.setOnItemSelectedListener(this);
			 
			JoystickView joystick = (JoystickView )this.findViewById(R.id.joystickView1);
	        joystick.setClickThreshold(0.115f);
	        joystick.setMovementRange(7);
	        joystick.setOnJostickMovedListener(listenerMoved);
	        joystick.setOnJostickClickedListener(listenerClicked);
	        
	        init(attrs);
		}
	    
	}
	
	
	
	 private JoystickMovedListener listenerMoved = new JoystickMovedListener() {

         @Override
         public void OnMoved(int pan, int tilt) {
        	 if(invertAxis)
        	 {
	        	 DroidPFChannelController.getInstance().setChannelSingleValue(channel,true,  DroidPFSingleChannel.IRLegoSingleAction.getByValue((invertRed?1:-1)*tilt));
	        	 DroidPFChannelController.getInstance().setChannelSingleValue(channel,false,  DroidPFSingleChannel.IRLegoSingleAction.getByValue((invertBlue?1:-1)*pan));
        	 }
        	 else
        	 {
        		 DroidPFChannelController.getInstance().setChannelSingleValue(channel,true,  DroidPFSingleChannel.IRLegoSingleAction.getByValue((invertRed?1:-1)*pan));
            	 DroidPFChannelController.getInstance().setChannelSingleValue(channel,false,  DroidPFSingleChannel.IRLegoSingleAction.getByValue((invertBlue?1:-1)*tilt));
        	 }
         }

         @Override
         public void OnReleased() {
        	 DroidPFChannelController.getInstance().setChannelSingleValue(channel,true,  DroidPFSingleChannel.IRLegoSingleAction.BRAKE);
        	 DroidPFChannelController.getInstance().setChannelSingleValue(channel,false,  DroidPFSingleChannel.IRLegoSingleAction.BRAKE);
         }
         
         public void OnReturnedToCenter() {
        	 DroidPFChannelController.getInstance().setChannelSingleValue(channel,true,  DroidPFSingleChannel.IRLegoSingleAction.BRAKE);
        	 DroidPFChannelController.getInstance().setChannelSingleValue(channel,false,  DroidPFSingleChannel.IRLegoSingleAction.BRAKE);
         };
 }; 


 private JoystickClickedListener listenerClicked = new JoystickClickedListener() {

		@Override
		public void OnClicked() {
			// TODO Auto-generated method stub
			//txtClick.setText("Clicked");
		}

		@Override
		public void OnReleased() {
			// TODO Auto-generated method stub
			//txtClick.setText("Release");
		}
 
 };

	@Override
	public void onClick(View v) {
		if(animator.getDisplayedChild() == INDEX_SETTINGS)
		{
			animator.setInAnimation(this.getContext(), R.anim.swipe_down_in);
		    animator.setOutAnimation(this.getContext(), R.anim.swipe_down_out);
			animator.setDisplayedChild(control);
		}
		else
		{
			channelSpinner.setSelection(channel-1);
			controlSpinner.setSelection(control);
			
			animator.setInAnimation(this.getContext(), R.anim.swipe_up_in);
		    animator.setOutAnimation(this.getContext(), R.anim.swipe_up_out);
			animator.setDisplayedChild(INDEX_SETTINGS);
		}
	}

	public boolean isInvertBlue() {
		return invertBlue;
	}

	public void setInvertBlue(boolean invertBlue) {
		this.invertBlue = invertBlue;
		((ToggleButton)findViewById(R.id.invertBlue)).setChecked(invertBlue);
		((DroidPFImageButton)findViewById(R.id.BDown)).setRevert(invertBlue);
		((DroidPFImageButton)findViewById(R.id.BUp)).setRevert(invertBlue);
	}

	public boolean isInvertRed() {
		return invertRed;
	}

	public void setInvertRed(boolean invertRed) {
		this.invertRed = invertRed;
		((ToggleButton)findViewById(R.id.invertRed)).setChecked(invertRed);
		((DroidPFImageButton)findViewById(R.id.RDown)).setRevert(invertRed);
		((DroidPFImageButton)findViewById(R.id.RUp)).setRevert(invertRed);
	}

	public boolean isInvertAxis() {
		return invertAxis;
	}

	public void setInvertAxis(boolean inverAxis) {
		this.invertAxis = inverAxis;
		//((ToggleButton)findViewById(R.id.invertAxis)).setChecked(invertAxis);
	}

	public int getControl() {
		return control;
	}

	public int getActualControl(){
		return animator.getDisplayedChild();
	}
	
	public void setActualControl(int control){
		animator.setDisplayedChild(control);
	}

	public void setControl(int control) {
		this.control = control;
		animator.setInAnimation(null);
	    animator.setOutAnimation(null);
		animator.setDisplayedChild(control);
		
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
		if(!this.isInEditMode())
		{
			((DroidPFImageButton)this.findViewById(R.id.BDown)).setChannel(channel);
			((DroidPFImageButton)this.findViewById(R.id.BUp)).setChannel(channel);
			((DroidPFImageButton)this.findViewById(R.id.RDown)).setChannel(channel);
			((DroidPFImageButton)this.findViewById(R.id.RUp)).setChannel(channel);
			((TextView)findViewById(R.id.TitleChannel)).setText(this.getContext().getString(R.string.channel)+ " " + channel);
		}
		
	}

	private void init(AttributeSet attrs) { 
		TypedArray a=getContext().obtainStyledAttributes(attrs,R.styleable.Channel);
		setChannel(a.getInt(R.styleable.Channel_channel,1));

	    //Don't forget this
	    a.recycle();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		if(animator.getDisplayedChild() == INDEX_SETTINGS)
		{
			int idView = parent.getId();
			if (idView == R.id.controlSpinner) {
				control = position;
			}
			else if (idView == R.id.channelSpinner) {
				setChannel(position +1); 
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		control = 0;
	}
	
	public void fromJson(String json)
	{
		Gson gson = new Gson();
		int intValue[] = new int[3];
		boolean boolValue[] = new boolean[3];
		
		String s[] = json.split("#");
		
		intValue = gson.fromJson(s[0], int[].class);
		boolValue = gson.fromJson(s[1], boolean[].class);
		
		
		setChannel(intValue[0]);
		setControl(intValue[1]);
		
		
		setInvertBlue( boolValue[0] );
		setInvertRed(boolValue[1]);
		setInvertAxis( boolValue[2]);
		animator.setInAnimation(null);
	    animator.setOutAnimation(null);
		animator.setDisplayedChild(intValue[2]);
	}
	
	public String toJson()
	{
		Gson gson = new Gson();
		int intValue[] = new int[3];
		boolean boolValue[] = new boolean[3];
		
		intValue[0]  = channel;
		intValue[1]  = control;
		intValue[2]  = animator.getDisplayedChild();
		
		boolValue[0] = invertBlue;
		boolValue[1] = invertRed;
		boolValue[2] = invertAxis;
		
		return gson.toJson(intValue) + "#" + gson.toJson(boolValue); 
	}
	
	
}
