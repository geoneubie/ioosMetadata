package services

import org.apache.log4j.Logger
import org.apache.commons.mail.HtmlEmail

class MailSender {

	def logger
	int online = 0

	public String generateEmailContent(Map linkResults) {
		logger = Logger.getLogger(MailSender.class)
		def writer = new StringWriter()
		def builder = new groovy.xml.MarkupBuilder(writer)

		linkResults.each {  key, value ->
			if ("${value}".toString().equals("Online")) online++
		}
		builder.html(){
			head(){ title("Monitoring Report"){ } }
			body(){
				p("IOOS daily monitoring report summary: ")
				p("There are a total of " + online + " services online, out of " + linkResults.size() + " currently monitored.")
				if (online!=linkResults.size()) {
					p("Services with a status of offline include:")
					p{
						ul{
							linkResults.each {  key, value ->
								if ("${value}".toString().equals("Offline")) {
									li("  ${key}")
								}
							}
						}
					}
				}
				p{
					mkp.yield "To view the full report click "
					a(href:'http://www.ngdc.noaa.gov/eds/ioos/index.html', "here")
					mkp.yield "."
				}
			}
		}
		logger.debug "HTML=" + writer.toString()
		return writer.toString()
	}

	public void send(Map linkResults, String test) {
		logger = Logger.getLogger(MailSender.class)
		try {
			def HtmlEmail email = new HtmlEmail();

			//IOOS.Catalog
			if (test.equals("false")) {
				logger.info("Emailing IOOS Catalog Team")
				email.addTo("IOOS.Catalog@noaa.gov", "IOOS Catalog")
			} else {
				logger.info("Emailing test")
				email.addTo("david.neufeld@noaa.gov", "IOOS Catalog")
			}
			email.setHostName("localhost")
			email.setFrom("edscron@lion.ngdc.noaa.gov", "IOOS Alert")

			def msg = generateEmailContent(linkResults)
			if (online==linkResults.size()) {
				email.setSubject("[IOOS Monitor] OK: All " + linkResults.size() + " services online")
			} else {
				email.setSubject("[IOOS Monitor] Alert: " + online + " out of " + linkResults.size() + " services online")
			}
			email.setHtmlMsg(msg)

			// set the alternative message
			email.setTextMsg("Your email client does not support HTML messages")

			// send the email
			email.send();



		}
		catch (Exception ex)
		{
			logger.error(ex);
		}
	}
}
