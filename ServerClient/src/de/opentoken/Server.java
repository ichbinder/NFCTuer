package de.opentoken;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Server
{
  private static void handleConnection( Socket client ) throws IOException
  {
    @SuppressWarnings("resource")
	Scanner     in  = new Scanner( client.getInputStream() );
    PrintWriter out = new PrintWriter( client.getOutputStream(), true );
    
    // hier wartet der Server auf die Daten des Clients, mit in.nextLine
    String raumNummer = in.nextLine();
    String name = in.nextLine();
    
    JSONParser parser = new JSONParser();
    
    try {
        Object obj = parser.parse(new FileReader("permissions.txt"));

        JSONObject jsonObject = (JSONObject) obj;

        String namefile = (String) jsonObject.get("Name");
        JSONArray permissionDatabase = (JSONArray) jsonObject.get("user permissions");

        @SuppressWarnings("unchecked")
		Iterator<String> iterator = permissionDatabase.iterator();
        boolean entersRight = false;
        while (iterator.hasNext()) {
            String[] permiFile = iterator.next().split(":");
            if ( name.equals(permiFile[0]) )
            	if ( raumNummer.equals(permiFile[1]) ) {
            		entersRight = true;
            		break;
            	}
        }
        
        if (entersRight)
        	out.println("yes");
        else
        	out.println("no");

    } catch (Exception e) {
        e.printStackTrace();
    }    
    // hier kann der Server mit der Methode etwas senden out.println
    // out.println( new BigInteger(factor1).multiply( new BigInteger(factor2) ) );
  }

  public static void main( String[] args ) throws IOException
  {
    @SuppressWarnings("resource")
	ServerSocket server = new ServerSocket( 3001 );

    while ( true )
    {
      Socket client = null;

      try
      {
        client = server.accept();
        handleConnection ( client );
      }
      catch ( IOException e ) {
        e.printStackTrace();
      }
      finally {
        if ( client != null )
          try { client.close(); } catch ( IOException e ) { }
      }
    }
  }
}