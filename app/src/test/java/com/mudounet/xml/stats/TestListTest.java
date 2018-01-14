/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mudounet.xml.stats;

import com.mudounet.core.ResourceManager;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mudounet
 */
public class TestListTest {
    
    ArrayList<String> listRefs;
    TestList instance;

    public TestListTest() {
    }

    @Before
    public void setUp() throws Exception {
        
        instance = new TestList();
        instance.load(ResourceManager.LoadFile("/lessons-test.stats"));

        listRefs = new ArrayList();
        listRefs.add("1.1");
        listRefs.add("0.1");
        listRefs.add("2.3");
        listRefs.add("2.2");
        listRefs.add("1.0");
        listRefs.add("2.0");
        listRefs.add("2.1");
        listRefs.add("0.0");
    }

    /**
     * Test of mean method, of class TestList.
     */
    @Test
    public void testMean() {
        System.out.println("TEST : mean");
        instance = new TestList();
        float expResult = 0.0F;
        assertEquals(expResult, instance.mean(), 0.0);

        TestStat t = new TestStat();
        t.addResult(100f);
        instance.getList().add(t);
        assertEquals(100, instance.mean(), 0.001f);

        t.addResult(0.0f);
        assertEquals(50, instance.mean(), 0.001f);

        t.addResult(0.0f);
        assertEquals(100 / 3f, instance.mean(), 1f);

        t = new TestStat();
        t.addResult(100f);
        instance.getList().add(t);
        assertEquals(((100 / 3) + 100) / 2, instance.mean(), 1f);

        t.addResult(0);
        assertEquals(((100 / 3) + 50) / 2, instance.mean(), 1f);

        t.addResult(0);
        assertEquals(((100 / 3) + (100 / 3)) / 2, instance.mean(), 1f);
    }

    /**
     * Test of getSelectedStat method, of class TestList.
     */
    @Test
    public void testGetSelectedStat() throws ParseException {
        System.out.println("getSelectedStat");

        TestStat result;
        
        result = instance.getSelectedStat();
        assertEquals("1.1", result.getKey());
        
        Date refDate = new Date();
        result = instance.getSelectedStat(new Date());
        assertEquals("1.1", result.getKey());
        
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s z");
        
        result = instance.getSelectedStat(dateFormatter.parse("2012-01-01 23:00:00.0 MSK"));
        assertEquals("2.0", result.getKey());
        
        result = instance.getSelectedStat(dateFormatter.parse("2005-01-01 23:00:00.0 MSK"));
        assertEquals("0.0", result.getKey());
        
        result = instance.getSelectedStat(dateFormatter.parse("1999-01-01 23:00:00.0 MSK"));
        assertNull(result);
    }

    /**
     * Test of getSelectedStat method, of class TestList.
     */
    @Test
    public void testOrderingStats() throws Exception {
        System.out.println("TEST : getSelectedStat");

        assertNotNull(instance.list);
        assertEquals(listRefs.size(), instance.list.size());

        for (int currentOrder = 0; currentOrder < listRefs.size(); currentOrder++) {
            assertNotEquals(listRefs.get(currentOrder), instance.list.get(currentOrder).getKey());
        }

        instance.getSelectedStat();

        for (int currentOrder = 0; currentOrder < listRefs.size(); currentOrder++) {
            assertEquals(listRefs.get(currentOrder), instance.list.get(currentOrder).getKey());
        }
    }
}
