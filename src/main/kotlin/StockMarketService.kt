import io.grpc.stub.StreamObserver
import se.proto.IndicesServiceGrpc
import se.proto.Messages

class StockMarketService : IndicesServiceGrpc.IndicesServiceImplBase() {
    override fun getStockMarketIndices(request: Messages.SubscribeRequest?, responseObserver: StreamObserver<Messages.Response>?) {
        println(request)
        while (true) {
            val indexValue = Messages.Response.newBuilder()
                    .setValue(23451.2)
                    .setIndex(Messages.Index.AEX)
                    .build()
            responseObserver?.onNext(indexValue)
        }
    }
}