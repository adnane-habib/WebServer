import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.xml.ws.http.HTTPException;



public class WebServerOld {
	
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
    			PrintWriter outgoing;   // Stream for sending data.
                outgoing = new PrintWriter( connection.getOutputStream() );
                
                if ( ! in.hasNextLine() )
					break;
				String line = in.nextLine();
				if (line.trim().length() == 0)
					break;

				String[] myLine = line.trim().split(" ");

				if (!myLine[0].equals("GET")){
					sendErrorResponse(501, outgoing);
					outgoing.flush();  // Make sure the data is actually sent!
		            connection.close();
		            return;
		            }
				
				else if (!myLine[myLine.length-1].equals("HTTP/1.1") && 
						!myLine[myLine.length-1].equals("HTTP/1.0")){
					sendErrorResponse(400, outgoing);
					outgoing.flush();  // Make sure the data is actually sent!
					connection.close();	
					return;
					}			
				
				File myFile = new File(rootDirectory +myLine[1].replace("/", "\\"));
				File myIndex = new File(rootDirectory +"\\index.html");
				
 				if (!myFile.exists() && !myIndex.exists() && !myFile.isDirectory()){
					sendErrorResponse(404, outgoing);
					outgoing.flush();  // Make sure the data is actually sent!
					connection.close();	
					return; 					
 				}
 				
 				else if (myFile.exists() || myIndex.exists() || myIndex.isDirectory()){
 				
				System.out.println("File "+myFile+" exists " + myFile.exists());
				System.out.println("File "+myFile+" is directory " + myFile.isDirectory());
				System.out.println("File "+myFile+" can read " + myFile.canRead());
			
	            outgoing.println( "HTTP/1.1 200 OK\r\n" );
	            outgoing.println( "Connection: close\r\n" );
	            outgoing.println( "Content-Length: "+myFile.length()+"\r\n" );
	            outgoing.println( "Content-Type: "+getMimeType(myFile.toString())+"\r\n" );
	            outgoing.println( "\r\n" );
				outgoing.flush();  // Make sure the data is actually sent!
				connection.close();	
				return;
 				}
 				else {
					sendErrorResponse(500, outgoing);
					outgoing.flush();  // Make sure the data is actually sent!
					connection.close();	
					return;
 				}
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
	private static String getMimeType(String fileName) {
        int pos = fileName.lastIndexOf('.');
        if (pos < 0)  // no file extension in name
            return "x-application/x-unknown";
        String ext = fileName.substring(pos+1).toLowerCase();
        if (ext.equals("txt")) return "text/plain";
        else if (ext.equals("html")) return "text/html";
        else if (ext.equals("htm")) return "text/html";
        else if (ext.equals("css")) return "text/css";
        else if (ext.equals("js")) return "text/javascript";
        else if (ext.equals("java")) return "text/x-java";
        else if (ext.equals("jpeg")) return "image/jpeg";
        else if (ext.equals("jpg")) return "image/jpeg";
        else if (ext.equals("png")) return "image/png";
        else if (ext.equals("gif")) return "image/gif"; 
        else if (ext.equals("ico")) return "image/x-icon";
        else if (ext.equals("class")) return "application/java-vm";
        else if (ext.equals("jar")) return "application/java-archive";
        else if (ext.equals("zip")) return "application/zip";
        else if (ext.equals("xml")) return "application/xml";
        else if (ext.equals("xhtml")) return"application/xhtml+xml";
        else return "x-application/x-unknown";
           // Note:  x-application/x-unknown  is something made up;
           // it will probably make the browser offer to save the file.
     }
	 static void sendErrorResponse(int errorCode, PrintWriter socketOut){
		 switch (errorCode){
		 case 400:
			 System.out.println("ERROR 400");
			 socketOut.println("HTTP/1.1 400 Bad Request\r\n"+
					 "Connection: close\r\n"+
					 "Content-Type: text/html\r\n"+"\r\n"+
					 "<html><head><title>Error</title></head><body>\r\n"+
					 "<h2>Error: 400 Bad Request</h2>\r\n"+
					 "<p>The resource that you requested does not exist on this server.</p>\r\n"+
					 "</body></html>\r\n");
			 break;
		 case 403:
			 System.out.println("ERROR 403");
			 socketOut.println("HTTP/1.1 403 Forbidden\r\n"+
					 "Connection: close\r\n"+
					 "Content-Type: text/html\r\n"+"\r\n"+
					 "<html><head><title>Error</title></head><body>\r\n"+
					 "<h2>Error: 403 Forbidden</h2>\r\n"+
					 "<p>The resource that you requested does not exist on this server.</p>\r\n"+
					 "</body></html>\r\n");
			 break;
		 case 404:
			 System.out.println("ERROR 404");
			 socketOut.println("HTTP/1.1 404 Not Found\r\n"+
					 "Connection: close\r\n"+
					 "Content-Type: text/html\r\n"+"\r\n"+
					 "<html><head><title>Error</title></head><body>\r\n"+
					 "<h2>Error: 404 Not Found</h2>\r\n"+
					 "<p>The resource that you requested does not exist on this server.</p>\r\n"+
					 "</body></html>\r\n");
			 break;			 			 
		 case 500:
			 System.out.println("ERROR 500");
			 socketOut.println("HTTP/1.1 500 Internal Server Error\r\n"+
					 "Connection: close\r\n"+
					 "Content-Type: text/html\r\n"+"\r\n"+
					 "<html><head><title>Error</title></head><body>\r\n"+
					 "<h2>Error: 500 Internal Server Error</h2>\r\n"+
					 "<p>The resource that you requested does not exist on this server.</p>\r\n"+
					 "</body></html>\r\n");
			 break;
		 case 501:
			 System.out.println("ERROR 501");
			 socketOut.println("HTTP/1.1 501 Not Implemented\r\n"+
					 "Connection: close\r\n"+
					 "Content-Type: text/html\r\n"+"\r\n"+
					 "<html><head><title>Error</title></head><body>\r\n"+
					 "<h2>Error: 501 Not Impelemented</h2>\r\n"+
					 "<p>The resource that you requested does not exist on this server.</p>\r\n"+
					 "</body></html>\r\n");
			 break;
			 default:
				 break;
			 }
		 
	 }// end of sendErrorResponse() method
	 
	 private static void sendFile(File file, OutputStream socketOut) throws
	  IOException {
	    InputStream in = new BufferedInputStream(new FileInputStream(file));
	    OutputStream out = new BufferedOutputStream(socketOut);
	    while (true) {
	      int x = in.read(); // read one byte from file
	      if (x < 0)
	         break; // end of file reached
	      out.write(x);  // write the byte to the socket
	   }
	   out.flush();
	}
	 
}
