import subprocess
import time
import yaml

# NOTE: this way to run and capture an experiment is specific to MiniShift

PROJECT_NAME = 'benchmarking-internship' # As defined in the MiniShift/OpenShift system
HOSTFOLDER_NAME = 'hostfolder'
PERSISTENT_VOLUME_DIR_NAME = 'benchmarker_data'
COMPONENTS_FILE = 'config/components.yaml'
BASE_MEMORY_CONSUMPTION = 40 << 20
BYTES_PER_CHAR = 3.26845703125 # Keep this constant the same as in the Component class

# Filename_suffix is used to capture the parameters of the experiment (e.g., memory usage, etc.)
def run_experiment(filename_suffix = ''):
    # Remove data from previous runs and recreate relevant manifestos
    subprocess.run(['minishift', 'ssh', 'rm', PERSISTENT_VOLUME_DIR_NAME + '/*'])
    subprocess.run(['make', 'clean'])
    subprocess.run(['make', 'up'])

    # Wait until the start pod finishes
    # NOTE: if the pod fails, this will run forever
    while True:
        status = subprocess.run(['oc', '-n', PROJECT_NAME, 'get', 'po', 'start'],
                                stdout=subprocess.PIPE).stdout.decode('utf-8').split()[7]
        if status == 'Completed':
            break

    # Move files from the persistent volume to the host folder using MiniShift SSH
    time.sleep(1)
    files = subprocess.run(['minishift', 'ssh', 'ls', PERSISTENT_VOLUME_DIR_NAME],
                           stdout=subprocess.PIPE).stdout.decode('utf-8').split()
    for f in files:
        parts_of_f = f.split('_')
        new_name = parts_of_f[0] + filename_suffix + '_' + parts_of_f[1]
        command = 'minishift ssh "touch {}/{}; echo \`cat {}/{}\` > {}/{}"'.format(
            HOSTFOLDER_NAME, new_name, PERSISTENT_VOLUME_DIR_NAME, f, HOSTFOLDER_NAME, new_name)
        subprocess.Popen(command, shell=True)

# Remove local data from previous runs
subprocess.Popen('rm ../' + PERSISTENT_VOLUME_DIR_NAME + '/*', shell=True)
run_experiment()

"""for memory in map(lambda x: 2**x, range(10)):
    print('==========MEMORY =', memory, '==========')
    output = 1
    while output <= int(((memory << 20) - BASE_MEMORY_CONSUMPTION) * BYTES_PER_CHAR / (BYTES_PER_CHAR + 1)) >> 20:
        config = [{'cpuTime': 0, 'memoryUsage': memory, 'outputSize': output << 10}]
        with open(COMPONENTS_FILE, 'w') as f:
            yaml.dump(config, f)
        for repetition in range(3):
            run_experiment('_{}_{}_{}'.format(memory, output, repetition))
        output *= 2
"""
