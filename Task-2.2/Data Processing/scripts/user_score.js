/*
 * mongo script to calculate user score based on user attributes.
 */

mongo = new Mongo("localhost")
YelpDB = mongo.getDB("YelpDB")
user = YelpDB.getCollection("user")

elite_max = user.find({})

user.find().forEach(function(u) {
	var record = {
		user_id: u.user_id, 
		user_score: ((0.25 * u.elite.length) + (0.50 * u.votes.useful) + (0.25 * u.fans))
	}		
	print(JSON.stringify(record));
});
