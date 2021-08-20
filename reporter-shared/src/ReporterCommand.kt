package dev.xoa.reporter

sealed class ReporterCommand {

    data class Log(
        val mode: LogMode,
        val message: Sentence
    ) : ReporterCommand()

    data class Meter(
        val mode: MeterMode,
        val code: Sentence,
        val tags: Map<String, Token> = mapOf(),
        val amount: Token = Token.Number(1.0)
    ) : ReporterCommand()

    companion object {

        fun parse(methodName: String, arguments: List<String>) : ReporterCommand {
            val sentence = methodName.toSentence(arguments)

            return when (val word = sentence.firstWord.lowercase()) {
                "increment" -> parseMeter(MeterMode.Counter, sentence)
                "register" -> parseMeter(MeterMode.Gauge, sentence)
                "debug" -> Log(LogMode.Debug, sentence.withOutFirstWorld())
                "info" -> Log(LogMode.Info, sentence.withOutFirstWorld())
                "warn" -> Log(LogMode.Warn, sentence.withOutFirstWorld())
                "error" -> Log(LogMode.Error, sentence.withOutFirstWorld())
                else -> throw IllegalArgumentException("Unknown word $word")
            }
        }

        private fun parseMeter(mode: MeterMode, sentence: Sentence) : Meter {
            val splits = sentence.splitBy("with", "by", "and")
            val values = mutableMapOf<String, Token>()
            var counterAmount : Token? = null

            splits.drop(1).forEach {
                when(it.firstWord.lowercase()) {
                    "with" -> values[it.tokens[1].asText().lowercase()] = it.tokens[2]
                    "by" -> counterAmount = it.tokens[1]
                    "and" -> values[it.tokens[1].asText().lowercase()] = it.tokens[2]
                }
            }

            val amount = when (mode) {
                MeterMode.Counter -> counterAmount ?: Token.Number(1.0)
                MeterMode.Gauge -> {
                    if ("gauge" !in values.keys) {
                        throw IllegalArgumentException("Sentence must end with 'withGauge_'")
                    }

                    if (counterAmount != null) {
                        throw IllegalArgumentException("by only can be used with counter")
                    }

                    values.remove("gauge")!!
                }
            }

            return Meter(mode,
                code = splits.first().withOutFirstWorld(),
                tags = values,
                amount = amount
            )
        }
    }
}