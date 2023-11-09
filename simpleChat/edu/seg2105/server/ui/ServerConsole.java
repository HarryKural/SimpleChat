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
		server = new EchoServer(port, this);
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
		System.out.println("SERVER MSG> " + message);
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
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!" + ex.getMessage());
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
