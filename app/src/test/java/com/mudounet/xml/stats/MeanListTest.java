package com.mudounet.xml.stats;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MeanListTest {
	
	MeanList listTested;

	@Before
	public void setUp() throws Exception {
		listTested = new MeanList();
	}

	@Test
	public void testMean() {
		assertEquals(0.0f, listTested.mean(), 0.0f);
		
		listTested.add(100.0f);
		for(int i = 1; i < 7; i++) {
			assertEquals(100.0f/i, listTested.mean(), 0.001f);
			listTested.add(0.0f);
		}
		
		assertEquals(0.0f, listTested.mean(), 0.1f);
		
		for(int i = 7; i < 0; i--) {
			assertEquals(100.0f/i, listTested.mean(), 0.001f);
			listTested.add(100.0f);
		}
		assertEquals(0.0f, listTested.mean(), 0.1f);
		
		
	}

}
