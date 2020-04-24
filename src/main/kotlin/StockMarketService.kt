import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver
import se.proto.IndicesServiceGrpc
import se.proto.Stockmarket

class StockMarketService(private val eventGenerator: EventGenerator) : IndicesServiceGrpc.IndicesServiceImplBase() {

    override fun getStockMarketIndices(request: Stockmarket.SubscribeRequest?, responseObserver: StreamObserver<Stockmarket.Response>?) {
        val callback = { response: Stockmarket.Response ->
            if (isObserved(response, request!!)) {
                responseObserver?.onNext(response)
            }
        }
        setOnCancelHandler(responseObserver, callback)
        eventGenerator.addSubscription(callback)
    }

    private fun setOnCancelHandler(responseObserver: StreamObserver<Stockmarket.Response>?, callback: (Stockmarket.Response) -> Unit) {
        if (responseObserver is ServerCallStreamObserver<Stockmarket.Response>) {
            responseObserver.setOnCancelHandler(Runnable { -> eventGenerator.removeSubscription(callback) })
        }
    }

    private fun isObserved(response: Stockmarket.Response, request: Stockmarket.SubscribeRequest): Boolean {
        return isInRange(request, response) && isIndexObserved(request, response)
    }

    private fun isIndexObserved(request: Stockmarket.SubscribeRequest, response: Stockmarket.Response) =
            request.indexesList.contains(response.index)

    private fun isInRange(request: Stockmarket.SubscribeRequest, response: Stockmarket.Response) =
            (request.lowerBound >= response.value || request.lowerBound < 0) &&
                    (request.upperBound <= response.value || request.upperBound < 0)

}
