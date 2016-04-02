import pymongo
from pymongo import MongoClient
import simplejson
import urllib3
import urllib.request
from operator import itemgetter

client = MongoClient()
db = client.yelp
#db_backup = client.YelpDB
r_scores = db.reviewScores1
#r_backup = db.review

#cname: Name of the city- all information related to user,review is segregated based on this.
def operation(cName, word):
	query = "http://localhost:8983/solr/" + cName + "/select?q=" + word + "&start=0&rows=1000&fl=review_id,score,business_id,stars,includes&wt=json";
	with urllib.request.urlopen(query) as url:
		solr_response = simplejson.load(url)
		word_score = 0
		stars = 1;
		numOfDocs = solr_response['response']['numFound']
		#print('word:',word,' total docs: ',numOfDocs)
		maxLength = len(solr_response['response']['docs'])
		if numOfDocs == 0 or maxLength==0:
				#print('inside doclen')
				return (word,0,0)
		for doc in solr_response['response']['docs']:
				stars += doc['stars'][0]
				review = r_scores.find_one({'review_id': doc['review_id'][0]});
				#print(doc['review_id'][0], doc['score'],review['review_score'])
				if review is not None:
					#print(review['review_id'],review['review_weight'],doc['score']);
					word_score += (review['review_weight'] * doc['score'])
				else:
					pass;
				return (word, word_score)

#words=["waitress","restaurant","burger"]
words = []
with open('input-features-by-city.txt', 'r') as f2:
    words = f2.read().splitlines() 

val = map(lambda x: operation('Pheonix', x),words)

#for item in val:
#	print(item)

#Prints a sorted output based on the word score obtained from the model.
print(sorted(val,key=itemgetter(1),reverse=True))
