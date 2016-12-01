/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mudounet.xml.core;

import com.mudounet.core.ResourceManager;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mudounet
 */
public class SectionListTest {
    
    public SectionListTest() {
    }
    
    SectionList instance;
    
    @Before
    public void setUp() throws Exception {
        instance = new SectionList();
        instance.load(ResourceManager.LoadFile("/lessons.xml"));
    }
    
    /**
     * Test of getList method, of class SectionList.
     */
    @Test
    public void testGetList() {
        System.out.println("TEST : getList");

        assertTrue(instance.getList().size() > 0);
    }

    /**
     * Test of setList method, of class SectionList.
     */
    @Test
    public void testSetList() {
        System.out.println("TEST : setList");
        
        assertNotNull(instance.getList());
        assertTrue(instance.getList().size() > 0);
        
        List<Section> list = null;
        instance.setList(list);
        assertNull(instance.getList());
        
        list = new ArrayList<Section>();
        instance.setList(list);
        assertEquals(0, instance.getList().size());
        
    }

    /**
     * Test of load method, of class SectionList.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("TEST : load");
        SectionList instance = new SectionList();
        instance.load(ResourceManager.LoadFile("/lessons.xml"));
        assertNotNull(instance.getList());
        assertTrue(instance.getList().size() > 0);
    }
}
