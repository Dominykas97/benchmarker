library("ggplot2")
library("reshape2")
library("rjson")

# Find all relevant files
cpu_files = list.files("data/", "cpu*", full.names = TRUE)

df <- data.frame(matrix(ncol = 5, nrow = 0))
colnames(df) <- c("variable", "timestamp", "value", "colour", "size")

max_timestamp <- 0
i <- 1
for (file in cpu_files) {
  data <- unlist(fromJSON(file = file)$data$result[[1]]$values)
  value <- as.double(data[c(FALSE, TRUE)])
  new_rows <- data.frame(variable = sprintf("s%d", i), timestamp = 0:(length(value) - 1), value = value,
                         colour = rgb(0, 0, 0, alpha = 0.2), size = 0.5)
  df <- rbind(df, new_rows)
  max_timestamp = max(max_timestamp, length(value) - 1)
  i <- i + 1
}

# Calculate mean, median, and standard deviation over time
mean_df <- data.frame(variable = character(), timestamp = integer(), value = double(), colour = character(), size = double())
median_df <- data.frame(variable = character(), timestamp = integer(), value = double(), colour = character(), size = double())
std_df <- data.frame(timestamp = integer(), mean = double(), std = double())
for (i in 0:max_timestamp) {
  values_at_time_i <- df$value[df$timestamp == i]

  median_row <- data.frame(variable = "median", timestamp = i, value = median(values_at_time_i), colour = "red", size = 1)
  mean_row <- data.frame(variable = "mean", timestamp = i, value = mean(values_at_time_i), colour = "blue", size = 1)
  std_row <- data.frame(timestamp = i, mean = mean(values_at_time_i), std = sd(values_at_time_i))

  mean_df <- rbind(mean_df, median_row)
  median_df <- rbind(median_df, mean_row)
  std_df <- rbind(std_df, std_row)
}
df <- rbind(df, mean_df, median_df)

ggplot(data = df, aes(x = timestamp, y = value)) + xlab("time") + ylab("CPU Usage") +
  geom_line(aes(group = variable), colour = df$colour, size = df$size) +
  geom_ribbon(data = std_df, aes(x = timestamp, y = mean, ymin = mean - std, ymax = mean + std),
              fill = "blue", alpha = 0.1)

