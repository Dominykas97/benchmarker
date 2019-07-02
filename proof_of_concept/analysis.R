data <- read.csv("results.csv", header = FALSE)
names(data) <- c("array", "string", "memory")
attach(data)
regression <- lm(memory ~ array + string)
summary(regression)

plot(array, memory, col = log(string, 2) + 1, log = "xy")
for (i in 0:8) {
  s = 2 ^ i
  abline(regression$coefficients[1] + regression$coefficients[3] * s, regression$coefficients[2],
         untf = TRUE, col = i + 1)
}

# Quadratic? Doesn't help that much
string2 <- string ^ 2
regression2 <- lm(memory ~ array + string + string2)
summary(regression2)

# Set one of the coefficients to 1024
remaining_memory <- memory - 1024 * array
regression3 <- lm(remaining_memory ~ string)
summary(regression3)

plot(array, memory, col = log(string, 2) + 1, log = "xy")
for (i in 0:8) {
  s = 2 ^ i
  abline(regression3$coefficients[1] + regression3$coefficients[2] * s, 1024, untf = TRUE, col = i + 1)
}
plot(string, remaining_memory, log = "xy")
abline(regression3$coefficients[1], regression3$coefficients[2], untf = TRUE)
