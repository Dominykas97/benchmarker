# Exploring how well the linear regression model works in practice
library(ggplot2)

data <- read.csv("results2.csv", header = FALSE)
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
                    "response size", "output size", "expected memory usage", "error (MiB)")

plot(expected_memory_usage, memory, log = "xy")
abline(0, 1)

plot(density(rel_errors), main = "")
plot(density(errors), main = "")
sum(rel_errors < 0.1 & rel_errors > -0.1) / length(rel_errors)
sum(errors > 1000 * 1024 | errors < 1000 * 1024) / length(errors)

plot(expected_memory_usage, errors, log = "x") # increase in variance
plot(output_size, errors, log = "x") # a shift from positive to negative
plot(response_size, errors, log = "x") # increase in variance

df <- data.frame(expected_memory_usage = as.factor(expected_memory_usage / 1024), output_size = as.factor(output_size),
                 response_size = as.factor(response_size), errors = errors, rel_errors = rel_errors)
ggplot(df, aes(x = expected_memory_usage, y = errors)) + geom_violin() + scale_x_discrete(name = "expected memory usage") +
  scale_y_continuous(name = "error (MiB)")
ggplot(df, aes(x = output_size, y = errors)) + geom_violin() + scale_x_discrete(name = "output size") +
  scale_y_continuous(name = "error (MiB)")
ggplot(df, aes(x = response_size, y = errors)) + geom_violin() + scale_x_discrete(name = "response size") +
  scale_y_continuous(name = "error (MiB)")
