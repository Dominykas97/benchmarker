PROJECT_NAME := benchmarking-internship

build: package bench start control prom
package:
	mvn clean package
bench:
	docker build -f docker/Dockerfile.bench -t dilkas/benchmarker-flink --no-cache ./
	docker push dilkas/benchmarker-flink
start:
	docker build -f docker/Dockerfile.start -t dilkas/benchmarker-start --no-cache ./
	docker push dilkas/benchmarker-start
control:
	docker build -f docker/Dockerfile.control -t dilkas/benchmarker-control --no-cache ./
	docker push dilkas/benchmarker-control
prom:
	docker build -f docker/Dockerfile.prom -t dilkas/benchmarker-prom --no-cache ./
	docker push dilkas/benchmarker-prom

prom-mini-up:
	minishift addon apply prometheus --addon-env namespace=$(PROJECT_NAME)
prom-mini-clean:
	oc delete sa,clusterrolebinding,route,svc,secret,deployment,configmap -l app=prometheus -n $(PROJECT_NAME) --as=system:admin

clean-prom:
	-oc -n $(PROJECT_NAME) delete configmaps prom-config
#	-oc -n $(PROJECT_NAME) delete -f docker/openshift/prometheus-pvc.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/prometheus-deploymentconfig.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/prometheus-imagestream.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/prometheus-service.yaml
up-prom:
	oc -n $(PROJECT_NAME) create configmap prom-config --from-file=config/prometheus.yml
	oc -n $(PROJECT_NAME) create -f docker/openshift/prometheus-pvc.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/prometheus-deploymentconfig.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/prometheus-imagestream.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/prometheus-service.yaml

clean-all:
#	-oc -n $(PROJECT_NAME) delete -f docker/openshift/control-pv.yaml # NOTE: for MiniShift only
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/control-pvc.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/control-service.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/taskmanager-service.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/jobmanager-service.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/control-pod.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/jobmanager-pod.yaml

up-all:
	oc -n $(PROJECT_NAME) create -f docker/openshift/control-pv.yaml # NOTE: for MiniShift only
	oc -n $(PROJECT_NAME) create -f docker/openshift/control-pvc.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/control-pod.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/jobmanager-pod.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/control-service.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/taskmanager-service.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/jobmanager-service.yaml

clean:
	-oc -n $(PROJECT_NAME) delete configmaps benchmarker-configs
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/taskmanager-pod.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/start-pod.yaml

up:
	oc -n $(PROJECT_NAME) create configmap benchmarker-configs --from-file=config/components.yaml --from-file=config/global.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/taskmanager-pod.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/start-pod.yaml
