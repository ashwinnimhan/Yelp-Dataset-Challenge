import StringIO
import csv
import re

#Identify substring from groundtruth file, to match 'shrimp' from 'shrimps' or 'hash' from 'hash-browns'
def find_str(s, char):
    index = 0
    if char in s:
        c = char[0]
        for ch in s:
            if ch == c:
                if s[index:index+len(char)] == char:
                    return index
            index += 1
    return -1
    
#Open csv file which has a list of features re-ranked by model
arr1= []
with open('input-features-by-city-MODEL.csv', 'r') as f1:
    arr1 = f1.read().splitlines() 

rel_score = []
count=0

#Open csv file which has a list of features identified as groundtruth from restaurants with very high or very low ratings
#Read each feature as a new array element
arr2 = []
with open('input-features-by-city-GROUNDTRUTH.txt', 'r') as f2:
    arr2 = f2.read().splitlines() 

#len of arr2 indicates no. of features in groundtruth     
total = len(arr2)
totalby2 = int(total/2)
totalby3_4 = int(total*0.75)

#Assign ranks based on position of the feature from model in the list of groundtruth file
for indx1,elem in enumerate(arr1):
#If feature is found directly in the file
	if elem in arr2:
		indx2 = arr2.index(elem)
		if indx2 <= totalby2:
			rel_score.append((elem,3))
		elif indx2 <= totalby3_4:
			rel_score.append((elem,2))
		elif indx2 < total:
			rel_score.append((elem,1))
		else:
			rel_score.append((elem,0))
#If feature is not found directly in the file, try to identify a partial match
	else:
		for elem2 in arr2:
			indx3 = find_str(elem2,elem)
			if indx3 == 0:
				indx4 = arr2.index(elem2)
				if indx4 <= totalby2:
					rel_score.append((elem,3))
				elif indx4 <= totalby3_4:
					rel_score.append((elem,2))
				elif indx4 < total:
					rel_score.append((elem,1))
				else:
					rel_score.append((elem,0))

#print a comma separated list of values which will output feature,relevance score
for item in rel_score:
	print item[0],',',item[1]

'''
OUTPUT: For Las Vegas relevance scores for top 50 features identified by the model
donut,1
bagel,2
crepe,3
pizza,3
omlette,3
yogurt,2
vegan,1
burger,3
gelato,1
sushi,3
wings,3
sandwich,3
pancake,3
coffee,3
burrito,3
oyster,3
buffet,3
waffle,3
chocolate,3
cake,3
breakfast,3
tea,3
cookies,3
gluten,3
pastrami,1
sub,1
croissant,3
crawfish,3
pastry,3
shrimp,3
lobster,1
atmosphere,3
Cole-slaw,3
omelet,3
latte,3
velvet,2
gem,3
milkshake,1
sushi,3
meatball,1
cheesecake,3
noodle,1
lounge,3
candy,3
steak,3
martini,3
rib,3
beer,3
cocktail,3
panini,3
'''
