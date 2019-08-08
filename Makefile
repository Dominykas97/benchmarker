PROJECT_NAME := benchmarking-internship

build: package bench control

package:
	mvn clean package

bench:
	docker build -f docker/Dockerfile.bench -t dilkas/benchmarker-flink --no-cache ./
	docker push dilkas/benchmarker-flink

control:
	docker build -f docker/Dockerfile.control -t dilkas/benchmarker-control --no-cache ./
	docker push dilkas/benchmarker-control

promclean:
    oc delete sa,clusterrolebinding,route,svc,secret,deployment,configmap -l app=prometheus -n myproject --as=system:admin

prom:
	minishift addon apply prometheus --addon-env namespace=myproject

clean-outer:
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/control-service.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/taskmanager-service.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/jobmanager-service.yaml
#	-oc -n $(PROJECT_NAME) delete -f docker/openshift/control-pvc.yaml

clean:
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/control-pod.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/taskmanager-pod.yaml
	-oc -n $(PROJECT_NAME) delete -f docker/openshift/jobmanager-pod.yaml
	-oc -n $(PROJECT_NAME) delete configmaps benchmarker-configs

up-outer:
#	oc -n $(PROJECT_NAME) create -f docker/openshift/control-pvc.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/control-service.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/taskmanager-service.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/jobmanager-service.yaml

up:
	oc -n $(PROJECT_NAME) create configmap benchmarker-configs --from-file=config/components.yaml --from-file=config/global.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/control-pod.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/taskmanager-pod.yaml
	oc -n $(PROJECT_NAME) create -f docker/openshift/jobmanager-pod.yaml
