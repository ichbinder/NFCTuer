/**************************************************************************/
/*!
This example attempts to write 16 bytes to a Mifare Classic 1K card

Note that you need the baud rate to be 115200 because we need to print
out the data and read from the card at the same time!

To enable debug message, define DEBUG in PN532/PN532_debug.h
*/
/**************************************************************************/

#include <Wire.h>
#include <SPI.h>
#include <Adafruit_PN532.h>

// If using the breakout with SPI, define the pins for SPI communication.
#define PN532_SCK	(2)
#define PN532_MOSI (3)
#define PN532_SS	 (4)
#define PN532_MISO (5)

// If using the breakout or shield with I2C, define just the pins connected
// to the IRQ and reset lines.	Use the values below (2, 3) for the shield!
#define PN532_IRQ	 (2)
#define PN532_RESET (3)	// Not connected by default on the NFC Shield

// Uncomment just _one_ line below depending on how your breakout or shield
// is connected to the Arduino:

// Use this line for a breakout with a software SPI connection (recommended):
//Adafruit_PN532 nfc(PN532_SCK, PN532_MISO, PN532_MOSI, PN532_SS);

// Use this line for a breakout with a hardware SPI connection.	Note that
// the PN532 SCK, MOSI, and MISO pins need to be connected to the Arduino's
// hardware SPI SCK, MOSI, and MISO pins.	On an Arduino Uno these are
// SCK = 13, MOSI = 11, MISO = 12.	The SS line can be any digital IO pin.
Adafruit_PN532 nfc(10); // !!! It works with SPI NFC Shield V1.0 !!!

                        // Or use this line for a breakout or shield with an I2C connection:
                        //Adafruit_PN532 nfc(PN532_IRQ, PN532_RESET); // !!! It works with I2C Adafruit PN532 RFID/NFC Shield !!!

#if defined(ARDUINO_ARCH_SAMD)
                        // for Zero, output on USB Serial console, remove line below if using programming port to program the Zero!
                        // also change #define in Adafruit_PN532.cpp library file
#define Serial SerialUSB
#endif

uint8_t success;													// Flag to check if there was an error with the PN532
uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };	// Buffer to store the returned UID
uint8_t uidLength;												// Length of the UID (4 or 7 bytes depending on ISO14443A card type)
uint8_t currentblock;										 // Counter to keep track of which block we're on
bool authenticated = false;							 // Flag to indicate if the sector is authenticated
uint8_t data[16];												 // Array to store block data during reads

                                                                 // Keyb on NDEF and Mifare Classic should be the same
uint8_t keyuniversal[6] = { 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };

String inputFromSrv = "";
boolean inputComplete = false;
boolean waitingForServerResponse = false;
//boolean waitingForNFCTag = false;

void setup(void) {

  // initialize digital pin 13 as an output.
  pinMode(13, OUTPUT);
  blinkResponse(3,500);
  
#ifndef ESP8266    
    while (!Serial); // for Leonardo/Micro/Zero
#endif
    Serial.begin(115200);
    Serial.println("Key Lock!");

    nfc.begin();
    
    blinkResponse(20,50);
    Serial.println("INIT nfc.begin() DONE");

    uint32_t versiondata = nfc.getFirmwareVersion();
    if (!versiondata) {
        Serial.print("Didn't find PN53x board");
        while (1); // halt
    }
    // Got ok data, print it out!
    Serial.print("Found chip PN5"); Serial.println((versiondata >> 24) & 0xFF, HEX);
    Serial.print("Firmware ver. "); Serial.print((versiondata >> 16) & 0xFF, DEC);
    Serial.print('.'); Serial.println((versiondata >> 8) & 0xFF, DEC);

    // configure board to read RFID tags
    nfc.SAMConfig();
    Serial.println("Waiting for an ISO14443A Card ...");
    blinkResponse(10,500);
    Serial.println("INIT nfc.SAMConfig() DONE");
}


void loop(void) {
  
    // Wait for an ISO14443A type cards (Mifare, etc.).	When one is found
    // 'uid' will be populated with the UID, and uidLength will indicate
    // if the uid is 4 bytes (Mifare Classic) or 7 bytes (Mifare Ultralight)
    if (!waitingForServerResponse) {
    
      success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  
      if (success) {
          // Display some basic information about the card
          Serial.println("Found an ISO14443A card");
          Serial.print("	UID Length: "); Serial.print(uidLength, DEC); Serial.println(" bytes");
          Serial.print("	UID Value: ");
          for (uint8_t i = 0; i < uidLength; i++) {
              Serial.print(uid[i], HEX);
              Serial.print(' ');
          }
          Serial.println("");
  
          if (uidLength == 4) {
              // We probably have a Mifare Classic card ...
              Serial.println("Seems to be a Mifare Classic card (4 byte UID)");
  
              Serial.println("Reading block 42");
              read(42);
          }
      }
      //waitingForNFCTag = false;
      waitingForServerResponse = true;
    } 
    
    if(waitingForServerResponse) 
    {
      serialEvent(); //read serial input
     
      if (inputComplete) {
          //Serial.print("Msg from Server: ");
          Serial.println(inputFromSrv);
  
          if (inputFromSrv.equalsIgnoreCase("OK")) {
              Serial.println("--ACCESS GRANTED--");
              //blink 5Hz
              Serial.println("blink 5.0 Hz");
              blinkResponse(5, 500);
              waitingForServerResponse = true;
          }
          else if (inputFromSrv.equalsIgnoreCase("DENIED")) {
              Serial.println("--ACCESS DENIED--");
              //blink 0.5 Hz
              Serial.println("blink 0.5 Hz");
              blinkResponse(5, 50);
              waitingForServerResponse = true;
          }
          else {
            Serial.println("UNKOWN COMMAND from Server");
          }
          inputFromSrv = "";
          inputComplete = false;
      }
    }
    //Serial.flush();
}

void blinkResponse(int times, int freq) {
  for(int i = 0; i < times; i++) {
    digitalWrite(13, HIGH);   // turn the LED on (HIGH is the voltage level)
    delay(freq);              // wait for a second
    digitalWrite(13, LOW);    // turn the LED off by making the voltage LOW
    delay(freq);              // wait for a second  
  }
}

void serialEvent() {
    while (Serial.available()) {
        // get the new byte:
        char inChar = (char)Serial.read();

        // if the incoming character is a newline, set a flag
        // so the main loop can do something about it:
        if (inChar == '\n') {
            inputComplete = true;
            return;
        }
        // add it to the inputString:
        inputFromSrv += inChar;
    }
}

void read(int currentblock) {
    success = nfc.mifareclassic_AuthenticateBlock(uid, uidLength, currentblock, 1, keyuniversal);
    if (success) {
        authenticated = true;
    }
    else {
        Serial.println("Authentication error");
    }
    if (!authenticated) {
        Serial.print("Block "); Serial.print(currentblock, DEC); Serial.println(" unable to authenticate");
    }
    else {
        // Dump the data into the 'data' array
        success = nfc.mifareclassic_ReadDataBlock(currentblock, data);
        if (success) {
            // Read successful
            //Serial.print("Block "); Serial.print(currentblock, DEC);
            Serial.print("REQ ");
            Serial.println(currentblock, DEC);
            //if (currentblock < 10) {
            //    Serial.print("	");
            //}
            //else {
            //    Serial.print(" ");
            //}
            // Dump the raw data
            nfc.PrintHexChar(data, 16);
        }
        else {
            // Oops ... something happened
            Serial.print("Block "); Serial.print(currentblock, DEC);
            Serial.println(" unable to read this block");
        }
    }
}
