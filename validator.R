# Does the expected amount of memory usage match the data?

library(dplyr)
library(rjson)
library(ismev)

EXPECTED_MEMORY_USAGE <- 512
NUM_STANDARD_ERRORS <- 2
DATA_DIRECTORY <- "data"
DATA_FILENAME <- "heap"

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
result <- ifelse(EXPECTED_MEMORY_USAGE >= fit$mle[1] - NUM_STANDARD_ERRORS * fit$se[1] &&
                   EXPECTED_MEMORY_USAGE <= fit$mle[1] + NUM_STANDARD_ERRORS * fit$se[1], "fits", "does not fit")

#gum.diag(fit)

cat("Expected memory usage:", EXPECTED_MEMORY_USAGE, "MB")
cat("Distribution of the data:", fit$mle[1], "+-", NUM_STANDARD_ERRORS * fit$se[1], "MB")
cat("The data", result, "our expectations")
