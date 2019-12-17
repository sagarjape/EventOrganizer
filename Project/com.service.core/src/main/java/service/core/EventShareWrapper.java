package service.core;

import java.util.List;

public class EventShareWrapper {
	private int eventNo;
	private String currentUsername;
	private List<String> usernames;

	public EventShareWrapper() {
		
	}
	public int getEventNo() {
		return eventNo;
	}
	public void setEventNo(int eventNo) {
		this.eventNo = eventNo;
	}
	public String getCurrentUsername() {
		return currentUsername;
	}
	public void setCurrentUsername(String currentUsername) {
		this.currentUsername = currentUsername;
	}
	public List<String> getUsernames() {
		return usernames;
	}
	public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}
}
