all:
	mvn clean package
	cd docker && docker-compose build --no-cache && docker-compose up
