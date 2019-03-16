package com.github.amap.track;

import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;

public class AMapTrackClientTest {
	public static String key = "faa9506acbe20b4397ef7230959a7475";

	@Test
	public void testCreateService() {
		AMapTrackClient client = new AMapTrackClient(key);
		Service service = client.createService("testservice", "thisistestservice");
		System.out.println("servie已经新建：" + service);
	}

	@Test
	public void testGetService() {
		AMapTrackClient client = new AMapTrackClient(key);
		Service service = client.getService(null, "testservice");
		System.out.println("servie已经获取：" + service);
	}
	
	@Test
	public void testDeleteService() {
		AMapTrackClient client = new AMapTrackClient(key);
		Service service = client.getService(null, "testservice");
		service.delete();
		System.out.println("servie已经删除：" + service);
	}

	@Test
	public void testListService() {
		AMapTrackClient client = new AMapTrackClient(key);
		List<Service> listService = client.listService();
		for (Service service2 : listService) {
			System.out.println("servie：" + service2);
		}
	}
	
	public static void main(String[] args) throws Exception{
		AMapTrackClient client = new AMapTrackClient(key);
		Service service = client.getService(null, "testservice");
		Terminal terminal = service.listTerminal("wangyao");
		System.out.println(terminal);
		Trace trace = terminal.createDefaultTrace();
		System.out.println(trace);
		
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		/*trace.uploadPoint(new Point(114.81287273562437, 38.07960082833725,  format2.parse("2019-02-14 20:19:50").getTime()));
		trace.uploadPoint(new Point(114.79585450331517, 38.08354028790167,  format2.parse("2019-02-14 20:35:04").getTime()));
		trace.uploadPoint(new Point(114.81235877024601, 38.026622851352776, format2.parse("2019-02-14 20:50:14").getTime()));
		trace.uploadPoint(new Point(114.82123160138842, 38.00700509894373,  format2.parse("2019-02-14 21:05:23").getTime()));
		trace.uploadPoint(new Point(114.81182582715873, 37.973364452373204, format2.parse("2019-02-14 21:21:06").getTime()));
		trace.uploadPoint(new Point(114.79856042890127, 37.97460471857192,  format2.parse("2019-02-14 21:37:29").getTime()));*/
		
		List<Point> points = trace.trsearch(null, null, true, 10000, 1, 990).getPoints();
		String path =  "var path =  [";
		String path2 = "var path2 = [";
		for (Point point : points) {
			path += "["+point.getXY()+"],";
		}
		path = path.substring(0, path.length()-1)+"]";
		points = trace.trsearch(null, null, true, 50, 1, 990).getPoints();
		for (Point point : points) {
			path2 += "["+point.getXY()+"],";
		}
		path2 = path2.substring(0, path2.length()-1)+"]";

		System.out.println(path);
		System.out.println(path2);
	}

}




