package git.test.com;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

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
import com.tsingoal.com.RtlsWsManager;

public class UWBLsWsPushProModifying extends RtlsWsManager {

	/////////////////////////////////////////////////////////////////////
	private String subTagDataDirPath = "";
	private int isSleep = 0;
	private long printTagid = -1L;
	
	private SimpleDateFormat sdf1 = null;
	private SimpleDateFormat sdf2 = null;
	private SimpleDateFormat sdf3 = null;
	private SimpleDateFormat sdf4 = null;
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

    private  String dsLogBackupPath = "";
    
    private String ds1TmTagid = ""; 
    private String ds1TmXTuning = ""; 
    private float f1TmXTuning = 0;
    private String ds1TmCode = "";
    private String ds1TmLineCode = ""; 
    
    private String ds2TmTagid = ""; 
    private String ds2TmXTuning = ""; 
    private float f2TmXTuning = 0;
    private String ds2TmCode = "";
    private String ds2TmLineCode = ""; 
    
    private String ds3TmTagid = ""; 
    private String ds3TmXTuning = ""; 
    private float f3TmXTuning = 0;
    private String ds3TmCode = "";
    private String ds3TmLineCode = ""; 
    private String ds3TmRadar = "";
    
    private String ds4TmTagid = ""; 
    private String ds4TmXTuning = ""; 
    private float f4TmXTuning = 0;
    private String ds4TmCode = "";
    private String ds4TmLineCode = ""; 
    
    private String ds1LdTagid = ""; 
    private String ds1LdXTuning = ""; 
    private float f1LdXTuning = 0;
    private String ds1LdCode = "";
    private String ds1LdLineCode = ""; 
    
    private String ds2LdTagid = ""; 
    private String ds2LdXTuning = ""; 
    private float f2LdXTuning = 0;
    private String ds2LdCode = "";
    private String ds2LdLineCode = ""; 
    private String ds2LdRadar = "";
    
    private String ds1HmTagid = ""; 
    private String ds1HmXTuning = ""; 
    private float  f1HmXTuning = 0;
    private String ds1HmCode = "";
    private String ds1HmLineCode = "";
    
    private String ds2HmTagid = ""; 
    private String ds2HmXTuning = ""; 
    private float  f2HmXTuning = 0;
    private String ds2HmCode = "";
    private String ds2HmLineCode = "";
    
    private String ds1ScTagid = ""; 
    private String ds1ScXTuning = ""; 
    private float  f1ScXTuning = 0;
    private String ds1ScCode = "";
    private String ds1ScLineCode = "";

    private String dsCraneTmYRangeStart = "";
    private String dsCraneTmYRangeEnd = "";
    private String dsCraneLdYRangeStart = "";
    private String dsCraneLdYRangeEnd = "";
    private String dsCraneHmYRangeStart = "";
    private String dsCraneHmYRangeEnd = "";
    
    private float fCraneTmYRangeStart = 0; 
    private float fCraneTmYRangeEnd = 0;
    private float fCraneLdYRangeStart = 0; 
    private float fCraneLdYRangeEnd = 0;
    private float fCraneHmYRangeStart = 0; 
    private float fCraneHmYRangeEnd = 0;
    
    private String dsTmLineYRangeStart = "";
    private String dsTmLineYRangeEnd = "";
    private String dsLdLineYRangeStart = "";
    private String dsLdLineYRangeEnd = "";
    private String dsHmLineYRangeStart = "";
    private String dsHmLineYRangeEnd = "";

    private float fTmLineYRangeStart = 0; 
    private float fTmLineYRangeEnd = 0;
    private float fLdLineYRangeStart = 0; 
    private float fLdLineYRangeEnd = 0;
    private float fHmLineYRangeStart = 0; 
    private float fHmLineYRangeEnd = 0;
    
    /////////////////////////////////////////
    
    private String exL1_Weight = "-99";
    private String exL1_Distance = "-99";
    private String exL1_HoistHeight = "-99";
    private String exL1_Spare1 = "-99";
    private String exL1_Spare2 = "-99";
    
    private String exL2_Weight = "-99";
    private String exL2_Distance = "-99";
    private String exL2_HoistHeight = "-99";
    private String exL2_Spare1 = "-99";
    private String exL2_Spare2 = "-99";
    
    private String exT1_Weight = "-99";
    private String exT1_Distance = "-99";
    private String exT1_HoistHeight = "-99";
    private String exT1_Spare1 = "-99";
    private String exT1_Spare2 = "-99";
    
    private String exT2_Weight = "-99";
    private String exT2_Distance = "-99";
    private String exT2_HoistHeight = "-99";
    private String exT2_Spare1 = "-99";
    private String exT2_Spare2 = "-99";
    
    private String exT3_Weight = "-99";
    private String exT3_Distance = "-99";
    private String exT3_HoistHeight = "-99";
    private String exT3_Spare1 = "-99";
    private String exT3_Spare2 = "-99";
    
    private String exT4_Weight = "-99";
    private String exT4_Distance = "-99";
    private String exT4_HoistHeight = "-99";
    private String exT4_Spare1 = "-99";
    private String exT4_Spare2 = "-99";
    
    private String exH1_Weight = "-99";
    private String exH1_Distance = "-99";
    private String exH1_HoistHeight = "-99";
    private String exH1_Spare1 = "-99";
    private String exH1_Spare2 = "-99";
    
    private String exH2_Weight = "-99";
    private String exH2_Distance = "-99";
    private String exH2_HoistHeight = "-99";
    private String exH2_Spare1 = "-99";
    private String exH2_Spare2 = "-99";
    
    private String exS1_Weight = "-99";
    private String exS1_Distance = "-99";
    private String exS1_HoistHeight = "-99";
    private String exS1_Spare1 = "-99";
    private String exS1_Spare2 = "-99";
    
    private String exTmp_Weight = "-99";
    private String exTmp_Distance = "-99";
    private String exTmp_HoistHeight = "-99";
    private String exTmp_Spare1 = "-99";
    private String exTmp_Spare2 = "-99";
    
    ////////////////////////////////////////////////////////////////////
    
    private String dsLadleTagids = "";
    private String [] aryDsLadleTagids = null;
    
    private String dsLadleNums = "";
    private String [] aryDsLadleNums = null;
    
    private String dsHumanTagids = "";
    private String [] aryDsHumanTagids = null;
    
    private float fdsCraneDistanceMax = 0;
    private float fdsCraneDistanceMin = 0;
    private float fdsCraneDistanceVirtualMax = 0;
    private float fdsCraneDistanceVirtualMin = 0;
    private float fdsCraneDistanceDefault = 0;
    
    // Crane Hoist, Sub Hoist Value Set
    private String dsTmHoistHeight = "";
    private String dsLdHoistHeight = "";
    private String dsHmHoistHeight = "";
    private String dsTmSubhoistHeight = "";
    private String dsLdSubhoistHeight = "";
    private String dsHmSubhoistHeight = "";
    
	////////////////////////////////////////////////////////////////////
	
	public UWBLsWsPushProModifying() {
		super();
		// TODO Auto-generated constructor stub
		this.sdf1 = new SimpleDateFormat ("yyyyMMdd");
		this.sdf2 = new SimpleDateFormat ("HHmmss.SSS");
		this.sdf3 = new SimpleDateFormat ("yyyy");
		this.sdf4 = new SimpleDateFormat ("MM");
		this.timestamp = new Timestamp(System.currentTimeMillis());
		
		try
        {
			FileInputStream fis = new FileInputStream(resource);
        	this.properties.load(fis);

        	this.dsLogBackupPath = this.properties.getProperty("datasource.log.backup.path");

        	this.ds1TmTagid 	= properties.getProperty("datasource.crane.1TM.tagid");
        	this.ds1TmXTuning 	= properties.getProperty("datasource.crane.1TM.x.tuning");
        	this.f1TmXTuning 	= (this.ds1TmXTuning!= null && !"".equals(this.ds1TmXTuning)) ? Float.parseFloat(this.ds1TmXTuning) : 0;
        	this.ds1TmCode 		= properties.getProperty("datasource.crane.1TM.code");
        	this.ds1TmLineCode 	= properties.getProperty("datasource.crane.1TM.line.code");

        	this.ds2TmTagid 	= properties.getProperty("datasource.crane.2TM.tagid");
        	this.ds2TmXTuning 	= properties.getProperty("datasource.crane.2TM.x.tuning");
        	this.f2TmXTuning	= (this.ds2TmXTuning!= null && !"".equals(this.ds2TmXTuning)) ? Float.parseFloat(this.ds2TmXTuning) : 0;
        	this.ds2TmCode 		= properties.getProperty("datasource.crane.2TM.code");
        	this.ds2TmLineCode 	= properties.getProperty("datasource.crane.2TM.line.code");
            
        	this.ds3TmTagid 	= properties.getProperty("datasource.crane.3TM.tagid");
        	this.ds3TmXTuning 	= properties.getProperty("datasource.crane.3TM.x.tuning");
        	this.f3TmXTuning	= (this.ds3TmXTuning!= null && !"".equals(this.ds3TmXTuning)) ? Float.parseFloat(this.ds3TmXTuning) : 0;
        	this.ds3TmCode 		= properties.getProperty("datasource.crane.3TM.code");
        	this.ds3TmLineCode 	= properties.getProperty("datasource.crane.3TM.line.code");
        	this.ds3TmRadar		= properties.getProperty("datasource.crane.3TM.radar");
        	
        	this.ds4TmTagid 	= properties.getProperty("datasource.crane.4TM.tagid");
        	this.ds4TmXTuning 	= properties.getProperty("datasource.crane.4TM.x.tuning");
        	this.f4TmXTuning	= (this.ds4TmXTuning!= null && !"".equals(this.ds4TmXTuning)) ? Float.parseFloat(this.ds4TmXTuning) : 0;
        	this.ds4TmCode 		= properties.getProperty("datasource.crane.4TM.code");
        	this.ds4TmLineCode 	= properties.getProperty("datasource.crane.4TM.line.code");
            
        	this.ds1LdTagid 	= properties.getProperty("datasource.crane.1LD.tagid");
        	this.ds1LdXTuning 	= properties.getProperty("datasource.crane.1LD.x.tuning");
        	this.f1LdXTuning	= (this.ds1LdXTuning!= null && !"".equals(this.ds1LdXTuning)) ? Float.parseFloat(this.ds1LdXTuning) : 0;
        	this.ds1LdCode 		= properties.getProperty("datasource.crane.1LD.code");
        	this.ds1LdLineCode 	= properties.getProperty("datasource.crane.1LD.line.code");
            
        	this.ds2LdTagid 	= properties.getProperty("datasource.crane.2LD.tagid");
        	this.ds2LdXTuning 	= properties.getProperty("datasource.crane.2LD.x.tuning");
        	this.f2LdXTuning	= (this.ds2LdXTuning!= null && !"".equals(this.ds2LdXTuning)) ? Float.parseFloat(this.ds2LdXTuning) : 0;
        	this.ds2LdCode 		= properties.getProperty("datasource.crane.2LD.code");
        	this.ds2LdLineCode 	= properties.getProperty("datasource.crane.2LD.line.code");
        	this.ds2LdRadar		= properties.getProperty("datasource.crane.2LD.radar");
        	
        	this.ds1HmTagid 	= properties.getProperty("datasource.crane.1HM.tagid");
        	this.ds1HmXTuning 	= properties.getProperty("datasource.crane.1HM.x.tuning");
        	this.f1HmXTuning	= (this.ds1HmXTuning!= null && !"".equals(this.ds1HmXTuning)) ? Float.parseFloat(this.ds1HmXTuning) : 0;
        	this.ds1HmCode 		= properties.getProperty("datasource.crane.1HM.code");
        	this.ds1HmLineCode 	= properties.getProperty("datasource.crane.1HM.line.code");
        	
        	this.ds2HmTagid 	= properties.getProperty("datasource.crane.2HM.tagid");
        	this.ds2HmXTuning 	= properties.getProperty("datasource.crane.2HM.x.tuning");
        	this.f2HmXTuning	= (this.ds2HmXTuning!= null && !"".equals(this.ds2HmXTuning)) ? Float.parseFloat(this.ds2HmXTuning) : 0;
        	this.ds2HmCode 		= properties.getProperty("datasource.crane.2HM.code");
        	this.ds2HmLineCode 	= properties.getProperty("datasource.crane.2HM.line.code");
        	
        	this.ds1ScTagid 	= properties.getProperty("datasource.crane.1SC.tagid");
        	this.ds1ScXTuning 	= properties.getProperty("datasource.crane.1SC.x.tuning");
        	this.f1ScXTuning	= (this.ds1ScXTuning!= null && !"".equals(this.ds1ScXTuning)) ? Float.parseFloat(this.ds1ScXTuning) : 0;
        	this.ds1ScCode 		= properties.getProperty("datasource.crane.1SC.code");
        	this.ds1ScLineCode 	= properties.getProperty("datasource.crane.1SC.line.code");


        	this.dsCraneTmYRangeStart 	= properties.getProperty("datasource.crane.TM.y.range.start");
        	this.fCraneTmYRangeStart	= (this.dsCraneTmYRangeStart!= null && !"".equals(this.dsCraneTmYRangeStart)) ? Float.parseFloat(this.dsCraneTmYRangeStart) : 0;
            
        	this.dsCraneTmYRangeEnd 	= properties.getProperty("datasource.crane.TM.y.range.end");
        	this.fCraneTmYRangeEnd		= (this.dsCraneTmYRangeEnd!= null && !"".equals(this.dsCraneTmYRangeEnd)) ? Float.parseFloat(this.dsCraneTmYRangeEnd) : 0;
            
        	this.dsCraneLdYRangeStart 	= properties.getProperty("datasource.crane.LD.y.range.start");
        	this.fCraneLdYRangeStart	= (this.dsCraneLdYRangeStart!= null && !"".equals(this.dsCraneLdYRangeStart)) ? Float.parseFloat(this.dsCraneLdYRangeStart) : 0;
            
        	this.dsCraneLdYRangeEnd 	= properties.getProperty("datasource.crane.LD.y.range.end");
        	this.fCraneLdYRangeEnd		= (this.dsCraneLdYRangeEnd!= null && !"".equals(this.dsCraneLdYRangeEnd)) ? Float.parseFloat(this.dsCraneLdYRangeEnd) : 0;
        	
        	this.dsCraneHmYRangeStart 	= properties.getProperty("datasource.crane.HM.y.range.start");
        	this.fCraneHmYRangeStart		= (this.dsCraneHmYRangeStart!= null && !"".equals(this.dsCraneHmYRangeStart)) ? Float.parseFloat(this.dsCraneHmYRangeStart) : 0;
        	
        	this.dsCraneHmYRangeEnd 	= properties.getProperty("datasource.crane.HM.y.range.end");
        	this.fCraneHmYRangeEnd		= (this.dsCraneHmYRangeEnd!= null && !"".equals(this.dsCraneHmYRangeEnd)) ? Float.parseFloat(this.dsCraneHmYRangeEnd) : 0;
            
        	
        	this.dsTmLineYRangeStart 	= properties.getProperty("datasource.TM.LINE.y.range.start");
        	this.fTmLineYRangeStart		= (this.dsTmLineYRangeStart!= null && !"".equals(this.dsTmLineYRangeStart)) ? Float.parseFloat(this.dsTmLineYRangeStart) : 0;
            
        	this.dsTmLineYRangeEnd 		= properties.getProperty("datasource.TM.LINE.y.range.end");
            this.fTmLineYRangeEnd		= (this.dsTmLineYRangeEnd!= null && !"".equals(this.dsTmLineYRangeEnd)) ? Float.parseFloat(this.dsTmLineYRangeEnd) : 0;
            
            this.dsLdLineYRangeStart 	= properties.getProperty("datasource.LD.LINE.y.range.start");
            this.fLdLineYRangeStart		= (this.dsLdLineYRangeStart!= null && !"".equals(this.dsLdLineYRangeStart)) ? Float.parseFloat(this.dsLdLineYRangeStart) : 0;
            
            this.dsLdLineYRangeEnd 		= properties.getProperty("datasource.LD.LINE.y.range.end");
            this.fLdLineYRangeEnd		= (this.dsLdLineYRangeEnd!= null && !"".equals(this.dsLdLineYRangeEnd)) ? Float.parseFloat(this.dsLdLineYRangeEnd) : 0;
            
            this.dsHmLineYRangeStart 	= properties.getProperty("datasource.HM.LINE.y.range.start");
            this.fHmLineYRangeStart		= (this.dsHmLineYRangeStart!= null && !"".equals(this.dsHmLineYRangeStart)) ? Float.parseFloat(this.dsHmLineYRangeStart) : 0;
            
            this.dsHmLineYRangeEnd 		= properties.getProperty("datasource.HM.LINE.y.range.end");
            this.fHmLineYRangeEnd		= (this.dsHmLineYRangeEnd!= null && !"".equals(this.dsHmLineYRangeEnd)) ? Float.parseFloat(this.dsHmLineYRangeEnd) : 0;
            
            this.dsLadleTagids = properties.getProperty("datasource.crane.Ladle.tagid");
            this.aryDsLadleTagids = (properties.getProperty("datasource.crane.Ladle.tagid")).split(",");
            
            this.dsLadleNums = properties.getProperty("datasource.crane.Ladle.number");
            this.aryDsLadleNums = (properties.getProperty("datasource.crane.Ladle.number")).split(",");
            
            this.dsHumanTagids = properties.getProperty("datasource.crane.Human.tagid");
            this.aryDsHumanTagids = (properties.getProperty("datasource.crane.Human.tagid")).split(",");
            
            this.fdsCraneDistanceMax = Float.parseFloat(properties.getProperty("datasource.crane.distance.max"));
            this.fdsCraneDistanceMin = Float.parseFloat(properties.getProperty("datasource.crane.distance.min"));
            this.fdsCraneDistanceVirtualMax = Float.parseFloat(properties.getProperty("datasource.crane.distance.virtual.max"));
            this.fdsCraneDistanceVirtualMin = Float.parseFloat(properties.getProperty("datasource.crane.distance.virtual.min"));
            this.fdsCraneDistanceDefault = Float.parseFloat(properties.getProperty("datasource.crane.distance.defalut"));
            
            this.dsTmHoistHeight = properties.getProperty("datasource.TM.hoist.height");
            this.dsLdHoistHeight = properties.getProperty("datasource.LD.hoist.height");
            this.dsHmHoistHeight = properties.getProperty("datasource.HM.hoist.height");
            this.dsTmSubhoistHeight = properties.getProperty("datasource.TM.subhoist.height");
            this.dsLdSubhoistHeight = properties.getProperty("datasource.LD.subhoist.height");
            this.dsHmSubhoistHeight = properties.getProperty("datasource.HM.subhoist.height");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}

	public UWBLsWsPushProModifying(String userName, String password) {
		super(userName, password);
		// TODO Auto-generated constructor stub
	}

	public UWBLsWsPushProModifying(String userName, String password, Boolean isMD5) {
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
	
	public List<String> getUWBLogDataArrayList()
	{
		return this.parmUWBLogDataList;
	}
	
	public void addUWBLogDataArrayList(String pUWBLogData)
	{
		this.parmUWBLogDataList.add(pUWBLogData);
	}
	
	public void initUWBLogDataArrayList()
	{
		this.parmUWBLogDataList = new ArrayList<String>();
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
	
	private String calTExtendedData(String extendData)
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
				
				sbExDt.append(", ");
				sbExDt.append(sWeight);
				sbExDt.append(", ");
				sbExDt.append(sDistance);
				sbExDt.append(", ");
				sbExDt.append(sHoistHeight);
				sbExDt.append(", ");
				sbExDt.append(sSpare1);
				sbExDt.append(", ");
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
	
	private String [] calTExtendedDataArry(String extendData)
	{
		String[] ret = new String[5];
		
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

		if( extendData == null || "".equals(extendData) )
		{
			ret = null;
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
				
				sWeight = Integer.toString((iDec[1] << 8) + iDec[0]) + "." + Integer.toString(iDec[2]);
				sDistance = Integer.toString(iDec[3]) + "." + Integer.toString(iDec[4]);
				sHoistHeight = Integer.toString(iDec[5]);
				//sHoistHeight = String.format("%.2f", iDec[5]/10.0);
				sSpare1 = Integer.toString(iDec[6]);
				sSpare2 = Integer.toString(iDec[7]);

				ret[0] = sWeight;
				ret[1] = sDistance;
				ret[2] = sHoistHeight;
				ret[3] = sSpare1;
				ret[4] = sSpare2;
			}
			else
			{
				ret = null;
			}			
		}

		return ret;
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
		/*
		this.setTPosInfo(posList);
		TExtendedInfo testExtendedInfo = this.getTExtendedInfo();				
		System.out.println("OnPosInfo=========>" + posList);
		System.out.println("OnExtendedInfo=========>" + testExtendedInfo);
		this.setTExtendedInfo(null);
		*/
		////////////////////////////////////////////////////////////////////////////////////////////////
		this.printTagid = -1L;
		
		//this.parmExtendedInfo = this.getTExtendedInfo();
		HashMap<String, String> hashMap = new HashMap<>();
		JSONArray jsonArray = new JSONArray();
		
		StringBuffer tagInfoSb = null;
		String tmp_tagtype = "";		//크레인태그 1, 래들 2, 휴대형태그 3, 기타태그 (휴대형 태그 등) 0
		String tmp_tagid = "";
		String tmp_craneline = "";		//크레인 태그면 코드값, 아니면 -
		String tmp_cranecode = "";		//크레인 태그면 코드값, 아니면 -
		String tmp_posx = "";			//UWB Tag X 축 값
		String tmp_posy = "";			//UWB Tag Y 축 값
		String tmp_posz = "";			//UWB Tag Z 축 값
		String tmp_sleepmode = "";		//태그 슬립 모드 0 (작동중), 1 (쉬고있음)
		String tmp_weight = "";			//래들 무게 (크레인 태그일때 존재, 아니면 -99)
		String tmp_distance = "";		//크레인 Hook 횡방향 Y값 (크레인 태그일때 존재, 아니면 -99)
		String tmp_hoistheight = "";	//주권 hoist heigth (크레인 태그일때 존재, 아니면 -99)
		String tmp_spare1 = "";			//spare1 (추가1 데이타 존재, 아니면 -99)
		String tmp_spare2 = "";			//spare2 (추가2 데이타 존재, 아니면 -99)		
		String tmp_ymd = "";			//년월일
		String tmp_hms = "";			//시분초
		
		float ftmp_distance = 0;
		float ftmp_sub_distance = 0;
		
		float ftmp_ex_weight = 0;
		float ftmp_ex_spare2 = 0;
		String logExtErrTmp = "";

		for (TPosInfo tPosInfo : posList) 
		{
			if(this.printTagid==-1 || this.printTagid == tPosInfo.getTagId()) 
			{
				this.timestamp = new Timestamp(System.currentTimeMillis());
				tmp_tagtype = "0";
				tmp_tagid = String.valueOf( tPosInfo.getTagId() );
				tmp_craneline = "-";
				tmp_cranecode = "-";
				tmp_posx = String.format( "%.2f",  tPosInfo.getPosX() );
				tmp_posy = "";
				tmp_posz = String.format( "%.2f",  tPosInfo.getPosZ() );
				tmp_sleepmode = "0";
				tmp_weight = "-99";
				tmp_distance = "-99";
				tmp_hoistheight = "-99";
				tmp_spare1 = "-99";
				tmp_spare2 = "-99";
				ftmp_ex_weight = 0;
				ftmp_ex_spare2 = 0;
				logExtErrTmp = "";
				tmp_ymd = sdf1.format(this.timestamp);
				tmp_hms = sdf2.format(this.timestamp);
				
				if( this.ds1TmTagid != null && tmp_tagid != null && tmp_tagid.equals( this.ds1TmTagid) )
				{
					tmp_tagtype = "1";
					tmp_craneline = this.ds1TmLineCode;
					tmp_cranecode = this.ds1TmCode;
					tmp_posy = calcCraneYRange(this.fCraneTmYRangeStart, this.fCraneTmYRangeEnd, tPosInfo.getPosY());
				}
				else if( this.ds2TmTagid != null && tmp_tagid != null && tmp_tagid.equals( this.ds2TmTagid) )
				{
					tmp_tagtype = "1";
					tmp_craneline = this.ds2TmLineCode;
					tmp_cranecode = this.ds2TmCode;
					tmp_posy = calcCraneYRange(this.fCraneTmYRangeStart, this.fCraneTmYRangeEnd, tPosInfo.getPosY());					
				}
				else if( this.ds3TmTagid != null && tmp_tagid != null && tmp_tagid.equals( this.ds3TmTagid) )
				{
					tmp_tagtype = "1";
					tmp_craneline = this.ds3TmLineCode;
					tmp_cranecode = this.ds3TmCode;
					tmp_posy = calcCraneYRange(this.fCraneLdYRangeStart, this.fCraneLdYRangeEnd, tPosInfo.getPosY());					
				}
				else if( this.ds4TmTagid != null && tmp_tagid != null && tmp_tagid.equals( this.ds4TmTagid) )
				{
					tmp_tagtype = "1";
					tmp_craneline = this.ds4TmLineCode;
					tmp_cranecode = this.ds4TmCode;
					tmp_posy = calcCraneYRange(this.fCraneTmYRangeStart, this.fCraneTmYRangeEnd, tPosInfo.getPosY());
				}
				else if( this.ds1LdTagid != null && tmp_tagid != null && tmp_tagid.equals( this.ds1LdTagid) )
				{
					tmp_tagtype = "1";
					tmp_craneline = this.ds1LdLineCode;
					tmp_cranecode = this.ds1LdCode;
					tmp_posy = calcCraneYRange(this.fCraneLdYRangeStart, this.fCraneLdYRangeEnd, tPosInfo.getPosY());
				}
				else if( this.ds2LdTagid != null && tmp_tagid != null && tmp_tagid.equals( this.ds2LdTagid) )
				{
					tmp_tagtype = "1";
					tmp_craneline = this.ds2LdLineCode;
					tmp_cranecode = this.ds2LdCode;
					tmp_posy = calcCraneYRange(this.fCraneLdYRangeStart, this.fCraneLdYRangeEnd, tPosInfo.getPosY());
				}
				else if( this.ds1HmTagid != null && tmp_tagid != null && tmp_tagid.equals( this.ds1HmTagid) )
				{
					tmp_tagtype = "1";
					tmp_craneline = this.ds1HmLineCode;
					tmp_cranecode = this.ds1HmCode;
					tmp_posy = calcCraneYRange(this.fCraneHmYRangeStart, this.fCraneHmYRangeEnd, tPosInfo.getPosY());
				}
				else if( this.ds2HmTagid != null && tmp_tagid != null && tmp_tagid.equals( this.ds2HmTagid) )
				{
					tmp_tagtype = "1";
					tmp_craneline = this.ds2HmLineCode;
					tmp_cranecode = this.ds2HmCode;
					tmp_posy = calcCraneYRange(this.fCraneHmYRangeStart, this.fCraneHmYRangeEnd, tPosInfo.getPosY());
				}
				else if( this.ds1ScTagid != null && tmp_tagid != null && tmp_tagid.equals( this.ds1ScTagid) )
				{
					tmp_tagtype = "1";
					tmp_craneline = this.ds1ScLineCode;
					tmp_cranecode = this.ds1ScCode;
					tmp_posy = calcCraneYRange(this.fCraneHmYRangeStart, this.fCraneHmYRangeEnd, tPosInfo.getPosY());
				}
				else if( this.dsLadleTagids != null && tmp_tagid != null && dsLadleTagids.indexOf(tmp_tagid) > -1 )
				{
					tmp_tagtype = "2";
					tmp_craneline = "";
					tmp_cranecode = "";
					
					for(int ii = 0 ; ii < this.aryDsLadleTagids.length ; ii++ )
					{
						if( ( this.aryDsLadleTagids[ii] ).equals( tmp_tagid ) )
						{
							tmp_cranecode = this.aryDsLadleNums[ii];
							break;
						}
					}
					
					tmp_posy = String.format( "%.2f",  tPosInfo.getPosY() );
				}
				else if( this.dsHumanTagids != null && tmp_tagid != null && dsHumanTagids.indexOf(tmp_tagid) > -1 )
				{
					tmp_tagtype = "3";
					tmp_craneline = "";
					tmp_cranecode = "";
					
					for(int ii = 0 ; ii < this.aryDsHumanTagids.length ; ii++ )
					{
						if( ( this.aryDsHumanTagids[ii] ).equals( tmp_tagid ) )
						{
							tmp_cranecode = this.aryDsHumanTagids[ii];
							break;
						}
					}

					tmp_posy = String.format( "%.2f",  tPosInfo.getPosY() );
				}
				else
				{
					tmp_tagtype = "9";
					tmp_craneline = "";
					tmp_cranecode = "";
					tmp_posy = String.format( "%.2f",  tPosInfo.getPosY() );
				}


				if( tPosInfo.getSleep() == false )	tmp_sleepmode = "0";
				else								tmp_sleepmode = "1";

				if( "1".equals(tmp_tagtype) )
				{
					//칭량 데이터
					if( this.arExtendedAdd != null )
					{
						int arSize = this.arExtendedAdd.size();
						if(arSize > 0)
						{
							for(int i=0 ; i<arSize ; i++)
							{
								this.parmExtendedInfo = this.arExtendedAdd.get(i);
								if( this.parmExtendedInfo != null )
								{	
									if( this.parmExtendedInfo.getTagId() != null )
									{
										if( tmp_tagid.equals( String.valueOf( this.parmExtendedInfo.getTagId() ) ) )
										{
											String [] tmp_extdata = calTExtendedDataArry(this.parmExtendedInfo.getData());
											if( tmp_extdata != null )
											{
												tmp_weight 		= tmp_extdata[0];
												tmp_distance 	= tmp_extdata[1];
												tmp_hoistheight = tmp_extdata[2];
												tmp_spare1		= tmp_extdata[3];
												tmp_spare2		= tmp_extdata[4];
												
												if( tmp_distance != null && !"".equals(tmp_distance) )
												{
													ftmp_distance = Float.parseFloat(tmp_distance);
													
													if( ftmp_distance > this.fdsCraneDistanceMax )
													{
														tmp_distance = String.format( "%.2f",  this.fdsCraneDistanceMax );
													}
													else if( ftmp_distance < this.fdsCraneDistanceMin )
													{
														tmp_distance = String.format( "%.2f",  this.fdsCraneDistanceMin );
													}
												}
												else
												{
													tmp_distance = String.format( "%.2f", ( this.fdsCraneDistanceMin + (this.fdsCraneDistanceMax - this.fdsCraneDistanceMin) / 2 ) );
												}

												///////////////////////////////////////////////////////
												/// 칭량 시스템 예상 범위를 벗어나는 데이터가 왔을때 처리하는 로직
												/// 데이터 무시하고, 년월일시분초_ext_err_log.txt 파일에 저장
												///////////////////////////////////////////////////////
												try
												{
													//숫자가 아닌 문자, 특수문자 등이 들어와 오류가 발생되는 상황이 문제가 되지 않도록 처리
													ftmp_ex_weight = Float.parseFloat(tmp_weight);
												}
												catch(Exception e)
												{
													//조건식에 맞지 않는 임의의 값 지정
													ftmp_ex_weight = 750;
													
													e.printStackTrace();
												}
												
												try
												{
													//숫자가 아닌 문자, 특수문자 등이 들어와 오류가 발생되는 상황이 문제가 되지 않도록 처리
													ftmp_ex_spare2 = Float.parseFloat(tmp_spare2);
												}
												catch(Exception e)
												{
													//조건식에 맞지 않는 임의의 값 지정
													ftmp_ex_spare2 = 10;
													
													e.printStackTrace();
												}
												
												if( ftmp_ex_weight > 740 || ( ftmp_ex_spare2 < 0 || ftmp_ex_spare2 > 3 ) )
												{
													logExtErrTmp = "["+this.sdf1.format(timestamp)+"_"+this.sdf2.format(timestamp)+"] "+ tmp_cranecode +", "+tmp_weight+", "+tmp_distance+", "+tmp_hoistheight+", "+tmp_spare1+", "+tmp_spare2;
													this.setFileWirte(this.dsLogBackupPath, this.timestamp, this.sdf1.format(timestamp)+"_ext_err_log.txt", logExtErrTmp);

													if( ftmp_ex_weight > 740 )
													{
														tmp_weight = "-99";
													}
													
													if( ftmp_ex_spare2 < 0 || ftmp_ex_spare2 > 3 )
													{
														tmp_spare2 = "-99";
													}
												}

												///////////////////////////////////////////////////////
												///////////////////////////////////////////////////////

												if( "1L".equals(tmp_cranecode) )
												{
													exL1_Weight = tmp_weight;
													exL1_Distance = tmp_distance;
													exL1_HoistHeight = tmp_hoistheight;
													exL1_Spare1 = tmp_spare1;
													exL1_Spare2 = tmp_spare2;
												}
												else if( "2L".equals(tmp_cranecode) )
												{
													exL2_Weight = tmp_weight;
													exL2_HoistHeight = tmp_hoistheight;
													exL2_Spare1 = tmp_spare1;
													exL2_Spare2 = tmp_spare2;
													exL2_Distance = tmp_distance;
												}
												else if( "1T".equals(tmp_cranecode) )
												{
													exT1_Weight = tmp_weight;
													exT1_Distance = tmp_distance;
													exT1_HoistHeight = tmp_hoistheight;
													exT1_Spare1 = tmp_spare1;
													exT1_Spare2 = tmp_spare2;
												}
												else if( "2T".equals(tmp_cranecode) )
												{
													exT2_Weight = tmp_weight;
													exT2_Distance = tmp_distance;
													exT2_HoistHeight = tmp_hoistheight;
													exT2_Spare1 = tmp_spare1;
													exT2_Spare2 = tmp_spare2;
												}
												else if( "3T".equals(tmp_cranecode) )
												{
													exT3_Weight = tmp_weight;
													exT3_HoistHeight = tmp_hoistheight;
													exT3_Spare1 = tmp_spare1;
													exT3_Spare2 = tmp_spare2;
													exT3_Distance = tmp_distance;
													
												}
												else if( "4T".equals(tmp_cranecode) )
												{
													exT4_Weight = tmp_weight;
													exT4_Distance = tmp_distance;
													exT4_HoistHeight = tmp_hoistheight;
													exT4_Spare1 = tmp_spare1;
													exT4_Spare2 = tmp_spare2;
												}
												else if( "1H".equals(tmp_cranecode) )
												{
													exH1_Weight = tmp_weight;
													exH1_Distance = tmp_distance;
													exH1_HoistHeight = tmp_hoistheight;
													exH1_Spare1 = tmp_spare1;
													exH1_Spare2 = tmp_spare2;
												}
												else if( "2H".equals(tmp_cranecode) )
												{
													exH2_Weight = tmp_weight;
													exH2_Distance = tmp_distance;
													exH2_HoistHeight = tmp_hoistheight;
													exH2_Spare1 = tmp_spare1;
													exH2_Spare2 = tmp_spare2;
												}
												else if( "1S".equals(tmp_cranecode) )
												{
													exS1_Weight = tmp_weight;
													exS1_Distance = tmp_distance;
													exS1_HoistHeight = tmp_hoistheight;
													exS1_Spare1 = tmp_spare1;
													exS1_Spare2 = tmp_spare2;
												}
											}
											
											break;
										}
									}
								}
							}
						}
					}
					
				}
				else if( "2".equals(tmp_tagtype) )
				{
					exTmp_Weight = "-99";
					exTmp_Distance = "-99";
					exTmp_HoistHeight = "-99";
					exTmp_Spare1 = "-99";
					exTmp_Spare2 = "-99";
				}
				else if( "3".equals(tmp_tagtype) )
				{
					exTmp_Weight = "-99";
					exTmp_Distance = "-99";
					exTmp_HoistHeight = "-99";
					exTmp_Spare1 = "-99";
					exTmp_Spare2 = "-99";					
				}
				else
				{
					exTmp_Weight = "-99";
					exTmp_Distance = "-99";
					exTmp_HoistHeight = "-99";
					exTmp_Spare1 = "-99";
					exTmp_Spare2 = "-99";					
				}
				
				/////////////////////////////////////////
				tagInfoSb = new StringBuffer();
				tagInfoSb.append(tmp_tagtype);
				tagInfoSb.append(", ");
				tagInfoSb.append(tmp_tagid);
				tagInfoSb.append(", ");
				tagInfoSb.append(tmp_craneline);
				tagInfoSb.append(", ");
				tagInfoSb.append(tmp_cranecode);
				tagInfoSb.append(", ");
				tagInfoSb.append(tmp_posx);
				tagInfoSb.append(", ");
				tagInfoSb.append(tmp_posy);
				tagInfoSb.append(", ");
				tagInfoSb.append(tmp_posz);
				tagInfoSb.append(", ");
				tagInfoSb.append(tmp_sleepmode);
				
				if( "1L".equals(tmp_cranecode) )
				{
					tagInfoSb.append(", ");
					tagInfoSb.append(exL1_Weight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exL1_Distance);
					tagInfoSb.append(", ");
					tagInfoSb.append(exL1_HoistHeight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exL1_Spare1);
					tagInfoSb.append(", ");
					tagInfoSb.append(exL1_Spare2);
				}
				else if( "2L".equals(tmp_cranecode) )
				{	
					tagInfoSb.append(", ");
					tagInfoSb.append(exL2_Weight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exL2_Distance);
					tagInfoSb.append(", ");
					tagInfoSb.append(exL2_HoistHeight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exL2_Spare1);
					tagInfoSb.append(", ");
					tagInfoSb.append(exL2_Spare2);
				}
				else if( "1T".equals(tmp_cranecode) )
				{
					tagInfoSb.append(", ");
					tagInfoSb.append(exT1_Weight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT1_Distance);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT1_HoistHeight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT1_Spare1);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT1_Spare2);
				}
				else if( "2T".equals(tmp_cranecode) )
				{
					tagInfoSb.append(", ");
					tagInfoSb.append(exT2_Weight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT2_Distance);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT2_HoistHeight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT2_Spare1);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT2_Spare2);
				}
				else if( "3T".equals(tmp_cranecode) )
				{
					tagInfoSb.append(", ");
					tagInfoSb.append(exT3_Weight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT3_Distance);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT3_HoistHeight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT3_Spare1);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT3_Spare2);
				}
				else if( "4T".equals(tmp_cranecode) )
				{
					tagInfoSb.append(", ");
					tagInfoSb.append(exT4_Weight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT4_Distance);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT4_HoistHeight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT4_Spare1);
					tagInfoSb.append(", ");
					tagInfoSb.append(exT4_Spare2);
				}
				else if( "1H".equals(tmp_cranecode) )
				{
					tagInfoSb.append(", ");
					tagInfoSb.append(exH1_Weight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exH1_Distance);
					tagInfoSb.append(", ");
					tagInfoSb.append(exH1_HoistHeight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exH1_Spare1);
					tagInfoSb.append(", ");
					tagInfoSb.append(exH1_Spare2);
				}
				else if( "2H".equals(tmp_cranecode) )
				{
					tagInfoSb.append(", ");
					tagInfoSb.append(exH2_Weight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exH2_Distance);
					tagInfoSb.append(", ");
					tagInfoSb.append(exH2_HoistHeight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exH2_Spare1);
					tagInfoSb.append(", ");
					tagInfoSb.append(exH2_Spare2);
				}
				else if( "1S".equals(tmp_cranecode) )
				{
					tagInfoSb.append(", ");
					tagInfoSb.append(exS1_Weight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exS1_Distance);
					tagInfoSb.append(", ");
					tagInfoSb.append(exS1_HoistHeight);
					tagInfoSb.append(", ");
					tagInfoSb.append(exS1_Spare1);
					tagInfoSb.append(", ");
					tagInfoSb.append(exS1_Spare2);
				}
				else
				{
					tagInfoSb.append(", -99");		//Weight
					tagInfoSb.append(", -99");		//Distance
					tagInfoSb.append(", -99");		//HoistHeight
					tagInfoSb.append(", -99");		//Spare1
					tagInfoSb.append(", -99");		//Spare2	
				}

				tagInfoSb.append(", ");
				tagInfoSb.append(tmp_ymd);
				tagInfoSb.append(", ");
				tagInfoSb.append(tmp_hms);

				hashMap = new HashMap<>();
				hashMap.put("tagtype", tmp_tagtype);
				hashMap.put("tagid", tmp_tagid);
				hashMap.put("craneline", tmp_craneline);
				hashMap.put("cranecode", tmp_cranecode);
				hashMap.put("posx", tmp_posx);
				hashMap.put("posy", tmp_posy);
				hashMap.put("posz", tmp_posz);
				hashMap.put("sleepmode", tmp_sleepmode);

				if( "1L".equals(tmp_cranecode) )
				{
					hashMap.put("weight", exL1_Weight);
					
					if( "-99".equals(exL1_Distance) )
					{
						hashMap.put("distance", "0");
					}
					else
					{
						hashMap.put("distance", exL1_Distance);
					}
					
					if( "-99".equals(exL1_HoistHeight) )
					{
						hashMap.put("hoistheight", this.dsLdHoistHeight);
					}
					else
					{
						hashMap.put("hoistheight", exL1_HoistHeight);
					}
				}
				else if( "2L".equals(tmp_cranecode) )
				{	
					hashMap.put("weight", exL2_Weight);
					
					if( "-99".equals(exL2_Distance) )
					{
						hashMap.put("distance", "0");
					}
					else
					{
						hashMap.put("distance", exL2_Distance);
					}
					
					if( "-99".equals(exL2_HoistHeight) )
					{
						hashMap.put("hoistheight", this.dsLdHoistHeight);
					}
					else
					{
						hashMap.put("hoistheight", exL2_HoistHeight);
					}
				}
				else if( "1T".equals(tmp_cranecode) )
				{
					hashMap.put("weight", exT1_Weight);
					
					if( "-99".equals(exT1_Distance) )
					{
						hashMap.put("distance", "0");
					}
					else
					{
						hashMap.put("distance", exT1_Distance);
					}
					
					if( "-99".equals(exT1_HoistHeight) )
					{
						hashMap.put("hoistheight", this.dsTmHoistHeight);
					}
					else
					{
						hashMap.put("hoistheight", exT1_HoistHeight);
					}
				}
				else if( "2T".equals(tmp_cranecode) )
				{
					hashMap.put("weight", exT2_Weight);
					
					if( "-99".equals(exT2_Distance) )
					{
						hashMap.put("distance", "0");
					}
					else
					{
						hashMap.put("distance", exT2_Distance);
					}
					
					if( "-99".equals(exT2_HoistHeight) )
					{
						hashMap.put("hoistheight", this.dsTmHoistHeight);
					}
					else
					{
						hashMap.put("hoistheight", exT2_HoistHeight);
					}
				}
				else if( "3T".equals(tmp_cranecode) )
				{
					hashMap.put("weight", exT3_Weight);
					
					if( "-99".equals(exT3_Distance) )
					{
						hashMap.put("distance", "0");
					}
					else
					{
						hashMap.put("distance", exT3_Distance);
					}
					
					if( "-99".equals(exT3_HoistHeight) )
					{
						hashMap.put("hoistheight", this.dsLdHoistHeight);
					}
					else
					{
						hashMap.put("hoistheight", exT3_HoistHeight);
					}
				}
				else if( "4T".equals(tmp_cranecode) )
				{
					hashMap.put("weight", exT4_Weight);
					
					if( "-99".equals(exT4_Distance) )
					{
						hashMap.put("distance", "0");
					}
					else
					{
						hashMap.put("distance", exT4_Distance);
					}
					
					if( "-99".equals(exT4_HoistHeight) )
					{
						hashMap.put("hoistheight", this.dsTmHoistHeight);
					}
					else
					{
						hashMap.put("hoistheight", exT4_HoistHeight);
					}
				}
				else if( "1H".equals(tmp_cranecode) )
				{
					hashMap.put("weight", exH1_Weight);
					
					if( "-99".equals(exH1_Distance) )
					{
						hashMap.put("distance", "0");
					}
					else
					{
						hashMap.put("distance", exH1_Distance);
					}
					
					if( "-99".equals(exH1_HoistHeight) )
					{
						hashMap.put("hoistheight", this.dsHmHoistHeight);
					}
					else
					{
						hashMap.put("hoistheight", exH1_HoistHeight);
					}
				}
				else if( "2H".equals(tmp_cranecode) )
				{
					hashMap.put("weight", exH2_Weight);
					
					if( "-99".equals(exH2_Distance) )
					{
						hashMap.put("distance", "0");
					}
					else
					{
						hashMap.put("distance", exH2_Distance);
					}
					
					if( "-99".equals(exH2_HoistHeight) )
					{
						hashMap.put("hoistheight", this.dsHmHoistHeight);
					}
					else 									
					{
						hashMap.put("hoistheight", exH2_HoistHeight);
					}
				}
				else if( "1S".equals(tmp_cranecode) )
				{
					hashMap.put("weight", exS1_Weight);
					
					if( "-99".equals(exS1_Distance) )		
					{
						hashMap.put("distance", "0");
					}
					else 									
					{
						hashMap.put("distance", exS1_Distance);
					}
					
					if( "-99".equals(exS1_HoistHeight) )
					{
						hashMap.put("hoistheight", this.dsHmHoistHeight);
					}
					else
					{
						hashMap.put("hoistheight", exS1_HoistHeight);
					}
				}
				else
				{
					hashMap.put("weight", "-99");
					hashMap.put("distance", "-99");
					hashMap.put("hoistheight", "-99");
				}
				
				hashMap.put("ymd", tmp_ymd);
				hashMap.put("hms", tmp_hms);
				JSONObject tmpJson = new JSONObject(hashMap);
				jsonArray.put(tmpJson);

				System.out.println(tagInfoSb.toString());
				this.setFileWirte(this.dsLogBackupPath, this.timestamp, this.sdf1.format(timestamp)+".txt", tagInfoSb.toString());
				////////////////////////////////////////
			}
		}
		
		if( this.arExtendedAdd != null && this.arExtendedAdd.size() > 0 )
		{
			this.initExtendedInfo();
		}		
		
		this.setUWBTagInfo(jsonArray.toString());
		//this.setFileWirte(this.dsLogBackupPath, this.timestamp, this.sdf1.format(timestamp)+"_json.txt", jsonArray.toString());
        ////////////////////////////////////////////////////////////////////////////////////////////////	
	}
	
	public String calcCraneYRange(float fStart, float fEnd, float fPosY)
	{
		String sRet = "";
	
		if( fStart <= fPosY && fEnd >= fPosY )
		{
			sRet = String.format( "%.2f",  fPosY );
		}
		else
		{

			sRet = String.format( "%.2f",  ((fStart+fEnd) / 2) );
		}

		return sRet;
	}
	
	@Override
	public void OnExtendedInfo(TExtendedInfo extendedInfo) {
		
		if (extendedInfo.getDataType() == 189)
		{
			if( extendedInfo.getData() != null && "09BD".equals( extendedInfo.getData().substring(0,4))  )
			{
				this.addExtededInfo(extendedInfo);
			}
			//System.out.println("OnExtendedInfo=========>" + extendedInfo);
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

	//AP Data JAVA API 웹 문서 3.4.29	基站状态信息
	//baseState	int	基站状态，0表示故障，1表示正在发送数据，2表示心跳 -> 기지국 상태, 0은 고장, 1은 데이터 전송 중, 2는 하트비트
	@Override
	public void OnBaseState(TBaseState baseState) {
		System.out.println(baseState);
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
