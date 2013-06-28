import requests
import ast


def getLinks():
	urls = []
	r = requests.get("http://localhost:8983/solr/collection2/select?q=*%3A*&fl=Link&wt=python&indent=true")
	for link in ast.literal_eval(r.content)["response"]["docs"]:
		urls.append(link["Link"])
	return urls