import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

import com.tsingoal.com.RtlsWsManager;
import com.tsingoal.com.TAreaStatics;
import com.tsingoal.com.TAttendanceStatics;
import com.tsingoal.com.TBaseState;
import com.tsingoal.com.TBaseTagRssiInfo;
import com.tsingoal.com.TCapacityInfo;
import com.tsingoal.com.TExtendedInfo;
import com.tsingoal.com.TPersonStatistics;
import com.tsingoal.com.TPosInfo;
import com.tsingoal.com.TPosInfoBeacon;
import com.tsingoal.com.TRichAlarmInfo;
import com.tsingoal.com.TSimpleAlarmInfo;
import com.tsingoal.com.TUpdateInfo;
import com.tsingoal.com.TVideoInfo;

public class UWBLsWsPushPro extends RtlsWsManager {

	/////////////////////////////////////////////////////////////////////
	private String subTagDataDirPath = "";
	private int isSleep = 0;
	private long printTagid = -1L;
	
	private SimpleDateFormat sdf1 = null;
	private SimpleDateFormat sdf2 = null;
	private SimpleDateFormat sdf3 = null;
	private SimpleDateFormat sdf4 = null;
	private SimpleDateFormat sdf5 = null;
	private Timestamp timestamp = null;
	
	private String sYear = "";
	private String sMonth = "";
	
	private StringBuffer sbTagInfo = new StringBuffer();
	
	private String parmUWBLogData = "";
	private List<String> parmUWBLogDataList = new ArrayList<String>();
	
	private File f = null;
	FileWriter fw = null;
	BufferedWriter bfw = null;
	
	private List<TPosInfo> rPosList = new ArrayList<TPosInfo>();
	private TExtendedInfo rExtendedInfo = new TExtendedInfo();
	private TExtendedInfo parmExtendedInfo = new TExtendedInfo();
	private List<TExtendedInfo> arExtendedAdd = new ArrayList<TExtendedInfo>();
	private String strUWBTagInfo = "";
	
	//String resource = "C:\\UWB\\eclipse\\workspace\\PoscoUWBCraneTracking\\set.properties";
	//private String resource = "C:\\eclipse\\workspace\\PoscoUWBCraneTracking\\set.properties";
	private String resource = "/usr/local/posco/uwbpos/set.properties";
	private Properties properties = new Properties();

    private String dsLogBackupPath = "";
    
    private String jdbc_driver = "";
	private String jdbc_url = "";
	private String db_id = "";
	private String db_pw = "";
	
	private String sCraneCode = "";
	private String sUwbTagID = "";
	private String sDistance = "";
	private String sHoistHeight = "";
	private String sSubHoistHeight = "";
	private String sLadleStatus = "";
	
	private String sTmpWeight = "";
	private String s1HWeight = "";
	private String s2HWeight = "";
	private String s1SWeight = "";
	private String s1LWeight = "";
	private String s2LWeight = "";
	private String s1TWeight = "";
	private String s2TWeight = "";
	private String s3TWeight = "";
	private String s4TWeight = "";
	
	private String extParsingRet = "";
	private String [] aryExt = null;
	private int iCheck = -1;
	////////////////////////////////////////////////////////////////////
	
	public UWBLsWsPushPro() 
	{
		super();
		// TODO Auto-generated constructor stub
		this.sdf1 = new SimpleDateFormat ("yyyyMMdd");
		this.sdf2 = new SimpleDateFormat ("HHmmss.SSS");
		this.sdf3 = new SimpleDateFormat ("yyyy");
		this.sdf4 = new SimpleDateFormat ("MM");
		this.sdf5 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS");
		this.timestamp = new Timestamp(System.currentTimeMillis());
		
		try
        {
			FileInputStream fis = new FileInputStream(resource);
        	this.properties.load(fis);

        	this.dsLogBackupPath = this.properties.getProperty("datasource.log.backup.path");
        	
        	//Mysql DB Server
			this.jdbc_driver 	= properties.getProperty("datasource.poscoict.uwbextdatadb.db.driver");
			this.jdbc_url 		= properties.getProperty("datasource.poscoict.uwbextdatadb.db.url");
			this.db_id 			= properties.getProperty("datasource.poscoict.uwbextdatadb.db.id");
			this.db_pw 			= properties.getProperty("datasource.poscoict.uwbextdatadb.db.pw");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}

	public UWBLsWsPushPro(String userName, String password) {
		super(userName, password);
		// TODO Auto-generated constructor stub
	}

	public UWBLsWsPushPro(String userName, String password, Boolean isMD5) {
		super(userName, password, isMD5);
		// TODO Auto-generated constructor stub
	}

	public String getUWBLogData()
	{
		return this.parmUWBLogData;
	}

	private void setUWBLogData(String pUWBLogData)
	{
		this.parmUWBLogData = pUWBLogData;
	}

	public List<TPosInfo> getTPosInfo()
	{
		return this.rPosList;
	}

	private void setTPosInfo(List<TPosInfo> parmPosInfo)
	{
		this.rPosList = parmPosInfo;
	}

	public TExtendedInfo getTExtendedInfo()
	{
		return this.rExtendedInfo;
	}

	private void setTExtendedInfo(TExtendedInfo parmExtendedInfo)
	{
		this.rExtendedInfo = parmExtendedInfo;
		//System.out.println("setTExtendedInfo=========>" + parmExtendedInfo);
	}
	
	private void initExtendedInfo()
	{
		this.arExtendedAdd = new ArrayList<TExtendedInfo>();
		//System.out.println("Init TExtendedInfo.");
	}
	
	private void addExtededInfo(TExtendedInfo parmExtendedInfo)
	{
		arExtendedAdd.add(parmExtendedInfo);
	}
	
	public String getUWBTagInfo()
	{
		return this.strUWBTagInfo;
	}

	private void setUWBTagInfo(String parmTagInfo)
	{
		this.strUWBTagInfo = parmTagInfo;
	}

	private void setFileWirte(String tagDataDir, Timestamp pTimestamp, String fileName, String sTagInfoParam)
	{
		this.sYear = this.sdf3.format(pTimestamp);
		this.sMonth = this.sdf4.format(pTimestamp);
	
		////////////////////////////////////////////////////////////////////////////
		//String yearDir = this.tagDataDir + "\\" + this.sYear;
		//String monthDir = this.tagDataDir + "\\" + this.sYear + "\\" + this.sMonth;
		//String sFileName = monthDir + "\\" + fileName;
		////////////////////////////////////////////////////////////////////////////
		String yearDir = tagDataDir + "/" + this.sYear;
		String monthDir = tagDataDir + "/" + this.sYear + "/" + this.sMonth;
		String sFileName = monthDir + "/" + fileName;
		////////////////////////////////////////////////////////////////////////////

		this.f = new File(yearDir);
		if( !f.exists() )	f.mkdir();
		
		this.f = new File(monthDir);
		if( !f.exists() )	f.mkdir();

		try
		{
			this.fw = new FileWriter(sFileName, true);
			this.bfw = new BufferedWriter(this.fw);
			
			bfw.write(sTagInfoParam);
			bfw.newLine();
			bfw.flush();
			
			//System.out.println(sTagInfoParam);
		}
		catch( IOException e )
		{
			System.out.println(e);
		}
		finally
		{
			try
			{
				bfw.close();
				fw.close();
			}
			catch( IOException e )
			{
				System.out.println(e);
			}
		}
	}
	
	private String calTExtendedDataParsing1(String extendData)
	{
		String ret = "";
		
		StringBuffer sbExDt = new StringBuffer(10);
		
		int il = 0;
		int iSAr = 0;
		int exLen = 0;
		String sHex [] = new String[8];
		int iDec[] = new int[8];
		
		String sWeight = "";
		String sDistance = "";
		String sHoistHeight = "";
		String sSpare1 = "";
		String sSpare2 = "";
		String sSpare3 = "";

		if( extendData == null || "".equals(extendData) )
		{
			ret = "";
		}
		else
		{
			if( extendData.length() == 22 )
			{
				extendData = extendData.substring(4, 22);
				extendData = extendData.substring(0, 16);
				exLen = extendData.length();

				for( il = 0 ; il < exLen ; il++)
				{
					if( (il%2) == 1 )
					{
						sHex[iSAr] = sHex[iSAr] + extendData.charAt(il);
						iSAr++;
					}
					else
					{
						sHex[iSAr] = "0x" + extendData.charAt(il);
					}
				}
				
				for( il = 0 ; il < iDec.length ; il++ )
				{
					iDec[il] =  Integer.decode(sHex[il]);
				}
				
				/*
				for( int i=0 ; i<iDec.length ; i++ )
				{
					System.out.println("["+ sHex[i] +"]["+ iDec[i] +"]");
				}
				*/
				
				sWeight = Integer.toString((iDec[1] << 8) + iDec[0]) + "." + Integer.toString(iDec[2]);
				sDistance = Integer.toString(iDec[3]) + "." + Integer.toString(iDec[4]);
				sHoistHeight = Integer.toString(iDec[5]);
				sSpare1 = Integer.toString(iDec[6]);
				sSpare2 = Integer.toString(iDec[7]);
				
				sbExDt.append(sWeight);
				sbExDt.append(",");
				sbExDt.append(sDistance);
				sbExDt.append(",");
				sbExDt.append(sHoistHeight);
				sbExDt.append(",");
				sbExDt.append(sSpare1);
				sbExDt.append(",");
				sbExDt.append(sSpare2);
				
				ret = sbExDt.toString();
			}
			else
			{
				ret = "";
			}			
		}
		
		/*
		System.out.println("======>"+ ret);
		*/		
		return ret;
	}
	
	private String calTExtendedDataParsing2(String extendData)
	{
		String ret = "";
		
		StringBuffer sbExDt = new StringBuffer(10);
		
		int il = 0;
		int iSAr = 0;
		int exLen = 0;
		String sHex [] = new String[8];
		int iDec[] = new int[8];
		
		String sWeight = "";
		String sDistance = "";
		String sHoistHeight = "";
		String sSpare1 = "";
		String sSpare2 = "";
		String sSpare3 = "";

		if( extendData == null || "".equals(extendData) )
		{
			ret = "";
		}
		else
		{
			if( extendData.length() == 22 )
			{
				extendData = extendData.substring(4, 22);
				extendData = extendData.substring(0, 16);
				exLen = extendData.length();

				for( il = 0 ; il < exLen ; il++)
				{
					if( (il%2) == 1 )
					{
						sHex[iSAr] = sHex[iSAr] + extendData.charAt(il);
						iSAr++;
					}
					else
					{
						sHex[iSAr] = "0x" + extendData.charAt(il);
					}
				}
				
				for( il = 0 ; il < iDec.length ; il++ )
				{
					iDec[il] =  Integer.decode(sHex[il]);
				}
				
				/*
				for( int i=0 ; i<iDec.length ; i++ )
				{
					System.out.println("["+ sHex[i] +"]["+ iDec[i] +"]");
				}
				*/
				
				sWeight = Integer.toString((iDec[1] << 8) + iDec[0]) + "." + Integer.toString(iDec[2]);
				sDistance = Integer.toString(iDec[3]) + "." + Integer.toString(iDec[4]);
				sHoistHeight = Integer.toString(iDec[5]);
				sSpare1 = Integer.toString(iDec[6]);
				sSpare2 = Integer.toString(iDec[7]);
				
				sbExDt.append(", Weight : ");
				sbExDt.append(sWeight);
				sbExDt.append(", Distance : ");
				sbExDt.append(sDistance);
				sbExDt.append(", HoistHeight : ");
				sbExDt.append(sHoistHeight);
				sbExDt.append(", Spare1 : ");
				sbExDt.append(sSpare1);
				sbExDt.append(", Spare2 : ");
				sbExDt.append(sSpare2);
				
				ret = sbExDt.toString();
			}
			else
			{
				ret = "";
			}			
		}
		
		/*
		System.out.println("======>"+ ret);
		*/		
		return ret;
	}
	
	private void extendDataDBInsert(String pCraneCode, String pUwbTagID, String pWeight, String pDistance, String pHoistHeight, String pSubHoistHeight, String pLadleStatus)
	{
		Connection con = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;

    	try
    	{
    		Class.forName(this.jdbc_driver);
    		con = DriverManager.getConnection(this.jdbc_url, this.db_id, this.db_pw);
    		
    		ps = con.prepareStatement( " INSERT INTO uwb_extdata VALUES ( null, ?, ?, ?, ?, ?, ?, ?, now() ) " );
    		ps.setString( 1 , pCraneCode );
    		ps.setString( 2 , pUwbTagID );
    		ps.setString( 3 , pWeight );
    		ps.setString( 4 , pDistance );
    		ps.setString( 5 , pHoistHeight );
    		ps.setString( 6 , pSubHoistHeight );
    		ps.setString( 7 , pLadleStatus );
    		ps.executeUpdate();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		finally
		{
			try
			{
				if( rs != null ) rs.close();
        		if( ps != null ) ps.close();
        		if( con != null ) con.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	
	@Override
	public void OnAreaStatistics(List<TAreaStatics> statisticsInfo) {
		// System.out.println("OnAreaStatistics=========>" + statisticsInfo);
	}

	@Override
	public void OnCapacityInfo(List<TCapacityInfo> tagCapcityList) {
		// System.out.println("OnCapacityInfo=========>" + tagCapcityList);
	}

	@Override
	public void OnPosInfo(List<TPosInfo> posList) {
		//System.out.println("OnPosInfo=========>" + posList);
		/*
		 * for (TPosInfo tPosInfo : posList) { System.out.println("OnPosInfo=========>"
		 * + tPosInfo); }
		 */
	}
	
	@Override
	public void OnExtendedInfo(TExtendedInfo extendedInfo) 
	{
		if (extendedInfo.getDataType() == 189)
		{
			this.timestamp = new Timestamp(System.currentTimeMillis());
			
			StringBuffer strExtData = new StringBuffer(10);
			
			System.out.print("["+this.sdf1.format(timestamp)+"]["+this.sdf2.format(timestamp)+"]");
			strExtData.append( this.sdf5.format(timestamp) ); 
			strExtData.append( ", " );
			strExtData.append( extendedInfo.getTagId() );
			
			this.sUwbTagID = extendedInfo.getTagId().toString();
			
			if( extendedInfo.getTagId() != null && "565084".equals( extendedInfo.getTagId().toString() )  )
			{
				this.sCraneCode = "1T";
				strExtData.append( ", 1T" );
				System.out.print("[ CraneCode : 1T ]");
			}
			else if( extendedInfo.getTagId() != null && "565085".equals(extendedInfo.getTagId().toString())  )
			{
				this.sCraneCode = "2T";
				strExtData.append( ", 2T" );
				System.out.print("[ CraneCode : 2T ]");
			}
			else if( extendedInfo.getTagId() != null && "565091".equals(extendedInfo.getTagId().toString())  )
			{
				this.sCraneCode = "3T";
				strExtData.append( ", 3T" );
				System.out.print("[ CraneCode : 3T ]");
			}
			else if( extendedInfo.getTagId() != null && "565082".equals(extendedInfo.getTagId().toString())  )
			{
				this.sCraneCode = "4T";
				strExtData.append( ", 4T" );
				System.out.print("[ CraneCode : 4T ]");
			}
			else if( extendedInfo.getTagId() != null && "565080".equals(extendedInfo.getTagId().toString())  )
			{
				this.sCraneCode = "1L";
				strExtData.append( ", 1L" );
				System.out.print("[ CraneCode : 1L ]");
			}
			else if( extendedInfo.getTagId() != null && "565093".equals(extendedInfo.getTagId().toString())  )
			{
				this.sCraneCode = "2L";
				strExtData.append( ", 2L" );
				System.out.print("[ CraneCode : 2L ]");
			}
			else if( extendedInfo.getTagId() != null && "565088".equals(extendedInfo.getTagId().toString())  )
			{
				this.sCraneCode = "1H";
				strExtData.append( ", 1H" );
				System.out.print("[ CraneCode : 1H ]");
			}
			else if( extendedInfo.getTagId() != null && "565092".equals(extendedInfo.getTagId().toString())  )
			{
				this.sCraneCode = "2H";
				strExtData.append( ", 2H" );				
				System.out.print("[ CraneCode : 2H ]");
			}
			else if( extendedInfo.getTagId() != null && "565089".equals(extendedInfo.getTagId().toString())  )
			{	
				this.sCraneCode = "1S";
				strExtData.append( ", 1S" );
				System.out.print("[ CraneCode : 1S ]");
			}

			System.out.print("[tagid:" + extendedInfo.getTagId() + "]");
			System.out.println("[parsing:" + this.calTExtendedDataParsing2( extendedInfo.getData() ) + "]");
			
			strExtData.append( this.calTExtendedDataParsing2( extendedInfo.getData() ) );
			
			//[20230217][093238.599][ CraneCode : 3T ][tagid:565091][parsing:, Weight : 390.33, Distance : 13.97, HoistHeight : 21, Spare1 : 26, Spare2 : 1]
			
			/*
			System.out.print("["+this.sdf1.format(timestamp)+"]["+this.sdf2.format(timestamp)+"][tagid:" + extendedInfo.getTagId() + "][type:" + extendedInfo.getDataType() + "][length:" + extendedInfo.getDataLength() + "]");
			System.out.println("[val:" + extendedInfo.getData() + "][parsing:" + this.calTExtendedData( extendedInfo.getData() ) + "]");
			*/
			
			//String strTmp = "["+this.sdf1.format(timestamp)+"]["+this.sdf2.format(timestamp)+"][tagid:" + extendedInfo.getTagId() + "][type:" + extendedInfo.getDataType() + "][length:" + extendedInfo.getDataLength() + "]";
			//strTmp = strTmp + "[val:" + extendedInfo.getData() + "][parsing:" + this.calTExtendedData( extendedInfo.getData() ) + "]";
			
			this.setFileWirte(this.dsLogBackupPath, this.timestamp, this.sdf1.format(timestamp)+"_ExtParsingTest.txt", strExtData.toString() );
			
			//////////////////////////////////////////
			//////////////////////////////////////////
			//////////////////////////////////////////
			
			this.extParsingRet = this.calTExtendedDataParsing1( extendedInfo.getData() );
			if( this.extParsingRet != null && !"".equals(this.extParsingRet)  )
			{
				this.aryExt = this.extParsingRet.split(",");

				this.sTmpWeight = this.aryExt[0];
				this.sDistance = this.aryExt[1];
				this.sHoistHeight = this.aryExt[2];
				this.sSubHoistHeight = this.aryExt[3];
				if( this.aryExt.length == 4 )
				{
					this.sLadleStatus = "0";
				}
				else
				{
					this.sLadleStatus = aryExt[4];
				}
				
				/////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////
				
				this.iCheck = -1;
				
				if( "1H".equals(this.sCraneCode) )
				{
					if( this.s1HWeight != null && this.sTmpWeight != null && (this.sTmpWeight).equals(this.s1HWeight) )
					{
						this.iCheck = -1;
					}
					if( this.s1HWeight != null && this.sTmpWeight != null && !(this.sTmpWeight).equals(this.s1HWeight) )
					{
						this.s1HWeight = this.sTmpWeight;
						this.iCheck = 1;
					}					
					else
					{
						this.iCheck = -1;						
					}
				}
				else if( "2H".equals(this.sCraneCode) )
				{
					if( this.s2HWeight != null && this.sTmpWeight != null && (this.sTmpWeight).equals(this.s2HWeight) )
					{
						this.iCheck = -1;
					}
					if( this.s2HWeight != null && this.sTmpWeight != null && !(this.sTmpWeight).equals(this.s2HWeight) )
					{
						this.s2HWeight = this.sTmpWeight;
						this.iCheck = 1;
					}					
					else
					{
						this.iCheck = -1;						
					}					
				}
				else if( "1S".equals(this.sCraneCode) )
				{
					if( this.s1SWeight != null && this.sTmpWeight != null && (this.sTmpWeight).equals(this.s1SWeight) )
					{
						this.iCheck = -1;
					}
					if( this.s1SWeight != null && this.sTmpWeight != null && !(this.sTmpWeight).equals(this.s1SWeight) )
					{
						this.s1SWeight = this.sTmpWeight;
						this.iCheck = 1;
					}					
					else
					{
						this.iCheck = -1;						
					}
				}
				else if( "1L".equals(this.sCraneCode) )
				{
					if( this.s1LWeight != null && this.sTmpWeight != null && (this.sTmpWeight).equals(this.s1LWeight) )
					{
						this.iCheck = -1;
					}
					if( this.s1LWeight != null && this.sTmpWeight != null && !(this.sTmpWeight).equals(this.s1LWeight) )
					{
						this.s1LWeight = this.sTmpWeight;
						this.iCheck = 1;
					}					
					else
					{
						this.iCheck = -1;						
					}
				}
				else if( "2L".equals(this.sCraneCode) )
				{
					if( this.s2LWeight != null && this.sTmpWeight != null && (this.sTmpWeight).equals(this.s2LWeight) )
					{
						this.iCheck = -1;
					}
					if( this.s2LWeight != null && this.sTmpWeight != null && !(this.sTmpWeight).equals(this.s2LWeight) )
					{
						this.s2LWeight = this.sTmpWeight;
						this.iCheck = 1;
					}					
					else
					{
						this.iCheck = -1;						
					}
				}
				else if( "1T".equals(this.sCraneCode) )
				{
					if( this.s1TWeight != null && this.sTmpWeight != null && (this.sTmpWeight).equals(this.s1TWeight) )
					{
						this.iCheck = -1;
					}
					if( this.s1TWeight != null && this.sTmpWeight != null && !(this.sTmpWeight).equals(this.s1TWeight) )
					{
						this.s1TWeight = this.sTmpWeight;
						this.iCheck = 1;
					}					
					else
					{
						this.iCheck = -1;						
					}
				}
				else if( "2T".equals(this.sCraneCode) )
				{
					if( this.s2TWeight != null && this.sTmpWeight != null && (this.sTmpWeight).equals(this.s2TWeight) )
					{
						this.iCheck = -1;
					}
					if( this.s2TWeight != null && this.sTmpWeight != null && !(this.sTmpWeight).equals(this.s2TWeight) )
					{
						this.s2TWeight = this.sTmpWeight;
						this.iCheck = 1;
					}					
					else
					{
						this.iCheck = -1;						
					}
				}
				else if( "3T".equals(this.sCraneCode) )
				{
					if( this.s3TWeight != null && this.sTmpWeight != null && (this.sTmpWeight).equals(this.s3TWeight) )
					{
						this.iCheck = -1;
					}
					if( this.s3TWeight != null && this.sTmpWeight != null && !(this.sTmpWeight).equals(this.s3TWeight) )
					{
						this.s3TWeight = this.sTmpWeight;
						this.iCheck = 1;
					}					
					else
					{
						this.iCheck = -1;						
					}
				}
				else if( "4T".equals(this.sCraneCode) )
				{
					if( this.s4TWeight != null && this.sTmpWeight != null && (this.sTmpWeight).equals(this.s4TWeight) )
					{
						this.iCheck = -1;
					}
					if( this.s4TWeight != null && this.sTmpWeight != null && !(this.sTmpWeight).equals(this.s4TWeight) )
					{
						this.s4TWeight = this.sTmpWeight;
						this.iCheck = 1;
					}					
					else
					{
						this.iCheck = -1;						
					}
				}
				
				if( this.iCheck == 1 )
				{
					this.extendDataDBInsert(this.sCraneCode, this.sUwbTagID, this.sTmpWeight, this.sDistance, this.sHoistHeight, this.sSubHoistHeight, this.sLadleStatus);
				}
			}
		}
		
		
	}

	@Override
	public void OnMessage(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnOpen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnPersonStatistics(TPersonStatistics statisticsInfo) {
//		 System.out.println("OnPersonStatistics=========>" + statisticsInfo);
	}
	
	@Override
	public void OnRichAlarm(TRichAlarmInfo alarm) {
//		System.out.println("OnRichAlarm=========>" + alarm);
	}

	@Override
	public void OnSimpleAlarm(TSimpleAlarmInfo alarm) {
//		 System.out.println("===simple alarm===: " + alarm);		
	}

	@Override
	public void OnUnknownMessage(ByteBuffer blob) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnUpdate(TUpdateInfo update) {

	}

	@Override
	public void OnAttendanceStatics(TAttendanceStatics attendanceStatics) {
//		System.out.println("OnAttendanceStatics=========>" + attendanceStatics);
	}

	@Override
	public void OnBaseState(TBaseState baseState) {
//		System.out.println(baseState);
	}

//////////////////only available json pro////////////////////////////////////
	@Override
	public void OnSwitchChanged(int ntype, boolean opened) {
//		System.out.println(ntype + "---" + opened);
	}

	@Override
	public void OnTrackTagVideoChanged(TVideoInfo video) {
		// json sub pro only

	}

	@Override
	public void OnBaseTagRssi(TBaseTagRssiInfo baseTagRssi) {

	}

	@Override
	public void OnPosBeaconInfo(List<TPosInfoBeacon> posList) {
		// TODO Auto-generated method stub
//		for (TPosInfoBeacon tPosInfoBeacon : posList) {
//			System.out.println("=============>>>>>>>>>>>>>>>>>>>>>>>" + tPosInfoBeacon);
//		}
	}

}
