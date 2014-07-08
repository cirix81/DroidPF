package com.designame.droidpf;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;




public class DroidPFProfile {
	private static HashMap<String,DroidPFProfile> profiles;
	private DroidPFProfileView views[];
	private String name;
	
	
	public DroidPFProfile() {
		super();
		views = new DroidPFProfileView[4];
	}
	
	
	public static HashMap<String, DroidPFProfile> getProfiles() {
		return profiles;
	}


	public static void setProfiles(HashMap<String, DroidPFProfile> profiles) {
		DroidPFProfile.profiles = profiles;
	}





	public DroidPFProfileView[] getViews() {
		return views;
	}


	public void setViews(DroidPFProfileView[] views) {
		this.views = views;
	}


	public String getName() {
		return name;
	}





	public void setName(String name) {
		this.name = name;
	}





	public static void load(String json) 
	{
		profiles = new HashMap<String, DroidPFProfile>();
		
		JsonReader reader = new JsonReader(new StringReader(json));
        
		
		
        try {
        	
			reader.beginArray();
		
	        while (reader.hasNext()) {
	        	
	            DroidPFProfile p = new DroidPFProfile ();
	            p.fromJson(reader);
	            profiles.put(p.name,p);
	        }
	        reader.endArray();
	        reader.close();
	        
        } catch (Exception e) {
			profiles.clear();
		}
		
		
		
	}
	
	
	private void fromJson(JsonReader reader) throws IOException {
		 reader.beginObject();
		 while (reader.hasNext()) {
			 String name = reader.nextName();
			 if (name.equals("name")) {
				 this.name = reader.nextString();
			 } else if (name.equals("views")) {
				 reader.beginArray();
				 int i = 0;
				 while (reader.hasNext() && reader.peek() != JsonToken.END_ARRAY) {
					 if(reader.peek() != JsonToken.NULL)
					 {
					 views[i] = new DroidPFProfileView();
					 views[i].fromJson(reader);
					 
					 }
					 else
					 {
						 reader.nextNull();
					 }
					 i++;
				 }
				 reader.endArray();
			 }
		 }
		 reader.endObject();
		
		
	}

	
	public static DroidPFProfile fromJson(String json) {
		 JsonReader reader = new JsonReader(new StringReader(json));
		 DroidPFProfile p = new DroidPFProfile ();
         try {
			p.fromJson(reader);
			reader.close();
			return p;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}		
	}


	public static String save()
	{
		Gson gson = new Gson();
		return gson.toJson(profiles.values());
	}
	
	public static void add(DroidPFProfile profile)
	{
		if(profiles.containsKey(profile.name))
		{
			profiles.remove(profile.name);
		}
		profiles.put(profile.name, profile);
	}


	public String toJson() {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}



