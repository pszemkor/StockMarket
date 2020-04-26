import se.proto.Stockmarket
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import kotlin.random.Random

//todo: in the future this class will be reading indexes' values from rest api
class EventGenerator {
    private val subscriptions = CopyOnWriteArrayList<((Stockmarket.Response) -> Unit)>()

    fun addSubscription(callback: (Stockmarket.Response) -> Unit) {
        subscriptions.add(callback)
    }

    fun removeSubscription(callback: (Stockmarket.Response) -> Unit) {
        subscriptions.remove(callback)
    }

    fun startGenerating() {
        val executorService = Executors.newFixedThreadPool(1)
        executorService.submit(::generateEvents)
    }

    private fun generateEvents() {
        while (true) {
            Stockmarket.Index.values()
                    .filter { index -> index != Stockmarket.Index.UNRECOGNIZED }
                    .forEach { index -> generateEvent(index) }
        }
    }

    private fun generateEvent(index: Stockmarket.Index) {
        val indexValue = Random.nextDouble(0.0, 100000.0)
        val response = buildResponse(index, indexValue)
        println(response)
        subscriptions.forEach { sub ->
            sub(response!!)
            sub(response)
        }
        Thread.sleep(500)
    }

    private fun buildResponse(index: Stockmarket.Index, indexValue: Double): Stockmarket.Response? {
        return Stockmarket.Response.newBuilder()
                .setIndex(index)
                .setValue(indexValue)
                .build()
    }
}