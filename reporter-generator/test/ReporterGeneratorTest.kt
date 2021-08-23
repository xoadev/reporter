package dev.xoa.reporter

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.Test
import kotlin.test.assertEquals


class ReporterGeneratorTest {

    @Test
    fun test1() {
        val source = SourceFile.kotlin("TestReporter.kt", """
            |package dev.xoa.reporter
            |
            |@Reporter
            |interface TestReporter {
            |
            |    //fun incrementMessagesSentBy(value: Double)
            |
            |    //fun incrementOrdersWithError()
            |
            |    fun warnLabel_MustStartWithUppercase(label: String)
            |
            |    fun debug_StockUpdatedTo_AndIncrementStockUpdated(product: String, newStock: Int)
            |}
    """.trimMargin()
        )

        val result = KotlinCompilation().apply {
            sources = listOf(source)
            annotationProcessors = listOf(ReporterGenerator())
            inheritClassPath = true
            verbose = false
        }.compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
        println("Messages: ${result.messages}")

        println(result.generatedFiles.find { it.name == "TestReporterImpl.java" }!!.readText())
    }
}