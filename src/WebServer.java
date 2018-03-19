import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
	
	private final static int LISTENING_PORT = 8080;

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
	        catch (Exception e) {
	            System.out.println("Server socket shut down unexpectedly!");
	            System.out.println("Error: " + e);
	            System.out.println("Exiting.");
	        }
	    }

	private static void handleConnection(Socket connection) {
		// TODO Auto-generated method stub
		
	}

}
