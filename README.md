1. Move all files to Desktop. 
Run:
protoc --java_out=. player.proto
protoc --java_out=. tcp_packet.proto
//should create a new 'proto' folder

2. download protobuf-master in github. 
Go to: protobuf-master > java > core > src > main > java
//should see a 'com' folder. copy 'com' folder inside 'proto' folder.

3. go to Desktop. Run: javac *.java

4. java Proj_Protobuf