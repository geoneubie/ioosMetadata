package services;

import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.log4j.Logger;

public class ReportWriterTest {
	def logger
	@Test
	public void publishTest() {
		logger = Logger.getLogger(ReportWriterTest.class)
		def filePath = this.getClass().getResource("/ioosReport.html").toURI()
		println filePath
		def reportFile = new File(filePath);
		def rw = new ReportWriter(reportFile)	
		def linkResults = ["http://oos.soest.hawaii.edu/thredds/iso/hioos/model/wav/swan/bigi/SWAN_Big_Island_Regional_Wave_Model_best.ncd":"Online","http://oos.soest.hawaii.edu/thredds/iso/hioos/model/wav/swan/bigi/SWAN_Big_Island_Regional_Wave_Model_best2.ncd":"Offline"]
		logger.debug "try to publish"
		def result = rw.publish(linkResults)
		assertTrue(result)
	}

}
