package services;

import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.log4j.*;

class ReportIntegrationTest {
	def logger

	//@Test
	void integrationTest() {

		def mon = new ServicesMonitor()
		def url = "http://www.ngdc.noaa.gov/metadata/published/NOAA/IOOS/iso_series/xml/ioosSeries.xml"
		def devUrl = "http://localhost:8080/examples/IOOS.xml"
		def links = mon.getSeriesUrlList(url)
		def linkResults = [:]
		def reportWriter = new ReportWriter()
		if (links!=null) {

			links.each {
				def String monUrl = "${it.@'xlink:href'.text()}"
				def result
				if (monUrl.contains("service=SOS")) {
					logger.debug "SOS Service: " + monUrl
					result = mon.sosOnline(monUrl)
				} else {
					logger.debug "TDS Service: " + monUrl
					result = mon.tdsOnline(monUrl)
				}
				linkResults.put(monUrl, result)
			}

			reportWriter.publish(linkResults)
			assertTrue(links.size() > 0)
		} else {
			assertTrue(false)
		}
	}
}
