package com.github.amap.track;

import static org.junit.Assert.*;

import org.junit.Test;

public class ServiceTest {


	@Test
	public void testUpdate() {
		AMapTrackClient client = new AMapTrackClient(AMapTrackClientTest.key);
		Service service = client.createService("testservice", "thisistestservice");
		System.out.println(service);
		service.update("testservice2", "thisistestservice");
		System.out.println(service);
		service.delete();
	}

	@Test
	public void testCreateTerminal() {
		AMapTrackClient client = new AMapTrackClient(AMapTrackClientTest.key);
		Service service = client.createService("testservice1", "thisistestservice");
		Terminal createTerminal = null;
		try {
			createTerminal = service.createTerminal("imei415534799", "aaaa", null);
			createTerminal.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(createTerminal);
		service.delete();
	}

	@Test
	public void testListTerminalInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testListTerminalString() {
		fail("Not yet implemented");
	}

	@Test
	public void testListTerminalIntegerStringInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPropsForTerminal() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelPropsForTerminal() {
		fail("Not yet implemented");
	}

}
