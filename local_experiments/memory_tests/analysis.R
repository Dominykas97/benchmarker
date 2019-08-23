data <- read.csv("results.csv", header = FALSE)
names(data) <- c("array", "string", "memory")
attach(data)

regression <- lm(memory ~ array + string)
summary(regression)

plot(array, memory, col = log(string, 2) + 1, log = "xy", xlab = "array size", ylab = "memory usage")
for (i in 0:8) {
  s = 2 ^ i
  abline(regression$coefficients[1] + regression$coefficients[3] * s, regression$coefficients[2],
         untf = TRUE, col = i + 1)
}

# Quadratic? Doesn't help that much
string2 <- string ^ 2
regression2 <- lm(memory ~ array + string + string2)
summary(regression2)

# Fix one of the coefficients
memory <- memory / 1024
remaining_memory <- memory - array
regression3 <- lm(remaining_memory ~ string)
summary(regression3)

plot(array, memory, col = log(string, 2) + 1, log = "xy", xlab = "array size (MiB)", ylab = "memory usage (MiB)")
for (i in 0:8) {
  s = 2 ^ i
  abline(regression3$coefficients[1] + regression3$coefficients[2] * s, 1, untf = TRUE, col = i + 1)
}

library(ggplot2)
df <- data.frame(string = string, remaining_memory = remaining_memory)
ggplot(df, aes(string, remaining_memory)) + geom_jitter(width = 0.4) + geom_smooth(method = 'lm') +
  coord_trans(x = "log10", y = "log10") + xlab("string length") + ylab("remaining memory (MiB)")
