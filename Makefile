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

clean:
	-oc delete configmaps benchmarker-configs
	-oc delete -f docker/openshift/taskmanager-service.yaml
	-oc delete -f docker/openshift/taskmanager-pod.yaml
	-oc delete -f docker/openshift/control-service.yaml
	-oc delete -f docker/openshift/control-pod.yaml
	-oc delete -f docker/openshift/jobmanager-service.yaml
	-oc delete -f docker/openshift/jobmanager-pod.yaml
	-oc delete -f docker/openshift/control-pvc.yaml
	-oc delete -f docker/openshift/control-pv.yaml

prom:
	minishift addon apply prometheus --addon-env namespace=myproject

up:
	oc create configmap benchmarker-configs --from-file=config/components.yaml --from-file=config/global.yaml
	oc create -f docker/openshift/control-pv.yaml
	oc create -f docker/openshift/control-pvc.yaml
	oc create -f docker/openshift/jobmanager-pod.yaml
	oc create -f docker/openshift/jobmanager-service.yaml
	oc create -f docker/openshift/control-pod.yaml
	oc create -f docker/openshift/control-service.yaml
	oc create -f docker/openshift/taskmanager-pod.yaml
	oc create -f docker/openshift/taskmanager-service.yaml
