import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.xml.ws.http.HTTPException;
/**
 * This program creates a multi-connections web server 
 * @author MedAdnane
 *
 */
public class WebServer {
	// port is defined as a final variable
	private final static int LISTENING_PORT = 50505; 
	// Path to /www folder is defined dynamically using relative path
	private final static String rootDirectory = System.getProperty("user.dir") + "\\www";

	public static void main(String[] args) {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(LISTENING_PORT);
		} catch (Exception e) {
			System.out.println("Failed to create listening socket.");
			return;
		}
		System.out.println("Listening on port " + LISTENING_PORT);
		try {
			while (true) {
				Socket connection = serverSocket.accept();
				System.out.println("\nConnection from " + connection.getRemoteSocketAddress());
				//handleConnection(connection);
				//connections are handleded by thread to allow multiple connections
				ConnectionThread thread = new ConnectionThread(connection);
				thread.start();
			}
		} catch (HTTPException e) {
			System.out.println("Invalid HTTP format");

		}

		catch (Exception e) {
			System.out.println("Server socket shut down unexpectedly!");
			System.out.println("Error: " + e);
			System.out.println("Exiting.");
		}
	} // End of main() method

	private static void handleConnection(Socket connection) {
		// TODO Auto-generated method stub
		try {
			Scanner in = new Scanner(connection.getInputStream());

			while (true) {
				OutputStream outgoing; // Stream for sending data.
				outgoing = new BufferedOutputStream(connection.getOutputStream());

				if (!in.hasNextLine())
					break;
				String line = in.nextLine();
				if (line.trim().length() == 0)
					break;
				// request is converted to strings to verify if format is valid
				String[] myLine = line.trim().split(" ");
				// if request is not valid, error is handled and response is submitted to remote client
				if (!myLine[0].equals("GET")) {
					sendErrorResponse(501, outgoing);
					outgoing.flush(); // Make sure the data is actually sent!
					connection.close();
					return;
				}

				else if (!myLine[myLine.length - 1].equals("HTTP/1.1")
						&& !myLine[myLine.length - 1].equals("HTTP/1.0")) {
					sendErrorResponse(400, outgoing);
					outgoing.flush(); // Make sure the data is actually sent!
					connection.close();
					return;
				}

				File myFile = new File(rootDirectory + myLine[1].replace("/", "\\"));
				File myIndex = new File(rootDirectory + "\\index.html");

				if (!myFile.exists() && !myIndex.exists() && !myFile.isDirectory()) {
					sendErrorResponse(404, outgoing);
					outgoing.flush(); // Make sure the data is actually sent!
					connection.close();
					return;
				}
				// valid requests are processed, if requested file is present, it is transmitted
				//otherwise, user is redirected to homepage
				else if (myFile.exists() || myIndex.exists() || myFile.isDirectory()) {

					try {
						if (myFile.exists()) {
							outgoing.write("HTTP/1.1 200 OK\r\n".getBytes());
							outgoing.write("Connection: close\r\n".getBytes());
							outgoing.write(("Content-Length: " + myFile.length() + "\r\n").getBytes());
							outgoing.write(("Content-Type: " + getMimeType(myFile.toString()) + "\r\n").getBytes());
							outgoing.write("\r\n".getBytes());
							if (myFile.canRead()) {

								System.out.println("My File exists");
								sendFile(myFile, outgoing);
								outgoing.flush(); // Make sure the data is
													// actually
								connection.close();
							} else if (myFile.isDirectory()) {
								System.out.println("My Directory exists");
								System.out.println("Files are " + myFile.list());
								try {

									String[] files = myFile.list();
									String concat = "";
									for (String subFile : files) {
										concat += "\n" + subFile;
										System.out.println(subFile);
									}
									outgoing.write(concat.getBytes());
									outgoing.flush(); // Make sure the data is
														// actually
									connection.close();
								} catch (FileNotFoundException e) {
									sendErrorResponse(403, outgoing);
								}
							}
						}

						else if (myIndex.exists()) {
							outgoing.write("HTTP/1.1 200 OK\r\n".getBytes());
							outgoing.write("Connection: close\r\n".getBytes());
							outgoing.write(("Content-Length: " + myIndex.length() + "\r\n").getBytes());
							outgoing.write(("Content-Type: " + getMimeType(myFile.toString()) + "\r\n").getBytes());
							outgoing.write("\r\n".getBytes());

							System.out.println("My index exists");
							sendFile(myIndex, outgoing);
							outgoing.flush(); // Make sure the data is actually
							connection.close();
						}

						else {
							sendErrorResponse(500, outgoing);
							outgoing.flush(); // Make sure the data is actually
												// sent!
							connection.close();
							return;
						}

					}

					catch (IOException e) {
						System.out.println(e);
						outgoing.flush();
					} finally {
						outgoing.flush(); // Make sure the data is actually
											// sent!
						connection.close();
						return;
					}

				}

				else {
					sendErrorResponse(500, outgoing);
					outgoing.flush(); // Make sure the data is actually sent!
					connection.close();
					return;
				}
			}
		} catch (Exception e) {
			System.out.println("Error while communicating with client: " + e);
		} finally { // make SURE connection is closed before returning!
			try {
				connection.close();
			} catch (Exception e) {
			}
			System.out.println("Connection closed.");
		}
	}// End of handleConnection()

	/**
	 * This methid takes a file a return a string with its type
	 * @param fileName
	 * @return corresponds to file type
	 */
	private static String getMimeType(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if (pos < 0) // no file extension in name
			return "x-application/x-unknown";
		String ext = fileName.substring(pos + 1).toLowerCase();
		if (ext.equals("txt"))
			return "text/plain";
		else if (ext.equals("html"))
			return "text/html";
		else if (ext.equals("htm"))
			return "text/html";
		else if (ext.equals("css"))
			return "text/css";
		else if (ext.equals("js"))
			return "text/javascript";
		else if (ext.equals("java"))
			return "text/x-java";
		else if (ext.equals("jpeg"))
			return "image/jpeg";
		else if (ext.equals("jpg"))
			return "image/jpeg";
		else if (ext.equals("png"))
			return "image/png";
		else if (ext.equals("gif"))
			return "image/gif";
		else if (ext.equals("ico"))
			return "image/x-icon";
		else if (ext.equals("class"))
			return "application/java-vm";
		else if (ext.equals("jar"))
			return "application/java-archive";
		else if (ext.equals("zip"))
			return "application/zip";
		else if (ext.equals("xml"))
			return "application/xml";
		else if (ext.equals("xhtml"))
			return "application/xhtml+xml";
		else
			return "x-application/x-unknown";
		// Note: x-application/x-unknown is something made up;
		// it will probably make the browser offer to save the file.
	}
	
	/**
	 * This method takes the error code and return an html code 
	 * @param errorCode	standard HTTP error codes
	 * @param socketOut	standard HTTP response
	 */

	static void sendErrorResponse(int errorCode, OutputStream socketOut) {
		try {
			switch (errorCode) {
			case 400:
				System.out.println("ERROR 400");
				socketOut.write(("HTTP/1.1 400 Bad Request\r\n" + "Connection: close\r\n"
						+ "Content-Type: text/html\r\n" + "\r\n" + "<html><head><title>Error</title></head><body>\r\n"
						+ "<h2>Error: 400 Bad Request</h2>\r\n"
						+ "<p>The resource that you requested does not exist on this server.</p>\r\n"
						+ "</body></html>\r\n").getBytes());
				break;
			case 403:
				System.out.println("ERROR 403");
				socketOut.write(("HTTP/1.1 403 Forbidden\r\n" + "Connection: close\r\n" + "Content-Type: text/html\r\n"
						+ "\r\n" + "<html><head><title>Error</title></head><body>\r\n"
						+ "<h2>Error: 403 Forbidden</h2>\r\n"
						+ "<p>The resource that you requested does not exist on this server.</p>\r\n"
						+ "</body></html>\r\n").getBytes());
				break;
			case 404:
				System.out.println("ERROR 404");
				socketOut.write(("HTTP/1.1 404 Not Found\r\n" + "Connection: close\r\n" + "Content-Type: text/html\r\n"
						+ "\r\n" + "<html><head><title>Error</title></head><body>\r\n"
						+ "<h2>Error: 404 Not Found</h2>\r\n"
						+ "<p>The resource that you requested does not exist on this server.</p>\r\n"
						+ "</body></html>\r\n").getBytes());
				break;
			case 500:
				System.out.println("ERROR 500");
				socketOut.write(("HTTP/1.1 500 Internal Server Error\r\n" + "Connection: close\r\n"
						+ "Content-Type: text/html\r\n" + "\r\n" + "<html><head><title>Error</title></head><body>\r\n"
						+ "<h2>Error: 500 Internal Server Error</h2>\r\n"
						+ "<p>The resource that you requested does not exist on this server.</p>\r\n"
						+ "</body></html>\r\n").getBytes());
				break;
			case 501:
				System.out.println("ERROR 501");
				socketOut.write(("HTTP/1.1 501 Not Implemented\r\n" + "Connection: close\r\n"
						+ "Content-Type: text/html\r\n" + "\r\n" + "<html><head><title>Error</title></head><body>\r\n"
						+ "<h2>Error: 501 Not Impelemented</h2>\r\n"
						+ "<p>The resource that you requested does not exist on this server.</p>\r\n"
						+ "</body></html>\r\n").getBytes());
				break;
			default:
				break;
			}
		} catch (IOException e) {

		}

	}// end of sendErrorResponse() method

	/**
	 * THis method takes a file and an active connection and transmits the file through the connection
	 * @param file
	 * @param socketOut
	 * @throws IOException
	 */
	private static void sendFile(File file, OutputStream socketOut) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		OutputStream out = new BufferedOutputStream(socketOut);
		while (true) {
			int x = in.read(); // read one byte from file
			if (x < 0)
				break; // end of file reached
			out.write(x); // write the byte to the socket
		}
		out.flush();
		in.close();
	}
	/**
	 * This Class takes care of multiple connections
	 * @author MedAdnane
	 *
	 */
    private static class ConnectionThread extends Thread {
        Socket connection;
        ConnectionThread(Socket connection) {
           this.connection = connection;
        }
        public void run() {
           handleConnection(connection);
        }
     }
}
