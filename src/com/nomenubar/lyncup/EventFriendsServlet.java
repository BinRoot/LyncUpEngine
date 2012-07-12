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

@SuppressWarnings("serial")
public class EventFriendsServlet extends HttpServlet {
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		
		String id = (String) req.getParameter("id");
		String target = (String) req.getParameter("target");
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity user = getUserEntity(ds, id);
		
		if(target == null) {
			
			if(user!=null) {
				ArrayList<String> eventfriends = (ArrayList<String>)user.getProperty("eventfriends");
				
				if(eventfriends != null) {
					JSONArray ja = new JSONArray();
					for(String fid : eventfriends) {
						Entity fEnt = getUserEntity(ds, fid);
						double lat = (Double)fEnt.getProperty("lat");
						double lng = (Double)fEnt.getProperty("lng");
						String fname = (String)fEnt.getProperty("name");
						
						HashMap<String, Object> mymap = new HashMap<String, Object>();
			    		mymap.put("id", fid);
			    		mymap.put("name", fname);
			    		mymap.put("lat", lat);
			    		mymap.put("lng", lng);
				    	ja.put(mymap);
					}
					try {
				    	String output = ja.toString();
						resp.getWriter().print(output);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
		else {
			// add an eventfriend
			
			if(user!=null) {
				ArrayList<String> eventFriends = (ArrayList<String>)user.getProperty("eventfriends");
				
				if(eventFriends == null) {
					eventFriends = new ArrayList<String>();
				}
				
				eventFriends.add(target);
				user.setProperty("eventfriends", eventFriends);
				ds.put(user);
			}
			
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
