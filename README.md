# ChatApp-JETS

commands : 

To convert to exe

```
mvn -pl Client clean package
mvn -pl Server clean package
```

to run 

```
mvn -pl Client clean javafx:run
mvn -pl Server clean javafx:run
```

to run Docker

```
docker pull klash7/towk
docker run -d -p YourDesiredPort:3306 --name mysql-chat towk
```