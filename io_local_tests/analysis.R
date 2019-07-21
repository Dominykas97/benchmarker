data <- read.csv("results.csv", header = FALSE)
names(data) <- c("num_nodes", "memory")
attach(data)
memory <- memory * 1024

# Simple regression
regression <- lm(memory ~ num_nodes)
summary(regression)

fitted1 <- function(x) {
  regression$coefficients[1] + regression$coefficients[2] * x
}

errors <- (fitted1(num_nodes) - memory) / memory
plot(num_nodes, errors, log = "x", xlab = "number of nodes", ylab = "relative error")

num_nodes2 <- num_nodes^2
regression2 <- lm(memory ~ num_nodes + num_nodes2)
summary(regression2)

fitted <- function(x) {
  regression2$coefficients[1] + regression2$coefficients[2] * x + regression2$coefficients[3] * x^2
}

#plot.function(fitted, from = 0, to = max(num_nodes))
#points(num_nodes, memory, xlab = "number of nodes", ylab = "memory usage (MiB)")

errors2 <- (fitted(num_nodes) - memory) / memory
plot(num_nodes, errors2, log = "x", xlab = "number of nodes", ylab = "relative error")
