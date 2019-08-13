import json
import matplotlib.pyplot as plt
import os
import yaml

LOCAL_DIR_WITH_DATA = '../benchmarker_data'
LOCAL_DIR_WITH_PLOTS = '../benchmarker_plots'

# Read in a list of metrics
with open('config/global.yaml', 'r') as config:
    metrics = yaml.safe_load(config)['metrics']
files = os.listdir(LOCAL_DIR_WITH_DATA)

# Read in the performance data
data = {}
first_timestamp = float('inf')
for filename in files:
    with open('{}/{}'.format(LOCAL_DIR_WITH_DATA, filename)) as f:
        data[filename] = json.loads(f.read())
    first_timestamp = min(first_timestamp, min(series['values'][0][0]
                                               for series in data[filename]['data']['result']))

def transform_value(metric_name, value):
    'Transform heap usage from bytes into MB'
    return int(value) >> 20 if metric_name.startswith('heap') else float(value)

def add_expected_line_to_plot(metric_name, x):
    'Only makes sense for single-component setups'
    #if metric_name.startswith('cpu'):
    #    plt.plot([x[0], x[-1]], [1, 1], label='expected')
    if metric_name.startswith('heap'):
        with open('config/components.yaml', 'r') as components:
            expected_memory_usage = yaml.safe_load(components)[0]['memoryUsage']
        plt.plot([x[0], x[-1]], [expected_memory_usage, expected_memory_usage], label='expected')

for i, filename in enumerate(files):
    plt.figure(i + 1)
    for i, series in enumerate(data[filename]['data']['result']):
        x = []
        y = []
        for timestamp, value in series['values']:
            x.append(timestamp - first_timestamp)
            y.append(transform_value(filename, value))
        plt.plot(x, y, label='observed')
    #add_expected_line_to_plot(metric['filename'], x)
    plt.xlabel('time')
    metric_name = next(metric['name'] for metric in metrics if metric['filename'] == filename.split('_')[0])
    plt.ylabel(metric_name)
    plt.legend()
    plt.savefig('{}/{}'.format(LOCAL_DIR_WITH_PLOTS, filename.replace('json', 'png')))
