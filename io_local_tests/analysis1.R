# Building a linear regression model for predictions

data <- read.csv("results1.csv", header = FALSE)
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
errors <- (predictions - memory) / 1024^2
summary(rel_errors)
summary(errors)

# 3D relative error plot
source("../plotting.R")
coloured_scatter_3d(array_size, num_nodes, string_length, rel_errors,
                    "array size", "number of nodes", "string length", "relative error", size = 4)
coloured_scatter_3d(array_size, num_nodes, string_length, errors,
                    "array size", "number of nodes", "string length", "error (MiB)", size = 4)

plot(density(rel_errors), main = "")
plot(density(errors), main = "")

# Probably not informative enough to be included
plot(array_size, memory, log = "xy")
abline(regression$coefficients[1], regression$coefficients[2], untf = TRUE)
plot(string_length, memory, log = "xy")
abline(regression$coefficients[1], regression$coefficients[3], untf = TRUE)
plot(num_nodes, memory, log = "xy")
abline(regression$coefficients[1], regression$coefficients[4], untf = TRUE)

# Inappropriate for a serious report
plot(errors)
plot(rel_errors)

df <- data.frame(array_size = as.factor(array_size), string_length = as.factor(string_length),
                 num_nodes = as.factor(num_nodes), errors = errors, rel_errors = rel_errors)
ggplot(df, aes(x = array_size, y = errors)) + geom_violin() + scale_x_discrete(name = "array size", breaks = NULL) +
  scale_y_continuous(name = "error (MiB)")
ggplot(df, aes(x = string_length, y = errors)) + geom_violin() + scale_x_discrete(name = "string length", breaks = NULL) +
  scale_y_continuous(name = "error (MiB)")
ggplot(df, aes(x = num_nodes, y = errors)) + geom_violin() + scale_x_discrete(name = "number of nodes", breaks = NULL) +
  scale_y_continuous(name = "error (MiB)")
plot(array_size, errors, log = "x")
plot(string_length, errors, log = "x")
plot(num_nodes, errors, log = "x")

sum(rel_errors < 0.1 & rel_errors > -0.1) / length(rel_errors)
sum(errors > 1000 * 1024 | errors < 1000 * 1024) / length(errors)
