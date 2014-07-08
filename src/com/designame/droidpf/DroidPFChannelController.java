package com.designame.droidpf;



public class DroidPFChannelController {
	
	private static class IRLegoChannelControllerHolder { 
        public static final DroidPFChannelController INSTANCE = new DroidPFChannelController();
    }

	public static DroidPFChannelController getInstance() {
	        return IRLegoChannelControllerHolder.INSTANCE;
	}

	
	private final DroidPFComboChannel channelsCombo[];
	private final DroidPFSingleChannel channelsSingle[];
	
	private DroidPFChannelController() {
		super();
		channelsCombo = new DroidPFComboChannel[4];
		channelsCombo[0] = new DroidPFComboChannel(1);
		channelsCombo[1] = new DroidPFComboChannel(2);
		channelsCombo[2] = new DroidPFComboChannel(3);
		channelsCombo[3] = new DroidPFComboChannel(4);
		
		channelsSingle = new DroidPFSingleChannel[8];
		channelsSingle[0] = new DroidPFSingleChannel(1,true);
		channelsSingle[1] = new DroidPFSingleChannel(1,false);
		channelsSingle[2] = new DroidPFSingleChannel(2,true);
		channelsSingle[3] = new DroidPFSingleChannel(2,false);
		channelsSingle[4] = new DroidPFSingleChannel(3,true);
		channelsSingle[5] = new DroidPFSingleChannel(3,false);
		channelsSingle[6] = new DroidPFSingleChannel(4,true);
		channelsSingle[7] = new DroidPFSingleChannel(4,false);
		
	}
	
	
	
	
	
	public void setChannelComboValue(int channel,boolean red, DroidPFComboChannel.IRLegoComboAction action)
	{
		channel--;
		DroidPFComboChannel c = channelsCombo[channel];
		synchronized(c)
		{
			
			if(red)
				c.setOutputRed(action);
			else
				c.setOutputBlue(action);
			c.createCommand();

			byte[] snd = new byte[c.getSound().length];
        	
            System.arraycopy(c.getSound(), 0, snd , 0, c.getSound().length);
            DroidPFPlayer.getInstance().AddSound(snd);
		}
		
	}
	
	public void setChannelSingleValue(int channel,boolean red, DroidPFSingleChannel.IRLegoSingleAction action)
	{
		channel--;
		DroidPFSingleChannel c = channelsSingle[channel*2 + (red?0:1)];
		synchronized(c)
		{
			
			c.setOutput(action);
			
			c.createCommand();

			byte[] snd = new byte[c.getSound().length];
        	
            System.arraycopy(c.getSound(), 0, snd , 0, c.getSound().length);
            DroidPFPlayer.getInstance().AddSound(snd);
		}
		
	}
	
	
	
	
}
