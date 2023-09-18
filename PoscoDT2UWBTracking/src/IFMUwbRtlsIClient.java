import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.io.FileInputStream;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/*
 *   /usr/local/java/jdk1.8.0_321/jre/bin/java IFMUwbRtlsIClient  
 */

public class IFMUwbRtlsIClient implements Runnable 
{

	private boolean debug = false;
	private int crunchifyRunEveryNSeconds = 60;	
	private long lastKnownPosition = 0;
	private boolean shouldIRun = true;
	private File crunchifyFile = null;
	private static int dataCounter = 1;
	private int LoopCnt_01 = 0;
	
	private String curYYYY = "";
	private String curMM = "";
	private String curYYYYMMDD = "";
	private static int setDataCount = 60;
	//private static int setIFDataLen = 50;
	
	private static int isIFMUse = 1;
	private static int isViewYn = 1;
	private static int isLogYn = 1;
	
	private String dsLogBackupPath = "";
	
	
	private File f = null;
	FileWriter fw = null;
	BufferedWriter bfw = null;
	

	////////////////////////////////////////////////////////////////////
	private String resource = "/usr/local/posco/uwbpos/set.properties";
	private Properties properties = new Properties();
	////////////////////////////////////////////////////////////////////

	SimpleDateFormat sdf1 = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
	SimpleDateFormat sdf2 = new SimpleDateFormat ("yyyyMMddHHmmss");
	
	SimpleDateFormat sdfYYYY = new SimpleDateFormat ("yyyy");
	SimpleDateFormat sdfMM = new SimpleDateFormat ("MM");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat ("yyyyMMdd");
	
	Timestamp  timestamp = new Timestamp(System.currentTimeMillis());
	
	private String [] arryTmp = null;
	private int arrLen = 0;
	
	private StringBuffer sbHeader = new StringBuffer();
	private StringBuffer sbRet = new StringBuffer();
	
	private String tmp_tagtype = "";        //태그 유형 1 : 크레인, 0 : 휴대형
	private String tmp_tagid = "";          //태그 ID
	private String tmp_craneline = "";		//크레인 태그면 코드값, 아니면 -
	private String tmp_cranecode = "";		//크레인 태그면 코드값, 아니면 -
	private String tmp_posx = "";			//UWB Tag X 축 값
	private String tmp_posy = "";			//UWB Tag Y 축 값
	private String tmp_posz = "";			//UWB Tag Z 축 값
	private String tmp_sleepmode = "";		//태그 슬립 모드 0 (작동중), 1 (쉬고있음)
	private String tmp_weight = "";			//래들 무게 (크레인 태그일때 존재, 아니면 -99)
	private String tmp_distance = "";		//크레인 Hook 횡방향 Y값 (크레인 태그일때 존재, 아니면 -99)
	private String tmp_hoistheight = "";	//주권 hoist heigth (크레인 태그일때 존재, 아니면 -99)
	private String tmp_subhoistheight = "";	//주권 hoist heigth (크레인 태그일때 존재, 아니면 -99)
	private String tmp_spare1 = "";			//spare1 (추가1 데이타 존재, 아니면 -99)
	private String tmp_spare2 = "";			//spare2 (추가2 데이타 존재, 아니면 -99) 영 Ladle(F) 3, 공 Ladle 2,  빈(N) 1
	private String tmp_rweight = "";		//실시간 무게 데이타
	private String tmp_rmove = "";			//실시간 움직임 확인 데이타
	private String tmp_ymd = "";			//년월일
	private String tmp_hms = "";			//시분초
	private String tmp_old_spare2 = "";		//과거 spare2 (추가2 데이타 존재, 아니면 -99)
	
	private String tmp_dstlladnum = "";		//수강래들번호
	private String tmp_eqprfidtagid1 = "";	//설비 RFID_TagID 크레인에 실린 경우 (UWB)래들 Tag ID
	private String tmp_bindingflag1 = "";	//BindingFlag_1
	private String tmp_mtlno = "";			//재료번호
	private String tmp_old_dstlladnum = "";		//수강래들번호
	private String tmp_old_eqprfidtagid1 = "";	//설비 RFID_TagID 크레인에 실린 경우 (UWB)래들 Tag ID
	private String tmp_old_bindingflag1 = "";	//BindingFlag_1
	private String tmp_old_mtlno = "";			//재료번호

	private Socket socket = null;
	private InetSocketAddress isaSvr = null;
	private OutputStream out = null;
	private PrintWriter pr = null;
	////////////////////////////////////////////////////////////////////////
	private String Poscoict_Svr_Ip = "";
	private int Poscoict_Svr_Port = -1;
	
	private String jdbc_driver = "";
	private String jdbc_url = "";
	private String db_id = "";
	private String db_pw = "";
	
	private String dsLadleTagids = "";
    private String [] aryDsLadleTagids = null;
    
    private String dsLadleNums = "";
    private String [] aryDsLadleNums = null;
    
    private String dsHumanTagids = "";
    private String [] aryDsHumanTagids = null;
    
    //RH
    private float fds1RHs = 0;
    private float fds1RHe = 0;
    private float fds2RHs = 0;
    private float fds2RHe = 0;
    private float fds3RHs = 0;
    private float fds3RHe = 0;
    
    //BAP
    private float fds1Baps = 0;
    private float fds1Bape = 0;
    private float fds2Baps = 0;
    private float fds2Bape = 0;
    private float fds3Baps = 0;
    private float fds3Bape = 0;
    
    //LF
    private float fds1LFs = 0;
    private float fds1LFe = 0;
    
    //R대차, 회송대차
    private float fds1RCs = 0;
    private float fds1RCe = 0;
    
    //M/C
    private float fds1MCs = 0;
    private float fds1MCe = 0;
    private float fds2MCs = 0;
    private float fds2MCe = 0;
    private float fds3MCs = 0;
    private float fds3MCe = 0;
    private float fds4MCs = 0;
    private float fds4MCe = 0;
    
    // T/M Crane-Ladle status
    private String dsCraneStateCdF = "F";
    private String dsCraneStateCdE = "E";
    private String dsCraneStateCdN = "N";
    
    // Crane Distance Range Value Set
    private float fdsCraneDistanceMax = 0;
    private float fdsCraneDistanceMin = 0;
    private float fdsCraneDistanceVirtualMax = 0;
    private float fdsCraneDistanceVirtualMin = 0;
    private float fdsCraneDistanceDefault = 0;
    private String dsCraneConvertDistanceDefault = "";
    
    // Crane Hoist, Sub Hoist Value Set
    private String dsTmHoistHeight = "";
    private String dsLdHoistHeight = "";
    private String dsHmHoistHeight = "";
    private String dsTmSubhoistHeight = "";
    private String dsLdSubhoistHeight = "";
    private String dsHmSubhoistHeight = "";    
    
    private float fDsTmHoistHeight = 0;
    private float fDsLdHoistHeight = 0;
    private float fDsHmHoistHeight = 0;
    private float fDsTmSubhoistHeight = 0;
    private float fDsLdSubhoistHeight = 0;
    private float fDsHmSubhoistHeight = 0;
    
    //TLTC H6, H5 X-axis Range
    private float fDsTltcH5_1_s = 0;
    private float fDsTltcH5_1_e = 0;
    private float fDsTltcH5_2_s = 0;
    private float fDsTltcH5_2_e = 0;
    private float fDsTltcH6_1_s = 0;
    private float fDsTltcH6_1_e = 0;
    private float fDsTltcH6_2_s = 0;
    private float fDsTltcH6_2_e = 0;
    
    // IFM Send Data Count
    private int dsIFMDataCount = 0;
    
    // 1~3 Convert x-range
    private float fDsConvertMin = 0;
    private float fDsConvertMax = 0;
    
    // 용선, L/D, T/M 크레인 x 좌표값 조정값
    private String ds1TmXTuning = ""; 
    private float f1TmXTuning = 0;
    
    private String ds2TmXTuning = ""; 
    private float f2TmXTuning = 0;
    
    private String ds3TmXTuning = ""; 
    private float f3TmXTuning = 0;
    
    private String ds4TmXTuning = ""; 
    private float f4TmXTuning = 0;
    
    private String ds1LdXTuning = ""; 
    private float f1LdXTuning = 0;
    
    private String ds2LdXTuning = ""; 
    private float f2LdXTuning = 0;
    
    private String ds1HmXTuning = ""; 
    private float  f1HmXTuning = 0;
    
    private String ds2HmXTuning = ""; 
    private float  f2HmXTuning = 0;
    
    private String ds1ScXTuning = ""; 
    private float  f1ScXTuning = 0;
    ////////////////////////////////////////////////////////////////////////
	
	public IFMUwbRtlsIClient()
	{
		
	}
	
	public IFMUwbRtlsIClient(int myInterval) 
	{
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.curYYYY = this.sdfYYYY.format(this.timestamp);
		this.curMM = this.sdfMM.format(this.timestamp);
		this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
		
		try
		{
			FileInputStream fis = new FileInputStream(this.resource);
	    	this.properties.load(fis);
	    	
	    	//log file path
	    	this.dsLogBackupPath = properties.getProperty("datasource.log.backup.path");

	    	String myFile = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +".txt";
			this.crunchifyFile = new File(myFile);
			this.crunchifyRunEveryNSeconds = myInterval;

			//IFM
			this.Poscoict_Svr_Ip = properties.getProperty("datasource.poscoict.ifm.svr.ip");
			this.Poscoict_Svr_Port = Integer.parseInt(properties.getProperty("datasource.poscoict.ifm.svr.port"));
			
			//Mysql DB Server
			this.jdbc_driver 	= properties.getProperty("datasource.poscoict.ucube.db.driver");
			this.jdbc_url 		= properties.getProperty("datasource.poscoict.ucube.db.url");
			this.db_id 			= properties.getProperty("datasource.poscoict.ucube.db.id");
			this.db_pw 			= properties.getProperty("datasource.poscoict.ucube.db.pw");
			
			//Ladle Tags Info
			this.dsLadleTagids = properties.getProperty("datasource.crane.Ladle.tagid");
			this.aryDsLadleTagids = (properties.getProperty("datasource.crane.Ladle.tagid")).split(",");
		    
			this.dsLadleNums = properties.getProperty("datasource.crane.Ladle.number");
			this.aryDsLadleNums = (properties.getProperty("datasource.crane.Ladle.number")).split(",");
		    
		    //Human Tags Info
			this.dsHumanTagids = properties.getProperty("datasource.crane.Human.tagid");
			this.aryDsHumanTagids = (properties.getProperty("datasource.crane.Human.tagid")).split(",");
		    
		    //RH
			this.fds1RHs = Float.parseFloat(properties.getProperty("datasource.crane.1rh.s"));
			this.fds1RHe = Float.parseFloat(properties.getProperty("datasource.crane.1rh.e"));
			this.fds2RHs = Float.parseFloat(properties.getProperty("datasource.crane.2rh.s"));
			this.fds2RHe = Float.parseFloat(properties.getProperty("datasource.crane.2rh.e"));
			this.fds3RHs = Float.parseFloat(properties.getProperty("datasource.crane.3rh.s"));
			this.fds3RHe = Float.parseFloat(properties.getProperty("datasource.crane.3rh.e"));
		    
		    //BAP
			this.fds1Baps = Float.parseFloat(properties.getProperty("datasource.crane.1bap.s"));
			this.fds1Bape = Float.parseFloat(properties.getProperty("datasource.crane.1bap.e"));
			this.fds2Baps = Float.parseFloat(properties.getProperty("datasource.crane.2bap.s"));
			this.fds2Bape = Float.parseFloat(properties.getProperty("datasource.crane.2bap.e"));
			this.fds3Baps = Float.parseFloat(properties.getProperty("datasource.crane.3bap.s"));
			this.fds3Bape = Float.parseFloat(properties.getProperty("datasource.crane.3bap.e"));
		    
		    //LF
			this.fds1LFs = Float.parseFloat(properties.getProperty("datasource.crane.lf.s"));
			this.fds1LFe = Float.parseFloat(properties.getProperty("datasource.crane.lf.e"));
		    
		    //R대차, 회송대차
			this.fds1RCs = Float.parseFloat(properties.getProperty("datasource.crane.rc.s"));
			this.fds1RCe = Float.parseFloat(properties.getProperty("datasource.crane.rc.e"));
		    
		    //M/C
			this.fds1MCs = Float.parseFloat(properties.getProperty("datasource.crane.1mc.s"));
			this.fds1MCe = Float.parseFloat(properties.getProperty("datasource.crane.1mc.e"));
			this.fds2MCs = Float.parseFloat(properties.getProperty("datasource.crane.2mc.s"));
			this.fds2MCe = Float.parseFloat(properties.getProperty("datasource.crane.2mc.e"));
			this.fds3MCs = Float.parseFloat(properties.getProperty("datasource.crane.3mc.s"));
			this.fds3MCe = Float.parseFloat(properties.getProperty("datasource.crane.3mc.e"));
			this.fds4MCs = Float.parseFloat(properties.getProperty("datasource.crane.4mc.s"));
			this.fds4MCe = Float.parseFloat(properties.getProperty("datasource.crane.4mc.e"));
			
			this.dsCraneStateCdF = properties.getProperty("datasource.crane.state.cd.f");
			this.dsCraneStateCdE = properties.getProperty("datasource.crane.state.cd.e");
			this.dsCraneStateCdN = properties.getProperty("datasource.crane.state.cd.n");
			
			this.fdsCraneDistanceMax = Float.parseFloat(properties.getProperty("datasource.crane.distance.max"));
            this.fdsCraneDistanceMin = Float.parseFloat(properties.getProperty("datasource.crane.distance.min"));
            this.fdsCraneDistanceVirtualMax = Float.parseFloat(properties.getProperty("datasource.crane.distance.virtual.max"));
            this.fdsCraneDistanceVirtualMin = Float.parseFloat(properties.getProperty("datasource.crane.distance.virtual.min"));
            this.fdsCraneDistanceDefault = Float.parseFloat(properties.getProperty("datasource.crane.distance.defalut"));
            this.dsCraneConvertDistanceDefault = properties.getProperty("datasource.crane.convert.distance.defalut");
            
            this.dsTmHoistHeight = properties.getProperty("datasource.TM.hoist.height");
            this.dsLdHoistHeight = properties.getProperty("datasource.LD.hoist.height");
            this.dsHmHoistHeight = properties.getProperty("datasource.HM.hoist.height");
            this.dsTmSubhoistHeight = properties.getProperty("datasource.TM.subhoist.height");
            this.dsLdSubhoistHeight = properties.getProperty("datasource.LD.subhoist.height");
            this.dsHmSubhoistHeight = properties.getProperty("datasource.HM.subhoist.height");
            
            this.fDsTmHoistHeight = Float.parseFloat(this.dsTmHoistHeight);
            this.fDsLdHoistHeight = Float.parseFloat(this.dsLdHoistHeight);
            this.fDsHmHoistHeight = Float.parseFloat(this.dsHmHoistHeight);
            this.fDsTmSubhoistHeight = Float.parseFloat(this.dsTmSubhoistHeight);
            this.fDsLdSubhoistHeight = Float.parseFloat(this.dsLdSubhoistHeight);
            this.fDsHmSubhoistHeight = Float.parseFloat(this.dsHmSubhoistHeight);
            
            this.fDsTltcH5_1_s = Float.parseFloat(properties.getProperty("datasource.tltc.h5.1.s"));
            this.fDsTltcH5_1_e = Float.parseFloat(properties.getProperty("datasource.tltc.h5.1.e"));
            this.fDsTltcH5_2_s = Float.parseFloat(properties.getProperty("datasource.tltc.h5.2.s"));
            this.fDsTltcH5_2_e = Float.parseFloat(properties.getProperty("datasource.tltc.h5.2.e"));
            this.fDsTltcH6_1_s = Float.parseFloat(properties.getProperty("datasource.tltc.h6.1.s"));
            this.fDsTltcH6_1_e = Float.parseFloat(properties.getProperty("datasource.tltc.h6.1.e"));
            this.fDsTltcH6_2_s = Float.parseFloat(properties.getProperty("datasource.tltc.h6.2.s"));
            this.fDsTltcH6_2_e = Float.parseFloat(properties.getProperty("datasource.tltc.h6.2.e"));

            this.dsIFMDataCount = Integer.parseInt(properties.getProperty("datasource.ifm.data.count"));
            
            this.fDsConvertMin = Float.parseFloat(properties.getProperty("datasource.convert.s"));
            this.fDsConvertMax = Float.parseFloat(properties.getProperty("datasource.convert.e"));
            
            // 용선, L/D, T/M 크레인 x 좌표값 조정값
        	this.ds1TmXTuning 	= properties.getProperty("datasource.crane.1TM.x.tuning");
        	this.f1TmXTuning 	= (this.ds1TmXTuning!= null && !"".equals(this.ds1TmXTuning)) ? Float.parseFloat(this.ds1TmXTuning) : 0;

        	this.ds2TmXTuning 	= properties.getProperty("datasource.crane.2TM.x.tuning");
        	this.f2TmXTuning	= (this.ds2TmXTuning!= null && !"".equals(this.ds2TmXTuning)) ? Float.parseFloat(this.ds2TmXTuning) : 0;
            
        	this.ds3TmXTuning 	= properties.getProperty("datasource.crane.3TM.x.tuning");
        	this.f3TmXTuning	= (this.ds3TmXTuning!= null && !"".equals(this.ds3TmXTuning)) ? Float.parseFloat(this.ds3TmXTuning) : 0;
        	
        	this.ds4TmXTuning 	= properties.getProperty("datasource.crane.4TM.x.tuning");
        	this.f4TmXTuning	= (this.ds4TmXTuning!= null && !"".equals(this.ds4TmXTuning)) ? Float.parseFloat(this.ds4TmXTuning) : 0;
            
        	this.ds1LdXTuning 	= properties.getProperty("datasource.crane.1LD.x.tuning");
        	this.f1LdXTuning	= (this.ds1LdXTuning!= null && !"".equals(this.ds1LdXTuning)) ? Float.parseFloat(this.ds1LdXTuning) : 0;
            
        	this.ds2LdXTuning 	= properties.getProperty("datasource.crane.2LD.x.tuning");
        	this.f2LdXTuning	= (this.ds2LdXTuning!= null && !"".equals(this.ds2LdXTuning)) ? Float.parseFloat(this.ds2LdXTuning) : 0;
        	
        	this.ds1HmXTuning 	= properties.getProperty("datasource.crane.1HM.x.tuning");
        	this.f1HmXTuning	= (this.ds1HmXTuning!= null && !"".equals(this.ds1HmXTuning)) ? Float.parseFloat(this.ds1HmXTuning) : 0;
        	
        	this.ds2HmXTuning 	= properties.getProperty("datasource.crane.2HM.x.tuning");
        	this.f2HmXTuning	= (this.ds2HmXTuning!= null && !"".equals(this.ds2HmXTuning)) ? Float.parseFloat(this.ds2HmXTuning) : 0;
        	
        	this.ds1ScXTuning 	= properties.getProperty("datasource.crane.1SC.x.tuning");
        	this.f1ScXTuning	= (this.ds1ScXTuning!= null && !"".equals(this.ds1ScXTuning)) ? Float.parseFloat(this.ds1ScXTuning) : 0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public IFMUwbRtlsIClient(String myFile, int myInterval) 
	{
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.curYYYY = this.sdfYYYY.format(this.timestamp);
		this.curMM = this.sdfMM.format(this.timestamp);
		this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
		
		myFile = myFile + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +".txt";
		
		this.crunchifyFile = new File(myFile);
		this.crunchifyRunEveryNSeconds = myInterval;
	}

	private void printLine(int viewyn, String message) 
	{
		if( viewyn == 1 )
		{
			System.out.println(message);
		}
	}

	public void stopRunning() {
		shouldIRun = false;
	}

	public void run() 
	{	
    	while(shouldIRun)
    	{
    		Connection con = null;
        	PreparedStatement ps = null;
        	ResultSet rs = null;
        	
        	String myFile = "";
			String oldMyFile = "";
			
			this.timestamp = new Timestamp(System.currentTimeMillis());
			this.curYYYY = this.sdfYYYY.format(this.timestamp);
			this.curMM = this.sdfMM.format(this.timestamp);
			this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
			
			myFile = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +".txt";
			oldMyFile = myFile;
        	
        	int isIFMUse = 1;

			try 
			{
				float ftmpdistancecal = 0;
				float ftmphoistheight = 0;
				float ftmpsubhoistheight = 0;
				String strIFMDataTmp = "";
				
				float ftmp_ex_weight = 0;
				float ftmp_ex_spare2 = 0;				
	
				HashMap <String, String> hmIFMData = new HashMap<String, String>();
				Iterator<String> itrIFMDataKeys = hmIFMData.keySet().iterator();
				
				HashMap <String, String> hmTmpIFMData = new HashMap<String, String>();
				
				Class.forName(this.jdbc_driver);
	    		con = DriverManager.getConnection(this.jdbc_url, this.db_id, this.db_pw);
				
	    		if( this.isIFMUse == 1 )
	    		{
					this.socket = new Socket(this.Poscoict_Svr_Ip, this.Poscoict_Svr_Port);
	    		}
				
				while (shouldIRun) 
				{	
					Thread.sleep(crunchifyRunEveryNSeconds);
				
					this.timestamp = new Timestamp(System.currentTimeMillis());
					this.curYYYY = this.sdfYYYY.format(this.timestamp);
					this.curMM = this.sdfMM.format(this.timestamp);
					this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
					
					myFile = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +".txt";
					this.crunchifyFile = new File(myFile);
					
					long fileLength = this.crunchifyFile.length();
					
					if( !oldMyFile.equals(myFile) && fileLength > 200 )
					{
						this.lastKnownPosition = fileLength - 200;
					}
					
					if (fileLength > this.lastKnownPosition) 
					{
						oldMyFile = myFile;
						
						//this.printLine(this.isViewYn, "myFile [fileLength : "+ fileLength +"] >>> [lastKnownPosition : "+ lastKnownPosition +"]");
						
						if( this.LoopCnt_01 == 0 )
						{
							this.sbRet = new StringBuffer();
							this.lastKnownPosition = fileLength -1;
						}
	
						// Reading and writing file
						RandomAccessFile readWriteFileAccess = new RandomAccessFile(this.crunchifyFile, "r");
						readWriteFileAccess.seek(this.lastKnownPosition);
						String sDataLine = null;
						
						while ((sDataLine = readWriteFileAccess.readLine()) != null) 
						{
							if( sDataLine != null && !"".equals(sDataLine) )
							{
								if( dataCounter != 0 && ( dataCounter % (setDataCount+1) ) > 0 )
								{
									//this.printLine( "["+ dataCounter +"]" + sDataLine );
									
									this.arryTmp = sDataLine.split(",");
									this.arrLen = this.arryTmp.length;
									
									if( this.arrLen == 17 )
									{
											this.tmp_tagtype = this.arryTmp[0].trim();
											this.tmp_tagid = this.arryTmp[1].trim();
											this.tmp_craneline = this.arryTmp[2].trim();
											this.tmp_cranecode = this.arryTmp[3].trim();
											this.tmp_posx = this.arryTmp[4].trim();
											this.tmp_posy = this.arryTmp[5].trim();
											this.tmp_posz = this.arryTmp[6].trim();
											this.tmp_sleepmode = this.arryTmp[7].trim();
											
											///////////////////////////
											this.tmp_old_spare2 = "";
											this.tmp_old_dstlladnum = "";
											this.tmp_old_eqprfidtagid1 = "";
											this.tmp_old_bindingflag1 = "";
											///////////////////////////
			
											if( this.tmp_tagtype != null )
											{
												if( "1".equals(this.tmp_tagtype) )
												{
													// UWB Crane Tag Data Start
													///////////////////////////
													
													this.tmp_weight = this.arryTmp[8].trim();
													this.tmp_distance = this.arryTmp[9].trim();
													this.tmp_hoistheight = this.arryTmp[10].trim();
													this.tmp_spare1 = this.arryTmp[11].trim();
													this.tmp_spare2 = this.arryTmp[12].trim();
													this.tmp_rweight = this.arryTmp[13].trim();
													this.tmp_rmove = this.arryTmp[14].trim();
													
													ftmphoistheight = 0;
													ftmpsubhoistheight = 0;
	
													
													///////////////////////////////////////////////////////
													/// 칭량 시스템 예상 범위를 벗어나는 데이터가 왔을때 처리하는 로직
													/// 데이터 무시하고, 년월일시분초_ext_err_log.txt 파일에 저장
													///////////////////////////////////////////////////////
													try
													{
														//숫자가 아닌 문자, 특수문자 등이 들어와 오류가 발생되는 상황이 문제가 되지 않도록 처리
														ftmp_ex_weight = Float.parseFloat(this.tmp_weight);
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
														ftmp_ex_spare2 = Float.parseFloat(this.tmp_spare2);
													}
													catch(Exception e)
													{
														//조건식에 맞지 않는 임의의 값 지정
														ftmp_ex_spare2 = 10;
														
														e.printStackTrace();
													}
													
													if( ftmp_ex_weight > 740 || ( ftmp_ex_spare2 < 0 || ftmp_ex_spare2 > 3 ) )
													{
														if( ftmp_ex_weight > 740 )
														{
															this.tmp_weight = "";
														}
														
														if( ftmp_ex_spare2 < 0 || ftmp_ex_spare2 > 3 )
														{
															this.tmp_spare2 = "0";
														}
													}
													///////////////////////////////////////////////////////
													///////////////////////////////////////////////////////
													
													
													if( "H".equals(this.tmp_craneline) || this.tmp_distance == null || "-99".equals(this.tmp_distance) )
													{
														this.tmp_distance = String.format( "%.2f",  this.fdsCraneDistanceDefault );
													}
													else
													{												
														this.tmp_distance = calcDistanceVal( this.fdsCraneDistanceMin, this.fdsCraneDistanceMax, this.fdsCraneDistanceVirtualMin, this.fdsCraneDistanceVirtualMax, Float.parseFloat( this.tmp_distance ) );
													}
	
													if( !"-99".equals(this.tmp_hoistheight) )
													{
														ftmphoistheight = Float.parseFloat(this.tmp_hoistheight);
	
														if( "L".equals(tmp_craneline) )
														{
															if( ftmphoistheight > this.fDsLdHoistHeight )
															{ 
																ftmphoistheight = this.fDsLdHoistHeight;
															}
															else if( ftmphoistheight < 0 )
															{ 
																ftmphoistheight = 0;
															}
															
															this.tmp_hoistheight = String.format( "%.2f",  this.fDsLdHoistHeight - ftmphoistheight );
														}
														else if( "T".equals(tmp_craneline) )
														{
															if( ftmphoistheight > this.fDsTmHoistHeight )
															{ 
																ftmphoistheight = this.fDsTmHoistHeight;
															}
															else if( ftmphoistheight < 0 )
															{ 
																ftmphoistheight = 0;
															}
															
															this.tmp_hoistheight = String.format( "%.2f",  this.fDsTmHoistHeight - ftmphoistheight );														
														}
														else if( "H".equals(tmp_craneline) )
														{
															if( ftmphoistheight > this.fDsHmHoistHeight )
															{ 
																ftmphoistheight = this.fDsHmHoistHeight;
															}
															else if( ftmphoistheight < 0 )
															{ 
																ftmphoistheight = 0;
															}
															
															this.tmp_hoistheight = String.format( "%.2f",  this.fDsHmHoistHeight - ftmphoistheight );
														}
														else
														{
															this.tmp_hoistheight = "0";
														}
													}
													
													if( !"-99".equals(this.tmp_spare1) )
													{
														ftmpsubhoistheight = Float.parseFloat(this.tmp_spare1);
														
														if( "L".equals(tmp_craneline) )
														{
															if( ftmpsubhoistheight > this.fDsLdSubhoistHeight )
															{ 
																ftmpsubhoistheight = this.fDsLdSubhoistHeight;
															}
															else if( ftmpsubhoistheight < 0 )
															{ 
																ftmpsubhoistheight = 0;
															}
															
															this.tmp_subhoistheight = String.format( "%.2f",  this.fDsLdSubhoistHeight - ftmpsubhoistheight );
														}
														else if( "T".equals(tmp_craneline) )
														{
															if( ftmpsubhoistheight > this.fDsTmSubhoistHeight )
															{ 
																ftmpsubhoistheight = this.fDsTmSubhoistHeight;
															}
															else if( ftmpsubhoistheight < 0 )
															{ 
																ftmpsubhoistheight = 0;
															}
															
															this.tmp_subhoistheight = String.format( "%.2f",  this.fDsTmSubhoistHeight - ftmpsubhoistheight );														
														}
														else if( "H".equals(tmp_craneline) )
														{
															if( ftmpsubhoistheight > this.fDsHmSubhoistHeight )
															{ 
																ftmpsubhoistheight = this.fDsHmSubhoistHeight;
															}
															else if( ftmpsubhoistheight < 0 )
															{ 
																ftmpsubhoistheight = 0;
															}
	
															this.tmp_subhoistheight = String.format( "%.2f",  this.fDsHmSubhoistHeight - ftmpsubhoistheight );														
														}
														else
														{
															this.tmp_subhoistheight = "0";
														}
													}
	
													if( "-99".equals(this.tmp_weight) ) 		this.tmp_weight = "0";
													if( "-99".equals(this.tmp_distance) ) 		this.tmp_distance = "0";
													if( "-99".equals(this.tmp_hoistheight) ) 	this.tmp_hoistheight = "0";
													if( "-99".equals(this.tmp_spare2) ) 		this.tmp_spare2 = "1";
													if( "-99".equals(this.tmp_spare1) )
													{
														this.tmp_spare1 = "0";
														this.tmp_subhoistheight = "0";
													}
													
													if( hmTmpIFMData.size() > 0 )
													{
														if( hmTmpIFMData.get(this.tmp_tagid) != null )
														{
															String [] arryOldData = (hmTmpIFMData.get(this.tmp_tagid)+" ").split(",");
															
															this.tmp_old_spare2 = arryOldData[8];
															this.tmp_old_dstlladnum = arryOldData[9];
															this.tmp_old_eqprfidtagid1 = arryOldData[10];
															this.tmp_old_bindingflag1 = arryOldData[11];
															
															if( arryOldData.length == 14 )
															{
																this.tmp_old_mtlno = arryOldData[13].trim();
															}
															else
															{
																this.tmp_old_mtlno = "";
															}
														}
													}
													
													if( this.tmp_spare2 != null && !"0".equals(this.tmp_spare2) )
													{
														if( "1".equals(this.tmp_spare2) )		this.tmp_spare2 = "N";
														else if( "2".equals(this.tmp_spare2) )	this.tmp_spare2 = "E";
														else if( "3".equals(this.tmp_spare2) )	this.tmp_spare2 = "F";
														else if( "0".equals(this.tmp_spare2) )	this.tmp_spare2 = "N";
													}
													else
													{
														this.tmp_spare2 = this.tmp_old_spare2;
														
														if( "".equals(this.tmp_spare2) )	this.tmp_spare2 = "N";
													}
													
													
													if( "T".equals(this.tmp_craneline) )
													{
														HashMap <String, String> hmCrLdMappingInfo = null;
														float fTmpCranePosX = Float.parseFloat(this.tmp_posx);

                                                        //uwb crane tag 설치가 운전석 또는 운전석 반대편에 설치가 되어 있다.
                                                        if( "1T".equals(this.tmp_cranecode) )	fTmpCranePosX = fTmpCranePosX - 5.00f; 
                                                        else if( "2T".equals(this.tmp_cranecode) )	fTmpCranePosX = fTmpCranePosX + 5.00f;
                                                        else if( "4T".equals(this.tmp_cranecode) )	fTmpCranePosX = fTmpCranePosX - 5.00f;
														
														if( this.tmp_spare2 != null && this.tmp_old_spare2 != null && !(this.tmp_old_spare2).equals(this.tmp_spare2)  )
														{
															if( (this.dsCraneStateCdF).equals(this.tmp_spare2) )
															{
																if( 
																	this.tmp_distance == null || "".equals(this.tmp_distance)  
																	|| "-".equals(this.tmp_distance) || "-99".equals(this.tmp_distance) 
																)
																{
																	this.setIFMLogFileWirte(this.isLogYn, 1, "T/M ["+ this.tmp_tagid +"]["+ this.tmp_craneline +"]["+ this.tmp_cranecode +"][x : "+ fTmpCranePosX +"][y : "+this.fdsCraneDistanceMax+"][ld_state(old) : "+ this.tmp_old_spare2 +"][ld_state : "+ this.tmp_spare2 +"]");
																	hmCrLdMappingInfo = this.getCrLdMappingProc_F( con, ps, rs, fTmpCranePosX, this.fdsCraneDistanceMax );
																}
																else
																{
																	this.setIFMLogFileWirte(this.isLogYn, 1, "T/M ["+ this.tmp_tagid +"]["+ this.dsCraneStateCdF +"]["+ this.tmp_cranecode +"][x : "+ fTmpCranePosX +"][y : "+this.tmp_distance+"][ld_state(old) : "+ this.tmp_old_spare2 +"][ld_state : "+ this.tmp_spare2 +"]");
																	hmCrLdMappingInfo = this.getCrLdMappingProc_F( con, ps, rs, fTmpCranePosX, Float.parseFloat(this.tmp_distance) );
																}
																
																if( hmCrLdMappingInfo.size() > 0 )
																{
																	this.tmp_dstlladnum = this.isNullCheck( hmCrLdMappingInfo.get("tmp_dstlladnum"), "" );
																	this.tmp_eqprfidtagid1 = this.isNullCheck( hmCrLdMappingInfo.get("tmp_eqprfidtagid1"), "" );
																	this.tmp_bindingflag1 = this.isNullCheck( hmCrLdMappingInfo.get("tmp_bindingflag1"), "" );
																	this.tmp_mtlno = this.isNullCheck( hmCrLdMappingInfo.get("tmp_mtlno"), "" );
																}
																else
																{
																	this.extend_1_DataInit();
																	
																	this.setIFMLogFileWirte(this.isLogYn, 1, "T/M_F HashMap No Data.");
																}

																this.setIFMLogFileWirte(this.isLogYn, 1, "T/M_F ["+ this.tmp_dstlladnum +"]["+ this.tmp_eqprfidtagid1 +"]["+ this.tmp_bindingflag1 +"]["+ this.tmp_mtlno +"]");
															}
															else if( (this.dsCraneStateCdE).equals(this.tmp_spare2) )
															{
																if( 
																		this.tmp_distance == null || "".equals(this.tmp_distance)  
																	|| "-".equals(this.tmp_distance) || "-99".equals(this.tmp_distance) 
																)
																{
																	this.setIFMLogFileWirte(this.isLogYn, 1, "T/M ["+ this.tmp_tagid +"]["+ this.dsCraneStateCdE +"]["+ this.tmp_cranecode +"][x : "+ fTmpCranePosX +"][y : "+ this.fdsCraneDistanceMax +"][ld_state(old) : "+ this.tmp_old_spare2 +"][ld_state : "+ this.tmp_spare2 +"]");
																	hmCrLdMappingInfo = this.getCrLdMappingProc_E( con, ps, rs, fTmpCranePosX, this.fdsCraneDistanceMax );
																}
																else
																{
																	this.setIFMLogFileWirte(this.isLogYn, 1, "T/M ["+ this.tmp_tagid +"]["+ this.dsCraneStateCdE +"]["+ this.tmp_cranecode +"][x : "+ fTmpCranePosX +"][y : "+ this.tmp_distance +"][ld_state(old) : "+ this.tmp_old_spare2 +"][ld_state : "+ this.tmp_spare2 +"]");
																	hmCrLdMappingInfo = this.getCrLdMappingProc_E( con, ps, rs, fTmpCranePosX, Float.parseFloat(this.tmp_distance) );
																}
																
																if( hmCrLdMappingInfo.size() > 0 )
																{
																	this.tmp_dstlladnum = this.isNullCheck( hmCrLdMappingInfo.get("tmp_dstlladnum") , "" );
																	this.tmp_eqprfidtagid1 = this.isNullCheck( hmCrLdMappingInfo.get("tmp_eqprfidtagid1") , "" );
																	this.tmp_bindingflag1 = this.isNullCheck( hmCrLdMappingInfo.get("tmp_bindingflag1") , "" );
																	this.tmp_mtlno = this.isNullCheck( hmCrLdMappingInfo.get("tmp_mtlno") , "" );
																}
																else
																{
																	this.extend_1_DataInit();
																	
																	this.setIFMLogFileWirte(this.isLogYn, 1, "T/M_E HashMap No Data.");
																}
																
																this.setIFMLogFileWirte(this.isLogYn, 1, "T/M_E ["+ this.tmp_dstlladnum +"]["+ this.tmp_eqprfidtagid1 +"]["+ this.tmp_bindingflag1 +"]["+ this.tmp_mtlno +"]");															
															}
															else if( (this.dsCraneStateCdN).equals(this.tmp_spare2) )
															{
																this.extend_1_DataInit();
															}
															else
															{
																this.extend_1_DataInit();
															}
														}
														else
														{
															this.tmp_spare2 = this.tmp_old_spare2;
															this.tmp_dstlladnum = this.tmp_old_dstlladnum;
															this.tmp_eqprfidtagid1 = this.tmp_old_eqprfidtagid1;
															this.tmp_bindingflag1 = this.tmp_old_bindingflag1;
															this.tmp_mtlno = this.tmp_old_mtlno;
														}
													}
													else if( "L".equals(this.tmp_craneline) )
													{
														HashMap <String, String> hmCrLdMappingInfo = null;
														float fTmpCranePosX = Float.parseFloat(this.tmp_posx);
														
														//uwb crane tag 설치가 운전석 또는 운전석 반대편에 설치가 되어 있다.
                                                        if( "1L".equals(this.tmp_cranecode) )	fTmpCranePosX = fTmpCranePosX - 5.00f; 
                                                        else if( "2L".equals(this.tmp_cranecode) )	fTmpCranePosX = fTmpCranePosX + 5.00f;
                                                        else if( "3T".equals(this.tmp_cranecode) )	fTmpCranePosX = fTmpCranePosX - 5.00f;
                                                        
														if( this.tmp_spare2 != null && this.tmp_old_spare2 != null && !(this.tmp_old_spare2).equals(this.tmp_spare2)  )
														{
															if( (this.dsCraneStateCdF).equals(this.tmp_spare2) )
															{
																if( 
																	this.tmp_distance == null || "".equals(this.tmp_distance)  
																	|| "-".equals(this.tmp_distance) || "-99".equals(this.tmp_distance) 
																)
																{
																	this.setIFMLogFileWirte(this.isLogYn, 1, "L/D ["+ this.tmp_tagid +"]["+ this.tmp_craneline +"]["+ this.tmp_cranecode +"][x : "+ fTmpCranePosX +"][y : "+this.fdsCraneDistanceMax+"][ld_state(old) : "+ this.tmp_old_spare2 +"][ld_state : "+ this.tmp_spare2 +"]");
																	hmCrLdMappingInfo = this.getCrLdMappingProc_F( con, ps, rs, fTmpCranePosX, this.fdsCraneDistanceMax );
																}
																else
																{
																	this.setIFMLogFileWirte(this.isLogYn, 1, "L/D ["+ this.tmp_tagid +"]["+ this.dsCraneStateCdF +"]["+ this.tmp_cranecode +"][x : "+ fTmpCranePosX +"][y : "+this.tmp_distance+"][ld_state(old) : "+ this.tmp_old_spare2 +"][ld_state : "+ this.tmp_spare2 +"]");
																	hmCrLdMappingInfo = this.getCrLdMappingProc_F( con, ps, rs, fTmpCranePosX, Float.parseFloat(this.tmp_distance) );
																}
																
																if( hmCrLdMappingInfo.size() > 0 )
																{
																	this.tmp_dstlladnum =  this.isNullCheck( hmCrLdMappingInfo.get("tmp_dstlladnum"), "" );
																	this.tmp_eqprfidtagid1 = this.isNullCheck( hmCrLdMappingInfo.get("tmp_eqprfidtagid1"), "" );
																	this.tmp_bindingflag1 = this.isNullCheck( hmCrLdMappingInfo.get("tmp_bindingflag1"), "" );
																	this.tmp_mtlno = this.isNullCheck( hmCrLdMappingInfo.get("tmp_mtlno"), "" );
																}
																else
																{
																	this.extend_1_DataInit();
																	
																	this.setIFMLogFileWirte(this.isLogYn, 1, "L/D_F HashMap No Data.");
																}

																this.setIFMLogFileWirte(this.isLogYn, 1, "L/D_F ["+ this.tmp_dstlladnum +"]["+ this.tmp_eqprfidtagid1 +"]["+ this.tmp_bindingflag1 +"]["+ this.tmp_mtlno +"]");
															}
															else if( (this.dsCraneStateCdE).equals(this.tmp_spare2) )
															{
																
																// L/D  라인 크레인에서 공 Ladle를 들어 올릴때 처리하는 로직이 필요
																
															}
															else if( (this.dsCraneStateCdN).equals(this.tmp_spare2) )
															{
																this.extend_1_DataInit();															
															}
															else
															{
																this.extend_1_DataInit();
															}
														}
														else
														{
															this.tmp_spare2 = this.tmp_old_spare2;
															this.tmp_dstlladnum = this.tmp_old_dstlladnum;
															this.tmp_eqprfidtagid1 = this.tmp_old_eqprfidtagid1;
															this.tmp_bindingflag1 = this.tmp_old_bindingflag1;
															this.tmp_mtlno = this.tmp_old_mtlno;
														}
													}
													else if( "H".equals(this.tmp_craneline) && ( "1H".equals( this.tmp_cranecode ) || "2H".equals( this.tmp_cranecode ) ) )
													{
														HashMap <String, String> hmCrLdMappingInfo = null;
														float fTmpCranePosX = Float.parseFloat(this.tmp_posx);
														
														if( this.tmp_spare2 != null && this.tmp_old_spare2 != null && !(this.tmp_old_spare2).equals(this.tmp_spare2)  )
														{
															if( (this.dsCraneStateCdF).equals(this.tmp_spare2) )
															{
																this.setIFMLogFileWirte(this.isLogYn, 1, "H/C ["+ this.tmp_tagid +"]["+ this.tmp_craneline +"]["+ this.tmp_cranecode +"][x : "+ fTmpCranePosX +"][y : "+this.tmp_distance+"][ld_state(old) : "+ this.tmp_old_spare2 +"][ld_state : "+ this.tmp_spare2 +"]");

																hmCrLdMappingInfo = this.getConvertCrLdMappingProc_F( con, ps, rs, fTmpCranePosX, Float.parseFloat(this.tmp_distance) );
																
																if( hmCrLdMappingInfo.size() > 0 )
																{
																	this.tmp_dstlladnum =  this.isNullCheck( hmCrLdMappingInfo.get("tmp_dstlladnum"), "" );
																	this.tmp_eqprfidtagid1 = "";
																	this.tmp_bindingflag1 = "";
																	this.tmp_mtlno = this.isNullCheck( hmCrLdMappingInfo.get("tmp_mtlno"), "" );
																}
																else
																{
																	this.extend_1_DataInit();
																	this.setIFMLogFileWirte(this.isLogYn, 1, "H/C_F HashMap No Data.");
																}

																this.setIFMLogFileWirte(this.isLogYn, 1, "H/C_F ["+ this.tmp_dstlladnum +"]["+ this.tmp_eqprfidtagid1 +"]["+ this.tmp_bindingflag1 +"]["+ this.tmp_mtlno +"]");
															}
															else if( (this.dsCraneStateCdE).equals(this.tmp_spare2) )
															{
																// 용선 라인 크레인에서 재료를 전로에 넣고나면 영 -> 공 으로 Ladle 상태가 변할때 처리 로직
																this.tmp_dstlladnum = this.tmp_old_dstlladnum;
																
															}
															else if( (this.dsCraneStateCdN).equals(this.tmp_spare2) )
															{
																this.extend_1_DataInit();
															}
															else
															{
																this.extend_1_DataInit();
															}
														}
														else
														{
															this.tmp_spare2 = this.tmp_old_spare2;
															this.tmp_dstlladnum = this.tmp_old_dstlladnum;
															this.tmp_eqprfidtagid1 = this.tmp_old_eqprfidtagid1;
															this.tmp_bindingflag1 = this.tmp_old_bindingflag1;
															this.tmp_mtlno = this.tmp_old_mtlno;
														}

													}
													
												// UWB Crane Tag Data End
												/////////////////////////
												}
												else if( "2".equals(this.tmp_tagtype) )
												{
													// UWB Ladle Tag Data Start
													/////////////////////////
													this.tmp_weight = "";
													this.tmp_distance = this.tmp_posy;
													this.tmp_hoistheight = this.tmp_posz;
													this.tmp_subhoistheight = "";
													this.tmp_spare1 = "";
													this.tmp_spare2 = "";
													
													this.tmp_dstlladnum = this.tmp_cranecode;
													this.tmp_eqprfidtagid1 = "";
													this.tmp_bindingflag1 = "";
													this.tmp_mtlno = "";
													
													this.tmp_cranecode = "";
													//////////////////////////
													// UWB Ladle Tag Data End
												}
												else if( "3".equals(this.tmp_tagtype) )
												{
													// UWB Human Tag Data Start
													///////////////////////////
													this.tmp_weight = "";
													this.tmp_distance = this.tmp_posy;
													this.tmp_hoistheight = this.tmp_posz;
													this.tmp_subhoistheight = "";
													this.tmp_spare1 = "";
													this.tmp_spare2 = "";
													
													this.extend_1_DataInit();
													///////////////////////////
													// UWB Human Tag Data End
												}
												else
												{
													// UWB ETC Tag Data Start
													//////////////////////////
													this.tmp_weight = "";
													this.tmp_distance = "";
													this.tmp_hoistheight = "";
													this.tmp_subhoistheight = "";
													this.tmp_spare1 = "";
													this.tmp_spare2 = "";
													
													this.extend_1_DataInit();
													//////////////////////////
													// UWB ETC Tag Data End
												}
											}
											else
											{
												//Not Exist tagtype data Start
												//////////////////////////////
												this.tmp_weight = "";
												this.tmp_distance = "";
												this.tmp_hoistheight = "";
												this.tmp_subhoistheight = "";
												this.tmp_spare1 = "";
												this.tmp_spare2 = "";
												
												this.extend_1_DataInit();
												/////////////////////////////
												//Not Exist tagtype data End
											}
	
											if( "-".equals(this.tmp_craneline) ) this.tmp_craneline = "";
											if( "-".equals(this.tmp_cranecode) ) this.tmp_cranecode = "";
											
											this.tmp_ymd = this.arryTmp[15].trim();
											this.tmp_hms = this.arryTmp[16].trim();
											
											if( "1".equals(this.tmp_tagtype) && this.tmp_dstlladnum != null && !"".equals(this.tmp_dstlladnum) 
													&& !"1H".equals( this.tmp_cranecode ) && !"2H".equals( this.tmp_cranecode ) && !"1S".equals( this.tmp_cranecode )  )
											{
												this.tmp_eqprfidtagid1 = this.findLadleTagid( this.tmp_dstlladnum );
												
												if( this.tmp_eqprfidtagid1 != null && !"".equals(this.tmp_eqprfidtagid1) )
												{
													this.tmp_bindingflag1 = "2";
												}
											}
											
											if( "1".equals( this.tmp_tagtype ) )
											{
												if( "1H".equals( this.tmp_cranecode ) || "2H".equals( this.tmp_cranecode ) || "1S".equals( this.tmp_cranecode ) )
												{
													float fTmpCranePosX = Float.parseFloat(this.tmp_posx);
													
													float fTmpHoistHeight = Float.parseFloat(this.tmp_hoistheight);
													float fTmpSubHoistHeight = Float.parseFloat(this.tmp_subhoistheight);
													
													if( this.fDsConvertMin <= fTmpCranePosX && this.fDsConvertMax >=  fTmpCranePosX )
													{
														// 전로 라인은 전로 앞 부분으로 움직여 보이도록 처리 하는 값
														this.tmp_distance = this.dsCraneConvertDistanceDefault;
														
														//용선라인의 전로 부분에 크레인 주권, 보권이 땅에 닿아 조절을 한것임
														//this.tmp_hoistheight = String.format( "%.2f",  fTmpHoistHeight - 4 );
														if( fTmpHoistHeight <= 5.9f )
														{
															fTmpHoistHeight = 6.00f;
														}
														this.tmp_hoistheight = String.format( "%.2f",  fTmpHoistHeight);

														if( fTmpSubHoistHeight >= 10.0f )		this.tmp_subhoistheight = "10.00";
														else if( fTmpSubHoistHeight < 10.0f && fTmpSubHoistHeight >= 9.0f )		this.tmp_subhoistheight = "9.70";
														else if( fTmpSubHoistHeight < 9.0f && fTmpSubHoistHeight >= 8.0f )		this.tmp_subhoistheight = "9.40";
														else if( fTmpSubHoistHeight < 8.0f && fTmpSubHoistHeight >= 7.0f )		this.tmp_subhoistheight = "9.10";
														else if( fTmpSubHoistHeight < 7.0f && fTmpSubHoistHeight >= 6.0f )		this.tmp_subhoistheight = "8.80";
														else if( fTmpSubHoistHeight < 6.0f && fTmpSubHoistHeight >= 5.0f )		this.tmp_subhoistheight = "8.50";
														else if( fTmpSubHoistHeight < 5.0f && fTmpSubHoistHeight >= 4.0f )		this.tmp_subhoistheight = "8.20";
														else if( fTmpSubHoistHeight < 4.0f && fTmpSubHoistHeight >= 3.0f )		this.tmp_subhoistheight = "7.90";
														else if( fTmpSubHoistHeight < 3.0f && fTmpSubHoistHeight >= 2.0f )		this.tmp_subhoistheight = "7.60";
														else if( fTmpSubHoistHeight < 2.0f && fTmpSubHoistHeight >= 1.0f )		this.tmp_subhoistheight = "7.30";
														else if( fTmpSubHoistHeight < 1.0f && fTmpSubHoistHeight >= 0.0f )		this.tmp_subhoistheight = "7.00";
														else if( fTmpSubHoistHeight < 0.0f )	this.tmp_subhoistheight = "6.70";

														//this.tmp_subhoistheight = String.format( "%.2f",  fTmpSubHoistHeight + 4 );
													}
													else
													{
														// 1 용선, 2 용선, 고철크레인은 Radar이 없음. 그래서 중간값 처리													
														this.tmp_distance = String.format( "%.2f",  this.fdsCraneDistanceDefault );
														
														if( fTmpHoistHeight <= 5.9f )
														{
															this.tmp_hoistheight = "6.00";
														}
														
														if( fTmpHoistHeight >= 18.0f )
														{
															this.tmp_hoistheight = "18.00";
														}
													}
													
													if( "1H".equals( this.tmp_cranecode ) )
													{
														this.tmp_posx = String.format( "%.2f",  ( fTmpCranePosX + this.f1HmXTuning ) );
													}
													else if( "2H".equals( this.tmp_cranecode ) )
													{
														this.tmp_posx = String.format( "%.2f",  ( fTmpCranePosX + this.f2HmXTuning ) );
													}
													else if( "1S".equals( this.tmp_cranecode ) )
													{
														this.tmp_posx = String.format( "%.2f",  ( fTmpCranePosX  + this.f1ScXTuning) );
														this.tmp_hoistheight = String.format( "%.2f",  ( fTmpHoistHeight) );
														this.tmp_subhoistheight = String.format( "%.2f",  ( fTmpSubHoistHeight) );
														
														if( this.tmp_spare2 != null && "N".equals(this.tmp_spare2) )
														{
															this.tmp_spare2 = "E";
														}
													}
													
												}
												else
												{
													if( "1T".equals( this.tmp_cranecode ) || "2T".equals( this.tmp_cranecode ) || "4T".equals( this.tmp_cranecode ) || "3T".equals( this.tmp_cranecode ) )
													{
														//POSCO ICT 3D에서 Y값 (RADAR) 반대로 표현이 된다고 하여 ( 10 - Y ) 값으로 변경
														ftmpdistancecal = Float.parseFloat( this.tmp_distance );
														ftmpdistancecal = 10.00f - ftmpdistancecal;
														
														if( "3T".equals( this.tmp_cranecode ) )
														{
															ftmpdistancecal = ftmpdistancecal - 0.5f;
														}
															
														this.tmp_distance = String.format( "%.2f",  ftmpdistancecal );
													}
													
													float fTmpCranePosX = Float.parseFloat(this.tmp_posx);

													if( "1T".equals( this.tmp_cranecode )  )
													{
														this.tmp_posx = String.format( "%.2f",  ( fTmpCranePosX + this.f1TmXTuning) );
													}
													else if( "2T".equals( this.tmp_cranecode ) )
													{
														this.tmp_posx = String.format( "%.2f",  ( fTmpCranePosX + this.f2TmXTuning ) );
													}
													else if( "4T".equals( this.tmp_cranecode ) )
													{
														this.tmp_posx = String.format( "%.2f",  ( fTmpCranePosX + this.f4TmXTuning ) );
														this.tmp_hoistheight = "1.00";
														this.tmp_subhoistheight = "6.00";
													}
													else if( "1L".equals( this.tmp_cranecode ) )
													{
														this.tmp_posx = String.format( "%.2f",  ( fTmpCranePosX + this.f1LdXTuning ) );
													}
													else if( "2L".equals( this.tmp_cranecode ) )
													{
														this.tmp_posx = String.format( "%.2f",  ( fTmpCranePosX + this.f2LdXTuning) );
													}
													else if( "3T".equals( this.tmp_cranecode ) )
													{
														this.tmp_posx = String.format( "%.2f",  ( fTmpCranePosX + this.f3TmXTuning) );
													}
												}
											}
											
											this.tmp_dstlladnum = this.isNullCheck(this.tmp_dstlladnum, "");
											this.tmp_eqprfidtagid1 = this.isNullCheck(this.tmp_eqprfidtagid1, "");
											this.tmp_bindingflag1 = this.isNullCheck(this.tmp_bindingflag1, "");
											this.tmp_mtlno = this.isNullCheck(this.tmp_mtlno, "");
											
											StringBuffer sbSubUwbTagData = new StringBuffer();
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_tagid);           //18.설비RFID_TagID
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_tagtype);         //19.BindingFlag (Crane : 1 , Ladle : 2 , 사람   : 3, 기타 : 9)
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_cranecode);       //20.Crane코드
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_posx);            //21.X좌표
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_distance);        //22.Y좌표 ( 크레인 경우 횡행 정보 )
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_hoistheight);     //23.Z좌표1 ( 주 후크 )
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_subhoistheight);  //24.Z좌표2 ( 보조   후크, 크레인의 경우 해당 )
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_spare2);        	//25.영Ladle(F), 공Ladle(E), 빈(N)
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_dstlladnum);		//26.수강Ladle번호
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_eqprfidtagid1);  	//27.설비RFID_TagID_1 (크레인에 실린 경우 (UWB)래들 Tag ID)
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_bindingflag1);		//28.BindingFlag_1 (Crane : 1 , Ladle : 2 , 사람   : 3, 기타 : 9)
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_weight );       	//29.수강Ladle중량 
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_mtlno);			//30.재료번호
											hmIFMData.put(this.tmp_tagid, sbSubUwbTagData.toString());
	
											hmTmpIFMData.put(this.tmp_tagid, sbSubUwbTagData.toString());
											
											this.extend_1_DataInit();
									}
								}
								else if( dataCounter != 0 && ( dataCounter % (setDataCount+1) ) == 0 )
								{
									this.sbHeader = new StringBuffer();
									
									timestamp = new Timestamp(System.currentTimeMillis());
									
									this.sbHeader.append("KE2D1Z10");    //1.TransactionCode
									this.sbHeader.append(",K");          //2.사소구분
									this.sbHeader.append(",3");          //3.조업구분
									this.sbHeader.append(",2L1");        //4.공장공정코드
									this.sbHeader.append(","+ this.sdf2.format(timestamp));           //5.송신측정보편성일시
									this.sbHeader.append(",K2SMRTLSUWBSYS");           //6.송신측정보편성ProgramID
									this.sbHeader.append(",");           //7.EAI송수신관리InterfaceID
									this.sbHeader.append(",3");          //8.InterfaceData지시실적구분
									this.sbHeader.append(",1");          //9.InterfaceData발생응답구분
									this.sbHeader.append(",00001");      //10.InterfaceData송신순서
									this.sbHeader.append(",1");          //11.InterfaceData수정구분
									this.sbHeader.append(",004500");     //12.InterfaceData총길이
									this.sbHeader.append(",");           //13.여분항목
									this.sbHeader.append(",");           //14.BSC_GW_Data공통전문내용
									this.sbHeader.append(",");           //15.IT공통EAI_Interface가변항목사용유무
									this.sbHeader.append("," + this.sdf1.format(timestamp));         //16.Data수집주기구분
	
									if( hmIFMData.size() < this.dsIFMDataCount )
									{
										int forCnt = this.dsIFMDataCount - hmIFMData.size();
	
										itrIFMDataKeys = hmIFMData.keySet().iterator();
										while( itrIFMDataKeys.hasNext() )
										{
											String keyVal = itrIFMDataKeys.next();
											this.sbRet.append( hmIFMData.get(keyVal) );
											
											strIFMDataTmp = hmIFMData.get(keyVal);
											if( 
												strIFMDataTmp.indexOf(",1T,") > 0 || strIFMDataTmp.indexOf(",2T,") > 0 || strIFMDataTmp.indexOf(",3T,") > 0
												|| strIFMDataTmp.indexOf(",4T,") > 0 || strIFMDataTmp.indexOf(",1L,") > 0 || strIFMDataTmp.indexOf(",2L,") > 0
												|| strIFMDataTmp.indexOf(",1H,") > 0 || strIFMDataTmp.indexOf(",2H,") > 0 || strIFMDataTmp.indexOf(",1S,") > 0
											  )
											{
												this.setIFMLogFileWirte(this.isLogYn, 2, strIFMDataTmp);
											}
										}
	
										for(int iSubCnt = 1 ; iSubCnt <= forCnt ; iSubCnt++ )
										{
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
											this.sbRet.append(",");
										}
									}
	
									this.sbHeader.append(this.sbRet.toString());
	
									if( this.isIFMUse == 1 )
									{
										if( ( (this.sbHeader.toString()).length() - (this.sbHeader.toString().replace(",", "")).length() ) == 665 )
										{
											this.out = this.socket.getOutputStream();
											byte[] sendByte = this.concat( this.convertIntToBytes( (this.sbHeader.toString().length() + 4) ), (this.sbHeader.toString()).getBytes() );
											this.out.write( sendByte, 0, sendByte.length );
											this.out.flush();
											
											this.printLine(this.isViewYn, "[Send OK] >>>> " + this.sbHeader.toString() );
											this.setIFMLogFileWirte(this.isLogYn, 3, this.sbHeader.toString());
										}
									}
	
									this.sbRet = new StringBuffer();
									this.dataCounter = 0;				
									
									hmIFMData = new HashMap<String, String>();
								}
								
								this.dataCounter++;
							}
						}
						lastKnownPosition = readWriteFileAccess.getFilePointer();
						readWriteFileAccess.close();
	
						this.LoopCnt_01 = 99;
					}
					else 
					{
						if (debug)
						{
							this.printLine(this.isViewYn, "Couldn't found new line after line # " + dataCounter);
						}
					}
				}
			}
			catch (IOException e) 
			{
				System.out.println("IFM server connection refused. [IOException -  ["+ e.toString() +"]");
			}
			catch (Exception e) 
			{
				stopRunning();
				System.out.println("[Exception - [stopRunning]");
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if( this.isIFMUse == 1 )
					{
						if( this.out != null) this.out.close();
						if( this.socket != null) this.socket.close();
					}
					if( rs != null ) rs.close();
	        		if( ps != null ) ps.close();
	        		if( con != null ) con.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			try
			{
				System.out.println("Retry IFM server connection.");
				Thread.sleep(3000);
				
				//전송 중 접속이 끊겼을때 재접속을 시도하면서 위치데이터 마지막 부분부터 가져온다.
				this.crunchifyFile = new File(this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +".txt");
				long fileLength = this.crunchifyFile.length();
				this.lastKnownPosition = fileLength - 1;
			}
			catch(Exception e)
			{
				System.out.println("IFM server connection refused. ["+ e.toString() +"]");
			}
    	}
    	
    	
		
		if (debug)
		{
			this.printLine(this.isViewYn, "Exit the program...");
		}
	}
	
	
	public byte[] concat( byte[]... values ) {
        int length = 0;
        for ( byte[] value : values ) {
            length += value.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate( length );

        for ( byte[] value : values ) {
            buffer.put( value );
        }
        return buffer.array();
    }

    /**
     * Convert integer value to byte array
     *
     * @param value integer value
     * @param order byte order
     * @return byte array
     */
    public byte[] convertIntToBytes( int value, ByteOrder order ) {
        return ByteBuffer.allocate( Integer.SIZE / Byte.SIZE ).order( order ).putInt( value ).array();
    }

    /**
     * Convert integer value to byte array
     *
     * @param value integer value
     * @return byte array
     */
    public byte[] convertIntToBytes( int value ) {
        return convertIntToBytes( value, ByteOrder.BIG_ENDIAN );
    }

    /**
     * Convert byte array to integer value
     *
     * @param value byte array
     * @param order byte order
     * @return integer value
     */
    public int convertBytesToInt( byte[] value, ByteOrder order ) {
        return ByteBuffer.wrap( value ).order( order ).getInt();
    }

    /**
     * Convert byte array to integer value
     *
     * @param value byte array
     * @return integer value
     */
    public int convertBytesToInt( byte[] value ) {
        return convertBytesToInt( value, ByteOrder.BIG_ENDIAN );
    }
    
    public HashMap <String, String> getCrLdMappingProc_F(Connection sCon, PreparedStatement sPs, ResultSet sRs, float paramPosX, float paramDistance)
    {
    	HashMap <String, String> hm = new HashMap<>();
    	
    	String strRHQuery  = " select seq as seq, rh_tp as no, trim(mtl_no) as mtl_no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm from ke2d1r01 where mtl_no != '' and dstl_lad_num != '00' and operation_tracking_cd in ('R3','R4') and rh_tp = ? order by seq desc limit 1 ";
    	String strLFQuery  = " select seq as seq, lf_num as no, trim(mtl_no) as mtl_no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm from ke2d1t01 where mtl_no != '' and dstl_lad_num != '00' and operation_tracking_cd in ('T3','T4') and lf_num = ? order by seq desc limit 1 ";
    	String strBAPQuery = " select seq as seq, bap_field_num as no, trim(mtl_no) as mtl_no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm from ke2d1q01 where mtl_no != '' and dstl_lad_num != '00' and operation_tracking_cd in ('Q3','Q4') and bap_field_num = ? order by seq desc limit 1 ";

    	String strQuery = "";    	
    	String strParmVal = "";
    	
    	//this.setIFMLogFileWirte(this.isLogYn, 1, "[paramPosX : " + paramPosX +"][paramDistance : " + paramDistance + "]");
    	

    	if( paramPosX >= this.fds1RHs && paramPosX <= this.fds1RHe  )
		{
			// 1RH
    		strQuery = strRHQuery;
    		strParmVal = "1";
		}
    	else if( paramPosX >= this.fds2RHs && paramPosX <= this.fds2RHe  )
		{
			// 2RH
    		strQuery = strRHQuery;
    		strParmVal = "2";
		}
    	else if( paramPosX >= this.fds3RHs && paramPosX <= this.fds3RHe  )
		{
			// 3RH
    		strQuery = strRHQuery;
    		strParmVal = "3";
		}
    	else if( paramPosX >= this.fds1LFs && paramPosX <= this.fds1LFe  )
		{
			// LF
    		strQuery = strLFQuery;
    		strParmVal = "1";
		}
    	else if( paramPosX >= this.fds1RCs && paramPosX <= this.fds1RCe  )
		{
    		// R대차, 회송대차
    		strQuery = "";    	
        	strParmVal = "";
		}
    	else if( paramPosX >= this.fds1Baps && paramPosX <= this.fds1Bape && paramDistance <= 8.00 )
		{
			// 1 BAP
    		strQuery = strBAPQuery;
    		strParmVal = "1";
		}
    	else if( paramPosX >= this.fds2Baps && paramPosX <= this.fds2Bape && paramDistance <= 8.00 )
		{
			// 2 BAP
    		strQuery = strBAPQuery;
    		strParmVal = "2";
		}
    	else if( paramPosX >= this.fds3Baps && paramPosX <= this.fds3Bape && paramDistance <= 8.00 )
		{
			// 3 BAP
    		strQuery = strBAPQuery;
    		strParmVal = "3";
		}

    	if( !"".equals(strQuery) && !"".equals(strParmVal) )
    	{
    		try
    		{
        		sPs = sCon.prepareStatement( strQuery );
        		sPs.setString( 1 , strParmVal );
        		sRs = sPs.executeQuery();
        		
        		this.setIFMLogFileWirte(this.isLogYn, 1, "["+ strParmVal +"]["+ strQuery +"]");
        		
        		while( sRs.next()  )
    			{
        			hm.put("tmp_dstlladnum", sRs.getString("ldnm"));
        			hm.put("tmp_mtlno", sRs.getString("mtl_no"));
        			hm.put("tmp_eqprfidtagid1", "");
        			hm.put("tmp_bindingflag1", "");
        			this.setIFMLogFileWirte(this.isLogYn, 1, "F [ldnm:"+ hm.get("tmp_dstlladnum") +"][mtl_no:"+ hm.get("tmp_mtlno") +"]");
    			}
        		
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	else
    	{
    		hm.put("tmp_dstlladnum", "");
			hm.put("tmp_mtlno", "");
			hm.put("tmp_eqprfidtagid1", "");
			hm.put("tmp_bindingflag1", "");
    	}
    	return hm;
    }
    
    public HashMap <String, String> getCrLdMappingProc_E(Connection sCon, PreparedStatement sPs, ResultSet sRs, float paramPosX, float paramDistance)
    {
    	HashMap <String, String> hm = new HashMap<>();
    	
     	//String strMCQuery  = " select seq as seq, mc_no as no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm from zm2ee508 where dstl_lad_num != '00' and dstl_lad_num != '' and mc_no = ? order by seq desc limit 1 ";
     	//String strMCQuery  = " select seq as seq, mc_no as no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm from zm2ee508 where operation_tracking_cd = 'W7' and mc_no = ? order by seq desc limit 1 ";
    	String strMCQuery  = " select seq as seq, mc_no as no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm from zm2ee506 where operation_tracking_cd = 'W7' and mc_no = ? order by seq desc limit 1 ";

    	String strQuery = "";    	
    	String strParmVal = "";
    	
    	//this.setIFMLogFileWirte(this.isLogYn, 1, "[paramPosX : " + paramPosX +"][paramDistance : " + paramDistance + "]");
    	/*
    	this.setIFMLogFileWirte(this.isLogYn, 1, "getCrLdMappingProc_E_1 => [paramPosX : " + paramPosX +"][paramDistance : " + paramDistance + "]");
    	this.setIFMLogFileWirte(this.isLogYn, 1, "getCrLdMappingProc_E_2 => [this.fds1MCs : " + this.fds1MCs +"][this.fds1MCe : " + this.fds1MCe + "]");
    	this.setIFMLogFileWirte(this.isLogYn, 1, "getCrLdMappingProc_E_2 => [this.fds2MCs : " + this.fds2MCs +"][this.fds2MCe : " + this.fds2MCe + "]");
    	this.setIFMLogFileWirte(this.isLogYn, 1, "getCrLdMappingProc_E_2 => [this.fds3MCs : " + this.fds3MCs +"][this.fds3MCe : " + this.fds3MCe + "]");
    	this.setIFMLogFileWirte(this.isLogYn, 1, "getCrLdMappingProc_E_2 => [this.fds4MCs : " + this.fds4MCs +"][this.fds4MCe : " + this.fds4MCe + "]");
    	*/

    	if( paramPosX >= this.fds1MCs && paramPosX <= this.fds1MCe && paramDistance > 8.00 )
		{
			// 1 M/C
    		strQuery = strMCQuery;
    		strParmVal = "1";
		}
    	else if( paramPosX >= this.fds2MCs && paramPosX <= this.fds2MCe && paramDistance > 8.00 )
		{
			// 2 M/C
    		strQuery = strMCQuery;
    		strParmVal = "2";
		}
    	else if( paramPosX >= this.fds3MCs && paramPosX <= this.fds3MCe )
		{
			// 3 M/C
    		strQuery = strMCQuery;
    		strParmVal = "3";
		}
    	else if( paramPosX >= this.fds4MCs && paramPosX <= this.fds4MCe )
		{
			// 4 M/C
    		strQuery = strMCQuery;
    		strParmVal = "4";
		}
    	
    	if( !"".equals(strQuery) && !"".equals(strParmVal) )
    	{
    		try
    		{
        		sPs = sCon.prepareStatement( strQuery );
        		sPs.setString( 1 , strParmVal );
        		sRs = sPs.executeQuery();
        		
        		this.setIFMLogFileWirte(this.isLogYn, 1, "["+ strParmVal +"]["+ strQuery +"]");
        		
        		while( sRs.next()  )
    			{
        			hm.put("tmp_dstlladnum", sRs.getString("ldnm"));
        			hm.put("tmp_mtlno", "");
        			hm.put("tmp_eqprfidtagid1", "");
        			hm.put("tmp_bindingflag1", "");
        			this.setIFMLogFileWirte(this.isLogYn, 1, "E [ldnm:"+ hm.get("tmp_dstlladnum") +"]");
    			}
        		
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	else
    	{
    		hm.put("tmp_dstlladnum", "");
			hm.put("tmp_mtlno", "");
			hm.put("tmp_eqprfidtagid1", "");
			hm.put("tmp_bindingflag1", "");
    	}

    	return hm;
    }
    
    public HashMap <String, String> getConvertCrLdMappingProc_F(Connection sCon, PreparedStatement sPs, ResultSet sRs, float paramPosX, float paramDistance)
    {
    	HashMap <String, String> hm = new HashMap<>();
    	int iRetCheck = 0;
    	
    	StringBuffer strTLTCQuery = new StringBuffer(5);

    	String strQuery = "";  
    	
    	int iTltcType = 0;
    	String strParmVal_otc = "";

    	String getH21_way = "";
    	String getH22_way = "";
    	
    	int iH21_time = 0; 
    	int iH22_time = 0;
    	
    	String getTltcNo = "";

    	try
		{
	    	//TLTC 진행 방향
	    	strTLTCQuery = new StringBuffer(5);
	    	strTLTCQuery.append(" select h21_way, h22_way from view_km2dte12_way ");
	    	sPs = sCon.prepareStatement( strTLTCQuery.toString() );
			sRs = sPs.executeQuery();
			while( sRs.next()  )
			{
				getH21_way = sRs.getString("h21_way");
				getH22_way = sRs.getString("h22_way");
				this.setIFMLogFileWirte(this.isLogYn, 1, "H/C_F_1 [H21_way:"+ getH21_way +"][H22_way:"+ getH22_way +"]");
			}
			
			if( getH21_way != null && !"".equals(getH21_way) )
			{
				if( getH21_way.indexOf("A") > -1 )
				{
					getH21_way = "A";
				}
				else if( getH21_way.indexOf("B") > -1 )
				{
					getH21_way = "B";
				}
				else
				{
					getH21_way = "B";
				}
			}
			
			if( getH22_way != null && !"".equals(getH22_way) )
			{
				if( getH22_way.indexOf("A") > -1 )
				{
					getH22_way = "A";
				}
				else if( getH22_way.indexOf("B") > -1 )
				{
					getH22_way = "B";
				}
				else
				{
					getH22_way = "B";
				}
			}
			this.setIFMLogFileWirte(this.isLogYn, 1, "H/C_F_2 [H21_way:"+ getH21_way +"][H22_way:"+ getH22_way +"]");
	
			if( "A".equals(getH21_way) || "A".equals(getH22_way) )
			{
				//크레인 위치에 따른 TLTC 위치 구분
		    	if( paramPosX >= this.fDsTltcH5_1_s && paramPosX <= this.fDsTltcH5_1_e  )
				{
					//TLTC H5 - 1
		    		strParmVal_otc = "H5";
		    		iTltcType = 1;
				}
		    	else if( paramPosX >= this.fDsTltcH5_2_s && paramPosX <= this.fDsTltcH5_2_e  )
				{
					//TLTC H5 - 2 
		    		strParmVal_otc = "H5";
		    		iTltcType = 2;
				}
		    	else if( paramPosX >= this.fDsTltcH6_1_s && paramPosX <= this.fDsTltcH6_1_e  )
				{
					//TLTC H6 - 1
		    		strParmVal_otc = "H6";
		    		iTltcType = 1;
				}
		    	else if( paramPosX >= this.fDsTltcH6_2_s && paramPosX <= this.fDsTltcH6_2_e  )
				{
					//TLTC H6 - 2
		    		strParmVal_otc = "H6";
		    		iTltcType = 2;
				}
		    	
		    	if( !"".equals(strParmVal_otc) && iTltcType != 0 )
		    	{
					//TLTC 선로 구분
			    	//용선 크레인 위치에서 H21 또는 H22 대차를 찾는다.
			    	if( "A".equals(getH21_way) && "A".equals(getH21_way) )
			    	{
			    		strTLTCQuery = new StringBuffer(5);
				    	strTLTCQuery.append(" SELECT trim(rway_dl_pare_rway_no) as rway_dl_pare_rway_no, operation_tracking_cd, mat_ch_prg_dir_tp  ");
				    	strTLTCQuery.append(" FROM view_km2dte12_rway_tc_tp ");
				    	strTLTCQuery.append(" WHERE mat_ch_prg_dir_tp like '%A%' AND operation_tracking_cd = ? ");
				    	sPs = sCon.prepareStatement( strTLTCQuery.toString() );
				    	sPs.setString( 1 , strParmVal_otc );
						sRs = sPs.executeQuery();
						while( sRs.next()  )
						{
							getTltcNo = sRs.getString("rway_dl_pare_rway_no");
						}
			    	}
			    	else if( "A".equals(getH21_way) && !"A".equals(getH22_way) )
					{
			    		getTltcNo = "H21";
					}				
					else if( !"A".equals(getH21_way) && "A".equals(getH22_way) )
					{
						getTltcNo = "H22";
					}
			    	this.setIFMLogFileWirte(this.isLogYn, 1, "H/C_F [strParmVal_otc:"+ strParmVal_otc +"][iTltcType:"+ iTltcType +"][getTltcNo:"+ getTltcNo +"]");
		    		
		    		
			    	//크레인에서 들어올리는 mtl_no1, ch_lad_num1, mtl_no2, ch_lad_num2 값 가져오기
			    	strTLTCQuery = new StringBuffer(5);
			    	strTLTCQuery.append(" SELECT  ");
			    	strTLTCQuery.append(" seq, trim(rway_dl_pare_rway_no) as rway_dl_pare_rway_no, trim(operation_tracking_cd) as operation_tracking_cd, trim(mat_ch_prg_dir_tp) as mat_ch_prg_dir_tp ");
			    	if( iTltcType == 1)
			    	{
			    		strTLTCQuery.append(" , trim(mtl_no1) as mtl_no, trim(ch_lad_num1) as ldnm ");		    		
			    	}
			    	else if( iTltcType == 2)
			    	{
			    		strTLTCQuery.append(" , trim(mtl_no2) as mtl_no, trim(ch_lad_num2) as ldnm ");
			    	}
			    	strTLTCQuery.append(" FROM km2dte12   ");
			    	strTLTCQuery.append(" WHERE rway_dl_pare_rway_no = ? AND operation_tracking_cd = ? ");		    			    	
			    	strTLTCQuery.append(" ORDER BY seq DESC LIMIT 1  ");
	
			    	sPs = sCon.prepareStatement( strTLTCQuery.toString() );
			    	sPs.setString( 1 , getTltcNo );
			    	sPs.setString( 2 , strParmVal_otc );
					sRs = sPs.executeQuery();
					
					this.setIFMLogFileWirte(this.isLogYn, 1, "["+ getTltcNo +"]["+ strParmVal_otc +"]["+ strTLTCQuery.toString() +"]");
					
					while( sRs.next()  )
					{
						hm.put("tmp_dstlladnum", sRs.getString("ldnm"));
	        			hm.put("tmp_mtlno", sRs.getString("mtl_no"));
	        			hm.put("tmp_eqprfidtagid1", "");
	        			hm.put("tmp_bindingflag1", "");
						this.setIFMLogFileWirte(this.isLogYn, 1, "H/C_F RET_DB ["+sRs.getString("seq") +","+ sRs.getString("rway_dl_pare_rway_no") +","+ sRs.getString("operation_tracking_cd") +","+ sRs.getString("mat_ch_prg_dir_tp") +","+ sRs.getString("mtl_no") +","+ sRs.getString("ldnm") +"]");
					}
					
					iRetCheck = 1;
		    	}
		    	else
		    	{
		    		iRetCheck = 0;
		    	}
			}
			else
			{
				iRetCheck = 0;
			}
	
	    	if( iRetCheck == 0 )
	    	{
	    		hm.put("tmp_dstlladnum", "");
				hm.put("tmp_mtlno", "");
				hm.put("tmp_eqprfidtagid1", "");
				hm.put("tmp_bindingflag1", "");
	    	}
    	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

    	return hm;
    }
    
    private String calcDistanceVal(float r_x1, float r_x2, float v_x1, float v_x2, float orgx)
    {
    	//this.fdsCraneDistanceMin, this.fdsCraneDistanceMax, this.fdsCraneDistanceVirtualMin, this.fdsCraneDistanceVirtualMax, Float.parseFloat( this.tmp_distance )
    	
    	float ret_val = 0;
    	
    	float m = (v_x2 - v_x1) / ( r_x2 - r_x1 );
		float b = -1 * m * r_x1;
		
		if( orgx > 19.9 )
		{
			orgx = 20.00f;
		}
		
		ret_val = m * orgx + b;
    	
    	return String.format( "%.2f",  ret_val );
    }
    
    private String findLadleTagid(String parm_LadleNum)
    {
    	String ret = "";

    	for(int ii = 0 ; ii < this.aryDsLadleNums.length ; ii++ )
		{
			if( ( this.aryDsLadleNums[ii] ).equals( parm_LadleNum ) )
			{
				ret = this.aryDsLadleTagids[ii];
				break;
			}
		}
    	
    	return ret;
    }
    
    private String isNullCheck(String paramVal, String param_set)
    {
    	String ret = "";
    	
    	if( paramVal == null || "null".equals(paramVal) || "0".equals(paramVal) )
    	{
    		ret = param_set;
    	}
    	else
    	{
    		ret = paramVal;
    	}
    	
    	return ret;
    }
    
    private void extend_1_DataInit()
    {
    	this.tmp_dstlladnum = "";
		this.tmp_eqprfidtagid1 = "";
		this.tmp_bindingflag1 = "";
		this.tmp_mtlno = "";
    }

    private void setIFMLogFileWirte(int paramLogYn, int fileflag, String sLogInfoParam)
	{
    	if( paramLogYn == 1 )
    	{
			this.timestamp = new Timestamp(System.currentTimeMillis());
			this.curYYYY = this.sdfYYYY.format(this.timestamp);
			this.curMM = this.sdfMM.format(this.timestamp);
			this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
			
			////////////////////////////////////////////////////////////////////////////
			String yearDir = this.dsLogBackupPath + "/" + this.curYYYY;
			String monthDir = this.dsLogBackupPath + "/" + this.curYYYY + "/" + this.curMM;
			String sFileName = "";
			if( fileflag == 2 )
			{
				sFileName = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +"_ifm_crane_log.txt";
			}
			else if( fileflag == 3 )
			{
				sFileName = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +"_ifm_full_log.txt";
			}
			else
			{
				sFileName = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +"_ifm_log.txt";
			}
			////////////////////////////////////////////////////////////////////////////
	
			this.f = new File(yearDir);
			if( !f.exists() )	f.mkdir();
			
			this.f = new File(monthDir);
			if( !f.exists() )	f.mkdir();
	
			try
			{
				this.fw = new FileWriter(sFileName, true);
				this.bfw = new BufferedWriter(this.fw);
				
				bfw.write("[" + this.sdf2.format(this.timestamp) + "] >> " + sLogInfoParam);
				bfw.newLine();
				bfw.flush();
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
	}

    public static void main(String args[])
	{
		ExecutorService crunchifyExecutor = Executors.newFixedThreadPool(4);
		// Replace username with your real value

		IFMUwbRtlsIClient ifm = new IFMUwbRtlsIClient(150);
		
		// Start running log file tailer on crunchify.log file
		crunchifyExecutor.execute(ifm);		
	}
}
