import time
import json
import matplotlib.pyplot as plt
import subprocess
import yaml

HOSTFOLDER_NAME = 'hostfolder'
PERSISTENT_VOLUME_DIR_NAME = 'benchmarker_data'
LOCAL_DIR_WITH_DATA = '../benchmarker_data'
LOCAL_DIR_WITH_PLOTS = 'plots'

# Read in a list of metrics
with open('config/global.yaml', 'r') as config:
    metrics = yaml.safe_load(config)['metrics']

#subprocess.Popen('minishift ssh "rm benchmarker_data/*; rm hostfolder/*"', shell=True)
#subprocess.run(['make', 'clean'])
#subprocess.run(['make', 'up'])

# Wait until the control server finishes
# NOTE: if the pod fails, this will run forever
#while True:
#    status = subprocess.run(['oc', 'get', 'po', 'control'], stdout=subprocess.PIPE).stdout.decode('utf-8').split()[7]
#    if status == 'Completed':
#        break

# Move files from the persistent volume to the host folder using MiniShift SSH
#for metric in metrics:
#    command = 'minishift ssh "touch {}/{}.json; echo \`cat {}/{}.json\` > {}/{}.json"'.format(
#        HOSTFOLDER_NAME, metric['filename'], PERSISTENT_VOLUME_DIR_NAME, metric['filename'], HOSTFOLDER_NAME,
#        metric['filename'])
#    print(command)
#    subprocess.Popen(command, shell=True)
#time.sleep(2) # Wait a bit, making sure that the files have time to move from the VM to the local machine

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
    if metric_name == 'cpu':
        plt.plot([x[0], x[-1]], [1, 1], label='expected')
    elif metric_name == 'heap':
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
    add_expected_line_to_plot(metric['filename'], x)
    plt.title(metric['name'])
    plt.xlabel('time')
    plt.legend()
    plt.savefig('{}/{}.png'.format(LOCAL_DIR_WITH_PLOTS, metric['filename']))

# TODO: Compare them with estimated numbers (if available)
