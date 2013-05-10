package services

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLEncoder
import java.util.Arrays
import java.util.List

/**
 * Using groovy to query Fusion Tables
 *
 * @author david.neufeld@noaa.gov
 */
public class RegistryReader {
	/**
	 * A little helper method to get rid of the quotes in the input
	 * and cast values so they can be compared properly.
	 */
	private double castToDouble(string) {
		return string.replaceAll('"', '').toDouble()
	}

	public Map getServiceEndPoints(tableId, svcType, svcStatus) {

		def urlStr = "https://www.google.com/fusiontables/api/query?sql=select%20*%20from%20" + tableId + "%20where%20svc_status%20%3D%20'" + svcStatus + "'%20AND%20svc_type%3D'" + svcType + "'"
		def url = new URL(urlStr)

		def csv = url.text
		//load and split the file

		String[] lines = csv.split('\n')
		List<String[]> rows = lines.collect {it.split(',')}


		//OK, it's parsed - let's ask some questions
		int SVC_ENDPOINT_TITLE = 0;
		int SVC_ENDPOINT = 5;

		def endPoint
		def endPointTitle
		// Result variables
		def endPoints = [:]

		int i = 0
		rows.each { row ->

			endPoint = URLEncoder.encode(row[SVC_ENDPOINT],"UTF-8")
			endPointTitle = row[SVC_ENDPOINT_TITLE]
			//Skip the first row because it contains the field headers.
			if (i >= 1)	 {
				endPoints.put(endPoint, endPointTitle)
			}
			i++
		}
		return endPoints

	}

}