package services;
import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.log4j.*;

public class ServicesMonitorTest {
	def logger

	@Test
	public void getCollectionTypeTest() {

		logger = Logger.getLogger(ServicesMonitorTest.class)
		def mon = new ServicesMonitor()
		def url = "http://www.ngdc.noaa.gov/metadata/published/NOAA/IOOS/iso_series/xml/ioosSeries.xml"
		assertTrue(!mon.collectionTypeIsNested(url))
	}

	@Test
	public void getMonListTest() {
		logger = Logger.getLogger(ServicesMonitorTest.class)
		def mon = new ServicesMonitor()
		def url = "http://www.ngdc.noaa.gov/metadata/published/NOAA/IOOS/iso_series/xml/ioosSeries.xml"
		def links = mon.getSeriesUrlList(url)
		if (links!=null) {
			links.each {
				logger.debug(" ${it.@'xlink:href'.text()}")
				println " ${it.@'xlink:href'.text()}"
			}
			assertTrue(links.size() > 0)
		} else {
			assertTrue(false)
		}
	}

	//@Test
	public void monSOSOnlineRespTest() {
		def mon = new ServicesMonitor()
		assertTrue(mon.sosOnline("http://opendap.co-ops.nos.noaa.gov/ioos-dif-sos/SOS?service=SOS&request=GetCapabilities").equals("Online"))
	}

	//@Test
	public void monWHOIOnlineISOTest() {
		def mon = new ServicesMonitor()
		assertTrue(mon.tdsOnline("http://geoport.whoi.edu:8081/thredds/iso/bathy/vs_1sec_20070725.nc").equals("Online"))
	}

	//@Test
	public void monHIOOSOnlineISOTest() {
		def mon = new ServicesMonitor()
		assertTrue(mon.tdsOnline("http://oos.soest.hawaii.edu/thredds/iso/hioos/model/wav/swan/bigi/SWAN_Big_Island_Regional_Wave_Model_best.ncd").equals("Online"))
	}

	//@Test
	public void monHIOOSOnlineNCMLTest() {
		def mon = new ServicesMonitor()
		assertTrue(mon.tdsOnline("http://oos.soest.hawaii.edu/thredds/ncml/hioos/model/wav/swan/bigi/SWAN_Big_Island_Regional_Wave_Model_best.ncd").equals("Offline"))
	}

	//@Test
	public void monHIOOSOnline404Test() {
		def mon = new ServicesMonitor()
		assertTrue(mon.tdsOnline("http://oos.soest.hawaii.edu/thredds/blah/hioos/model/wav/swan/bigi/SWAN_Big_Island_Regional_Wave_Model_best.ncd").equals("Offline"))
	}
}
