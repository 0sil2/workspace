<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>

<%
String jdbc_driver = "com.mysql.jdbc.Driver";
String jdbc_url = "jdbc:mysql://192.168.1.12:3306/uwbextdatadb?serverTime=Asia/Seoul&useSSL=false&useUnicode=true&characterEncoding=utf8";
String db_id = "root";
String db_pw = "localsense";

Connection con = null;
PreparedStatement ps = null;
ResultSet rs = null;

PreparedStatement psSub = null;
ResultSet rsSub = null;

String s1HCraneCode = "";
String s1HcWeight = "";
String s1HrWeight = "";
String s1HmtlNo = "";
String s1HStatus = "";

String s2HCraneCode = "";
String s2HcWeight = "";
String s2HrWeight = "";
String s2HmtlNo = "";
String s2HStatus = "";

String s1SCraneCode = "";
String s1ScWeight = "";
String s1SrWeight = "";
String s1SmtlNo = "";
String s1SStatus = "";

String s1LCraneCode = "";
String s1LcWeight = "";
String s1LrWeight = "";
String s1LmtlNo = "";
String s1LStatus = "";

String s2LCraneCode = "";
String s2LcWeight = "";
String s2LrWeight = "";
String s2LmtlNo = "";
String s2LStatus = "";

String s1TCraneCode = "";
String s1TcWeight = "";
String s1TrWeight = "";
String s1TmtlNo = "";
String s1TStatus = "";

String s2TCraneCode = "";
String s2TcWeight = "";
String s2TrWeight = "";
String s2TmtlNo = "";
String s2TStatus = "";

String s3TCraneCode = "";
String s3TcWeight = "";
String s3TrWeight = "";
String s3TmtlNo = "";
String s3TStatus = "";

String s4TCraneCode = "";
String s4TcWeight = "";
String s4TrWeight = "";
String s4TmtlNo = "";
String s4TStatus = "";

try
{
	Class.forName(jdbc_driver);
	con = DriverManager.getConnection(jdbc_url, db_id, db_pw);

	StringBuffer subQuery = new StringBuffer(10);
	subQuery.append("  select T2.seq, T2.cranecode, T3.cweight  ");
	subQuery.append("  FROM  ");
	subQuery.append("  (  ");
	subQuery.append("    select cranecode, max(seq) as seq  ");
	subQuery.append("    FROM   ");
	subQuery.append("    (  ");
	subQuery.append("      select cranecode, seq  ");
	subQuery.append("      from uwb_extcombination  ");
	subQuery.append("      where cranecode = ?  and cweight not in ('0', '0.0')  ");
	subQuery.append("    ) AS T1  ");
	subQuery.append("    group by cranecode  ");
	subQuery.append("  ) AS T2 inner join uwb_extcombination as T3 ON T2.seq = T3.seq  ");

	StringBuffer sbQuery = new StringBuffer(10);
	sbQuery.append("  select T2.seq, T2.cranecode, T3.cweight, T3.rweight, T3.mtlno, T3.ladle_status  ");
	sbQuery.append("  FROM  ");
	sbQuery.append("  (  ");
	sbQuery.append("    select cranecode, max(seq) as seq  ");
	sbQuery.append("    FROM   ");
	sbQuery.append("    (  ");
	sbQuery.append("      select cranecode, seq  ");
	sbQuery.append("      from uwb_extcombination  ");
	sbQuery.append("      order by seq desc  ");
	//sbQuery.append("      limit 0, 200  ");
	sbQuery.append("    ) AS T1  ");
	sbQuery.append("    group by cranecode  ");
	sbQuery.append("  ) AS T2 inner join uwb_extcombination as T3 ON T2.seq = T3.seq  ");

	ps = con.prepareStatement(sbQuery.toString());
	rs = ps.executeQuery();
	while( rs.next()  )
	{
		if( "1H".equals(rs.getString("cranecode")) )
		{
			s1HCraneCode = rs.getString("cranecode");
			s1HcWeight = rs.getString("cweight");
			s1HrWeight = rs.getString("rweight");
			s1HmtlNo = rs.getString("mtlno");
			s1HStatus = rs.getString("ladle_status");

			if( "0".equals(s1HcWeight) || "0.0".equals(s1HcWeight)  )
			{
				psSub = con.prepareStatement(subQuery.toString());
				psSub.setString(1, "1H");
				rsSub = psSub.executeQuery();
				while( rsSub.next() )
				{
					s1HcWeight = rsSub.getString("cweight");
				}
			}
		}
		else if( "2H".equals(rs.getString("cranecode")) )
		{
			s2HCraneCode = rs.getString("cranecode");
			s2HcWeight = rs.getString("cweight");
			s2HrWeight = rs.getString("rweight");
			s2HmtlNo = rs.getString("mtlno");
			s2HStatus = rs.getString("ladle_status");

			if( "0".equals(s2HcWeight) || "0.0".equals(s2HcWeight)  )
			{
				psSub = con.prepareStatement(subQuery.toString());
				psSub.setString(1, "2H");
				rsSub = psSub.executeQuery();
				while( rsSub.next() )
				{
					s2HcWeight = rsSub.getString("cweight");
				}
			}
		}
		else if( "1S".equals(rs.getString("cranecode")) )
		{
			s1SCraneCode = rs.getString("cranecode");
			s1ScWeight = rs.getString("cweight");
			s1SrWeight = rs.getString("rweight");
			s1SmtlNo = rs.getString("mtlno");
			s1SStatus = rs.getString("ladle_status");

			if( "0".equals(s1ScWeight) || "0.0".equals(s1ScWeight)  )
			{
				psSub = con.prepareStatement(subQuery.toString());
				psSub.setString(1, "1S");
				rsSub = psSub.executeQuery();
				while( rsSub.next() )
				{
					s1ScWeight = rsSub.getString("cweight");
				}
			}
		}
		else if( "1L".equals(rs.getString("cranecode")) )
		{
			s1LCraneCode = rs.getString("cranecode");
			s1LcWeight = rs.getString("cweight");
			s1LrWeight = rs.getString("rweight");
			s1LmtlNo = rs.getString("mtlno");
			s1LStatus = rs.getString("ladle_status");

			if( "0".equals(s1LcWeight) || "0.0".equals(s1LcWeight)  )
			{
				psSub = con.prepareStatement(subQuery.toString());
				psSub.setString(1, "1L");
				rsSub = psSub.executeQuery();
				while( rsSub.next() )
				{
					s1LcWeight = rsSub.getString("cweight");
				}
			}
		}
		else if( "2L".equals(rs.getString("cranecode")) )
		{
			s2LCraneCode = rs.getString("cranecode");
			s2LcWeight = rs.getString("cweight");
			s2LrWeight = rs.getString("rweight");
			s2LmtlNo = rs.getString("mtlno");
			s2LStatus = rs.getString("ladle_status");

			if( "0".equals(s2LcWeight) || "0.0".equals(s2LcWeight)  )
			{
				psSub = con.prepareStatement(subQuery.toString());
				psSub.setString(1, "2L");
				rsSub = psSub.executeQuery();
				while( rsSub.next() )
				{
					s2LcWeight = rsSub.getString("cweight");
				}
			}
		}
		else if( "1T".equals(rs.getString("cranecode")) )
		{
			s1TCraneCode = rs.getString("cranecode");
			s1TcWeight = rs.getString("cweight");
			s1TrWeight = rs.getString("rweight");
			s1TmtlNo = rs.getString("mtlno");
			s1TStatus = rs.getString("ladle_status");

			if( "0".equals(s1TcWeight) || "0.0".equals(s1TcWeight)  )
			{
				psSub = con.prepareStatement(subQuery.toString());
				psSub.setString(1, "1T");
				rsSub = psSub.executeQuery();
				while( rsSub.next() )
				{
					s1TcWeight = rsSub.getString("cweight");
				}
			}
		}
		else if( "2T".equals(rs.getString("cranecode")) )
		{
			s2TCraneCode = rs.getString("cranecode");
			s2TcWeight = rs.getString("cweight");
			s2TrWeight = rs.getString("rweight");
			s2TmtlNo = rs.getString("mtlno");
			s2TStatus = rs.getString("ladle_status");

			if( "0".equals(s2TcWeight) || "0.0".equals(s2TcWeight)  )
			{
				psSub = con.prepareStatement(subQuery.toString());
				psSub.setString(1, "2T");
				rsSub = psSub.executeQuery();
				while( rsSub.next() )
				{
					s2TcWeight = rsSub.getString("cweight");
				}
			}
		}
		else if( "3T".equals(rs.getString("cranecode")) )
		{
			s3TCraneCode = rs.getString("cranecode");
			s3TcWeight = rs.getString("cweight");
			s3TrWeight = rs.getString("rweight");
			s3TmtlNo = rs.getString("mtlno");
			s3TStatus = rs.getString("ladle_status");

			if( "0".equals(s3TcWeight) || "0.0".equals(s3TcWeight)  )
			{
				psSub = con.prepareStatement(subQuery.toString());
				psSub.setString(1, "3T");
				rsSub = psSub.executeQuery();
				while( rsSub.next() )
				{
					s3TcWeight = rsSub.getString("cweight");
				}
			}
		}
		else if( "4T".equals(rs.getString("cranecode")) )
		{
			s4TCraneCode = rs.getString("cranecode");
			s4TcWeight = rs.getString("cweight");
			s4TrWeight = rs.getString("rweight");
			s4TmtlNo = rs.getString("mtlno");
			s4TStatus = rs.getString("ladle_status");

			if( "0".equals(s4TcWeight) || "0.0".equals(s4TcWeight)  )
			{
				psSub = con.prepareStatement(subQuery.toString());
				psSub.setString(1, "4T");
				rsSub = psSub.executeQuery();
				while( rsSub.next() )
				{
					s4TcWeight = rsSub.getString("cweight");
				}
			}
		}
	}

}
catch (Exception e) 
{
	out.println( "[Exception] " + e.toString() );
	e.printStackTrace();
}
finally
{
	try
	{
		if( rsSub != null ) rsSub.close();
		if( rs != null ) rs.close();
		if( psSub != null ) psSub.close();
		if( ps != null ) ps.close();
		if( con != null ) con.close();
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
} 

%>


<%!
public String ladlestatus(String parm)
{
	String ret = "";

	if( "N".equals(parm) )
	{
		ret = "빈래들";
	}
	else if( "E".equals(parm) )
	{
		ret = "공래들";		
	}
	else if( "F".equals(parm) )
	{
		ret = "영래들";
	}
	else
	{
		ret = "";
	}

	return ret;
}
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>크레인 칭량 시스템</title>
    <!-- css -->
    <link rel="stylesheet" href="./css/style.css">
</head>

<script>
	setTimeout(function(){
	location.reload();
	},5000);
</script>

<body>
    <!-- 헤더 -->
    <header>
        <div style="font-size: 40px; font-weight: bold; text-align: center;padding-top: 30px;">
            2제강 크레인 칭량 시스템
        </div>
    </header>
    <!-- //헤더 -->

    <!-- 공정별 시각 관리 테이블 -->
    <div class="table-wrap">
        <table class="header-table">
            <colgroup>
                <col width="25%">
                <col width="25%">
                <col width="25%">
				<col width="25%">
            </colgroup>
            <thead>
             <tr class="thead-first">
                 <th class="head-idx" style="font-size: 40px; font-weight: bold;">구분</th>
                 <th style="font-size: 40px; font-weight: bold;">계산된 Weight (Ton)</th>
                 <th style="font-size: 40px; font-weight: bold;">실시간 Weight (Ton)</th>
				 <th style="font-size: 40px; font-weight: bold;">래들구분</th>
             </tr>
            </thead>
			<tbody>
				<tr>
					<td class="letter-bold" style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;">1 용선</td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s1HcWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s1HrWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= ladlestatus(s1HStatus) %></td>
				</tr>
				<tr>
					<td class="letter-bold" style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;">2 용선</td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s2HcWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s2HrWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= ladlestatus(s2HStatus) %></td>
				</tr>
				<tr>
					<td class="letter-bold" style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;">1 고철</td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s1ScWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s1SrWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= ladlestatus(s1SStatus) %></td>
				</tr>

				<tr>
					<td class="letter-bold" style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;">1 L/D</td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s1LcWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s1LrWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= ladlestatus(s1LStatus) %></td>
				</tr>
				<tr>
					<td class="letter-bold" style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;">2 L/D</td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s2LcWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s2LrWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= ladlestatus(s2LStatus) %></td>
				</tr>
				<tr>
					<td class="letter-bold" style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;">1 T/M</td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s1TcWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s1TrWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= ladlestatus(s1TStatus) %></td>
				</tr>

				<tr>
					<td class="letter-bold" style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;">2 T/M</td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s2TcWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s2TrWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= ladlestatus(s2TStatus) %></td>
				</tr>
				<tr>
					<td class="letter-bold" style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;">3 T/M</td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s3TcWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s3TrWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= ladlestatus(s3TStatus) %></td>
				</tr>
				<tr>
					<td class="letter-bold" style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;">4 T/M</td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s4TcWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= s4TrWeight %></td>
					<td style="font-size: 40px; font-weight: bold;padding-top: 10px;padding-bottom: 10px;"><%= ladlestatus(s4TStatus) %></td>
				</tr>


			</tbody>
        </table>
    </div>

</body>
</html>