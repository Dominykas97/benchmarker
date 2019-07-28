# Exploring how well the linear regression model works in practice

data <- read.csv("results3_3.csv", header = FALSE)
names(data) <- c("expected_memory_usage", "output_size", "response_size", "memory")
data$expected_memory_usage <- data$expected_memory_usage * 1024
attach(data)

rel_errors <- (memory - expected_memory_usage) / expected_memory_usage
errors <- (memory - expected_memory_usage) / 1024
plot(rel_errors)
plot(errors)

source("../plotting.R")
coloured_scatter_3d(response_size, output_size, expected_memory_usage, rel_errors,
                    "response size", "output size", "expected memory usage", "relative error")
coloured_scatter_3d(response_size, output_size, expected_memory_usage, errors,
                    "response size", "output size", "expected memory usage", "error")
