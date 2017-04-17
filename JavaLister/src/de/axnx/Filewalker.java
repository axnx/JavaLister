package de.axnx;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Set;

public class Filewalker {

    public static void walk( String path ) {

        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	String path1 = f.getAbsolutePath();
            	App.insertToDB(true, null, path1, null);
                walk( f.getAbsolutePath() );
                //System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
            	//String filename = f.getAbsoluteFile().toString();
            	String filename = f.getName();
            	String extension = getFileExtension(filename); 
            	
                System.out.println( "File:" + filename );
                App.insertToDB(false, filename, f.getAbsolutePath(), extension);
                //App.writeToFile( filename );
                
            }
        }
    }
    
    
    public static String getFileExtension (String filename) {

    	String separator = System.getProperty("file.separator");      
        int extensionIndex = filename.lastIndexOf(".");
        String fileExtension = filename.substring(extensionIndex + 1);
       
        return fileExtension;
    }
    
    
    public static void walk2(String file ) {
    	File path = new File(file);
    	try {
			Files.walk(path.toPath())
			.filter(p -> !Files.isDirectory(p))
			.forEach(p -> System.out.println(p));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
    
    static Set<String> dirSet = new LinkedHashSet<>();
    static int i = 0;
    //Java8 Lambda
    public static Set<String> listerDir(String path){
    	
		try {
			Files.list(new File(path).toPath())
			.filter(p -> Files.isDirectory(p))
			.forEach(p -> {
				//System.out.println(p.getFileName());
				System.out.println("-> " + i++ + " " + p);
				dirSet.add(p.toString());
				Filewalker.listerDir(p.toString());
				//writeToFile(p.toString());
			});			
		} catch (IOException e) {
			
		} catch (UncheckedIOException e){
			
		}
		return dirSet;
	}
    
    public static Set<String> listerFile(String path){
		Set<String> fileSet = new LinkedHashSet<>();
    	try {
			Files.list(new File(path).toPath())
			.filter(p -> Files.isRegularFile(p))
			.forEach(p -> {
				System.out.println("-> " + p);
				fileSet.add(p.toString());
				Filewalker.listerFile(p.toString());
				//writeToFile(p.toString());
			});			
		} catch (IOException e) {
			
		} catch (UncheckedIOException e){
			
		}
    	return fileSet;
	}
    
    
    
    

}
