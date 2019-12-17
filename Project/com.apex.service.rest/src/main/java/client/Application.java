package client;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import model.connection.pooling.ConnectionPool;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		ConnectionPool pool = new ConnectionPool();
		try {
			pool.createConnectionPool();
		} catch (Exception e) {
		}
		SpringApplication.run(Application.class, args);
	}
}