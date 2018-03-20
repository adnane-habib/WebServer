import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class WebServer {
	
	private final static int LISTENING_PORT = 50505;

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
				System.out.println("\n\n");
				for (String element : myLine){
					//System.out.println("Start of array \n\n");

					System.out.println(element);
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

}
