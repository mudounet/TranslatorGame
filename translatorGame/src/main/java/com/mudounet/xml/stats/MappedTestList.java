/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mudounet.xml.stats;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author isabelle
 */
public class MappedTestList extends TestList {

    private static final Logger Logger = LoggerFactory.getLogger(MappedTestList.class);
    private Map<String, TestStat> linkedList = new HashMap<String, TestStat>();

    public Map<String, TestStat> getLinkedList() {
        this.buildLinkedList();
        return linkedList;
    }

    public TestStat getItemByKey(String key, boolean autocreate) throws Exception {
        this.buildLinkedList();
        if (!linkedList.containsKey(key) && autocreate) {
            Logger.debug("Key \"" + key + " will be created automatically.");
            this.createTestStat(key);
        }

        return linkedList.get(key);
    }

    public TestStat getItemByKey(String key) throws Exception {
        return this.getItemByKey(key, false);
    }

    public void createTestStat(String key) throws Exception {
        if (linkedList.containsKey(key)) {
            Logger.error("Key \"" + key + " is already inserted.");
            throw new Exception("Key \"" + key + " is already inserted.");
        }

        TestStat t = new TestStat(key);
        this.getList().add(t);

        linkedList.put(key, t);
    }

    private void buildLinkedList() {
        if (this.list == null) {
            this.linkedList = null;
            return;
        }
        if (this.linkedList != null && this.linkedList.size() == this.list.size()) {
            return;
        }

        this.linkedList = new HashMap<String, TestStat>();

        for (TestStat testStat : this.list) {
            this.linkedList.put(testStat.getKey(), testStat);
        }

    }

    @Override
    public void setList(List<TestStat> list) {
        this.linkedList = null;
        super.setList(list);
        buildLinkedList();
    }

    @Override
    public void load(InputStream stream) throws Exception {
        super.load(stream);
        this.setList(this.getList());
    }
}
