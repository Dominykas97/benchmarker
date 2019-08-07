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
* `io_memory_tests`
  * `analysis1.R`
  * `analysis2.R`
  * `Component.java`
  * `experiment1.py`
  * `experiment2.py`
  * `FullComponent.java`
  * `plots`
  * `results1.csv`
  * `results2.csv`
* `io_runtime_tests`
  * `analysis.R`
  * `Component.java`
  * `experiment.py`
  * `linkedlist_analysis.R`
  * `linkedlist.csv`
  * `RandomList.java`
  * `results.csv`
  * `results_ratio.csv`
* `Makefile`: the only currently used command is `make build`, which compiles all Java code into a JAR, builds Docker images, and uploads them to Docker Hub
* `plot_cpu_experiments.R`
* `plot_experiment.py`: after performing an experiment with `experiment.py`, this is an easy way to visualise the results
* `plot_function.py`
* `plot_memory_experiments.R`
* `plots`
* `plottting.R`
* `prometheus`: the Prometheus MiniShift addon from [here](https://github.com/minishift/minishift-addons/tree/master/add-ons/prometheus) with minor modifications
* `proof_of_concept`
  * `*.png`
  * `analysis.R`
  * `analysis2.R`
  * `Component.java`
  * `experiment.py`
  * `experiment2.py`
  * `FullComponent.java`
  * `results.csv`
  * `results2.csv`
* `report`: you might want to read it
  * `talk`: slides for a talk midway through the project
* `src/main/java`: all Java classes meant for the actual application (rather than separate experiments)
* `validator.R`
