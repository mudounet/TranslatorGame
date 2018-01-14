package com.mudounet.xml.stats;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Root
public class TestList {

    private static final Logger Logger = LoggerFactory.getLogger(TestList.class);
    @ElementList(inline = true)
    protected List<TestStat> list = new ArrayList<TestStat>();

    public void setList(List<TestStat> list) {
        this.list = list;
    }

    /**
     * @return the list
     */
    public List<TestStat> getList() {
        return list;
    }

    public void save(OutputStream stream) throws Exception {
        Serializer serializer = new Persister();
        Collections.sort(list);
        serializer.write(this, stream);
    }

    public void load(InputStream stream) {
        if (stream == null) {
            this.list = new ArrayList<TestStat>();
        } else {
            Serializer serializer = new Persister();
            TestList t = null;
            try {
                t = serializer.read(TestList.class, stream);
                this.list = t.list;
            } catch (Exception e) {
                this.list = new ArrayList<TestStat>();
            }
        }
    }

    public float mean() {
        if (list.isEmpty()) {
            return 0;
        }

        float mean = 0;
        for (TestStat t : this.list) {
            mean += t.mean();
        }

        return mean / this.list.size();
    }

    public TestStat getSelectedStat() {
        return getSelectedStat(null);
    }

    public TestStat getSelectedStat(Date limitBeforeReuse) {
        Collections.sort(list);
        
        if(limitBeforeReuse == null) {
            return this.list.get(0);
        }
        
        TestStat selectedItem = null;
        int indexOfSelection;
        for(indexOfSelection = 0; indexOfSelection < this.list.size(); indexOfSelection++) {
            if(this.list.get(indexOfSelection).getLastUpdate().before(limitBeforeReuse)) {
                selectedItem = this.list.get(indexOfSelection);
                break;
            }
        }
        
        Logger.info("Skipped "+indexOfSelection+" tests ("+(indexOfSelection * 100 / this.list.size())+"%)");
        return selectedItem;
    }
}
