package com.github.klee0kai.hummus.collections.sequence

/**
 * Traverses a tree structure in breadth-first order (level by level).
 *
 * Starts from this node and yields all nodes at each level before moving to the next level.
 * Uses a queue internally to maintain breadth-first order. All children of a node are
 * processed before any of their children.
 *
 * **Traversal order:**
 * Given a tree:
 * ```
 *       A
 *      / \
 *     B   C
 *    / \
 *   D   E
 * ```
 * BFS yields: A, B, C, D, E
 *
 * **Use cases:**
 * - Level-order tree traversal
 * - Finding nodes at a specific depth
 * - Computing tree levels
 * - Shortest path searches
 *
 * **Performance:**
 * - Time: O(N) where N is number of nodes
 * - Space: O(W) where W is maximum width of tree
 *
 * **Lazy evaluation:**
 * Returns a [Sequence], so traversal is lazy. Iteration stops when needed.
 *
 * **Usage example:**
 * ```kotlin
 * data class TreeNode(val value: Int, val children: List<TreeNode> = emptyList())
 *
 * val root = TreeNode(
 *     1,
 *     listOf(
 *         TreeNode(2, listOf(TreeNode(4), TreeNode(5))),
 *         TreeNode(3)
 *     )
 * )
 *
 * root.walkBreadthFirst { it.children }.forEach { node ->
 *     println(node.value)  // Prints: 1, 2, 3, 4, 5
 * }
 *
 * // Find first node with value > 2
 * val found = root.walkBreadthFirst { it.children }
 *     .firstOrNull { it.value > 2 }  // Returns node 3
 * ```
 *
 * **vs. [walkTopDown] (DFS):**
 * - BFS: processes all nodes at depth D before depth D+1
 * - DFS: processes all descendants before siblings
 * - BFS uses more memory (queue), DFS uses call stack
 * - BFS better for level-based problems, DFS for path problems
 *
 * @param T the type of nodes in the tree
 * @param getChildren function that returns immediate children of a node
 * @return lazy sequence of nodes in breadth-first order
 *
 * @see walkTopDown for depth-first traversal
 */
fun <T> T.walkBreadthFirst(
    getChildren: (T) -> Iterable<T>,
): Sequence<T> = sequence {
    val queue = ArrayDeque<T>()
    queue.addLast(this@walkBreadthFirst)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        yield(current)

        queue.addAll(getChildren(current))
    }
}

/**
 * Traverses a tree structure in depth-first order (top-down, pre-order).
 *
 * Starts from this node and recursively processes all descendants before returning to siblings.
 * Yields the parent node before its children (pre-order traversal). This is the natural recursive
 * traversal of a tree structure.
 *
 * **Traversal order:**
 * Given a tree:
 * ```
 *       A
 *      / \
 *     B   C
 *    / \
 *   D   E
 * ```
 * DFS (top-down) yields: A, B, D, E, C
 *
 * **Use cases:**
 * - Default tree traversal (most intuitive)
 * - Pre-order traversal needs
 * - Path finding and exploration
 * - Computing all descendants of a node
 * - Building traversal order for processing
 *
 * **Performance:**
 * - Time: O(N) where N is number of nodes
 * - Space: O(H) for call stack, where H is tree height
 * - Better space usage for wide, shallow trees
 * - More stack usage for deep, narrow trees
 *
 * **Lazy evaluation:**
 * Returns a [Sequence], so traversal is lazy. Can stop iteration early without
 * traversing entire tree.
 *
 * **Usage example:**
 * ```kotlin
 * data class TreeNode(val value: Int, val children: List<TreeNode> = emptyList())
 *
 * val root = TreeNode(
 *     1,
 *     listOf(
 *         TreeNode(2, listOf(TreeNode(4), TreeNode(5))),
 *         TreeNode(3)
 *     )
 * )
 *
 * // Print entire tree
 * root.walkTopDown { it.children }.forEach { node ->
 *     println(node.value)  // Prints: 1, 2, 4, 5, 3
 * }
 *
 * // Find first leaf (node with no children)
 * val leaf = root.walkTopDown { it.children }
 *     .firstOrNull { it.children.isEmpty() }  // Returns node 4
 *
 * // Count total nodes
 * val count = root.walkTopDown { it.children }.count()
 * ```
 *
 * **Flattening nested structures:**
 * ```kotlin
 * data class Menu(val name: String, val submenus: List<Menu> = emptyList())
 *
 * val allMenus = mainMenu.walkTopDown { it.submenus }.toList()
 * ```
 *
 * **vs. [walkBreadthFirst] (BFS):**
 * - DFS: processes all descendants before siblings
 * - BFS: processes all nodes at depth D before depth D+1
 * - DFS uses call stack (recursive), BFS uses explicit queue
 * - DFS better for path problems, BFS better for level-based
 *
 * @param T the type of nodes in the tree
 * @param getChildren function that returns immediate children of a node
 * @return lazy sequence of nodes in depth-first (pre-order) traversal
 *
 * @see walkBreadthFirst for breadth-first traversal
 */
fun <T> T.walkTopDown(
    getChildren: (T) -> Iterable<T>,
): Sequence<T> = sequence {
    yield(this@walkTopDown)

    for (child in getChildren(this@walkTopDown)) {
        yieldAll(child.walkTopDown(getChildren))
    }
}