# ChatApp-JETS

commands : 

To convert to exe

install https://github.com/wixtoolset/wix3/releases/tag/wix3141rtm and add install-location/bin to environment path

```
mvn clean package
```

for only one child

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