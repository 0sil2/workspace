import java.util.ArrayList;
import java.util.Arrays;
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
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        System.out.println("["+dsDbUrl+"]["+dsDbDriver+"]["+dsDbUsername+"]["+dsDbPassword+"]");
        System.out.println("["+dsRtlsIp+"]["+dsRtlsPort+"]["+dsRtlsId+"]["+dsRtlsPw+"]["+dsRtlsSalt+"]");
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
	}
}
