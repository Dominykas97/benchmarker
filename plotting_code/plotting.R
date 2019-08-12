library(plotly)
library(RColorBrewer)

# The colours are adjusted so that:
# 1) If we have more positive than negative values, positive colours will occupy more of the total colour range.
# 2) Zero will always be im the middle of the colour range.
# The "..." allows one to, e.g., set the size of each dot according to how many data points there are.
coloured_scatter_3d <- function(x_values, y_values, z_values, col_values, x_title, y_title, z_title, col_title, ...) {
  num_colours <- 11
  colour_for_zero <- 6
  colours <- brewer.pal(n = num_colours, name = "Spectral")

  # All assumptions can be easily lifted, if needed
  stopifnot(num_colours <= 11)
  #stopifnot(abs(max(col_values)) >= abs(min(col_values)))
  stopifnot(max(col_values) >= 0)
  stopifnot(min(col_values) <= 0)

  # If the range of col_values is scaled to [0, 1], where does zero go?
  midpoint <- -min(col_values) / (max(col_values) - min(col_values))

  # which colour to assign to the min value
  max_abs <- max(abs(col_values))
  smallest_colour <- 1 + max(round((max(col_values) + min(col_values)) / (2 * max_abs) * (num_colours - 1)), 0)
  largest_colour <- 1 + min(round(max(col_values) - min(col_values) / (2 * max_abs) * (num_colours - 1)), 10)

  # Lists of tuples (x, y), where y is the colour and x is its position in [0, 1]
  first_part <- lapply(smallest_colour:colour_for_zero, function(colour)
    c((colour - smallest_colour) * midpoint / (colour_for_zero - smallest_colour + 1), colours[colour]))
  second_part <- lapply((colour_for_zero + 1):num_colours, function(colour)
    c(midpoint + (colour - colour_for_zero) * (1 - midpoint) / (num_colours - colour_for_zero), colours[colour]))
  colorscale <- c(first_part, second_part)

  plot_ly(x = x_values, y = y_values, z = z_values, type = "scatter3d", mode = "markers",
          marker = list(color = as.vector(col_values), colorscale = colorscale, showscale = TRUE,
                        colorbar = list(title = col_title), ...)) %>%
    layout(scene = list(xaxis = list(title = x_title, type = "log"), yaxis = list(title = y_title, type = "log"),
                        zaxis = list(title = z_title, type = "log")))
}
