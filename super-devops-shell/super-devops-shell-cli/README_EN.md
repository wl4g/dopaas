Shell-cli is an open source command-line tool based on spring cloud service, which is similar to the way spark-shell works.

[中文说明/中文文档](README_CN.md)

### Quick start

#### Source code compilation
```
cd super-devops-shell
mvn clean install -DskipTests 
```

##### Way1(For client mode, usually temporarily used to connect application services):
Specify the port of the service, and then run directly as a client:

```
java -Dservport=10.0.0.160:60120 -jar shell-cli-master-executable.jar
```
	
In the above command - Dservpoint = 10.0.0.160:60120 indicates the Spring Cloud service 
listening address and port to connect to.

##### Way2(For local mode, usually used as a built-in console for application services):
Specify the PID list of the service, then run directly as the client, where shell-cli automatically 
scans the ports that match all the locally monitored ports of the PID process (default matching 
range 60100-60200)

```
java -Dservpids=19767,32374 -Ddebug -jar shell-cli-master-executable.jar 
```

#### Features
- Press TAB key to complete automatically
- Built-in command: clear/cls、exit/ex/quit/qu、history/his、stacktrace/st、help
	