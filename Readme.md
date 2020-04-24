Kotlin client is just class for debugging, 
Python client is interactive

This command generates files:
protoc -I=. --java_out=src/main/gen --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java.exe --grpc-java_out=src/main/gen proto/stockmarket.proto