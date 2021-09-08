package com.welyab.ankobachen

fun String.normalizeLineBreaks() =
    replace("""\r\n|\r""".toRegex(), "\n")
