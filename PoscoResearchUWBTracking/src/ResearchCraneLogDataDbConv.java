import java.io.BufferedWriter;
import java.io.File;
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


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/*
 *   /usr/local/java/jdk1.8.0_321/jre/bin/java IFMUwbRtlsIClient
 *   
 *   192.168.1.12:3306, 192.168.1.13:3306  두군데 전부 데이터 등록
 *   mysql database table에 데이터 insert : 테이블명 ucubedb.craneinfo
 *   
 *   설치 서버 정보 : 10.30.92.51
 *   설치 폴더 : /usr/local/posco/uwbpos/ResearchCraneLogDataDbConv
 *   사용하는곳 : 크레인관제 알고리즘
 */

public class ResearchCraneLogDataDbConv implements Runnable 
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
	
	private static int isViewYn = 1;
	
	private String dsLogBackupPath = "";
	
	
	////////////////////////////////////////////////////////////////////
	private String resource = "/usr/local/posco/uwbpos/ResearchCraneLogDataDbConv/set.research.properties";
	private Properties properties = new Properties();
	////////////////////////////////////////////////////////////////////

	SimpleDateFormat sdf1 = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
	SimpleDateFormat sdf2 = new SimpleDateFormat ("yyyyMMddHHmmss");
	
	SimpleDateFormat sdfYYYY = new SimpleDateFormat ("yyyy");
	SimpleDateFormat sdfMM = new SimpleDateFormat ("MM");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat ("yyyyMMdd");
	
	Timestamp  timestamp = new Timestamp(System.currentTimeMillis());
	

	////////////////////////////////////////////////////////////////////////
	//Posco Research Svr #1 uCubeDB DB
	private String jdbc_driver_posco1_ucubedb = "";
	private String jdbc_url_posco1_ucubedb = "";
	private String db_id_posco1_ucubedb = "";
	private String db_pw_posco1_ucubedb = "";
	
	//Posco Research Svr #2 uCubeDB DB
	private String jdbc_driver_posco2_ucubedb = "";
	private String jdbc_url_posco2_ucubedb = "";
	private String db_id_posco2_ucubedb = "";
	private String db_pw_posco2_ucubedb = "";
    ////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	    
	private String [] arryTmp = null; 
	private int arrLen = 0;
	
	private String tmp_old_status = "";
	
	private int iArryFPos = 0;
	private float tmp_fx = 0;
	private float tmp_fy = 0;
	
	private String strNew = null;
	private String sLine = null;
	
	private int iRet = -1;
	private int fileLineCnt = 0;
	private int textLength = 0;
	
	private String [][] mtData = {
	{"","","","","","","","","",""}
	, {"","","","","","","","","",""}
	, {"","","","","","","","","",""}
	, {"","","","","","","","","",""}
	, {"","","","","","","","","",""}
	, {"","","","","","","","","",""}
	, {"","","","","","","","","",""}
	, {"","","","","","","","","",""}
	, {"","","","","","","","","",""}
	};
	
	private String tmp_ymd = "";          // 0
	private String tmp_tagid = "";        // 1
	private String tmp_tagtype = "";      // 2
	private String tmp_cranecode = "";    // 3
	private String tmp_x = "";            // 4
	private String tmp_y_distance = "";   // 5
	private String tmp_z1_hoist = "";     // 6
	private String tmp_z2_sub = "";       // 7
	private String tmp_status = "";       // 8
	private String tmp_ladleno = "";      // 9
	private String tmp_weight = "";       // 12
	private String tmp_mtlno = "";        // 13
	private String tmp_poscode = "";        // 14
	
	private StringBuffer retSb = new StringBuffer(10);
	
	////////////////////////////////////////////////////////////////////////
	
	public ResearchCraneLogDataDbConv()
	{
		
	}
	
	public ResearchCraneLogDataDbConv(int myInterval) 
	{
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.curYYYY = this.sdfYYYY.format(this.timestamp);
		this.curMM = this.sdfMM.format(this.timestamp);
		this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
		
		try
		{
			FileInputStream fis = new FileInputStream(this.resource);
	    	this.properties.load(fis);
	    	
        	//Posco Research Svr #1 uCubeDB DB
        	this.jdbc_driver_posco1_ucubedb = properties.getProperty("datasource.posco.1.ucubedb.db.driver");
        	this.jdbc_url_posco1_ucubedb = properties.getProperty("datasource.posco.1.ucubedb.db.url");
        	this.db_id_posco1_ucubedb = properties.getProperty("datasource.posco.1.ucubedb.db.id");
        	this.db_pw_posco1_ucubedb = properties.getProperty("datasource.posco.1.ucubedb.db.pw");
        	
        	//Posco Research Svr #2 uCubeDB DB
        	this.jdbc_driver_posco2_ucubedb = properties.getProperty("datasource.posco.2.ucubedb.db.driver");
        	this.jdbc_url_posco2_ucubedb = properties.getProperty("datasource.posco.2.ucubedb.db.url");
        	this.db_id_posco2_ucubedb = properties.getProperty("datasource.posco.2.ucubedb.db.id");
        	this.db_pw_posco2_ucubedb = properties.getProperty("datasource.posco.2.ucubedb.db.pw");

	    	//log file path
	    	this.dsLogBackupPath = properties.getProperty("datasource.log.backup.path");

	    	String myFile = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +"_ifm_crane_log.txt";
			this.crunchifyFile = new File(myFile);
			this.crunchifyRunEveryNSeconds = myInterval;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ResearchCraneLogDataDbConv(String myFile, int myInterval) 
	{
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.curYYYY = this.sdfYYYY.format(this.timestamp);
		this.curMM = this.sdfMM.format(this.timestamp);
		this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
		
		myFile = myFile + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +"_ifm_crane_log.txt";
		
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
    		Connection con_posco1_ucubedb = null;
    		Connection con_posco2_ucubedb = null;
        	PreparedStatement ps = null;

        	String myFile = "";
			String oldMyFile = "";
			
			this.timestamp = new Timestamp(System.currentTimeMillis());
			this.curYYYY = this.sdfYYYY.format(this.timestamp);
			this.curMM = this.sdfMM.format(this.timestamp);
			this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
			
			myFile = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +"_ifm_crane_log.txt";
			oldMyFile = myFile;
        	
			this.printLine(this.isViewYn, "[1] File Path : " + this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +"_ifm_crane_log.txt");
			
			try 
			{
				Class.forName(this.jdbc_driver_posco1_ucubedb);
				con_posco1_ucubedb = DriverManager.getConnection(this.jdbc_url_posco1_ucubedb, this.db_id_posco1_ucubedb, this.db_pw_posco1_ucubedb);
        		con_posco2_ucubedb = DriverManager.getConnection(this.jdbc_url_posco2_ucubedb, this.db_id_posco2_ucubedb, this.db_pw_posco2_ucubedb);
        		
        		this.printLine(this.isViewYn, "[2] DB Connect : " + this.jdbc_driver_posco1_ucubedb);
        		
        		StringBuffer sbquery = new StringBuffer(10);
        		sbquery.append(" INSERT INTO craneinfo ");
        		sbquery.append(" ( ymd, cranecode, x, y, z1, z2, ldstatus, ldno, ldweight, mtlno, poscode ) ");
        		sbquery.append(" VALUES ");
        		sbquery.append(" ( ");
        		sbquery.append("   ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
        		sbquery.append(" ) ");

				while (shouldIRun) 
				{	
					Thread.sleep(crunchifyRunEveryNSeconds);
				
					this.timestamp = new Timestamp(System.currentTimeMillis());
					this.curYYYY = this.sdfYYYY.format(this.timestamp);
					this.curMM = this.sdfMM.format(this.timestamp);
					this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
					
					myFile = this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +"_ifm_crane_log.txt";
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
							this.lastKnownPosition = fileLength -1;
						}
						
						//this.printLine(this.isViewYn, "[3] File Info : [fileLength : "+ fileLength +"] [this.lastKnownPosition : "+ this.lastKnownPosition +"]");
	
						// Reading and writing file
						RandomAccessFile readWriteFileAccess = new RandomAccessFile(this.crunchifyFile, "r");
						readWriteFileAccess.seek(this.lastKnownPosition);
						String sDataLine = null;
						
						while ((sDataLine = readWriteFileAccess.readLine()) != null) 
						{
							if( sDataLine != null && !"".equals(sDataLine) )
							{
								this.strNew = sDataLine;
								this.textLength = this.strNew.length();
								
								if( this.textLength > 14)
								{
										this.strNew = this.strNew.substring(1, textLength);
										this.strNew =  this.strNew.replaceAll("] >> ,", ",");
								    	
										this.arryTmp = this.strNew.split(",");
										this.arrLen = this.arryTmp.length;
										
										this.tmp_ymd = this.arryTmp[0].trim();          // 0
										this.tmp_tagid = this.arryTmp[1].trim();        // 1
										this.tmp_tagtype = this.arryTmp[2].trim();      // 2
										this.tmp_cranecode = this.arryTmp[3].trim();    // 3
										this.tmp_x = this.arryTmp[4].trim();            // 4
										this.tmp_y_distance = this.arryTmp[5].trim();   // 5
										this.tmp_z1_hoist = this.arryTmp[6].trim();     // 6
										this.tmp_z2_sub = this.arryTmp[7].trim();       // 7
										this.tmp_status = this.arryTmp[8].trim();       // 8
										this.tmp_ladleno = this.arryTmp[9].trim();      // 9
										this.tmp_weight = this.arryTmp[12].trim();       // 12
							    		
							    		if( this.arrLen == 14 )
							    		{
							    			this.tmp_mtlno = this.arryTmp[13].trim();        // 13
							    		}
							    		else
							    		{
							    			this.tmp_mtlno = "";        // 13
							    		}
							    		
							    		this.tmp_poscode = "";
							    		
							    		
							    		if( "1".equals(this.tmp_tagtype) && (0 <= this.getCraneArryPos(this.tmp_cranecode) && this.getCraneArryPos(this.tmp_cranecode) <= 8 ) )
							    		{
							    			this.iArryFPos = this.getCraneArryPos(this.tmp_cranecode);
							    			
							    			this.tmp_old_status = this.mtData[iArryFPos][6]; 
							    			
							    			this.mtData[iArryFPos][0] = this.tmp_ymd;
							    			this.mtData[iArryFPos][1] = this.tmp_cranecode;
							    			this.mtData[iArryFPos][2] = this.tmp_x;
							    			this.mtData[iArryFPos][3] = this.tmp_y_distance;
							    			this.mtData[iArryFPos][4] = this.tmp_z1_hoist;
							    			this.mtData[iArryFPos][5] = this.tmp_z2_sub;
							    			this.mtData[iArryFPos][6] = this.tmp_status;
							    			this.mtData[iArryFPos][7] = this.tmp_ladleno; 
							    			this.mtData[iArryFPos][8] = this.tmp_weight;
							    			this.mtData[iArryFPos][9] = this.tmp_mtlno;
							    			
		
							    			if( 
							    					this.tmp_old_status != null && !"".equals(this.tmp_old_status) && this.tmp_status != null 
							    					&& !"".equals(this.tmp_status) && !this.tmp_status.equals(this.tmp_old_status) 
							    				)
							    			{
							    				this.tmp_fx = Float.parseFloat(this.tmp_x);
							    				this.tmp_fy = Float.parseFloat(this.tmp_y_distance);
		
							    				this.tmp_poscode = this.getPosXCode(tmp_fx, tmp_fy);
							    			}
		
							    			//Research RTLS UWB DB Server #1 Insert
							    			ps = con_posco1_ucubedb.prepareStatement( sbquery.toString() );
							    			ps.setString(1,  this.tmp_ymd);
							    			ps.setString(2,  this.tmp_cranecode);
							    			ps.setString(3,  this.tmp_x);
							    			ps.setString(4,  this.tmp_y_distance);
							    			ps.setString(5,  this.tmp_z1_hoist);
							    			ps.setString(6,  this.tmp_z2_sub);
							    			ps.setString(7,  this.tmp_status);
							    			ps.setString(8,  this.tmp_ladleno);
							    			ps.setString(9,  this.tmp_weight);
							    			ps.setString(10, this.tmp_mtlno);
							    			ps.setString(11, this.tmp_poscode);
							    			this.iRet = ps.executeUpdate();
							    			ps.clearParameters();
							    			if( ps != null ) ps.close();
		
							    			//Research RTLS UWB DB Server #2 Insert
							    			ps = con_posco2_ucubedb.prepareStatement( sbquery.toString() );
							    			ps.setString(1,  this.tmp_ymd);
							    			ps.setString(2,  this.tmp_cranecode);
							    			ps.setString(3,  this.tmp_x);
							    			ps.setString(4,  this.tmp_y_distance);
							    			ps.setString(5,  this.tmp_z1_hoist);
							    			ps.setString(6,  this.tmp_z2_sub);
							    			ps.setString(7,  this.tmp_status);
							    			ps.setString(8,  this.tmp_ladleno);
							    			ps.setString(9,  this.tmp_weight);
							    			ps.setString(10, this.tmp_mtlno);
							    			ps.setString(11, this.tmp_poscode);
							    			this.iRet = ps.executeUpdate();
							    			ps.clearParameters();
							    			if( ps != null ) ps.close();
							    			
							    			this.retSb = new StringBuffer(10);
							    			this.retSb.append(this.tmp_ymd);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_cranecode);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_x);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_y_distance);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_z1_hoist);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_z2_sub);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_status);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_ladleno);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_weight);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_mtlno);
							    			this.retSb.append(", "); this.retSb.append(this.tmp_poscode);
							    			
							    			this.printLine(this.isViewYn, "[Craneinfo Insert] " + this.retSb.toString() );
							    		}
								}
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
				System.out.println(" Crane Log Data File refused. [IOException -  ["+ e.toString() +"]");
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
	        		if( ps != null ) ps.close();
	        		if( con_posco1_ucubedb != null ) con_posco1_ucubedb.close();
	        		if( con_posco2_ucubedb != null ) con_posco2_ucubedb.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			try
			{
				this.printLine(this.isViewYn, "[1] Retry Crane Log Data File connection.");
				Thread.sleep(300);
				
				this.printLine(this.isViewYn, "[2] Retry Crane Log Data File connection.");
				//전송 중 접속이 끊겼을때 재접속을 시도하면서 위치데이터 마지막 부분부터 가져온다.
				this.crunchifyFile = new File(this.dsLogBackupPath + "/" + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +"_ifm_crane_log.txt");
				long fileLength = this.crunchifyFile.length();
				this.lastKnownPosition = fileLength - 1;
			}
			catch(Exception e)
			{
				System.out.println(" Crane Log Data File refused. ["+ e.toString() +"]");
			}
    	}

		if (debug)
		{
			this.printLine(this.isViewYn, "Exit the program...");
		}
	}
	
	
	public int getCraneArryPos(String CrCd)
	{
		int iPos = -1;
		
		if( "1H".equals(CrCd) )				iPos = 0;
		else if( "2H".equals(CrCd) )		iPos = 1;
		else if( "1S".equals(CrCd) )		iPos = 2;
		else if( "1L".equals(CrCd) )		iPos = 3;
		else if( "2L".equals(CrCd) )		iPos = 4;
		else if( "1T".equals(CrCd) )		iPos = 5;
		else if( "2T".equals(CrCd) )		iPos = 6;
		else if( "3T".equals(CrCd) )		iPos = 7;
		else if( "4T".equals(CrCd) )		iPos = 8;

		return iPos;
	}
	

	
	public String getPosXCode(float fPosXValue, float fPosYValue)
	{
		String ret = "";

		if( 60.00 < fPosXValue && fPosXValue < 84.00  )
		{
			//1RH
			ret = "1R";
		}
		else if( 210.00 < fPosXValue && fPosXValue < 234.00  )
		{
			//2RH
			ret = "2R";
		}
		else if( 286.00 < fPosXValue && fPosXValue < 312.00  )
		{
			//3RH
			ret = "3R";
		}
		else if( 136.00 < fPosXValue && fPosXValue < 162.00 && fPosYValue <= 8.00  )
		{
			//1BAP
			ret = "1Q";
		}
		else if( 110.00 < fPosXValue && fPosXValue < 136.00 && fPosYValue <= 8.00  )
		{
			//2BAP
			ret = "2Q";
		}
		else if( 84.00 < fPosXValue && fPosXValue < 110.00 && fPosYValue <= 8.00  )
		{
			//3BAP
			ret = "3Q";
		}
		else if( 162.00 < fPosXValue && fPosXValue < 186.00  )
		{
			//LF
			ret = "LF";
		}
		else if( 186.00 < fPosXValue && fPosXValue < 210.00  )
		{
			//회송배차
			ret = "RC";
		}
		else if( 234.00 < fPosXValue && fPosXValue < 240.50  )
		{
			//구경동대 1
			ret = "1P";
		}
		else if( 240.50 < fPosXValue && fPosXValue < 247.00  )
		{
			//구경동대 2
			ret = "2P";
		}
		else if( 247.00 < fPosXValue && fPosXValue < 253.50  )
		{
			//구경동대 3
			ret = "3P";
		}
		else if( 253.50 < fPosXValue && fPosXValue < 260.00  )
		{
			//구경동대 4
			ret = "4P";
		}
		else if( 36.00 < fPosXValue && fPosXValue < 44.00  )
		{
			//신경동대 5
			ret = "5P";
		}
		else if( 44.00 < fPosXValue && fPosXValue < 52.00  )
		{
			//신경동대 6
			ret = "6P";
		}
		else if( 52.00 < fPosXValue && fPosXValue < 60.00  )
		{
			//신경동대 7
			ret = "7P";
		}
		else if( 174.00 < fPosXValue && fPosXValue < 198.00  )
		{
			//POS-LEAD
			ret = "PL";
		}
		else if( 84.00 < fPosXValue && fPosXValue < 110.00 && fPosYValue > 8.00  )
		{
			//1 M/C
			ret = "1W";
		}
		else if( 136.00 < fPosXValue && fPosXValue < 162.00 && fPosYValue > 8.00  )
		{
			//2 M/C
			ret = "2W";
		}
		else if( 260.00 < fPosXValue && fPosXValue < 282.00  )
		{
			//3 M/C
			ret = "3W";
		}
		else if( 312.00 < fPosXValue && fPosXValue < 336.00  )
		{
			//4 M/C
			ret = "4W";
		}
		else if( 97.00 < fPosXValue && fPosXValue < 110.00  )
		{
			//드라이어 (보온버너) 1
			ret = "1D";
		}
		else if( 110.00 < fPosXValue && fPosXValue < 123.00  )
		{
			//드라이어 (보온버너) 2
			ret = "2D";
		}
		else if( 123.00 < fPosXValue && fPosXValue < 136.00  )
		{
			//드라이어 (보온버너) 3
			ret = "3D";
		}
		else if( 136.00 < fPosXValue && fPosXValue < 149.00  )
		{
			//드라이어 (보온버너) 4
			ret = "4D";
		}
		else if( 312.00 < fPosXValue && fPosXValue < 320.00  )
		{
			//드라이어 (보온버너) 5
			ret = "5D";
		}
		else if( 320.00 < fPosXValue && fPosXValue < 328.00  )
		{
			//드라이어 (보온버너) 6
			ret = "6D";
		}
		else if( 328.00 < fPosXValue && fPosXValue < 336.00  )
		{
			//드라이어 (보온버너) 7
			ret = "7D";
		}
		else if( 348.00 < fPosXValue && fPosXValue < 360.00  )
		{
			//래들파쇄설비 1
			ret = "1S";
		}
		else if( 360.00 < fPosXValue && fPosXValue < 372.00  )
		{
			//래들파쇄설비 2
			ret = "2S";
		}
		else if( 372.00 < fPosXValue && fPosXValue < 384.00  )
		{
			//래들파쇄설비 3
			ret = "3S";
		}
		else if( 36.00 < fPosXValue && fPosXValue < 60.00  )
		{
			//보온대기 위치 가정
			ret = "VR";
		}

		return ret;
	}
    

    public static void main(String args[])
	{
		ExecutorService crunchifyExecutor = Executors.newFixedThreadPool(4);
		// Replace username with your real value

		ResearchCraneLogDataDbConv ifm = new ResearchCraneLogDataDbConv(150);
		
		// Start running log file tailer on crunchify.log file
		crunchifyExecutor.execute(ifm);		
	}
}
