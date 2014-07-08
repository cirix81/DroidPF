package com.designame.droidpf;

import java.io.IOException;

import com.google.gson.stream.JsonReader;

public class DroidPFProfileView {
	private int idView;
	private int channel;
	private boolean invertBlue;
	private boolean invertRed;
	private boolean invertAxis;
	private int control;
	
	
	
	public int getIdView() {
		return idView;
	}
	public void setIdView(int idView) {
		this.idView = idView;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public boolean isInvertBlue() {
		return invertBlue;
	}
	public void setInvertBlue(boolean invertBlue) {
		this.invertBlue = invertBlue;
	}
	public boolean isInvertRed() {
		return invertRed;
	}
	public void setInvertRed(boolean invertRed) {
		this.invertRed = invertRed;
	}
	public boolean isInvertAxis() {
		return invertAxis;
	}
	public void setInvertAxis(boolean invertAxis) {
		this.invertAxis = invertAxis;
	}
	public int getControl() {
		return control;
	}
	public void setControl(int control) {
		this.control = control;
	}
	public void fromJson(JsonReader reader) throws IOException {
		// TODO Auto-generated method stub
		reader.beginObject();
		 while (reader.hasNext()) {
			 String name = reader.nextName();
			 if (name.equals("idView")) {
				 this.idView = reader.nextInt();
			 } else if (name.equals("channel")) {
				 this.channel = reader.nextInt();
			 }else if (name.equals("invertBlue")) {
				 this.invertBlue = reader.nextBoolean();
			 }else if (name.equals("invertRed")) {
				 this.invertRed = reader.nextBoolean();
			 }else if (name.equals("invertAxis")) {
				 this.invertAxis = reader.nextBoolean();
			 }else if (name.equals("control")) {
				 this.control = reader.nextInt();
			 }
			 
		 }
		 reader.endObject();
	}
	
	

	
	
}



