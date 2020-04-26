##### Requirements: proto compiler
##### Generating grpc files: 
#####> protoc -I=. --java_out=src/main/gen --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java.exe --grpc-java_out=src/main/gen proto/stockmarket.proto
#####> python -m grpc_tools.protoc -I=./proto --python_out=gen --grpc_python_out=gen proto/stockmarket.proto
