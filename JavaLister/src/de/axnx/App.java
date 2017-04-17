package de.axnx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import net.openhft.hashing.LongHashFunction;

public class App {
	
	private static DB _db = new DB("jdbc:sqlserver://localhost:1433;user=sa;password=sasql;database=DuplicateFinder", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
	
	private static final String newLine = System.getProperty("line.separator");

	public static synchronized void writeToFile(String msg)  {
	    String fileName = "e:\\filewalker.txt";
	    PrintWriter printWriter = null;
	    File file = new File(fileName);
	    try {
	        if (!file.exists()) file.createNewFile();
	        printWriter = new PrintWriter(new FileOutputStream(fileName, true));
	        printWriter.write(newLine + msg);
	    } catch (IOException ioex) {
	        ioex.printStackTrace();
	    } finally {
	        if (printWriter != null) {
	            printWriter.flush();
	            printWriter.close();
	        }
	    }
	}
	
	
	public synchronized byte[] getFileAsByteArray(String pathFile){
	
 		 FileInputStream fileInputStream=null;
		 File file = new File(pathFile);
	     byte[] bFile = new byte[(int) file.length()];

	     try {
		    fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
	     }catch(Exception e){
	        	e.printStackTrace();
	     }
	     return bFile;
 	}
	
	 	    
	public String getHashFromFile(String pathFile){
		byte[] bArr = getFileAsByteArray(pathFile);
     	String longString = Long.toHexString( LongHashFunction.xx_r39().hashBytes(bArr) );
		return longString;
	}
	
	
	public synchronized HashMap<Integer, String> getHMFiles(int thread){
		System.out.println("-> Thread #" + thread + " is running");
		HashMap<Integer, String> hm = new TableF().getData(100);
		new TableF().setHMStatus(1, hm);
		return hm;
	}
	
	public synchronized void workingProcess2(int thread, HashMap hm){
		HashMap<Integer, String> hmHashes = new HashMap<>(); 
		hm.forEach( (id,v) -> {
			System.out.println((int)id + ";" + (String)v);
			String hash = getHashFromFile((String)v);
			hmHashes.put((int)id, hash);
		});
		new TableF().setHash(thread, hmHashes);
	}
	
	public static boolean deleteFile(String pathFile){
		File file = new File(pathFile);
		if (file.exists()) {
		    file.delete();
		    System.out.println("-> file deleted: " + pathFile);
		} else {
		    System.err.println(
		        "-> file not found: " + pathFile);
		    return false;
		}
		return true;
	}
	
	
	 public static void renameDirectory(String fromDir, String toDir) {
		 
	        File from = new File(fromDir);
	        if (!from.exists() || !from.isDirectory()) {
	            System.out.println("Directory does not exist: " + fromDir);
	            return;
	        }
	 
	        File to = new File(toDir);
	        //Rename
	        if (from.renameTo(to)) {
	            System.out.println("Success!");
	        } else {
	            System.out.println("Error");
	        }
	    }
	
	
	public static int getPosFromRight(String str, char search){
		
		int i = str.length();
		do{
			if(str.charAt(i-1) == search){
		         return i;
		    }
			i--;
		} while (i > 0);
		
//		for(int i=str.length(); i > 0; i--)
//		{
//		    if(str.charAt(i) == search){
//		         return i;
//		    }
//		}
		return 0;
	}
	
	public static void working(String pathFile){
		File file = new File(pathFile);	
		String pathOfFile = file.getParent();
		
		if(!new File(pathOfFile).isDirectory()){
			return;
		}
		
		System.out.println(pathOfFile);
		
		String strSubDirName = pathOfFile.substring(getPosFromRight(pathOfFile, '\\'));
		
		if(strSubDirName == "MUSIK_iTunes Match" ){
			return;
		}
		
		try {
			System.out.println("-> mv " + pathOfFile + " to " +  "F:/MUSIK_iTunes Match2/" + strSubDirName );
			Files.move(new File(pathOfFile).toPath(), new File("F:/MUSIK_iTunes Match2/" + strSubDirName).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (DirectoryNotEmptyException e) {
			try {
				Files.copy(new File(pathOfFile).toPath(), new File("F:/MUSIK_iTunes Match2/" + strSubDirName).toPath(), StandardCopyOption.REPLACE_EXISTING);
				renameDirectory(pathOfFile, pathOfFile + "_XXX");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args)  {
		
		long startTime = System.currentTimeMillis();

		//Filewalker.walk("F:\\");
		
//		App app = new App();
//		Thread t1;
//		Thread t2;
//		Thread t3;
//		
//		for(;;) {
//		
//		HashMap hm1 = app.getHMFiles(1);
//		HashMap hm2 = app.getHMFiles(2);
//		HashMap hm3 = app.getHMFiles(3);
//		
//		Runnable task1 = () -> { 
//			app.workingProcess2(1,hm1); 
//		};
//		
//		Runnable task2 = () -> { 
//			app.workingProcess2(2,hm2);
//		};
//		
//		Runnable task3 = () -> {
//			app.workingProcess2(3,hm3); 
//		};
//
//		t1 = new Thread(task1);
//		t2 = new Thread(task2);
//		t3 = new Thread(task3);
//		
//		t1.start();
//		t2.start();
//		t3.start();
//		try {
//			t3.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
		//for file delete
//		HashMap<Integer, String> hm  = TableF.getDataStatus2();
//		hm.forEach((id,pathFile) -> {
//			deleteFile(pathFile);
//		});

		
		//hash directory		
//		HashMap<Integer, String> hm  = TableF.getDataDirectory();
//		hm.forEach((id,pathFile) -> {
//			String hash = DirMD5.dirMD5(pathFile);
//			System.out.println("-> updating id: " + id);
//			TableF.updateTableHash(id, hash);
//			TableF.updateTable(4, id);
//		});
		
//		HashMap<Integer, String> hm  = TableF.getDataDirectory2();
//		hm.forEach((id,path) -> {
//			working(path);
//		});
//		
//		Set<String> files = Filewalker.listerFile("C:/Users/User/Desktop/OllyDBG/odbg200");
		Set<String> dirs = Filewalker.listerDir("C:/temp");
//		Set<String> dirs = Filewalker.listerDir("C:/Windows");
		
		dirs.forEach( p -> TableF.insertToDB(true, null, p ,null));
		
		
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//dirs.forEach(System.out::println); 
		HashMap<String, String> hm = new LinkedHashMap<>();
		dirs.forEach(dir -> {
			String md5 = DirMD5.dirMD5(dir);
			System.out.println("-> update dir: " + dir + " with hash: " + md5);
			TableF.updateTableHashDir(dir, md5);
		}); 
		
		
//		System.out.println(dirs.size());
		
		//String md5 = DirMD5.dirMD5("C:/Users/User/Desktop/OllyDBG/odbg200");
//		System.out.println(md5);
		
		//files.forEach(System.out::println);
		
		//long l = getHashFromFile("F:\\MUSIK_Alben_MAC\\Heaven Shall burn\\Iconoclast (2008)\\02-endzeit-qtxmp3.mp3");
		//System.out.println(l);

		//DB.insertToDB(true, "testFile", "testPath");
		//DB db = new DB();
		//insertToDB(true, "filename", "path");
		
		//db.insertSQL(sql, ppst);
		
		long endTime = System.currentTimeMillis();
	    System.out.println("Executed in " + (endTime - startTime) + " ms.");
	}
		
	//}
	
}
