package de.opentoken;

import java.net.*;
import java.util.Scanner;
import java.io.*;

class Client
{
  @SuppressWarnings("resource")
public static void main( String[] args )
  {
    Socket server = null;

    try
    {
      server = new Socket( "localhost", 3141 );
      Scanner     in  = new Scanner( server.getInputStream() );
      PrintWriter out = new PrintWriter( server.getOutputStream(), true );
      
      
      // hier kann mit out.println was gesendet werden zum Server, hier sind nur zwei bsp's
      out.println( "2" );
      out.println( "4" );
      
      // mit in.nextLine kann was vom Server empfangen werden 
      System.out.println( in.nextLine() );

      server = new Socket( "localhost", 3141 );
      in  = new Scanner( server.getInputStream() );
      out = new PrintWriter( server.getOutputStream(), true );

      out.println( "23895737895" );
      out.println( "434589358935857" );
      System.out.println( in.nextLine() );
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
}