/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mudounet.xml.core;

import com.mudounet.xml.stats.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author isabelle
 */
public class MappedSectionList extends SectionList {

    private static final Logger Logger = LoggerFactory.getLogger(MappedTestList.class);
    private Map<String, Test> linkedList = new HashMap<String, Test>();

    public Map<String, Test> getLinkedList() {
        this.buildLinkedList();
        return linkedList;
    }

    public Test getItemByKey(String key) throws Exception {
        this.buildLinkedList();
        return linkedList.get(key);
    }

    private void buildLinkedList() {
        if (this.getList() == null) {
            this.linkedList = null;
            return;
        }
        if (this.linkedList != null && this.linkedList.size() == this.getList().size()) {
            return;
        }

        this.linkedList = new HashMap<String, Test>();

        for (int sectionIdx = 0; sectionIdx < this.getList().size(); sectionIdx++) {
            Section section = this.getList().get(sectionIdx);

            for (int testIdx = 0; testIdx < section.getList().size(); testIdx++) {
                Test test = section.getList().get(testIdx);
                this.linkedList.put(sectionIdx + "." + testIdx, test);
            }

        }

    }

    @Override
    public void load(InputStream stream) throws Exception {
        super.load(stream);
        this.setList(this.getList());
    }
}
