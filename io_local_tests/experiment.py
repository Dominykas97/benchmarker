import subprocess
import statistics

memory = 1

with open('results.csv', 'w') as results:
    for num_nodes in [0] + [2**x for x in range(25)]:
        usage = []
        for _ in range(5):
            p1 = subprocess.Popen(['/usr/bin/time',  '-v', 'java', 'Component', str(num_nodes)],
                                  stderr=subprocess.PIPE)
            words = p1.stderr.read().split()
            usage.append(int(words[words.index(b'resident') + 4]))
        results.write(','.join([str(num_nodes), str(statistics.median(usage))]) + '\n')
