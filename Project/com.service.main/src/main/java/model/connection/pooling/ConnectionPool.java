package model.connection.pooling;
 
import java.sql.Connection;
 
import javax.sql.DataSource;
 
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
 
public class ConnectionPool {
	static final String endpoint="aa1xm5m7zya3fsz.cmnjgzkkqhhr.us-east-1.rds.amazonaws.com";
	static final String port="5432";
	static final String host="jdbc:postgresql://"+endpoint+":"+port+"/postgres";

    static DataSource dataSource;
    // JDBC Database Credentials
    static final String JDBC_USER = "postgres";
    static final String JDBC_PASS = "welcome123";
 
    private static GenericObjectPool gPool = null;
 
    @SuppressWarnings("unused")
    public DataSource setUpPool() throws Exception {
        //Class.forName(JDBC_DRIVER);
 
        // Creates an Instance of GenericObjectPool That Holds Our Pool of Connections Object!
        gPool = new GenericObjectPool();
        gPool.setMaxActive(5);
 
        // Creates a ConnectionFactory Object Which Will Be Use by the Pool to Create the Connection Object!
        ConnectionFactory cf = new DriverManagerConnectionFactory(host, JDBC_USER, JDBC_PASS);
 
        // Creates a PoolableConnectionFactory That Will Wraps the Connection Object Created by the ConnectionFactory to Add Object Pooling Functionality!
        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, null, null, false, true);
        return new PoolingDataSource(gPool);
    }
 
    public GenericObjectPool getConnectionPool() {
        return gPool;
    }
 
    public void createConnectionPool() {
        ConnectionPool jdbcObj = new ConnectionPool();
        try {
        	dataSource = jdbcObj.setUpPool();
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }

    }
    public Connection getConnection() {
        Connection connObj = null;
        try {   
            connObj = dataSource.getConnection();
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        return connObj;
    }
    
    public void closeConnection(Connection connObj) {
    	try {
            if(connObj != null) {
                connObj.close();
            }
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
    }
}