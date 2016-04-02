from pyspark import SparkContext, SparkConf
from pyspark.mllib.clustering import KMeans, KMeansModel
from math import sqrt

def parse_line(ln):
	split_ln = ln.split(',')
	ln_coord = [float(split_ln[1]), float(split_ln[2])]
	new_line = ln + ',' + cluster_labels[ KMeans_model.predict(ln_coord)]
	return new_line

# load and parse the data
# conf = SparkConf()
sc = SparkContext()

# load previously generated k-means model
KMeans_model = KMeansModel.load(sc, "kmeans_model")

# define cluster label array
cluster_labels = ["Pheonix-AZ", "Edinburgh-UK", "Charlotte-NC", "Madison-WI", "Montreal-Canada", "Waterloo-Canada", "Las Vegas-NV", "Urbana-Champaign-IL", "Pittsburgh-PA", "Karlsruhe-Germany"]

# read the file which has business_ids, latitude, longitude
data = sc.textFile("./business_gps.csv")

# get labelled rows
parsedData = data.map(parse_line)

# save labelled businesses in the output folder
parsedData.saveAsTextFile("./output")