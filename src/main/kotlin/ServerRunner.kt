
fun main() {
    val server = StockServer(50052)
    server.init()
    server.blockUntilShutdown()
}