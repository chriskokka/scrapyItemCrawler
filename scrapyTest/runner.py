from subprocess import Popen
import time


def runSpider():
	while True:
		p = Popen(["C:\Users\Kasutaja\Desktop\scrapyTest","scrapy", "crawl", "spider1"])
		stdout, stderr = p.communicate()
		time.sleep(15)
	
	
runSpider()