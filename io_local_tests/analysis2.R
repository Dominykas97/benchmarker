# Building a linear regression model for predictions

library(plotly)

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

# Scale the colormap
# The colours are adjusted so that:
# 1) If we have more positive than negative values, positive colours will be stronger
# 2) Zero will always be im the middle of the colour range

library(RColorBrewer)
midpoint <- -min(rel_errors) / (max(rel_errors) - min(rel_errors))
colours <- brewer.pal(n = 11, name = "Spectral")
smallest_colour <- 1 + round(-min(rel_errors) / (2 * max(rel_errors)) * 10) # which colour to assign to the min value
first_part <- lapply(smallest_colour:6,
                     function(colour) c((colour - smallest_colour) * midpoint / (6 - smallest_colour + 1), colours[colour]))
second_part <- lapply(7:11, function(colour) c(midpoint + (colour - 6) * (1 - midpoint) / 5, colours[colour]))
colorscale <- c(first_part, second_part)

plot_ly(x = array_size, y = num_nodes, z = string_length, type = "scatter3d", mode = "markers",
        marker = list(color = as.vector(rel_errors), colorscale = colorscale, showscale = TRUE,
                      colorbar = list(title = "relative error"))) %>%
  layout(scene = list(xaxis = list(title = "array size", type = "log"), yaxis = list(title = "number of nodes", type = "log"),
                      zaxis = list(title = "string length", type = "log")))

plot(array_size, memory, log = "xy")
abline(regression$coefficients[1], regression$coefficients[2], untf = TRUE)
plot(string_length, memory, log = "xy")
abline(regression$coefficients[1], regression$coefficients[3], untf = TRUE)
plot(num_nodes, memory, log = "xy")
abline(regression$coefficients[1], regression$coefficients[4], untf = TRUE)

plot(errors)
plot(array_size, errors, log = "x")
plot(string_length, errors, log = "x")
plot(num_nodes, errors, log = "x")

