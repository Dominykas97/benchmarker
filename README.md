To compile:
```sh
mvn clean package
```

To build:
```sh
docker build -f docker/Dockerfile.control -t control --no-cache ./
docker build -f docker/Dockerfile.app -t benchmarker --no-cache ./
```

To run:
```sh
cd docker && docker-compose up
```
