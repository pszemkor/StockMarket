##### Requirements: proto compiler
##### Generating files from proto: 
##### > protoc -I=. --java_out=src/main/gen --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java.exe --grpc-java_out=src/main/gen proto/stockmarket.proto
##### > python -m grpc_tools.protoc -I=./proto --python_out=gen --grpc_python_out=gen proto/stockmarket.proto

##### Events are being generated randomly

![image](https://user-images.githubusercontent.com/37248877/80375050-ed3f2880-8897-11ea-9a7d-2c58e04b4020.png)
