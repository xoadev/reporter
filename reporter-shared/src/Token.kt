package dev.xoa.reporter

sealed class Token {

    data class Text(val value: String) : Token() {

        override fun asText() = value
    }

    data class Number(val value: Double) : Token() {
        override fun asText() = value.toString()
    }

    data class Ref(val value: String) : Token() {

        override fun asText() = throw UnsupportedOperationException()
    }

    abstract fun asText() : String
}