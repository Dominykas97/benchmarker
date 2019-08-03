data <- read.csv("simple_results.csv", header = FALSE)
names(data) <- c("num_requests", "response_size", "database_latency", "bandwidth", "interval_between_requests",
                 "java_num_nodes", "java_bandwidth_latency", "total_time")

NODE_SIZE <- 61.94
MB <- bitwShiftL(1, 20)
data$total_time <- data$total_time / 1e9
data$expected_time <- data$database_latency / 1000 + data$response_size * 8 / data$bandwidth
data$bandwidth_latency <- NODE_SIZE / (data$bandwidth / 8 * MB) * 1000 # ms
data$num_nodes <- data$response_size * MB / NODE_SIZE
data$error <- data$expected_time - data$total_time
attach(data)

plot(error)
plot(database_latency, error)
plot(response_size * 8 / bandwidth, total_time)

plot(bandwidth_latency, error, log = "x")
plot(num_nodes, error)
