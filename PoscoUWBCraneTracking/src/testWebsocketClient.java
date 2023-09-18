import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tsingoal.com.FrameParse;
import com.tsingoal.com.RtlsWsManager.PosOutMode;

public class testWebsocketClient {

	public static void main(String[] args) {
		createLocalsensePushWs();
	}
	
	/**
	 * ws sub pro: localSensePush-protocol example
	 *Used for receiving commonly used real-time data, such as location data, base station status,
	 *label power, alarm data, area in and out events, heart rate, label movement rate, mileage and
	 * other information
	 */
	public static void createLocalsensePushWs() {
		/**
		 * Construction Mode I (recommended)
		 * The constructor takes no arguments, and the username and password arguments
		 * are set by the function
		 */
		//Create ws and set the username and password part start
		UWBLsWsPushPro rtls_pos = new UWBLsWsPushPro();
		//Setting a User Name
		rtls_pos.setWsUserName("admin");
		//Set the password text (the first parameter), the salt value (the second parameter). The salt value is usually unchanged, is a fixed value
		rtls_pos.setWsUserPasswd("Aa123456", "abcdefghijklmnopqrstuvwxyz20191107salt");
		//Create ws and set the username and password part end

		/**
		 * Structural mode 2
		 * The first parameter: username
		 * Second argument: use the salt-encrypted result password obtained by md5(MD5 (password) + salt)
		 * Third parameter: the password marked as final salted encryption (third parameter)
		 */
		//Set the WebSocket service address
		//rtls_pos.setHost("192.168.0.112");
		rtls_pos.setHost("192.168.1.12");
		//Set the WebSocket service port
		rtls_pos.setServerPort(48300);
		//Set the label number, AOA to 64 bits
		rtls_pos.setTagidBit(FrameParse.TAGID_32BIT);
		
		/** 
		   * 위치 데이터 맞춤 출력
			* XY, 표준 로컬 평면 좌표만 출력(기본값, 각 맵 도트 참조)
			* GEO, 위도 및 경도 좌표만 출력
			* GLOBAL은 전역 좌표만 출력합니다(전역 좌표 원점을 참조로 사용)
			* XY_GEO는 표준 로컬 평면 좌표와 위도 및 경도 좌표를 동시에 출력합니다.
			* XY_GLOBAL은 표준 로컬 평면 좌표와 글로벌 좌표를 모두 출력합니다.

			*Custom output for location data
			*XY, output only standard local plane coordinates (default, based on each map dot)
			*GEO, outputs only latitude and longitude coordinates
			*GLOBAL outputs only GLOBAL coordinates (with the origin of the GLOBAL coordinates as reference)
			*XY_GEO outputs both standard local plane coordinates and latitude and longitude coordinates
			*XY_GLOBAL outputs both standard local plane coordinates and global coordinates
		 */
		rtls_pos.setPos_mode(PosOutMode.XY);
		//Set the WebSocket sub-protocol
		rtls_pos.setProtocal("localSensePush-protocol");
		//Connect the websocket
		rtls_pos.connectToServer();
		
		//The SDK version number is displayed
		//System.out.println("java sdk version:"+rtls_pos.GetVerMajor()+"."+rtls_pos.GetVerMinor());
	}
}
