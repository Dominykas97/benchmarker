data <- read.csv("results_incomplete.csv", header = FALSE)
names(data) <- c("num_requests", "response_size", "database_latency", "bandwidth", "interval_between_requests", "total_time")
data$total_time <- data$total_time / 1e9
attach(data)

NODE_SIZE <- 61.94
MB <- bitwShiftL(1, 20)
expected_time <- num_requests * (database_latency / 1000 + response_size * 8 / bandwidth) +
  (num_requests - 1) * interval_between_requests / 1000 # seconds
bandwidth_latency <- NODE_SIZE / (bandwidth / 8 * MB) # seconds

errors <- total_time - expected_time
plot(density((errors)))
summary(errors)

rel_errors <- total_time - expected_time / expected_time
plot(density(rel_errors))
summary(rel_errors)
boxplot(rel_errors)

# All negative
big_errors <- errors[errors < -60 | errors > 60]
length(big_errors) / length(errors)
summary(big_errors)

small_errors <- errors[errors > -60 & errors < 60]
length(small_errors) / length(errors)

plot(interval_between_requests, errors, log = "x")
summary(expected_time)
plot(expected_time, total_time, log = "xy")
abline(0, 1, untf = TRUE)
plot(bandwidth_latency, errors, log = "x")

data[errors < -7000, ]

# Let's try to fit a linear model?
regression <- lm(total_time ~ num_requests * response_size * database_latency * bandwidth * interval_between_requests)
summary(regression)
