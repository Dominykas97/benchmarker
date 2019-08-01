import json
import matplotlib.pyplot as plt
import yaml

LOCAL_DIR_WITH_DATA = '../benchmarker_data'
LOCAL_DIR_WITH_PLOTS = 'plots'

# Read in a list of metrics
with open('config/global.yaml', 'r') as config:
    metrics = yaml.safe_load(config)['metrics']

# Read in the performance data
data = {}
first_timestamp = float('inf')
for metric in metrics:
    with open('{}/{}.json'.format(LOCAL_DIR_WITH_DATA, metric['filename'])) as f:
        data[metric['filename']] = json.loads(f.read())
    first_timestamp = min(first_timestamp, min(series['values'][0][0]
                                               for series in data[metric['filename']]['data']['result']))

def transform_value(metric_name, value):
    'Transform heap usage from bytes into MB'
    return int(value) >> 20 if metric_name == 'heap' else float(value)

def add_expected_line_to_plot(metric_name, x):
    'Only makes sense for single-component setups'
    #if metric_name == 'cpu':
    #    plt.plot([x[0], x[-1]], [1, 1], label='expected')
    if metric_name == 'heap':
        with open('config/components.yaml', 'r') as components:
            expected_memory_usage = yaml.safe_load(components)[0]['memoryUsage']
        plt.plot([x[0], x[-1]], [expected_memory_usage, expected_memory_usage], label='expected')

for i, metric in enumerate(metrics):
    plt.figure(i + 1)
    for i, series in enumerate(data[metric['filename']]['data']['result']):
        x = []
        y = []
        for timestamp, value in series['values']:
            x.append(timestamp - first_timestamp)
            y.append(transform_value(metric['filename'], value))
        plt.plot(x, y, label='observed')
    #add_expected_line_to_plot(metric['filename'], x)
    plt.xlabel('time')
    plt.ylabel(metric['name'])
    plt.legend()
    plt.savefig('{}/{}.png'.format(LOCAL_DIR_WITH_PLOTS, metric['filename']))
