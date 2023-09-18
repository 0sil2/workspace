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

public class PoscoResearchKaistStartMonitoringLdTTNo 
{

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		String jdbc_driver = "com.mysql.jdbc.Driver";
		String jdbc_url = "jdbc:mysql://10.30.92.142:3306/ucube?serverTime=Asia/Seoul&useSSL=false&useUnicode=true&characterEncoding=utf8";
		String db_id = "root";
		String db_pw = "localsense";
		
		Connection con = null;

		PreparedStatement ps = null;
		PreparedStatement ps_01 = null;

		ResultSet rs = null;
		ResultSet rs_01 = null;
		
		StringBuffer sbQuery = new StringBuffer(10);
		sbQuery.append("  SELECT A.mtl_no, B.be_charge_no, B.sm_dstl_ld_tt_no ");
		sbQuery.append("  FROM ");
		sbQuery.append("  ( ");
		sbQuery.append("  	SELECT mtl_no ");
		sbQuery.append("  	FROM caresultdb.cakaiststartbackup A ");
		sbQuery.append("  	WHERE flag = '1' AND mtl_no != '' ");
		sbQuery.append("  ) A ");
		sbQuery.append("  LEFT JOIN "); 
		sbQuery.append("  ( ");
		sbQuery.append("  	SELECT be_charge_no, sm_dstl_ld_tt_no "); 
		sbQuery.append("  	FROM ucubedb.zm2de504  ");
		sbQuery.append("  	WHERE ( be_charge_no, sndr_inform_edit_date ) ");
		sbQuery.append("  	      in (  ");
		sbQuery.append("  			     SELECT be_charge_no, MAX(sndr_inform_edit_date) AS sndr_inform_edit_date "); 
		sbQuery.append("  			     FROM ucubedb.zm2de504  ");
		sbQuery.append("  				  GROUP BY be_charge_no ");
		sbQuery.append("  				) ");
		sbQuery.append("  	GROUP BY be_charge_no, sm_dstl_ld_tt_no "); 
		sbQuery.append("  ) B ");
		sbQuery.append("  ON A.mtl_no = B.be_charge_no ");
		sbQuery.append("  WHERE B.be_charge_no != '' ");
		
		StringBuffer sbUpdateQuery  = new StringBuffer(10);
		sbUpdateQuery.append(" UPDATE caresultdb.cakaiststartbackup ");
		sbUpdateQuery.append(" SET SM_DSTL_LD_TT_NO = ? ");
		sbUpdateQuery.append(" WHERE MTL_NO = ? AND FLAG = '1' ");
		
		while(true)
		{
				try
				{
					Class.forName(jdbc_driver);
					con = DriverManager.getConnection(jdbc_url, db_id, db_pw);
		
					ps = con.prepareStatement( sbQuery.toString() );
					rs = ps.executeQuery();
					while( rs.next() )
					{
						ps_01 = con.prepareStatement( sbUpdateQuery.toString() );
						ps_01.setString(1, rs.getString("sm_dstl_ld_tt_no") );
						ps_01.setString(2, rs.getString("mtl_no") );
						ps_01.executeUpdate();
						ps_01.clearParameters();
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
						if( rs != null ) rs.close();
						if( rs_01 != null ) rs_01.close();
		
						if( ps != null ) ps.close();
						if( ps_01 != null ) ps_01.close();
		
						if( con != null ) con.close();
					}
					catch(Exception e)
					{
						System.out.println( "error => " + e );
						e.printStackTrace();
					}
				}
				
				try
				{
					System.out.println("[PoscoResearchKaistStartMonitoringLdTTNo] 1시간에 한번씩 실행 - zm2de504.sm_dstl_ld_tt_no ");
					Thread.sleep(1000 * 60 * 60);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}

	}

}
