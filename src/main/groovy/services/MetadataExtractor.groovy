package services
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import org.apache.log4j.Logger
class MetadataExtractor {
	def logger

	def download(address, location) {
		def file = new FileOutputStream(location)
		def out = new BufferedOutputStream(file)
		out << new URL(address).openStream()
		out.close()
	}

	def StreamSource getStreamSource(String urlStr) {
		URL url = new URL(urlStr)
		return new StreamSource(url.openStream())
	}

	def transform(String xsl, String xml) {
		logger = Logger.getLogger(MetadataExtractor.class)
		try {
			def TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl()
			def xslSource = getStreamSource(xsl)
			def xmlSource = getStreamSource(xml)
			def transformer = factory.newTransformer(xslSource)
			transformer.transform(xmlSource, new StreamResult(System.out))
		} catch (e) {
			logger.error("Transform failure: " + url, e)
		}
	}

	def transform(StreamSource xslSource, String xml, String location) {
		logger = Logger.getLogger(MetadataExtractor.class)
		logger.info("log - xslSource: " + xslSource)
		logger.info("log - xml: " + xml)
		logger.info("log - location: " + location)
		try {
			def TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl()
			def is = (new URL(xml)).openConnection().getInputStream()
			StreamSource xmlSource = new StreamSource(is);
			def transformer = factory.newTransformer(xslSource)
			transformer.transform(xmlSource, new StreamResult(new File(location)))
			//is.close()
			xmlSource.close()
			xslSource.close()
		} catch (e) {
			logger.error("Transform failure: " + xml, e)
		}
	}
}
