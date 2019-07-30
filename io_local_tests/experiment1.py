import math
import subprocess
import statistics

with open('results1.csv', 'w') as results:
    for min_array_size in [10**x for x in range(9)]:
        for array_size in [min_array_size * i for i in range(1, 10, 2)]:
            print('========== array size:', array_size)
            for min_string_length in [10**x for x in range(7)]:
                string_length = min_string_length
                while string_length <= array_size and string_length < 10 * min_string_length:
                    print('string length:', string_length)
                    for min_num_nodes in [10**x for x in range(7)]:
                        for num_nodes in [min_num_nodes * i for i in range(1, 10, 2)]:
                            #print(array_size, string_length, num_nodes)
                            p1 = subprocess.Popen(['/usr/bin/time',  '-v', 'java', 'Component', str(num_nodes),
                                                   str(array_size), str(string_length)], stderr=subprocess.PIPE)
                            words = p1.stderr.read().split()
                            memory_usage = int(words[words.index(b'resident') + 4])
                            results.write(','.join([str(array_size), str(string_length), str(num_nodes),
                                                    str(memory_usage)]) + '\n')
                    string_length += 2 * min_string_length
