library("ggplot2")
library("reshape2")
library("rjson")

# Find all relevant files
cpu_files = list.files("../benchmarker_data", "cpu*", full.names = TRUE)

timestamps = list()
values = list()

for (file in cpu_files) {
  data <- unlist(fromJSON(file = file)$data$result[[1]]$values)
  time <- as.double(data[c(TRUE, FALSE)])
  value <- as.double(data[c(FALSE, TRUE)])
  # All time series should start at 0
  timestamps[[length(timestamps) + 1]] <- time - time[1]
  values[[length(values) + 1]] <- value
}

find_extremum <- function(list_of_data, extremum_function) {
  per_list = lapply(list_of_data, function(series) extremum_function(series))
  overall = extremum_function(unlist(per_list))
  return(overall)
}

# Find the limits of the plot
max_timestamp <- find_extremum(timestamps, max)
min_value <- find_extremum(values, min)
max_value <- find_extremum(values, max)
max_int_timestamp <- round(max_timestamp)

# Plot individual lines
df <- data.frame(row.names = 0:max_int_timestamp)
for (i in 1:length(timestamps)) {
  existing_data <- unlist(values[[i]])
  df <- cbind(df, c(existing_data, rep(0, max_int_timestamp + 1 - length(existing_data))))
  #lines(timestamps[[i]], values[[i]], col = rgb(0, 0, 0, alpha = 0.2))
}
colnames(df) <- sprintf("s%d", 1:length(timestamps))

# Plot the mean line
mean_line <- c()
std <- c()
median_line <- c()
for (i in 0:max_int_timestamp) {
  # Find the mean across all experiments
  column <- unlist(lapply(values, function(series) ifelse(i + 1 <= length(series), series[i + 1], 0)))
  mean_line <- c(mean_line, mean(column))
  median_line <- c(median_line, median(column))
  std <- c(std, sd(column))
  # Record it in the i'th position of a vector (or append to it)
}
stats <- data.frame(mean = mean_line, std = std, median = median_line)
#df$mean <- mean_line
#df$std <- std
#lines(0:max_int_timestamp, mean_line, lwd = 2, col = "blue")
#plot(1, type = "n", xlab = "time", ylab = "CPU Usage", xlim = c(0, max_timestamp), ylim = c(min_value, max_value))

melted <- melt(df)
ggplot(data = melted, aes(x = rep(0:max_int_timestamp, length(timestamps)), y = value)) + xlab("time") + ylab("CPU Usage") +
  geom_line(aes(group = melted$variable), colour = rgb(0, 0, 0, alpha = 0.2)) +
  geom_line(data = stats, aes(x = 0:max_int_timestamp, y = median), colour = "red", size = 1) +
  geom_line(data = stats, aes(x = 0:max_int_timestamp, y = mean), colour = "blue", size = 1) +
  geom_ribbon(data = stats, aes(x = 0:max_int_timestamp, y = mean, ymin = mean - std, ymax = mean + std),
              fill = "blue", alpha = 0.1) +
  scale_color_identity()
  scale_fill_identity("", guide = "legend", labels = c("blue" = "blue"))
  scale_colour_manual(name = "colours", values = c('blue' = 'blue'), labels = c('c1'))

