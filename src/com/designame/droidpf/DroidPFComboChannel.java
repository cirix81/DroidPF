package com.designame.droidpf;

import java.util.BitSet;

public class DroidPFComboChannel {
	
	
	private int channel;
	private boolean toggle;
	private boolean escape;
	private boolean address;
	private int mode;
	private IRLegoComboAction outputRed;
	private IRLegoComboAction outputBlue;
	private byte[] sound; 
	
	public DroidPFComboChannel(int channel) {
		super();
		this.channel = channel;
		toggle = true;
		address =escape =false;
		mode = 1;
		outputRed = IRLegoComboAction.FLOAT;
		outputBlue = IRLegoComboAction.FLOAT;
	}

	public void setOutputRed(IRLegoComboAction action)
	{
		outputRed = action;
	}

	public void setOutputBlue(IRLegoComboAction action) {
		outputBlue = action;
	}

	

	public byte[] getSound() {
		return sound;
	}

	public void createCommand()
	{
		BitSet bit = new BitSet(16);
		BitSet bit_xor = new BitSet(4);
		
		
		bit.set(15,toggle);
		bit.set(14,escape);
		if(((channel-1)&2)==2)
			bit.set(13,true);
		if(((channel-1)&1)==1)
			bit.set(12,true);
		bit.set(11,address);
		if ((mode&4)==4)
			bit.set(10,true);
		if ((mode&2)==2)
			bit.set(9,true);
		if ((mode&1)==1)
			bit.set(8,true);
		
		
		if (((outputBlue.ordinal())&2)==2)
			bit.set(7,true);
		if (((outputBlue.ordinal())&1)==1)
			bit.set(6,true);
		if (((outputRed.ordinal())&2)==2)
			bit.set(5,true);
		if (((outputRed.ordinal())&1)==1)
			bit.set(4,true);
		
		
		bit_xor.set(0,4);
		BitSet b = bit.get(12,16);
		bit_xor.xor(b);
		b = bit.get(8,12);
		bit_xor.xor(bit.get(8,12));
		b = bit.get(4,8);
		bit_xor.xor(bit.get(4,8));
		
		bit.or(bit_xor);
		
		
		sound = DroidPFGenerator.createCommand(bit);
		
	}
	

	public enum IRLegoComboAction
	{
		FLOAT ,
		FORWARD ,
		BACKWARD ,
		BRAKE 
	}
}
