# Does the expected amount of memory usage match the data?

library(dplyr)
library(rjson)
library(ismev)

EXPECTED_MEMORY_USAGE <- 512
DATA_DIRECTORY <- "data"
DATA_FILENAME <- "heap"
ALPHA <- 0.05

# Construct a data frame of memory usage for a particular amount of expected memory usage
get_memory_usage_data <- function(expected_memory_usage) {
  # Find all relevant files
  files = list.files(paste0(DATA_DIRECTORY, "/"), sprintf("%s_%d*", DATA_FILENAME, expected_memory_usage),
                     full.names = TRUE)

  df <- data.frame(matrix(ncol = 4, nrow = 0))
  colnames(df) <- c("run_id", "output_size", "timestamp", "memory_usage")

  i <- 1
  for (file in files) {
    truncated_filename <- sub("[^_]+_[^_]+_", "", file)
    output_size <- as.integer(sub("_.*", "", truncated_filename))

    data <- unlist(fromJSON(file = file)$data$result[[1]]$values)
    value <- bitwShiftR(as.integer(data[c(FALSE, TRUE)]), 20)
    new_rows <- data.frame(run_id = i, output_size = output_size, timestamp = 0:(length(value) - 1),
                           memory_usage = value)
    df <- rbind(df, new_rows)
    i <- i + 1
  }
  df
}


df <- get_memory_usage_data(EXPECTED_MEMORY_USAGE)
data <- df %>% group_by(run_id) %>% summarise(memory_usage = max(memory_usage))
fit <- gum.fit(data$memory_usage)
location <- fit$mle[1]
scale <- fit$mle[2]

p <- pgumbel(EXPECTED_MEMORY_USAGE, loc = location, scale = scale) +
  1 - pgumbel(2 * location - EXPECTED_MEMORY_USAGE, loc = location, scale = scale)
result <- ifelse(p >= ALPHA, "fits", "does not fit")

#gum.diag(fit)

print(sprintf("Our expectation: %d", EXPECTED_MEMORY_USAGE))
print(sprintf("Mode of the data: %f", location))
print(sprintf("The data %s our expectations with a p-value of %f", result, p))

