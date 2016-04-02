/*
 * mongo script to calculate review weight from user score and review attributes
 */
mongo = new Mongo("localhost")
YelpDB = mongo.getDB("YelpDB")
review = YelpDB.getCollection("review")
user_scores = YelpDB.getCollection("user_scores")

review.find().forEach(function(r) {
	u = user_scores.findOne({'user_id': r.user_id});
	record = {
		review_id: r.review_id,
		user_id: r.user_id,
		review_score: ((0.2 * r.votes.useful) + (0.8 * ((r.stars > 4 || r.stars < 2) ? 1 : 0))) * (u.user_score)
	}		
	print(JSON.stringify(record));
});	