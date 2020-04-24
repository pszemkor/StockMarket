import io.grpc.Server
import io.grpc.ServerBuilder
import java.util.logging.Logger

class StockServer(private val port: Int) {
    private val logger = Logger.getLogger(StockServer::class.java.name)

    private var server: Server? = null
    fun init() {
        val eventGenerator = EventGenerator()
        eventGenerator.startGenerating()
        server = ServerBuilder.forPort(port)
                .addService(StockMarketService(eventGenerator))
                .build()
                .start()

        logger.info("Server running on port $port")

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down")
                this@StockServer.stop()
                System.err.println("*** server shut down")
            }
        })
    }

    fun stop() {
        if (server != null) {
            server?.shutdown()
        }
    }

    @Throws(InterruptedException::class)
    fun blockUntilShutdown() {
        if (server != null) {
            server?.awaitTermination()
        }
    }
}