package me.gabrieltk.mkscaffold.utils

inline fun <T> inlineMap(iterable: Iterable<T>, separator: String, crossinline out: (v: T) -> String)
        = iterable.joinToString(separator) { out(it) }

fun <T> forEachIndexed1(iterable: Iterable<T>, out: (i: Int, v: T) -> String): String {
    val sb = StringBuilder()
    iterable.forEachIndexed { i, it ->
        sb.append(out(i + 1, it))
    }
    return sb.toString()
}

enum class BKPerm(val value: String){
    TRUE("TRUE"),
    FALSE("FALSE"),
    OP("OP"),
    NOT_OP("NOT_OP")
}