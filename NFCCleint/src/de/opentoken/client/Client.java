package de.opentoken.client;


import java.net.*;
import java.util.Scanner;
import java.io.*;

class Client {
	private String ip;
	private int port;
	private Socket server = null;
	private String in;
	
	public Client( String ip, int port ) {
		this.ip = ip;
		this.port = port;
	}
	
	public void sendMsg( String[] msg ) {
		try
		{
		  server = new Socket( this.ip, this.port );
		  @SuppressWarnings("resource")
		  Scanner 	  input  = new Scanner( server.getInputStream() );
		  PrintWriter out = new PrintWriter( server.getOutputStream(), true );
		  
		  for (int i = 0; i < msg.length; i++) {
			  out.println( msg[i] );
		  }
		  this.in = input.nextLine();
	    }		
	    catch ( UnknownHostException e ) {
	      e.printStackTrace();
	    }
	    catch ( IOException e ) {
	      e.printStackTrace();
	    }
	    finally {
	      if ( server != null )
	        try { server.close(); } catch ( IOException e ) { }
	    }	  
	}
	
	public String getInput() {
		
		return in;
	}
}