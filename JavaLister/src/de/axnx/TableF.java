package de.axnx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;

public class TableF {
	
	private static DB _db = new DB("jdbc:sqlserver://localhost:1433;user=sa;password=sasql;database=DuplicateFinder", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
	
	 public static int insertToDB(boolean isDirectory, String filename, String path, String extension){
		 int rv = 0;
			try {
				String sql = "INSERT INTO workingTable(isDirectory, filename, path, date, extension) VALUES (?,?,?,?,?)";
				PreparedStatement ppst = _db.getConnection().prepareStatement(sql);
				ppst.setBoolean(1, isDirectory);
				ppst.setString(2, filename);
				ppst.setString(3, path);
				ppst.setObject(4, new Timestamp(System.currentTimeMillis()));
				ppst.setString(5, extension);
				rv = ppst.executeUpdate();
				 _db.getConnection().close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return rv;
	  }
	 
	 public synchronized HashMap<Integer, String> getData(int limit){
		 HashMap<Integer, String> hm = new HashMap<>();
			try{
				String sql = "select TOP " + limit + " * From workingTable where toCheck=? and Status is null";
				PreparedStatement ppst = _db.getConnection().prepareStatement(sql);
				//ppst.setInt(1, limit);
				ppst.setInt(1, 1);
				ResultSet rs = ppst.executeQuery();			
	            while (rs.next() ) {
	            	int id = rs.getInt("id");
	            	String pathFile = rs.getString("path");
	            	hm.put(id,pathFile);
	            }
	            _db.getConnection().close();
	        } catch (Exception e) {
	            System.err.println(e.getMessage());
	        }
		return hm;
	  }
	 
	 
	 public static HashMap<Integer, String> getDataStatus2(){
		 HashMap<Integer, String> hm = new HashMap<>();
			try{
				String sql = "select * from workingTable where status = 2";
				Statement stmt =  _db.getConnection().createStatement(); 
				ResultSet rs = stmt.executeQuery(sql);
	            while (rs.next() ) {
	            	int id = rs.getInt("id");
	            	String pathFile = rs.getString("path");
	            	hm.put(id,pathFile);
	            }
	            _db.getConnection().close();
	        } catch (Exception e) {
	            System.err.println(e.getMessage());
	        }
		return hm;
	  }
	 
	 
	 public static HashMap<Integer, String> getDataDirectory(){
		 HashMap<Integer, String> hm = new HashMap<>();
			try{
				String sql = "select * from  workingTable where isDirectory = 1 and status = 3";
				Statement stmt =  _db.getConnection().createStatement(); 
				ResultSet rs = stmt.executeQuery(sql);
	            while (rs.next() ) {
	            	int id = rs.getInt("id");
	            	String pathFile = rs.getString("path");
	            	hm.put(id,pathFile);
	            }
	            _db.getConnection().close();
	        } catch (Exception e) {
	            System.err.println(e.getMessage());
	        }
		return hm;
	  }
	 
	 public static HashMap<Integer, String> getDataDirectory2(){
		 HashMap<Integer, String> hm = new HashMap<>();
			try{
				String sql = 
						 "Select id, isDirectory, path, filename, xxhash, status"
						+ " from workingTable"  
						+ " where xxhash in ("  
						+ " select xxhash" 
						+ " from workingTable" 
						+ " group by xxhash" 
						+ " having count(xxhash) > 1)"
						+ " and path like '%F:\\MUSIK_iTunes Match\\Rest\\Sortiert1\\%'"
						+ " or path like 'F:\\MUSIK_iTunes Match\\sound1%'"
						+ " and xxhash is not null"
						+ " order by xxhash";
						
				Statement stmt =  _db.getConnection().createStatement(); 
				ResultSet rs = stmt.executeQuery(sql);
	            while (rs.next() ) {
	            	int id = rs.getInt("id");
	            	String pathFile = rs.getString("path");
	            	hm.put(id,pathFile);
	            }
	            _db.getConnection().close();
	        } catch (Exception e) {
	            System.err.println(e.getMessage());
	        }
		return hm;
	  }
	 
	 
	 public static int updateTableHash(int id, String hash){
		int rv = 0;
		try{
			String sql = "Update workingTable set xxhash=? where id=?";
			PreparedStatement ppst = _db.getConnection().prepareStatement(sql);
			ppst.setString(1, hash);
			ppst.setInt(2, id);
			rv = ppst.executeUpdate();
	        _db.getConnection().close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
		return rv;
	 }
	 
	 public static int updateTableHashDir(String path, String hash){
			int rv = 0;
			try{
				String sql = "Update workingTable set xxhash=? where path=?";
				PreparedStatement ppst = _db.getConnection().prepareStatement(sql);
				ppst.setString(1, hash);
				ppst.setString(2, path);
				rv = ppst.executeUpdate();
		        _db.getConnection().close();
	        } catch (Exception e) {
	            System.err.println(e.getMessage());
	        }
			return rv;
		 }
	 

	 
	 public static int updateTable(int status, int id){
			int rv = 0;
			try{
				String sql = "Update workingTable set status=? where id=?";
				PreparedStatement ppst = _db.getConnection().prepareStatement(sql);
				ppst.setInt(1, status);
				ppst.setInt(2, id);
				rv = ppst.executeUpdate();
		        _db.getConnection().close();
	        } catch (Exception e) {
	            System.err.println(e.getMessage());
	        }
			return rv;
		 }
	 
	 
	 public synchronized void setHMStatus(int status, HashMap hm){
		 hm.forEach( (id,filepath) -> {
			 System.out.println("-> update status, id" + id );
			 updateTable(status, (int)id);
		 });
	 }
	 
	 public  synchronized void setHash(int thread, HashMap hm){
		 hm.forEach( (id,hash) -> {
			 System.out.println("-> thread #" + thread +", update hash, id" + id );
			 updateTableHash((int)id, (String)hash);
		 });
	 }
	 
}
