package com.roy.binaryeye.text

// This code is based on Google's own implementation:
// http://www.java2s.com/example/android-utility-method/string-escape/javascriptunescape-string-s-dc359.html
fun String.unescape(): String {
    val len = length
    var i = 0
    fun String.expand() = when (val ec = get(i++)) {
        'n' -> '\n'
        'r' -> '\r'
        't' -> '\t'
        'b' -> '\b'
        '\\', '\"', '\'', '<', '>', '|' -> ec
        '0', '1', '2', '3', '4', '5', '6', '7' -> {
            val limit = if (ec < '4') 3 else 2
            var l = 1
            --i // Back to index of first digit.
            while (l < limit && i + l < len && get(i + l) in '0'..'7') {
                ++l
            }
            val from = i
            i += l
            substring(from, i).toInt(8).toChar()
        }

        'x', 'u' -> {
            val l = if (ec == 'u') 4 else 2
            val hexCode = try {
                substring(i, i + l)
            } catch (_: IndexOutOfBoundsException) {
                throw IllegalArgumentException(
                    "Invalid unicode sequence [${substring(i)}] at index $i"
                )
            }
            val unicodeValue = try {
                hexCode.toInt(16)
            } catch (_: NumberFormatException) {
                throw IllegalArgumentException(
                    "Invalid unicode sequence [$hexCode] at index $i"
                )
            }
            i += l
            unicodeValue.toChar()
        }

        else -> throw IllegalArgumentException(
            "Unknown escape code [$ec] at index $i"
        )
    }

    val sb = StringBuilder()
    while (i < len) {
        val ch = get(i++)
        sb.append(if (ch == '\\' && i < len) expand() else ch)
    }
    return sb.toString()
}
