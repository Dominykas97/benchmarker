data <- read.csv("results2.csv", header = FALSE)
names(data) <- c("expectedMemory", "stringSize", "observedMemory")
attach(data)
expectedMemory <- 1024 * expectedMemory

plot(expectedMemory, observedMemory, log = "xy")
abline(0, 1, untf = TRUE)
errors <- (observedMemory - expectedMemory) / 1024
plot(errors)
# Constantly underestimating, but max error is about 1 MB (with total memory usage going up to 512 MB)

matrix <- matrix(NA, length(unique(stringSize)), length(unique(expectedMemory)))
rownames(matrix) <- sort(unique(stringSize))
colnames(matrix) <- sort(unique(expectedMemory / 1024))

for (i in 1:nrow(data)) {
  matrix[toString(data[i, "stringSize"]),
         toString(data[i, "expectedMemory"])] <- errors[i]
}

library(gplots)
heatmap(matrix, Rowv = NA, Colv = NA, Colw = NA)

library(lattice)
levelplot(matrix, pretty = TRUE, xlab = "string size (MiB)", ylab = "expected memory usage (MiB)")
