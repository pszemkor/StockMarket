import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver
import se.proto.IndicesServiceGrpc
import se.proto.Stockmarket

class StockMarketService(private val eventGenerator: EventGenerator) : IndicesServiceGrpc.IndicesServiceImplBase() {

    override fun getStockMarketIndexes(request: Stockmarket.SubscribeRequest?, responseObserver: StreamObserver<Stockmarket.Response>?) {
        val callback = { response: Stockmarket.Response ->
            if (isEventObserved(response, request!!)) {
                println(response)
                responseObserver?.onNext(response)
            }
        }
        setOnCancelHandler(responseObserver, callback)
        eventGenerator.addSubscription(callback)
    }

    override fun getAvailableIndexes(request: Stockmarket.IndexesRequest?, responseObserver: StreamObserver<Stockmarket.AvailableIndexes>?) {
        responseObserver?.onNext(Stockmarket.AvailableIndexes.newBuilder().addAllIndexes(eventGenerator.availableIndexes).build())
        responseObserver?.onCompleted()
    }

    private fun setOnCancelHandler(responseObserver: StreamObserver<Stockmarket.Response>?, callback: (Stockmarket.Response) -> Unit) {
        if (responseObserver is ServerCallStreamObserver<Stockmarket.Response>) {
            responseObserver.setOnCancelHandler(Runnable { eventGenerator.removeSubscription(callback) })
        }
    }

    private fun isEventObserved(response: Stockmarket.Response, request: Stockmarket.SubscribeRequest): Boolean {
        request.indexesList.forEach { pattern ->
            if (pattern.index.equals(response.index) && isInRange(pattern, response)) {
                return true
            }
        }
        return false
    }


    private fun isInRange(request: Stockmarket.SingleIndexPattern, response: Stockmarket.Response) =
            (request.lowerBound <= response.value || request.lowerBound < 0) &&
                    (request.upperBound >= response.value || request.upperBound < 0)

}
