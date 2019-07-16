import subprocess
import statistics

BASE_MEMORY_CONSUMPTION = 40 << 20
BYTES_PER_CHAR = 3.26845703125

with open('results2.csv', 'w') as results:
    memory = 1
    while memory <= 512:
        output = 1
        while output <= int(((memory << 20) - BASE_MEMORY_CONSUMPTION) * BYTES_PER_CHAR / (BYTES_PER_CHAR + 1)) >> 20:
            usage = []
            for _ in range(3):
                p1 = subprocess.Popen(['/usr/bin/time',  '-v', 'java', 'FullComponent', str(memory), str(output)],
                                      stderr=subprocess.PIPE)
                words = p1.stderr.read().split()
                usage.append(int(words[words.index(b'resident') + 4]))
            results.write(','.join([str(memory), str(output), str(statistics.median(usage))]) + '\n')
            output *= 2
        memory *= 2
