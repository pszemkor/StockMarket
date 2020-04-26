import se.proto.Stockmarket
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random

//todo: in the future this class will be reading indexes' values from rest api
class EventGenerator {
    val availableIndexes = listOf("NASDAQ", "AEX", "DOWJONES", "SP500", "DAX", "MOEX",
            "ICEX", "WIG", "NIKKEI225", "SHANGHAI", "ASX50")
    private val subscriptions = CopyOnWriteArrayList<((Stockmarket.Response) -> Unit)>()

    fun addSubscription(callback: (Stockmarket.Response) -> Unit) {
        subscriptions.add(callback)
    }

    fun removeSubscription(callback: (Stockmarket.Response) -> Unit) {
        subscriptions.remove(callback)
    }

    fun generateEvents() {
        while (true) {
            availableIndexes
                    .forEach { index -> generateEvent(index) }
        }
    }

    private fun generateEvent(index: String) {
        val indexValue = Random.nextDouble(0.0, 100000.0)
        val response = buildResponse(index, indexValue)
        Thread.sleep(500)
        subscriptions.forEach { sub -> sub(response) }
    }

    private fun buildResponse(index: String, indexValue: Double): Stockmarket.Response {
        return Stockmarket.Response.newBuilder()
                .setIndex(index)
                .setState(resolveState(indexValue))
                .setValue(indexValue)
                .build()
    }

    private fun resolveState(indexValue: Double): Stockmarket.Response.State {
        return when {
            indexValue < 50000 -> {
                Stockmarket.Response.State.BEAR
            }
            else -> {
                Stockmarket.Response.State.BULL
            }
        }
    }
}