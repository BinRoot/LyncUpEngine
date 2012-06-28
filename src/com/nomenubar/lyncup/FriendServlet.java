package com.nomenubar.lyncup;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class FriendServlet extends HttpServlet {
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		
		String id = (String) req.getParameter("id");
		String friend = (String) req.getParameter("friend");
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Entity user = getUserEntity(ds, id);
		if(user!=null) {

			ArrayList<String> friends = (ArrayList<String>)user.getProperty("friends");
			
			if(friends == null) {
				friends = new ArrayList<String>();
			}

			friends.add(friend);
			user.setProperty("friends", friends);
			ds.put(user);
			
			resp.getWriter().println("success!");
			
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
