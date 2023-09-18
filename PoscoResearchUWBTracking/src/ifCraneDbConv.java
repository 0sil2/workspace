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

public class ifCraneDbConv 
{

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		ifCraneDbConv ifdb = new ifCraneDbConv();
		
		String jdbc_driver = "org.mariadb.jdbc.Driver";
		String jdbc_url = "jdbc:mariadb://192.168.0.55:3307/cranedb?useUnicode=true&characterEncoding=utf8&serverTime=Asia/Seoul&useSSL=false";
		String db_id = "admin";
		String db_pw = "localsense12!@";

		String filePath = "/usr/local/posco/crane/new/";
		
		String [] aryFileNm = { "20230327" };
		
		/*
		String [] aryFileNm = { 
								"20230328", "20230329", "20230330", "20230331", "20230401", 
								"20230402", "20230403", "20230404", "20230405", "20230406", 
								"20230407", "20230408", "20230409", "20230410", "20230411", 
								"20230412", "20230413", "20230414", "20230415", "20230416", 
								"20230417", "20230418", "20230419", "20230420", "20230421", 
								"20230422", "20230423", "20230424"
							 };
		
		
		String [] aryFileNm = { 
				"20230401", 
				"20230402", "20230403", "20230404", "20230405", "20230406", 
				"20230407", "20230408", "20230409", "20230410", "20230411", 
				"20230412", "20230413", "20230414", "20230415", "20230416", 
				"20230417", "20230418", "20230419", "20230420", "20230421", 
				"20230422", "20230423", "20230424"
			 };
		*/
		
		// n_20230328_ifm_crane_log.txt  ~  n_20230424_ifm_crane_log.txt
		String sLine = null;
		
		File file = null;
		File fileErr = null;
		BufferedReader inFile = null;
		BufferedWriter writer = null;
		
    	Connection con = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try
		{
			Class.forName(jdbc_driver);
    		con = DriverManager.getConnection(jdbc_url, db_id, db_pw);

    		int iRet = -1;
    		
    		String [] arryTmp = null;
    		int arryLength = 0;
    		
    		StringBuffer sbquery = new StringBuffer(10);
    		
    		for(int i=0 ; i<aryFileNm.length ; i++)
    		{
    			
	    		file = new File( filePath + "n_"+ aryFileNm[i] +"_ifm_crane_log.txt" );
	    		System.out.println( "File Open : " + (filePath + "n_"+ aryFileNm[i] +"_ifm_crane_log.txt") );
	    		
	    		if(file.exists())
				{
	    			sbquery = new StringBuffer(10);
	    			sbquery.append(" INSERT INTO T_" + aryFileNm[i]);
	    			sbquery.append(" ( seq, ymd, cranecode, x, y, z1, z2, ldstatus, ldno, weight, mtlno, poscode ) ");
	    			sbquery.append(" VALUES ");
	    			sbquery.append(" ( ");
	    			sbquery.append(" NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
	    			sbquery.append(" ) ");
	    			
	    			
	    			inFile = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
				    while( (sLine = inFile.readLine()) != null )
				    {
				    	if( sLine != null )
				    	{
				    		arryTmp = sLine.split(",");
				    		arryLength = arryTmp.length;
				    		iRet = -1;
				    		
				    		ps = con.prepareStatement( sbquery.toString() );
				    		if( arryLength == 8 )
				    		{
				    			ps.setString(1,  arryTmp[0]);
				    			ps.setString(2,  arryTmp[1]);
				    			ps.setString(3,  arryTmp[2]);
				    			ps.setString(4,  arryTmp[3]);
				    			ps.setString(5,  arryTmp[4]);
				    			ps.setString(6,  arryTmp[5]);
				    			ps.setString(7,  arryTmp[6]);
				    			ps.setString(8,  arryTmp[7]);
				    			ps.setString(9,  "");
				    			ps.setString(10, "");
				    			ps.setString(11, "");
				    		}
				    		else if( arryLength == 9 )
				    		{
				    			ps.setString(1,  arryTmp[0]);
				    			ps.setString(2,  arryTmp[1]);
				    			ps.setString(3,  arryTmp[2]);
				    			ps.setString(4,  arryTmp[3]);
				    			ps.setString(5,  arryTmp[4]);
				    			ps.setString(6,  arryTmp[5]);
				    			ps.setString(7,  arryTmp[6]);
				    			ps.setString(8,  arryTmp[7]);
				    			ps.setString(9,  arryTmp[8]);
				    			ps.setString(10, "");
				    			ps.setString(11, "");
				    		}
				    		else if( arryLength == 10 )
				    		{
				    			ps.setString(1,  arryTmp[0]);
				    			ps.setString(2,  arryTmp[1]);
				    			ps.setString(3,  arryTmp[2]);
				    			ps.setString(4,  arryTmp[3]);
				    			ps.setString(5,  arryTmp[4]);
				    			ps.setString(6,  arryTmp[5]);
				    			ps.setString(7,  arryTmp[6]);
				    			ps.setString(8,  arryTmp[7]);
				    			ps.setString(9,  arryTmp[8]);
				    			ps.setString(10, arryTmp[9]);
				    			ps.setString(11, "");
				    		}
				    		else if( arryLength == 11 )
				    		{
				    			ps.setString(1,  arryTmp[0]);
				    			ps.setString(2,  arryTmp[1]);
				    			ps.setString(3,  arryTmp[2]);
				    			ps.setString(4,  arryTmp[3]);
				    			ps.setString(5,  arryTmp[4]);
				    			ps.setString(6,  arryTmp[5]);
				    			ps.setString(7,  arryTmp[6]);
				    			ps.setString(8,  arryTmp[7]);
				    			ps.setString(9,  arryTmp[8]);
				    			ps.setString(10, arryTmp[9]);
				    			ps.setString(11, arryTmp[10]);
				    		}
				    		iRet = ps.executeUpdate();
				    		
				    		if( iRet == -1 )
				    		{
				    			System.out.println("Err >> ["+ arryLength +"]" + sLine);
				    		}
				    	}
				    }
				}
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

}
