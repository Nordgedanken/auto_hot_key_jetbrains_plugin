package de.nordgedanken.auto_hotkey.psi

import com.intellij.openapi.util.TextRange

/**
 * Stores offsets of distinguishable parts of a literal.
 */
data class LiteralOffsets(
    val prefix: TextRange? = null,
    val openDelim: TextRange? = null,
    val value: TextRange? = null,
    val closeDelim: TextRange? = null,
    val suffix: TextRange? = null
) {
    companion object {
        fun fromEndOffsets(
            prefixEnd: Int,
            openDelimEnd: Int,
            valueEnd: Int,
            closeDelimEnd: Int,
            suffixEnd: Int
        ): LiteralOffsets {
            val prefix = makeRange(0, prefixEnd)
            val openDelim = makeRange(prefixEnd, openDelimEnd)

            // empty value is still a value provided we have open delimiter
            val fallbackValue = if (openDelim != null) TextRange.create(openDelimEnd, openDelimEnd) else null

            val value = makeRange(openDelimEnd, valueEnd) ?: fallbackValue

            val closeDelim = makeRange(valueEnd, closeDelimEnd)
            val suffix = makeRange(closeDelimEnd, suffixEnd)

            return LiteralOffsets(
                    prefix = prefix, openDelim = openDelim, value = value,
                    closeDelim = closeDelim, suffix = suffix
            )
        }

        private fun makeRange(start: Int, end: Int): TextRange? = when {
            end - start > 0 -> TextRange(start, end)
            else -> null
        }
    }
}
