from pyspark import SparkContext

sc = SparkContext()

#read LasVegas-NV.txt
text_file = sc.textFile("./LasVegas-NV.txt")

#use map-reduce to determine word count  
counts = text_file.flatMap(lambda line: line.split(" ")) \
             .map(lambda word: (word, 1)) \
             .reduceByKey(lambda a, b: a + b)
			 
#save results to WC folder
counts.saveAsTextFile("./WC")