package client;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import service.core.Credentials;
import service.core.Event;
import service.core.EventShareWrapper;
import service.core.User;
import service.event.EventController;
import service.user.UserController;

/**
 * Implementation of the Apex REST layer that receives all the calls.
 * 
 * @author Sagar
 *
 */
@RestController
public class Client {

	@RequestMapping(value = "/event", method = RequestMethod.POST)
	public Event createEvent(@RequestBody Event event) {
		UserController userController = new UserController();
		User user = userController.getProfile(event.getHost().getUsername());
		event.setHost(user);
		EventController eventController = new EventController();
		event = eventController.createEvent(event);
		// logic to get user based on session Id.
		// save events to db
		return event;
	}

	@RequestMapping(value = "/event/{eventNo}", method = RequestMethod.DELETE)
	public String deleteEvent(@PathVariable("eventNo") int eventNumber) throws NoSuchEventException {
		EventController eventController = new EventController();
		boolean deleted = eventController.deleteEvent(eventNumber);
		if (!deleted) {
			throw new NoSuchEventException();
		}
		// save events to db
		return "Removed";
	}

	// Uodate event
	@RequestMapping(value = "/event/{eventNo}", method = RequestMethod.PUT)
	public Event updateEvent(@PathVariable("eventNo") int eventNumber, @RequestBody Event event)
			throws NoSuchEventException {
		UserController userController = new UserController();
		User user = userController.getProfile(event.getHost().getUsername());
		event.setHost(user);
		EventController eventController = new EventController();
		event = eventController.updateEvent(eventNumber, event);
		if (event == null) {
			throw new NoSuchEventException();
		}
		// save events to db
		return event;
	}

	// Reads a particular event using the eventNo.
	@RequestMapping(value = "/event/{eventNo}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.CREATED)
	public Event getEvent(@PathVariable("eventNo") int eventNo) throws NoSuchEventException {
		// get events from db for eventNo
		EventController eventController = new EventController();
		Event event = eventController.read(eventNo);
		if (event == null)
			throw new NoSuchEventException();
		return event;
	}

	// Lists all the events.
	@RequestMapping(value = "/events", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.CREATED)
	public ArrayList<Event> listEvents(@RequestParam(value = "filterType", required = false) String filterType,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "date", required = false) Date date,
			@RequestParam(value = "host", required = false) String host,
			@RequestParam(value = "newEvents", required = false) boolean newEvents) {
		ArrayList<Event> list = new ArrayList<>();
		// fetch events from db
		list = new EventController().list(filterType, username, location, date, host, newEvents);
		return list;
	}

	// Shares a particular event using the eventId with a user or group of users.
	@RequestMapping(value = "/event/share", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public boolean shareEvent(@RequestBody EventShareWrapper shareWrapper)
			throws NoSuchUserException, NoSuchEventException {
		// fetch user profile
		// User user=fetchUser(username);
		// emailSent=email(users, event, currentUser);
		// store mapping in database
		User currentUser = new UserController().getProfile(shareWrapper.getCurrentUsername());

		ArrayList<User> users = new ArrayList<User>();
		for (String username : shareWrapper.getUsernames()) {
			UserController userController = new UserController();
			User u = userController.getProfile(username);
			if (u == null) {
				throw new NoSuchUserException();
			}
			users.add(u);
		}
		boolean shared = false;
		Event event = new EventController().read(shareWrapper.getEventNo());
		if (event == null) {
			throw new NoSuchEventException();
		}
		shared = new EventController().share(event, users, currentUser);
		return shared;
	}

	@RequestMapping(value = "/event/share/{username}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.CREATED)
	public ArrayList<Event> getSharedEventsForUser(@PathVariable("username") String username)
			throws NoSuchUserException {
		// get events from db for eventNo
		UserController userController = new UserController();
		User u = userController.getProfile(username);
		if (u == null) {
			throw new NoSuchUserException();
		}
		EventController eventController = new EventController();
		ArrayList<Event> events = eventController.getSharedEventsForUser(username);
		return events;
	}

	@RequestMapping(value = "/events/registeredUsers/{eventId}", method = RequestMethod.GET)
	public List<String> getRegisteredUsers(@PathVariable("eventId") int eventId) throws NoSuchEventException {
		// get events from db for eventNo
		EventController eventController = new EventController();
		ArrayList<String> users = eventController.getRegisteredUsers(eventId);
		return users;
	}

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public User signup(@RequestBody User user) throws UserCreationFailedException {
		UserController userController = new UserController();

		User createdUser = userController.createUser(user);
		if (createdUser == null) {
			throw new UserCreationFailedException();
		}
		// send to db
		return user;
	}

	// Reads a particular application using the applicationNo.
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Boolean login(@RequestBody Credentials credential) throws NoSuchUserException {
		// check credentials
		// User user=login(credential.getUsername(),credential.getPassword());
		UserController userController = new UserController();
		Boolean isUser = userController.login(credential);
		if (!isUser)
			throw new NoSuchUserException();
		return isUser;
	}

	// Reads a particular application using the applicationNo.
	@RequestMapping(value = "/userProfile/{username}", method = RequestMethod.GET)
	public User getUserProfile(@PathVariable("username") String username) throws NoSuchUserException {
		// get events from db for eventNo
		UserController userController = new UserController();
		User user = userController.getProfile(username);

		if (user == null)
			throw new NoSuchUserException();
		return user;
	}

	@RequestMapping(value = "/event/{eventNo}", method = RequestMethod.POST)
	public Event register(@PathVariable("eventNo") int eventNo,
			@RequestParam(value = "username", required = false) String username)
			throws NoSuchEventException, NoSuchUserException {
		// get events from db for eventNo
		UserController userController = new UserController();
		User user = userController.getProfile(username);
		if (user == null)
			throw new NoSuchUserException();
		EventController eventController = new EventController();
		Event event = eventController.register(eventNo, user);
		if (event == null) {
			throw new NoSuchEventException();
		}
		return event;
	}

	@RequestMapping(value = "/event/archive/{eventNo}", method = RequestMethod.POST)
	public Event archive(@PathVariable("eventNo") int eventNo,
			@RequestParam(value = "username", required = false) String username)
			throws NoSuchEventException, NoSuchUserException, InvalidDateException {
		// get events from db for eventNo
		UserController userController = new UserController();
		User user = userController.getProfile(username);
		if (user == null)
			throw new NoSuchUserException();
		EventController eventController = new EventController();
		Event event;
		try {
			event = eventController.archive(eventNo, user);

			if (event == null) {
				throw new NoSuchEventException();
			}
		} catch (Exception e) {
			throw new InvalidDateException();
		}
		return event;
	}

	@RequestMapping(value = "/event/unarchieve/{eventNo}", method = RequestMethod.POST)
	public Event unarchive(@PathVariable("eventNo") int eventNo,
			@RequestParam(value = "username", required = false) String username)
			throws NoSuchEventException, NoSuchUserException, InvalidDateException {
		// get events from db for eventNo
		UserController userController = new UserController();
		User user = userController.getProfile(username);
		if (user == null)
			throw new NoSuchUserException();
		EventController eventController = new EventController();
		Event event;
		try {
			event = eventController.unarchive(eventNo, user);

			if (event == null) {
				throw new NoSuchEventException();
			}
		} catch (Exception e) {
			throw new InvalidDateException();
		}
		return event;
	}

	@RequestMapping(value = "/event/unregister/{eventNo}", method = RequestMethod.POST)
	public Event unregister(@PathVariable("eventNo") int eventNo,
			@RequestParam(value = "username", required = false) String username)
			throws NoSuchEventException, NoSuchUserException {
		// get events from db for eventNo
		UserController userController = new UserController();
		User user = userController.getProfile(username);
		if (user == null)
			throw new NoSuchUserException();
		EventController eventController = new EventController();
		Event event = eventController.unregister(eventNo, user);
		if (event == null) {
			throw new NoSuchEventException();
		}
		return event;
	}

	@RequestMapping(value = "/events/registeredEvents/{username}", method = RequestMethod.GET)
	public List<Event> getRegisteredEvents(@PathVariable("username") String username) throws NoSuchEventException {
		// get events from db for eventNo
		EventController eventController = new EventController();
		ArrayList<Event> events = eventController.getRegisteredEvents(username);
		return events;
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List<String> getUsers(@RequestParam(value = "filterUsername", required = false) String filterUsername,
			@RequestParam(value = "eventNo", required = false) int eventNo) {
		UserController userController = new UserController();
		ArrayList<String> users = userController.getUsers(filterUsername, eventNo);
		return users;
	}

}
