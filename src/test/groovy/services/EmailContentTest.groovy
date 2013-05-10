package services;

import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.log4j.Logger;

public class EmailContentTest {
	def logger

	//@Test
	public void contentTest() {
		logger = Logger.getLogger(EmailContentTest.class)
		def ms = new MailSender()
		def linkResults = ["http://oos.soest.hawaii.edu/thredds/iso/hioos/model/wav/swan/bigi/SWAN_Big_Island_Regional_Wave_Model_best.ncd":"Offline",
					"http://oos.soest.hawaii.edu/thredds/iso/hioos/model/wav/swan/bigi/SWAN_Big_Island_Regional_Wave_Model_best2.ncd":"Offline",
					"http://oos.soest.hawaii.edu/thredds/iso/hioos/model/wav/swan/bigi/SWAN_Big_Island_Regional_Wave_Model_best3.ncd":"Offline"]
		logger.debug "create email"
		def result = ms.generateEmailContent(linkResults)
		print result
		assertTrue(result!=null)
	}

}
