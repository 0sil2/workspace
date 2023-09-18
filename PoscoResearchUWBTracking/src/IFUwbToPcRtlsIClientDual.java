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
import java.util.Calendar;
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
 *   
 *   포스코 박희용 대리 작업 사항
 *   UWB DATA -> 조업 PC로 데이터 전송 처리
 *   현재는 개발 서버로 적용 되어 있음.
 *   
 *   개발서버 정보
 *   IP : 172.24.92.104 / PORT : 3008, 3108 
 *   
 *   운영서버 정보
 *   IP : 172.24.92.100 / PORT : 3008, 3108
 *   
 */

public class IFUwbToPcRtlsIClientDual implements Runnable 
{

	private boolean debug = false;
	private int crunchifyRunEveryNSeconds = 50;
	private long lastKnownPosition = 0;
	private boolean shouldIRun = true;
	private File crunchifyFile = null;
	private static int dataCounter = 1;
	private int LoopCnt_01 = 0;
	
	private String curYYYY = "";
	private String curMM = "";
	private String curYYYYMMDD = "";
	private static int setDataCount = 1000;
	//private static int setIFDataLen = 50;
	
	private static int isIFMUse = 1;
	private static int isViewYn = 1;
	private static int isLogYn = 1;
	
	private String dsLogBackupPath = "";
	
	
	private File f = null;
	FileWriter fw = null;
	BufferedWriter bfw = null;
	

	////////////////////////////////////////////////////////////////////
	private String resource = "/usr/local/posco/uwbpos/set_pc.properties";
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
	
	// 전송 데이터 임시 저장 배열 
	// 0 : 크레인구분, 1 : 출발공정코드, 2 : 도착공정코드, 3 : 출발일시, 4 : 도착일시, 5 : 이송시간 (출발->도착 시간 분으로)
	// 6 : 권상일시(개시), 7 : 권하일시(개시), 8 : 권상시간(종료), 9 : 권하시간(종료), 
	// 10 : 제강공 Ladle 구분 (N, E, F)
	// 11 : 측정중량, 12 : 잔탕구분(Y/N), 13 : 배재구분(Y/N), 14 : 터렛도착구분(Y/N), 
	// 15 : 크레인방향 (1 N->E/F, 2 E/F->N ), 16 : 데이터완료상태 (0 미완료, 1 완료)
	private String [][] arryTmpCraneInfo = { 
			  {"H1","","","","","","","","","","","","","","","","0"}
			, {"H2","","","","","","","","","","","","","","","","0"}
			, {"S1","","","","","","","","","","","","","","","","0"}
			, {"L1","","","","","","","","","","","","","","","","0"}
			, {"L2","","","","","","","","","","","","","","","","0"}
			, {"T1","","","","","","","","","","","","","","","","0"}
			, {"T2","","","","","","","","","","","","","","","","0"}
			, {"T3","","","","","","","","","","","","","","","","0"}
			, {"T4","","","","","","","","","","","","","","","","0"}
	};
	
	// 크레인 현재 위치
	// 0 : H1, 1 : H2, 2 : S1, 3 : L1, 4 : L2, 
	// 5 : T1, 6 : T2, 7 : T3, 8 : T4
	private String [] arryCrPos = {"", "", "", "", "", "", "", "", ""};
	
	//ladle status
	// 0 : H1, 1 : H2, 2 : S1, 3 : L1, 4 : L2, 
	// 5 : T1, 6 : T2, 7 : T3, 8 : T4
	private String [] arryCurLdStatus = {"", "", "", "", "", "", "", "", ""};
	private String [] arryOldLdStatus = {"", "", "", "", "", "", "", "", ""};

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
	private String tmp_spare2 = "";			//spare1 (추가1 데이타 존재, 아니면 -99)
	private String tmp_CRstatus = "";			//spare2 (추가2 데이타 존재, 아니면 -99) 영 Ladle(F) 3, 공 Ladle 2,  빈(N) 1
	private String tmp_ymd = "";			//년월일
	private String tmp_hms = "";			//시분초
	private String tmp_old_CRstatus2 = "";		//과거 spare2 (추가2 데이타 존재, 아니면 -99)
	
	private String tmp_rweight = "";
	private String tmp_rmove = "";
	
	private String tmp_dstlladnum = "";		//수강래들번호
	private String tmp_eqprfidtagid1 = "";	//설비 RFID_TagID 크레인에 실린 경우 (UWB)래들 Tag ID
	private String tmp_bindingflag1 = "";	//BindingFlag_1
	private String tmp_mtlno = "";			//재료번호
	private String tmp_old_dstlladnum = "";		//수강래들번호
	private String tmp_old_eqprfidtagid1 = "";	//설비 RFID_TagID 크레인에 실린 경우 (UWB)래들 Tag ID
	private String tmp_old_bindingflag1 = "";	//BindingFlag_1
	private String tmp_old_mtlno = "";			//재료번호

	private Socket socket = null;
	private OutputStream out = null;
	
	private Socket socket_2 = null;
	private OutputStream out_2 = null;
	////////////////////////////////////////////////////////////////////////
	private String Poscoict_Svr_Ip = "";
	private int Poscoict_Svr_Port = -1;
	
	private String Poscoict_Svr_Ip_2 = "";
	private int Poscoict_Svr_Port_2 = -1;
	
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
    private String ds1RHcode = "";
    private String ds2RHcode = "";
    private String ds3RHcode = "";
    private float fds1RHs = 0;
    private float fds1RHe = 0;
    private float fds2RHs = 0;
    private float fds2RHe = 0;
    private float fds3RHs = 0;
    private float fds3RHe = 0;
    
    //BAP
    private String ds1Bapcode = "";
    private String ds2Bapcode = "";
    private String ds3Bapcode = "";
    private float fds1Baps = 0;
    private float fds1Bape = 0;
    private float fds2Baps = 0;
    private float fds2Bape = 0;
    private float fds3Baps = 0;
    private float fds3Bape = 0;
        
    // MC
    private String ds1MCcode = "";
    private String ds2MCcode = "";
    private String ds3MCcode = "";
    private String ds4MCcode = "";
    private float fds1MCs = 0;
    private float fds1MCe = 0;
    private float fds2MCs = 0;
    private float fds2MCe = 0;
    private float fds3MCs = 0;
    private float fds3MCe = 0;
    private float fds4MCs = 0;
    private float fds4MCe = 0;
    
    // Bunner
    private String ds1Bnrcode = "";
    private String ds2Bnrcode = "";
    private String ds3Bnrcode = "";
    private String ds4Bnrcode = "";
    private String ds5Bnrcode = "";
    private String ds6Bnrcode = "";
    private String ds7Bnrcode = "";
    private float fds1Bnrs = 0;
    private float fds1Bnre = 0;
    private float fds2Bnrs = 0;
    private float fds2Bnre = 0;
    private float fds3Bnrs = 0;
    private float fds3Bnre = 0;
    private float fds4Bnrs = 0;
    private float fds4Bnre = 0;
    private float fds5Bnrs = 0;
    private float fds5Bnre = 0;
    private float fds6Bnrs = 0;
    private float fds6Bnre = 0;
    private float fds7Bnrs = 0;
    private float fds7Bnre = 0;
    
    // 신구 경동대 (Ladle Repair) 
    private String ds1Lrcode = "";
    private String ds2Lrcode = "";
    private String ds3Lrcode = "";
    private String ds4Lrcode = "";
    private String ds5Lrcode = "";
    private String ds6Lrcode = "";
    private String ds7Lrcode = "";
    private float fds1Lrs = 0;
    private float fds1Lre = 0;
    private float fds2Lrs = 0;
    private float fds2Lre = 0;
    private float fds3Lrs = 0;
    private float fds3Lre = 0;
    private float fds4Lrs = 0;
    private float fds4Lre = 0;
    private float fds5Lrs = 0;
    private float fds5Lre = 0;
    private float fds6Lrs = 0;
    private float fds6Lre = 0;
    private float fds7Lrs = 0;
    private float fds7Lre = 0;

    //LF
    private String ds1LFcode = "";
    private float fds1LFs = 0;
    private float fds1LFe = 0;
    
    //R대차, 회송대차
    private String ds1RCcode = "";
    private float fds1RCs = 0;
    private float fds1RCe = 0;
    
    //Pos-Lead
    private String dsPosLeadcode = "";
    private float fdsPosLeads = 0;
    private float fdsPosLeade = 0;

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
	
	public IFUwbToPcRtlsIClientDual()
	{
		
	}
	
	public IFUwbToPcRtlsIClientDual(int myInterval) 
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
			
			this.Poscoict_Svr_Ip_2 = properties.getProperty("datasource.poscoict.ifm.svr.ip2");
			this.Poscoict_Svr_Port_2 = Integer.parseInt(properties.getProperty("datasource.poscoict.ifm.svr.port2"));
			
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
		    
			//BAP
			this.ds1Bapcode = properties.getProperty("datasource.crane.1bap.code");
			this.ds2Bapcode = properties.getProperty("datasource.crane.2bap.code");
			this.ds3Bapcode = properties.getProperty("datasource.crane.3bap.code");
			this.fds1Baps = Float.parseFloat(properties.getProperty("datasource.crane.1bap.s"));
			this.fds1Bape = Float.parseFloat(properties.getProperty("datasource.crane.1bap.e"));
			this.fds2Baps = Float.parseFloat(properties.getProperty("datasource.crane.2bap.s"));
			this.fds2Bape = Float.parseFloat(properties.getProperty("datasource.crane.2bap.e"));
			this.fds3Baps = Float.parseFloat(properties.getProperty("datasource.crane.3bap.s"));
			this.fds3Bape = Float.parseFloat(properties.getProperty("datasource.crane.3bap.e"));
			
		    //RH
			this.ds1RHcode = properties.getProperty("datasource.crane.1rh.code");
			this.ds2RHcode = properties.getProperty("datasource.crane.2rh.code");
			this.ds3RHcode = properties.getProperty("datasource.crane.3rh.code");
			this.fds1RHs = Float.parseFloat(properties.getProperty("datasource.crane.1rh.s"));
			this.fds1RHe = Float.parseFloat(properties.getProperty("datasource.crane.1rh.e"));
			this.fds2RHs = Float.parseFloat(properties.getProperty("datasource.crane.2rh.s"));
			this.fds2RHe = Float.parseFloat(properties.getProperty("datasource.crane.2rh.e"));
			this.fds3RHs = Float.parseFloat(properties.getProperty("datasource.crane.3rh.s"));
			this.fds3RHe = Float.parseFloat(properties.getProperty("datasource.crane.3rh.e"));
			
			// MC
			this.ds1MCcode = properties.getProperty("datasource.crane.1mc.code");
			this.ds2MCcode = properties.getProperty("datasource.crane.2mc.code");
			this.ds3MCcode = properties.getProperty("datasource.crane.3mc.code");
			this.ds4MCcode = properties.getProperty("datasource.crane.4mc.code");
			this.fds1MCs = Float.parseFloat(properties.getProperty("datasource.crane.1mc.s"));
			this.fds1MCe = Float.parseFloat(properties.getProperty("datasource.crane.1mc.e"));
			this.fds2MCs = Float.parseFloat(properties.getProperty("datasource.crane.2mc.s"));
			this.fds2MCe = Float.parseFloat(properties.getProperty("datasource.crane.2mc.e"));
			this.fds3MCs = Float.parseFloat(properties.getProperty("datasource.crane.3mc.s"));
			this.fds3MCe = Float.parseFloat(properties.getProperty("datasource.crane.3mc.e"));
			this.fds4MCs = Float.parseFloat(properties.getProperty("datasource.crane.4mc.s"));
			this.fds4MCe = Float.parseFloat(properties.getProperty("datasource.crane.4mc.e"));
			
			// Bunner
			this.ds1Bnrcode = properties.getProperty("datasource.crane.1bnr.code");
			this.ds2Bnrcode = properties.getProperty("datasource.crane.2bnr.code");
			this.ds3Bnrcode = properties.getProperty("datasource.crane.3bnr.code");
			this.ds4Bnrcode = properties.getProperty("datasource.crane.4bnr.code");
			this.ds5Bnrcode = properties.getProperty("datasource.crane.5bnr.code");
			this.ds6Bnrcode = properties.getProperty("datasource.crane.6bnr.code");
			this.ds7Bnrcode = properties.getProperty("datasource.crane.7bnr.code");
			this.fds1Bnrs = Float.parseFloat(properties.getProperty("datasource.crane.1bnr.s"));
			this.fds1Bnre = Float.parseFloat(properties.getProperty("datasource.crane.1bnr.e"));
			this.fds2Bnrs = Float.parseFloat(properties.getProperty("datasource.crane.2bnr.s"));
			this.fds2Bnre = Float.parseFloat(properties.getProperty("datasource.crane.2bnr.e"));
			this.fds3Bnrs = Float.parseFloat(properties.getProperty("datasource.crane.3bnr.s"));
			this.fds3Bnre = Float.parseFloat(properties.getProperty("datasource.crane.3bnr.e"));
			this.fds4Bnrs = Float.parseFloat(properties.getProperty("datasource.crane.4bnr.s"));
			this.fds4Bnre = Float.parseFloat(properties.getProperty("datasource.crane.4bnr.e"));
			this.fds5Bnrs = Float.parseFloat(properties.getProperty("datasource.crane.5bnr.s"));
			this.fds5Bnre = Float.parseFloat(properties.getProperty("datasource.crane.5bnr.e"));
			this.fds6Bnrs = Float.parseFloat(properties.getProperty("datasource.crane.6bnr.s"));
			this.fds6Bnre = Float.parseFloat(properties.getProperty("datasource.crane.6bnr.e"));
			this.fds7Bnrs = Float.parseFloat(properties.getProperty("datasource.crane.7bnr.s"));
			this.fds7Bnre = Float.parseFloat(properties.getProperty("datasource.crane.7bnr.e"));

			// 신구 경동대 (Ladle Repair) 
			this.ds1Lrcode = properties.getProperty("datasource.crane.1lr.code");
			this.ds2Lrcode = properties.getProperty("datasource.crane.2lr.code");
			this.ds3Lrcode = properties.getProperty("datasource.crane.3lr.code");
			this.ds4Lrcode = properties.getProperty("datasource.crane.4lr.code");
			this.ds5Lrcode = properties.getProperty("datasource.crane.5lr.code");
			this.ds6Lrcode = properties.getProperty("datasource.crane.6lr.code");
			this.ds7Lrcode = properties.getProperty("datasource.crane.7lr.code");
			this.fds1Lrs = Float.parseFloat(properties.getProperty("datasource.crane.1lr.s"));
			this.fds1Lre = Float.parseFloat(properties.getProperty("datasource.crane.1lr.e"));
			this.fds2Lrs = Float.parseFloat(properties.getProperty("datasource.crane.2lr.s"));
			this.fds2Lre = Float.parseFloat(properties.getProperty("datasource.crane.2lr.e"));
			this.fds3Lrs = Float.parseFloat(properties.getProperty("datasource.crane.3lr.s"));
			this.fds3Lre = Float.parseFloat(properties.getProperty("datasource.crane.3lr.e"));
			this.fds4Lrs = Float.parseFloat(properties.getProperty("datasource.crane.4lr.s"));
			this.fds4Lre = Float.parseFloat(properties.getProperty("datasource.crane.4lr.e"));
			this.fds5Lrs = Float.parseFloat(properties.getProperty("datasource.crane.5lr.s"));
			this.fds5Lre = Float.parseFloat(properties.getProperty("datasource.crane.5lr.e"));
			this.fds6Lrs = Float.parseFloat(properties.getProperty("datasource.crane.6lr.s"));
			this.fds6Lre = Float.parseFloat(properties.getProperty("datasource.crane.6lr.e"));
			this.fds7Lrs = Float.parseFloat(properties.getProperty("datasource.crane.7lr.s"));
			this.fds7Lre = Float.parseFloat(properties.getProperty("datasource.crane.7lr.e"));
			
		    //LF
			this.ds1LFcode = properties.getProperty("datasource.crane.lf.code");
			this.fds1LFs = Float.parseFloat(properties.getProperty("datasource.crane.lf.s"));
			this.fds1LFe = Float.parseFloat(properties.getProperty("datasource.crane.lf.e"));
		    
		    //R대차, 회송대차
			this.ds1RCcode = properties.getProperty("datasource.crane.rc.code");
			this.fds1RCs = Float.parseFloat(properties.getProperty("datasource.crane.rc.s"));
			this.fds1RCe = Float.parseFloat(properties.getProperty("datasource.crane.rc.e"));
			
			// POS-LEAD
			this.dsPosLeadcode = properties.getProperty("datasource.crane.poslead.code");
			this.fdsPosLeads = Float.parseFloat(properties.getProperty("datasource.crane.poslead.s"));
			this.fdsPosLeade = Float.parseFloat(properties.getProperty("datasource.crane.poslead.e"));

		    // Ladle Status
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
	
	public IFUwbToPcRtlsIClientDual(String myFile, int myInterval) 
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
        	String myFile = "";
			String oldMyFile = "";
			
			this.timestamp = new Timestamp(System.currentTimeMillis());
			this.curYYYY = this.sdfYYYY.format(this.timestamp);
			this.curMM = this.sdfMM.format(this.timestamp);
			this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
			
			myFile = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +".txt";
			oldMyFile = myFile;
        	
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
									//this.printLine(this.isViewYn, "["+ dataCounter +"]" + sDataLine );
									
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
											this.tmp_old_CRstatus2 = "";
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
													this.tmp_CRstatus = this.arryTmp[12].trim();
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
														
														//현재 PC 서버에 전송하는 데이터가 NUMBER(4)로 되어 있어, 소수점 절삭을 시킴 
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
														ftmp_ex_spare2 = Float.parseFloat(this.tmp_CRstatus);
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
															this.tmp_CRstatus = "0";
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
													if( "-99".equals(this.tmp_CRstatus) ) 		this.tmp_CRstatus = "1";
													if( "-99".equals(this.tmp_spare1) )
													{
														this.tmp_spare1 = "0";
														this.tmp_subhoistheight = "0";
													}

													/////////////////////////////////////////////////
													//현재 크레인에 설비 위치 코드 설정
													this.setCurrentCraneLocationCheck(this.tmp_cranecode, Float.parseFloat(this.tmp_posx), Float.parseFloat(this.tmp_distance));
													/////////////////////////////////////////////////
													
													if( hmTmpIFMData.size() > 0 )
													{
														if( hmTmpIFMData.get(this.tmp_tagid) != null )
														{
															String [] arryOldData = (hmTmpIFMData.get(this.tmp_tagid)+" ").split(",");
															
															this.tmp_old_CRstatus2 = arryOldData[8];
															this.tmp_old_dstlladnum = arryOldData[9];
															this.tmp_old_eqprfidtagid1 = arryOldData[10];
															this.tmp_old_bindingflag1 = arryOldData[11];
															
															if( arryOldData.length == 16 )
															{
																this.tmp_old_mtlno = arryOldData[13].trim();
															}
															else
															{
																this.tmp_old_mtlno = "";
															}
														}
													}
													
													if( this.tmp_CRstatus != null && !"0".equals(this.tmp_CRstatus) )
													{
														if( "1".equals(this.tmp_CRstatus) )		this.tmp_CRstatus = "N";
														else if( "2".equals(this.tmp_CRstatus) )	this.tmp_CRstatus = "E";
														else if( "3".equals(this.tmp_CRstatus) )	this.tmp_CRstatus = "F";
														else if( "0".equals(this.tmp_CRstatus) )	this.tmp_CRstatus = "N";
													}
													else
													{
														this.tmp_CRstatus = this.tmp_old_CRstatus2;
														
														if( "".equals(this.tmp_CRstatus) )	this.tmp_CRstatus = "N";
													}
													
													
													//this.timestamp = new Timestamp(System.currentTimeMillis());
													//this.printLine(this.isViewYn, "[999--] "+ this.sdf2.format(this.timestamp) +" ["+ this.tmp_cranecode +"] ["+ this.tmp_posx +"] ["+ this.tmp_CRstatus +"] ["+ this.tmp_weight +"]");
													
													
													if( "T".equals(this.tmp_craneline) )
													{
														HashMap <String, String> hmCrLdMappingInfo = null;
														float fTmpCranePosX = Float.parseFloat(this.tmp_posx);

                                                        //uwb crane tag 설치가 운전석 또는 운전석 반대편에 설치가 되어 있다.
                                                        if( "1T".equals(this.tmp_cranecode) )	fTmpCranePosX = fTmpCranePosX - 5.00f; 
                                                        else if( "2T".equals(this.tmp_cranecode) )	fTmpCranePosX = fTmpCranePosX + 5.00f;
                                                        else if( "4T".equals(this.tmp_cranecode) )	fTmpCranePosX = fTmpCranePosX - 5.00f;
														
														if( this.tmp_CRstatus != null && this.tmp_old_CRstatus2 != null && !(this.tmp_old_CRstatus2).equals(this.tmp_CRstatus)  )
														{
															//this.printLine(this.isViewYn, "[1] ["+ this.tmp_CRstatus +"] ["+ this.tmp_weight +"]");
															
															if( (this.dsCraneStateCdF).equals(this.tmp_CRstatus) )
															{
																if( 
																	this.tmp_distance == null || "".equals(this.tmp_distance)  
																	|| "-".equals(this.tmp_distance) || "-99".equals(this.tmp_distance) 
																)
																{
																	hmCrLdMappingInfo = this.getCrLdMappingProc_F( fTmpCranePosX, this.fdsCraneDistanceMax );
																	
																	//this.printLine(this.isViewYn, "[1]");
																	this.timestamp = new Timestamp(System.currentTimeMillis());
																	this.setCraneMatrixProcessSetting(this.tmp_cranecode, this.getCraneCurPosMatrix(this.tmp_cranecode), this.sdf2.format(this.timestamp));
																	this.setCraneMatrix(this.tmp_cranecode, 10, this.tmp_CRstatus);
																	this.setCraneMatrix(this.tmp_cranecode, 11, this.tmp_weight);
																	this.setSendSocket(this.tmp_cranecode);
																}
																else
																{
																	hmCrLdMappingInfo = this.getCrLdMappingProc_F( fTmpCranePosX, Float.parseFloat(this.tmp_distance) );
																	
																	//this.printLine(this.isViewYn, "[2]");
																	
																	this.timestamp = new Timestamp(System.currentTimeMillis());
																	this.setCraneMatrixProcessSetting(this.tmp_cranecode, this.getCraneCurPosMatrix(this.tmp_cranecode), this.sdf2.format(this.timestamp));
																	this.setCraneMatrix(this.tmp_cranecode, 10, this.tmp_CRstatus);
																	this.setCraneMatrix(this.tmp_cranecode, 11, this.tmp_weight);
																	this.setSendSocket(this.tmp_cranecode);
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
																}
															}
															else if( (this.dsCraneStateCdE).equals(this.tmp_CRstatus) )
															{
																//this.printLine(this.isViewYn, "[3] ["+ this.tmp_CRstatus +"] ["+ this.tmp_weight +"]");
																
																if( 
																		this.tmp_distance == null || "".equals(this.tmp_distance)  
																	|| "-".equals(this.tmp_distance) || "-99".equals(this.tmp_distance) 
																)
																{
																	hmCrLdMappingInfo = this.getCrLdMappingProc_E( fTmpCranePosX, this.fdsCraneDistanceMax );
																	
																	//this.printLine(this.isViewYn, "[3]");
																	
																	this.timestamp = new Timestamp(System.currentTimeMillis());
																	this.setCraneMatrixProcessSetting(this.tmp_cranecode, this.getCraneCurPosMatrix(this.tmp_cranecode), this.sdf2.format(this.timestamp));
																	this.setCraneMatrix(this.tmp_cranecode, 10, this.tmp_CRstatus);
																	this.setCraneMatrix(this.tmp_cranecode, 11, this.tmp_weight);
																	this.setSendSocket(this.tmp_cranecode);
																}
																else
																{
																	hmCrLdMappingInfo = this.getCrLdMappingProc_E( fTmpCranePosX, Float.parseFloat(this.tmp_distance) );
																	
																	//this.printLine(this.isViewYn, "[4]");
																	
																	this.timestamp = new Timestamp(System.currentTimeMillis());
																	this.setCraneMatrixProcessSetting(this.tmp_cranecode, this.getCraneCurPosMatrix(this.tmp_cranecode), this.sdf2.format(this.timestamp));
																	this.setCraneMatrix(this.tmp_cranecode, 10, this.tmp_CRstatus);
																	this.setCraneMatrix(this.tmp_cranecode, 11, this.tmp_weight);
																	this.setSendSocket(this.tmp_cranecode);
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
																}
															}
															else if( (this.dsCraneStateCdN).equals(this.tmp_CRstatus) )
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
															this.tmp_CRstatus = this.tmp_old_CRstatus2;
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
                                                        
														if( this.tmp_CRstatus != null && this.tmp_old_CRstatus2 != null && !(this.tmp_old_CRstatus2).equals(this.tmp_CRstatus)  )
														{
															//this.printLine(this.isViewYn, "[5] ["+ this.tmp_CRstatus +"] ["+ this.tmp_weight +"] ====== ");
															
															if( (this.dsCraneStateCdF).equals(this.tmp_CRstatus) )
															{
																if( 
																	this.tmp_distance == null || "".equals(this.tmp_distance)  
																	|| "-".equals(this.tmp_distance) || "-99".equals(this.tmp_distance) 
																)
																{
																	hmCrLdMappingInfo = this.getCrLdMappingProc_F( fTmpCranePosX, this.fdsCraneDistanceMax );
																	
																	//this.printLine(this.isViewYn, "[5]");
																	
																	this.timestamp = new Timestamp(System.currentTimeMillis());
																	this.setCraneMatrixProcessSetting(this.tmp_cranecode, this.getCraneCurPosMatrix(this.tmp_cranecode), this.sdf2.format(this.timestamp));
																	this.setCraneMatrix(this.tmp_cranecode, 10, this.tmp_CRstatus);
																	this.setCraneMatrix(this.tmp_cranecode, 11, this.tmp_weight);
																	this.setSendSocket(this.tmp_cranecode);
																}
																else
																{
																	hmCrLdMappingInfo = this.getCrLdMappingProc_F( fTmpCranePosX, Float.parseFloat(this.tmp_distance) );
																	
																	//this.printLine(this.isViewYn, "[6]");
																	
																	this.timestamp = new Timestamp(System.currentTimeMillis());
																	this.setCraneMatrixProcessSetting(this.tmp_cranecode, this.getCraneCurPosMatrix(this.tmp_cranecode), this.sdf2.format(this.timestamp));
																	this.setCraneMatrix(this.tmp_cranecode, 10, this.tmp_CRstatus);
																	this.setCraneMatrix(this.tmp_cranecode, 11, this.tmp_weight);
																	this.setSendSocket(this.tmp_cranecode);
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
																}
															}
															else if( (this.dsCraneStateCdE).equals(this.tmp_CRstatus) )
															{
																// L/D  라인 크레인에서 공 Ladle를 들어 올릴때 처리하는 로직이 필요
																
																//this.printLine(this.isViewYn, "[7]");
																
																this.timestamp = new Timestamp(System.currentTimeMillis());
																this.setCraneMatrixProcessSetting(this.tmp_cranecode, this.getCraneCurPosMatrix(this.tmp_cranecode), this.sdf2.format(this.timestamp));
																this.setCraneMatrix(this.tmp_cranecode, 10, this.tmp_CRstatus);
																this.setCraneMatrix(this.tmp_cranecode, 11, this.tmp_weight);
																this.setSendSocket(this.tmp_cranecode);
															}
															else if( (this.dsCraneStateCdN).equals(this.tmp_CRstatus) )
															{
																this.extend_1_DataInit();															
															}
															else
															{
																this.extend_1_DataInit();
															}
															
															//this.printLine(this.isViewYn, "[5] End");
														}
														else
														{
															this.tmp_CRstatus = this.tmp_old_CRstatus2;
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
														
														if( this.tmp_CRstatus != null && this.tmp_old_CRstatus2 != null && !(this.tmp_old_CRstatus2).equals(this.tmp_CRstatus)  )
														{
															//this.printLine(this.isViewYn, "[8] ["+ this.tmp_CRstatus +"] ["+ this.tmp_weight +"]");
															
															if( (this.dsCraneStateCdF).equals(this.tmp_CRstatus) )
															{
																hmCrLdMappingInfo = this.getConvertCrLdMappingProc_F( fTmpCranePosX, Float.parseFloat(this.tmp_distance) );
																
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
																}
																
																//this.printLine(this.isViewYn, "[8]");
																
																this.timestamp = new Timestamp(System.currentTimeMillis());
																this.setCraneMatrixProcessSetting(this.tmp_cranecode, this.getCraneCurPosMatrix(this.tmp_cranecode), this.sdf2.format(this.timestamp));
																this.setCraneMatrix(this.tmp_cranecode, 10, this.tmp_CRstatus);
																this.setCraneMatrix(this.tmp_cranecode, 11, this.tmp_weight);
																this.setSendSocket(this.tmp_cranecode);
															}
															else if( (this.dsCraneStateCdE).equals(this.tmp_CRstatus) )
															{
																// 용선 라인 크레인에서 재료를 전로에 넣고나면 영 -> 공 으로 Ladle 상태가 변할때 처리 로직
																this.tmp_dstlladnum = this.tmp_old_dstlladnum;
																
																//this.printLine(this.isViewYn, "[9]");
																
																this.timestamp = new Timestamp(System.currentTimeMillis());
																this.setCraneMatrixProcessSetting(this.tmp_cranecode, this.getCraneCurPosMatrix(this.tmp_cranecode), this.sdf2.format(this.timestamp));
																this.setCraneMatrix(this.tmp_cranecode, 10, this.tmp_CRstatus);
																this.setCraneMatrix(this.tmp_cranecode, 11, this.tmp_weight);
																this.setSendSocket(this.tmp_cranecode);
																
															}
															else if( (this.dsCraneStateCdN).equals(this.tmp_CRstatus) )
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
															this.tmp_CRstatus = this.tmp_old_CRstatus2;
															this.tmp_dstlladnum = this.tmp_old_dstlladnum;
															this.tmp_eqprfidtagid1 = this.tmp_old_eqprfidtagid1;
															this.tmp_bindingflag1 = this.tmp_old_bindingflag1;
															this.tmp_mtlno = this.tmp_old_mtlno;
														}

													}
													
												// UWB Crane Tag Data End
												/////////////////////////
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
												this.tmp_CRstatus = "";
												
												this.extend_1_DataInit();
												/////////////////////////////
												//Not Exist tagtype data End
											}
	
											if( "-".equals(this.tmp_craneline) ) this.tmp_craneline = "";
											if( "-".equals(this.tmp_cranecode) ) this.tmp_cranecode = "";
											
											this.tmp_ymd = this.arryTmp[15].trim();
											this.tmp_hms = this.arryTmp[16].trim();
											
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
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_CRstatus);        	//25.영Ladle(F), 공Ladle(E), 빈(N)
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_dstlladnum);		//26.수강Ladle번호
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_eqprfidtagid1);  	//27.설비RFID_TagID_1 (크레인에 실린 경우 (UWB)래들 Tag ID)
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_bindingflag1);		//28.BindingFlag_1 (Crane : 1 , Ladle : 2 , 사람   : 3, 기타 : 9)
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_weight );       	//29.수강Ladle중량 
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_mtlno);			//30.재료번호
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_rweight );       	//31 
											sbSubUwbTagData.append(","); sbSubUwbTagData.append(this.tmp_rmove);			//32
											
											hmIFMData.put(this.tmp_tagid, sbSubUwbTagData.toString());
											hmTmpIFMData.put(this.tmp_tagid, sbSubUwbTagData.toString());
											
											this.extend_1_DataInit();
									}
								}
								else if( dataCounter != 0 && ( dataCounter % (setDataCount+1) ) == 0 )
								{
									//this.printCraneMatrixCell();

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
						
						if( this.out_2 != null) this.out_2.close();
						if( this.socket_2 != null) this.socket_2.close();
					}
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
    
    public HashMap <String, String> getCrLdMappingProc_F(float paramPosX, float paramDistance)
    {
    	HashMap <String, String> hm = new HashMap<>();
    	
    	String strQuery = "";    	
    	String strParmVal = "";
    	
    	//this.setIFMLogFileWirte(this.isLogYn, 1, "[paramPosX : " + paramPosX +"][paramDistance : " + paramDistance + "]");
    	if( paramPosX >= this.fds1RHs && paramPosX <= this.fds1RHe  )
		{
			// 1RH
    		strParmVal = "1";
		}
    	else if( paramPosX >= this.fds2RHs && paramPosX <= this.fds2RHe  )
		{
			// 2RH
    		strParmVal = "2";
		}
    	else if( paramPosX >= this.fds3RHs && paramPosX <= this.fds3RHe  )
		{
			// 3RH
    		strParmVal = "3";
		}
    	else if( paramPosX >= this.fds1LFs && paramPosX <= this.fds1LFe  )
		{
			// LF
    		strParmVal = "1";
		}
    	else if( paramPosX >= this.fds1RCs && paramPosX <= this.fds1RCe  )
		{
    		// R대차, 회송대차
        	strParmVal = "";
		}
    	else if( paramPosX >= this.fds1Baps && paramPosX <= this.fds1Bape && paramDistance <= 8.00 )
		{
			// 1 BAP
    		strParmVal = "1";
		}
    	else if( paramPosX >= this.fds2Baps && paramPosX <= this.fds2Bape && paramDistance <= 8.00 )
		{
			// 2 BAP
    		strParmVal = "2";
		}
    	else if( paramPosX >= this.fds3Baps && paramPosX <= this.fds3Bape && paramDistance <= 8.00 )
		{
			// 3 BAP
    		strParmVal = "3";
		}

		hm.put("tmp_dstlladnum", "");
		hm.put("tmp_mtlno", "");
		hm.put("tmp_eqprfidtagid1", "");
		hm.put("tmp_bindingflag1", "");

    	return hm;
    }
    
    public HashMap <String, String> getCrLdMappingProc_E(float paramPosX, float paramDistance)
    {
    	HashMap <String, String> hm = new HashMap<>();
    	
    	String strQuery = "";    	
    	String strParmVal = "";
    	

    	if( paramPosX >= this.fds1MCs && paramPosX <= this.fds1MCe && paramDistance > 8.00 )
		{
			// 1 M/C
    		strParmVal = "1";
		}
    	else if( paramPosX >= this.fds2MCs && paramPosX <= this.fds2MCe && paramDistance > 8.00 )
		{
			// 2 M/C
    		strParmVal = "2";
		}
    	else if( paramPosX >= this.fds3MCs && paramPosX <= this.fds3MCe )
		{
			// 3 M/C
    		strParmVal = "3";
		}
    	else if( paramPosX >= this.fds4MCs && paramPosX <= this.fds4MCe )
		{
			// 4 M/C
    		strParmVal = "4";
		}
    	
		hm.put("tmp_dstlladnum", "");
		hm.put("tmp_mtlno", "");
		hm.put("tmp_eqprfidtagid1", "");
		hm.put("tmp_bindingflag1", "");

    	return hm;
    }
    
    public HashMap <String, String> getConvertCrLdMappingProc_F(float paramPosX, float paramDistance)
    {
    	HashMap <String, String> hm = new HashMap<>();

    	hm.put("tmp_dstlladnum", "");
		hm.put("tmp_mtlno", "");
		hm.put("tmp_eqprfidtagid1", "");
		hm.put("tmp_bindingflag1", "");

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
    
    private void setCurrentCraneLocationCheck(String CRCode, float paramPosX, float paramDistance)
    {
    	if( CRCode != null && !"".equals(CRCode) )
    	{
    		switch(CRCode)
    		{
    			case "1H" :
    				this.arryCrPos[0] = getCurrentCraneLocationCode(paramPosX, paramDistance); 
    				break;
    			case "2H" :
    				this.arryCrPos[1] = getCurrentCraneLocationCode(paramPosX, paramDistance); 
    				break;
    			case "1S" :
    				this.arryCrPos[2] = getCurrentCraneLocationCode(paramPosX, paramDistance); 
    				break;
    			case "1L" :
    				this.arryCrPos[3] = getCurrentCraneLocationCode(paramPosX, paramDistance); 
    				break;
    			case "2L" :
    				this.arryCrPos[4] = getCurrentCraneLocationCode(paramPosX, paramDistance); 
    				break;
    			case "1T" :
    				this.arryCrPos[5] = getCurrentCraneLocationCode(paramPosX, paramDistance); 
    				break;
    			case "2T" :
    				this.arryCrPos[6] = getCurrentCraneLocationCode(paramPosX, paramDistance); 
    				break;
    			case "3T" :
    				this.arryCrPos[7] = getCurrentCraneLocationCode(paramPosX, paramDistance); 
    				break;
    			case "4T" :
    				this.arryCrPos[8] = getCurrentCraneLocationCode(paramPosX, paramDistance); 
    				break;
    			default :
    				break;
    		}
    	}
    	
    	//this.timestamp = new Timestamp(System.currentTimeMillis());
    	//System.out.print("["+ this.sdf2.format(this.timestamp) +"] " + this.arryCrPos[0] + "_ " + this.arryCrPos[1] + " _" + this.arryCrPos[2] + " _ " + this.arryCrPos[3]);
    	//System.out.println(" _ " + this.arryCrPos[4] + " _ " + this.arryCrPos[5] + " _ " + this.arryCrPos[6] + " _ " + this.arryCrPos[7] + " _ " + this.arryCrPos[8]);
    }
    
    private String getCurrentCraneLocationCode(float paramPosX, float paramDistance)
	{
    	String sPosCode = "";
    	
    	if( this.fds1Baps <= paramPosX && paramPosX <= this.fds1Bape && paramDistance <= 8.00 )
    	{
    		sPosCode = this.ds1Bapcode;
    	}
    	else if( this.fds2Baps <= paramPosX && paramPosX <= this.fds2Bape && paramDistance <= 8.00 )
    	{
    		sPosCode = this.ds2Bapcode;
    	}
    	else if( this.fds3Baps <= paramPosX && paramPosX <= this.fds3Bape && paramDistance <= 8.00 )
    	{
    		sPosCode = this.ds3Bapcode;
    	}
    	else if( this.fds1RHs <= paramPosX && paramPosX <= this.fds1RHe )
    	{
    		sPosCode = this.ds1RHcode;
    	}
    	else if( this.fds2RHs <= paramPosX && paramPosX <= this.fds2RHe )
    	{
    		sPosCode = this.ds2RHcode;
    	}
    	else if( this.fds3RHs <= paramPosX && paramPosX <= this.fds3RHe )
    	{
    		sPosCode = this.ds3RHcode;
    	}
    	else if( this.fds1MCs <= paramPosX && paramPosX <= this.fds1MCe && paramDistance > 8.00 )
    	{
    		sPosCode = this.ds1MCcode;
    	}
    	else if( this.fds2MCs <= paramPosX && paramPosX <= this.fds2MCe && paramDistance > 8.00 )
    	{
    		sPosCode = this.ds2MCcode;
    	}
    	else if( this.fds3MCs <= paramPosX && paramPosX <= this.fds3MCe && paramDistance > 8.00 )
    	{
    		sPosCode = this.ds3MCcode;
    	}
    	else if( this.fds4MCs <= paramPosX && paramPosX <= this.fds4MCe && paramDistance > 8.00 )
    	{
    		sPosCode = this.ds4MCcode;
    	}
    	else if( this.fds1Bnrs <= paramPosX && paramPosX <= this.fds1Bnrs )
    	{
    		sPosCode = this.ds1Bnrcode;
    	}
    	else if( this.fds2Bnrs <= paramPosX && paramPosX <= this.fds2Bnrs )
    	{
    		sPosCode = this.ds2Bnrcode;
    	}
    	else if( this.fds3Bnrs <= paramPosX && paramPosX <= this.fds3Bnrs )
    	{
    		sPosCode = this.ds3Bnrcode;
    	}
    	else if( this.fds4Bnrs <= paramPosX && paramPosX <= this.fds4Bnrs )
    	{
    		sPosCode = this.ds4Bnrcode;
    	}
    	else if( this.fds5Bnrs <= paramPosX && paramPosX <= this.fds5Bnrs )
    	{
    		sPosCode = this.ds5Bnrcode;
    	}
    	else if( this.fds6Bnrs <= paramPosX && paramPosX <= this.fds6Bnrs )
    	{
    		sPosCode = this.ds6Bnrcode;
    	}
    	else if( this.fds7Bnrs <= paramPosX && paramPosX <= this.fds7Bnrs )
    	{
    		sPosCode = this.ds7Bnrcode;
    	}
    	else if( this.fds1Lrs <= paramPosX && paramPosX <= this.fds1Lre )
    	{
    		sPosCode = this.ds1Lrcode;
    	}
    	else if( this.fds2Lrs <= paramPosX && paramPosX <= this.fds2Lre )
    	{
    		sPosCode = this.ds2Lrcode;
    	}
    	else if( this.fds3Lrs <= paramPosX && paramPosX <= this.fds3Lre )
    	{
    		sPosCode = this.ds3Lrcode;
    	}
    	else if( this.fds4Lrs <= paramPosX && paramPosX <= this.fds4Lre )
    	{
    		sPosCode = this.ds4Lrcode;
    	}
    	else if( this.fds5Lrs <= paramPosX && paramPosX <= this.fds5Lre )
    	{
    		sPosCode = this.ds5Lrcode;
    	}
    	else if( this.fds6Lrs <= paramPosX && paramPosX <= this.fds6Lre )
    	{
    		sPosCode = this.ds6Lrcode;
    	}
    	else if( this.fds7Lrs <= paramPosX && paramPosX <= this.fds7Lre )
    	{
    		sPosCode = this.ds7Lrcode;
    	}
    	else if( this.fds1LFs <= paramPosX && paramPosX <= this.fds1LFe )
    	{
    		sPosCode = this.ds1LFcode;
    	}
    	else if( this.fds1RCs <= paramPosX && paramPosX <= this.fds1RCe )
    	{
    		sPosCode = this.ds1RCcode;
    	}
    	else if( this.fdsPosLeads <= paramPosX && paramPosX <= this.fdsPosLeade )
    	{
    		sPosCode = this.dsPosLeadcode;
    	}
    	else
    	{
    		sPosCode = "";
    	}

    	return sPosCode;
	}
    
    /*
     * 크레인 PC에 보내는 데이터 값 저장하기
     */
    private void setCraneMatrix(String CraneCode, int iSecond, String sValue)
    {
    	int iFirst = -1;
    	
    	if( CraneCode != null && !"".equals(CraneCode) )
    	{
    		if( "1H".equals(CraneCode) )		iFirst = 0;    		
    		else if( "2H".equals(CraneCode) )	iFirst = 1;    		
    		else if( "1S".equals(CraneCode) )	iFirst = 2;    		
    		else if( "1L".equals(CraneCode) )	iFirst = 3;    		
    		else if( "2L".equals(CraneCode) )	iFirst = 4;    		
    		else if( "1T".equals(CraneCode) )	iFirst = 5;    		
    		else if( "2T".equals(CraneCode) )	iFirst = 6;    		
    		else if( "3T".equals(CraneCode) )	iFirst = 7;    		
    		else if( "4T".equals(CraneCode) )	iFirst = 8;
    		
    		if( -1 < iFirst && iFirst < 9 )
    		{
    			if( iSecond < 16)
    			{
    				this.arryTmpCraneInfo[iFirst][iSecond] = sValue;
    			}
    		}
    	}
    }
    
    /*
     * 크레인 PC에 보내는 데이터 값 초기화
     */
    private void setCraneMatrixAllInit()
    {
    	for(int i=0 ; i<9 ; i++)
    	{
    		for(int x=1 ; x<16 ; x++)
    		{
    			if( x < 16 )
    			{
    				this.arryTmpCraneInfo[i][x] = "";
    			}
    			else if( x >= 16 )
    			{
    				this.arryTmpCraneInfo[i][x] = "0";
    			}
    		}
    	}
    	
    }
    
    private void setCraneMatrixCellInit(String CraneCode)
    {
    	int iFirst = -1;
    	if( CraneCode != null && !"".equals(CraneCode) )
    	{
    		if( "1H".equals(CraneCode) )		iFirst = 0;    		
    		else if( "2H".equals(CraneCode) )	iFirst = 1;    		
    		else if( "1S".equals(CraneCode) )	iFirst = 2;    		
    		else if( "1L".equals(CraneCode) )	iFirst = 3;    		
    		else if( "2L".equals(CraneCode) )	iFirst = 4;    		
    		else if( "1T".equals(CraneCode) )	iFirst = 5;    		
    		else if( "2T".equals(CraneCode) )	iFirst = 6;    		
    		else if( "3T".equals(CraneCode) )	iFirst = 7;    		
    		else if( "4T".equals(CraneCode) )	iFirst = 8;
    		
    		if( -1 < iFirst && iFirst < 9 )
    		{
    			for(int i=1 ; i<16 ; i++)
    			{
    				this.arryTmpCraneInfo[iFirst][i] = "";
    			}
    			this.arryTmpCraneInfo[iFirst][16] = "0";
    		}
    	}
    }
    
    private void printCraneMatrixCell()
    {
    	for(int i=0 ; i<9 ; i++ )
    	{
			for(int x=0 ; x < 17 ; x++)
			{
				System.out.print("___ ");
				System.out.print( this.arryTmpCraneInfo[i][x] );
			}
			System.out.println("___");
    	}
    }
    
    /*
     * 크레인 PC에 보내는 데이터 값 가져오기
     */
    private String getCraneMatrix(String CraneCode, int iSecond)
    {
    	String sRet = "";
    	
    	int iFirst = -1;
    	if( CraneCode != null && !"".equals(CraneCode) )
    	{
    		if( "1H".equals(CraneCode) )		iFirst = 0;    		
    		else if( "2H".equals(CraneCode) )	iFirst = 1;    		
    		else if( "1S".equals(CraneCode) )	iFirst = 2;    		
    		else if( "1L".equals(CraneCode) )	iFirst = 3;    		
    		else if( "2L".equals(CraneCode) )	iFirst = 4;    		
    		else if( "1T".equals(CraneCode) )	iFirst = 5;    		
    		else if( "2T".equals(CraneCode) )	iFirst = 6;    		
    		else if( "3T".equals(CraneCode) )	iFirst = 7;    		
    		else if( "4T".equals(CraneCode) )	iFirst = 8;
    		
    		if( -1 < iFirst && iFirst < 9 )
    		{
    			if( iSecond < 17)
    			{
    				sRet = this.arryTmpCraneInfo[iFirst][iSecond];
    				
    				// 전송 데이터 임시 저장 배열 
    		    	// 0 : 크레인구분, 1 : 출발공정코드, 2 : 도착공정코드, 3 : 출발일시, 4 : 도착일시, 5 : 이송시간 (출발->도착 시간 분으로)
    		    	// 6 : 권상일시(개시), 7 : 권하일시(개시), 8 : 권상시간(종료), 9 : 권하시간(종료), 
    		    	// 10 : 제강공 Ladle 구분 (N, E, F)
    		    	// 11 : 측정중량, 12 : 잔탕구분(Y/N), 13 : 배재구분(Y/N), 14 : 터렛도착구분(Y/N), 
    		    	// 15 : 크레인방향 (1 N->E/F, 2 E/F->N ), 16 : 데이터완료상태 (0 미완료, 1 완료)
    			}
    		}
    	}
    	
    	return sRet;
    }
    
    /*
     * 크레인이 현재 공장에 있는 위치값 가져오기
     */
    private String getCraneCurPosMatrix(String CraneCode)
    {
    	String sRet = "";
    	
    	int iFirst = -1;
    	if( CraneCode != null && !"".equals(CraneCode) )
    	{
    		if( "1H".equals(CraneCode) )		iFirst = 0;    		
    		else if( "2H".equals(CraneCode) )	iFirst = 1;    		
    		else if( "1S".equals(CraneCode) )	iFirst = 2;    		
    		else if( "1L".equals(CraneCode) )	iFirst = 3;    		
    		else if( "2L".equals(CraneCode) )	iFirst = 4;    		
    		else if( "1T".equals(CraneCode) )	iFirst = 5;    		
    		else if( "2T".equals(CraneCode) )	iFirst = 6;    		
    		else if( "3T".equals(CraneCode) )	iFirst = 7;    		
    		else if( "4T".equals(CraneCode) )	iFirst = 8;
    		
    		if( -1 < iFirst && iFirst < 9 )
    		{
    			// 크레인 현재 위치
    			// 0 : H1, 1 : H2, 2 : S1, 3 : L1, 4 : L2, 
    			// 5 : T1, 6 : T2, 7 : T3, 8 : T4
    			
   				sRet = this.arryCrPos[iFirst];
    		}
    	}
    	
    	return sRet;
    }
    
    /*
     * 크레인에 공정과 일시 셋팅
     */
    private void setCraneMatrixProcessSetting(String CraneCode, String sProcess, String ymd)
    {
    	String startProcess = "";
    	String startymd = "";
    	String endProcess = "";
    	String endymd = "";
    	String endStatus = "";

    	int iFirst = -1;
    	if( CraneCode != null && !"".equals(CraneCode) )
    	{
    		if( "1H".equals(CraneCode) )		iFirst = 0;    		
    		else if( "2H".equals(CraneCode) )	iFirst = 1;    		
    		else if( "1S".equals(CraneCode) )	iFirst = 2;    		
    		else if( "1L".equals(CraneCode) )	iFirst = 3;    		
    		else if( "2L".equals(CraneCode) )	iFirst = 4;    		
    		else if( "1T".equals(CraneCode) )	iFirst = 5;    		
    		else if( "2T".equals(CraneCode) )	iFirst = 6;    		
    		else if( "3T".equals(CraneCode) )	iFirst = 7;    		
    		else if( "4T".equals(CraneCode) )	iFirst = 8;
    		
    		if( -1 < iFirst && iFirst < 9 )
    		{
    			startProcess = this.arryTmpCraneInfo[iFirst][1];
    			startymd = this.arryTmpCraneInfo[iFirst][3];
    			endProcess = this.arryTmpCraneInfo[iFirst][2];
    			endymd = this.arryTmpCraneInfo[iFirst][4];
    			endStatus = this.arryTmpCraneInfo[iFirst][16];
    			
    			if( "".equals(startymd) && "".equals(endymd) )
    			{
    				this.arryTmpCraneInfo[iFirst][1] = sProcess;
    				this.arryTmpCraneInfo[iFirst][3] = ymd;

    				if( 
    					"C1".equals(this.arryTmpCraneInfo[iFirst][1])
    					|| "C2".equals(this.arryTmpCraneInfo[iFirst][1])
    					|| "C3".equals(this.arryTmpCraneInfo[iFirst][1])
    					|| "C4".equals(this.arryTmpCraneInfo[iFirst][1])
    				)
    				{
    					this.arryTmpCraneInfo[iFirst][12] = "Y";
    				}
    				else if( "P1".equals(this.arryTmpCraneInfo[iFirst][1]) )
        			{
        				this.arryTmpCraneInfo[iFirst][13] = "Y";
        			}
    				else
    				{
    					this.arryTmpCraneInfo[iFirst][12] = "N";
    					this.arryTmpCraneInfo[iFirst][13] = "N";
    				}
    				
    				this.arryTmpCraneInfo[iFirst][16] = "0"; //미완료
    			}
    			else if( !"".equals(startymd) && "".equals(endymd) )
    			{
    				this.arryTmpCraneInfo[iFirst][2] = sProcess;
    				this.arryTmpCraneInfo[iFirst][4] = ymd;
    				
    				if( 
    					"C1".equals(this.arryTmpCraneInfo[iFirst][2])
    					|| "C2".equals(this.arryTmpCraneInfo[iFirst][2])
    					|| "C3".equals(this.arryTmpCraneInfo[iFirst][2])
    					|| "C4".equals(this.arryTmpCraneInfo[iFirst][2])
    				)
        			{
        					this.arryTmpCraneInfo[iFirst][14] = "Y";
        			}
    				else
    				{
    					this.arryTmpCraneInfo[iFirst][14] = "N";
    				}
    				
    				this.arryTmpCraneInfo[iFirst][16] = "1"; //완료
    			}
    			else if( !"".equals(startymd) && !"".equals(endymd) )
    			{
    				if( "0".equals(endStatus) )
    				{
    					this.arryTmpCraneInfo[iFirst][2] = sProcess;
        				this.arryTmpCraneInfo[iFirst][4] = ymd;
        				this.arryTmpCraneInfo[iFirst][16] = "1"; //완료
        				
        				if( 
    						"C1".equals(this.arryTmpCraneInfo[iFirst][2])
        					|| "C2".equals(this.arryTmpCraneInfo[iFirst][2])
        					|| "C3".equals(this.arryTmpCraneInfo[iFirst][2])
        					|| "C4".equals(this.arryTmpCraneInfo[iFirst][2])
        				)
            			{
            					this.arryTmpCraneInfo[iFirst][14] = "Y";
            			}
        				else
        				{
        					this.arryTmpCraneInfo[iFirst][14] = "N";
        				}
    				}
    				else if( "1".equals(endStatus) )
    				{
    					this.arryTmpCraneInfo[iFirst][1] = this.arryTmpCraneInfo[iFirst][2];
    					this.arryTmpCraneInfo[iFirst][2] = sProcess;
    					
    					this.arryTmpCraneInfo[iFirst][3] = this.arryTmpCraneInfo[iFirst][4];
        				this.arryTmpCraneInfo[iFirst][4] = ymd;
        				
        				if( 
        					"C1".equals(this.arryTmpCraneInfo[iFirst][1])
        					|| "C2".equals(this.arryTmpCraneInfo[iFirst][1])
        					|| "C3".equals(this.arryTmpCraneInfo[iFirst][1])
        					|| "C4".equals(this.arryTmpCraneInfo[iFirst][1])
        				)
        				{
        					this.arryTmpCraneInfo[iFirst][12] = "Y";
        				}
        				else if( "P1".equals(this.arryTmpCraneInfo[iFirst][1]) )
            			{
            				this.arryTmpCraneInfo[iFirst][13] = "Y";
            			}
        				else
        				{
        					this.arryTmpCraneInfo[iFirst][12] = "N";
        					this.arryTmpCraneInfo[iFirst][13] = "N";
        				}
        				
        				if( 
    						"C1".equals(this.arryTmpCraneInfo[iFirst][2])
        					|| "C2".equals(this.arryTmpCraneInfo[iFirst][2])
        					|| "C3".equals(this.arryTmpCraneInfo[iFirst][2])
        					|| "C4".equals(this.arryTmpCraneInfo[iFirst][2])
        				)
            			{
            					this.arryTmpCraneInfo[iFirst][14] = "Y";
            			}
        				else
        				{
        					this.arryTmpCraneInfo[iFirst][14] = "N";
        				}
        				
    					this.arryTmpCraneInfo[iFirst][16] = "1"; //완료
    				}
    			}
    		}
    	}
    }
    
    
    private void setSendSocket(String CraneCode)
    {
    	
    	this.printLine(this.isViewYn, "setSendSocket....Start");
    	
    	int iFirst = -1;
    	if( CraneCode != null && !"".equals(CraneCode) )
    	{
    		if( "1H".equals(CraneCode) )		iFirst = 0;    		
    		else if( "2H".equals(CraneCode) )	iFirst = 1;    		
    		else if( "1S".equals(CraneCode) )	iFirst = 2;    		
    		else if( "1L".equals(CraneCode) )	iFirst = 3;    		
    		else if( "2L".equals(CraneCode) )	iFirst = 4;    		
    		else if( "1T".equals(CraneCode) )	iFirst = 5;    		
    		else if( "2T".equals(CraneCode) )	iFirst = 6;    		
    		else if( "3T".equals(CraneCode) )	iFirst = 7;    		
    		else if( "4T".equals(CraneCode) )	iFirst = 8;
    		
    		if( -1 < iFirst && iFirst < 9 )
    		{
    			this.printCraneMatrixCell();
    			this.printLine(this.isViewYn, "this.arryTmpCraneInfo["+ iFirst +"][16] >> " + this.arryTmpCraneInfo[iFirst][16]);

    			if( "1".equals( this.arryTmpCraneInfo[iFirst][16] ) )
    			{
    				if( 
    					!"".equals( this.arryTmpCraneInfo[iFirst][1] )
    					&& !"".equals( this.arryTmpCraneInfo[iFirst][2] )
    					&& !"".equals( this.arryTmpCraneInfo[iFirst][3] )
    					&& !"".equals( this.arryTmpCraneInfo[iFirst][4] )
    					&& !(this.arryTmpCraneInfo[iFirst][1]).equals( this.arryTmpCraneInfo[iFirst][2] )
    				)
    				{
    					/*
    					String strDate1 = "20230512150510";
						String strDate2 = "20230512150713";

						Date date01 = jt.dateConv(strDate1);
						Date date02 = jt.dateConv(strDate2);

						System.out.println( jt.calDateConv(date01, strDate1, 10) );
						System.out.println( jt.calDateConv(date01, strDate1, -10) );

    					long diffMin = (date02.getTime() - date01.getTime()) / 60000; //분 차이
    					 */
    					
    					Date date01 = this.dateConv(this.arryTmpCraneInfo[iFirst][3]); //출발
						Date date02 = this.dateConv(this.arryTmpCraneInfo[iFirst][4]); //도착
						
						//제강 크레인 이송시간 (분으로 계산)
						long diffMin = (date02.getTime() - date01.getTime()) / 60000; 
						this.arryTmpCraneInfo[iFirst][5] = String.valueOf( diffMin );
						
						//출발
						this.arryTmpCraneInfo[iFirst][7] = this.calDateConv(date01, this.arryTmpCraneInfo[iFirst][3], -85);  //래들 올리기 위해 내린다
						this.arryTmpCraneInfo[iFirst][6] = this.calDateConv(date01, this.arryTmpCraneInfo[iFirst][3], 25); //래들을 들어 올린다
						
						//도착
						this.arryTmpCraneInfo[iFirst][9] = this.calDateConv(date02, this.arryTmpCraneInfo[iFirst][4], -98);  //래들 내린다
						this.arryTmpCraneInfo[iFirst][8] = this.calDateConv(date02, this.arryTmpCraneInfo[iFirst][4], 25); //후크를 올린다
										
						
						//현재 PC 서버에 전송하는 데이터가 NUMBER(4)로 되어 있어, 소수점 절삭을 시킴 
						//래들 중량 : this.arryTmpCraneInfo[iFirst][11]
						
						if( this.arryTmpCraneInfo[iFirst][11] != null && !"".equals( this.arryTmpCraneInfo[iFirst][11] ) )
						{
							if( this.arryTmpCraneInfo[iFirst][11].length() < 8 )
								this.arryTmpCraneInfo[iFirst][11] = String.valueOf( (int) Float.parseFloat( this.arryTmpCraneInfo[iFirst][11] ) );
							else
								this.arryTmpCraneInfo[iFirst][11] = "";
						}
						
						///////////////////////////////
						
		    			StringBuffer sendSb = new StringBuffer(30);
		    			//sendSb.append("BOF2CRANE10000");
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][0]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][1]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][2]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][3]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][4]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][5]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][6]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][7]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][8]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][9]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][10]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][11]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][12]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][13]);
		    	    	sendSb.append(",");
		    	    	sendSb.append(this.arryTmpCraneInfo[iFirst][14]);
		    	    	
		    	    	int sendDataLen = sendSb.toString().length() + 14;
		    	    	String sendHeader = "BOF2CRANE1" + String.format("%04d", sendDataLen);
		    	    	
		    	    	StringBuffer sendFinalSb = new StringBuffer(30);
		    	    	sendFinalSb.append(sendHeader);
		    	    	sendFinalSb.append(sendSb.toString());
		    	    	
		    	    	
		    	    	try
		    	    	{
							this.socket = new Socket(this.Poscoict_Svr_Ip, this.Poscoict_Svr_Port);
							this.socket_2 = new Socket(this.Poscoict_Svr_Ip_2, this.Poscoict_Svr_Port_2);
		    	    		
		    	    		this.printLine(this.isViewYn, "[Sending...(1) "+ this.Poscoict_Svr_Ip +":"+ this.Poscoict_Svr_Port +"] >>>> " + sendFinalSb.toString() );
		    	    		
		    			    this.out = this.socket.getOutputStream();
//		    				byte[] sendByte = this.concat( this.convertIntToBytes( (sendSb.toString().length() + 4) ), (sendSb.toString()).getBytes() );
		    				this.out.write( sendFinalSb.toString().getBytes(), 0, sendFinalSb.toString().getBytes().length );
		    				this.out.flush();
		    			
		    				this.printLine(this.isViewYn, "[Sending...(2) "+ this.Poscoict_Svr_Ip_2 +":"+ this.Poscoict_Svr_Port_2 +"] >>>> " + sendFinalSb.toString() );
		    				
		    				this.out_2 = this.socket_2.getOutputStream();
//		    				byte[] sendByte_2 = this.concat( this.convertIntToBytes( (sendSb.toString().length() + 4) ), (sendSb.toString()).getBytes() );
		    				this.out_2.write( sendFinalSb.toString().getBytes(), 0, sendFinalSb.toString().getBytes().length );
		    				this.out_2.flush();
		    	    	}
		    	    	catch(Exception e)
		    	    	{
		    	    		e.printStackTrace();
		    	    	}
	    				finally
	    				{
	    					try
	    					{
    							if( this.out != null) this.out.close();
    							if( this.socket != null) this.socket.close();
    							
    							if( this.out_2 != null) this.out_2.close();
    							if( this.socket_2 != null) this.socket_2.close();
	    					}
	    					catch(Exception e)
	    					{
	    						e.printStackTrace();
	    					}
	    				}
		    	    	
		    	    	
		    	    	this.arryTmpCraneInfo[iFirst][1] = this.arryTmpCraneInfo[iFirst][3];
						this.arryTmpCraneInfo[iFirst][2] = this.arryTmpCraneInfo[iFirst][4];
						this.arryTmpCraneInfo[iFirst][3] = "";
	    				this.arryTmpCraneInfo[iFirst][4] = "";
		    	    	this.arryTmpCraneInfo[iFirst][16] = "0";
    				}
    			}
    		}
    	}
    	
    	this.printLine(this.isViewYn, "setSendSocket....End");
    }    
    
    private Date dateConv(String parmDate)
	{
		Date newDate = null;
		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try
		{
			newDate = dtFormat.parse(parmDate);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return newDate;
	}

	

    private String calDateConv(Date parmDate, String paramDate, int addVal)
	{
		String ret = "";
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyyMMddHHmmss");
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(parmDate);

			cal.add(Calendar.SECOND, addVal);
			ret = sdfYMDHMS.format(cal.getTime());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

    public static void main(String args[])
	{
		ExecutorService crunchifyExecutor = Executors.newFixedThreadPool(4);
		// Replace username with your real value

		IFUwbToPcRtlsIClientDual ifm = new IFUwbToPcRtlsIClientDual(150);
		
		// Start running log file tailer on crunchify.log file
		crunchifyExecutor.execute(ifm);		
	}
}
