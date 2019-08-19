library(ggplot2)

location <- 272
scale <- 27
expectation <- 256
min <- 200
max <- 500

x <- seq(min, max, length = 1000)

ggplot(data.frame(x = x), aes(x)) +
  stat_function(fun = dgumbel, args = c(location, scale), geom = "area", xlim = c(min, expectation), fill = "pink") +
  stat_function(fun = dgumbel, args = c(location, scale), geom = "area", xlim = c(2 * location - expectation, max), fill = "pink") +
  stat_function(fun = dgumbel, args = c(location, scale)) +
  geom_vline(xintercept = location, linetype = "dotted") +
  geom_vline(xintercept = expectation, linetype = "dashed", color = "red") +
  geom_vline(xintercept = 2 * location - expectation, linetype = "dashed", color = "red") +
  ylab("probability density function") +
  scale_x_continuous("memory usage (MiB)", breaks = c(200, expectation, location, 2 * location - expectation, 300, 400, 500))
