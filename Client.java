import java.io.*;
import java.net.*;

/**
 * Client class contains the main connection and server communication methods. 
 *  
 * @author Maxence Weyrich
 *
 */
public class Client implements Runnable {

   private Socket server;
   private PrintStream out;
   private BufferedReader in;
   private UI frame;

   private boolean isConnected;
   //private String userName;
   private Thread p;
   
   /**
    * Main entry point
    */
   public static void main(String[] args) {
      new Client();
   }

   /**
    * Construct a new UI & set defaults.
    */
   public Client() {
	  frame = new UI(this);

      isConnected = false;
      
   }
   
   /**
    * Establish the connection based on user input
    * @param userName String the username to send to the server
    * @param password char[] the password submitted 
    * @param address InetAddress - the address of the server
    * @param port int the port to which the client will attempt to communicate to the server with
    * @throws IOException 
    */
   public void connect(String userName, char[] password, InetAddress address, int port) throws IOException   {
	   //this.userName = userName;

	   server = new Socket(address, port);

	   out = new PrintStream(new BufferedOutputStream(server.getOutputStream()));
	   in = new BufferedReader(new InputStreamReader(server.getInputStream()));
	   
       out.print("NCR" + userName); //send server username followed by password
       if(password.length > 0) { //if there is a password, send a separator char, then the password
    	   out.print('\3');
           for(int i = 0; i < password.length; i++) {
        	   out.print(password[i]);
        	   password[i] = '\0'; //clear the password -- for security? i mean there are other things that are less secure about this program but i mean i guess its a good practice.....
           }
       }
       out.print('\0');
       out.flush();
	   
       
	   p = new Thread(this);
	   p.start(); //start listening
	       	
   }
   
   /**
    * Handle the incoming message and take appropriate action based on
    * the code ID included in the message. 
    * @param s StringBuilder containing the server response data.
    */
   public void handleMessage(StringBuilder s) {

	      String cmd = s.substring(0,3);
	      String content = s.substring(3);

	      if(cmd.equals("CON")) {
		   	   isConnected = true; //now connected!
			   frame.setConnected(isConnected); //enable buttons and text bar
			   frame.addNotification("You are now connected to the server");
			  
	      } else if(cmd.equals("NCN")) {
	    	  
	    	  isConnected = false;
	  		  try {
  			     /*
  			      * this sleep is to give a moment for the UI to refresh & look like its connecting
  			      * that way when people enter a bad info once again, they will know that the error is new info
  			      * rather than the UI not refreshing from the previous attempt.
  			      */
				  Thread.sleep(250);
			  } catch(Exception ex) {
				  ex.printStackTrace();
			  }
	    	  frame.showConnectionResult(content);
	    	  
	      } else if(cmd.equals("MSG")) {

	         int separator = content.indexOf('\3')+1;
             frame.addMessage(content.substring(separator), content.substring(0, separator-1));

	      } else if(cmd.equals("NOT")) {
	    	  
	    	  frame.addNotification(content);
	    	 
	      } else if(cmd.equals("DSC")) {
	    	  
	    	  frame.addNotification("Disconnected from the server");
	    	  if(content.length() != 0) {
	    		  frame.addNotification("Message from server: " + content);
	    	  }
	    	  
	    	  cleanConnection();
	    	  
	      } else if(cmd.equals("RSP")) {
	    	  
	    	  frame.addCommandResponse(content);
	    	  
	      } else if(cmd.equals("ERC")) {

	         frame.addError(content);
	         

	      } else {
	    	  
	    	 out.print("ERSUnknown message \"" + cmd + "\"\nFull message: " + s + '\0');
	    	  
	         System.out.println("Unknown??\n" + s);
	      }
	   }
   
   
   /**
    * Sends an admin command to the server
    * @param msg String command to send to server
    */
   public void sendCommand(String msg) {
	   out.print("ADM" + msg + '\0');
	   out.flush();
   }
   
   /**
    * Gets user input and sends it to the sever.
    * @param msg String the message that the client has sent to the server
    */
   public void sendMessage(String msg) {
     out.print("SND" + msg + '\0');
     out.flush();
   }

   /**
    * Called when the window is closing to provide for a graceful exit.
    */
   public void close() {
	   if(isConnected) {
		   out.println("DSC\0");
		   out.flush();
	   }
   }
   
   /**
    * Cleans connection by closing ports, and setting values accordingly
    */
   private void cleanConnection() {
	   try {
		   server.shutdownInput();
		   server.shutdownOutput();
		   server.close();
		   isConnected = false;
		   frame.setConnected(isConnected);
	   } catch(Exception e) {
		   e.printStackTrace();
	   }
   }
   
   /**
    * Listens for messages from the server.
    * Saves all characters into a StringBuilder until the end of the message, at which point
    * begin processing the message.
    */
   public void run() {
      int character;
      StringBuilder sb = new StringBuilder();
      try {
         while(!server.isClosed()) {
        	
            while( (character = in.read()) > 0 ) {
            	sb.append((char)character);
            }
            
            if(character == -1) { //pipe closed. Handle connection close
            	if(isConnected)
            		frame.addNotification("Disconnected from the server");
            	cleanConnection();
            	break;
            }
            handleMessage(sb);
            sb.setLength(0);
         }
      } catch (Exception e) {
         cleanConnection();
         frame.addNotification("Connection to the server was lost");
      }
   }

}