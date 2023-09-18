import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PoscoResearchCraneDataSocketClient 
{

    public static void main(String[] args) throws IOException
    {
    	Socket ournewsocket = null;
    	DataInputStream ournewDataInputstream = null;
    	DataOutputStream ournewDataOutputstream = null;
    	String newresuiltReceivedString = "";
    	
        try
        {
			// In the following loop, the client and client handle exchange data.
			while (true)
			{
				// establishing the connection 
				//ournewsocket = new Socket("192.168.1.13", 3307);
				ournewsocket = new Socket("10.30.92.143", 3307);

				ournewDataInputstream = new DataInputStream(ournewsocket.getInputStream());
				ournewDataOutputstream = new DataOutputStream(ournewsocket.getOutputStream());
				
			    // printing date or time as requested by client
			    newresuiltReceivedString = ournewDataInputstream.readUTF();

			    System.out.println(newresuiltReceivedString);

			    Thread.sleep(1000);

			    newresuiltReceivedString = "";
			    
			    if( ournewDataInputstream != null ) ournewDataInputstream.close();
	        	if( ournewDataOutputstream != null ) ournewDataOutputstream.close();
	        	if( ournewsocket != null ) ournewsocket.close();
			}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        	if( ournewDataInputstream != null ) ournewDataInputstream.close();
        	if( ournewDataOutputstream != null ) ournewDataOutputstream.close();
        	if( ournewsocket != null ) ournewsocket.close();
        }
    }

}
