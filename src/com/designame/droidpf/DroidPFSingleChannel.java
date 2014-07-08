package com.designame.droidpf;

import java.util.BitSet;

public class DroidPFSingleChannel {
	
	
	private int channel;
	/*private boolean toggle;
	private boolean escape;
	private boolean address;
	private int mode;*/
	private IRLegoSingleAction output;
	
	private byte[] sound;
	
	private final BitSet bit ;
	
	public DroidPFSingleChannel(int channel,boolean red) {
		super();
		bit = new BitSet(16);
		
		bit.set(15);//toggle
		
		if(((channel-1)&2)==2)
			bit.set(13);
		if(((channel-1)&1)==1)
			bit.set(12);
		
		
		bit.set(10);
		if(!red)
			bit.set(8);
			
		
		this.channel = channel;
		
		
		output = IRLegoSingleAction.FLOAT;
	}

	public void setOutput(IRLegoSingleAction action)
	{
		output = action;
	}

	

	

	public byte[] getSound() {
		return sound;
	}

	public void createCommand()
	{
		
		BitSet bit_xor = new BitSet(4);
				
		bit.clear(0,8);
		bit.or(output.bits);
		
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
	

	public enum IRLegoSingleAction
	{
		FLOAT(false,false,false,false),
		FORWARD1(false,false,false,true),
		FORWARD2(false,false,true,false),
		FORWARD3(false,false,true,true),
		FORWARD4(false,true,false,false),
		FORWARD5(false,true,false,true),
		FORWARD6(false,true,true,false),
		FORWARD7(false,true,true,true),
		BRAKE(true,false,false,false),
		BACKWARD7(true,false,false,true),
		BACKWARD6(true,false,true,false),
		BACKWARD5(true,false,true,true),
		BACKWARD4(true,true,false,false),
		BACKWARD3(true,true,false,true),
		BACKWARD2(true,true,true,false),
		BACKWARD1(true,true,true,true);
		
		private final BitSet bits;
		
		private IRLegoSingleAction(boolean bit3,boolean bit1,boolean bit2,boolean bit0) {
			bits = new BitSet(8);
			bits.set(4, bit0);
			bits.set(5, bit1);
			bits.set(6, bit2);
			bits.set(7, bit3);
		}
		
		public static IRLegoSingleAction getByValue(int value)
		{
			switch(value)
			{
				case 7 : return FORWARD7;
				case 6 : return FORWARD6;
				case 5 : return FORWARD5;
				case 4 : return FORWARD4;
				case 3 : return FORWARD3;
				case 2 : return FORWARD2;
				case 1 : return FORWARD1;
				case 0 : return BRAKE;
				case -1 : return BACKWARD1;
				case -2 : return BACKWARD2;
				case -3 : return BACKWARD3;
				case -4 : return BACKWARD4;
				case -5 : return BACKWARD5;
				case -6 : return BACKWARD6;
				case -7 : return BACKWARD7;
				default :return FLOAT; 
			}
			
		}
		/*public IRLegoSingleAction getByValue(int value)
		{
			//return new IRLegoSingleAction()
		}*/
	}
}

