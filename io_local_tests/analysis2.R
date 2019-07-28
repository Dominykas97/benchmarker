# Building a linear regression model for predictions

data <- read.csv("results2_3.csv", header = FALSE)
names(data) <- c("array_size", "string_length", "num_nodes", "memory")
attach(data)
memory <- memory * 1024

# Only linear terms

regression <- lm(memory ~ array_size + string_length + num_nodes)
predictions <- cbind(1, as.matrix(data)[, 1:3]) %*% cbind(regression$coefficients)

# Weighted regression?
weights <- 1 / memory ^ 2
regression <- lm(memory ~ array_size + string_length + num_nodes, weights = weights)
predictions <- cbind(1, as.matrix(data)[, 1:3]) %*% cbind(regression$coefficients)

# Quadratic terms

array_size2 <- array_size ^ 2
string_length2 <- string_length ^ 2
num_nodes2 <- num_nodes ^ 2

regression <- lm(memory ~ array_size + string_length + num_nodes + array_size2 + string_length2 + num_nodes2)
predictions <- cbind(1, as.matrix(data)[, 1:3], array_size2, string_length2, num_nodes2) %*% cbind(regression$coefficients)

# Results & plotting

summary(regression)

rel_errors <- (predictions - memory) / memory
errors <- predictions - memory
summary(rel_errors)

# 3D relative error plot
source("../plotting.R")
coloured_scatter_3d(array_size, num_nodes, string_length, rel_errors,
                    "array size", "number of nodes", "string length", "relative error")

plot(array_size, memory, log = "xy")
abline(regression$coefficients[1], regression$coefficients[2], untf = TRUE)
plot(string_length, memory, log = "xy")
abline(regression$coefficients[1], regression$coefficients[3], untf = TRUE)
plot(num_nodes, memory, log = "xy")
abline(regression$coefficients[1], regression$coefficients[4], untf = TRUE)

plot(errors)
plot(rel_errors)
plot(array_size, errors, log = "x")
plot(string_length, errors, log = "x")
plot(num_nodes, errors, log = "x")

