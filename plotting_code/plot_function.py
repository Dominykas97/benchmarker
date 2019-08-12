import math
import matplotlib.pyplot as plt
import numpy as np

bin_width = 1

def f(x):
    return 10 + x + 5 * np.sin(-4 * x + 4) + 10 * np.exp(-(x - 10)**2 / 2)

#plt.xkcd() <- I had no idea this existed!

plt.xlim(0, 15)
plt.xlabel('time (s)')
plt.ylabel('frequency (Hz)')

x = np.arange(0, 15, 0.0001)
plt.plot(x, f(x))

special_x = np.arange(bin_width / 2, 15, bin_width)
plt.plot(special_x, f(special_x), 'o')

boundaries = np.arange(0, 15, bin_width)
for b in boundaries:
    plt.axvline(x=b, linestyle='--', color='grey')

plt.show()
