/*
 * filter the db to get reviews for restaurants only
 */

mongo = new Mongo("localhost");
YelpDB = mongo.getDB("YelpDB");
business = YelpDB.getCollection("business");
review = YelpDB.getCollection("review");

food_categories = ["American (Traditional)","Bars","Restaurants","Food","Lounges","American (New)","Breakfast & Brunch","Burgers","Cafes","Irish","Chinese","Italian","Coffee & Tea","Fast Food","Breweries","Food Delivery Services","Salad","Sandwiches","Soup","Pizza","Gluten-Free","Seafood","Bakeries","Desserts","Sports Bars","Ice Cream & Frozen Yogurt","Beer"," Wine & Spirits","Barbeque","Specialty Food","Mediterranean","Wine Bars","Vegetarian","Delis","Bed & Breakfast","Dive Bars","Diners","Bistros","Chicken Wings","Hot Dogs","Sushi Bars","Tex-Mex","Bagels","Donuts","Comfort Food","Tapas Bars","Ethnic Food","Cocktail Bars","Steakhouses","Candy Stores","Chocolatiers & Shops","Cheese Shops","Tea Rooms","Do-It-Yourself Food","Buffets","Cajun/Creole","Resorts","Soul Food","Juice Bars & Smoothies","Caribbean","Pakistani","Malaysian","Hookah Bars","Spanish","Fruits & Veggies","Beer Bar","Cheesesteaks","Fish & Chips","Cupcakes","Vegan","Gastropubs","Food Banks","Russian","Pretzels","Shaved Ice","Arabian","Gelato","Halal","Dim Sum","Mongolian","Filipino","Argentine","Cantonese","Food Trucks","Cambodian","Belgian","Hungarian","Szechuan","Wineries","Delicatessen","Pasta Shops","Basque","Himalayan/Nepalese","Moroccan","Falafel","African","Indonesian","Turkish","Afghan","Food Stands","Modern European","Irish Pub","Brazilian","Laotian","Coffeeshops","Hot Pot","Burmese","Live/Raw Food","Bubble Tea","Bartenders","Singaporean","Champagne Bars","Colombian","Cafeteria","Poutineries","Scandinavian","Canadian (New)","Austrian","Food Court","Dominican","Scottish","Patisserie/Cake Shop","Pub Food","Ramen","Bangladeshi","Australian","Ukrainian","International","Kebab","Serbo Croatian","Oriental","Shanghainese","Venezuelan","Bavarian","Iberian","Trinidadian","Curry Sausage","Egyptian","Eastern European","Eastern German","Wok","Swiss Food","Uzbek","Pita","Teppanyaki","Izakaya"];

b1 = business.distinct('business_id', {categories:{$in:food_categories}});
filtered_reviews = review.find({'business_id': {$in: b1}}, {'_id': 0});
print("{'filtered_reviews': [");
filtered_reviews.forEach(function(msg) { printjson(msg); print(",")});
print("]}");
