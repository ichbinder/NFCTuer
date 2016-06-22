/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lockarduino;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import jssc.*;

/**
 *
 * @author s0552871
 */
public class LockArduino {

    private static final String lockId = "1";

    public static void main(String[] args) {
        System.out.println("Central Locking System\n=====");

        // first populate the serial ports list
        String chosenPort = choosePort();
        if (chosenPort == null) {
            System.out.println("No port chosen - Exit now.");
            System.exit(0);
        }

        // connect to the port
        SerialPort serialPort = connect(chosenPort);
        if (serialPort == null) {
            System.out.println("No connection possible - Exit now.");
            System.exit(0);
        }

        // Register port reader
        try {
            serialPort.addEventListener(
                    new PortReader(serialPort),
                    SerialPort.MASK_RXCHAR
                    + SerialPort.MASK_CTS
                    + SerialPort.MASK_DSR);
        } catch (SerialPortException ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }

        // write mode
        /*
         String command = "";
         do {
         command = waitForCommand();
         if(command != "exit") {
         try {
         serialPort.writeString(command);
         } catch(SerialPortException ex) {
         System.out.println("Error: " + ex.getMessage());
         }
         }
         } while (command != "exit");
         */
    }

    private static String waitForCommand() {
        System.out.print("Enter command (\"exit\" for end): ");
        Scanner scanIn = new Scanner(System.in);
        String command = null;
        try {
            System.out.println("X1");
            command = scanIn.nextLine();
            System.out.println("X2");
        } catch (NoSuchElementException ex) {

        }

        scanIn.close();
        return command;
    }

    private static SerialPort connect(String portName) {
        SerialPort serialPort = new SerialPort(portName);

        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
            //                              SerialPort.FLOWCONTROL_RTSCTS_OUT);
        } catch (SerialPortException ex) {
            System.out.println("Error: " + ex.getMessage());
            return null;
        }

        return serialPort;
    }

    private static String choosePort() {
        // List of names
        String[] portNames = SerialPortList.getPortNames();
        System.out.println("List of serial ports");
        for (int i = 0; i < portNames.length; i++) {
            System.out.println(i + ":\t" + portNames[i]);
        }

        // let the user choose
        System.out.print("Your choice: ");
        Scanner scanIn = new Scanner(System.in);
        int choice = scanIn.nextInt();
        scanIn.close();
        System.out.println("Choice is: " + choice);

        // check the choice
        String result = null;
        if (choice >= 0 && choice < portNames.length) {
            result = portNames[choice];
        }

        return result;
    }

    private static class PortReader implements SerialPortEventListener {

        private SerialPort serialPort;

        public PortReader(SerialPort serPort) {
            this.serialPort = serPort;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {

            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    String receivedData = serialPort.readString(event.getEventValue());
                    System.out.print(receivedData);
                    checkLock(receivedData);
                } catch (SerialPortException ex) {
                    System.out.println("Error in receiving string from COM-port: " + ex);
                }
            }
        }

        private void checkLock(String input) throws SerialPortException {
            String keyId = input.length() > 8 ? input.substring(0, 8) : input;
            boolean isAllowed = askServer(keyId);
            
            if(isAllowed) {
                serialPort.writeString("TRUE");
            } else {
                serialPort.writeString("FALSE");
            }
        }

        private boolean askServer(String keyId) {
            boolean result = false;
            
            String message = lockId + ":" + keyId + "\n";
            System.out.println("Sending:" + message);
            
            try (Socket clientSocket = new Socket("localhost", 6789)) {
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.writeBytes(message);
                String answer = inFromServer.readLine();
                System.out.println("FROM SERVER: " + answer);
                
                if(answer.equals("TRUE")) {
                    result = true;
                }
            } catch(IOException ex) {
                System.out.println("Could not contact server: " +ex.getMessage());
            }
            
            return result;
        }

    }

}
