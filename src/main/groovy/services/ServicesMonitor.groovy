package services

import org.apache.log4j.Logger

class ServicesMonitor {
	def logger

	def collectionTypeIsNested(String urlStr) {
		logger = Logger.getLogger(ServicesMonitor.class)
		try {
			def url = new URL(urlStr)
			def xml = url.text

			def dsSeries = new XmlSlurper().parseText(xml)
			dsSeries.declareNamespace(gmd: 'http://www.isotc211.org/2005/gmd/gmd.xsd')

			return dsSeries.'composedOf'.'DS_DataSet'[0].@id.text().equals("TDSNestedCollection")
		} catch (Exception e) {
			logger.error("getMonList failure: " + urlStr, e)
			return null
		}
	}

	def getSeriesUrlList(String urlStr) {
		logger = Logger.getLogger(ServicesMonitor.class)
		try {
			def url = new URL(urlStr)
			def xml = url.text

			def dsSeries = new XmlSlurper().parseText(xml)
			dsSeries.declareNamespace(gmd: 'http://www.isotc211.org/2005/gmd/gmd.xsd')

			def links = dsSeries.'composedOf'.'DS_DataSet'.'has'

			return links
		} catch (Exception e) {
			logger.error("getMonList failure: " + urlStr, e)
			return null
		}
	}

	def String tdsOnline(String url) {
		logger = Logger.getLogger(ServicesMonitor.class)

		try {
			def nc = ucar.nc2.dataset.NetcdfDataset.openDataset(url)

			if (nc!=null) {
				return "Online"
			} else {
				return "Offline"
			}
		} catch (e) {
			logger.error("tdsOnline failure: " + url, e)
			return "Offline"
		}
	}

	def String sosOnline(String urlStr) {
		logger = Logger.getLogger(ServicesMonitor.class)
		try {
			logger.info("sosOnline: " + urlStr)
			def url = new URL(urlStr)
			def URLConnection conn = url.openConnection()
			conn.setConnectTimeout(10000)
			conn.setReadTimeout(30000)

			def xml = conn.inputStream
			def sosResp = new XmlSlurper().parse(xml)
			sosResp.declareNamespace(ows: 'http://schemas.opengis.net/ows/2.0/owsAll.xsd')

			def title = sosResp.'ows:ServiceIdentification'.'Title'.toString()
			if (title.equals('')) {
				//XMLSlurper returns empty string if element does not exist
				logger.debug 'it is true!'
				return "Online"
			} else {
				logger.debug 'it is false!'
				logger.error("sosOnline failure: " + urlStr, e)
				return "Offline"
			}
			sosResp.each{ println it.name() }
			return "Online"
		} catch (Exception e) {
			logger.error("sosOnline failure: " + urlStr, e)
			return "Offline"
		}
	}
}
