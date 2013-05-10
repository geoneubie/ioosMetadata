package services

import java.util.Map
import org.apache.log4j.Logger

class SeriesWriter {
	def logger


	def writeHeader () {
		def seriesFile = new File("ioos/ioosSeries.xml")
		seriesFile.withWriter { out ->

			//Add header
			def reportHdrIs = getClass().getResourceAsStream("/ioosSeries_hdr.xml")
			reportHdrIs.eachLine { line ->
				out.writeLine(line)
			}
		}
	}

	def boolean publish(Map results, String collectionType) {
		logger = Logger.getLogger(SeriesWriter.class)
		logger.info "publish series"
		def seriesFile = new FileWriter("ioos/ioosSeries.xml",true)
		try {
			seriesFile.withWriter { out ->
				//Collection
				out.append("   <gmd:composedOf>\n")
				out.append("      <gmd:DS_DataSet id=\"" + collectionType + "\" >\n")

				logger.info("Entering loop... " + results.size())
				results.each {  key, value ->

					out.append("      <gmd:has xlink:href=\"${key}\" xlink:title=\"${value}\" />\n")

				}


				out.append("      </gmd:DS_DataSet>\n")
				out.append("   </gmd:composedOf>\n")


			}
			return true
		} catch (Exception e) {
			logger.error("publish failure: ", e)
			return false
		}
	}

	def writeFooter () {
		def seriesFile = new FileWriter("ioos/ioosSeries.xml",true)
		seriesFile.withWriter { out ->

			//Add header
			def reportHdrIs = getClass().getResourceAsStream("/ioosSeries_ftr.xml")
			reportHdrIs.eachLine { line ->
				out.append(line + "\n")
			}

		}


	}

}
