package controller

import static org.junit.Assert.*

import org.apache.log4j.Logger
import org.junit.Test

import services.MailSender
import services.ReportWriter
import services.SeriesWriter
import services.ServicesMonitor
import services.RegistryReader
import util.TdsCrawler;

class MonitorTest {
	def logger

	//This is a long test!!!
	//@Test 
	public void integrationTest() {
		try {
			def seriesUrl = "http://www.ngdc.noaa.gov/metadata/published/NOAA/IOOS/iso_series/xml/ioosSeries.xml"

			println "servicesMonitor integrationTest using " + seriesUrl
			def mon = new ServicesMonitor()

			println "DS_Series record url: " + seriesUrl
			def links = mon.getSeriesUrlList(seriesUrl)
			def tds = new TdsCrawler()


			def opdEndPoints = [:]
			def allOdpEndPoints = [:]

			// Result variables
			def linkResults = [:]
			def reportWriter = new ReportWriter()

			def result
			if (links!=null) {

				links.each {
					def endPoint = URLDecoder.decode("${it.@'xlink:href'.text()}","UTF-8")
					println "endPoint=" + endPoint
					if (!endPoint.contains("service=SOS")) {
						tds.crawl(endPoint, mon.collectionTypeIsNested(seriesUrl))
						opdEndPoints = tds.getOpenDAPUrls()
						opdEndPoints.each { ds, tdsUrl->
							allOdpEndPoints.put(ds, tdsUrl)
						}
					} else {
						result = mon.sosOnline(endPoint)
						linkResults.put(endPoint, result)
					}
				}
				allOdpEndPoints.each { ds, url->
					result = mon.tdsOnline(url)
					//Add html for OpenDAP page
					linkResults.put(url + ".html", result)
					reportWriter.publish(linkResults)
				}
			} else {
				println "No links found in DS_Series record."
			}

			println "links.size=" + links.size();

			def ms = new MailSender()
			ms.send(linkResults, test)

		} catch (Exception e) {
			println "Error: " + e.getMessage() + " check usage."
		}
	}
}

