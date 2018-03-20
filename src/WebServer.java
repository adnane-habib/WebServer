import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.xml.ws.http.HTTPException;



public class WebServer {
	
	private final static int LISTENING_PORT = 50505;
	private final static String rootDirectory = System.getProperty("user.dir")+"\\www"; 

	 public static void main(String[] args) {
	        ServerSocket serverSocket;
	        try {
	            serverSocket = new ServerSocket(LISTENING_PORT);
	        }
	        catch (Exception e) {
	            System.out.println("Failed to create listening socket.");
	            return;
	        }
	        System.out.println("Listening on port " + LISTENING_PORT);
	        try {
	            while (true) {
	                Socket connection = serverSocket.accept();
	                System.out.println("\nConnection from " 
	                        + connection.getRemoteSocketAddress());
	                handleConnection(connection);
	            }
	        }
	        catch(HTTPException e){
	            System.out.println("Invalid HTTP format");

	        } 
	        
	        catch (Exception e) {
	            System.out.println("Server socket shut down unexpectedly!");
	            System.out.println("Error: " + e);
	            System.out.println("Exiting.");
	        }
	    }

	private static void handleConnection(Socket connection) 
	throws HTTPException {
		// TODO Auto-generated method stub
		try {
			Scanner in = new Scanner(connection.getInputStream());
			while (true) {
				if ( ! in.hasNextLine() )
					break;
				String line = in.nextLine();
				if (line.trim().length() == 0)
					break;
				//System.out.println("   " + line);
				String[] myLine = line.trim().split(" ");
				System.out.println("\n\nAnd the winner is");
				System.out.println(!myLine[0].equals("GET") || (!myLine[myLine.length-1].equals("HTTP/1.1") && !myLine[myLine.length-1].equals("HTTP/1.0")));
				if (!myLine[0].equals("GET") || (!myLine[myLine.length-1].equals("HTTP/1.1") && !myLine[myLine.length-1].equals("HTTP/1.0")))
					throw new HTTPException(-1);
				for (String element : myLine){
					//System.out.println("Start of array \n\n");
					System.out.println(element);
				}
				
				System.out.println(System.getProperty("user.dir")+"This is my path");
				System.out.println(System.getProperty("user.dir")+"\\www"+myLine[1].replace("/", "\\"));
				File myFile = new File(rootDirectory +myLine[1].replace("/", "\\"));
				
				System.out.println("File "+myFile+" exists " + myFile.exists());
				System.out.println("File "+myFile+" is directory " + myFile.isDirectory());
				
				
				return;

			}
		}
		catch (Exception e) {
			System.out.println("Error while communicating with client: " + e);
		}
		finally {  // make SURE connection is closed before returning!
			try {
				connection.close();
			}
			catch (Exception e) {
			}
			System.out.println("Connection closed.");
		}
	}

}
