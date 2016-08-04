/*
 * Based on a demo application for USBtinLib, the Java Library for USBtin
 * http://www.fischl.de/usbtin
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package USBtinController;

import de.fischl.usbtin.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.Thread;
import java.lang.InterruptedException;

/**
 * Demo application using USBtinLib, the Java Library for USBtin.
 * 
 * @author Thomas Fischl
 */
public class USBtinController implements CANMessageListener {

    /** CAN message identifier we look for */
    //SET ACCORDING TO ORONOS' MAPPING
    //static final int WATCHID = 0x002;
    
    /**
     * This method is called every time a CAN message is received.
     * 
     * @param canmsg Received CAN message
     */
    @Override
    public void receiveCANMessage(CANMessage canmsg) {

        // In this example we look for CAN messages with given ID
        //if (canmsg.getId() == WATCHID) {
            
            // juhuu.. match!
            
            // print out message infos
            //System.out.println("Watched message: " + canmsg);
            //System.out.println(
            //        "  id:" + canmsg.getId()
            //        + " dlc:" + canmsg.getData().length
            //        + " ext:" + canmsg.isExtended()
            //        + " rtr:" + canmsg.isRtr());
            
            // and now print payload
            //for (byte b : canmsg.getData()) {
            //    System.out.print(" " + b);
            //}
            //System.out.println();
            
        //} else {
            // no match, just print the message string
            //System.out.println(canmsg);
        //}
    }

    /**
     * Entry method for our demo programm
     * 
     * @param args Arguments
     */
    public static void main(String[] args) {
        
        try {

            // create the instances
            USBtin usbtin = new USBtin();
            USBtinController usbtincontroller = new USBtinController();

            // connect to USBtin and open CAN channel with 500kBaud in Active-Mode
            usbtin.connect("/dev/ttyACM0"); // For Raspberry Pi
            //usbtin.addMessageListener(usbtincontroller); 	
            usbtin.openCANChannel(500000, USBtin.OpenMode.ACTIVE); //BAUDRATE

            // send an example CAN message (standard)
            //usbtin.send(new CANMessage(0x100, new byte[]{0x11, 0x22, 0x33}));
            // send an example CAN message (extended)
            //usbtin.send(new CANMessage(0x101, new byte[]{0x44}, true, false));
            //hex id (11/29 bits: 0x000 to 0x7FF or 0x00000 to 0x3FFFF?), hex data bytes (1 to 8), bool Ext, bool RTR [jacopo]
            
            long start = System.currentTimeMillis();
	    long current = System.currentTimeMillis();
		
	    while ((current-start) < 43200000) {					 		//SET MAX EXECUTION TIME IN MILS HERE
			
		try {
		   Thread.sleep(1000); 								//SET "SEND MESSAGE" PERIOD IN MILS HERE
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		current = System.currentTimeMillis();
		
		//get last line in the log file
		File f = new File("/home/pi/Desktop/hat-log/hat.csv"); 					//CHANGE PATH AS NEEDED
		String s = tail(f);
		System.out.printf(s);
		System.out.printf("\n");
			
		//convert into byte array
		byte[] b = s.getBytes(Charset.forName("UTF-8"));
		
		//send 8 Bytes at a time
            	int id = Integer.parseInt("00000000000000000000000000001",2); // HARDCODE THIS WITH FREDERIC, 29 bits?
            	for(int i = 0; i < (b.length / 8); i++) {
            		System.out.printf("0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X \n", b[i*8+0], b[i*8+1], b[i*8+2], b[i*8+3], b[i*8+4], b[i*8+5], b[i*8+6], b[i*8+7]);
            		usbtin.send(new CANMessage(id, new byte[]{b[i*8+0], b[i*8+1], b[i*8+2], b[i*8+3], b[i*8+4], b[i*8+5], b[i*8+6], b[i*8+7]}, true, false));
      	    	}
      	    	
      	    	byte[] remainder = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
		if ((b.length % 8) != 0) {
			int first_index = (b.length / 8) * 8;
			int last_index = b.length - 1;
			int i = 0;
			for(int index = first_index; index <= last_index; index++) {
				remainder[i] = b[index];
				i++;
			}
            		System.out.printf("0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X \n", remainder[0], remainder[1], remainder[2], remainder[3], remainder[4], remainder[5], remainder[6], remainder[7]);
            		usbtin.send(new CANMessage(id, new byte[]{remainder[0], remainder[1], remainder[2], remainder[3], remainder[4], remainder[5], remainder[6], remainder[7]}, true, false));
		}
		
	    }
			


            // now wait for user input
            //System.out.println("Listen for CAN messages (watch id=" + WATCHID + ") ... press ENTER to exit!");
            //System.in.read();

            // close the CAN channel and close the connection
            usbtin.closeCANChannel();
            usbtin.disconnect();
            System.out.println("Closing");

        } catch (USBtinException ex) {
            
            // Ohh.. something goes wrong while accessing/talking to USBtin   
            System.out.println("USBtinException: " + ex.getMessage());         
            System.err.println(ex);           
            
		//} catch (java.io.IOException ex) {
            
            // this we need because of the System.in.read()
			//System.out.println("IOException (main): " + ex.getMessage()); 
            //System.err.println(ex);
            
        }
        catch (NumberFormatException nfe)
    	{
      	    System.out.println("NumberFormatException: " + nfe.getMessage());
        }
		

    }
	
	
	public static String tail( File file ) {
	    RandomAccessFile fileHandler = null;
	    try {
	        fileHandler = new RandomAccessFile( file, "r" );
	        long fileLength = fileHandler.length() - 1;
	        StringBuilder sb = new StringBuilder();
	        
	        //int penultimateLine = 0;
	        //int penultimateA = 0;
	        //int penultimateD = 0;

	        for(long filePointer = fileLength; filePointer != -1; filePointer--){
	            fileHandler.seek( filePointer );
	            int readByte = fileHandler.readByte();
	            
	            

	            if( readByte == 0xA ) {
	            
	            	//if( penultimateA == 1 ) {
	               	//    penultimateA = 0;
	               	//    penultimateLine = 1;
	                //    continue;
	                //}
	            	
	                if( filePointer == fileLength ) {
	                //    penultimateA = 1;
	                    continue;
	                }
	                
	                break;

	            } else if( readByte == 0xD ) {
	            
	            	//if( penultimateD == 1 ) {
	               	//    penultimateD = 0;
	                //    continue;
	                //}
	            
	                if( filePointer == fileLength - 1 ) {
	               	//    penultimateD = 1;
	                    continue;
	                }
	                
	                break;
	            }
	            
	            
		    //if (penultimateLine == 1) {
		    	sb.append( ( char ) readByte );
		    //}
		    
	        }

	        String lastLine = sb.reverse().toString();
	        return lastLine;
	    } catch( java.io.FileNotFoundException e ) {
	        e.printStackTrace();
			System.out.println("FileNotFoundException: " + e.getMessage());
	        return null;
	    } catch( java.io.IOException e ) {
	        e.printStackTrace();
			System.out.println("IOException (tail 1): " + e.getMessage());
	        return null;
	    } finally {
	        if (fileHandler != null )
	            try {
	                fileHandler.close();
	            } catch (IOException e) {
	                /* ignore */
					System.out.println("IOException (tail 2): " + e.getMessage());
	            }
	    }
	}
	
	
}
