package services

import java.util.Map
import org.apache.log4j.Logger

class ReportWriter {
	def logger

	def boolean publish(Map results) {
		logger = Logger.getLogger(ReportWriter.class)
		logger.info "publish report"
		try {

			def report = new File("ioos/ioosReport.html")
			logger.debug(report.absolutePath)
			report.withWriter { out ->

				//Add header
				def reportHdrIs = getClass().getResourceAsStream("/ioosCatalogSvcReport_hdr.html")
				reportHdrIs.eachLine { line ->
					out.writeLine(line)
				}

				// Add date updated
				def dateMod = new Date().format( 'yyyy-MM-dd HH:mm:ss' )
				out.writeLine('<table width="900" border="0" cellpadding="0" cellspacing="0">')
				out.writeLine('<tr>')
				out.writeLine('	<td>')
				out.writeLine("		Last Modified: ${dateMod}" )
				out.writeLine('	</td>')
				out.writeLine('</tr>')
				out.writeLine('</table>')
				int i = 0
				// Add results
				out.writeLine('<table cellpadding="0" cellspacing="0" border="0" class="display" id="example">')
				out.writeLine('<thead>')
				out.writeLine('	<tr>')
				out.writeLine('		<th>URL</th>')
				out.writeLine('		<th>Status</th>')
				out.writeLine('	</tr>')
				out.writeLine('</thead>')
				out.writeLine('<tbody>')

				logger.info("Entering loop... " + results.size())
				results.each {  key, value ->
					logger.info("${key},${value}")
					if (i%2) {
						if ("${value}".toString().equals("Online")) {
							out.writeLine('<tr class="even gradeA">')
							logger.debug('<tr class="even gradeA">')
						} else {
							out.writeLine('<tr class="even gradeX">')
							logger.debug('<tr class="even gradeX">')
						}
					} else {
						if ("${value}".toString().equals("Online")) {
							out.writeLine('<tr class="odd gradeA">')
							logger.debug('<tr class="odd gradeA">')
						} else {
							out.writeLine('<tr class="odd gradeX">')
							logger.debug('<tr class="odd gradeX">')
						}					}
					out.writeLine("<td>${key}</td>")
					out.writeLine("<td class='center'><a href='${key}' target='_blank'>${value}</a></td>")
					out.writeLine('</tr>')
					i++
				};



				//Add footer
				def reportFtrIs = getClass().getResourceAsStream("/ioosCatalogSvcReport_ftr.html")
				reportFtrIs.eachLine { line ->
					out.writeLine(line)
				}
			}
			return true
		} catch (Exception e) {
			logger.error("publish failure: ", e)
			return false
		}
	}
}
