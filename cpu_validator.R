library(rjson)

DATA_DIRECTORY <- "data"
DATA_FILENAME <- "cpu"
EPSILON <- 1e-2
EXPECTED_TIME <- 0
ALPHA <- 0.05

# Find all relevant files
files = list.files(paste0(DATA_DIRECTORY, "/"), sprintf("%s_*", DATA_FILENAME),
                   full.names = TRUE)

durations <- c()

for (file in files) {
  data <- unlist(fromJSON(file = file)$data$result[[1]]$values)
  durations <- c(durations, as.double(data[length(data) - 1]) - as.double(data[1]))
}

library(fitdistrplus)
library(evd)

durations[durations == 0] <- EPSILON
fit <- fitdist(durations, "lnorm")
fit2 <- fitdist(durations, "frechet", start = list(loc = 0, scale = 1, shape = 1))
legend <- c("log-normal", "Frechet")

#summary(fit)
#summary(fit2)

par(mfrow = c(2, 2))
denscomp(list(fit, fit2), legendtext = legend)
cdfcomp(list(fit, fit2), legendtext = legend)
qqcomp(list(fit, fit2), legendtext = legend)
ppcomp(list(fit, fit2), legendtext = legend)

# Compare with mode, since mean can be infinite.
# If x < mode, p-value = CDF(x) + 1 - CDF(2 * mode - x)
# If x >= mode, p-value = 1 - CDF(x) + CDF(2 * mode - x)
# We want: p-value >= 0.05

location <- fit2$estimate[1]
scale <- fit2$estimate[2]
shape <- fit2$estimate[3]

mode <- location + scale * (shape / (1 + shape)) ^ (1 / shape)
p <- pfrechet(EXPECTED_TIME, loc = location, scale = scale, shape = shape) +
  1 - pfrechet(2 * mode - EXPECTED_TIME, loc = location, scale = scale, shape = shape)

result <- ifelse(p >= ALPHA, "fits", "does not fit")

print(sprintf("Our expectation: %d", EXPECTED_TIME))
print(sprintf("Mode of the data: %f", mode))
print(sprintf("The CPU time data %s our expectations with a p-value of %f", result, p))
