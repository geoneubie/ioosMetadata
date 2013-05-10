package services;
import static org.junit.Assert.*;
import controller.MetadataController
import org.junit.Test;
import org.apache.log4j.*;
import util.TdsCrawler
public class MetadataExtractorTest {
	def logger
	//@Test
	public void streamSourceXSLTest() {
		def me = new MetadataExtractor()
		def urlStr = "http://www.ngdc.noaa.gov/metadata/published/xsl/SensorObservationService/SOS2ISO_MI.xsl"
		def ss = me.getStreamSource(urlStr)
		assertTrue(ss!=null)
	}
	//@Test
	public void streamSourceXMLTest() {
		def me = new MetadataExtractor()
		def urlStr = "http://opendap.co-ops.nos.noaa.gov/ioos-dif-sos/SOS?service=SOS&request=GetCapabilities"
		def ss = me.getStreamSource(urlStr)
		assertTrue(ss!=null)
	}

	//@Test
	public void transformCoopsTest() {
		try {
			def location = "D:/temp/waf/coopsTest.xml"
			def me = new MetadataExtractor()
			def xslUrlStr = "http://www.ngdc.noaa.gov/metadata/published/xsl/SensorObservationService/SOS2ISO_MI.xsl"
			def xmlUrlStr = "http://opendap.co-ops.nos.noaa.gov/ioos-dif-sos/SOS?service=SOS&request=GetCapabilities"
			def xslSource = me.getStreamSource(xslUrlStr)
			me.transform(xslSource,xmlUrlStr, location)
		} catch (e) {
			e.printStackTrace()
			assertTrue(false);
		}
	}

	//@Test
	public void transformGCOOS10Test() {
		try {
			def location = "D:/temp/waf/coopsTest.xml"
			def me = new MetadataExtractor()
			def xslUrlStr = "http://www.ngdc.noaa.gov/metadata/published/xsl/SensorObservationService/SOS2ISO_MI.xsl"
			//def xmlUrlStr = "http://www.wavcis.lsu.edu/gcoos/oostethys_server.asp?request=GetCapabilities&service=SOS"
			def xmlUrlStr = "http://michigan.glin.net/glos/sos/sos.php?request=GetCapabilities&service=SOS"
			def xslSource = me.getStreamSource(xslUrlStr)
			me.transform(xslSource,xmlUrlStr, location)
		} catch (e) {
			e.printStackTrace()
			assertTrue(false);
		}
	}

	//@Test
	public void transformAxiomTest() {
		try {
			def location = "D:/temp/waf/sosTest.xml"
			def me = new MetadataExtractor()
			def xslUrlStr = "http://www.ngdc.noaa.gov/metadata/published/xsl/SensorObservationService/SOS2ISO_MI.xsl"
			def xmlUrlStr = "http://sos.axiomalaska.com/sos?request=GetCapabilities&service=SOS&acceptVersions=1.0.0"
			def xslSource = me.getStreamSource(xslUrlStr)
			me.transform(xslSource,xmlUrlStr, location)
		} catch (e) {
			e.printStackTrace()
			assertTrue(false);
		}
	}

	@Test
	public void updateWAFIntegrationTest() {
		def mc = new MetadataController()
		mc.process("updateWAF"," http://www.ngdc.noaa.gov/metadata/published/NOAA/IOOS/iso_series/xml/ioosSeriesCandidate.xml","D:/temp/waf/ioos/")
	}


}
