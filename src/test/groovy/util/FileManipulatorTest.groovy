package util

import static org.junit.Assert.*

import org.apache.log4j.Logger
import org.junit.Test

import services.ServicesMonitor

class FileManipulatorTest {
	def logger

	@Test
	public void updateGMLNamespaceTest() {
		logger = Logger.getLogger(FileManipulatorTest.class)

		def fileLocation = "D:/temp/waf/ioos/NERACOOS_SOS_10.xml"
		FileManipulator.updateGMLNamespace(fileLocation)
		def updatedText = new File(fileLocation).text
		assertTrue updatedText.contains("xmlns:gml=\"http://www.opengis.net/gml/3.2\"")
	}
}
