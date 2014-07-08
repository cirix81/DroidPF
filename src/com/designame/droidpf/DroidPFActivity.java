package com.designame.droidpf;



import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.designame.multitouch.MultiTouchActivity;
import com.designame.net.ServerThread;

import com.designame.droidpf.R;
import com.designame.droidpf.widget.DroidPFChannelView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;;



public class DroidPFActivity extends MultiTouchActivity{
	
	public static final String PREFS_NAME = "ProfilePreference";
	ServerThread serverThread; 
	private SharedPreferences settings;
	private Menu menu;
	private int order;
	private DroidPFChannelView channels[];
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_lego);
        
        
        DroidPFGenerator.InitGenerator();

        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        
        channels = new DroidPFChannelView[4];
        channels[0] = (DroidPFChannelView)findViewById(R.id.channelView1);
        channels[1] = (DroidPFChannelView)findViewById(R.id.channelView2);
        channels[2] = (DroidPFChannelView)findViewById(R.id.channelView3);
        channels[3] = (DroidPFChannelView)findViewById(R.id.channelView4);
        
        
        new Thread(serverThread = new ServerThread(this, "", "",10937)).start();
        settings = getSharedPreferences(PREFS_NAME, 0);
        //settings.edit().clear().commit();
        DroidPFProfile.load(settings.getString("profiles", ""));
        
        if(savedInstanceState != null)
        {
        	String state; 
        	for (int i = 0 ; i < 4 ;i++)
        	{
        		state =savedInstanceState.getString("channel" + (i+1)); 
		        if(state != null && channels[i] != null)
		        {
		        	channels[i].fromJson(state);
		        }
	        	
	        }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

    	MenuInflater inflater = getSupportMenuInflater();
    	inflater.inflate(R.menu.principal, menu);
    	
    	order = 0;
    	
    	for (DroidPFProfile p : DroidPFProfile.getProfiles().values())
    	{
    		menu.findItem(R.id.profile_menu).getSubMenu().addSubMenu(R.id.list_profile_menu, Menu.NONE, order++, p.getName());
    	}
    	
    	menu.findItem(R.id.profile_menu).getSubMenu().setGroupCheckable(R.id.list_profile_menu, true, true);
    	

    	this.menu = menu;
        return true;
    }
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    	if(item.getGroupId()== R.id.list_profile_menu)
    	{
    		loadProfile( DroidPFProfile.getProfiles().get(item.getTitle()));
    		item.setChecked(true);
    		
    	}
    	else if (item.getItemId() == R.id.save)
    	{
    		saveProfile();
    	}
    	else if (item.getItemId() == R.id.clear)
    	{
    		getSharedPreferences(PREFS_NAME, 0).edit().clear().commit();
    		menu.findItem(R.id.profile_menu).getSubMenu().removeGroup(R.id.list_profile_menu);
    		
    	}
    	else if (item.getItemId() == R.id.ip_menu)
    	{
    		item.getSubMenu().removeGroup(R.id.list_ip_menu);
    		for(Object ip :getLocalIpAddress())
    		{
    			item.getSubMenu().add(R.id.list_ip_menu,Menu.NONE,Menu.NONE,ip.toString());
    		}
    		
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    
    
    private Object[] getLocalIpAddress() {
    	List<String> ip =new ArrayList<String>();
        try {
        	
        	
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ip.add(inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e(LOG_TAG, ex.toString());
        }
        return ip.toArray();
    }
    
    private void loadProfile(DroidPFProfile profile) {
    	
    	for(int i = 0 ; i < profile.getViews().length;i++ )
    	{
    		
    		DroidPFProfileView pv = profile.getViews()[i];
    		if (pv != null && channels[i] != null)
    		{
    			channels[i].setInvertRed(pv.isInvertRed());
    			channels[i].setInvertBlue(pv.isInvertBlue());
    			channels[i].setInvertAxis(pv.isInvertAxis());
    			channels[i].setControl(pv.getControl());
    			channels[i].setChannel(pv.getChannel());
    		}
    	}
		
	}

	private void saveProfile()
    {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Profile Name");
		alert.setMessage("Enter Profile Name");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			
			DroidPFProfile p = getActualProfile();
	    	p.setName(input.getText().toString());
	    	DroidPFProfile.add(p);
	    	
	    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    	SharedPreferences.Editor editor = settings.edit();
	    	editor.putString("profiles", DroidPFProfile.save());
	    	editor.commit();
	    	
	    	if(menu != null)
	    	{
	    		menu.findItem(R.id.profile_menu).getSubMenu().addSubMenu(R.id.list_profile_menu, Menu.NONE, order++, p.getName()).getItem().setChecked(true);
	    		menu.findItem(R.id.profile_menu).getSubMenu().setGroupCheckable(R.id.list_profile_menu, true, true);
	    	}
		  // Do something with value!
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
		// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog

    }
	
	private DroidPFProfile getActualProfile()
	{
		DroidPFProfile p = new DroidPFProfile();
		
    	//p.setName(input.getText().toString());
		p.setName("actual");
    	
    	DroidPFProfileView pv = new DroidPFProfileView();
    	DroidPFChannelView v = (DroidPFChannelView)findViewById(R.id.channelView1);
    	p.getViews()[0] = setProfileFormView(pv, v);
    	
    	pv = new DroidPFProfileView();
    	v = (DroidPFChannelView)findViewById(R.id.channelView2);
    	p.getViews()[1] = setProfileFormView(pv, v);
    	
    	
    	v = (DroidPFChannelView)findViewById(R.id.channelView3);
    	if(v!=null)
    	{
    		pv = new DroidPFProfileView();        	
        	p.getViews()[2] = setProfileFormView(pv, v);
        	
        	
    	}
    	
    	v = (DroidPFChannelView)findViewById(R.id.channelView4);
    	if(v!=null)
    	{
	    	pv = new DroidPFProfileView();
	    	p.getViews()[3] = setProfileFormView(pv, v);
    	}
    	return p;
	}
    
    private DroidPFProfileView setProfileFormView(DroidPFProfileView pv,DroidPFChannelView view )
    {
    	pv.setChannel(view.getChannel());
    	pv.setIdView(view.getId());
    	pv.setInvertRed(view.isInvertRed());
    	pv.setInvertBlue(view.isInvertBlue());
    	pv.setInvertAxis(view.isInvertAxis());
    	pv.setControl(view.getControl());
    	return pv;
    }
    
   
    
   
	@Override
    public void onDestroy() {
    	super.onDestroy();
    	try {
			if (serverThread.getServersocket()!=null) {
				serverThread.closeConnections();
			} else {
				Log.e("out", "serversocket null");
			}
		} catch (Exception ex) {
			Log.e("ex", ""+ex);
		}
    }
	
	

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Always call the superclass so it can save the view hierarchy state
		
		
		savedInstanceState.putString("channel1",channels[0].toJson());
		savedInstanceState.putString("channel2",channels[1].toJson());
		if(channels[2]!=null)
		{
			savedInstanceState.putString("channel3",channels[2].toJson());
			
		}
		if(channels[3]!=null)
		{
			savedInstanceState.putString("channel4",channels[3].toJson());
		}
	    super.onSaveInstanceState(savedInstanceState);
	}
    
	

}