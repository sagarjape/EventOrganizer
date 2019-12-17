package model.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.connection.pooling.ConnectionPool;
import service.core.Credentials;
import service.core.User;

public class UserModel {
	ConnectionPool connectionPool = new ConnectionPool();

	public User createUser(User user) {
		Connection Con=null;
		int res = 0, res1 = 0;
		try {
			Con = connectionPool.getConnection();

			String userSql = "INSERT INTO users(username,name,user_profile, email) " + "VALUES(?,?,?,?)";

			String credentialSql = "INSERT INTO credentials(username,password) " + "VALUES(?,?)";
			PreparedStatement credentialpstmt = Con.prepareStatement(credentialSql);
			PreparedStatement pstmt = Con.prepareStatement(userSql);

			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getName());
			pstmt.setString(3, user.getUserProfile());
			pstmt.setString(4, user.getEmail());

			credentialpstmt.setString(1, user.getCredentials().getUsername());
			credentialpstmt.setString(2, user.getCredentials().getPassword());

			Con.setAutoCommit(false);
			res = credentialpstmt.executeUpdate();
			if (res > 0) {
				res1 = pstmt.executeUpdate();
			}
			Con.commit();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			connectionPool.closeConnection(Con);
		}
		if (res == 0 || res1 == 0) {
			return null;
		}
		return user;
	}

	public boolean login(Credentials credentials) {
		Connection Con=null;

		try {
			Con = connectionPool.getConnection();

			String credentialSql = "select * from credentials where USERNAME=? and PASSWORD=?";
			PreparedStatement credentialpstmt = Con.prepareStatement(credentialSql);

			credentialpstmt.setString(1, credentials.getUsername());
			credentialpstmt.setString(2, credentials.getPassword());

			ResultSet rs = credentialpstmt.executeQuery();
			if(rs.next()) {
				connectionPool.closeConnection(Con);
				return true;
			}
		} catch (Exception e) {
			System.out.println(e);
		}finally {
			connectionPool.closeConnection(Con);
		}

		return false;
	}

	public User getProfile(String username) {

		Connection Con=null;
		User user = null;

		try {
			Con = connectionPool.getConnection();

			String userSql = "select * from users where USERNAME=?";
			PreparedStatement pstmt = Con.prepareStatement(userSql);

			pstmt.setString(1, username);
			ResultSet result = pstmt.executeQuery();
			while (result.next()) {
				String email = result.getString("email");
				String name = result.getString("name");
				String userprofile = result.getString("user_profile");
				int id = result.getInt("id");
				user = new User();
				user.setEmail(email);
				user.setName(name);
				user.setUsername(username);
				user.setId(id);
				user.setUserProfile(userprofile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			connectionPool.closeConnection(Con);
		}
		return user;
	}
	
	public ArrayList<String> getUsers(String filterUsername, int eventNo){
		ArrayList<String> users=new ArrayList<String>();
		Connection Con=null;
		try {
			Con = connectionPool.getConnection();

			String userSql = "select * from users u left outer join event_shared_map es on u.username=es.username where u.username != ? and ( event_no!=? or event_no is null)";
			PreparedStatement pstmt = Con.prepareStatement(userSql);
			pstmt.setString(1, filterUsername);
			pstmt.setInt(2, eventNo);

			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				users.add(rs.getString("username"));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		finally {
			connectionPool.closeConnection(Con);
		}
		return users;
	}
}
