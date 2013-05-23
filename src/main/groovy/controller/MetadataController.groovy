package controller

import org.apache.log4j.Logger

import services.MetadataExtractor
import services.RegistryReader
import services.SeriesWriter
import services.ServicesMonitor
import services.ReportWriter
import services.MailSender
import util.TdsCrawler
import util.FileManipulator

class MetadataController {
	def static logger

	public static void updateRegistry(tableId) {
		try {
			println "Launching IOOS application for IOOS registry retrieval."
			println "Google table id: " + tableId

			def svcType = ""
			def svcStatus = "APPROVED"
			def ioosDir = new File("ioos")
			if (!ioosDir.exists()) ioosDir.mkdirs()
			
			def seriesFile = new File("ioos/ioosSeries.xml");
			seriesFile.delete()

			def seriesWriter = new SeriesWriter(seriesFile)
			seriesWriter.writeHeader()

			svcType = "SOS"

			def svcRegReader = new RegistryReader()
			def results

			results = svcRegReader.getServiceEndPoints(tableId, svcType, svcStatus)

			//println "sos results size=" + results.size()

			seriesWriter.publish(results, "SOSCollection")
			//println "wrote SOS to ISO Series"

			svcType = "THREDDS"

			results = svcRegReader.getServiceEndPoints(tableId, svcType, svcStatus)
			seriesWriter.publish(results, "TDSFlatCollection")


			//println "tds results size=" + results.size()
			seriesWriter.writeFooter()
		} catch(Exception e) {
			println "Error: " + e.getMessage()
		}
	}

	public static void checkSystemStatus(seriesUrl, test) {

		try {
			println "Launching IOOS application include a url to the DS_Series record."
			println "DS_Series record url: " + seriesUrl

			def mon = new ServicesMonitor()

			def links = mon.getSeriesUrlList(seriesUrl)
			def tds = new TdsCrawler()


			def opdEndPoints = [:]
			def allOdpEndPoints = [:]

			// Result variables
			def linkResults = [:]
			def seriesFile = new File("ioos/ioosReport.html")
			def reportWriter = new ReportWriter()

			def result
			if (links!=null) {

				links.each {
					def endPoint = URLDecoder.decode("${it.@'xlink:href'.text()}","UTF-8")
					//println "endPoint=" + endPoint
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
			println "Error in checkStatus: " + e.getMessage()
		}
	}

	public static void updateWAF(seriesUrl, baseLocation) {
		logger = Logger.getLogger(MetadataController.class)
		try {
			println "Launching IOOS application include a url to the DS_Series record."
			println "DS_Series record url: " + seriesUrl

			def mon = new ServicesMonitor()
			def me = new MetadataExtractor()
			def sosXslUrlStr = "http://www.ngdc.noaa.gov/metadata/published/xsl/SensorObservationService/SOS2ISO_MI.xsl"
			def wmsXslUrlStr = "http://www.ngdc.noaa.gov/metadata/published/xsl/wms1.3_to_isoSV.xsl"
			
			int i = 1

			def links = mon.getSeriesUrlList(seriesUrl)
			def tds = new TdsCrawler()


			def opdEndPoints = [:]
			def allOdpEndPoints = [:]

			// Result variables
			def linkResults = [:]

			def result
			if (links!=null) {

				links.each {
					def sosXslSource = me.getStreamSource(sosXslUrlStr)
					def wmsXslSource = me.getStreamSource(wmsXslUrlStr)
					def title = URLDecoder.decode("${it.@'xlink:title'.text()}","UTF-8")
					def xmlEndPoint = URLDecoder.decode("${it.@'xlink:href'.text()}","UTF-8")
					//println "endPoint=" + endPoint
//					if (!xmlEndPoint.contains("service=SOS")) {
//						//Add later
//						/*tds.crawl(endPoint, mon.collectionTypeIsNested(seriesUrl))
//						 opdEndPoints = tds.getOpenDAPUrls()
//						 opdEndPoints.each { ds, tdsUrl->
//						 allOdpEndPoints.put(ds, tdsUrl) */
//					} 
					if (xmlEndPoint.toUpperCase().contains("SERVICE=SOS")) {
						def fileLocation = baseLocation + "/" + title + "_SOS_" + i + ".xml"
						println "print sosXslSource: " + sosXslSource
						println ("print xmlEndPoint: " + xmlEndPoint)
						println ("print location: " + fileLocation)
						try {
							//me.download(endPoint, location)
							me.transform(sosXslSource, xmlEndPoint, fileLocation)
							sosXslSource = null
							FileManipulator.updateGMLNamespace(fileLocation)
						} catch (e) {
							logger.error("Error in transform for xmlEndPoint: " + xmlEndPoint + "," + e.getMessage())
							logger.error("Using xslSource: " + sosXslSource)
						}

						i++
					}
					if (xmlEndPoint.toUpperCase().contains("SERVICE=WMS")) {
						def fileLocation = baseLocation + "/" + title + "_WMS_" + i + ".xml"
						println "print wmsXslSource: " + wmsXslSource
						println ("print xmlEndPoint: " + xmlEndPoint)
						println ("print location: " + fileLocation)
						try {
							//me.download(endPoint, location)
							me.transform(wmsXslSource, xmlEndPoint, fileLocation)
							wmsXslSource = null
							FileManipulator.updateGMLNamespace(fileLocation)
						} catch (e) {
							logger.error("Error in transform for xmlEndPoint: " + xmlEndPoint + "," + e.getMessage())
							logger.error("Using xslSource: " + wmsXslSource)
						}

						i++
					}

				}
				/* allOdpEndPoints.each { ds, url->
				 result = mon.tdsOnline(url)
				 //Add html for OpenDAP page
				 linkResults.put(url + ".html", result)
				 } */
			} else {
				println "No links found in DS_Series record."
			}



		} catch (Exception e) {
			println "Error in updateWAF: " + e.getMessage()
		}
	}

	public static process(flag, param, param2) {
		try {
			if (flag.equals("monitor")) {
				println "Checking system status..."
				checkSystemStatus(param, param2)
			}
			if (flag.equals("updateRegistry")) {
				updateRegistry(param)
			}
			if (flag.equals("updateWAF")) {
				updateWAF(param, param2)
			}
		} catch (Exception e) {
			println "Error in main: " + flag + "," + param + e.getMessage()
		}
	}

	static main(args){
		def String flag = ""
		def String param = ""
		def String param2 = "false"
		try {

			if (args.length == 3) {
				flag = "${args[0]}"
				param = "${args[1]}"
				param2 = "${args[2]}"
				println flag + "," + param + "," + param2
			} else {
				println "Error: Check usage, not enough command line arguments."
				println "Usage: <action> <url or id> <test>"
				println "Available actions: monitor, updateRegistry, updateWAF"
				println "Example: updateRegistry 2111243 true"
				println "Example: updateWAF http://www.ngdc.noaa.gov/metadata/published/NOAA/IOOS/iso_series/xml/ioosSeries.xml /nfs/eds_pipeline/data/waf/NOAA/IOOS/iso/xml/"
				println "Example: monitor http://www.ngdc.noaa.gov/metadata/published/NOAA/IOOS/iso_series/xml/ioosSeries.xml false"
				System.exit(-1)
			}

			process(flag, param, param2)

		} catch (Exception e) {
			println "Error in main: " + flag + "," + param + e.getMessage()
		}

	}
}
