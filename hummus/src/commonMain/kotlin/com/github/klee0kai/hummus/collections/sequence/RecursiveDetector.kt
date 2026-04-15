package com.github.klee0kai.hummus.collections.sequence

/**
 * Detects cycles in a sequence using Floyd's cycle detection algorithm (tortoise and hare).
 *
 * Efficiently detects if a sequence contains a cycle (repeating elements) without
 * storing the entire sequence. Uses the "tortoise and hare" algorithm where:
 * - **Hare** (fast pointer): moves 2 steps per iteration
 * - **Tortoise** (slow pointer): moves 1 step per iteration
 * - If there's a cycle, they will meet
 *
 * **Algorithm details:**
 * - Stores a list that alternates between holding items at positions 0, 2, 4, ... (hare)
 * - Tortoise is always at position 0 of this list
 * - If hare (current item) equals tortoise (first item), cycle detected
 * - Prevents full history from being stored by removing old tortoise position
 *
 * **Space complexity:**
 * - O(1) constant space: list never grows beyond 2 items
 * - Unlike naive cycle detection which stores all items O(N)
 *
 * **Time complexity:**
 * - O(N + K) where N is pre-cycle length, K is cycle size
 * - For sequence with cycle at position N, detects it by position N + K
 *
 * **Use cases:**
 * - Detecting infinite loops in data structures
 * - Validating linked list integrity
 * - Graph cycle detection
 * - Preventing infinite iterations
 *
 * **Example - Detecting cycle in traversal:**
 * ```kotlin
 * data class Node(val value: Int, var next: Node? = null)
 *
 * val n1 = Node(1)
 * val n2 = Node(2)
 * val n3 = Node(3)
 * n1.next = n2
 * n2.next = n3
 * n3.next = n2  // Creates cycle: 2 -> 3 -> 2 -> ...
 *
 * val detector = RecursiveDetector<Node>()
 * var current: Node? = n1
 * var hasCycle = false
 * while (current != null) {
 *     if (detector.next(current)) {
 *         hasCycle = true
 *         break
 *     }
 *     current = current.next
 * }
 * // hasCycle is true
 * ```
 *
 * **Famous algorithm:**
 * This is the "tortoise and hare" algorithm by Robert Floyd, commonly used for
 * cycle detection in linked lists (e.g., LeetCode problem 141).
 *
 * @param T the type of items in the sequence
 *
 * @see detectRecursive extension function for detecting cycles in [Sequence]
 */
class RecursiveDetector<T> {

    private val race = mutableListOf<T?>()
    private var hareStep: Long = 0

    /**
     * Processes the next item in the sequence and checks for a cycle.
     *
     * Should be called repeatedly with each successive item in the sequence.
     * Returns true as soon as a cycle is detected.
     *
     * **How it works:**
     * - Hare moves every step (odd steps), tortoise doesn't
     * - On even steps: advance hare, remove tortoise position from history
     * - On odd steps: compare hare with tortoise (first item)
     * - If they match: cycle detected
     *
     * **Example:**
     * ```kotlin
     * val detector = RecursiveDetector<Int>()
     * val items = listOf(1, 2, 3, 2, 3, 2, 3, ...)  // Repeats 2, 3
     *
     * var cycleFound = false
     * for (item in items) {
     *     if (detector.next(item)) {
     *         cycleFound = true
     *         break
     *     }
     * }
     * ```
     *
     * @param next the next item in the sequence
     * @return true if a cycle has been detected, false otherwise
     */
    fun next(next: T?): Boolean {
        race.add(next)
        if (hareStep++ % 2 == 0L) {
            race.removeAt(0)
        }
        if (race.size <= 2) return false

        val hare = next
        val turtle = race.first()
        return hare == turtle
    }

}


/**
 * Detects if a sequence contains repeating elements (cycle).
 *
 * Efficiently checks if the sequence produces repeating values, indicating a cycle.
 * Uses [RecursiveDetector] internally with Floyd's algorithm for O(1) space complexity.
 *
 * **Important limitation:**
 * This function checks for **repeating values**, not repeating subsequences.
 * A cycle means the same exact value appears again (equality check with ==).
 *
 * **What it detects:**
 * ```kotlin
 * val seq1 = sequenceOf(1, 2, 3, 2, 3, 2, 3, ...).take(7)
 * seq1.detectRecursive()  // true (2 repeats at position 3)
 *
 * val seq2 = sequenceOf(1, 2, 3, 4, 5, 6)
 * seq2.detectRecursive()  // false (no repeats)
 * ```
 *
 * **Use cases:**
 * - Validating that a sequence doesn't cycle
 * - Preventing infinite loops during iteration
 * - Cycle detection in data structures represented as sequences
 * - Verifying graph acyclicity
 *
 * **Performance:**
 * - Time: O(N + K) where N is pre-cycle position, K is cycle size
 * - Space: O(1) constant, thanks to Floyd's algorithm
 * - Much more efficient than storing all elements
 *
 * **Example:**
 * ```kotlin
 * // Checking a linked list for cycles
 * fun <T> hasLinkedListCycle(head: Node<T>?): Boolean {
 *     val sequence = generateSequence(head) { it.next }
 *     return sequence.detectRecursive()
 * }
 *
 * val list = Node(1) { Node(2) { Node(3) } }
 * list.next?.next?.next = list.next  // Create cycle
 * hasLinkedListCycle(list)  // true
 * ```
 *
 * **Note:**
 * Due to the nature of [Sequence], this function will iterate through the sequence
 * until a cycle is found or sequence ends. For infinite sequences, this will run forever
 * unless a cycle is found.
 *
 * @param T the type of elements in the sequence
 * @return true if the sequence contains repeating elements, false if no cycles
 *
 * @see RecursiveDetector for detailed algorithm explanation
 */
fun <T> Sequence<T>.detectRecursive(): Boolean {
    val detector = RecursiveDetector<T>()
    for (element in this) {
        if (detector.next(element)) {
            return true
        }
    }
    return false
}