# CMSC 137 PROJECT: Picture This
###### A requirement for CMSC137 AB-3L that simulates the game Draw My Thing.
1. Compile all java files using: $ javac *.java
2. Run Client using: $ java Main 202.92.144.45 80

###### If protobuf is not yet extracted:
1. Have protobuf-java-3.6.1.jar in main directory.
2. Download tcp_packet.proto and player.proto.
3. Compile player.proto using: 
```
$ protoc -I=. --java_out=. ./player.proto 	#will result in new 'proto' folder
```
4. Move player.proto in proto folder.
5. Compile tcp_packet.proto using: 
```
$ protoc -I=. --java_out=. ./tcp_packet.proto
```
5. Move tcp_packet.proto in proto folder.
6. Extract protobuf-java-3.6.1.jar using: 
```
$ jar -xvf protobuf-java-3.6.1.jar
```