# Benchmarker
A tool to efficiently test cloud resource configurations for distributed applications

## Directories and Files
* `config`
  * `components.yaml`
  * `flink-conf.yaml`
  * `global_periodic.yaml`
  * `ml_components.yaml`
  * `prometheus.yml`
* `data`: each file records a single experiment. Each filename has four parts:
  1. what performance metric was measured (`cpu`, `heap`, or `throughput`)
  2. the expected amount of memory (64, 128, 256, or 512 MiB)
  3. the size of the output string (1, 2, 4, ..., 256 MiB)
  4. run ID (in case there are multiple runs with the same parameters)
* `docker`: all files needed to set up Docker Compose and/or OpenShift
  * `control-entrypoint.sh`: the entrypoint script for the control server, running both the server and the Flink app
  * `docker-compose.yml`: a Docker Compose file that was used as a basis for the OpenShift setup
  * `Dockerfile.bench`: a Dockerfile for the Flink JobManagers and TaskManagers
  * `Dockerfile.control`: a Dockerfile for the control server
  * `openshift`: OpenShift manifestos generated using Kompose (with some modifications and additions)
* `experiment.py`: the primary way to run a MiniShift experiment and get Prometheus data to a local directory
* `local_experiments`: experiments with standalone Java applications similar to the main Component class
  * `io_memory_tests`: does the I/O system use the right amount of memory? Barely.
  * `io_runtime_tests`: does the I/O system take the right amount of time? Usually.
  * `memory_tests`: does the main Component class use the right amount of memory? Yes!
  * `analysis*.R`: data analysis and plots using R
  * `Component.java`: versions of the original Component class adapted to be run without Flink
  * `experiment*.py`: Python scripts that run Component with various parameters and capture its performance in a CSV file
  * `FullComponent.java`: similar to Component.java, but used to check the performance after adjusting the constants
  * `plots` and `*.png`: plots specific to that set of experiments
  * `results*.csv`: column names can be found in the analysis files
  * `linkedlist_analysis.R`
  * `linkedlist.csv`
  * `RandomList.java`
* `Makefile`: the only currently used command is `make build`, which compiles all Java code into a JAR, builds Docker images, and uploads them to Docker Hub
* `plot_cpu_experiments.R`
* `plot_experiment.py`: after performing an experiment with `experiment.py`, this is an easy way to visualise the results
* `plot_function.py`
* `plot_memory_experiments.R`
* `plots`
* `plottting.R`
* `prometheus`: the Prometheus MiniShift addon from [here](https://github.com/minishift/minishift-addons/tree/master/add-ons/prometheus) with minor modifications
* `report`: you might want to read it
  * `talk`: slides for a talk midway through the project
* `src/main/java`: all Java classes meant for the actual application (rather than separate experiments)
* `validator.R`
