package dev.xoa.reporter

import dev.xoa.reporter.LogMode.Debug
import dev.xoa.reporter.LogMode.Warn
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ReporterCommandTest {

    @Test
    fun testWarnWithOneArg() {
        assertEquals(
            ReporterCommand.Log(Warn, "Label_MustStartWithUppercase".toSentence(listOf("label"))),
            ReporterCommand.parse("warnLabel_MustStartWithUppercase", listOf("label"))
        )
    }

    @Test
    fun testDebugWithTwoArgsAtBeginAndEnd() {
        assertEquals(
            ReporterCommand.Log(Debug, "_StockUpdatedTo_".toSentence(listOf("product", "newStock"))),
            ReporterCommand.parse("debug_StockUpdatedTo_", listOf("product", "newStock"))
        )
    }

    @Test
    fun testIncrementSimple() {
        assertEquals(
            ReporterCommand.Meter(MeterMode.Counter,"MessageReceived".toSentence(listOf())),
            ReporterCommand.parse("incrementMessageReceived", listOf())
        )
    }

    @Test
    fun testIncrementWithOneArg() {
        assertEquals(
            ReporterCommand.Meter(MeterMode.Counter,
                "Message_Received".toSentence(listOf("messageType")),
                amount = Token.Ref("amount")
            ),
            ReporterCommand.parse("incrementMessage_ReceivedBy_", listOf("messageType", "amount"))
        )
    }

    @Test
    fun testIncrementWithTags() {
        assertEquals(
            ReporterCommand.Meter(MeterMode.Counter,
                code = "MessageReceived".toSentence(listOf()),
                tags = mapOf(
                    "type" to Token.Ref("type"),
                    "host" to Token.Text( "Local")
                ),
                amount = Token.Number(1.0)
            ),
            ReporterCommand.parse("incrementMessageReceivedWithType_AndHostLocal", listOf("type"))
        )
    }

    @Test
    fun testGauge() {
        assertEquals(
            ReporterCommand.Meter(MeterMode.Gauge,
                "Batch_Size".toSentence(listOf("batchType")),
                amount = Token.Ref("gauge")
            ),
            ReporterCommand.parse("registerBatch_SizeWithGauge_", listOf("batchType", "gauge"))
        )
    }
}
