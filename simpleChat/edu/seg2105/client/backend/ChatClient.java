// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  // Check if the first character of the input is '#'
	  if (message.charAt(0) == '#') {
		  String command = message;
		  // Storing the strings/input from user,
		  // individually into array if they are separated with spaces
		  String[] split = message.split(" ");
		  
		switch (split[0]) {
		// Call quit() if the first element of the split array is #quit
		case "#quit":
			quit();
			break;
		// Call closeConnection() if the first element of the split array is #logoff
		case "#logoff":
			try {
				closeConnection();
			} catch (IOException e) {
				clientUI.display("Error logging out");
			}
			break;
		// Checks if user is connected, if not then calls
		// setHost() if the first element of the split array is #sethost
		case "#sethost":
			if (this.isConnected()) {
				clientUI.display("You are already connected!");
			}
			else {
				this.setHost(split[1]);
				clientUI.display("The host name has updated!");
			}
			break;
		// Checks if user is connected, if not then calls
		// setPort() if the first element of the split array is #setPort
		case "#setport":
			if (this.isConnected()) {
				clientUI.display("You are already connected!");
			}
			else {
				this.setPort(Integer.parseInt(split[1]));
				clientUI.display("The port number has updated!");
			}
			break;
		// Calls openConnections() if client is not already connected otherwise print message
		case "#login":
			if (!this.isConnected()) {
				try {
					openConnection();
					clientUI.display("You are logged in!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				clientUI.display("You are already connected!");
			}
			break;
		// Display host name
		case "#gethost":
			System.out.println("The Host number is: " + getHost());
			break;
		// Display port number
		case "#getport":
			clientUI.display("The Port number is: " + getPort());
			break;
		default:
			clientUI.display("Invalid command: " + command);
			break;
		}
	  }
	  // If user enters anything without the '#' symbol / not a command
	  else {
		  try {
			  sendToServer(message);
		  }
		  catch (IOException e) {
			  clientUI.display
			  ("Could not send message to server.  Terminating client.");
			  quit();
		  }
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
   * Implements the hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server.
   * 
   * @param exception
   *            the exception raised.
   */
  @Override
  protected void connectionException(Exception exception) {
	  clientUI.display("The server has shut down");
	  quit();
  }
  
  /**
   * Implements the hook method called after the connection has been closed.
   * The method perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  @Override
  protected void connectionClosed() {
	  clientUI.display("Connection closed");
  }
}
//End of ChatClient class
