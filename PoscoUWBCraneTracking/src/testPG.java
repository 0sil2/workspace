import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

public class testPG {

	public static void main(String[] args) 
	{
		/*		
		String sHex3 = "0x9D";
		String sHex4 = "0x01";
		String sHex5 = "0x3A";
		String sHex6 = "0x0C";
		String sHex7 = "0x3C";
		String sHex8 = "0x18";
		
		09BD61004E0B02E3E3E380
		09BD000000112B00000000
		09BD
		*/
		
		System.out.println(  String.format("%.2f", (10/3.0))  );
		
		/*
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

		//String exData = "09BD61004E0B02E3E3E380";
		//String exData = "09BD61004E0E3FE3E3E380";
		//String exData = "09BD000000112B00000080";
		//String exData = "09BD61004E0E33E3E3E300";
		//String exData = "09BD61004E0F58E3E3E300";
		String exData = "09BD61004E0B30E3E3E300";
		
		System.out.println(exData.substring(0,4));
		
		if( exData != null && exData.length() == 22 )
		{
			
			exData = exData.substring(4, 22);
			exData = exData.substring(0, 16);
			exLen = exData.length();
			System.out.println( exData );
			for( il = 0 ; il < exLen ; il++)
			{
				if( (il%2) == 1 )
				{
					sHex[iSAr] = sHex[iSAr] + exData.charAt(il);
					iSAr++;
				}
				else
				{
					sHex[iSAr] = "0x" + exData.charAt(il);
				}
			}
		}
		
		for( il = 0 ; il < iDec.length ; il++ )
		{
			iDec[il] =  Integer.decode(sHex[il]);
		}
		
		for( int i=0 ; i<iDec.length ; i++ )
		{
			System.out.println("["+ sHex[i] +"]["+ iDec[i] +"]");
		}
		
		sWeight = Integer.toString((iDec[1] << 8) + iDec[0]) + "." + Integer.toString(iDec[2]);
		sDistance = Integer.toString(iDec[3]) + "." + Integer.toString(iDec[4]);
		sHoistHeight = Double.toString( iDec[5]/10.0 );
		sSpare1 = Integer.toString(iDec[6]);
		sSpare2 = Integer.toString(iDec[7]);
		
		System.out.println("["+sWeight+"]["+sDistance+"]["+sHoistHeight+"]["+sSpare1+"]["+sSpare2+"]");
		*/
		
		//[97.78][11.2][227][227][227]
		//[97.78][14.63][227][227][227]
		//[0.0][17.43][0][0][0]
		//[97.78][14.51][227][227][227]
		//[97.78][15.88][227][227][227]
		//[97.78][11.48][227][227][227]
		
		/*
		String sHex3 = "0x96";
		String sHex4 = "0x00";
		String sHex5 = "0x0C";
		String sHex6 = "0x07";
		String sHex7 = "0x28";
		String sHex8 = "0x10";
		
		
		String sHex3 = "0x61";
		String sHex4 = "0x00";
		String sHex5 = "0x4E";
		String sHex6 = "0x0B";
		String sHex7 = "0x02";
		String sHex8 = "0xE3";
		
		int i3 = Integer.decode(sHex3);
		int i4 = Integer.decode(sHex4);
		int i5 = Integer.decode(sHex5);
		int i6 = Integer.decode(sHex6);
		int i7 = Integer.decode(sHex7);
		int i8 = Integer.decode(sHex8);
		
		int iTot = (i4 << 8) + i3;
		System.out.println(iTot + "." + i5);
		System.out.println(i6 + "." + i7);
		System.out.println(i8);
		*/
	}

}