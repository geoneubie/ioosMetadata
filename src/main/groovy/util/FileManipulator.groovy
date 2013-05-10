package util

class FileManipulator {

	def static updateGMLNamespace(srcFileNm) {

		def file = new File(srcFileNm)
		def String srcFileStr = file.text

		def fixedStr = srcFileStr.replaceFirst(/xmlns:gml=\"http:\/\/www.opengis.net\/gml\"/, "xmlns:gml=\"http://www.opengis.net/gml/3.2\"")
		def writer = file.newWriter()

		writer << fixedStr
		writer.flush()
		writer.close()
	}
}
