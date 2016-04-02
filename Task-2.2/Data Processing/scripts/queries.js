use Pheonix 
db.Business.find({$and: [{'stars' : {$gt : 4}}, {'stars' : {$lte : 5}}], 'categories': {$in: ['Bakeries', 'Breakfast & Brunch', 'Mexican']}}).forEach(function(b) {
    good_reviews = db.Reviews.find({'business_id': b.business_id, $and: [{'stars' : {$gt : 4}}, {'stars' : {$lte : 5}}]}, {'_id': 0}).forEach(function(r) {
        printjsononeline(r)
	})
})
db.Business.find({$and: [{'stars' : {$gt : 0}}, {'stars' : {$lte : 2}}], 'categories': {$in: ['Bakeries', 'Breakfast & Brunch', 'Mexican']}}).forEach(function(b) {
    good_reviews = db.Reviews.find({'business_id': b.business_id, $and: [{'stars' : {$gt : 0}}, {'stars' : {$lte : 2}}]}, {'_id': 0}).forEach(function(r) {
        printjsononeline(r)
    })
})

use LasVegas
db.Business.find({$and: [{'stars' : {$gt : 4}}, {'stars' : {$lte : 5}}], 'categories': {$in: ['Seafood', 'Bars', 'Breakfast & Brunch']}}).forEach(function(b) {
    good_reviews = db.Reviews.find({'business_id': b.business_id, $and: [{'stars' : {$gt : 4}}, {'stars' : {$lte : 5}}]}, {'_id': 0}).forEach(function(r) {
        printjsononeline(r)
	})
})
db.Business.find({$and: [{'stars' : {$gt : 0}}, {'stars' : {$lte : 2}}], 'categories': {$in: ['Seafood', 'Bars', 'Breakfast & Brunch']}}).forEach(function(b) {
    good_reviews = db.Reviews.find({'business_id': b.business_id, $and: [{'stars' : {$gt : 0}}, {'stars' : {$lte : 2}}]}, {'_id': 0}).forEach(function(r) {
        printjsononeline(r)
    })
})
 
good_businesses = db.Business.find({$and: [{'stars' : {$gt : 4}}, {'stars' : {$lte : 5}}]}, {'business_id': 1, '_id': 0}).forEach(function(b) {
    good_reviews = db.Reviews.find({'business_id': b.business_id}).forEach(function(r) {
        print(r.text)
    })
})
bad_businesses = db.Business.find({$and: [{'stars' : {$gt : 0}}, {'stars' : {$lte : 2}}]}, {'business_id': 1, '_id': 0}).forEach(function(b) {
    bad_reviews = db.Reviews.find({'business_id': b.business_id}).forEach(function(r) {
        print(r.text)
    })
})

db.getCollection('ReviewWord_WT_Score').find({ $query: {}, $orderby: { score : -1 } }, {'word': 1, '_id':0}).forEach(function(r){
	print(r.word)
})