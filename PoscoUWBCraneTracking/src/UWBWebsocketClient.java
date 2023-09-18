import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.io.FileInputStream;
import java.util.Properties;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.tsingoal.com.FrameParse;
import com.tsingoal.com.TExtendedInfo;
import com.tsingoal.com.TPosInfo;
import com.tsingoal.com.RtlsWsManager.PosOutMode;

public class UWBWebsocketClient {

	public static void main(String[] args) {
		createLocalsensePushWs();
	}
	
	/**
	 * /usr/local/java/jdk1.8.0_321/jre/bin/java -cp "/usr/local/posco/uwbpos/uwbpos.jar:/usr/local/posco/uwbpos/json.jar:/usr/local/posco/uwbpos/json-simple-1.1.1.jar:/usr/local/posco/uwbpos/WebsocketClient-v2.2.1.a.jar" UWBWebsocketClient
	 * 
	 * ws sub pro: localSensePush-protocol example
	 * Used for receiving commonly used real-time data, such as location data, base station status,
	 * label power, alarm data, area in and out events, heart rate, label movement rate, mileage and
	 * other information
	 */
	public static void createLocalsensePushWs() 
	{

		////////////////////////////////////////////////////////////////////////////////////////////////
		//String resource = "C:\\UWB\\eclipse\\workspace\\PoscoUWBCraneTracking\\set.properties";
		//String resource = "C:\\eclipse\\workspace\\PoscoUWBCraneTracking\\set.properties";
		String resource = "/usr/local/posco/uwbpos/set.properties";
		
        Properties properties = new Properties();

        String dsDbUrl = "";
        String dsDbDriver = "";
        String dsDbUsername = "";
        String dsDbPassword = "";
        
        String dsRtlsIp = "";
        String dsRtlsPort = "";
        int iDsRtlsPort = 0;
        String dsRtlsId = "";
        String dsRtlsPw = "";
        String dsRtlsSalt = "";
        String dsSocketSvrIp = "";
        
        try
        {
        	FileInputStream fis = new FileInputStream(resource);
            properties.load(fis);

            dsDbUrl = properties.getProperty("datasource.connection.url");
            dsDbDriver = properties.getProperty("datasource.connection.driverclass");
            dsDbUsername = properties.getProperty("datasource.connection.username");
            dsDbPassword = properties.getProperty("datasource.connection.password");
            
            dsRtlsIp = properties.getProperty("datasource.rtls.socket.ip");
            dsRtlsPort = properties.getProperty("datasource.rtls.socket.port");
            iDsRtlsPort = (dsRtlsPort != null && !"".equals(dsRtlsPort)) ? Integer.parseInt(dsRtlsPort) : 0;
            dsRtlsId = properties.getProperty("datasource.rtls.socket.id");
            dsRtlsPw = properties.getProperty("datasource.rtls.socket.pw");
            dsRtlsSalt = properties.getProperty("datasource.rtls.socket.salt");
            
            dsSocketSvrIp = properties.getProperty("datasource.rtls.socket.server.json.port");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        System.out.println("["+dsDbUrl+"]["+dsDbDriver+"]["+dsDbUsername+"]["+dsDbPassword+"]");
        System.out.println("["+dsRtlsIp+"]["+dsRtlsPort+"]["+dsRtlsId+"]["+dsRtlsPw+"]["+dsRtlsSalt+"]["+dsSocketSvrIp+"]");
        ///////////////////////////////////////////////////////////////////////////////////////////////

		/**
		 * Construction Mode I (recommended)
		 * The constructor takes no arguments, and the username and password arguments
		 * are set by the function
		 */
		//Create ws and set the username and password part start
		UWBLsWsPushPro rtls_pos = new UWBLsWsPushPro();
		//Setting a User Name
		rtls_pos.setWsUserName(dsRtlsId);
		//Set the password text (the first parameter), the salt value (the second parameter). The salt value is usually unchanged, is a fixed value
		rtls_pos.setWsUserPasswd(dsRtlsPw, dsRtlsSalt);
		//Create ws and set the username and password part end
		
		/**
		 * Structural mode 2
		 * The first parameter: username
		 * Second argument: use the salt-encrypted result password obtained by md5(MD5 (password) + salt)
		 * Third parameter: the password marked as final salted encryption (third parameter)
		 */
		//Set the WebSocket service address
		//rtls_pos.setHost("192.168.0.112");
		rtls_pos.setHost(dsRtlsIp);
		//Set the WebSocket service port
		rtls_pos.setServerPort(iDsRtlsPort);
		//Set the label number, AOA to 64 bits
		rtls_pos.setTagidBit(FrameParse.TAGID_32BIT);

		rtls_pos.setPos_mode(PosOutMode.XY);
		//Set the WebSocket sub-protocol
		rtls_pos.setProtocal("localSensePush-protocol");
		//Connect the websocket
		rtls_pos.connectToServer();

		///////////////////////////////////////////////////////////////////////////////////////////////
		/*
		try
		{
			long printTagid = -1L;
			while(true)
			{
				System.out.println(rtls_pos.getUWBTagInfo());
				Thread.sleep(1000);
			}
        }
		catch (InterruptedException e)
		{
			System.err.format("IOException: %s%n", e);
	    }
	    */
		
		if( dsSocketSvrIp != null && !"".equals(dsSocketSvrIp) )
		{

			int port = Integer.parseInt(dsSocketSvrIp);

			Socket socket = null;
			OutputStream output = null;
			PrintWriter writer = null;

			SimpleDateFormat sdf1 = new SimpleDateFormat ("yyyyMMdd");
			SimpleDateFormat sdf2 = new SimpleDateFormat ("HHmmss.SSS");
			
			try( ServerSocket serverSocket = new ServerSocket(port) )
			{
				System.out.println("RTLS UWB Tag Info Socket Server is listening on Port " + port);
				
				while(true)
				{
					socket = serverSocket.accept();
					System.out.print("RTLS UWB Tag Info Socket Svr : ["+ sdf1.format(new Timestamp(System.currentTimeMillis())) +"]");
					System.out.println("["+ sdf2.format(new Timestamp(System.currentTimeMillis())) +"]["+ socket.getInetAddress() +"] clinent connected.");

					//Server -> Client Data Send
					output = socket.getOutputStream();
					writer = new PrintWriter(output, true);
					writer.println( rtls_pos.getUWBTagInfo() );
					
					if(writer!=null) writer.close();
					if(output!=null) output.close();
					if(socket!=null) socket.close();
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(writer!=null) writer.close();
					if(output!=null) output.close();
					if(socket!=null) socket.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}

		}


		///////////////////////////////////////////////////////////////////////////////////////////////
	}
}
