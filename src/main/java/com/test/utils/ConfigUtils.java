package com.test.utils;

import java.io.*;
import java.util.*;


public class ConfigUtils {

	 public static void main(String args[]) throws IOException {
	      Properties prop = readPropertyFile("config/config.properties");
	      System.out.println("username: "+ prop.getProperty("USERNAME"));
	      System.out.println("password: "+ prop.getProperty("PASSWORD"));
	   }
	   public static Properties readPropertyFile(String fileName) throws IOException {
	      FileInputStream fis = null;
	      Properties prop = null;
	      try {
	         fis = new FileInputStream(fileName);
	         prop = new Properties();
	         prop.load(fis);
	      } catch(FileNotFoundException fnfe) {
	         fnfe.printStackTrace();
	      } catch(IOException ioe) {
	         ioe.printStackTrace();
	      } finally {
	         fis.close();
	      }
	      return prop;
	   }
	
}
