# Does the expected amount of memory usage match the data?

library(dplyr)
library(rjson)
#library(sn)
library(ismev)

# Construct a data frame of memory usage for a particular amount of expected memory usage
get_memory_usage_data <- function(expected_memory_usage) {
  # Find all relevant files
  files = list.files("data/", sprintf("heap_%d*", expected_memory_usage), full.names = TRUE)

  df <- data.frame(matrix(ncol = 4, nrow = 0))
  colnames(df) <- c("run_id", "output_size", "timestamp", "memory_usage")

  i <- 1
  for (file in files) {
    truncated_filename <- sub("[^_]+_[^_]+_", "", file)
    output_size <- as.integer(sub("_.*", "", truncated_filename))

    data <- unlist(fromJSON(file = file)$data$result[[1]]$values)
    value <- bitwShiftR(as.integer(data[c(FALSE, TRUE)]), 20)
    new_rows <- data.frame(run_id = i, output_size = output_size, timestamp = 0:(length(value) - 1), memory_usage = value)
    df <- rbind(df, new_rows)
    i <- i + 1
  }
  df
}

df <- get_memory_usage_data(512)
data <- df %>% group_by(run_id) %>% summarise(memory_usage = max(memory_usage))

# Skewed normal distribution produces a confidence interval for the mean that's slightly above our expected value
#model <- selm(data$memory_usage ~ 1, data = data)
#cf <- confint(model)

fit <- gum.fit(data$memory_usage)
gum.diag(fit)
