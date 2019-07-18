import subprocess
import yaml

HOSTFOLDER_NAME = 'hostfolder'
PERSISTENT_VOLUME_DIR_NAME = 'benchmarker_data'
COMPONENTS_FILE = 'config/components.yaml'
BASE_MEMORY_CONSUMPTION = 40 << 20
BYTES_PER_CHAR = 3.26845703125

# Read in a list of metrics
with open('config/global.yaml', 'r') as config:
    metrics = yaml.safe_load(config)['metrics']

def run_experiment(filename_suffix = ''):
    subprocess.run(['make', 'clean'])
    subprocess.run(['make', 'up'])

    # Wait until the control server finishes
    # NOTE: if the pod fails, this will run forever
    while True:
        status = subprocess.run(['oc', 'get', 'po', 'control'],
                                stdout=subprocess.PIPE).stdout.decode('utf-8').split()[7]
        if status == 'Completed':
            break

    # Move files from the persistent volume to the host folder using MiniShift SSH
    for metric in metrics:
        new_name = metric['filename'] + filename_suffix
        command = 'minishift ssh "touch {}/{}.json; echo \`cat {}/{}.json\` > {}/{}.json"'.format(
            HOSTFOLDER_NAME, new_name, PERSISTENT_VOLUME_DIR_NAME, metric['filename'], HOSTFOLDER_NAME, new_name)
        subprocess.Popen(command, shell=True)

for memory in map(lambda x: 2**x, range(10)):
    print('==========MEMORY =', memory, '==========')
    output = 1
    while output <= int(((memory << 20) - BASE_MEMORY_CONSUMPTION) * BYTES_PER_CHAR / (BYTES_PER_CHAR + 1)) >> 20:
        config = [{'cpuTime': 0, 'memoryUsage': memory, 'outputSize': output << 10}]
        with open(COMPONENTS_FILE, 'w') as f:
            yaml.dump(config, f)
        for repetition in range(3):
            run_experiment('_{}_{}_{}'.format(memory, output, repetition))
        output *= 2
