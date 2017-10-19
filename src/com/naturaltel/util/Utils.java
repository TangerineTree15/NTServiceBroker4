package com.naturaltel.util;

import java.util.Scanner;

import org.apache.log4j.Logger;

import com.naturaltel.cache.Config;
import com.naturaltel.config.entity.SipInfo;
import com.naturaltel.config.entity.UserAgent;

public class Utils {
	
	private Logger logger = Logger.getLogger("debug");
	
	private static Scanner scanner = new Scanner(System.in);;

	public void inputTestCase() throws Exception {
    	//get and set test case number from console
		try {
			boolean isValidTestCase = false;
			while(!isValidTestCase) {
				System.out.println("input your test case num(1-7) : ");
				int testCase = scanner.nextInt();
				if(testCase < 1 || testCase > 7) {
					System.out.println("invalid test case number !");
				} else {
					Config.setTestCase(testCase);
					isValidTestCase = true;
					System.out.println("test case : " + testCase + " is set");
				}
				
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
}
