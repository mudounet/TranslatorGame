package com.mudounet.xml.stats;

import java.util.Date;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class TestStat implements Comparable<TestStat> {

    @Attribute
    private String key = "";
    @Attribute
    private int failed = 0;
    @Attribute
    private int total = 0;
    @Attribute
    private Date lastUpdate = new Date(0);
    @Attribute
    private Date lastFailed = new Date(0);
    @Element
    private MeanList lastResults = new MeanList();

    public TestStat(String key) {
        setKey(key);
    }
    
    public TestStat() {
    }

    public int getFailed() {
        return failed;
    }

    public Date getLastFailed() {
        return lastFailed;
    }

    public MeanList getLastResults() {
        return lastResults;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public int getTotal() {
        return total;
    }

    public int compareTo(TestStat o) {
        // TODO Auto-generated method stub
        if (this.lastResults.mean() < o.lastResults.mean()) {
            return -1;
        }
        if (this.lastResults.mean() > o.lastResults.mean()) {
            return 1;
        }
        if (this.total < o.total) {
            return -1;
        }
        if (this.total > o.total) {
            return 1;
        }
        return 0;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public void addResult(float result) {
        this.lastResults.add(result);
        this.lastUpdate = new Date();
        this.total++;
        if(result < 100.0) {
            this.failed++;
            this.lastFailed = new Date();
        }
    }

    public float mean() {
        return this.lastResults.mean();
    }

    @Override
    public String toString() {
        return "TestStat{" + "key=" + key + ", mean=" + lastResults.mean() + "}@"+this.hashCode();
    }
    
    
}
