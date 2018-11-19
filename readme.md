1. Have protobuf-java-3.6.1.jar in main directory.
2. Download tcp_packet.proto and player.proto.
3. Compile player.proto using: $ protoc -I=. --java_out=. ./player.proto 	#will result in new 'proto' folder
4. Move player.proto in proto folder.
5. Compile tcp_packet.proto using: $ protoc -I=. --java_out=. ./tcp_packet.proto
5. Move tcp_packet.proto in proto folder.
6. Extract protobuf-java-3.6.1.jar using: $ jar -xvf protobuf-java-3.6.1.jar.
-----Above steps are already done-----
7. Compile Proj_Protobuf.java using: $ javac *.java
8. Run program using: $ java Proj_Protobuf 202.92.144.45 80