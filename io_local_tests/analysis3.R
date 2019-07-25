# Exploring how well the linear regression model works in practice

data <- read.csv("results3.csv", header = FALSE)
names(data) <- c("expected_memory_usage", "output_size", "response_size", "memory")
data$expected_memory_usage <- data$expected_memory_usage * 1024
attach(data)

rel_errors <- (memory - expected_memory_usage) / expected_memory_usage

library(plotly)
plot_ly(x = response_size, y = output_size, z = expected_memory_usage, type = "scatter3d", mode = "markers",
        marker = list(color = as.vector(rel_errors), colorscale = "RdBu", showscale = TRUE,
                      colorbar = list(title = "relative error"))) %>%
  layout(scene = list(xaxis = list(title = "response size", type = "log"), yaxis = list(title = "output size", type = "log"),
                      zaxis = list(title = "expected memory usage", type = "log")))
