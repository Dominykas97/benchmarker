library(ggplot2)
library(lattice)
library(reshape2)
library(rjson)
library(RColorBrewer)

colour <- list()
colour[c("red", "blue", "green")] <- brewer.pal(n = 3, name = "Set1")

errors = data.frame(expected = integer(), output = integer(), error = integer())

for (expected_heap_usage in c(64, 128, 256, 512)) {
  # Find all relevant files
  cpu_files = list.files("data/", sprintf("heap_%d*", expected_heap_usage), full.names = TRUE)

  df <- data.frame(matrix(ncol = 6, nrow = 0))
  colnames(df) <- c("variable", "timestamp", "value", "colour", "size", "alpha")

  max_timestamp <- 0
  i <- 1
  for (file in cpu_files) {
    data <- unlist(fromJSON(file = file)$data$result[[1]]$values)
    value <- bitwShiftR(as.integer(data[c(FALSE, TRUE)]), 20)
    new_rows <- data.frame(variable = sprintf("s%d", i), timestamp = 0:(length(value) - 1), value = value,
                           colour = rgb(0, 0, 0), size = 0.5, alpha = 0.2)
    df <- rbind(df, new_rows)
    max_timestamp = max(max_timestamp, length(value) - 1)
    i <- i + 1

    truncated_filename <- sub("[^_]+_[^_]+_", "", file)
    output_size <- as.integer(sub("_.*", "", truncated_filename))
    error_row <- data.frame(expected = expected_heap_usage, output = output_size,
                            error = (max(value) - expected_heap_usage) / expected_heap_usage)
    errors <- rbind(errors, error_row)
  }

  # Calculate mean, median, and standard deviation over time
  mean_df <- data.frame(variable = character(), timestamp = integer(), value = double(), colour = character(), size = double(), alpha = double())
  median_df <- data.frame(variable = character(), timestamp = integer(), value = double(), colour = character(), size = double(), alpha = double())
  std_df <- data.frame(timestamp = integer(), mean = double(), std = double())
  for (i in 0:max_timestamp) {
    values_at_time_i <- df$value[df$timestamp == i]

    median_row <- data.frame(variable = "median", timestamp = i, value = median(values_at_time_i), colour = colour$red, size = 1, alpha = 1)
    mean_row <- data.frame(variable = "mean", timestamp = i, value = mean(values_at_time_i), colour = colour$blue, size = 1, alpha = 1)
    std_row <- data.frame(timestamp = i, mean = mean(values_at_time_i), std = sd(values_at_time_i))

    mean_df <- rbind(mean_df, median_row)
    median_df <- rbind(median_df, mean_row)
    std_df <- rbind(std_df, std_row)
  }
  df <- rbind(df, mean_df, median_df)

  ggplot(data = df, aes(x = timestamp, y = value)) + xlab("time (s)") + ylab("memory usage (MiB)") +
    geom_line(aes(group = variable, colour = df$colour), size = df$size, alpha = df$alpha) +
    geom_ribbon(data = std_df, aes(x = timestamp, y = mean, ymin = mean - std, ymax = mean + std, fill = "one standard deviation"), alpha = 0.2) +
    geom_hline(aes(linetype = "expected memory usage", yintercept = expected_heap_usage), size = 1, color = colour$green) +
    scale_colour_manual(values = c("black", "red", "blue", "green"), name = "", labels = c("individual runs", "median", "mean", "expected memory usage")) +
    scale_linetype_manual(name = "", values = 2, guide = guide_legend(override.aes = list(color = "green"))) +
    scale_fill_manual("", values = colour$blue)
  ggsave(sprintf("plots/heap_%d.png", expected_heap_usage))
}

all_outputs <- sort(unique(errors$output))
all_expected <- sort(unique(errors$expected))
matrix <- matrix(NA, length(all_outputs), length(all_expected))
rownames(matrix) <- all_outputs
colnames(matrix) <- all_expected

for (expected in all_expected) {
  for (output in all_outputs) {
    matrix[toString(output), toString(expected)] <- median(errors$error[errors$expected == expected & errors$output == output])
  }
}

# Draw a heatmap with a colormap centered around zero, even if it's not the center of the interval
myPanel <- function(x, y, z, ...) {
  panel.levelplot(x, y, z, ...)
  panel.text(x, y, round(z, 2))
}
cols <- colorRampPalette(brewer.pal(11, "RdBu"))(110)
max_abs <- max(abs(matrix), na.rm = TRUE)
brk <- do.breaks(c(-max_abs, max_abs), 110)
first_true <- which.max(brk > min(matrix, na.rm = TRUE))
brk <- brk[(first_true - 1):length(brk)]
cols <- cols[(first_true - 1):length(cols)]
levelplot(matrix, pretty = TRUE, panel = myPanel, xlab = "string size (MiB)", ylab = "expected memory usage (MiB)",
          col.regions = cols, at = brk, colorkey = list(col = cols, at = brk))
