package xml

import org.apache.log4j.Logger

class IsoDsSeriesSlurper {
	def logger
	
	def getSeriesUrlList(String urlStr) {
		logger = Logger.getLogger(ServicesMonitor.class)
		try {
			def url = new URL(urlStr)
			def xml = url.text
			println xml
			def dsSeries = new XmlSlurper().parseText(xml)
			dsSeries.declareNamespace(gmd: 'http://www.isotc211.org/2005/gmd/gmd.xsd')

			def links = dsSeries.'composedOf'.'DS_DataSet'.'has'

			return links
		} catch (Exception e) {
			logger.error("getSeriesUrlList failure: " + urlStr, e)
			return null
		}
	}
}
