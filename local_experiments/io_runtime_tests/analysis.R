expand_data_frame <- function(data) {
  names(data) <- c("num_requests", "response_size", "database_latency", "bandwidth", "interval_between_requests",
                   "java_num_nodes", "java_bandwidth_latency", "total_time")
  NODE_SIZE <- 61.94
  MB <- bitwShiftL(1, 20)
  data$total_time <- data$total_time / 1e9
  data$expected_time <- data$num_requests * (data$database_latency / 1000 + data$response_size * 8 / data$bandwidth) +
    (data$num_requests - 1) * data$interval_between_requests / 1000
  data$bandwidth_latency <- NODE_SIZE / (data$bandwidth / 8 * MB) * 1000 # ms
  data$num_nodes <- data$response_size * MB / NODE_SIZE
  data$error <- data$total_time - data$expected_time
  data
}

#data <- read.csv("results_ratio.csv", header = FALSE)
data <- read.csv("results.csv", header = FALSE)
data <- expand_data_frame(data)

max(abs(data$java_num_nodes - data$num_nodes))
#max(abs(data$java_bandwidth_latency - data$bandwidth_latency))

summary(data$error)
plot(data$error)
plot(data$expected_time, data$total_time)
abline(0, 1)
