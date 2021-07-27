package com.welyab.ankobachen

class CircularList<T>(
    val capacity: Int
) : MutableList<T> {

    init {
        if (capacity < 1) {
            throw IllegalArgumentException("capacity should be greater than 0")
        }
    }

    private val elements = arrayOfNulls<Any>(capacity)
    private var tailIndex = 0
    private var _size = 0

    override val size: Int
        get() = _size

    override fun contains(element: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(index: Int): T {
        TODO("Not yet implemented")
    }

    override fun indexOf(element: T): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun iterator(): MutableIterator<T> {
        TODO("Not yet implemented")
    }

    override fun lastIndexOf(element: T): Int {
        TODO("Not yet implemented")
    }

    override fun add(element: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun add(index: Int, element: T) {
        checkBounds(index, index)
        val nIndex =
            TODO("Not yet implemented")
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun addAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun listIterator(): MutableListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun remove(element: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAt(index: Int): T {
        TODO("Not yet implemented")
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, element: T): T {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        TODO("Not yet implemented")
    }

    private fun checkBounds(index: Int, indexToCheck: Int) {
        if (indexToCheck !in 0 until _size) throw IndexOutOfBoundsException("$index")
    }
}
