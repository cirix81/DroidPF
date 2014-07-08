package com.designame.droidpf;

import java.util.BitSet;



public class DroidPFGenerator {
	
	
	private static double[] sample;
	private final static int startlength = 49;
	private final static int lowlength = 20;
	private final static int highlength = 31;
	private static double[] start,low,high;
	
	
	
	public static void InitGenerator() {
		// TODO Auto-generated constructor stub
		start = new double[startlength];
		low = new double[lowlength];
		high = new double[highlength];
		
		
		double max = 0;
		
		sample = new double[6];
		for (int i = 0; i <6 ;i++)
		{
			sample[i] = Math.sin(2 * Math.PI * i / ((double)DroidPFPlayer.SAMPLE_RATE/DroidPFPlayer.FREQ_OF_TONE));
			max=Math.max(max, Math.abs( sample[i]));
		}		
		
		for (int i = 0; i <6 ;i++)
		{
			sample[i] = sample[i] * (1/max);
		}
		
		
		System.arraycopy(sample, 0, start, 0, 6);
		System.arraycopy(sample, 0, low, 0, 6);
		System.arraycopy(sample, 0, high, 0, 6);
	}
	
	public static byte[] createCommand(BitSet command)
	{
		
		
		int lenght = 2*startlength + command.cardinality()*highlength + (16-command.cardinality())*lowlength;
		
		double[] b = new double[lenght];
		
		int offset = 0;
		
		System.arraycopy(start, 0, b, offset, startlength);//S
		offset +=startlength;
		
		
		for (int j = 15 ; j >=0 ;j--)
		{
			if( command.get(j) )
			{
				System.arraycopy(high, 0, b, offset, highlength);//1
				offset +=highlength;
			}
			else
			{
				System.arraycopy(low, 0, b, offset, lowlength);//1
				offset +=lowlength;
			}
				
			
		}
	
		
		
		System.arraycopy(start, 0, b, offset, startlength);//S
		offset +=startlength;
		
		int pp = 522;
		
		int length_total = lenght*5 +3*pp + 10*pp + 16*pp;
		
		byte generatedSnd[] = new byte[4 * length_total];
		
		int idx = 0;
		for (int i = 0 ; i <3*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
		
		
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        

		for (int i = 0 ; i <5*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
        
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        
        for (int i = 0 ; i <5*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
        
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        
        for (int i = 0 ; i <8*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
        
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        
        for (int i = 0 ; i <8*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
        
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        
        
		return generatedSnd;
		
	}
	
	
	
	public byte[] sendCommand(int command)
	{
		
		int lenght = 2*startlength + 12*lowlength + 4*highlength;		
		double[] b = new double[lenght];
		
		int offset = 0;
		
		System.arraycopy(start, 0, b, offset, startlength);//S
		offset +=startlength;
		System.arraycopy(high, 0, b, offset, highlength);//1
		offset +=highlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(high, 0, b, offset, highlength);//1
		offset +=highlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(high, 0, b, offset, highlength);//1
		offset +=highlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(high, 0, b, offset, highlength);//1
		offset +=highlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(low, 0, b, offset, lowlength);//0
		offset +=lowlength;
		System.arraycopy(start, 0, b, offset, startlength);//S
		//offset +=startlength;
		
		int pp = 522;
		
		int length_total = lenght*5 +3*pp + 10*pp + 16*pp;
		
		byte generatedSnd[] = new byte[4 * length_total];
		
		int idx = 0;
		for (int i = 0 ; i <3*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
		
		
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        

		for (int i = 0 ; i <5*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
        
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        
        for (int i = 0 ; i <5*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
        
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        
        for (int i = 0 ; i <8*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
        
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        
        for (int i = 0 ; i <8*pp;i++)
		{
			generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) (0 & 0x00ff);
            generatedSnd[idx++] = (byte) ((0 & 0xff00) >>> 8);

		}
        
		
        for (final double dVal : b) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            generatedSnd[idx++] = (byte) ((-val) & 0x00ff);
            generatedSnd[idx++] = (byte) (((-val) & 0xff00) >>> 8);

           
        }
        
        
		return generatedSnd;
	}
	
	
	
	
}
