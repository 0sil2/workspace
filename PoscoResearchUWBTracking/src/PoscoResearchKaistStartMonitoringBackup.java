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


/*

> crontab -e
> systemctl restart crond
> crontab -l
> tail -f /var/log/cron
*/

public class PoscoResearchKaistStartMonitoringBackup 
{

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		PoscoResearchKaistStartMonitoringBackup prks = new PoscoResearchKaistStartMonitoringBackup();
		
		
		String jdbc_driver = "com.mysql.jdbc.Driver";
		//String jdbc_url = "jdbc:mysql://192.168.1.15:3306/ucube?serverTime=Asia/Seoul&useSSL=false&useUnicode=true&characterEncoding=utf8";
		String jdbc_url = "jdbc:mysql://10.30.92.142:3306/ucube?serverTime=Asia/Seoul&useSSL=false&useUnicode=true&characterEncoding=utf8";
		String db_id = "root";
		String db_pw = "localsense";
		
		Connection con = null;

		PreparedStatement ps = null;
		PreparedStatement ps_01 = null;
		PreparedStatement ps_02 = null;
		PreparedStatement ps_03 = null;
		PreparedStatement ps_04 = null;
		PreparedStatement ps_05 = null;

		ResultSet rs = null;
		ResultSet rs_01 = null;
		ResultSet rs_02 = null;
		ResultSet rs_03 = null;
		ResultSet rs_04 = null;
		ResultSet rs_05 = null;

		StringBuffer sbQuery = new StringBuffer(10);
		StringBuffer sbCrNoQuery = new StringBuffer(10);
		
		StringBuffer sbCountQuery  = new StringBuffer(10);
		StringBuffer sbInsertQuery  = new StringBuffer(10);
		StringBuffer sbUpdateQuery  = new StringBuffer(10);
		
		sbCountQuery.append(" SELECT COUNT(*) as CNT  ");
		sbCountQuery.append(" FROM caresultdb.cakaiststartbackup ");
		sbCountQuery.append(" WHERE FLAG = ? AND MTL_NO2 = ? ");
		
		
		sbInsertQuery.append(" INSERT INTO caresultdb.cakaiststartbackup ");
		sbInsertQuery.append(" ( ");
		sbInsertQuery.append("     FLAG, SNDR_INFORM_EDIT_DATE, MTL_NO, MTL_NO2, PRP_CHARGE_NO, PRP_CHARGE_NO2 ");
		sbInsertQuery.append("     , PLAN_CHARGE_NO, SM_LD_BLW_METH_TP, SM_STEEL_GRD_N, DSTL_LAD_NUM, SM_2ND_RFN_CD ");
		sbInsertQuery.append("     , FCE_NUM, SM_SREF_ORI_OP_TP, SM_SREF_FNL_OP_TP, MCC, LD_FCE_OX_BW_SAT_DT ");
		sbInsertQuery.append("     , LD_FCE_OX_BW_SAT_DT1, TAPP_DN_DATE, TAPP_DN_DATE1, BAP_SAT_DT, BAP_SAT_DT1 ");
		sbInsertQuery.append("     , BAP_DN_DT, BAP_DN_DT1, LF_CR_NO, LF_SAT_DT, LF_SAT_DT1, LF_DN_DT, LF_DN_DT1 ");
		sbInsertQuery.append("     , RH_CR_NO, RH_VACCUM_SAT_DT, RH_VACCUM_SAT_DT1, RH_VACCUM_DN_DT, RH_VACCUM_DN_DT1 ");
		sbInsertQuery.append("     , SM_SREF_CAST_SAT_STAG_TM, SM_SREF_CAST_SAT_STAG_TM1, LAD_INJ_SAT_DT, LAD_INJ_SAT_DT1 ");
		sbInsertQuery.append("     , CC_I_CR_NO, CAST_TM, CAST_TM1, CC_O_CR_NO, SM_SREF_IS_STAY_LT1, TT_NO ");
		sbInsertQuery.append(" ) ");
		sbInsertQuery.append(" VALUES  ");
		sbInsertQuery.append(" ( ");
		sbInsertQuery.append("       ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
		sbInsertQuery.append("     , ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
		sbInsertQuery.append("     , ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
		sbInsertQuery.append("     , ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
		sbInsertQuery.append("     , ?, ?, ? ");
		sbInsertQuery.append(" ) ");

		
		sbUpdateQuery.append(" UPDATE caresultdb.cakaiststartbackup ");
		sbUpdateQuery.append(" SET ");
		sbUpdateQuery.append(" 	FLAG=?, ");
		sbUpdateQuery.append(" 	SNDR_INFORM_EDIT_DATE=?, ");
		sbUpdateQuery.append(" 	MTL_NO=?, ");
		sbUpdateQuery.append(" 	PRP_CHARGE_NO=?, ");
		sbUpdateQuery.append(" 	PRP_CHARGE_NO2=?, ");
		sbUpdateQuery.append(" 	PLAN_CHARGE_NO=?, ");
		sbUpdateQuery.append(" 	SM_LD_BLW_METH_TP=?, ");
		sbUpdateQuery.append(" 	SM_STEEL_GRD_N=?, ");
		sbUpdateQuery.append(" 	DSTL_LAD_NUM=?, ");
		sbUpdateQuery.append(" 	SM_2ND_RFN_CD=?, ");
		sbUpdateQuery.append(" 	FCE_NUM=?, ");
		sbUpdateQuery.append(" 	SM_SREF_ORI_OP_TP=?, ");
		sbUpdateQuery.append(" 	SM_SREF_FNL_OP_TP=?, ");
		sbUpdateQuery.append(" 	MCC=?, ");
		sbUpdateQuery.append(" 	LD_FCE_OX_BW_SAT_DT=?, ");
		sbUpdateQuery.append(" 	LD_FCE_OX_BW_SAT_DT1=?, ");
		sbUpdateQuery.append(" 	TAPP_DN_DATE=?, ");
		sbUpdateQuery.append(" 	TAPP_DN_DATE1=?, ");
		sbUpdateQuery.append(" 	BAP_SAT_DT=?, ");
		sbUpdateQuery.append(" 	BAP_SAT_DT1=?, ");
		sbUpdateQuery.append(" 	BAP_DN_DT=?, ");
		sbUpdateQuery.append(" 	BAP_DN_DT1=?, ");
		sbUpdateQuery.append(" 	LF_CR_NO=?, ");
		sbUpdateQuery.append(" 	LF_SAT_DT=?, ");
		sbUpdateQuery.append(" 	LF_SAT_DT1=?, ");
		sbUpdateQuery.append(" 	LF_DN_DT=?, ");
		sbUpdateQuery.append(" 	LF_DN_DT1=?, ");
		sbUpdateQuery.append(" 	RH_CR_NO=?, ");
		sbUpdateQuery.append(" 	RH_VACCUM_SAT_DT=?, ");
		sbUpdateQuery.append(" 	RH_VACCUM_SAT_DT1=?, ");
		sbUpdateQuery.append(" 	RH_VACCUM_DN_DT=?, ");
		sbUpdateQuery.append(" 	RH_VACCUM_DN_DT1=?, ");
		sbUpdateQuery.append(" 	SM_SREF_CAST_SAT_STAG_TM=?, ");
		sbUpdateQuery.append(" 	SM_SREF_CAST_SAT_STAG_TM1=?, ");
		sbUpdateQuery.append(" 	LAD_INJ_SAT_DT=?, ");
		sbUpdateQuery.append(" 	LAD_INJ_SAT_DT1=?, ");
		sbUpdateQuery.append(" 	CC_I_CR_NO=?, ");
		sbUpdateQuery.append(" 	CAST_TM=?, ");
		sbUpdateQuery.append(" 	CAST_TM1=?, ");
		sbUpdateQuery.append(" 	CC_O_CR_NO=?, ");
		sbUpdateQuery.append(" 	SM_SREF_IS_STAY_LT1=?, ");
		sbUpdateQuery.append(" 	TT_NO=? ");
		sbUpdateQuery.append(" WHERE MTL_NO2=? AND FLAG=? ");
		
		while(true)
		{
				try
				{
					Class.forName(jdbc_driver);
					con = DriverManager.getConnection(jdbc_url, db_id, db_pw);
		
					String dstl_lad_num = "";
					
					String smtlno = "";
					String strBold = "";
					String strBgcolor = "";
					String strBgMcc = "";
		
					String smtlno2 = "";
					String smtlno2_org = "";
					String prpChargeNo2 = "";
					String prpChargeNo2_org = "";
					String sMcno = "";
		
					String sPlanCharegNo = "";
					String sSmSteelGrdN = "";
		
					String sCa_ld_no = "";
					String sLF_CR_NO = "";
					String sRH_CR_NO = "";
					String sCC_I_CR_NO = "";
					String sCC_O_CR_NO = "";
					String sTT_NO = "";
		
					String query_chageNo = "";
		
					String m_ste_wk_yrd_no = "";
					String rf1 = "";
					String rf2 = "";
					String sndr_inform_edit_date = "";
					
					int rowCnt = 0;
					int ret1 = 0;
					int ret2 = 0;
					
					HashMap <String, String> hmData = new HashMap<String, String>(); 
					HashMap <String, String> hmKaist = new HashMap<String, String>();
		
					sbQuery = new StringBuffer(10);
					sbQuery.append("   SELECT      ");
					sbQuery.append("     seq      ");
					sbQuery.append("   , subseq      ");
					sbQuery.append("   , sndr_inform_edit_date      ");
					sbQuery.append("   , MTL_NO      ");
					sbQuery.append("   , MTL_NO2      ");
					sbQuery.append("   , PRP_CHARGE_NO      ");
					sbQuery.append("   , PRP_CHARGE_NO2      ");
					sbQuery.append("   , PLAN_CHARGE_NO      ");
					sbQuery.append("   , SM_STEEL_GRD_N      ");
					sbQuery.append("   , MC_NO       ");
					sbQuery.append("   , MCC      ");
					sbQuery.append("   , SM_LD_BLW_METH_TP       ");
					sbQuery.append("   , SM_2ND_RFN_CD      ");
					sbQuery.append("   , MTRL_STATUS_FLAG       ");
					sbQuery.append("   , FCE_NUM      ");
					sbQuery.append("   , SM_SREF_ORI_OP_TP      "); 
					sbQuery.append("   , SM_SREF_FNL_OP_TP       ");
					sbQuery.append("   , LD_FCE_OX_BW_SAT_DT      ");
					sbQuery.append("   , LD_FCE_OX_BW_SAT_DT1      ");
					sbQuery.append("   , LD_FCE_OX_BW_DN_DT      ");
					sbQuery.append("   , LD_FCE_OX_BW_DN_DT1      ");
					sbQuery.append("   , TAPP_STA_DATE      ");
					sbQuery.append("   , TAPP_STA_DATE1      ");
					sbQuery.append("   , TAPP_DN_DATE      ");
					sbQuery.append("   , TAPP_DN_DATE1      ");
					sbQuery.append("   , BAP_SAT_DT      ");
					sbQuery.append("   , BAP_SAT_DT1      ");
					sbQuery.append("   , BAP_DN_DT      ");
					sbQuery.append("   , BAP_DN_DT1      ");
					sbQuery.append("   , BAP_DEPT_DT      ");
					sbQuery.append("   , BAP_DEPT_DT1      ");
					sbQuery.append("   , LF_ARR_DT      ");
					sbQuery.append("   , LF_ARR_DT1      ");
					sbQuery.append("   , LF_SAT_DT      ");
					sbQuery.append("   , LF_SAT_DT1      ");
					sbQuery.append("   , LF_DN_DT      ");
					sbQuery.append("   , LF_DN_DT1      ");
					sbQuery.append("   , LF_DEPT_DT      ");
					sbQuery.append("   , LF_DEPT_DT1      ");
					sbQuery.append("   , RH_ARR_DT      ");
					sbQuery.append("   , RH_ARR_DT1      ");
					sbQuery.append("   , RH_VACCUM_SAT_DT      ");
					sbQuery.append("   , RH_VACCUM_SAT_DT1      ");
					sbQuery.append("   , RH_VACCUM_DN_DT      ");
					sbQuery.append("   , RH_VACCUM_DN_DT1      ");
					sbQuery.append("   , RH_DEPT_DT      ");
					sbQuery.append("   , RH_DEPT_DT1      ");
					sbQuery.append("   , SM_SREF_CAST_SAT_STAG_TM       ");
					sbQuery.append("   , SM_SREF_CAST_SAT_STAG_TM1       ");
					sbQuery.append("   , LAD_INJ_SAT_DT      ");
					sbQuery.append("   , LAD_INJ_SAT_DT1      ");
					sbQuery.append("   , CAST_TM       ");
					sbQuery.append("   , CAST_TM1       ");
					sbQuery.append("   , SM_SREF_IS_STAY_LT      "); 
					sbQuery.append("   , SM_SREF_IS_STAY_LT1       ");
					sbQuery.append("   , ca_ld_no   ");
					sbQuery.append("  , (   ");
					sbQuery.append("  	SELECT DSTL_LAD_NUM       ");  
					sbQuery.append("  	FROM ucubedb.vm_zm2de507_ldnm     ");
					sbQuery.append("  	WHERE subseq = (         ");
					sbQuery.append("  		SELECT max(subseq) AS subseq     ");
					sbQuery.append("  		FROM ucubedb.vm_zm2de507_ldnm     ");
					sbQuery.append("  		WHERE 1=1     ");
					sbQuery.append("  		   AND  mtl_no IN ( VM.MTL_NO, VM.MTL_NO2 )     ");
					sbQuery.append("  		GROUP BY mtl_no   ");
					sbQuery.append("  	    )   ");
					sbQuery.append("    ) AS DSTL_LAD_NUM   ");
					sbQuery.append("  FROM  ucubedb.vw_zm2de501 VM   ");
					//sbQuery.append("  ORDER BY subseq ASC       ");
		
					ps = con.prepareStatement( sbQuery.toString() );
					rs = ps.executeQuery();
		
					while( rs.next() )
					{
						sCa_ld_no = "";
						sLF_CR_NO = "";
						sRH_CR_NO = "";
						sCC_I_CR_NO = "";
						sCC_O_CR_NO = "";
						sTT_NO = "";
						m_ste_wk_yrd_no = "";
						rf1 = "";
						rf2 = "";
						
						rowCnt = 0;
						
						hmData = new HashMap<String, String>(); 
						hmKaist = new HashMap<String, String>();
		
						smtlno = rs.getString("MTL_NO");
						sMcno = rs.getString("MC_NO");
						sPlanCharegNo = rs.getString("PLAN_CHARGE_NO");
						sSmSteelGrdN = rs.getString("SM_STEEL_GRD_N");
						sCa_ld_no = rs.getString("ca_ld_no");
						dstl_lad_num = rs.getString("DSTL_LAD_NUM");
						sndr_inform_edit_date = rs.getString("sndr_inform_edit_date");
						
						smtlno2_org = rs.getString("MTL_NO2");
						prpChargeNo2_org = rs.getString("PRP_CHARGE_NO2");
						
						//System.out.println(">>>> ["+ smtlno2_org +"]");
						
						if( smtlno != null && !"".equals(smtlno) )
						{
							smtlno2 = rs.getString("MTL_NO");
							prpChargeNo2 = rs.getString("PRP_CHARGE_NO");
						}
						else
						{
							smtlno2 = rs.getString("MTL_NO2");
							prpChargeNo2 = rs.getString("PRP_CHARGE_NO2");
						}
		
						if( smtlno == null ) smtlno = "";
						if( smtlno2 == null ) smtlno2 = "";
						
						
						if( !"".equals(smtlno) || !"".equals(smtlno2) )
						{
							if( !"".equals(smtlno) && !"".equals(smtlno2) )
							{
								query_chageNo = " AND charge_no IN ( '"+ smtlno +"', '"+ smtlno2 +"' )  ";
							}
							else if( !"".equals(smtlno) && "".equals(smtlno2) )
							{
								query_chageNo = " AND charge_no = '"+ smtlno +"'  ";
							}
							else if( "".equals(smtlno) && !"".equals(smtlno2) )
							{
								query_chageNo = " AND charge_no = '"+ smtlno2 +"'  ";
							}
		
							sbCrNoQuery = new StringBuffer(10);
							sbCrNoQuery.append("  SELECT  ");
							sbCrNoQuery.append("    ( SELECT cr_no from caresultdb.ca_crane_start_result WHERE 1=1 "+ query_chageNo +" AND TYPE = 'LF' ORDER BY seq DESC LIMIT 0, 1 ) AS LF_CR_NO  ");
							sbCrNoQuery.append("  , ( SELECT cr_no from caresultdb.ca_crane_start_result WHERE 1=1 "+ query_chageNo +" AND TYPE = 'RH' ORDER BY seq DESC LIMIT 0, 1 ) AS RH_CR_BO  ");
							sbCrNoQuery.append("  , ( SELECT cr_no from caresultdb.ca_crane_start_result WHERE 1=1 "+ query_chageNo +" AND TYPE = 'CC_I' ORDER BY seq DESC LIMIT 0, 1 ) AS CC_I_CR_BO   ");
							sbCrNoQuery.append("  , ( SELECT cr_no from caresultdb.ca_crane_start_result WHERE 1=1 "+ query_chageNo +" AND TYPE = 'CC_O' ORDER BY seq DESC LIMIT 0, 1 ) AS CC_O_CR_BO   ");
							ps_03 = con.prepareStatement( sbCrNoQuery.toString() );
							rs_03 = ps_03.executeQuery();
							while( rs_03.next() )
							{
								sLF_CR_NO = rs_03.getString("LF_CR_NO");
								sRH_CR_NO = rs_03.getString("RH_CR_BO");
								sCC_I_CR_NO = rs_03.getString("CC_I_CR_BO");
								sCC_O_CR_NO = rs_03.getString("CC_O_CR_BO");
							}
		
		
							sbCrNoQuery = new StringBuffer(10);
							sbCrNoQuery.append(" SELECT tt_no  ");
							sbCrNoQuery.append(" FROM caresultdb.ca_tt_result  ");
							sbCrNoQuery.append(" WHERE 1=1 ");
							sbCrNoQuery.append( query_chageNo );
							sbCrNoQuery.append(" ORDER BY seq DESC  ");
							sbCrNoQuery.append(" LIMIT 0, 1  ");
							ps_05 = con.prepareStatement( sbCrNoQuery.toString() );
							rs_05 = ps_05.executeQuery();
							while( rs_05.next() )
							{
								sTT_NO = rs_05.getString("tt_no");
							}
		
						}
		
						sbQuery = new StringBuffer(10);
						sbQuery.append(" SELECT charge_no, rf1, rf2        ");  
						sbQuery.append(" FROM caresultdb.ca_rf_result     ");
						sbQuery.append(" WHERE charge_no in ('"+ smtlno +"', '"+ smtlno2 +"')   ");
						sbQuery.append(" ORDER BY seq DESC ");
						sbQuery.append(" LIMIT 1 ");
						ps_05 = con.prepareStatement( sbQuery.toString() );
						rs_05 = ps_05.executeQuery();
						while( rs_05.next() )
						{
							rf1 = rs_05.getString("rf1");
							rf2 = rs_05.getString("rf2");
						}
		
						if( sLF_CR_NO == null || "null".equals(sLF_CR_NO) ) sLF_CR_NO = "";
						if( sRH_CR_NO == null || "null".equals(sRH_CR_NO) ) sRH_CR_NO = "";
						if( sCC_I_CR_NO == null || "null".equals(sCC_I_CR_NO) ) sCC_I_CR_NO = "";
						if( sCC_O_CR_NO == null || "null".equals(sCC_I_CR_NO) ) sCC_O_CR_NO = "";
		
						//////////////////////////////////////////////////////////////
						// MES, PC Server uCube Start Monitoring Data Insert
						//////////////////////////////////////////////////////////////				
						hmData.put("FLAG",  "1");
						hmData.put("SNDR_INFORM_EDIT_DATE",  sndr_inform_edit_date);
						hmData.put("MTL_NO",  smtlno);
						hmData.put("MTL_NO2", smtlno2_org);
						hmData.put("PRP_CHARGE_NO",  sPlanCharegNo);
						hmData.put("PRP_CHARGE_NO2", prpChargeNo2_org);
						hmData.put("PLAN_CHARGE_NO", rs.getString("PLAN_CHARGE_NO"));
						hmData.put("SM_LD_BLW_METH_TP", rs.getString("SM_LD_BLW_METH_TP"));
						hmData.put("SM_STEEL_GRD_N", rs.getString("SM_STEEL_GRD_N"));
						hmData.put("DSTL_LAD_NUM", dstl_lad_num);
						hmData.put("SM_2ND_RFN_CD", rs.getString("SM_2ND_RFN_CD"));
						hmData.put("FCE_NUM", rs.getString("FCE_NUM"));
						
						hmData.put("SM_SREF_ORI_OP_TP", prks.getSmSref( rs.getString("SM_SREF_ORI_OP_TP") )  );
						hmData.put("SM_SREF_FNL_OP_TP", prks.getSmSref( rs.getString("SM_SREF_FNL_OP_TP") )  );
						hmData.put("MCC", rs.getString("MCC"));
						hmData.put("LD_FCE_OX_BW_SAT_DT", prks.getTime_01( rs.getString("LD_FCE_OX_BW_SAT_DT") ));
						hmData.put("LD_FCE_OX_BW_SAT_DT1", prks.getTime_01( rs.getString("LD_FCE_OX_BW_SAT_DT1") ));
						hmData.put("TAPP_DN_DATE", prks.getTime_01( rs.getString("TAPP_DN_DATE") ));
						hmData.put("TAPP_DN_DATE1", prks.getTime_01( rs.getString("TAPP_DN_DATE1") ));
						hmData.put("BAP_SAT_DT", prks.getTime_01( rs.getString("BAP_SAT_DT") ));
						hmData.put("BAP_SAT_DT1", prks.getTime_01( rs.getString("BAP_SAT_DT1") ));
						hmData.put("BAP_DN_DT", prks.getTime_01( rs.getString("BAP_DN_DT") ));
						hmData.put("BAP_DN_DT1", prks.getTime_01( rs.getString("BAP_DN_DT1") ));
						
						hmData.put("LF_CR_NO", ""); // kaist
						
						hmData.put("LF_SAT_DT", prks.getTime_01( rs.getString("LF_SAT_DT") ));
						hmData.put("LF_SAT_DT1", prks.getTime_01( rs.getString("LF_SAT_DT1") ));
						hmData.put("LF_DN_DT", prks.getTime_01( rs.getString("LF_DN_DT") ));
						hmData.put("LF_DN_DT1", prks.getTime_01( rs.getString("LF_DN_DT1") ));
						
						hmData.put("RH_CR_NO", ""); // kaist
						
						hmData.put("RH_VACCUM_SAT_DT", prks.getTime_01( rs.getString("RH_VACCUM_SAT_DT") ));
						hmData.put("RH_VACCUM_SAT_DT1", prks.getTime_01( rs.getString("RH_VACCUM_SAT_DT1") ));
						hmData.put("RH_VACCUM_DN_DT", prks.getTime_01( rs.getString("RH_VACCUM_DN_DT") ));
						hmData.put("RH_VACCUM_DN_DT1", prks.getTime_01( rs.getString("RH_VACCUM_DN_DT1") ));
						hmData.put("SM_SREF_CAST_SAT_STAG_TM", rs.getString("SM_SREF_CAST_SAT_STAG_TM"));
						hmData.put("SM_SREF_CAST_SAT_STAG_TM1", rs.getString("SM_SREF_CAST_SAT_STAG_TM1"));
						hmData.put("LAD_INJ_SAT_DT", prks.getTime_01( rs.getString("LAD_INJ_SAT_DT") ));
						hmData.put("LAD_INJ_SAT_DT1", prks.getTime_01( rs.getString("LAD_INJ_SAT_DT1") ));
						
						hmData.put("CC_I_CR_NO", ""); // kaist
						
						hmData.put("CAST_TM", rs.getString("CAST_TM"));
						hmData.put("CAST_TM1", rs.getString("CAST_TM1"));
						
						hmData.put("CC_O_CR_NO", ""); // kaist
						
						hmData.put("SM_SREF_IS_STAY_LT1", rs.getString("SM_SREF_IS_STAY_LT1"));
						
						hmData.put("TT_NO", ""); // kaist
						
						
						//////////////////////////////////////////////////////////////
						// MES, PC Server Kaist Start Monitoring Data Insert
						//////////////////////////////////////////////////////////////
						hmKaist.put("FLAG",  "2");
						hmKaist.put("SNDR_INFORM_EDIT_DATE",  sndr_inform_edit_date);
						hmKaist.put("MTL_NO",  smtlno);
						hmKaist.put("MTL_NO2", smtlno2_org);
						hmKaist.put("PRP_CHARGE_NO",  sPlanCharegNo);
						hmKaist.put("PRP_CHARGE_NO2", prpChargeNo2_org);
						hmKaist.put("PLAN_CHARGE_NO", rs.getString("PLAN_CHARGE_NO"));
						
						hmKaist.put("SM_LD_BLW_METH_TP", rs.getString("SM_LD_BLW_METH_TP"));
						hmKaist.put("SM_STEEL_GRD_N", rs.getString("SM_STEEL_GRD_N"));
						
						hmKaist.put("DSTL_LAD_NUM", sCa_ld_no);  // kaist
						
						hmKaist.put("SM_2ND_RFN_CD", rs.getString("SM_2ND_RFN_CD"));
						hmKaist.put("FCE_NUM", rs.getString("FCE_NUM"));
						
						hmKaist.put("SM_SREF_ORI_OP_TP", rf1 ); // kaist
						hmKaist.put("SM_SREF_FNL_OP_TP", rf2 ); // kaist
						
						hmKaist.put("MCC", rs.getString("MCC"));
						hmKaist.put("LD_FCE_OX_BW_SAT_DT", prks.getTime_01( rs.getString("LD_FCE_OX_BW_SAT_DT") ));
						hmKaist.put("LD_FCE_OX_BW_SAT_DT1", prks.getTime_01( rs.getString("LD_FCE_OX_BW_SAT_DT1") ));
						hmKaist.put("TAPP_DN_DATE", prks.getTime_01( rs.getString("TAPP_DN_DATE") ));
						hmKaist.put("TAPP_DN_DATE1", prks.getTime_01( rs.getString("TAPP_DN_DATE1") ));
						hmKaist.put("BAP_SAT_DT", prks.getTime_01( rs.getString("BAP_SAT_DT") ));
						hmKaist.put("BAP_SAT_DT1", prks.getTime_01( rs.getString("BAP_SAT_DT1") ));
						hmKaist.put("BAP_DN_DT", prks.getTime_01( rs.getString("BAP_DN_DT") ));
						hmKaist.put("BAP_DN_DT1", prks.getTime_01( rs.getString("BAP_DN_DT1") ));
						
						hmKaist.put("LF_CR_NO", sLF_CR_NO); // kaist
						
						hmKaist.put("LF_SAT_DT", prks.getTime_01( rs.getString("LF_SAT_DT") ));
						hmKaist.put("LF_SAT_DT1", prks.getTime_01( rs.getString("LF_SAT_DT1") ));
						hmKaist.put("LF_DN_DT", prks.getTime_01( rs.getString("LF_DN_DT") ));
						hmKaist.put("LF_DN_DT1", prks.getTime_01( rs.getString("LF_DN_DT1") ));
						
						hmKaist.put("RH_CR_NO", sRH_CR_NO); // kaist
						
						hmKaist.put("RH_VACCUM_SAT_DT", prks.getTime_01( rs.getString("RH_VACCUM_SAT_DT") ));
						hmKaist.put("RH_VACCUM_SAT_DT1", prks.getTime_01( rs.getString("RH_VACCUM_SAT_DT1") ));
						hmKaist.put("RH_VACCUM_DN_DT", prks.getTime_01( rs.getString("RH_VACCUM_DN_DT") ));
						hmKaist.put("RH_VACCUM_DN_DT1", prks.getTime_01( rs.getString("RH_VACCUM_DN_DT1") ));
						hmKaist.put("SM_SREF_CAST_SAT_STAG_TM", rs.getString("SM_SREF_CAST_SAT_STAG_TM"));
						hmKaist.put("SM_SREF_CAST_SAT_STAG_TM1", rs.getString("SM_SREF_CAST_SAT_STAG_TM1"));
						hmKaist.put("LAD_INJ_SAT_DT", prks.getTime_01( rs.getString("LAD_INJ_SAT_DT") ));
						hmKaist.put("LAD_INJ_SAT_DT1", prks.getTime_01( rs.getString("LAD_INJ_SAT_DT1") ));
						
						hmKaist.put("CC_I_CR_NO", sCC_I_CR_NO); // kaist
						
						hmKaist.put("CAST_TM", rs.getString("CAST_TM"));
						hmKaist.put("CAST_TM1", rs.getString("CAST_TM1"));
						
						hmKaist.put("CC_O_CR_NO", sCC_O_CR_NO); // kaist
						
						hmKaist.put("SM_SREF_IS_STAY_LT1", rs.getString("SM_SREF_IS_STAY_LT1"));
						
						hmKaist.put("TT_NO", sTT_NO); // kaist
						
						//////////////////////////////////////////////////////////////
						// Insert, Update
						//////////////////////////////////////////////////////////////
						
						ps_03 = con.prepareStatement( sbCountQuery.toString() );
						ps_03.setString(1, (String) hmData.get("FLAG") );
						ps_03.setString(2, (String) hmData.get("MTL_NO2") );
						rs_03 = ps_03.executeQuery();
						while( rs_03.next() )
						{
							rowCnt = rs_03.getInt("CNT");
						}
						
						System.out.println("[rowCnt : "+ rowCnt +"]["+ (String) hmData.get("FLAG") +"]["+ (String) hmData.get("MTL_NO2") +"]");
						
						if( rowCnt == 0 )
						{
							ps_01 = con.prepareStatement( sbInsertQuery.toString() );
							ps_01.setString(1, (String) hmData.get("FLAG") );
							ps_01.setString(2, (String) hmData.get("SNDR_INFORM_EDIT_DATE") );
							ps_01.setString(3, (String) hmData.get("MTL_NO") );
							ps_01.setString(4, (String) hmData.get("MTL_NO2") );
							ps_01.setString(5, (String) hmData.get("PRP_CHARGE_NO") );
							ps_01.setString(6, (String) hmData.get("PRP_CHARGE_NO2") );
							ps_01.setString(7, (String) hmData.get("PLAN_CHARGE_NO") );
							ps_01.setString(8, (String) hmData.get("SM_LD_BLW_METH_TP") );
							ps_01.setString(9, (String) hmData.get("SM_STEEL_GRD_N") );
							ps_01.setString(10, (String) hmData.get("DSTL_LAD_NUM") );
							ps_01.setString(11, (String) hmData.get("SM_2ND_RFN_CD") );
							ps_01.setString(12, (String) hmData.get("FCE_NUM") );
							ps_01.setString(13, (String) hmData.get("SM_SREF_ORI_OP_TP") );
							ps_01.setString(14, (String) hmData.get("SM_SREF_FNL_OP_TP") );
							ps_01.setString(15, (String) hmData.get("MCC") );
							ps_01.setString(16, (String) hmData.get("LD_FCE_OX_BW_SAT_DT") );
							ps_01.setString(17, (String) hmData.get("LD_FCE_OX_BW_SAT_DT1") );
							ps_01.setString(18, (String) hmData.get("TAPP_DN_DATE") );
							ps_01.setString(19, (String) hmData.get("TAPP_DN_DATE1") );
							ps_01.setString(20, (String) hmData.get("BAP_SAT_DT") );
							ps_01.setString(21, (String) hmData.get("BAP_SAT_DT1") );
							ps_01.setString(22, (String) hmData.get("BAP_DN_DT") );
							ps_01.setString(23, (String) hmData.get("BAP_DN_DT1") );
							ps_01.setString(24, (String) hmData.get("LF_CR_NO") );
							ps_01.setString(25, (String) hmData.get("LF_SAT_DT") );
							ps_01.setString(26, (String) hmData.get("LF_SAT_DT1") );
							ps_01.setString(27, (String) hmData.get("LF_DN_DT") );
							ps_01.setString(28, (String) hmData.get("LF_DN_DT1") );
							ps_01.setString(29, (String) hmData.get("RH_CR_NO") );
							ps_01.setString(30, (String) hmData.get("RH_VACCUM_SAT_DT") );
							ps_01.setString(31, (String) hmData.get("RH_VACCUM_SAT_DT1") );
							ps_01.setString(32, (String) hmData.get("RH_VACCUM_DN_DT") );
							ps_01.setString(33, (String) hmData.get("RH_VACCUM_DN_DT1") );
							ps_01.setString(34, (String) hmData.get("SM_SREF_CAST_SAT_STAG_TM") );
							ps_01.setString(35, (String) hmData.get("SM_SREF_CAST_SAT_STAG_TM1") );
							ps_01.setString(36, (String) hmData.get("LAD_INJ_SAT_DT") );
							ps_01.setString(37, (String) hmData.get("LAD_INJ_SAT_DT1") );
							ps_01.setString(38, (String) hmData.get("CC_I_CR_NO") );
							ps_01.setString(39, (String) hmData.get("CAST_TM") );
							ps_01.setString(40, (String) hmData.get("CAST_TM1") );
							ps_01.setString(41, (String) hmData.get("CC_O_CR_NO") );
							ps_01.setString(42, (String) hmData.get("SM_SREF_IS_STAY_LT1") );
							ps_01.setString(43, (String) hmData.get("TT_NO") );
							ret1 = ps_01.executeUpdate();
							ps_01.clearParameters();
							
		
							ps_02 = con.prepareStatement( sbInsertQuery.toString() );
							ps_02.setString(1, (String) hmKaist.get("FLAG") );
							ps_02.setString(2, (String) hmKaist.get("SNDR_INFORM_EDIT_DATE") );
							ps_02.setString(3, (String) hmKaist.get("MTL_NO") );
							ps_02.setString(4, (String) hmKaist.get("MTL_NO2") );
							ps_02.setString(5, (String) hmKaist.get("PRP_CHARGE_NO") );
							ps_02.setString(6, (String) hmKaist.get("PRP_CHARGE_NO2") );
							ps_02.setString(7, (String) hmKaist.get("PLAN_CHARGE_NO") );
							ps_02.setString(8, (String) hmKaist.get("SM_LD_BLW_METH_TP") );
							ps_02.setString(9, (String) hmKaist.get("SM_STEEL_GRD_N") );
							ps_02.setString(10, (String) hmKaist.get("DSTL_LAD_NUM") );
							ps_02.setString(11, (String) hmKaist.get("SM_2ND_RFN_CD") );
							ps_02.setString(12, (String) hmKaist.get("FCE_NUM") );
							ps_02.setString(13, (String) hmKaist.get("SM_SREF_ORI_OP_TP") );
							ps_02.setString(14, (String) hmKaist.get("SM_SREF_FNL_OP_TP") );
							ps_02.setString(15, (String) hmKaist.get("MCC") );
							ps_02.setString(16, (String) hmKaist.get("LD_FCE_OX_BW_SAT_DT") );
							ps_02.setString(17, (String) hmKaist.get("LD_FCE_OX_BW_SAT_DT1") );
							ps_02.setString(18, (String) hmKaist.get("TAPP_DN_DATE") );
							ps_02.setString(19, (String) hmKaist.get("TAPP_DN_DATE1") );
							ps_02.setString(20, (String) hmKaist.get("BAP_SAT_DT") );
							ps_02.setString(21, (String) hmKaist.get("BAP_SAT_DT1") );
							ps_02.setString(22, (String) hmKaist.get("BAP_DN_DT") );
							ps_02.setString(23, (String) hmKaist.get("BAP_DN_DT1") );
							ps_02.setString(24, (String) hmKaist.get("LF_CR_NO") );
							ps_02.setString(25, (String) hmKaist.get("LF_SAT_DT") );
							ps_02.setString(26, (String) hmKaist.get("LF_SAT_DT1") );
							ps_02.setString(27, (String) hmKaist.get("LF_DN_DT") );
							ps_02.setString(28, (String) hmKaist.get("LF_DN_DT1") );
							ps_02.setString(29, (String) hmKaist.get("RH_CR_NO") );
							ps_02.setString(30, (String) hmKaist.get("RH_VACCUM_SAT_DT") );
							ps_02.setString(31, (String) hmKaist.get("RH_VACCUM_SAT_DT1") );
							ps_02.setString(32, (String) hmKaist.get("RH_VACCUM_DN_DT") );
							ps_02.setString(33, (String) hmKaist.get("RH_VACCUM_DN_DT1") );
							ps_02.setString(34, (String) hmKaist.get("SM_SREF_CAST_SAT_STAG_TM") );
							ps_02.setString(35, (String) hmKaist.get("SM_SREF_CAST_SAT_STAG_TM1") );
							ps_02.setString(36, (String) hmKaist.get("LAD_INJ_SAT_DT") );
							ps_02.setString(37, (String) hmKaist.get("LAD_INJ_SAT_DT1") );
							ps_02.setString(38, (String) hmKaist.get("CC_I_CR_NO") );
							ps_02.setString(39, (String) hmKaist.get("CAST_TM") );
							ps_02.setString(40, (String) hmKaist.get("CAST_TM1") );
							ps_02.setString(41, (String) hmKaist.get("CC_O_CR_NO") );
							ps_02.setString(42, (String) hmKaist.get("SM_SREF_IS_STAY_LT1") );
							ps_02.setString(43, (String) hmKaist.get("TT_NO") );
							ret2 = ps_02.executeUpdate();
							ps_02.clearParameters();
						}
						else if( rowCnt > 0 )
						{
		
							ps_01 = con.prepareStatement( sbUpdateQuery.toString() );
							ps_01.setString(1, (String) hmData.get("FLAG") );
							ps_01.setString(2, (String) hmData.get("SNDR_INFORM_EDIT_DATE") );
							ps_01.setString(3, (String) hmData.get("MTL_NO") );
							ps_01.setString(4, (String) hmData.get("PRP_CHARGE_NO") );
							ps_01.setString(5, (String) hmData.get("PRP_CHARGE_NO2") );
							ps_01.setString(6, (String) hmData.get("PLAN_CHARGE_NO") );
							ps_01.setString(7, (String) hmData.get("SM_LD_BLW_METH_TP") );
							ps_01.setString(8, (String) hmData.get("SM_STEEL_GRD_N") );
							ps_01.setString(9, (String) hmData.get("DSTL_LAD_NUM") );
							ps_01.setString(10, (String) hmData.get("SM_2ND_RFN_CD") );
							ps_01.setString(11, (String) hmData.get("FCE_NUM") );
							ps_01.setString(12, (String) hmData.get("SM_SREF_ORI_OP_TP") );
							ps_01.setString(13, (String) hmData.get("SM_SREF_FNL_OP_TP") );
							ps_01.setString(14, (String) hmData.get("MCC") );
							ps_01.setString(15, (String) hmData.get("LD_FCE_OX_BW_SAT_DT") );
							ps_01.setString(16, (String) hmData.get("LD_FCE_OX_BW_SAT_DT1") );
							ps_01.setString(17, (String) hmData.get("TAPP_DN_DATE") );
							ps_01.setString(18, (String) hmData.get("TAPP_DN_DATE1") );
							ps_01.setString(19, (String) hmData.get("BAP_SAT_DT") );
							ps_01.setString(20, (String) hmData.get("BAP_SAT_DT1") );
							ps_01.setString(21, (String) hmData.get("BAP_DN_DT") );
							ps_01.setString(22, (String) hmData.get("BAP_DN_DT1") );
							ps_01.setString(23, (String) hmData.get("LF_CR_NO") );
							ps_01.setString(24, (String) hmData.get("LF_SAT_DT") );
							ps_01.setString(25, (String) hmData.get("LF_SAT_DT1") );
							ps_01.setString(26, (String) hmData.get("LF_DN_DT") );
							ps_01.setString(27, (String) hmData.get("LF_DN_DT1") );
							ps_01.setString(28, (String) hmData.get("RH_CR_NO") );
							ps_01.setString(29, (String) hmData.get("RH_VACCUM_SAT_DT") );
							ps_01.setString(30, (String) hmData.get("RH_VACCUM_SAT_DT1") );
							ps_01.setString(31, (String) hmData.get("RH_VACCUM_DN_DT") );
							ps_01.setString(32, (String) hmData.get("RH_VACCUM_DN_DT1") );
							ps_01.setString(33, (String) hmData.get("SM_SREF_CAST_SAT_STAG_TM") );
							ps_01.setString(34, (String) hmData.get("SM_SREF_CAST_SAT_STAG_TM1") );
							ps_01.setString(35, (String) hmData.get("LAD_INJ_SAT_DT") );
							ps_01.setString(36, (String) hmData.get("LAD_INJ_SAT_DT1") );
							ps_01.setString(37, (String) hmData.get("CC_I_CR_NO") );
							ps_01.setString(38, (String) hmData.get("CAST_TM") );
							ps_01.setString(39, (String) hmData.get("CAST_TM1") );
							ps_01.setString(40, (String) hmData.get("CC_O_CR_NO") );
							ps_01.setString(41, (String) hmData.get("SM_SREF_IS_STAY_LT1") );
							ps_01.setString(42, (String) hmData.get("TT_NO") );
							ps_01.setString(43, (String) hmData.get("MTL_NO2") );
							ps_01.setString(44, (String) hmData.get("FLAG") );
							ret1 = ps_01.executeUpdate();
							ps_01.clearParameters();
							
		
		
							ps_02 = con.prepareStatement( sbUpdateQuery.toString() );
							ps_02.setString(1, (String) hmKaist.get("FLAG") );
							ps_02.setString(2, (String) hmKaist.get("SNDR_INFORM_EDIT_DATE") );
							ps_02.setString(3, (String) hmKaist.get("MTL_NO") );
							ps_02.setString(4, (String) hmKaist.get("PRP_CHARGE_NO") );
							ps_02.setString(5, (String) hmKaist.get("PRP_CHARGE_NO2") );
							ps_02.setString(6, (String) hmKaist.get("PLAN_CHARGE_NO") );
							ps_02.setString(7, (String) hmKaist.get("SM_LD_BLW_METH_TP") );
							ps_02.setString(8, (String) hmKaist.get("SM_STEEL_GRD_N") );
							ps_02.setString(9, (String) hmKaist.get("DSTL_LAD_NUM") );
							ps_02.setString(10, (String) hmKaist.get("SM_2ND_RFN_CD") );
							ps_02.setString(11, (String) hmKaist.get("FCE_NUM") );
							ps_02.setString(12, (String) hmKaist.get("SM_SREF_ORI_OP_TP") );
							ps_02.setString(13, (String) hmKaist.get("SM_SREF_FNL_OP_TP") );
							ps_02.setString(14, (String) hmKaist.get("MCC") );
							ps_02.setString(15, (String) hmKaist.get("LD_FCE_OX_BW_SAT_DT") );
							ps_02.setString(16, (String) hmKaist.get("LD_FCE_OX_BW_SAT_DT1") );
							ps_02.setString(17, (String) hmKaist.get("TAPP_DN_DATE") );
							ps_02.setString(18, (String) hmKaist.get("TAPP_DN_DATE1") );
							ps_02.setString(19, (String) hmKaist.get("BAP_SAT_DT") );
							ps_02.setString(20, (String) hmKaist.get("BAP_SAT_DT1") );
							ps_02.setString(21, (String) hmKaist.get("BAP_DN_DT") );
							ps_02.setString(22, (String) hmKaist.get("BAP_DN_DT1") );
							ps_02.setString(23, (String) hmKaist.get("LF_CR_NO") );
							ps_02.setString(24, (String) hmKaist.get("LF_SAT_DT") );
							ps_02.setString(25, (String) hmKaist.get("LF_SAT_DT1") );
							ps_02.setString(26, (String) hmKaist.get("LF_DN_DT") );
							ps_02.setString(27, (String) hmKaist.get("LF_DN_DT1") );
							ps_02.setString(28, (String) hmKaist.get("RH_CR_NO") );
							ps_02.setString(29, (String) hmKaist.get("RH_VACCUM_SAT_DT") );
							ps_02.setString(30, (String) hmKaist.get("RH_VACCUM_SAT_DT1") );
							ps_02.setString(31, (String) hmKaist.get("RH_VACCUM_DN_DT") );
							ps_02.setString(32, (String) hmKaist.get("RH_VACCUM_DN_DT1") );
							ps_02.setString(33, (String) hmKaist.get("SM_SREF_CAST_SAT_STAG_TM") );
							ps_02.setString(34, (String) hmKaist.get("SM_SREF_CAST_SAT_STAG_TM1") );
							ps_02.setString(35, (String) hmKaist.get("LAD_INJ_SAT_DT") );
							ps_02.setString(36, (String) hmKaist.get("LAD_INJ_SAT_DT1") );
							ps_02.setString(37, (String) hmKaist.get("CC_I_CR_NO") );
							ps_02.setString(38, (String) hmKaist.get("CAST_TM") );
							ps_02.setString(39, (String) hmKaist.get("CAST_TM1") );
							ps_02.setString(40, (String) hmKaist.get("CC_O_CR_NO") );
							ps_02.setString(41, (String) hmKaist.get("SM_SREF_IS_STAY_LT1") );
							ps_02.setString(42, (String) hmKaist.get("TT_NO") );
							ps_02.setString(43, (String) hmKaist.get("MTL_NO2") );
							ps_02.setString(44, (String) hmKaist.get("FLAG") );
							ret2 = ps_02.executeUpdate();
							ps_02.clearParameters();
						}
					}
					
					System.out.println("Kaist Start Monitoring Data Insert/Update Success!");
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
						if( rs_02 != null ) rs_02.close();
						if( rs_03 != null ) rs_03.close();
						if( rs_04 != null ) rs_04.close();
						if( rs_05 != null ) rs_05.close();
		
						if( ps != null ) ps.close();
						if( ps_01 != null ) ps_01.close();
						if( ps_02 != null ) ps_02.close();
						if( ps_03 != null ) ps_03.close();
						if( ps_04 != null ) ps_04.close();
						if( ps_05 != null ) ps_05.close();
		
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
				Thread.sleep(1000 * 60 * 5);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}


	}
	
	public String getTime_01(String sParm)
	{
		StringBuffer sbRet = new StringBuffer(10);

		if( sParm != null && !"".equals(sParm) && sParm.length() == 14 )
		{
			sbRet.append(sParm.substring(8,10));
			sbRet.append(":");
			sbRet.append(sParm.substring(10,12));
		}

		return sbRet.toString();
	}

	public String getSmSref(String sParm)
	{
		String sRet = "";

		if( sParm != null && !"".equals(sParm) )
		{
			if( "R1".equals(sParm) )		sRet = "1RH";
			else if( "R2".equals(sParm) )	sRet = "2RH";
			else if( "R3".equals(sParm) )	sRet = "3RH";
			else if( "T1".equals(sParm) )	sRet = "LF";
		}
		return sRet;
	}

}


