package com.nomenubar.lyncup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.*;

import org.json.JSONArray;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class UserServlet extends HttpServlet {
	
	// /user?id=12345
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		
		String id = (String) req.getParameter("id");
		String name = (String) req.getParameter("name");
		String pushkey = (String) req.getParameter("pushkey");
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Entity user = getUserEntity(ds, id);

		System.out.println("user: "+user);
		
		if(user == null) { // new user
			Entity e = new Entity("User");
			e.setProperty("id", id);
			ArrayList<String> friendsList = new ArrayList<String>();
			e.setProperty("friends", friendsList);
			e.setProperty("event", new Integer(-1));
			e.setProperty("name", name);
			e.setProperty("pushkey", pushkey);
			
			ds.put(e);
			printFriendsList(ds, friendsList, resp);
		}
		else { // user exists
			if(pushkey == null) { // display user's friends
				ArrayList<String> friendsList = (ArrayList<String>) user.getProperty("friends");
				if(friendsList == null) friendsList = new ArrayList<String>();
				printFriendsList(ds, friendsList, resp);
			}
			else { // update key
				user.setProperty("pushkey", pushkey);
				ds.put(user);
			}
			
		}
		

	}
	
	// print JSON of friends
	private void printFriendsList(DatastoreService ds, ArrayList<String> friendsList, HttpServletResponse resp) {
		JSONArray ja = new JSONArray();
		
		for(String fid : friendsList) {

			Entity friendEnt = getUserEntity(ds, fid);
		    if(friendEnt != null) {
		    	String friendName = (String)friendEnt.getProperty("name");
		    	String pushkey = (String)friendEnt.getProperty("pushkey");
		    	
		    	// put friendName and fEmail into JSON object
		    	
		    	HashMap<String, String> mymap = new HashMap<String, String>();
		    	
		    	
	    		mymap.put("id", fid);
	    		mymap.put("name", friendName);
		    	mymap.put("pushkey", pushkey);
		    	
		    	ja.put(mymap);
		    }
		}
		try {
	    	String output = ja.toString();
	    	
			resp.getWriter().print(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// find user
	public Entity getUserEntity(DatastoreService ds, String id) {
		Query q = new Query("User");
		PreparedQuery pq = ds.prepare(q);
		for(Entity e : pq.asIterable()) {
			if(e.getProperty("id").equals(id)) {
				return e;
			}
		}
		return null;
	}
	
	
	public Entity getConfigEntity(DatastoreService ds) {
		Query q = new Query("Config");
		PreparedQuery pq = ds.prepare(q);
		for(Entity e : pq.asIterable()) {
			return e;
		}
		
		return new Entity("Config");
	}
	
	
	
}
