package model.event;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import model.connection.pooling.ConnectionPool;
import model.user.UserModel;
import service.core.Event;
import service.core.User;

public class EventModel {
	ConnectionPool connectionPool = new ConnectionPool();

	public Event createEvent(Event event) {
		int res = 0;
		Connection Con = null;
		try {
			Con = connectionPool.getConnection();

			String eventSql = "INSERT INTO events(event_name,description,date, location, ticket_price,host, capacity) "
					+ "VALUES(?,?,?,?,?,?,?)";

			PreparedStatement pstmt = Con.prepareStatement(eventSql);

			pstmt.setString(1, event.getEventName());
			pstmt.setString(2, event.getDescription());
			pstmt.setDate(3, event.getDate());
			pstmt.setString(4, event.getLocation());
			pstmt.setDouble(5, event.getTicketPrice());
			pstmt.setString(6, event.getHost().getUsername());
			pstmt.setInt(7, event.getCapacity());

			res = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		if (res == 0) {
			return null;
		}
		return event;
	}

	public ArrayList<Event> list(String filterType, String username, String location, Date date, String hostname, boolean newEvents) {
		Connection Con = null;
		Event event = null;
		ArrayList<Event> events = new ArrayList<Event>();
		try {
			Con = connectionPool.getConnection();

			String userSql = "select * from events";
			PreparedStatement pstmt = Con.prepareStatement(userSql);
			if (filterType != null && !"".equals(filterType)) {
				switch (filterType) {
				case "username": {
					String eventsUserSql = "select * from event_shared_map where username=? or shared_by=?";
					PreparedStatement pstmt1 = Con.prepareStatement(eventsUserSql);
					pstmt1.setString(1, username);
					pstmt1.setString(2, username);
					ResultSet result = pstmt1.executeQuery();
					List<Integer> eventIds = new ArrayList<Integer>();
					while (result.next()) {
						eventIds.add(result.getInt("event_no"));
					}
					Iterator<Integer> itr = eventIds.iterator();
					if (eventIds.size() > 0) {
						userSql = (userSql + " where (id in (");
						while (itr.hasNext()) {
							userSql += "?,";
							itr.next();
						}
						userSql = userSql.substring(0, userSql.length() - 1);
						userSql += ")";
					} else {
						userSql += " where (id = -1";
					}
					int i = 0;
					userSql += " or host = ?)";
					userSql += "and id not in (select event_number from event_archive_map where username=?) and id not in (select event_no from event_user_map where username=?);";
					pstmt = Con.prepareStatement(userSql);
					for (; i < eventIds.size(); i++) {
						pstmt.setInt(i + 1, eventIds.get(i));
					}

					pstmt.setString(i + 1, username);
					pstmt.setString(i + 2, username);
					pstmt.setString(i + 3, username);
					break;
				}
				case "archive": {
					String archiveSql = "select * from EVENT_ARCHIVE_MAP where username=?";
					pstmt = Con.prepareStatement(archiveSql);
					pstmt.setString(1, username);
					ResultSet result = pstmt.executeQuery();
					List<Integer> eventIds = new ArrayList<Integer>();
					while (result.next()) {
						eventIds.add(result.getInt("event_number"));
					}
					Iterator<Integer> itr = eventIds.iterator();
					if (eventIds.size() > 0) {
						userSql = (userSql + " where id in (");
						while (itr.hasNext()) {
							userSql += "?,";
							itr.next();
						}
						userSql = userSql.substring(0, userSql.length() - 1);
						userSql += ");";
					} else {
						userSql += " where id = -1";
					}
					pstmt = Con.prepareStatement(userSql);
					for (int i = 0; i < eventIds.size(); i++) {
						pstmt.setInt(i + 1, eventIds.get(i));
					}
					break;
				}
				case "location": {
					userSql = (userSql + " where location=?");
					pstmt = Con.prepareStatement(userSql);
					pstmt.setString(1, location);
					break;
				}
				case "host": {
					userSql = (userSql + " where host=?");
					if(newEvents) {
						userSql +=" and date >= CURRENT_DATE";
					}
					pstmt = Con.prepareStatement(userSql);
					pstmt.setString(1, hostname);
					break;
				}
				case "date": {
					userSql = (userSql + " where date=?");
					pstmt = Con.prepareStatement(userSql);
					pstmt.setDate(1, date);
					break;
				}
				}
			}

			// pstmt.setString(1, username);
			ResultSet result = pstmt.executeQuery();

			while (result.next()) {
				String eventName = result.getString("event_name");
				String description = result.getString("description");
				String eventLocation = result.getString("location");
				double ticketPrice = result.getDouble("ticket_price");
				String eventHost = result.getString("host");
				int capacity = result.getInt("capacity");
				int id = result.getInt("id");
				Date eventDate = result.getDate("date");
				event = new Event();
				event.setCapacity(capacity);
				event.setDate(eventDate);
				event.setDescription(description);
				event.setEventName(eventName);
				User user = new UserModel().getProfile(eventHost);
				event.setHost(user);
				event.setEventId(id);
				event.setLocation(eventLocation);
				event.setTicketPrice(ticketPrice);
				events.add(event);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectionPool.closeConnection(Con);
		}
		return events;
	}

	public boolean delete(int eventNo) {
		int res, res1 = 0, res2 = 0, res3 = 0;
		int[] rs = new int[2];
		Connection Con = null;

		try {

			Event event = read(eventNo);
			if (event.getDate().before(new Date(Calendar.getInstance().getTimeInMillis()))) {
				return false;
			}
			Con = connectionPool.getConnection();

			String eventSql = "DELETE FROM events WHERE id=?";
			String event_map = "DELETE from event_user_map where event_no=?";
			String event_share_map = "DELETE from event_shared_map where event_no=?";
			String event_archive_map = "DELETE from event_archive_map where event_number=?";
			PreparedStatement pstmt = Con.prepareStatement(eventSql);
			PreparedStatement pstmt1 = Con.prepareStatement(event_map);
			PreparedStatement pstmt2 = Con.prepareStatement(event_share_map);
			PreparedStatement pstmt3 = Con.prepareStatement(event_archive_map);

			pstmt.setInt(1, eventNo);
			pstmt1.setInt(1, eventNo);
			pstmt2.setInt(1, eventNo);
			pstmt3.setInt(1, eventNo);

			Con.setAutoCommit(false);
			res = pstmt.executeUpdate();
			if (res > 0) {
				res1 = pstmt1.executeUpdate();
			}
			if (res1 >= 0) {
				res2 = pstmt2.executeUpdate();
			}
			if (res2 >= 0) {
				res3 = pstmt3.executeUpdate();
			}
			if (res3 >= 0)
				Con.commit();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		if (rs[0] < 0 || rs[1] < 0) {
			return false;
		}
		return true;
	}

	public Event updateEvent(int eventNo, Event event) {
		int res = 0;
		Connection Con = null;

		try {
			if (event.getDate().before(new Date(Calendar.getInstance().getTimeInMillis()))) {
				return null;
			}
			Con = connectionPool.getConnection();

			String eventSql = "UPDATE events "
					+ "set event_name=?, description=?, date=?, location=?, ticket_price=?, host=?, capacity=? where id=?";

			PreparedStatement pstmt = Con.prepareStatement(eventSql);

			pstmt.setString(1, event.getEventName());
			pstmt.setString(2, event.getDescription());
			pstmt.setDate(3, event.getDate());
			pstmt.setString(4, event.getLocation());
			pstmt.setDouble(5, event.getTicketPrice());
			pstmt.setString(6, event.getHost().getUsername());
			pstmt.setInt(7, event.getCapacity());
			pstmt.setInt(8, eventNo);

			res = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		if (res == 0) {
			return null;
		}
		return event;
	}

	public Event read(int eventNo) {
		Connection Con = null;
		Event event = null;
		try {
			Con = connectionPool.getConnection();

			String userSql = "select * from events where id=?";
			PreparedStatement pstmt = Con.prepareStatement(userSql);
			pstmt.setInt(1, eventNo);
			ResultSet result = pstmt.executeQuery();
			while (result.next()) {
				String eventName = result.getString("event_name");
				String description = result.getString("description");
				String eventLocation = result.getString("location");
				double ticketPrice = result.getDouble("ticket_price");
				String eventHost = result.getString("host");
				int capacity = result.getInt("capacity");
				int id = result.getInt("id");
				Date eventDate = result.getDate("date");
				event = new Event();
				event.setCapacity(capacity);
				event.setDate(eventDate);
				event.setDescription(description);
				event.setEventName(eventName);
				User user = new UserModel().getProfile(eventHost);
				event.setHost(user);
				event.setEventId(id);
				event.setLocation(eventLocation);
				event.setTicketPrice(ticketPrice);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectionPool.closeConnection(Con);
		}
		return event;
	}

	public Event register(int eventNo, User user) {
		int res = 0;
		Event event = null;
		Connection Con = null;

		try {
			event = read(eventNo);
			if (event == null)
				return null;

			if (event.getDate().before(new Date(Calendar.getInstance().getTimeInMillis()))) {
				return null;
			}

			Con = connectionPool.getConnection();

			String eventSql = "INSERT INTO EVENT_USER_MAP(event_no, username) VALUES (?,?)";

			PreparedStatement pstmt = Con.prepareStatement(eventSql);

			pstmt.setInt(1, eventNo);
			pstmt.setString(2, user.getUsername());

			res = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		if (res == 0) {
			return null;
		}
		return event;
	}

	public Event archive(int eventNo, User user) throws InvalidDateException {
		int res = 0, res1 = 0;
		Connection Con = null;
		Event event = null;
		try {
			event = read(eventNo);
			if (event == null)
				return null;

			if (event.getDate().after(new Date(Calendar.getInstance().getTimeInMillis()))) {
				throw new InvalidDateException();
			}

			Con = connectionPool.getConnection();

			String eventSql = "INSERT INTO EVENT_ARCHIVE_MAP(event_number, username) VALUES (?,?)";
			String deleteSql = "DELETE FROM EVENT_SHARED_MAP where event_no=? and username= ?";

			PreparedStatement pstmt1 = Con.prepareStatement(eventSql);
			PreparedStatement pstmt2 = Con.prepareStatement(deleteSql);

			pstmt1.setInt(1, eventNo);
			pstmt1.setString(2, user.getUsername());
			pstmt2.setInt(1, eventNo);
			pstmt2.setString(2, user.getUsername());

			Con.setAutoCommit(false);
			res = pstmt1.executeUpdate();
			if (res > 0) {
				res1 = pstmt2.executeUpdate();
			}
			if (res1 >= 0)
				Con.commit();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		if (res == 0) {
			return null;
		}
		return event;
	}

	public Event unarchive(int eventNo, User user) throws InvalidDateException {
		int res = 0, res1 = 0;
		Event event = null;
		Connection Con = null;

		try {
			event = read(eventNo);
			if (event == null)
				return null;

			Con = connectionPool.getConnection();

			String eventSql = "INSERT INTO EVENT_SHARED_MAP(event_no, username, shared_by) VALUES (?,?,?)";
			String deleteSql = "DELETE FROM EVENT_ARCHIVE_MAP where event_number=? and username= ?";

			PreparedStatement pstmt1 = Con.prepareStatement(eventSql);
			PreparedStatement pstmt2 = Con.prepareStatement(deleteSql);

			pstmt1.setInt(1, eventNo);
			pstmt1.setString(2, user.getUsername());
			pstmt1.setString(3, event.getHost().getUsername());
			pstmt2.setInt(1, eventNo);
			pstmt2.setString(2, user.getUsername());

			Con.setAutoCommit(false);
			res = pstmt1.executeUpdate();
			if (res > 0) {
				res1 = pstmt2.executeUpdate();
			}
			if (res1 >= 0)
				Con.commit();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		if (res == 0) {
			return null;
		}
		return event;
	}

	public Event unregister(int eventNo, User user) {
		int res = 0;
		Connection Con = null;

		Event event = null;
		try {
			event = read(eventNo);
			if (event == null)
				return null;

			if (event.getDate().before(new Date(Calendar.getInstance().getTimeInMillis()))) {
				return null;
			}

			Con = connectionPool.getConnection();

			String eventSql = "DELETE FROM EVENT_USER_MAP where EVENT_NO=? and USERNAME=?";

			PreparedStatement pstmt = Con.prepareStatement(eventSql);

			pstmt.setInt(1, eventNo);
			pstmt.setString(2, user.getUsername());

			res = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		if (res == 0) {
			return null;
		}
		return event;
	}

	public ArrayList<String> getRegisteredUsers(int eventNo) {
		ArrayList<String> usernames = new ArrayList<>();
		Event event = null;
		Connection Con = null;

		try {
			event = read(eventNo);
			if (event == null)
				return null;

			if (event.getDate().before(new Date(Calendar.getInstance().getTimeInMillis()))) {
				return null;
			}

			Con = connectionPool.getConnection();

			String eventSql = "select * from EVENT_USER_MAP where event_no=?";

			PreparedStatement pstmt = Con.prepareStatement(eventSql);

			pstmt.setInt(1, eventNo);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				usernames.add(rs.getString("username"));
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		return usernames;
	}

	public boolean share(Event event, ArrayList<User> users, User currentUser) {
		Connection Con = null;

		try {

			if (event.getDate().before(new Date(Calendar.getInstance().getTimeInMillis()))) {
				return false;
			}

			Con = connectionPool.getConnection();
			Con.setAutoCommit(false);
			String eventSql = "INSERT INTO event_shared_map(event_no, username, shared_by) VALUES (?,?,?)";
			PreparedStatement ps = Con.prepareStatement(eventSql);
			for (User user : users) {
				ps.setObject(1, event.getEventId());
				ps.setObject(2, user.getUsername());
				ps.setObject(3, currentUser.getUsername());
				ps.addBatch();
			}
			ps.executeBatch();
			Con.commit();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		return true;
	}

	public ArrayList<Event> getSharedEventsForUser(String username) {
		ArrayList<Event> events = new ArrayList<>();
		Connection Con = null;

		try {
			Con = connectionPool.getConnection();

			String eventSql = "select * from event_shared_map where username=?";

			PreparedStatement pstmt = Con.prepareStatement(eventSql);

			pstmt.setString(1, username);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Event event = read(Integer.parseInt(rs.getString("event_no")));
				events.add(event);
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		return events;
	}

	public ArrayList<Event> getRegisteredEvents(String username) {
		ArrayList<Event> events = new ArrayList<Event>();
		Connection Con = null;

		try {
			Con = connectionPool.getConnection();

			String eventSql = "select * from event_user_map where username=?";

			PreparedStatement pstmt = Con.prepareStatement(eventSql);

			pstmt.setString(1, username);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Event event = read(Integer.parseInt(rs.getString("event_no")));
				if (event != null)
					events.add(event);
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		return events;
	}
}
