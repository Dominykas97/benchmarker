all: package bench control prom up

clean:
	oc delete sa,clusterrolebinding,route,svc,secret,deployment,configmap -l app=prometheus -n myproject --as=system:admin
	oc delete dc,is,svc,deployment.apps,configmaps,po --all

package:
	mvn clean package

bench:
	docker build -f docker/Dockerfile.bench -t dilkas/benchmarker-flink --no-cache ./
	docker push dilkas/benchmarker-flink

control:
	docker build -f docker/Dockerfile.control -t dilkas/benchmarker-control --no-cache ./
	docker push dilkas/benchmarker-control

prom:
	-minishift addon apply prometheus --addon-env namespace=myproject

up:
	cd docker && kompose up --provider=openshift
