package service.user;

import java.util.ArrayList;

import model.user.UserModel;
import service.core.Credentials;
import service.core.User;

public class UserController {
	public User createUser(User user){
		new UserModel().createUser(user);
		return user;
	}
	
	public Boolean login(Credentials credential) {
		Boolean isUser=new UserModel().login(credential);
		return isUser;
	}
	
	public User getProfile(String username) {
		return new UserModel().getProfile(username);
	}
	
	public ArrayList<String> getUsers(String filterUsername, int eventNo){
		return new UserModel().getUsers(filterUsername, eventNo);
	}
}
