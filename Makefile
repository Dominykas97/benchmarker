all: package bench control up

clean:
	oc delete dc,is,svc --all

package:
	mvn clean package

bench:
	docker build -f docker/Dockerfile.bench -t dilkas/benchmarker-flink --no-cache ./
	docker push dilkas/benchmarker-flink

control:
	docker build -f docker/Dockerfile.control -t dilkas/benchmarker-control --no-cache ./
	docker push dilkas/benchmarker-control

up:
	cd docker && kompose up --provider=openshift
