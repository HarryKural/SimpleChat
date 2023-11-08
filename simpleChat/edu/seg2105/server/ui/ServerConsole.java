package edu.seg2105.server.ui;

import java.io.*;
import java.util.*;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

/**
 * This class constructs the UI for a chat server. It implements the
 * chat interface in order to activate the display() method.
 */
public class ServerConsole implements ChatIF {
	
  //Class variables *************************************************
  
	/**
   	 * The default port to listen on.
   	*/
	final public static int DEFAULT_PORT = 5555;
	
	EchoServer server;
	Scanner fromConsole;
	
	//Constructors ****************************************************

	public ServerConsole(int port)
	{
		server = new EchoServer(port);
		fromConsole = new Scanner(System.in);
		
		try {
			server.listen();
		}
		catch(IOException e){
			System.out.println("Error: Can't setup connection!"
	                + " Terminating Server console.");
	      System.exit(1);
		}
	}
	
	// Class Method ****************************************************
	
	/**
     * This method overrides the method in the ChatIF interface. It
     * displays a message onto the screen. This method implements
     * commands functionality from server side
     *
     * @param message The string to be displayed.
     */
	@Override
	public void display(String message) {
		if (message.charAt(0) == '#') {
			String command = message;
			  // Storing the strings/input from user,
			  // individually into array if they are separated with spaces
			  String[] split = message.split(" ");
			  
			  switch (split[0]) {
			// Close the server
			case "#quit":
				System.out.println("Shutting down server");
				System.exit(0);
				break;
			// Server will stop listening for connections from new clients
			case "#stop":
				if (server.isListening()) {
					server.stopListening();
					System.out.println("Server has stopped listening for new connections");
				}
				else {
					System.out.println("Server has stopped listening");
				}
				break;
			// Stop listening for new connections then disconnect all the clients
			case "#close":
				if (server.isListening()) {
					server.stopListening();
					System.out.println("Server has stopped listening for connections." +
					System.lineSeparator() + "Disconnecting all clients...");
					
					try {
						server.close();
						System.out.println("Server has stopped");
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				break;
			// Calls setPort() from user input; only allowed if server is currently not connected
			case "#setport":
				if (server.isListening()) {
					System.out.println("Server already has an open connection");
				}
				else {
					server.setPort(Integer.parseInt(split[1]));
					System.out.println("The port number has updated: " + split[1]);
				}
				break;
			// Start listening for new connections, allowed if server is currently stopped
			case "#start":
				if (server.isListening()) {
					System.out.println("Server already running... listening for connections");
				}
				else {
					try {
						server.listen();
						}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				break;
			// Display port number
			case "#getport":
				if (server.isListening()) {
					System.out.println("The Port number is: " + server.getPort());
				}
				else {
					System.out.println("Server currently not listening");
				}
				break;
			default:
				System.out.println("Invalid command: " + command);
				break;
			}
			
		}
		// Echoing data types by end-user on server's console to server's console & all clients
		else
		{
			String msg = "SERVER MSG> " + message;
			server.sendToAllClients(msg);
			System.out.println(msg);
		}
	}
	
	//Instance methods ************************************************
	  
	/**
	 * This method waits for input from the console.  Once it is 
	 * received, it sends it to the server's console and the clients
	 */
	public void accept() 
	{
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        display(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	}
	
	//Class methods ***************************************************
	
	/**
	 * This method is responsible for the creation of 
	 * the server instance (there is no UI in this phase).
	 *
	 * @param args[0] The port number to listen on.  Defaults to 5555 
	 *          if no argument is entered.
	 */
	public static void main(String[] args) 
	{
	    int port = 0; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole serverConsole = new ServerConsole(port);
	    serverConsole.accept();  //Wait for console data
	}

}
//End of ServerConsole class
