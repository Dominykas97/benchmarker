data <- read.csv("results2.csv", header = FALSE)
names(data) <- c("array_size", "string_length", "num_nodes", "memory")
attach(data)
memory <- memory * 1024

regression <- lm(memory ~ array_size + string_length + num_nodes)
summary(regression)

predictions <- cbind(1, as.matrix(data)[, 1:3]) %*% cbind(regression$coefficients)
rel_errors <- (predictions - memory) / memory

plot(string_length, rel_errors, log = "x")

library(plotly)
plot_ly(x = array_size, y = num_nodes, z = string_length, type = "scatter3d", mode = "markers",
        marker = list(color = as.vector(rel_errors), colorscale = "RdBu", showscale = TRUE,
                      colorbar = list(title = "relative error"))) %>%
  layout(scene = list(xaxis = list(title = "array size", type = "log"), yaxis = list(title = "number of nodes", type = "log"),
                      zaxis = list(title = "string length", type = "log")))
