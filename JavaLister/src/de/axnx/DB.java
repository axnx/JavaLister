package de.axnx;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
public class DB {
 
  private Connection _conn = null;
  private String _connectionString;
  private String _driver;

  public DB(){
	  
  }
  
  public DB(String connectionString, String driver){
	  _connectionString = connectionString; 
	  _driver = driver;
  }
  
  public static void test() throws ClassNotFoundException, SQLException{
	  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
		Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;user=sa;password=sasql;database=DuplicateFinder");
		System.out.println("test");
		Statement sta = conn.createStatement();
		String Sql = "select * from workingTable";
		ResultSet rs = sta.executeQuery(Sql);
		while (rs.next()) {
			System.out.println(rs.getString("id"));
		}
  }
  
  public Connection getConnection() {

     _conn = null;
	  try {
		Class.forName(_driver);
		_conn = DriverManager.getConnection(_connectionString);
	} catch (SQLException e) {
		e.printStackTrace();
		return null;
	} catch (ClassNotFoundException e1) {
		e1.printStackTrace();
	}	
	return _conn;
  }

}
