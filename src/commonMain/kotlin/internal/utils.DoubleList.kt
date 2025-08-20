package dev.adamko.polybool.internal

import kotlin.jvm.JvmInline

/**
 * Read-only [DoubleArray].
 */
@JvmInline
value class DoubleList internal constructor(
  private val content: DoubleArray
): Collection<Double> {
  override val size: Int
    get() = content.size

  operator fun get(index: Int): Double = content[index]

  override fun isEmpty(): Boolean = content.isEmpty()

  override operator fun iterator(): Iterator<Double> = content.iterator()

  fun listIterator(): ListIterator<Double> =
    listIterator(0)

  fun listIterator(index: Int): ListIterator<Double> =
    DoubleListIterator(
      index = index,
      content = content,
    )

  fun subList(fromIndex: Int, toIndex: Int): DoubleList {
    return DoubleList(content.copyOfRange(fromIndex, toIndex))
  }

  fun lastIndexOf(element: Double): Int {
    return content.indexOfLast { it == element }
  }

  fun indexOf(element: Double): Int {
    return content.indexOfFirst { it == element }
  }

  override fun containsAll(elements: Collection<Double>): Boolean {
    return elements.all { e ->
      content.any { it == e }
    }
  }

  override fun contains(element: Double): Boolean =
    content.any { it == element }

  fun sorted(): DoubleList =
    DoubleList(content.sorted())

  fun <R, T : MutableCollection<in R>> mapTo(
    destination: T,
    transform: (Double) -> R,
  ): T {
    for (e in content) {
      destination.add(transform(e))
    }
    return destination
  }
}

/**
 * Create a new [DoubleList].
 */
internal fun DoubleList(
  elements: Collection<Double>
): DoubleList =
  DoubleList(elements.toDoubleArray())

/**
 * Create a new [DoubleList].
 */
internal fun DoubleList(
  vararg elements: Double
): DoubleList =
  DoubleList(elements)


private class DoubleListIterator(
  private val content: DoubleArray,
  private var index: Int,
) : ListIterator<Double> {

  override fun hasNext(): Boolean {
    return index < content.size - 1
  }

  override fun hasPrevious(): Boolean = index > 0

  override fun next(): Double = content[index++]

  override fun nextIndex(): Int = index + 1

  override fun previous(): Double = content[--index]

  override fun previousIndex(): Int = index - 1
}
