/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lockserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author s0552871
 */
public class LockServer {
    
    private static final String[][] permissions = {
            {"11111111", "87654321"}, // lock 0
            {"11111111"}, // lock 1
            {"22222222"} // lock 2
        };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        try {
            String clientSentence;
            String returnToSender;
            ServerSocket welcomeSocket = new ServerSocket(6789);
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient = new BufferedReader(
                        new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                clientSentence = inFromClient.readLine();
                System.out.println("Received: " + clientSentence);
                
                boolean isAllowed = checkPermission(clientSentence);
                if(isAllowed) {
                    returnToSender = "TRUE";
                } else {
                    returnToSender = "FALSE";
                }
                
                outToClient.writeBytes(returnToSender + "\n");
               
            }
        } catch(IOException io) {
            System.out.println("Error while Listening: " + io.getMessage());
        }
    }
    
    private static boolean checkPermission(String message) {
        boolean result = false;
        
        String[] msgSplit = message.split(":");
        int lockId = Integer.parseInt(msgSplit[0]);
        String keyId = msgSplit[1];
        
        if(lockId >= 0 && lockId < permissions.length) {
            for(int i = 0; i < permissions[lockId].length; i++) {
                if(keyId.contains(permissions[lockId][i])) {
                    result = true;
                }
            }
        }
        
        return result;
    }

}
