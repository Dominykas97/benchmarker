data <- read.csv("results2.csv", header = FALSE)
names(data) <- c("expectedMemory", "stringSize", "observedMemory")
attach(data)
expectedMemory <- 1024 * expectedMemory

plot(expectedMemory, observedMemory, col = log(stringSize, 2), log = "xy")
abline(0, 1, untf = TRUE)
errors <- (observedMemory - expectedMemory) / 1024
plot(errors)
# Constantly underestimating, but max error is about 1 MB (with total memory usage going up to 512 MB)
