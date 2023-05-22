package com.gapi.utilities;

/** ReadConfig class is used to read properties from config file
 * @author rkasi
 * @version 17
 * @since 2022 
 * */

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ReadConfig {

	Properties pro;

	// load config file data
	
	public ReadConfig() {
		File src = new File(".\\configurations\\config.properties");
		try {
			FileInputStream fis = new FileInputStream(src);
			pro = new Properties();
			pro.load(fis);
		} catch (Exception e) {
			System.out.println("Exception :" + e.getLocalizedMessage());
		}
	}

	// read specific property  from properties file
	
	public String getConfigValue(String propertyname) {
		
		return pro.getProperty(propertyname);
	}

}
