data <- read.csv("linkedlist.csv", header = FALSE)
names(data) <- c("num_nodes", "time")
attach(data)

plot(num_nodes, time)

time_per_node <- time / num_nodes
plot(density(time_per_node), log = "x")
summary(time_per_node)
boxplot(time_per_node, outline = FALSE)

# TL;DR: adding one random node to a LinkedList takes about 98 ns
