#### Project consist of proto file with interfaces and messages definitions. After generating files with use of protoc.exe user can start server implemented in Kotlin and client implemented in Python 3.
#### Client shall pick stock market indexes with lower/upper boundary for its values. When indexes are chosen server will be sending notifications.

This command generates files:
protoc -I=. --java_out=src/main/gen --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java.exe --grpc-java_out=src/main/gen proto/stockmarket.proto
