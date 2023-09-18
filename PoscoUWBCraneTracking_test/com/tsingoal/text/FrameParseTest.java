package com.tsingoal.text;

import com.tsingoal.com.FrameParse;
import com.tsingoal.com.TAttendanceStatics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FrameParseTest {
	private static FrameParse frameParse = new FrameParse();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParseAttendanceStatice_32bit() {
		byte[] data = { 0, 0, 117, 52, 0, 6, -79, -68, -77, -37, 52, 83, 0, 0, 1, 103, 94, 76, -109, 122, 0, 9, -65,
				-68, -57, -38, -57, -8, -45, -14, 50, 0, 1, 0, 2, 109, 49, 0, 0, 0, 1, 103, -127, -17, -30, -104, 69,
				-53, -86, -69 };
		frameParse.ParseAttendanceStatice(data, data.length, FrameParse.ID_XBIT.ID_32BIT, 32);
		TAttendanceStatics expected = new TAttendanceStatics();
		expected.setRelatedTagId(Long.valueOf(30004L));
		expected.setRelatedTagName("奔驰4S");
		expected.setAreaId(1543475336058L);
		expected.setAreaName("考勤区域2");
		expected.setMapId(1);
		expected.setMapName("m1");
		expected.setStat(0);
		expected.setTimestamp(1544073241240L);
		Assert.assertEquals(expected, frameParse.Result());
	}
}
