package service.event;

import java.sql.Date;
import java.util.ArrayList;

import model.event.EventModel;
import model.event.InvalidDateException;
import service.core.Event;
import service.core.User;

public class EventController {
	public Event createEvent(Event event) {
		EventModel model=new EventModel();
		event=model.createEvent(event);
		return event;
	}
	
	public ArrayList<Event> list(String filterType, String  username, String location, Date date, String host, boolean newEvents){
		return new EventModel().list(filterType, username, location, date, host, newEvents);
	}
	
	public boolean deleteEvent(int eventNo) {
		return new EventModel().delete(eventNo);
	}
	
	public Event updateEvent(int eventNo, Event event) {
		return new EventModel().updateEvent(eventNo, event);

	}
	
	public Event register(int eventNo, User user) {
		return new EventModel().register(eventNo, user);
	}
	public Event unregister(int eventNo, User user) {
		return new EventModel().unregister(eventNo, user);
	}
	public Event archive(int eventNo, User user) throws InvalidDateException {
		return new EventModel().archive(eventNo, user);
	}
	public Event unarchive(int eventNo, User user) throws InvalidDateException {
		return new EventModel().unarchive(eventNo, user);
	}
	public ArrayList<String> getRegisteredUsers(int eventNo) {
		return new EventModel().getRegisteredUsers(eventNo);

	}
	public Event read(int eventNo) {
		return new EventModel().read(eventNo);

	}
	public boolean share(Event event, ArrayList<User> users, User currentUser) {
		return new EventModel().share(event, users, currentUser);

	}
	public ArrayList<Event> getSharedEventsForUser(String username) {
		return new EventModel().getSharedEventsForUser(username);
	}
	public ArrayList<Event> getRegisteredEvents(String username) {
		return new EventModel().getRegisteredEvents(username);
	}
	
	

}
