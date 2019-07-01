import subprocess
import statistics

memory = 1

with open('results.csv', 'w') as results:
    while memory <= 512:
        output = 1
        while output <= min(memory, 256):
            usage = []
            for _ in range(3):
                p1 = subprocess.Popen(['/usr/bin/time',  '-v', 'java', 'Component', str(memory), str(output)],
                                      stderr=subprocess.PIPE)
                words = p1.stderr.read().split()
                usage.append(int(words[words.index(b'resident') + 4]))
            results.write(','.join([str(memory), str(output), str(statistics.median(usage))]) + '\n')
            output *= 2
        memory *= 2
