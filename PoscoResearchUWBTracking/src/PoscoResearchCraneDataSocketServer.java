import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/*
 *   10.30.92.143:3307  설치

 *   설치 서버 정보 : 10.30.92.143
 *   설치 폴더 : /usr/local/posco/uCube/PoscoResearchCraneDataSocketServer
 *   사용하는곳 : CCTV, 2D HMI
 */
public class PoscoResearchCraneDataSocketServer 
{
	public static void main(String[] args) throws IOException
	{
		// TODO Auto-generated method stub
		

		String resource = "/usr/local/posco/uCube/PoscoResearchCraneDataSocketServer/set.poscoresearch.cranedata.properties";
		Properties properties = new Properties();
		
		FileInputStream fis = new FileInputStream(resource);
    	properties.load(fis);
		
		int iSocketPort = Integer.parseInt( properties.getProperty("datasource.crane.data.socket.port") );
		String db_driver = properties.getProperty("datasource.posco.research.crane.data.db.driver");
		String db_url    = properties.getProperty("datasource.posco.research.crane.data.db.url");
		String db_id     = properties.getProperty("datasource.posco.research.crane.data.db.id");
		String db_pw     = properties.getProperty("datasource.posco.research.crane.data.db.pw");

		ServerSocket myserverSocket = new ServerSocket( iSocketPort );
		Socket mynewSocket = null;
		
		// getting client request
		// running infinite loop
		
		int iLoop = 0;
		String sYmd = "";
		
		PrintWriter writer = null;
		DataInputStream ournewDataInputstream = null;
		DataOutputStream ournewDataOutputstream = null;
		
		String receivedString;
		StringBuffer stringToReturn = new StringBuffer(10);
		
		Connection con = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
		while (true)
		{
			try
			{
			
				Class.forName(db_driver);
	    		con = DriverManager.getConnection(db_url,db_id, db_pw);
	    		
	    		StringBuffer sbQuery = new StringBuffer(10);
	    		sbQuery.append(" SELECT    \n");
	    		sbQuery.append("     ymd \n");
	    		sbQuery.append("   , cranecode \n");
	    		sbQuery.append("   , MAX( x ) AS x \n");
	    		sbQuery.append("   , MAX( y ) AS y \n");
	    		sbQuery.append("   , MAX( z1 ) AS z1 \n");
	    		sbQuery.append("   , MAX( z2 ) AS z2 \n");
	    		sbQuery.append("   , MAX( ldstatus ) AS ldstatus \n");
	    		sbQuery.append("   , MAX( ldno ) AS ldno \n");
	    		sbQuery.append("   , MAX( ldweight ) AS ldweight \n");
	    		sbQuery.append("   , MAX( mtlno ) AS mtlno \n");
	    		sbQuery.append("   , MAX( poscode ) AS poscode \n");
	    		sbQuery.append(" FROM     ucubedb.craneinfo \n");
	    		sbQuery.append(" WHERE    ymd = ( SELECT MAX(ymd) FROM ucubedb.craneinfo WHERE length(ymd) = 14 ) \n");
	    		sbQuery.append(" GROUP BY ymd, cranecode \n");
	    		
	    		while (true)
				{
					// mynewSocket object to receive incoming client requests
					mynewSocket = myserverSocket.accept();
					
					System.out.println("A new Posco Research Crane Data connection identified : " + mynewSocket);

					// starting
					System.out.println("Socket Check : " + mynewSocket);
					
					//writer = new PrintWriter(this.mynewSocket.getOutputStream());
					
					ournewDataOutputstream = new DataOutputStream( mynewSocket.getOutputStream() );

	    			
					ps = con.prepareStatement( sbQuery.toString() );
					rs = ps.executeQuery();

					iLoop = 0;
					sYmd = "";
					stringToReturn = new StringBuffer(10);
					while( rs.next() )
					{
						
						if( iLoop > 0 )
						{
							sYmd = rs.getString("ymd");
						}

						stringToReturn.append(";"); stringToReturn.append( rs.getString("cranecode") );
						stringToReturn.append(","); stringToReturn.append( rs.getString("x") );
						stringToReturn.append(","); stringToReturn.append( rs.getString("y") );
						stringToReturn.append(","); stringToReturn.append( rs.getString("z1") );
						stringToReturn.append(","); stringToReturn.append( rs.getString("z2") );
						stringToReturn.append(","); stringToReturn.append( rs.getString("ldstatus") );
						stringToReturn.append(","); stringToReturn.append( rs.getString("ldno") );
						stringToReturn.append(","); stringToReturn.append( rs.getString("ldweight") );
						stringToReturn.append(","); stringToReturn.append( rs.getString("mtlno") );
						stringToReturn.append(","); stringToReturn.append( rs.getString("poscode") );

						iLoop++;
					}

					System.out.println("[["+ sYmd +"]] >> " + iLoop + stringToReturn.toString() );
					
					//writer.write( stringToReturn.toString() );
					//writer.flush();
					
					if( rs != null )	rs.close();
					
					if( ps != null )
					{
						ps.clearParameters();
						ps.close();
					}

					ournewDataOutputstream.writeUTF( iLoop + stringToReturn.toString() );
					ournewDataOutputstream.flush();
					if( ournewDataOutputstream != null )	ournewDataOutputstream.close();
					if( mynewSocket != null )	mynewSocket.close();

				}	
				
			}
			catch (Exception e)
			{
				System.out.println("Main 1 Socket Closed!!!!!!");
				e.printStackTrace();
			}
			finally
			{
				try
				{
					// closing resources
					if( rs != null ) rs.close();
					if( ps != null ) ps.close();
					if( con != null ) con.close();
					
					if( writer != null )	writer.close();
					if( ournewDataOutputstream != null )	ournewDataOutputstream.close();
					if( mynewSocket != null )	mynewSocket.close();
					if( myserverSocket != null )	myserverSocket.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			try
			{
				// retry socket reconnection
				myserverSocket = new ServerSocket( iSocketPort );
			}
			catch (Exception e)
			{
				System.out.println("Main 2 Socket Closed!!!!!!");
				e.printStackTrace();
			}

		}
	}

}
