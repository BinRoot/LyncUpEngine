package com.nomenubar.lyncup;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class EventServlet extends HttpServlet {
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		String op = (String) req.getParameter("op");

		if(op.equals("n")) { // return next event number
			long nextEvent = getNextEventId(ds);
			resp.getWriter().write(nextEvent+"");
		}
		
		if(op.equals("p")) { // putting users
			String friendsStr = (String) req.getParameter("friends"); // CSV of users
			Long eventid = Long.parseLong(req.getParameter("eventid"));
			
			Entity eventEnt = getEventEntity(ds, eventid);
			
			String userList[] = friendsStr.split(",");
			
			List<String> friends = Arrays.asList(userList);
			
			eventEnt.setProperty("friends", friends);
			ds.put(eventEnt);
		}
		
		if(op.equals("a")) { // adding a user
			String friendStr = (String) req.getParameter("friend"); // 
			Long eventid = Long.parseLong(req.getParameter("eventid"));
			
			Entity eventEnt = getEventEntity(ds, eventid);
			
			List<String> friend = (List<String>) eventEnt.getProperty("friend");
			if(friend == null) friend = new ArrayList<String>();
			friend.add(friendStr);
			eventEnt.setProperty("friend", friend);
			ds.put(eventEnt);
		}
		
		if(op.equals("r")) {
			Long eventid = Long.parseLong(req.getParameter("eventid"));
			
			Entity eventEnt = getEventEntity(ds, eventid);
			ArrayList<String> friends = (ArrayList<String>) eventEnt.getProperty("friends");
			if(friends == null) friends = new ArrayList<String>();
			
			StringWriter sw = new StringWriter();
			for(String s : friends) {
				sw.append(s);
				sw.append(",");
			}
			resp.getWriter().write(sw.toString());
		}
		
	}
	
	
	// find user
	public Entity getEventEntity(DatastoreService ds, long eventid) {
		Query q = new Query("Event");
		PreparedQuery pq = ds.prepare(q);
		for(Entity e : pq.asIterable()) {
			if(e.getProperty("eventid").equals(eventid)) {
				return e;
			}
		}
		
		Entity eventEnt = new Entity("Event");
		eventEnt.setProperty("eventid", eventid);
		
		return eventEnt;
		
	}
	
	public Entity getConfigEntity(DatastoreService ds) {
		Query q = new Query("Config");
		PreparedQuery pq = ds.prepare(q);
		for(Entity e : pq.asIterable()) {
			return e;
		}
		
		return new Entity("Config");
	}
	
	public long getNextEventId(DatastoreService ds) {
		Entity configEnt = getConfigEntity(ds);
		Long eventid = (Long) configEnt.getProperty("eventid");
		
		if(eventid == null) { // create an eventid starting at zero
			configEnt.setProperty("eventid", new Long(0));
			ds.put(configEnt);
			return 0;
		}
		else {
			configEnt.setProperty("eventid", new Long(eventid+1));
			ds.put(configEnt);
			return eventid;
		}
	}
}
