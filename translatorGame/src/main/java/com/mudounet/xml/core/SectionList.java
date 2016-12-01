package com.mudounet.xml.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Root
public class SectionList {

    @ElementList(inline = true)
    private List<Section> list;

    /**
     * @return the list
     */
    public List<Section> getList() {
        return list;
    }

    public void setList(List<Section> list) {
        this.list = list;
    }

    /**
     * @param stream stream to read from
     * @return
     * @throws Exception
     */
    public void load(InputStream stream) throws Exception {
        Serializer serializer = new Persister();
        SectionList t = serializer.read(SectionList.class, stream);
        this.list = t.list;
    }
}
