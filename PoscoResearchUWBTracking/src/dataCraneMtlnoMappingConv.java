import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

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
import java.util.Properties;

public class dataCraneMtlnoMappingConv 
{

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		// TODO Auto-generated method stub
		dataCraneMtlnoMappingConv ifdb = new dataCraneMtlnoMappingConv();
	
		String jdbc_driver = "com.mysql.jdbc.Driver";
		String jdbc_url = "jdbc:mysql://192.168.1.15:3306/ucube?serverTime=Asia/Seoul&useSSL=false&useUnicode=true&characterEncoding=utf8";
		String db_id = "root";
		String db_pw = "localsense";

		String filePath    = "/usr/local/posco/UwbTagsDataLog/2023/05/";
		String newFilePath = "/usr/local/posco/uwbpos/dataCraneMtlnoMappingConv/new/";
		String logFilePath = "/usr/local/posco/uwbpos/dataCraneMtlnoMappingConv/log/";
		

		String [] aryFileNm = {
				 "20230501_ifm_crane_log.txt"
				, "20230502_ifm_crane_log.txt"
				, "20230503_ifm_crane_log.txt"
				, "20230504_ifm_crane_log.txt"
				, "20230505_ifm_crane_log.txt"
				, "20230506_ifm_crane_log.txt"
				, "20230507_ifm_crane_log.txt"
				, "20230508_ifm_crane_log.txt"
				, "20230509_ifm_crane_log.txt"
				, "20230510_ifm_crane_log.txt"
				, "20230511_ifm_crane_log.txt"
				, "20230512_ifm_crane_log.txt"
				, "20230513_ifm_crane_log.txt"
				, "20230514_ifm_crane_log.txt"
				, "20230515_ifm_crane_log.txt"
				, "20230516_ifm_crane_log.txt"
				, "20230517_ifm_crane_log.txt"
				, "20230518_ifm_crane_log.txt"
				, "20230519_ifm_crane_log.txt"
				, "20230520_ifm_crane_log.txt"
				, "20230521_ifm_crane_log.txt"
				, "20230522_ifm_crane_log.txt"
				, "20230523_ifm_crane_log.txt"
				, "20230524_ifm_crane_log.txt"
				, "20230525_ifm_crane_log.txt"
				, "20230526_ifm_crane_log.txt"
				, "20230527_ifm_crane_log.txt"
				, "20230528_ifm_crane_log.txt"
				, "20230529_ifm_crane_log.txt"
				, "20230530_ifm_crane_log.txt"
				, "20230531_ifm_crane_log.txt"
			};

		String strNew = null;
		String sLine = null;

		File file = null;
		File fileNew = null;
		File fileLog = null;
		BufferedReader inFile = null;
		BufferedWriter writer = null;
		BufferedWriter logWriter = null;

    	Connection con = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;

    	String mtlno_1l = "";
    	String mtlno_2l = "";
    	String mtlno_1t = "";
    	String mtlno_2t = "";
    	String mtlno_3t = "";
    	String mtlno_4t = "";
    	String mtlno_1h = "";
    	String mtlno_2h = "";
    	String mtlno_1s = "";
    	
    	String ldno_1l = "";
    	String ldno_2l = "";
    	String ldno_1t = "";
    	String ldno_2t = "";
    	String ldno_3t = "";
    	String ldno_4t = "";
    	String ldno_1h = "";
    	String ldno_2h = "";
    	String ldno_1s = "";
    	
    	String sCraneLine = "";
    	HashMap <String, String> hmCrLdMappingInfo = null;
    	
    	try
		{
    		Class.forName(jdbc_driver);
    		con = DriverManager.getConnection(jdbc_url, db_id, db_pw);

    		int iRet = -1;
    		int fileLineCnt = 0;
    		int textLength = 0;
    		   		
    		String [] arryTmp = null;
    		int arrLen = 0;

    		String tmp_ymd = "";          // 0
    		String tmp_tagid = "";        // 1
    		String tmp_tagtype = "";      // 2
    		String tmp_cranecode = "";    // 3
    		String tmp_x = "";            // 4
    		String tmp_y_distance = "";   // 5
    		String tmp_z1_hoist = "";     // 6
    		String tmp_z2_sub = "";       // 7
    		String tmp_status = "";       // 8
    		String tmp_ladleno = "";      // 9
    		
    		String tmp_2_tagid = "";      // 10
    		String tmp_2_tagtype = "";    // 11

    		String tmp_weight = "";       // 12
    		String tmp_mtlno = "";        // 13

    		String tmp_old_status = "";
    		String tmp_dstlladnum = "";
    		String tmp_mappingMtlNo = "";
    		
    		StringBuffer sbDt = new StringBuffer(30);
    		
    		float tmp_fx = 0;
    		float tmp_fy = 0;
    		float tmp_fweight = 0;
    		
    		String [][] mtData = {
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
    		
    		
    		
    		int iArryFPos = 0;
    		
    		//1H (0), 2H (1), 1S (2), 1L (3), 2L (4), 1T (5), 2T (6), 3T (7), 4T (8) 

    		for(int i=0 ; i<aryFileNm.length ; i++)
    		{
	    		file = new File( filePath + aryFileNm[i] );
	    		System.out.println( "File Open : " + (filePath + aryFileNm[i]) );
	    		fileLineCnt = 0;

	    		fileNew = new File(newFilePath + "n_" + aryFileNm[i]); 
	            if(!fileNew.exists())
	            { 
	            	fileNew.createNewFile(); 
	            }
	            writer = new BufferedWriter(new FileWriter(fileNew, true));

	            fileLog = new File(logFilePath + "qry_" + aryFileNm[i]);
	            if(!fileLog.exists())
	            { 
	            	fileLog.createNewFile();
	            }
	            logWriter = new BufferedWriter(new FileWriter(fileLog, true));

				if(file.exists())
				{
					mtlno_1l = "";
	    	    	mtlno_2l = "";
	    	    	mtlno_1t = "";
	    	    	mtlno_2t = "";
	    	    	mtlno_3t = "";
	    	    	mtlno_4t = "";
	    	    	mtlno_1h = "";
	    	    	mtlno_2h = "";
	    	    	mtlno_1s = "";
	    	    	
	    	    	ldno_1l = "";
	    	    	ldno_2l = "";
	    	    	ldno_1t = "";
	    	    	ldno_2t = "";
	    	    	ldno_3t = "";
	    	    	ldno_4t = "";
	    	    	ldno_1h = "";
	    	    	ldno_2h = "";
	    	    	ldno_1s = "";
	    	    	
				    inFile = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
				    while( (sLine = inFile.readLine()) != null )
				    {
				    	hmCrLdMappingInfo = null;
				    	tmp_dstlladnum = "";
				    	tmp_mappingMtlNo = "";
				    	
				    	strNew = sLine;
				    	textLength = strNew.length();
				    	
				    	strNew = strNew.substring(1, textLength);
				    	strNew =  strNew.replaceAll("] >> ,", ",");
				    	
				    	arryTmp = strNew.split(",");
			    		arrLen = arryTmp.length;
			    		
			    		tmp_ymd = arryTmp[0].trim();          // 0
			    		tmp_tagid = arryTmp[1].trim();        // 1
			    		tmp_tagtype = arryTmp[2].trim();      // 2
			    		tmp_cranecode = arryTmp[3].trim();    // 3
			    		tmp_x = arryTmp[4].trim();            // 4
			    		tmp_y_distance = arryTmp[5].trim();   // 5
			    		tmp_z1_hoist = arryTmp[6].trim();     // 6
			    		tmp_z2_sub = arryTmp[7].trim();       // 7
			    		tmp_status = arryTmp[8].trim();       // 8
			    		tmp_ladleno = arryTmp[9].trim();      // 9
			    		tmp_2_tagid = arryTmp[10].trim();;     // 10
			    		tmp_2_tagtype = arryTmp[11].trim();;   // 11
			    		tmp_weight = arryTmp[12].trim();       // 12

			    		if( arrLen == 14 )
			    		{
			    			tmp_mtlno = arryTmp[13].trim();        // 13
			    		}
			    		else
			    		{
			    			tmp_mtlno = "";        // 13
			    		}
			    		
			    		/*********************************************************************/
			    		/*********************************************************************/

			    		if( "1H".equals(tmp_cranecode) || "2H".equals(tmp_cranecode) || "1S".equals(tmp_cranecode) )
			    		{
			    			sCraneLine = "HS";
			    		}
			    		else if( "1L".equals(tmp_cranecode) || "2L".equals(tmp_cranecode) || "3T".equals(tmp_cranecode) )
			    		{
			    			sCraneLine = "LD";
			    		}
			    		else if( "1T".equals(tmp_cranecode) || "2T".equals(tmp_cranecode) || "4T".equals(tmp_cranecode) )
			    		{
			    			sCraneLine = "TM";
			    		}
			    		else
			    		{
			    			sCraneLine = "";
			    		}

			    		//System.out.println("tmp_tagid ["+ tmp_tagtype +"] getCraneArryPos ["+ tmp_cranecode +"] ["+ ifdb.getCraneArryPos(tmp_cranecode) +"]");
			    		
			    		if( "1".equals(tmp_tagtype) && (0 <= ifdb.getCraneArryPos(tmp_cranecode) && ifdb.getCraneArryPos(tmp_cranecode) <= 8 ) )
			    		{
			    			sbDt = new StringBuffer(30);
			    			
			    			iArryFPos = ifdb.getCraneArryPos(tmp_cranecode);
			    			
			    			tmp_old_status = mtData[iArryFPos][6]; 
			    			
			    			if( sCraneLine != null && !"".equals(sCraneLine) )
			    			{
			    				if( ( "LD".equals(sCraneLine) || "TM".equals(sCraneLine) )
			    					&& tmp_old_status != null && !"".equals(tmp_old_status) && tmp_status != null && !"".equals(tmp_status)
			    					&& !tmp_status.equals(tmp_old_status) 
			    				  )
				    			{
			    					if( "F".equals(tmp_status) )
			    					{
					    				tmp_fx = Float.parseFloat(tmp_x);
					    				tmp_fy = Float.parseFloat(tmp_y_distance);
	
					    				System.out.println("F_Mapping => ["+ tmp_ymd +"] ["+ tmp_cranecode +"] ["+ sCraneLine +"] ["+ tmp_fx +"] ["+ tmp_fy +"] ["+ tmp_status +"] ");
					    				
					    				hmCrLdMappingInfo = ifdb.getMtlnoMapping( con, ps, rs, sCraneLine, tmp_cranecode, tmp_ymd, tmp_fx, tmp_fy, logWriter);
					    				if( hmCrLdMappingInfo.size() > 0 )
										{
											tmp_dstlladnum =  ifdb.isNullCheck( hmCrLdMappingInfo.get("tmp_dstlladnum"), "" );
											tmp_mappingMtlNo = ifdb.isNullCheck( hmCrLdMappingInfo.get("tmp_mtlno"), "" );
										}
										else
										{
											tmp_dstlladnum =  "";
											tmp_mappingMtlNo = "";
										}
					    				
					    				if( "1L".equals(tmp_cranecode) )
					    				{
					    					mtlno_1l = tmp_mappingMtlNo;
					    					ldno_1l = tmp_dstlladnum;
					    					
					    					tmp_mtlno = mtlno_1l;
					    					tmp_ladleno = ldno_1l;
					    				}
							    		else if( "2L".equals(tmp_cranecode) )
							    		{
							    			mtlno_2l = tmp_mappingMtlNo;
							    			ldno_2l = tmp_dstlladnum;
							    			
							    			tmp_mtlno = mtlno_2l;
					    					tmp_ladleno = ldno_2l;
							    		}
							    		else if( "1T".equals(tmp_cranecode) )
							    		{
							    			mtlno_1t = tmp_mappingMtlNo;
							    			ldno_1t = tmp_dstlladnum;
							    			
							    			tmp_mtlno = mtlno_1t;
					    					tmp_ladleno = ldno_1t;
							    		}
							    		else if( "2T".equals(tmp_cranecode) )
							    		{
							    			mtlno_2t = tmp_mappingMtlNo;
							    			ldno_2t = tmp_dstlladnum;
							    			
							    			tmp_mtlno = mtlno_2t;
					    					tmp_ladleno = ldno_2t;
							    		}
							    		else if( "3T".equals(tmp_cranecode) )
							    		{
							    			mtlno_3t = tmp_mappingMtlNo;
							    			ldno_3t = tmp_dstlladnum;
							    			
							    			tmp_mtlno = mtlno_3t;
					    					tmp_ladleno = ldno_3t;
							    		}
							    		else if( "4T".equals(tmp_cranecode) )
							    		{
							    			mtlno_4t = tmp_mappingMtlNo;
							    			ldno_4t = tmp_dstlladnum;
							    			
							    			tmp_mtlno = mtlno_4t;
					    					tmp_ladleno = ldno_4t;
							    		}
			    					}
			    					else if( "E".equals(tmp_status) && "TM".equals(sCraneLine) )
			    					{
			    						tmp_fx = Float.parseFloat(tmp_x);
					    				tmp_fy = Float.parseFloat(tmp_y_distance);
	
					    				System.out.println("E_Mapping => ["+ tmp_ymd +"] ["+ tmp_cranecode +"] ["+ sCraneLine +"] ["+ tmp_fx +"] ["+ tmp_fy +"] ["+ tmp_status +"] ");
					    				
					    				hmCrLdMappingInfo = ifdb.getMtlnoMapping_MC( con, ps, rs, sCraneLine, tmp_cranecode, tmp_ymd, tmp_fx, tmp_fy, logWriter);
					    				if( hmCrLdMappingInfo.size() > 0 )
										{
											tmp_dstlladnum =  ifdb.isNullCheck( hmCrLdMappingInfo.get("tmp_dstlladnum"), "" );
											tmp_mappingMtlNo = ifdb.isNullCheck( hmCrLdMappingInfo.get("tmp_mtlno"), "" );
										}
										else
										{
											tmp_dstlladnum =  "";
											tmp_mappingMtlNo = "";
										}
					    				
							    		if( "1T".equals(tmp_cranecode) )
							    		{
							    			mtlno_1t = tmp_mappingMtlNo;
							    			ldno_1t = tmp_dstlladnum;
							    			
							    			tmp_mtlno = mtlno_1t;
					    					tmp_ladleno = ldno_1t;
							    		}
							    		else if( "2T".equals(tmp_cranecode) )
							    		{
							    			mtlno_2t = tmp_mappingMtlNo;
							    			ldno_2t = tmp_dstlladnum;
							    			
							    			tmp_mtlno = mtlno_2t;
					    					tmp_ladleno = ldno_2t;
							    		}
							    		else if( "4T".equals(tmp_cranecode) )
							    		{
							    			mtlno_4t = tmp_mappingMtlNo;
							    			ldno_4t = tmp_dstlladnum;
							    			
							    			tmp_mtlno = mtlno_4t;
					    					tmp_ladleno = ldno_4t;
							    		}
			    					}
			    					else
			    					{
					    				if( "1L".equals(tmp_cranecode) )
					    				{
					    					mtlno_1l = "";
					    					ldno_1l = "";
					    					
					    					tmp_mtlno = mtlno_1l;
					    					tmp_ladleno = ldno_1l;
					    				}
							    		else if( "2L".equals(tmp_cranecode) )
							    		{
							    			mtlno_2l = "";
							    			ldno_2l = "";
							    			
							    			tmp_mtlno = mtlno_2l;
					    					tmp_ladleno = ldno_2l;
							    		}
							    		else if( "1T".equals(tmp_cranecode) )
							    		{
							    			mtlno_1t = "";
							    			ldno_1t = "";
							    			
							    			tmp_mtlno = mtlno_1t;
					    					tmp_ladleno = ldno_1t;
							    		}
							    		else if( "2T".equals(tmp_cranecode) )
							    		{
							    			mtlno_2t = "";
							    			ldno_2t = "";
							    			
							    			tmp_mtlno = mtlno_2t;
					    					tmp_ladleno = ldno_2t;
							    		}
							    		else if( "3T".equals(tmp_cranecode) )
							    		{
							    			mtlno_3t = "";
							    			ldno_3t = "";
							    			
							    			tmp_mtlno = mtlno_3t;
					    					tmp_ladleno = ldno_3t;
							    		}
							    		else if( "4T".equals(tmp_cranecode) )
							    		{
							    			mtlno_4t = "";
							    			ldno_4t = "";
							    			
							    			tmp_mtlno = mtlno_4t;
					    					tmp_ladleno = ldno_4t;
							    		}
			    					}
				    			}
			    				
			    				
			    				if( "1L".equals(tmp_cranecode) )
			    				{	
			    					tmp_mtlno = mtlno_1l;
			    					tmp_ladleno = ldno_1l;
			    				}
					    		else if( "2L".equals(tmp_cranecode) )
					    		{	
					    			tmp_mtlno = mtlno_2l;
			    					tmp_ladleno = ldno_2l;
					    		}
					    		else if( "1T".equals(tmp_cranecode) )
					    		{	
					    			tmp_mtlno = mtlno_1t;
			    					tmp_ladleno = ldno_1t;
					    		}
					    		else if( "2T".equals(tmp_cranecode) )
					    		{	
					    			tmp_mtlno = mtlno_2t;
			    					tmp_ladleno = ldno_2t;
					    		}
					    		else if( "3T".equals(tmp_cranecode) )
					    		{	
					    			tmp_mtlno = mtlno_3t;
			    					tmp_ladleno = ldno_3t;
					    		}
					    		else if( "4T".equals(tmp_cranecode) )
					    		{	
					    			tmp_mtlno = mtlno_4t;
			    					tmp_ladleno = ldno_4t;
					    		}

			    				mtData[iArryFPos][0] = tmp_ymd;
				    			mtData[iArryFPos][1] = tmp_cranecode;
				    			mtData[iArryFPos][2] = tmp_x;
				    			mtData[iArryFPos][3] = tmp_y_distance;
				    			mtData[iArryFPos][4] = tmp_z1_hoist;
				    			mtData[iArryFPos][5] = tmp_z2_sub;
				    			mtData[iArryFPos][7] = tmp_ladleno;
				    			mtData[iArryFPos][6] = tmp_status;
				    			mtData[iArryFPos][8] = tmp_weight;
				    			mtData[iArryFPos][9] = tmp_mtlno;

				    			/*
					    		tmp_ymd = arryTmp[0].trim();          // 0
					    		tmp_tagid = arryTmp[1].trim();        // 1
					    		tmp_tagtype = arryTmp[2].trim();      // 2
					    		tmp_cranecode = arryTmp[3].trim();    // 3
					    		tmp_x = arryTmp[4].trim();            // 4
					    		tmp_y_distance = arryTmp[5].trim();   // 5
					    		tmp_z1_hoist = arryTmp[6].trim();     // 6
					    		tmp_z2_sub = arryTmp[7].trim();       // 7
					    		tmp_status = arryTmp[8].trim();       // 8
					    		tmp_ladleno = arryTmp[9].trim();      // 9
					    		tmp_2_tagid = arryTmp[10].trim();;     // 10
					    		tmp_2_tagtype = arryTmp[11].trim();;   // 11
					    		tmp_weight = arryTmp[12].trim();       // 12
					    		*/
				    			
				    			/*
					    		 * ������ ���� ��û���� ���� ���Կ� ���� ���� ���� ������ ���缭 ���� ���� ��ȯ ó��
					    		���� ���Կ���
					    		50      n
					    		50-200  e
					    		200     f
					    		*/
					    		tmp_fweight = Float.parseFloat(tmp_weight);
					    		if( tmp_fweight <= 50.00  )
					    		{
					    			tmp_status = "N";
					    			tmp_ladleno = "";
					    			tmp_mtlno = "";
					    		}
					    		else if( 50.00 < tmp_fweight && tmp_fweight <= 200.00  )
					    		{
					    			tmp_status = "E";
					    		}
					    		else if( 200.00 < tmp_fweight )
					    		{
					    			tmp_status = "F";
					    		}

				    			sbDt.append(tmp_ymd);
				    			sbDt.append(",");
				    			sbDt.append(tmp_tagid);
				    			sbDt.append(",");
				    			sbDt.append(tmp_tagtype);
				    			sbDt.append(",");
				    			sbDt.append(tmp_cranecode);
				    			sbDt.append(",");
				    			sbDt.append(tmp_x);
				    			sbDt.append(",");
				    			sbDt.append(tmp_y_distance);
				    			sbDt.append(",");
				    			sbDt.append(tmp_z1_hoist);
				    			sbDt.append(",");
				    			sbDt.append(tmp_z2_sub);
				    			sbDt.append(",");
				    			sbDt.append(tmp_status);
				    			sbDt.append(",");
				    			sbDt.append(tmp_ladleno);
				    			sbDt.append(",");
				    			sbDt.append(tmp_2_tagid);
				    			sbDt.append(",");
				    			sbDt.append(tmp_2_tagtype);
				    			sbDt.append(",");
				    			sbDt.append(tmp_weight);
				    			sbDt.append(",");
				    			sbDt.append(tmp_mtlno);

				    			writer.write( sbDt.toString() );
					        	writer.newLine();
			    			}

			    		}
			    		
				    	fileLineCnt++;
				    }
				    
				    System.out.println("File Line Count : " + fileLineCnt);
				}
				
				writer.flush(); // ������ ���� �����͸� ��� ����
		        writer.close(); // ��Ʈ�� ����
		        logWriter.flush();
		        logWriter.close();
    		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( inFile != null ) inFile.close();
				if( writer != null ) writer.close();
				if( logWriter != null ) logWriter.close();
				
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
	
	public String getYmd(String str)
	{
		String tmp = str.substring(0,8);
		return tmp;
	}
	
	public int getYear(String str)
	{
		String tmp = str.substring(0,4);
		return Integer.parseInt(tmp);
	}
    
    public int getMonth(String str)
	{
		String tmp = str.substring(4,6);
		return Integer.parseInt(tmp);
	}
    
    public int getDay(String str)
	{
		String tmp = str.substring(6,8);
		return Integer.parseInt(tmp);
	}
    
    public int getHour(String str)
	{
		String tmp = str.substring(8,10);
		return Integer.parseInt(tmp);
	}
    
    public int getMinute(String str)
	{
		String tmp = str.substring(10,12);
		return Integer.parseInt(tmp);
	}
    
    public int getSec(String str)
	{
		String tmp = str.substring(12,14);
		return Integer.parseInt(tmp);
	}
    
    public String getLocalTimeStringConv(String str)
	{
    	String tmp = str;
    	tmp = tmp.replaceAll("-", "");
    	tmp = tmp.replaceAll("T", "");
    	tmp = tmp.replaceAll(":", "");
		return tmp;
	}
    
    public String isNullCheck(String paramVal, String param_set)
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
    
    public HashMap <String, String> getMtlnoMapping(Connection sCon, PreparedStatement sPs, ResultSet sRs, String craneline, String craneCode, String sYmd, float fPosXValue, float fPosYValue, BufferedWriter sLogWriter)
	{
    	HashMap <String, String> hm = new HashMap<>();
    	
    	StringBuffer strRHQuery = new StringBuffer(5);
    	strRHQuery.append("  select seq as seq, rh_tp as no, trim(mtl_no) as mtl_no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm  \n ");
    	strRHQuery.append("  from ke2d1r01  \n  ");
    	strRHQuery.append("  where mtl_no != ''  \n  ");
    	strRHQuery.append("    and sndr_inform_edit_date <= ? \n ");
    	strRHQuery.append("    and dstl_lad_num != '00' \n ");
    	strRHQuery.append("    and operation_tracking_cd in ('R3','R4') \n ");
    	strRHQuery.append("    and rh_tp = ? \n ");
    	strRHQuery.append("  order by seq desc  \n ");
    	strRHQuery.append("  limit 0, 1 \n ");
    	
    	StringBuffer strLFQuery = new StringBuffer(5);
    	strLFQuery.append("   select seq as seq, lf_num as no, trim(mtl_no) as mtl_no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm  \n   ");
    	strLFQuery.append("   from ke2d1t01   \n  ");
    	strLFQuery.append("   where mtl_no != ''   \n  ");
    	strLFQuery.append("     and sndr_inform_edit_date <= ?  \n ");
    	strLFQuery.append("     and dstl_lad_num != '00' \n  ");
    	strLFQuery.append("     and operation_tracking_cd in ('T3','T4')  \n ");
    	strLFQuery.append("     and lf_num = ?  \n ");
    	strLFQuery.append("   order by seq desc   \n  ");
    	strLFQuery.append("   limit 0, 1   \n  ");

    	StringBuffer strBAPQuery = new StringBuffer(5);
    	strBAPQuery.append("   select seq as seq, bap_field_num as no, trim(mtl_no) as mtl_no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm   \n  ");
    	strBAPQuery.append("   from ke2d1q01    \n ");
    	strBAPQuery.append("   where mtl_no != ''   \n  ");
    	strBAPQuery.append("     and sndr_inform_edit_date <= ?  \n ");
    	strBAPQuery.append("     and dstl_lad_num != '00'  \n ");
    	strBAPQuery.append("     and operation_tracking_cd in ('Q3','Q4')  \n  ");
    	strBAPQuery.append("     and bap_field_num = ?  \n ");
    	strBAPQuery.append("   order by seq desc    \n ");
    	strBAPQuery.append("   limit 0, 1   \n  ");
    	
    	String strQuery = "";
    	String strParmVal = "";
    	String strCranePos = "";

		if( "LD".equals(craneline) && 150.00 < fPosXValue && fPosXValue < 176.00  )
		{
			// Ladle Line - 1BAP
			strCranePos = "Ladle Line - 1BAP";
			strQuery = strBAPQuery.toString();
    		strParmVal = "1";
			
		}
		else if( "LD".equals(craneline) && 126.00 < fPosXValue && fPosXValue < 149.00  )
		{
			// Ladle Line - 2BAP
			strCranePos = "Ladle Line - 2BAP";
			strQuery = strBAPQuery.toString();
    		strParmVal = "2";	
		}
		else if( "LD".equals(craneline) && 102.00 < fPosXValue && fPosXValue < 125.00  )
		{
			// Ladle Line - 3BAP
			strCranePos = "Ladle Line - 3BAP";
			strQuery = strBAPQuery.toString();
    		strParmVal = "3";
		}
		else if( ( "TM".equals(craneline) || "HS".equals(craneline) ) && 136.00 < fPosXValue && fPosXValue < 161.00  )
		{
			// T/M, H Line - 1BAP
			strCranePos = "T/M, H Line - 1BAP";
			strQuery = strBAPQuery.toString();
    		strParmVal = "1";
		}
		else if( ( "TM".equals(craneline) || "HS".equals(craneline) ) && 110.00 < fPosXValue && fPosXValue < 135.00  )
		{
			// T/M, H Line - 2BAP
			strCranePos = "T/M, H Line - 2BAP";
			strQuery = strBAPQuery.toString();
    		strParmVal = "2";
		}
		else if( ( "TM".equals(craneline) || "HS".equals(craneline) ) && 84.00 < fPosXValue && fPosXValue < 100.00  )
		{
			// T/M, H Line - 3BAP
			strCranePos = "T/M, H Line - 3BAP";
			strQuery = strBAPQuery.toString();
    		strParmVal = "3";
		}
		
		else if(  "LD".equals(craneline) && 175.00 < fPosXValue && fPosXValue < 197.00  )
		{
			// Ladle Line - LF
			strCranePos = "Ladle Line - LF";
			strQuery = strLFQuery.toString();
    		strParmVal = "1";
		}
		else if(  ( "TM".equals(craneline) || "HS".equals(craneline) ) && 162.00 < fPosXValue && fPosXValue < 185.00  )
		{
			// T/M, H Line - LF
			strCranePos = "T/M, H Line - LF";
			strQuery = strLFQuery.toString();
    		strParmVal = "1";
		}

		else if( "LD".equals(craneline) && 78.00 < fPosXValue && fPosXValue < 101.00  )
		{
			// Ladle Line - 1RH
			strCranePos = "Ladle Line - 1RH";
			strQuery = strRHQuery.toString();
    		strParmVal = "1";
		}
		else if( "LD".equals(craneline) && 222.00 < fPosXValue && fPosXValue < 250.00  )
		{
			// Ladle Line - 2RH
			strCranePos = "Ladle Line - 2RH";
			strQuery = strRHQuery.toString();
    		strParmVal = "2";
		}
		else if( "LD".equals(craneline) && 294.00 < fPosXValue && fPosXValue < 320.00  )
		{
			// Ladle Line - 3RH
			strCranePos = "Ladle Line - 3RH";
			strQuery = strRHQuery.toString();
    		strParmVal = "3";
		}
		else if( ( "TM".equals(craneline) || "HS".equals(craneline) ) && 55.00 < fPosXValue && fPosXValue < 83.00  )
		{
			// T/M, H Line - 1RH
			strCranePos = "T/M, H Line - 1RH";
			strQuery = strRHQuery.toString();
    		strParmVal = "1";
		}
		else if( ( "TM".equals(craneline) || "HS".equals(craneline) ) && 210.00 < fPosXValue && fPosXValue < 234.00  )
		{
			// T/M, H Line - 2RH
			strCranePos = "T/M, H Line - 2RH";
			strQuery = strRHQuery.toString();
    		strParmVal = "2";
		}
		else if( ( "TM".equals(craneline) || "HS".equals(craneline) ) && 286.00 < fPosXValue && fPosXValue <312.00  )
		{
			// T/M, H Line - 3RH
			strCranePos = "T/M, H Line - 3RH";
			strQuery = strRHQuery.toString();
    		strParmVal = "3";
		}
		
		/*
		else if( "LD".equals(craneline) && 198.00 < fPosXValue && fPosXValue < 221.00  )
		{
			// Ladle Line - R����, ȸ�۴���
			
		}
		else if( ( "TM".equals(craneline) || "HS".equals(craneline) ) && 186.00 < fPosXValue && fPosXValue < 200.00  )
		{
			// T/M, H Line - R����, ȸ�۴���
			
		}
		*/
		
		if( !"".equals(strQuery) )
    	{
			try
    		{
    			sLogWriter.write("["+ sYmd +"]["+ craneline +"]["+ craneCode +"]["+ fPosXValue +"]["+ fPosYValue +"]["+ strCranePos +"]");
    			sLogWriter.newLine();
    			sLogWriter.write("Parmeter => ["+ sYmd +"]["+ strParmVal +"]");
    			sLogWriter.newLine();
    			sLogWriter.write( strQuery );
    			sLogWriter.newLine();
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
			
    	}
		
		if( !"".equals(strQuery) && !"".equals(strParmVal) )
    	{			
    		try
    		{
        		sPs = sCon.prepareStatement( strQuery );
        		sPs.setString( 1 , sYmd );
        		sPs.setString( 2 , strParmVal );
        		sRs = sPs.executeQuery();
        		
        		while( sRs.next()  )
    			{
        			hm.put("tmp_dstlladnum", sRs.getString("ldnm"));
        			hm.put("tmp_mtlno", sRs.getString("mtl_no"));
        			
        			sLogWriter.write("Result => ["+ hm.get("tmp_dstlladnum") +"]["+ hm.get("tmp_mtlno") +"] \n ");
        			sLogWriter.newLine();
    			}
        		sPs.clearParameters();
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
    	}

		return hm;
	}
    
    public HashMap <String, String> getMtlnoMapping_MC(Connection sCon, PreparedStatement sPs, ResultSet sRs, String craneline, String craneCode, String sYmd, float fPosXValue, float fPosYValue, BufferedWriter sLogWriter)
	{
    	HashMap <String, String> hm = new HashMap<>();

    	StringBuffer strMCQuery = new StringBuffer(5);
    	strMCQuery.append("   select seq as seq, '' AS mtl_no, mc_no as no, operation_tracking_cd as otcd, trim(dstl_lad_num) as ldnm   \n  ");
    	strMCQuery.append("   from zm2ee506   \n  ");
    	strMCQuery.append("   where operation_tracking_cd = 'W7'  \n   ");
    	strMCQuery.append("     and sndr_inform_edit_date <= ?   \n ");
    	strMCQuery.append("     and mc_no = ?  \n ");
    	strMCQuery.append("   order by seq desc  \n ");
    	strMCQuery.append("   limit 0, 1   \n  ");
    	
    	String strQuery = "";
    	String strParmVal = "";
    	String strCranePos = "";

		if( "TM".equals(craneline) && 84.00 < fPosXValue && fPosXValue < 110.00 && fPosYValue > 8.00  )
		{
			//1 M/C
			strCranePos = "1 M/C";
			strQuery = strMCQuery.toString();
    		strParmVal = "1";
		}
		else if( "TM".equals(craneline) && 136.00 < fPosXValue && fPosXValue < 162.00 && fPosYValue > 8.00  )
		{
			//2 M/C
			strCranePos = "2 M/C";
			strQuery = strMCQuery.toString();
    		strParmVal = "2";
		}
		else if( "TM".equals(craneline) && 255.00 < fPosXValue && fPosXValue < 290.00  )
		{
			//3 M/C
			strCranePos = "3 M/C";
			strQuery = strMCQuery.toString();
    		strParmVal = "3";
		}
		else if( "TM".equals(craneline) && 310.00 < fPosXValue && fPosXValue < 336.00  )
		{
			//4 M/C
			strCranePos = "4 M/C";
			strQuery = strMCQuery.toString();
    		strParmVal = "4";
		}
		
		if( !"".equals(strQuery) )
    	{
			try
    		{
    			sLogWriter.write("["+ sYmd +"]["+ craneline +"]["+ craneCode +"]["+ fPosXValue +"]["+ fPosYValue +"]["+ strCranePos +"]");
    			sLogWriter.newLine();
    			sLogWriter.write("Parmeter => ["+ sYmd +"]["+ strParmVal +"]");
    			sLogWriter.newLine();
    			sLogWriter.write( strQuery );
    			sLogWriter.newLine();
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
			
    	}
		
		if( !"".equals(strQuery) && !"".equals(strParmVal) )
    	{			
    		try
    		{
        		sPs = sCon.prepareStatement( strQuery );
        		sPs.setString( 1 , sYmd );
        		sPs.setString( 2 , strParmVal );
        		sRs = sPs.executeQuery();
        		
        		while( sRs.next()  )
    			{
        			hm.put("tmp_dstlladnum", sRs.getString("ldnm"));
        			hm.put("tmp_mtlno", sRs.getString("mtl_no"));
        			
        			sLogWriter.write("Result => ["+ hm.get("tmp_dstlladnum") +"]["+ hm.get("tmp_mtlno") +"] \n ");
        			sLogWriter.newLine();
    			}
        		sPs.clearParameters();
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
    	}

		return hm;
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
			//ȸ�۹���
			ret = "RC";
		}
		else if( 234.00 < fPosXValue && fPosXValue < 240.50  )
		{
			//���浿�� 1
			ret = "1P";
		}
		else if( 240.50 < fPosXValue && fPosXValue < 247.00  )
		{
			//���浿�� 2
			ret = "2P";
		}
		else if( 247.00 < fPosXValue && fPosXValue < 253.50  )
		{
			//���浿�� 3
			ret = "3P";
		}
		else if( 253.50 < fPosXValue && fPosXValue < 260.00  )
		{
			//���浿�� 4
			ret = "4P";
		}
		else if( 36.00 < fPosXValue && fPosXValue < 44.00  )
		{
			//�Ű浿�� 5
			ret = "5P";
		}
		else if( 44.00 < fPosXValue && fPosXValue < 52.00  )
		{
			//�Ű浿�� 6
			ret = "6P";
		}
		else if( 52.00 < fPosXValue && fPosXValue < 60.00  )
		{
			//�Ű浿�� 7
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
			//����̾� (���¹���) 1
			ret = "1D";
		}
		else if( 110.00 < fPosXValue && fPosXValue < 123.00  )
		{
			//����̾� (���¹���) 2
			ret = "2D";
		}
		else if( 123.00 < fPosXValue && fPosXValue < 136.00  )
		{
			//����̾� (���¹���) 3
			ret = "3D";
		}
		else if( 136.00 < fPosXValue && fPosXValue < 149.00  )
		{
			//����̾� (���¹���) 4
			ret = "4D";
		}
		else if( 312.00 < fPosXValue && fPosXValue < 320.00  )
		{
			//����̾� (���¹���) 5
			ret = "5D";
		}
		else if( 320.00 < fPosXValue && fPosXValue < 328.00  )
		{
			//����̾� (���¹���) 6
			ret = "6D";
		}
		else if( 328.00 < fPosXValue && fPosXValue < 336.00  )
		{
			//����̾� (���¹���) 7
			ret = "7D";
		}
		else if( 348.00 < fPosXValue && fPosXValue < 360.00  )
		{
			//�����ļ⼳�� 1
			ret = "1S";
		}
		else if( 360.00 < fPosXValue && fPosXValue < 372.00  )
		{
			//�����ļ⼳�� 2
			ret = "2S";
		}
		else if( 372.00 < fPosXValue && fPosXValue < 384.00  )
		{
			//�����ļ⼳�� 3
			ret = "3S";
		}
		else if( 36.00 < fPosXValue && fPosXValue < 60.00  )
		{
			//���´�� ��ġ ����
			ret = "VR";
		}

		return ret;
	}

}
