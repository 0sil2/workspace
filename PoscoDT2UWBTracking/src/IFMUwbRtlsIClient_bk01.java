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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/*
 *   /usr/local/java/jdk1.8.0_321/jre/bin/java IFMUwbRtlsIClient
 */

public class IFMUwbRtlsIClient_bk01 implements Runnable 
{

	private boolean debug = false;
	private int crunchifyRunEveryNSeconds = 300;
	private long lastKnownPosition = 0;
	private boolean shouldIRun = true;
	private File crunchifyFile = null;
	private static int dataCounter = 1;
	private int LoopCnt_01 = 0;
	
	private String filePath = "/usr/local/posco/UwbTagsDataLog/";
	private String curYYYY = "";
	private String curMM = "";
	private String curYYYYMMDD = "";
	private static int setDataCount = 100;
	private static int getDataTotCount = 0;
	private static int setIFDataLen = 50;

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
	
	private String tmp_tagtype = "";       //태그 유형 1 : 크레인, 0 : 휴대형
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
	private String tmp_spare2 = "";			//spare2 (추가2 데이타 존재, 아니면 -99)		
	private String tmp_ymd = "";			//년월일
	private String tmp_hms = "";			//시분초
	
	private Socket socket = null;
	private InetSocketAddress isaSvr = null;
	private OutputStream out = null;
	//private PrintWriter pr = null;

	//private String Poscoict_Svr_Ip = "172.28.79.32";
	//private String Poscoict_Svr_Ip = "192.168.1.12";
	private String Poscoict_Svr_Ip = "172.28.79.153";
	
	private int Poscoict_Svr_Port = 9519;
	
	public IFMUwbRtlsIClient_bk01()
	{
		
	}
	
	public IFMUwbRtlsIClient_bk01(int myInterval) 
	{
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.curYYYY = this.sdfYYYY.format(this.timestamp);
		this.curMM = this.sdfMM.format(this.timestamp);
		this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
		
		String myFile = this.filePath + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +".txt";
		
		this.crunchifyFile = new File(myFile);
		this.crunchifyRunEveryNSeconds = myInterval;
	}
	
	public IFMUwbRtlsIClient_bk01(String myFile, int myInterval) 
	{
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.curYYYY = this.sdfYYYY.format(this.timestamp);
		this.curMM = this.sdfMM.format(this.timestamp);
		this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
		
		myFile = myFile + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +".txt";
		
		this.crunchifyFile = new File(myFile);
		this.crunchifyRunEveryNSeconds = myInterval;
	}

	private void printLine(String message) {
		System.out.println(message);
	}

	public void stopRunning() {
		shouldIRun = false;
	}

	public void run() 
	{	
		
		try 
		{
			
			String myFile = "";
			String oldMyFile = "";
			
			this.socket = new Socket(this.Poscoict_Svr_Ip, this.Poscoict_Svr_Port);

			while (shouldIRun) 
			{
				Thread.sleep(crunchifyRunEveryNSeconds);
				
				this.timestamp = new Timestamp(System.currentTimeMillis());
				this.curYYYY = this.sdfYYYY.format(this.timestamp);
				this.curMM = this.sdfMM.format(this.timestamp);
				this.curYYYYMMDD = this.sdfYYYYMMDD.format(this.timestamp);
				
				myFile = this.filePath + this.curYYYY +"/"+ this.curMM +"/"+ this.curYYYYMMDD +".txt";
				this.crunchifyFile = new File(myFile);

				long fileLength = this.crunchifyFile.length();
				
				System.out.println("myFile >> " + myFile);
				
				if( !oldMyFile.equals(myFile) )
				{
					lastKnownPosition = 0;
				}
				
				if (fileLength > lastKnownPosition) 
				{
					oldMyFile = myFile;
					
					System.out.println("myFile [fileLength : "+ fileLength +"] >>> [lastKnownPosition : "+ lastKnownPosition +"]");
					
					if( this.LoopCnt_01 == 0 )
					{
						this.sbRet = new StringBuffer();
						lastKnownPosition = fileLength -1;
					}

					// Reading and writing file
					RandomAccessFile readWriteFileAccess = new RandomAccessFile(this.crunchifyFile, "r");
					readWriteFileAccess.seek(lastKnownPosition);
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
								
								if( this.arrLen != 15 )
								{
									this.tmp_tagid = "";
									this.tmp_tagtype = "";
									this.tmp_cranecode = "";
									this.tmp_posx = "";
									this.tmp_distance = "";
									this.tmp_hoistheight = "";
									this.tmp_subhoistheight = "";
									this.tmp_spare2 = "";
									this.tmp_weight = "";
								}
								else if( this.arrLen == 15 )
								{
										this.tmp_tagtype = this.arryTmp[0].trim();
										this.tmp_tagid = this.arryTmp[1].trim();
										this.tmp_craneline = this.arryTmp[2].trim();
										this.tmp_cranecode = this.arryTmp[3].trim();
										this.tmp_posx = this.arryTmp[4].trim();
										this.tmp_posy = this.arryTmp[5].trim();
										this.tmp_posz = this.arryTmp[6].trim();
										this.tmp_sleepmode = this.arryTmp[7].trim();
		
										if( this.tmp_tagtype != null && "1".equals(this.tmp_tagtype) )
										{
											this.tmp_weight = this.arryTmp[8].trim();
											this.tmp_distance = this.arryTmp[9].trim();
											this.tmp_hoistheight = this.arryTmp[10].trim();
											
											if( !"-99".equals(this.tmp_hoistheight) )
											{
												if( "L".equals(tmp_craneline) )
												{
													this.tmp_subhoistheight = String.valueOf( Float.parseFloat("13") - Float.parseFloat(this.tmp_hoistheight) );
												}
												else
												{
													this.tmp_subhoistheight = String.valueOf( Float.parseFloat("25") - Float.parseFloat(this.tmp_hoistheight) );
												}
											}
											else
											{
												this.tmp_subhoistheight = "";
											}
											
											this.tmp_spare1 = this.arryTmp[11].trim();
											this.tmp_spare2 = this.arryTmp[12].trim();
											
											
											if( "-99".equals(this.tmp_weight) ) this.tmp_weight = "";
											if( "-99".equals(this.tmp_distance) ) this.tmp_distance = "";
											if( "-99".equals(this.tmp_hoistheight) ) this.tmp_hoistheight = "";
											if( "-99".equals(this.tmp_spare1) ) this.tmp_spare1 = "";
											if( "-99".equals(this.tmp_spare2) ) this.tmp_spare2 = "";
											
											if( this.tmp_spare2 != null )
											{
												if( "1".equals(this.tmp_spare2) )		this.tmp_spare2 = "N";
												else if( "2".equals(this.tmp_spare2) )	this.tmp_spare2 = "F";
												else if( "3".equals(this.tmp_spare2) )	this.tmp_spare2 = "E";
												else									this.tmp_spare2 = "N";
											}
										}
										else
										{
											this.tmp_weight = "";
											this.tmp_distance = "";
											this.tmp_hoistheight = "";
											this.tmp_subhoistheight = "";
											this.tmp_spare1 = "";
											this.tmp_spare2 = "";									
										}
										
										if( "-".equals(this.tmp_craneline) ) this.tmp_craneline = "";
										if( "-".equals(this.tmp_cranecode) ) this.tmp_cranecode = "";
										
										this.tmp_ymd = this.arryTmp[13].trim();
										this.tmp_hms = this.arryTmp[14].trim();

										if( this.sbRet.indexOf(this.tmp_tagid) == -1 )
										{
											//IFM 전송 데이터 생성 카운트
											this.getDataTotCount++;
											
											this.sbRet.append(","); this.sbRet.append(this.tmp_tagid);           //18.설비RFID_TagID
											this.sbRet.append(","); this.sbRet.append(this.tmp_tagtype);         //19.BindingFlag (Crane : 1 , Ladle : 2 , 사람   : 3, 기타 : 9)
											this.sbRet.append(","); this.sbRet.append(this.tmp_cranecode);       //20.Crane코드
											this.sbRet.append(","); this.sbRet.append(this.tmp_posx);            //21.X좌표
											this.sbRet.append(","); this.sbRet.append(this.tmp_distance);        //22.Y좌표 ( 크레인 경우 횡행 정보 )
											this.sbRet.append(","); this.sbRet.append(this.tmp_hoistheight);     //23.Z좌표1 ( 주 후크 )
											this.sbRet.append(","); this.sbRet.append(this.tmp_subhoistheight);  //24.Z좌표2 ( 보조   후크, 크레인의 경우 해당 )
											this.sbRet.append(","); this.sbRet.append(this.tmp_spare2);        	//25.영Ladle(F), 공Ladle(E), 빈(N)
											this.sbRet.append(",");                                    			//26.수강Ladle번호
											this.sbRet.append(",");                                    			//27.설비RFID_TagID_1 (크레인에 실린 경우 (UWB)래들 Tag ID)
											this.sbRet.append(",");                                    			//28.BindingFlag_1 (Crane : 1 , Ladle : 2 , 사람   : 3, 기타 : 9)
											this.sbRet.append(","); this.sbRet.append( this.tmp_weight );       //29.수강Ladle중량 
											this.sbRet.append(",");                                    			//30.재료번호
										}

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

								if( this.getDataTotCount < 50 )
								{
									int forCnt = 50 - this.getDataTotCount;

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
								
								
								this.out = this.socket.getOutputStream();

								byte[] sendByte = this.concat( this.convertIntToBytes( (this.sbHeader.toString().length() + 4) ), (this.sbHeader.toString()).getBytes() );
								this.out.write( sendByte, 0, sendByte.length );
								this.out.flush();
								

								/*
								this.socket = new Socket(this.Poscoict_Svr_Ip, this.Poscoict_Svr_Port);
								this.out = this.socket.getOutputStream();

								byte[] sendByte = this.concat( this.convertIntToBytes( (this.sbHeader.toString().length() + 4) ), (this.sbHeader.toString()).getBytes() );
								this.out.write( sendByte, 0, sendByte.length );
								this.out.flush();

								//this.pr.close();
								this.out.close();
								this.socket.close();
								//Socket Server Data Send End
								*/
								
								this.printLine( String.valueOf( this.sbHeader.toString().length() + 4 ) + " [Send OK] >>>> " + this.sbHeader.toString() );

								this.sbRet = new StringBuffer();
								this.dataCounter = 0;				
								this.getDataTotCount = 0;
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
						this.printLine("Couldn't found new line after line # " + dataCounter);
					}
				}
			}
		} 
		catch (Exception e) 
		{
			stopRunning();
		}
		finally
		{
			try
			{
				if( this.out != null) this.out.close();
				if( this.socket != null) this.socket.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
		if (debug)
			this.printLine("Exit the program...");
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

    public static void main(String args[])
	{
		ExecutorService crunchifyExecutor = Executors.newFixedThreadPool(4);
		// Replace username with your real value

		IFMUwbRtlsIClient_bk01 ifm = new IFMUwbRtlsIClient_bk01(50);
		
		// Start running log file tailer on crunchify.log file
		crunchifyExecutor.execute(ifm);		
	}
}
