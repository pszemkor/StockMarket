import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import se.proto.IndicesServiceGrpc
import se.proto.Messages

class SimpleClient
(host: String, port: Int) {
    private val channel: ManagedChannel
    private val blockingStub: IndicesServiceGrpc.IndicesServiceBlockingStub


    init {
        channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
                .usePlaintext(/*true*/)
                .build()
        blockingStub = IndicesServiceGrpc.newBlockingStub(channel)
    }


    fun handleUserInput() {

        val stockMarketIndices = blockingStub.getStockMarketIndices(Messages.SubscribeRequest.newBuilder()
                .addAllIndexes(listOf(Messages.Index.AEX))
                .build())
        stockMarketIndices.forEachRemaining { r: Messages.Response -> print(r.value) }
    }

}


fun main(args: Array<String>) {
    val client = SimpleClient("localhost", 50051)
    client.handleUserInput()
}