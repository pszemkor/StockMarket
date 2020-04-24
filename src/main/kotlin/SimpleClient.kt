import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import se.proto.IndicesServiceGrpc
import se.proto.Stockmarket

class SimpleClient
(host: String, port: Int) {
    private val channel: ManagedChannel
    private val blockingStub: IndicesServiceGrpc.IndicesServiceBlockingStub


    init {
        channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
                .usePlaintext(/*true*/)
//                .keepAliveTime(10, TimeUnit.SECONDS)
                .build()
        blockingStub = IndicesServiceGrpc.newBlockingStub(channel)
    }


    fun handleUserInput() {
        while (true) {
            try {
                val stockMarketIndices = blockingStub.getStockMarketIndices(Stockmarket.SubscribeRequest.newBuilder()
                        .addAllIndexes(listOf(Stockmarket.Index.AEX))
                        .build())
                stockMarketIndices.forEachRemaining { r: Stockmarket.Response -> print(r.value.toString() + " " + r.index) }
            } catch (e: Exception) {
                println(e.message)
                Thread.sleep(1000)
            }
        }
    }

}


fun main(args: Array<String>) {
    val client = SimpleClient("localhost", 50052)
    print("subscribing...")
    client.handleUserInput()
}