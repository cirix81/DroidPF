package com.designame.droidpf;

import java.util.Stack;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class DroidPFPlayer extends Thread {
	public final static int SAMPLE_RATE = 44100;
	public final static int FREQ_OF_TONE = 19000;
	
    
	//public final static IRLegoPlayer = 
			 
    private DroidPFPlayer() {
    	this.start();
    }
    
    
    private static class IRLegoPlayerHolder { 
        public static final DroidPFPlayer INSTANCE = new DroidPFPlayer();
    }

	public static DroidPFPlayer getInstance() {
	        return IRLegoPlayerHolder.INSTANCE;
	}
    
	Stack<byte[]> sounds  = new Stack<byte[]>();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		int minSize = AudioTrack.getMinBufferSize( 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT );
		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				 	SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
	                AudioFormat.ENCODING_PCM_16BIT, minSize*4,
	                AudioTrack.MODE_STATIC);
		 
		while(true)
		{
			synchronized (sounds) {
				while(sounds.isEmpty())
				{
					try {
						sounds.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						Thread.interrupted();
					}
				}
				byte[] s= sounds.pop();
				
				if(audioTrack.getState() == AudioTrack.STATE_INITIALIZED)
				{
					audioTrack.stop();
					audioTrack.flush();					
					audioTrack.reloadStaticData();
				}
		        audioTrack.write(s, 0, s.length);
		        audioTrack.play();
		        
		        
		        
		        
			}
		}
	}
	
	
	public void AddSound(byte[] sound)
	{
		synchronized (sounds) {
            sounds.push(sound);
            sounds.notifyAll();
		}
	    
	}
	
}
