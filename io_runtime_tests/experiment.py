import itertools
import math
import subprocess
import statistics

def expected_time(num_requests, response_size, database_latency, bandwidth, interval_between_requests):
    return num_requests * (database_latency * 1e6 + response_size * 8 / bandwidth * 1e9) + (
        num_requests - 1) * interval_between_requests * 1e6

with open('results.csv', 'w') as results:
    for num_requests, response_size, database_latency, bandwidth, interval_between_requests in itertools.product(
            [1, 2, 4, 8], [0.001, 0.01, 0.1, 1, 10, 100], [1, 10, 100, 1000],
            [0.001, 0.01, 0.1, 1, 10, 100, 1000], [1, 10, 100, 1000]):
        # if the experiment is expected to take > 1 min, skip it
        if expected_time(num_requests, response_size, database_latency, bandwidth, interval_between_requests) > 6e10:
            continue
        print(num_requests, response_size, database_latency)
        measurements = []
        p1 = subprocess.Popen(['java', 'Component', str(num_requests), str(response_size), str(database_latency),
                               str(bandwidth), str(interval_between_requests)], stdout=subprocess.PIPE)
        results.write(','.join([str(num_requests), str(response_size), str(database_latency), str(bandwidth),
                                str(interval_between_requests), str(int(p1.stdout.read()))]) + '\n')
