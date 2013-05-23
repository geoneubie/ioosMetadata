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
		
		URI uri = this.getClass().getResource("/NERACOOS_SOS_8.xml").toURI();
		println uri.toString()
		
		FileManipulator.updateGMLNamespace(uri)
		def updatedText = new File(uri).text
		assertTrue updatedText.contains("xmlns:gml=\"http://www.opengis.net/gml/3.2\"")
	}
}
