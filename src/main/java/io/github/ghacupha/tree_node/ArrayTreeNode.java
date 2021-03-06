/**
 * tree-node - Implementation of tree structures in java
 * Copyright © 2018 Edwin Njeru (mailnjeru@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.ghacupha.tree_node;

import io.github.ghacupha.tree_node.util.TreeNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of the K-ary (multi node) tree data structure,
 * based on the resizable array representation
 *
 * @param <T> Type of data being carried in the node
 */
public class ArrayTreeNode<T> extends MultiTreeNode<T> {

    private static final Logger log = LoggerFactory.getLogger(ArrayTreeNode.class);

    /**
     * Current UID of this object used for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default initial branching factor, that is the number of subtrees
     * this node can have before getting resized
     */
    private static final int DEFAULT_BRANCHING_FACTOR = 10;

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may mResult in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    /**
     * Current branching factor of the current tree node
     */
    private final int branchingFactor;
    /**
     * Array, which holds the references to the current tree node subtrees
     */
    private Object[] subtrees;
    /**
     * Number of subtrees currently present in the current tree node
     */
    private int subtreesSize;

    /**
     * Constructs the {@link ArrayTreeNode} instance
     *
     * @param data data to store in the current tree node
     */
    public ArrayTreeNode(T data) {
        super(data);
        this.branchingFactor = DEFAULT_BRANCHING_FACTOR;
        this.subtrees = new Object[branchingFactor];
        log.debug("ArrayNode subtree created with data: {} and branching factor of {}", data, this.branchingFactor);
    }

    /**
     * Constructs the {@link ArrayTreeNode} instance
     *
     * @param data data to store in the current tree node
     * @param branchingFactor initial branching factor, that is the number
     *                        of subtrees the current tree node can have
     *                        before getting resized
     */
    public ArrayTreeNode(T data, int branchingFactor) {
        super(data);
        if (branchingFactor < 0) {
            throw new IllegalArgumentException("Branching factor can not be negative");
        }
        this.branchingFactor = branchingFactor;
        this.subtrees = new Object[branchingFactor];
        log.debug("ArrayNode subtree created with data: {} and branching factor of {}", data, branchingFactor);
    }

    /**
     * Returns the collection of the child nodes of the current node
     * with all of its proper descendants, if any
     * <p>
     * Returns {@link Collections#emptySet()} if the current node is leaf
     *
     * @return collection of the child nodes of the current node with
     *         all of its proper descendants, if any;
     *         {@link Collections#emptySet()} if the current node is leaf
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends TreeNode<T>> subtrees() {
        if (isLeaf()) {
            return Collections.emptySet();
        }
        Collection<TreeNode<T>> subtrees =
            IntStream.range(0, subtreesSize).mapToObj(i -> (TreeNode<T>) this.subtrees[i]).collect(Collectors.toCollection(() -> Collections.synchronizedSet(new LinkedHashSet<>(subtreesSize))));
        log.debug("Returning collection of {} subtrees", subtrees.size());
        return subtrees;
    }

    /**
     * Adds the subtree with all of its descendants to the current tree node
     * <p>
     * {@code null} subtree cannot be added, in this case return result will
     * be {@code false}
     * <p>
     * Checks whether this tree node was changed as a result of the call
     *
     * @param subtree subtree to add to the current tree node
     * @return {@code true} if this tree node was changed as a
     *         result of the call; {@code false} otherwise
     */
    @Override
    public boolean add(TreeNode<T> subtree) {
        log.debug("Adding subtree {} to {} \n\n", subtree, this);
        if (subtree == null) {
            return false;
        }
        TreeNode.linkParent(subtree, this);
        ensureSubtreesCapacity(subtreesSize + 1);
        subtrees[subtreesSize++] = subtree;
        return true;
    }

    /**
     * Increases the capacity of the subtrees array, if necessary, to
     * ensure that it can hold at least the number of subtrees specified
     * by the minimum subtrees capacity argument
     *
     * @param minSubtreesCapacity the desired minimum subtrees capacity
     */
    private void ensureSubtreesCapacity(int minSubtreesCapacity) {
        log.debug("Checking if tree can handle the capacity of {}", minSubtreesCapacity);
        if (minSubtreesCapacity > subtrees.length) {
            increaseSubtreesCapacity(minSubtreesCapacity);
        }
    }

    /**
     * Increases the subtrees array capacity to ensure that it can hold
     * at least the number of elements specified by the minimum subtrees
     * capacity argument
     *
     * @param minSubtreesCapacity the desired minimum subtrees capacity
     */
    private void increaseSubtreesCapacity(int minSubtreesCapacity) {
        int oldSubtreesCapacity = subtrees.length;
        log.debug("Increasing subtrees capacity from {} to {}", oldSubtreesCapacity, minSubtreesCapacity);
        int newSubtreesCapacity = oldSubtreesCapacity + (oldSubtreesCapacity >> 1);
        if (newSubtreesCapacity < minSubtreesCapacity) {
            newSubtreesCapacity = minSubtreesCapacity;
        }
        if (newSubtreesCapacity > MAX_ARRAY_SIZE) {
            if (minSubtreesCapacity < 0) {
                throw new OutOfMemoryError();
            }
            newSubtreesCapacity = minSubtreesCapacity > MAX_ARRAY_SIZE ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
        }
        subtrees = Arrays.copyOf(subtrees, newSubtreesCapacity);
    }

    /**
     * Drops the first occurrence of the specified subtree from the current
     * tree node
     * <p>
     * Checks whether the current tree node was changed as a result of
     * the call
     *
     * @param subtree subtree to drop from the current tree node
     * @return {@code true} if the current tree node was changed as a result
     *         of the call; {@code false} otherwise
     */
    @Override
    public boolean dropSubtree(TreeNode<T> subtree) {
        log.debug("Dropping subtree {} from node {} \n\n", subtree, this);
        if (subtree == null || isLeaf() || subtree.isRoot()) {
            return false;
        }
        int mSubtreeIndex = indexOf(subtree);
        if (mSubtreeIndex < 0) {
            return false;
        }
        int mNumShift = subtreesSize - mSubtreeIndex - 1;
        if (mNumShift > 0) {
            System.arraycopy(subtrees, mSubtreeIndex + 1, subtrees, mSubtreeIndex, mNumShift);
        }
        subtrees[--subtreesSize] = null;
        TreeNode.unlinkParent(subtree);
        return true;
    }

    /**
     * Returns the index of the first occurrence of the specified subtree
     * within subtrees array; {@code -1} if the subtrees array does not contain
     * such subtree
     *
     * @param subtree subtree to find the index of
     * @return index of the first occurrence of the specified subtree within
     *         subtrees array; {@code -1} if the subtrees array does not contain
     *         such subtree
     */
    @SuppressWarnings("unchecked")
    private int indexOf(TreeNode<T> subtree) {
        log.debug("Finding the indexOf subtree {} in node : {} \n\n", subtree, this);
        int i = 0;
        while (i < subtreesSize) {
            TreeNode<T> mSubtree = (TreeNode<T>) subtrees[i];
            if (mSubtree.equals(subtree)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Removes all the subtrees with all of its descendants from the current
     * tree node
     */
    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        if (!isLeaf()) {
            IntStream.range(0, subtreesSize).mapToObj(i -> (TreeNode<T>) subtrees[i]).forEachOrdered(TreeNode::unlinkParent);
            subtrees = new Object[branchingFactor];
            subtreesSize = 0;
        }
    }

    /**
     * Returns an iterator over the elements in this tree in proper sequence
     * <p>
     * The returned iterator is <b>fail-fast</b>
     *
     * @return an iterator over the elements in this tree in proper sequence
     */
    @Override
    @Nonnull
    public TreeNodeIterator iterator() {
        return new TreeNodeIterator() {

            /**
             * Returns the leftmost node of the current tree node if the
             * current tree node is not a leaf
             *
             * @return leftmost node of the current tree node if the current
             *         tree node is not a leaf
             * @throws TreeNodeException an exception that is thrown in case
             *                           if the current tree node is a leaf
             */
            @SuppressWarnings("unchecked")
            @Override
            protected TreeNode<T> leftMostNode() {
                return (TreeNode<T>) subtrees[0];
            }

            /**
             * Returns the right sibling node of the current tree node if the
             * current tree node is not root
             *
             * @return right sibling node of the current tree node if the current
             *         tree node is not root
             * @throws TreeNodeException an exception that may be thrown in case if
             *                           the current tree node is root
             */
            @Override
            @SuppressWarnings("unchecked")
            protected TreeNode<T> rightSiblingNode() {
                log.debug("Getting the right sibling to node : {}", this);
                ArrayTreeNode<T> mParent = (ArrayTreeNode<T>) ArrayTreeNode.super.parent();
                int rightSiblingNodeIndex = mParent.indexOf(ArrayTreeNode.this) + 1;
                log.debug("The right sibling is : {}", rightSiblingNodeIndex);
                return rightSiblingNodeIndex < mParent.subtreesSize ? (TreeNode<T>) mParent.subtrees[rightSiblingNodeIndex] : null;
            }
        };
    }

    /**
     * Checks whether the current tree node is a leaf, e.g. does not have any
     * subtrees
     * <p>
     * Overridden to have a faster array implementation
     *
     * @return {@code true} if the current tree node is a leaf, e.g. does not
     *         have any subtrees; {@code false} otherwise
     */
    @Override
    public boolean isLeaf() {
        return subtreesSize == 0;
    }

    /**
     * Checks whether among the current tree node subtrees there is
     * a specified subtree
     * <p>
     * Overridden to have a faster array implementation
     *
     * @param subtree subtree whose presence within the current tree
     *                node children is to be checked
     * @return {@code true} if among the current tree node subtrees
     *         there is a specified subtree; {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean hasSubtree(TreeNode<T> subtree) {
        log.debug("Checking if : {} has subtree : {}", this, subtree);
        if (subtree == null || isLeaf() || subtree.isRoot()) {
            log.debug("Subtree {} not found", subtree);
            return false;
        }
        return IntStream.range(0, subtreesSize).mapToObj(i -> (TreeNode<T>) subtrees[i]).anyMatch(subtree::equals);
    }

    /**
     * Checks whether the current tree node with all of its descendants
     * (entire tree) contains the specified node
     * <p>
     * Overridden to have a faster array implementation
     *
     * @param node node whose presence within the current tree node with
     *             all of its descendants (entire tree) is to be checked
     * @return {@code true} if the current node with all of its descendants
     *         (entire tree) contains the specified node; {@code false}
     *         otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(TreeNode<T> node) {
        log.debug("Checking if node: {} contains node : {}", this, node);
        if (node == null || isLeaf() || node.isRoot()) {
            log.debug("Node: {} not found in node : {}", node, this);
            return false;
        }
        int i = 0;
        while (i < subtreesSize) {
            TreeNode<T> subtree = (TreeNode<T>) subtrees[i];
            if (subtree.equals(node)) {
                log.debug("Node: {} has been found at index : {}", node, this.indexOf(node));
                return true;
            }
            if (subtree.contains(node)) {
                log.debug("Node: {} has been found at index : {}", node, this.indexOf(node));
                return true;
            }
            i++;
        }
        log.debug("Node: {} not found in node : {}", node, this);
        return false;
    }

    /**
     * Removes the first occurrence of the specified node from the entire tree,
     * starting from the current tree node and traversing in a pre order manner
     * <p>
     * Checks whether the current tree node was changed as a result of the call
     * <p>
     * Overridden to have a faster array implementation
     *
     * @param node node to remove from the entire tree
     * @return {@code true} if the current tree node was changed as a result of
     *         the call; {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(TreeNode<T> node) {
        log.debug("Removing the node: {} from the subtree: {}", node, this);
        if (node == null || isLeaf() || node.isRoot()) {
            log.debug("The node : {} has not been removed", node);
            return false;
        }
        if (dropSubtree(node)) {
            return true;
        }
        return IntStream.range(0, subtreesSize).mapToObj(i -> (TreeNode<T>) subtrees[i]).anyMatch(subtree -> subtree.remove(node));
    }

    /**
     * Traverses the tree in a pre ordered manner starting from the
     * current tree node and performs the traversal action on each
     * traversed tree node
     * <p>
     * Overridden to have a faster array implementation
     *
     * @param action action, which is to be performed on each tree
     *               node, while traversing the tree
     */
    @SuppressWarnings("unchecked")
    @Override
    public void traversePreOrder(TraversalAction<TreeNode<T>> action) {
        log.debug("Performing postOrder traversal on : {} with {} subtrees", this, subtreesSize);
        if (action.isIncomplete()) {
            action.perform(this);
            if (!isLeaf()) {
                IntStream.range(0, subtreesSize).mapToObj(i -> (TreeNode<T>) subtrees[i]).forEachOrdered(subtree -> subtree.traversePreOrder(action));
            }
        }
        log.debug("postOrder traversal completed...");
    }

    /**
     * Traverses the tree in a post ordered manner starting from the
     * current tree node and performs the traversal action on each
     * traversed tree node
     * <p>
     * Overridden to have a faster array implementation
     *
     * @param action action, which is to be performed on each tree
     *               node, while traversing the tree
     */
    @SuppressWarnings("unchecked")
    @Override
    public void traversePostOrder(TraversalAction<TreeNode<T>> action) {
        log.debug("Performing postOrder traversal on : {} with {} subtrees", this, subtreesSize);
        if (action.isIncomplete()) {
            if (!isLeaf()) {
                IntStream.range(0, subtreesSize).mapToObj(i -> (TreeNode<T>) subtrees[i]).forEachOrdered(subtree -> subtree.traversePostOrder(action));
            }
            action.perform(this);
        }
        log.debug("postOrder traversal completed...");
    }

    /**
     * Returns the height of the current tree node, e.g. the number of edges
     * on the longest downward path between that node and a leaf
     * <p>
     * Overridden to have a faster array implementation
     *
     * @return height of the current tree node, e.g. the number of edges
     * on the longest downward path between that node and a leaf
     */
    @SuppressWarnings("unchecked")
    @Override
    public int height() {
        if (isLeaf()) {
            return 0;
        }
        int height = 0;
        int i = 0;
        while (i < subtreesSize) {
            TreeNode<T> subtree = (TreeNode<T>) subtrees[i];
            height = Math.max(height, subtree.height());
            i++;
        }
        return height + 1;
    }

    /**
     * Adds the collection of the subtrees with all of theirs descendants
     * to the current tree node
     * <p>
     * Checks whether this tree node was changed as a result of the call
     *
     * @param subtrees collection of the subtrees with all of their
     *                 descendants
     * @return {@code true} if this tree node was changed as a
     *         result of the call; {@code false} otherwise
     */
    @Override
    public boolean addSubtrees(Collection<? extends MultiTreeNode<T>> subtrees) {
        log.debug("Adding {} subtrees to ArrayTreeNode id {}", subtrees.size(), this);
        if (TreeNode.areAllNulls(subtrees)) {
            return false;
        }
        subtrees.forEach(subtree -> linkParent(subtree, this));
        Object[] subtreesArray = subtrees.toArray();
        int subtreesArrayLength = subtreesArray.length;
        ensureSubtreesCapacity(subtreesSize + subtreesArrayLength);
        System.arraycopy(subtreesArray, 0, this.subtrees, subtreesSize, subtreesArrayLength);
        subtreesSize += subtreesArrayLength;
        return subtreesArrayLength != 0;
    }

    /**
     * Returns the collection of nodes, which have the same parent
     * as the current node; {@link Collections#emptyList()} if the current
     * tree node is root or if the current tree node has no subtrees
     * <p>
     * Overridden to have a faster array implementation
     *
     * @return collection of nodes, which have the same parent as
     *         the current node; {@link Collections#emptyList()} if the
     *         current tree node is root or if the current tree node has
     *         no subtrees
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends MultiTreeNode<T>> siblings() {
        if (isRoot()) {
            String message = String.format("Unable to find the siblings. The tree node %1$s is root", root());
            throw new TreeNodeException(message);
        }
        ArrayTreeNode<T> mParent = (ArrayTreeNode<T>) super.parent();
        int parentSubtreesSize = mParent.subtreesSize;
        if (parentSubtreesSize == 1) {
            return Collections.emptySet();
        }
        Object[] parentSubtreeObjects = mParent.subtrees;

        Collection<MultiTreeNode<T>> siblings = IntStream.range(0, parentSubtreesSize).mapToObj(i -> (MultiTreeNode<T>) parentSubtreeObjects[i]).filter(parentSubtree -> !parentSubtree.equals(this))
            .collect(toSafeSet(parentSubtreesSize - 1));

        log.debug("Returning {} siblings", siblings.size());

        return siblings;
    }

}
