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
public class UpdateServlet extends HttpServlet {
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		
		String id = (String) req.getParameter("id");
		double lat = Double.parseDouble(req.getParameter("lat"));
		double lng = Double.parseDouble(req.getParameter("lng"));
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Entity user = getUserEntity(ds, id);

		if(user != null) { // new user
			user.setProperty("lat", lat);
			user.setProperty("lng", lng);
			ds.put(user);
		}
		

	}
	
	// print JSON of friends
	private void printFriendsList(DatastoreService ds, ArrayList<String> friendsList, HttpServletResponse resp) {
		JSONArray ja = new JSONArray();
		
		for(String fid : friendsList) {

			Entity friendEnt = getUserEntity(ds, fid);
		    if(friendEnt != null) {
		    	String friendName = (String)friendEnt.getProperty("name");
		    	
		    	// put friendName and fEmail into JSON object
		    	
		    	HashMap<String, String> mymap = new HashMap<String, String>();
		    	
		    	
	    		mymap.put("id", fid);
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
			if(e.getProperty("id").equals(id)) {
				return e;
			}
		}
		return null;
	}
	
	
}
