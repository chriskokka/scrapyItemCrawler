from scrapy.spider import BaseSpider
from scrapy.selector import HtmlXPathSelector
from subprocess import Popen
import re
import requests
import ast
from getLinks import *


	


class spider1(BaseSpider):
	name="spider1"
	allowed_domains = ["http://www.markit.eu/"]		#this is the site that I crawler for info
	start_urls = getLinks()		# getLinks requests sites from solr and then returns them as a list	
		
	
	#To understand the XPath queries I suggest going to w3schools.com or just google for scrapy tutorials.
	def parse(self, response):
		hxs = HtmlXPathSelector(response)
		itemData = hxs.select('//table[@id="MSpec"]/tbody/tr/td/text()').extract()
		category = hxs.select('//ul[@class="productParameters"]/li/span/a/text()').extract()  #category[0] is the category, category itself is a list
		price = hxs.select('//tr/td/span[@class="price"]/text()').extract()
		
	
		solrFile = "PATH_TO_SOLR_FILE_DIR"	#the dir where you are going to upload the data to Solr from
		counter = 0
		temp = ""
		
		try:
			avatudFail = open(solrFile, "w")
		except IOError:
			print "Fail"
			
		#Something for estonians, not complete due to encoding difference.
		if "lauaarvutid" in category[0]:
			Category = "Koik lauaarvutid"
		elif "learvutid" in category[0]:
			Category = "Koik sulearvutid"
		elif "ik monitorid" in category[0]:
			Category = "Koik monitorid"
		else:
			Category = category[0]
			
		
		avatudFail.write('[\n')
		avatudFail.write('\t{\n')
		avatudFail.write('\t\t"Link": "' + response.url + '",\n')
		avatudFail.write('\t\t"Category": "' + Category + '",\n')
		avatudFail.write('\t\t"Manufacturer": "' + category[1] + '",\n')
		avatudFail.write('\t\t"Price": "' + str(price[0].encode('utf-8')) + '",\n')
		description = re.sub('["]', '\'\'',itemData[1])
		avatudFail.write('\t\t"Description": "' + description + '",\n')
		avatudFail.write('\t\t"MoreInfo": "')
		
			
		for e in itemData:
			counter += 1
			if(counter==1 ):
				continue
			if(counter==2 ):
				continue
			if(counter%2):
				temp = e.encode('utf-8')
			else:
				e = e.encode('utf-8')
				e = re.sub('["]', '\'\'', e)
				avatudFail.write(temp + ':	' + e + ' +')
			
			
		avatudFail.write('"\n')	
		avatudFail.write('\t}\n')
		avatudFail.write(']')
		avatudFail.close()
		
		
		# open the file to upload
		with open('PATH_TO_SOLR_FILE_DIR', 'rb') as fin:
			# execute the post request
			headers = {'Content-type': 'application/json'}
			r = requests.post("http://localhost:8983/solr/collection1/update/json", params=dict(commit="true"), headers=headers, data=fin.read())

		
	
		
		
