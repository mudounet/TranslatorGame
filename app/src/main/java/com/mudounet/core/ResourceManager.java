package com.mudounet.core;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceManager {
	// Call the LoadText method and pass it the resourceId
	//LoadText(R.raw.textfile);
	
	private static final Logger Logger = LoggerFactory
			.getLogger(ResourceManager.class);

	public static InputStream LoadFile(String filename) {
		// The InputStream opens the resourceId and sends it to the buffer
	    //InputStream is = this.getResources().openRawResource(resourceId);
		InputStream is = "".getClass().getResourceAsStream(filename);
		if(is == null) Logger.warn("No resources named \""+filename+"\" found");
		
		return is;
	}
}
