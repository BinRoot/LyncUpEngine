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
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		
		String id = (String) req.getParameter("id");
		String name = (String) req.getParameter("name");
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Entity user = getUserEntity(ds, id);
		
		if(user == null) { // new user
			Entity e = new Entity("User");
			e.setProperty("email", id);
			ArrayList<String> friendsList = new ArrayList<String>();
			e.setProperty("friends", friendsList);
			e.setProperty("name", name);
			e.setProperty("location", null);
			ds.put(e);
			printFriendsList(ds, friendsList, resp);
		}
		else { // user exists
			ArrayList<String> friendsList = (ArrayList<String>) user.getProperty("friends");
			printFriendsList(ds, friendsList, resp);
		}
		

	}
	
	// print JSON of friends
	private void printFriendsList(DatastoreService ds, ArrayList<String> friendsList, HttpServletResponse resp) {
		JSONArray ja = new JSONArray();
		
		for(String fEmail : friendsList) {

			Entity friendEnt = getUserEntity(ds, fEmail);
		    if(friendEnt != null) {
		    	String friendName = (String)friendEnt.getProperty("name");
		    	
		    	// put friendName and fEmail into JSON object
		    	
		    	HashMap<String, String> mymap = new HashMap<String, String>();
		    	
		    	
	    		mymap.put("id", fEmail);
	    		mymap.put("name", friendName);
		    	
		    	
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
			if(e.getProperty("email").equals(id)) {
				return e;
			}
		}
		return null;
	}
	
	
}
