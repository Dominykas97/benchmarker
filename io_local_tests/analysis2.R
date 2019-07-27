# Building a linear regression model for predictions

library(plotly)

data <- read.csv("results2_3.csv", header = FALSE)
names(data) <- c("array_size", "string_length", "num_nodes", "memory")
attach(data)
memory <- memory * 1024

# Only linear terms

regression <- lm(memory ~ array_size + string_length + num_nodes)
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

plot_ly(x = array_size, y = num_nodes, z = string_length, type = "scatter3d", mode = "markers",
        marker = list(color = as.vector(rel_errors), colorscale = "RdBu", showscale = TRUE,
                      colorbar = list(title = "relative error"))) %>%
  layout(scene = list(xaxis = list(title = "array size", type = "log"), yaxis = list(title = "number of nodes", type = "log"),
                      zaxis = list(title = "string length", type = "log")))

plot(array_size, memory)
abline(regression$coefficients[1], regression$coefficients[2], untf = TRUE)
plot(string_length, memory)
abline(regression$coefficients[1], regression$coefficients[3], untf = TRUE)
plot(num_nodes, memory)
abline(regression$coefficients[1], regression$coefficients[4], untf = TRUE)

plot(errors)
plot(array_size, errors, log = "x")
plot(string_length, errors, log = "x")
plot(num_nodes, errors, log = "x")
