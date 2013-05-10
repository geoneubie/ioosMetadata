package util

import static org.junit.Assert.*

import org.apache.log4j.Logger
import org.junit.Test

import services.ServicesMonitor

class TdsCrawlerTest {
	def logger

	//@Test
	public void crawlTest() {
		logger = Logger.getLogger(TdsCrawlerTest.class)
		//def tdsEndPoint = "http://www.ngdc.noaa.gov/thredds/bathyCatalog.xml"
		//def tdsEndPoint = "http://svc.axiomalaska.com/thredds/catalog.xml"
		def tdsEndPoint = "http://oos.soest.hawaii.edu/thredds/idd/glide.xml"
		def tds = new TdsCrawler()
		logger.debug "tdsEndPoint=" + tdsEndPoint
		tds.crawl(tdsEndPoint, false)

		def openDapEndPts = tds.getOpenDAPUrls()
		println "odpSize=" + openDapEndPts.size()
		println "Loop thru map:"



		assertTrue(openDapEndPts.size() > 0)

	}

	//@Test
	public void integrationTest() {
		println "For example: http://www.ngdc.noaa.gov/metadata/published/NOAA/IOOS/Collection/iso_series/ioosSeries.xml"
		def mon = new ServicesMonitor()
		def seriesUrl = "http://www.ngdc.noaa.gov/metadata/published/NOAA/IOOS/iso_series/xml/ioosSeries.xml"
		println "DS_Series record url: " + seriesUrl
		def links = mon.getSeriesUrlList(seriesUrl)

		def tds = new TdsCrawler()

		def opdEndPoints = [:]
		def allOdpEndPoints = [:]
		def linkResults = [:]


		if (links!=null) {

			links.each {
				def xmlEndPoint = URLDecoder.decode("${it.@'xlink:href'.text()}","UTF-8")
				if (!xmlEndPoint.contains("service=SOS")) {
					tds.crawl(xmlEndPoint, mon.collectionTypeIsNested(seriesUrl))
					opdEndPoints = tds.getOpenDAPUrls()

					opdEndPoints.each { ds, url->
						allOdpEndPoints.put(ds, url)
					}
				}
			}

		}

		allOdpEndPoints.each { ds, url->
			def result
			if (url.contains("service=SOS")) {
				result = mon.sosOnline(url)
			} else {
				result = mon.tdsOnline(url)
			}
			linkResults.put(url, result)
		}


		linkResults.each { url, result->
			println url + ":" + result
		}
	}
}
