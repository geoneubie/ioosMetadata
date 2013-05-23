package services

import static org.junit.Assert.*

import org.apache.log4j.Logger
import org.junit.Test

import services.SeriesWriter
import services.ServicesMonitor
import services.RegistryReader

class RegistryIntegrationTest {
	def logger

	@Test
	public void integrationTest() {
		logger = Logger.getLogger(RegistryIntegrationTest.class)

		def tableId = "2111243" //Service Registry tableid for fusiontable
		def svcType = ""
		def svcStatus = "APPROVED"
		def filePath = this.getClass().getResource("/ioosSeries.xml").toURI()
		println filePath
		def seriesFile = new File(filePath);
		if (seriesFile.exists()) seriesFile.delete()

		def seriesWriter = new SeriesWriter(seriesFile)
		seriesWriter.writeHeader()

		svcType = "SOS"

		def svcRegReader = new RegistryReader()
		def results

		results = svcRegReader.getServiceEndPoints(tableId, svcType, svcStatus)

		println "sos results size=" + results.size()

		seriesWriter.publish(results, "SOSCollection")
		println "wrote SOS to ISO Series"

		svcType = "THREDDS"

		results = svcRegReader.getServiceEndPoints(tableId, svcType, svcStatus)
		seriesWriter.publish(results, "TDSFlatCollection")


		println "tds results size=" + results.size()
		seriesWriter.writeFooter()

		// Check content		
		assertTrue(seriesFile.length() > 0)
	}
}
