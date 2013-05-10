package util

import java.util.ArrayList
import java.util.List

import javax.naming.Context

import org.apache.log4j.Logger

import thredds.catalog.InvAccess
import thredds.catalog.InvCatalogRef
import thredds.catalog.InvDataset
import thredds.catalog.InvService
import thredds.catalog.ServiceType
import thredds.catalog.crawl.CatalogCrawler

public class TdsCrawler {
	def logger

	private Map urlOpenDAPMap = new HashMap<String,String>()
	private boolean nestedCrawl = false
	private String topCatalogUrl = ""

	public void setNested(boolean nested) {
		nestedCrawl = nested
	}

	private void doHarvest(InvDataset ids) {

		java.util.List<InvAccess> access = ids.getAccess()
		if (access.size() > 0) {

			for (InvAccess a : access) {
				InvService s = a.getService()
				ServiceType stype = s.getServiceType()
				if (stype == ServiceType.OPENDAP) {
					String urlString = a.getStandardUrlName()
					String dsCatalogUrl = ids.getCatalogUrl()
					String datasetId = ids.getID()

					if (nestedCrawl) {
						urlOpenDAPMap.put(datasetId, urlString)
					} else {
						if (dsCatalogUrl.contains(topCatalogUrl)) urlOpenDAPMap.put(datasetId, urlString)
					}
				}
			}
		}
	}

	public void crawl(String catalogUrl, boolean nested) {
		logger = Logger.getLogger(TdsCrawler.class)
		topCatalogUrl = catalogUrl
		nestedCrawl = nested
		Context ctx = null
		final List<String> crawlLst = new ArrayList<String>()
		println "catalogUrl: " + catalogUrl
		CatalogCrawler.Listener listener = new CatalogCrawler.Listener() {

					public boolean getCatalogRef(InvCatalogRef catRef, Object obj) {

						if (catRef != null) {
							crawlLst.add(catRef.getURI().toASCIIString())
							return true
						}
						return false
					}

					public void getDataset(InvDataset ids, Object crawler) {
						doHarvest(ids)
					}
				}

		CatalogCrawler crawler = new CatalogCrawler(
				CatalogCrawler.USE_ALL, false, listener)

		crawler.crawl(
				catalogUrl,
				null, System.out, ctx)
	}

	public Map getOpenDAPUrls() {
		return urlOpenDAPMap
	}
}