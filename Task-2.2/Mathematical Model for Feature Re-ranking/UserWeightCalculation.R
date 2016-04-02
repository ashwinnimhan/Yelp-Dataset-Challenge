library(RMongo)
library(ggplot2)
library(rjson)

#' This function calculates user weight for city name provided. It fetches data from mongodb
#' and retrives the values required for calculation.
#'
#' @param city  City Name for which user weights should be calculated
#'
#'
#' @export CSV file containing the User Weights
#'
User_Wt <- function (city)
{
    mg1 <- mongoDbConnect(city)

    data1 <- dbGetQuery(mg1, 'UserWeight', "", 0, 9999999)
    
    data1$votes_R_Ratio <- (data1$votes_useful/data1$review_count)

    data1$user_weight <- (0.25 * data1$elite_years/ max(data1$elite_years)+
      0.55 * data1$votes_R_Ratio/ min(data1$votes_R_Ratio)+
      0.20 * data1$fans/ max(data1$fans) + 0.000001)
    
    fileName <- paste(city, "csv", sep = ".")
    write.csv(format(data1[,c("user_id","user_weight")], scientific = FALSE), file = paste("D:/Data_User_WT/", fileName, sep = ""), row.names = FALSE)

}

lstCities <- list("Charlotte","Edinburgh", "Karlsruhe", "Madison", "Montreal","Pheonix", "Pittsburgh","Urbana","Waterloo", "Las_Vegas")

for (i in lstCities){

  User_Wt(i)
  
}

