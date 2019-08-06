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
* `docker`
  * `control-entrypoint.sh`
  * `docker-compose.yml`
  * `Dockerfile.bench`
  * `Dockerfile.control`
  * `openshift`
* `experiment.py`
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
* `Makefile`
* `plot_cpu_experiments.R`
* `plot_experiment.py`
* `plot_function.py`
* `plot_memory_experiments.R`
* `plots`
* `plottting.R`
* `prometheus`
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
* `report`
  * `talk`
* `src/main/java`
  * `Benchmarker.java`
  * `Component.java`
  * `Config.java`
  * `ControlServer.java`
  * `FunctionalWorkload.java`
  * `IOMode.java`
  * `Metric.java`
  * `PeriodicWorkload.java`
  * `WorkloadDeserializer.java`
  * `Workload.java`
* `validator.R`
