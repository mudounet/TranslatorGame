/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mudounet.xml.core;

import com.mudounet.core.ResourceManager;
import java.io.InputStream;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mudounet
 */
public class MappedSectionListTest {
    
    public MappedSectionListTest() {
    }
    
    MappedSectionList instance;
    
    @Before
    public void setUp() throws Exception {
        instance = new MappedSectionList();
        instance.load(ResourceManager.LoadFile("/lessons.xml"));
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getLinkedList method, of class MappedSectionList.
     */
    @Test
    public void testGetLinkedList() {
        System.out.println("TEST : getLinkedList");

        Map result = instance.getLinkedList();
        assertNotNull(result);
        
        assertEquals(92, result.keySet().size());
    }

    /**
     * Test of getItemByKey method, of class MappedSectionList.
     */
    @Test
    public void testGetItemByKey() throws Exception {
        System.out.println("TEST : getItemByKey");
        String key = "0.1";
        com.mudounet.xml.core.Test expResult = instance.getList().get(0).getList().get(1);

        com.mudounet.xml.core.Test result = instance.getItemByKey(key);
        assertEquals(expResult, result);
        
        key = "4.2";
        expResult = instance.getList().get(4).getList().get(2);
        result = instance.getItemByKey(key);
        assertEquals(expResult, result);
        
        key = "15.5";
        expResult = instance.getList().get(15).getList().get(5);
        result = instance.getItemByKey(key);
        assertEquals(expResult, result);
        
        key = "999.999";
        result = instance.getItemByKey(key);
        assertNull(result);
    }

    /**
     * Test of load method, of class MappedSectionList.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("TEST : load");
        instance.load(ResourceManager.LoadFile("/lessons.xml"));
        assertNotNull(instance.getList());
        assertTrue(instance.getList().size() > 0);
    }
}
