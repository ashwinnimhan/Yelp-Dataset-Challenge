library(RMongo)
library(ggplot2)
library(rjson)
library(plyr) 

#' This function calculates the review weight for specified city.
#'
#' @param city city for which review weights to be calculated
#'
#' @export CSV file containing review id and review weights
#'
review_WT <- function(city)
  {
    filename <- paste(city, "csv", sep = ".")
    filepath <- paste("D:/Data_User_WT/", filename, sep = "")
    
    mg1 <- mongoDbConnect(city)
    data1 <- dbGetQuery(mg1, 'ReviewWeight', "", 0, 999999999)
    data2 <- read.csv(filepath)
    
    data1$stars[data1$stars == 2] <- 1
    data1$stars[data1$stars == 3] <- 0
    data1$stars[data1$stars == 4] <- 1
    data1$stars[data1$stars == 5] <- 1
    
    data3 <- merge(data1, data2, by="user_id")
    
    data3$review_weight <- (0.25 * (data3$votes_useful+0.000001)/ max(data3$votes_useful)+
                            0.25 * data3$stars/ max(data3$stars) +
                            0.50 * data3$user_weight/ max(data3$user_weight) + 0.000001)
    
    
    data3 <- rename(data3,c('review_id'='_id'))
    write.csv(format(data3[,c("_id","review_weight")], scientific = FALSE), file = paste("D:/Data/Data_R_WT_point5/", filename, sep = ""), row.names = FALSE)
    return 
  }

lstCities <- list("Charlotte","Edinburgh", "Karlsruhe", "Madison", "Montreal","Pheonix", "Pittsburgh","Urbana","Waterloo", "Las_Vegas")


for (i in lstCities){
  review_WT(i)
  
}
