package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{ 
	
   // Instance variables **********************************************
	
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF serverUI;

  // login key used for storing client's login id
  private static final String LOGIN_KEY = "loginID";

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {  
	  String message = (String) msg;
	  // Print client's message on server console
	  serverUI.display("Message received: " + msg + " from " + client);
	  
	// If msg received from client is "#login"
	  if (message.startsWith("#login")) {
		  // 
		  if (client.getInfo(LOGIN_KEY) == null) {
			  client.setInfo(LOGIN_KEY, message.replaceAll("#login ", ""));
			  serverUI.display("Message received: #login <loginID> from null.");
			  // print server message indicating success in logging in client
			  serverUI.display(client.getInfo(LOGIN_KEY) + " has logged on.");
			  // print client message indication success in login
			  this.sendToAllClients(client.getInfo(LOGIN_KEY) + " has logged on.");
		  }
		  else {
			  // if client already logged in then close connection
			  try {
				  serverUI.display("ERROR - already logged in. Connection closing");
				  client.close();
			  }
			  catch (IOException e) {
				  serverUI.display("Unable to close connection");
			  }
		  }
	  }
	  // else if message received from the client is not a command
	  else {
		  this.sendToAllClients(client.getInfo(LOGIN_KEY) + ": " + message);
	  }
  }
  
	/**
	 * This method handles any messages received from the server UI.
	 *
	 * @param msg The message received from the server
	 */
	public void handleMessageFromServerUI(String message) {

		if (message.startsWith("#")) {
			handleCommandFromServerUI(message);
			
		}
		// Echoing data typed by end-user on server's console to server's console & all clients
		else {
			serverUI.display(message);
			this.sendToAllClients("SERVER MSG> " + message);

		}
	}

  /**
   * Method to handle commands input by user on the Server UI console
   * @param serverCommand command for server to handle
   * @throws IOException throws IOException for invalid command or other errors
   */
  private void handleCommandFromServerUI(String message) {
	  if (message.charAt(0) == '#') {
			String command = message;
			  // Storing the strings/input from user,
			  // individually into array if they are separated with spaces
			  String[] split = message.split(" ");
			  
			  switch (split[0]) {
			// Close the server
			case "#quit":
				serverUI.display("Shutting down server");
				System.exit(0);
				break;
			// Server will stop listening for connections from new clients
			case "#stop":
				if (isListening()) {
					stopListening();
					serverUI.display("Server has stopped listening for new connections");
				}
				else {
					serverUI.display("Server has stopped listening");
				}
				break;
			// Stop listening for new connections then disconnect all the clients
			case "#close":
				if (isListening()) {
					stopListening();
					serverUI.display("Server has stopped listening for connections." +
					System.lineSeparator() + "Disconnecting all clients...");
					
					try {
						close();
						serverUI.display("Server has stopped");
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				break;
			// Calls setPort() from user input; only allowed if server is currently not connected
			case "#setport":
				if (isListening()) {
					serverUI.display("Server already has an open connection");
				}
				else {
					setPort(Integer.parseInt(split[1]));
					serverUI.display("The port number has updated: " + split[1]);
				}
				break;
			// Start listening for new connections, allowed if server is currently stopped
			case "#start":
				if (isListening()) {
					serverUI.display("Server already running... listening for connections");
				}
				else {
					try {
						listen();
						}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				break;
			// Display port number
			case "#getport":
				if (isListening()) {
					serverUI.display("The Port number is: " + getPort());
				}
				else {
					serverUI.display("Server currently not listening");
				}
				break;
			default:
				serverUI.display("Invalid command: " + command);
				break;
			}
			
		}
	  	// else if no command recognized 
		else
		{
			serverUI.display("Error - Invalid command");
		}
	  
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  serverUI.display("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	  serverUI.display("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   *  This method is responsible for displaying a message to server
   *  when a client is connected to the server
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  serverUI.display("Client has connected to the Server");
  }
  
  /**
   *  This method is responsible for displaying a message to server
   *  when a client is disconnected from the server
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  serverUI.display("Connection disconnected with client: " + client.getInfo(LOGIN_KEY));
  }

}
//End of EchoServer class
