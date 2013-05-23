package services

import static org.junit.Assert.*

import org.apache.log4j.Logger
import org.junit.Test

import services.ServicesMonitor
import services.RegistryReader

class RegistyReaderTest {
	def logger
	def tableId = "2111243" //Service Registry tableid for fusiontable


	@Test
	public void sosReadTest() {
		logger = Logger.getLogger(RegistyReaderTest.class)


		def svcType = "SOS"
		def svcStatus = "APPROVED"

		def svcRegReader = new RegistryReader()
		def results = svcRegReader.getServiceEndPoints(tableId, svcType, svcStatus)

		results.each { key, value ->
			println("      <gmd:has xlink:href=\"${key}\" xlink:title=\"${value}\" />\n")
		}
		println "resultsSize=" + results.size()

		assertTrue(results.size() > 0)
	}

	@Test
	public void tdsReadTest() {
		logger = Logger.getLogger(RegistyReaderTest.class)


		def svcType = "THREDDS"
		def svcStatus = "APPROVED"

		def svcRegReader = new RegistryReader()
		def results = svcRegReader.getServiceEndPoints(tableId, svcType, svcStatus)
		results.each { key, value ->
			println("      <gmd:has xlink:href=\"${key}\" xlink:title=\"${value}\" />\n")
		}
		println "resultsSize=" + results.size()

		assertTrue(results.size() > 0)
	}

}
