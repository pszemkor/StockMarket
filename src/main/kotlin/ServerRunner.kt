fun main(args: Array<String>) {
    val server = StockServer(50052)
    server.init()
    server.blockUntilShutdown()
}

