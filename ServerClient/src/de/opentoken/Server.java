package de.opentoken;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Scanner;

public class Server
{
  private static void handleConnection( Socket client ) throws IOException
  {
    @SuppressWarnings("resource")
	Scanner     in  = new Scanner( client.getInputStream() );
    PrintWriter out = new PrintWriter( client.getOutputStream(), true );
    
    // hier wartet der Server auf die Daten des Clients, mit in.nextLine
    String factor1 = in.nextLine();
    String factor2 = in.nextLine();
    
    // hier kann der Server mit der Methode etwas senden out.println
    out.println( new BigInteger(factor1).multiply( new BigInteger(factor2) ) );
  }

  public static void main( String[] args ) throws IOException
  {
    @SuppressWarnings("resource")
	ServerSocket server = new ServerSocket( 3141 );

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