import math
import subprocess
import statistics

with open('results2.csv', 'w') as results:
    for array_size in [10**x for x in range(7)]:
        print('========== array size:', math.log10(array_size))
        string_length = 1
        while string_length <= min(array_size, 10**7):
            print('string length:', math.log10(string_length))
            for num_nodes in [10**x for x in range(7)]:
                usage = []
                for _ in range(3):
                    p1 = subprocess.Popen(['/usr/bin/time',  '-v', 'java', 'Component', str(num_nodes),
                                           str(array_size), str(string_length)], stderr=subprocess.PIPE)
                    words = p1.stderr.read().split()
                    usage.append(int(words[words.index(b'resident') + 4]))
                results.write(','.join([str(array_size), str(string_length), str(num_nodes),
                                        str(statistics.median(usage))]) + '\n')
            string_length *= 10
