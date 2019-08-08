import itertools
import math
import subprocess
import statistics

# num_requests = 1 => time = database_latency + response_size / bandwidth

# in ms
def expected_time(num_requests, response_size, database_latency, bandwidth, interval_between_requests):
    return num_requests * (database_latency + response_size * 8 / bandwidth * 1000) + (
        num_requests - 1) * interval_between_requests

def run_experiments(parameters, name = '', reps = 5):
    filename = 'results{}.csv'.format('_' + name if name else '')
    with open(filename, 'w') as results:
        for num_requests, response_size, database_latency, bandwidth, interval_between_requests in itertools.product(*parameters):
            # if the experiment is expected to take > 1 min, skip it
            if expected_time(num_requests, response_size, database_latency, bandwidth, interval_between_requests) > 6e4:
                print('Skipping an experiment because it would take too long')
                continue
            print(num_requests, response_size, database_latency)
            for _ in range(reps):
                p1 = subprocess.Popen(['java', 'Component', str(num_requests), str(response_size), str(database_latency),
                                       str(bandwidth), str(interval_between_requests)], stdout=subprocess.PIPE)
                results.write(','.join([str(num_requests), str(response_size), str(database_latency), str(bandwidth),
                                        str(interval_between_requests), str(p1.stdout.read(), 'utf-8')[:-1]]) + '\n')

#run_experiments([[1], [0.001, 0.01, 0.1, 1, 10, 100, 1000], [0], [0.001, 0.01, 0.1, 1, 10, 100, 1000], [0]], 'ratio')
run_experiments([[1, 2, 4, 8], [0.001, 0.01, 0.1, 1, 10, 100, 1000], [0, 100, 1000], [0.001, 0.01, 0.1, 1, 10, 100, 1000], [0, 100, 1000]], '', 1)
