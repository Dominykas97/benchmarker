import subprocess
import statistics

memory = 1

with open('results2.csv', 'w') as results: # results.csv
    while memory <= 512:
        output = 1
        while output <= min(memory, 256):
            usage = []
            for _ in range(3):
                p1 = subprocess.Popen(['/usr/bin/time',  '-v', 'java', 'FullComponent', str(memory), str(output)],
                                      stderr=subprocess.PIPE) # Component
                words = p1.stderr.read().split()
                usage.append(int(words[words.index(b'resident') + 4]))
            results.write(','.join([str(memory), str(output), str(statistics.median(usage))]) + '\n')
            output *= 2
        memory *= 2
