package dev.xoa.reporter

data class Sentence(
    val tokens: List<Token>
) {

    val firstWord: String
        get() = (tokens[0] as Token.Text).value

    val arguments: List<String>
        get() = tokens.filterIsInstance<Token.Ref>().map { it.value }

    fun withOutFirstWorld() = Sentence(tokens.subList(1, tokens.size))

    fun joinAsString(
        firstToken: Int = 0,
        lastTokensSkipped: Int = 0,
        separator: String,
        replaceUnderlineBy: String,
    ) : String {
        return tokens
            .subList(fromIndex = firstToken, toIndex = tokens.size - lastTokensSkipped)
            .joinToString(separator = separator) {
                when (it) {
                    is Token.Ref -> replaceUnderlineBy
                    is Token.Text -> it.value.lowercase()
                    is Token.Number -> it.value.toString()
                }
            }
    }

    fun splitBy(vararg splitTokens: String) : List<Sentence> {
        val splitTokensSet = splitTokens.toSet()
        val result = mutableListOf<Sentence>()
        var current = mutableListOf<Token>()

        for (token in tokens) {
            if (token is Token.Text && token.value.lowercase() in splitTokensSet) {
                result.add(Sentence(current))
                current = mutableListOf()
            }

            current.add(token)
        }

        result.add(Sentence(current))

        return result
    }
}

fun String.toSentence(arguments: List<String>): Sentence {
    if (isEmpty()) {
        throw IllegalArgumentException("String is empty")
    }

    val chars = toCharArray()
    val list = mutableListOf<Token>()
    var tokenStart = 0
    var currentType = Character.getType(chars[tokenStart])
    var argumentIndex = 0

    fun token(text: String) : Token {
        return when(text) {
            "_" -> {
                if (argumentIndex >= arguments.size) {
                    throw IllegalArgumentException("Invalid number of underscores, it must be ${arguments.size}")
                }

                Token.Ref(arguments[argumentIndex++])
            }
            else -> Token.Text(text)
        }
    }

    for (pos in tokenStart + 1 until chars.size) {
        val type = Character.getType(chars[pos])

        if (type == currentType) {
            continue
        }

        if (type == Character.LOWERCASE_LETTER.toInt() && currentType == Character.UPPERCASE_LETTER.toInt()) {
            val newTokenStart = pos - 1

            if (newTokenStart != tokenStart) {
                list.add(token(String(chars, tokenStart, newTokenStart - tokenStart)))
                tokenStart = newTokenStart
            }
        } else {
            list.add(token(String(chars, tokenStart, pos - tokenStart)))
            tokenStart = pos
        }

        currentType = type
    }

    list.add(token(String(chars, tokenStart, chars.size - tokenStart)))

    if (argumentIndex != arguments.size) {
        throw IllegalArgumentException("Invalid number of underscores, it must be ${arguments.size}")
    }

    return Sentence(list)
}