package com.designame.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.StringTokenizer;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.designame.droidpf.DroidPFChannelController;
import com.designame.droidpf.DroidPFSingleChannel.IRLegoSingleAction;



public class ServerThread implements Runnable {

	private Context mycontext;
	private ServerSocket serversocket;
	
	private Socket clientsocket;
	private BufferedReader input;
	private OutputStream output;
	
	StringCutter cutter=new StringCutter();

	final static Handler mHandler = new Handler();

	boolean isAuthorized=false;
	boolean isRunning=false;
	
	private String host;
	private String authName;
	private String authPass;
	private int port;
	
	

	public ServerSocket getServersocket() {
		return serversocket;
	}

	public ServerThread(Context mycontext, String authName, String authPass,int port) {
		this.mycontext=mycontext;
		this.authName=authName;
		this.authPass=authPass;
		this.port = port;
		isRunning=true;
	}

	public void run() {
    	
        try {
            host		= getLocalIpAddress();
            
            
            serversocket = new ServerSocket(port);
            serversocket.setReuseAddress(true);
            
            while (isRunning) {
            	
                    clientsocket	= serversocket.accept();
                    input 			= new BufferedReader(new InputStreamReader(clientsocket.getInputStream(), "ISO-8859-2"));
                    output 			= clientsocket.getOutputStream(); 
                    
/*            		mHandler.post(new Runnable() {
            			@Override 
            			public void run() {
            				Utils.hint(mycontext, "new client connected FROM "+clientsocket.getInetAddress()+" "+clientsocket.getPort());
            		}});
*/                    

                    String sAll			= getStringFromInput(input);
                    final String header	= sAll.split("\n")[0];

                    StringTokenizer s = new StringTokenizer(header);
                    String type = s.nextToken();
                    String query = s.nextToken();
                    String protocol = s.nextToken();
                    
                    /*if (temp.equals("GET")) { //send picture if any
                    	
                    	String fileName = s.nextToken();
                        String localfile=fileName.replace(host+"/","").replace("/", "");
                        
                        InputStream content=Utils.openFileFromAssets(localfile, mycontext);
                        if (content!=null) {
                        	send(content, ContentType.getContentType(localfile));
                        } 
                    }*/
                    
                    
                    
                	if (type.equals("GET") && query.startsWith("/action")) {
                		s = new StringTokenizer(query,"?&");
                		String aux[];
                		
                		int channel = 0;
                		boolean red = false;
                		IRLegoSingleAction pwr = IRLegoSingleAction.FLOAT; 
                		int i = 0;
                		
                		
                		while(s.hasMoreTokens())
                		{
                			aux = s.nextToken().split("=");
                			
                			if(aux.length>1)
                			{
                				if(aux[0].equals("channel"))
                				{
                					channel = Integer.parseInt(aux[1]);
                					i++;
                				}
                				else if(aux[0].equals("red"))
                				{
                					red = Boolean.parseBoolean(aux[1]);
                					i++;
                				}
                				else if(aux[0].equals("pwr"))
                				{
                					if(aux[1].equals("forward"))
                					{
                						pwr = IRLegoSingleAction.FORWARD7;
                						i++;
                					}
                					else if(aux[1].equals("backward"))
                					{
                						pwr = IRLegoSingleAction.BACKWARD7;
                						i++;
                					}
                					else if(aux[1].equals("brake"))
                					{
                						pwr = IRLegoSingleAction.BRAKE;
                						i++;
                					}
                					else {
                						try
                						{
                							pwr = IRLegoSingleAction.getByValue(Integer.parseInt(aux[1]));
                						}catch (NumberFormatException ex)
                						{
                							
                						}
                					}
                						
                				}	
                			}	
                		}
                		
                		if(i==3)
                		{
                			DroidPFChannelController.getInstance().setChannelSingleValue(channel, red, pwr);
                			send("{result:true}","application/json");
                		}else
                		{
                			send("{result:false}","application/json");
                		}
                    	
                    	closeInputOutput();
                	} else {
                    	send("404 error");	
                    	closeInputOutput();
                    }
                	
		        }
        } catch (Exception ex) {
        	Log.e("doInBackground Exception", " "+ex);
        } finally
        {
        	try {
				serversocket.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
        }
        
        Log.e("out", "end");
        
	}
	
	void send(InputStream fis, String contenttype) {
		try {
		    String header=
		    		"HTTP/1.1 200 OK\n" +
		    		"Content-type: "+contenttype+"\n"+
		    		"Content-Length: "+fis.available()+"\n" +
		    		"\n";
	
		    output.write(header.getBytes());
		    
		    byte[] buffer = new byte[1024];
		    int bytes = 0;

		    while ((bytes = fis.read(buffer)) != -1) {
		    	output.write(buffer, 0, bytes);
		    }
		    output.flush();
		    
		} catch (Exception ex) {
			Log.e("exxx send", ex+"");
		}
	}

	void send(InputStream fis, String contenttype, OutputStream out, Socket socket) {
		try {
		    String header=
		    		"HTTP/1.1 200 OK\n" +
		    		"Content-type: "+contenttype+"\n"+
		    		"Content-Length: "+fis.available()+"\n" +
		    		"\n";
	
		    out.write(header.getBytes());
		    
		    byte[] buffer = new byte[1024];
		    int bytes = 0;

		    while ((bytes = fis.read(buffer)) != -1) {
		    	out.write(buffer, 0, bytes);
		    }

			out.close();
			socket.close();

		} catch (Exception ex) {
			Log.e("exx send", ex+"");
		}
	}
	
	
	void send(String s) {
	    send(s,"text/html");
	}
	
	void send(String s,String contentType ) {
	    String header=
	    		"HTTP/1.1 200 OK\n" +
	    		"Connection: close\n"+
	    		"Content-type: "+contentType+"; charset=utf-8\n"+
	    		"Content-Length: "+s.length()+"\n" +
	    		"\n";

	    try {
	    	output.write((header+s).getBytes());
	    	output.flush();
	    } catch (Exception ex) {
	    	Log.e("ex send", ex+"");
	    }
	}
	
	
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("ex getLocalIpAddress", ex.toString());
	    }
	    return null;
	}
	
	String getStringFromInput(BufferedReader input) {
        StringBuilder sb = new StringBuilder();
        String sTemp; 
        try {
			while (!(sTemp = input.readLine()).equals(""))  {
				sb.append(sTemp+"\n");
			}
		} catch (IOException e) {
			return "";
		}

        return sb.toString();
	}

	public void closeConnections() {
		try {
			closeInputOutput();
			serversocket.close();
		} catch (Exception ex) {
			Log.e("err closeConnections", ex+"");
		}
		
		isRunning=false;
	}
	
	void closeInputOutput() {
    	try {
    		input.close();
    		output.close();
			clientsocket.close();
		} catch (Exception ex) {
			Log.e("ex closeInputOutput", ex+"");
		}
	}

	public String getHost() {
		return host;
	}

	
};

