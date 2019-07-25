# Building a linear regression model for predictions

library(plotly)

data <- read.csv("results2.csv", header = FALSE)
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
summary(rel_errors)

plot_ly(x = array_size, y = num_nodes, z = string_length, type = "scatter3d", mode = "markers",
        marker = list(color = as.vector(rel_errors), colorscale = "RdBu", showscale = TRUE,
                      colorbar = list(title = "relative error"))) %>%
  layout(scene = list(xaxis = list(title = "array size", type = "log"), yaxis = list(title = "number of nodes", type = "log"),
                      zaxis = list(title = "string length", type = "log")))
