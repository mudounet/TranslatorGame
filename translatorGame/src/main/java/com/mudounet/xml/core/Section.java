package com.mudounet.xml.core;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Section {
	
	@Attribute
	private String title;

	@ElementList(inline=true)
	private List<Test> list;

	/**
	 * @return the list
	 */
	public List<Test> getList() {
		return list;
	}
	
}
