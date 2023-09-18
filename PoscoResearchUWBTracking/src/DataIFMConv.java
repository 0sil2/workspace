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


public class DataIFMConv 
{

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		DataIFMConv ifdb = new DataIFMConv();
	
		/*
		String jdbc_driver = "org.mariadb.jdbc.Driver";
		String jdbc_url = "jdbc:mariadb://192.168.0.55:3307/ucubedb?useUnicode=true&characterEncoding=utf8&serverTime=Asia/Seoul&useSSL=false";
		String db_id = "admin";
		String db_pw = "localsense12!@";
		 */

		String filePath = "/usr/local/posco/crane/crane/";
		String newFilePath = "/usr/local/posco/crane/new/";
		
		/*
		String [] aryFileNm = {
				"20230301_ifm_crane_log.txt"
		};
		*/
		
		
		String [] aryFileNm = {
				"20230301_ifm_crane_log.txt", "20230302_ifm_crane_log.txt", "20230303_ifm_crane_log.txt", "20230304_ifm_crane_log.txt", 
				"20230305_ifm_crane_log.txt", "20230306_ifm_crane_log.txt", "20230307_ifm_crane_log.txt", "20230308_ifm_crane_log.txt", 
				"20230309_ifm_crane_log.txt", "20230310_ifm_crane_log.txt", "20230311_ifm_crane_log.txt", "20230312_ifm_crane_log.txt", 
				"20230313_ifm_crane_log.txt", "20230314_ifm_crane_log.txt", "20230315_ifm_crane_log.txt", "20230316_ifm_crane_log.txt", 
				"20230317_ifm_crane_log.txt", "20230318_ifm_crane_log.txt", "20230319_ifm_crane_log.txt", "20230320_ifm_crane_log.txt", 
				"20230321_ifm_crane_log.txt", "20230322_ifm_crane_log.txt", "20230323_ifm_crane_log.txt", "20230324_ifm_crane_log.txt", 
				"20230325_ifm_crane_log.txt", "20230326_ifm_crane_log.txt", "20230327_ifm_crane_log.txt", "20230328_ifm_crane_log.txt", 
				"20230329_ifm_crane_log.txt", "20230330_ifm_crane_log.txt", "20230331_ifm_crane_log.txt", "20230401_ifm_crane_log.txt", 
				"20230402_ifm_crane_log.txt", "20230403_ifm_crane_log.txt", "20230404_ifm_crane_log.txt", "20230405_ifm_crane_log.txt", 
				"20230406_ifm_crane_log.txt", "20230407_ifm_crane_log.txt", "20230408_ifm_crane_log.txt", "20230409_ifm_crane_log.txt", 
				"20230410_ifm_crane_log.txt", "20230411_ifm_crane_log.txt", "20230412_ifm_crane_log.txt", "20230413_ifm_crane_log.txt", 
				"20230414_ifm_crane_log.txt", "20230415_ifm_crane_log.txt", "20230416_ifm_crane_log.txt", "20230417_ifm_crane_log.txt", 
				"20230418_ifm_crane_log.txt", "20230419_ifm_crane_log.txt", "20230420_ifm_crane_log.txt", "20230421_ifm_crane_log.txt", 
				"20230422_ifm_crane_log.txt", "20230423_ifm_crane_log.txt", "20230424_ifm_crane_log.txt", "20230425_ifm_crane_log.txt", 
				"20230426_ifm_crane_log.txt", "20230427_ifm_crane_log.txt", "20230428_ifm_crane_log.txt", "20230429_ifm_crane_log.txt", 
				"20230430_ifm_crane_log.txt"
		};
		


		String strNew = null;
		String sLine = null;
		
		File file = null;
		File fileNew = null;
		BufferedReader inFile = null;
		BufferedWriter writer = null;
		
    	Connection con = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
		
		try
		{

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
    		String tmp_weight = "";       // 12
    		String tmp_mtlno = "";        // 13
    		
    		String tmp_old_status = "";
    		
    		StringBuffer sbDt = new StringBuffer(30);
    		
    		float tmp_fx = 0;
    		float tmp_fy = 0;
    		
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

	    		fileNew = new File(newFilePath + "n_" + aryFileNm[i]); // File객체 생성
	            if(!fileNew.exists()){ // 파일이 존재하지 않으면
	            	fileNew.createNewFile(); // 신규생성
	            }
	            writer = new BufferedWriter(new FileWriter(fileNew, true));


				if(file.exists())
				{
				    inFile = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
				    while( (sLine = inFile.readLine()) != null )
				    {
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
			    		tmp_weight = arryTmp[12].trim();       // 12
			    		
			    		if( arrLen == 14 )
			    		{
			    			tmp_mtlno = arryTmp[13].trim();        // 13
			    		}
			    		else
			    		{
			    			tmp_mtlno = "";        // 13
			    		}
				    	
			    		//System.out.println("tmp_tagid ["+ tmp_tagtype +"] getCraneArryPos ["+ tmp_cranecode +"] ["+ ifdb.getCraneArryPos(tmp_cranecode) +"]");
			    		
			    		if( "1".equals(tmp_tagtype) && (0 <= ifdb.getCraneArryPos(tmp_cranecode) && ifdb.getCraneArryPos(tmp_cranecode) <= 8 ) )
			    		{
			    			sbDt = new StringBuffer(30);
			    			
			    			iArryFPos = ifdb.getCraneArryPos(tmp_cranecode);
			    			
			    			tmp_old_status = mtData[iArryFPos][6]; 
			    			
			    			mtData[iArryFPos][0] = tmp_ymd;
			    			mtData[iArryFPos][1] = tmp_cranecode;
			    			mtData[iArryFPos][2] = tmp_x;
			    			mtData[iArryFPos][3] = tmp_y_distance;
			    			mtData[iArryFPos][4] = tmp_z1_hoist;
			    			mtData[iArryFPos][5] = tmp_z2_sub;
			    			mtData[iArryFPos][6] = tmp_status;
			    			mtData[iArryFPos][7] = tmp_ladleno; 
			    			mtData[iArryFPos][8] = tmp_weight;
			    			mtData[iArryFPos][9] = tmp_mtlno;
			    			
			    			sbDt.append(tmp_ymd);
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
			    			sbDt.append(tmp_weight);
			    			sbDt.append(",");
			    			sbDt.append(tmp_mtlno);
			    			sbDt.append(",");
			    			

			    			if( tmp_old_status != null && !"".equals(tmp_old_status) && tmp_status != null && !"".equals(tmp_status) && !tmp_status.equals(tmp_old_status) )
			    			{
			    				tmp_fx = Float.parseFloat(tmp_x);
			    				tmp_fy = Float.parseFloat(tmp_y_distance);

			    				System.out.println("["+ tmp_ymd +"] ["+ tmp_cranecode +"] ["+ tmp_fx +"] ["+ tmp_fy +"] ["+ tmp_status +"] ["+ ifdb.getPosXCode(tmp_fx, tmp_fy) +"] ");

			    				//System.out.println("ifdb.getPosXCode(tmp_fx, tmp_fy) ["+ ifdb.getPosXCode(tmp_fx, tmp_fy) +"] ");
			    				
			    				sbDt.append( ifdb.getPosXCode(tmp_fx, tmp_fy) );
			    			}

			    			
			    			writer.write( sbDt.toString() );
				        	writer.newLine();
			    		}
			    		
				    	fileLineCnt++;
				    }
				    
				    System.out.println("File Line Count : " + fileLineCnt);
				}
				
				writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
		        writer.close(); // 스트림 종료
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
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
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
	
}
