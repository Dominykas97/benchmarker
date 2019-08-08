import math
import subprocess
import statistics

MB = 1 << 20
BASE_MEMORY_CONSUMPTION = 2.409e7
BYTES_PER_CHAR = 5.79

with open('results2.csv', 'w') as results:
    for memory_usage in [2**x for x in range(11)]:
        leftover_memory = memory_usage * MB - BASE_MEMORY_CONSUMPTION
        if leftover_memory <= 0:
            continue
        print('========== memory usage:', memory_usage)

        output_size = 1
        while output_size * (MB + MB / BYTES_PER_CHAR) < leftover_memory:
            print('output size:', output_size)
            response_size = 1
            while response_size * MB <= leftover_memory - output_size * (MB + MB / BYTES_PER_CHAR):
                usage = []
                for _ in range(3):
                    p1 = subprocess.Popen(['/usr/bin/time',  '-v', 'java', 'FullComponent', str(memory_usage),
                                           str(output_size), str(response_size)], stderr=subprocess.PIPE)
                    words = p1.stderr.read().split()
                    usage.append(int(words[words.index(b'resident') + 4]))
                results.write(','.join([str(memory_usage), str(output_size), str(response_size),
                                        str(statistics.median(usage))]) + '\n')
                response_size *= 2
            output_size *= 2


# stringLength <= arraySize

# arraySize = memoryUsage * MB - BASE_MEMORY_CONSUMPTION - outputSize * KB - responseSize * MB
# stringLength = outputSize * KB / BYTES_PER_CHAR

# outputSize * KB / BYTES_PER_CHAR <= memoryUsage * MB - BASE_MEMORY_CONSUMPTION - outputSize * KB - responseSize * MB
# outputSize * KB / BYTES_PER_CHAR + outputSize * KB <= memoryUsage * MB - BASE_MEMORY_CONSUMPTION - responseSize * MB
# outputSize * (KB / BYTES_PER_CHAR + KB) <= memoryUsage * MB - BASE_MEMORY_CONSUMPTION - responseSize * MB
# outputSize * (KB / BYTES_PER_CHAR + KB) + responseSize * MB <= memoryUsage * MB - BASE_MEMORY_CONSUMPTION
# outputSize * (KB / BYTES_PER_CHAR + KB) + responseSize * MB <= leftover_memory
