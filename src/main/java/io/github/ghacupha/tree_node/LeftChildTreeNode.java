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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Implementation of the K-ary (multi node) tree data structure,
 * based on the leftmost-child-right-sibling representation
 *
 * @param <T> Type of data being carried in the node
 */
public class LeftChildTreeNode<T> extends MultiTreeNode<T> {

    /**
     * Current UID of this object used for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * A reference to the first subtree tree node of the current tree node
     */
    private LeftChildTreeNode<T> leftMostNode;

    /**
     * A reference to the right sibling tree node of the current tree node
     */
    private LeftChildTreeNode<T> rightSiblingNode;

    /**
     * A reference to the last subtree node of the current tree node
     * <p>
     * Used to avoid the discovery of the last subtree node. As a result
     * significantly optimized such operations like addition etc.
     */
    private LeftChildTreeNode<T> lastSubtreeNode;

    /**
     * Creates an instance of this class
     *
     * @param data data to store in the current tree node
     */
    public LeftChildTreeNode(T data) {
        super(data);
    }

    /**
     * Returns the collection of the child nodes of the current node
     * with all of its proper descendants, if any
     * <p>
     * Returns {@link Collections#emptySet()} if the current node is leaf
     *
     * @return collection of the child nodes of the current node with
     * all of its proper descendants, if any;
     * {@link Collections#emptySet()} if the current node is leaf
     */
    @Override
    public Collection<? extends TreeNode<T>> subtrees() {
        if (isLeaf()) {
            return Collections.emptySet();
        }
        Collection<TreeNode<T>> subtrees = Collections.synchronizedSet(new LinkedHashSet<>());
        subtrees.add(leftMostNode);
        LeftChildTreeNode<T> nextSubtree = leftMostNode.rightSiblingNode;
        while (nextSubtree != null) {
            subtrees.add(nextSubtree);
            nextSubtree = nextSubtree.rightSiblingNode;
        }
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
     * result of the call; {@code false} otherwise
     */
    @Override
    public boolean add(TreeNode<T> subtree) {
        if (subtree == null) {
            return false;
        }
        TreeNode.linkParent(subtree, this);
        if (isLeaf()) {
            leftMostNode = (LeftChildTreeNode<T>) subtree;
            lastSubtreeNode = leftMostNode;
        } else {
            lastSubtreeNode.rightSiblingNode = (LeftChildTreeNode<T>) subtree;
            lastSubtreeNode = lastSubtreeNode.rightSiblingNode;
        }
        return true;
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
     * of the call; {@code false} otherwise
     */
    @Override
    public boolean dropSubtree(TreeNode<T> subtree) {
        if (subtree == null || isLeaf() || subtree.isRoot()) {
            return false;
        }
        if (leftMostNode.equals(subtree)) {
            leftMostNode = leftMostNode.rightSiblingNode;
            TreeNode.unlinkParent(subtree);
            ((LeftChildTreeNode<T>) subtree).rightSiblingNode = null;
            return true;
        } else {
            LeftChildTreeNode<T> nextSubtree = leftMostNode;
            while (nextSubtree.rightSiblingNode != null) {
                if (nextSubtree.rightSiblingNode.equals(subtree)) {
                    TreeNode.unlinkParent(subtree);
                    nextSubtree.rightSiblingNode = nextSubtree.rightSiblingNode.rightSiblingNode;
                    ((LeftChildTreeNode<T>) subtree).rightSiblingNode = null;
                    return true;
                } else {
                    nextSubtree = nextSubtree.rightSiblingNode;
                }
            }
        }
        return false;
    }

    /**
     * Removes all the subtrees with all of its descendants from the current
     * tree node
     */
    @Override
    public void clear() {
        if (!isLeaf()) {
            LeftChildTreeNode<T> nextNode = leftMostNode;
            while (nextNode != null) {
                TreeNode.unlinkParent(nextNode);
                LeftChildTreeNode<T> nextNodeRightSiblingNode = nextNode.rightSiblingNode;
                nextNode.rightSiblingNode = null;
                nextNode.lastSubtreeNode = null;
                nextNode = nextNodeRightSiblingNode;
            }
            leftMostNode = null;
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
    public TreeNode.TreeNodeIterator iterator() {
        return new TreeNode.TreeNodeIterator() {

            /**
             * Returns the leftmost node of the current tree node if the
             * current tree node is not a leaf
             *
             * @return leftmost node of the current tree node if the current
             *         tree node is not a leaf
             * @throws TreeNodeException an exception that is thrown in case
             *                           if the current tree node is a leaf
             */
            @Override
            protected TreeNode<T> leftMostNode() {
                return leftMostNode;
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
            protected TreeNode<T> rightSiblingNode() {
                return rightSiblingNode;
            }

        };
    }

    /**
     * Checks whether the current tree node is a leaf, e.g. does not have any
     * subtrees
     *
     * @return {@code true} if the current tree node is a leaf, e.g. does not
     * have any subtrees; {@code false} otherwise
     */
    @Override
    public boolean isLeaf() {
        return leftMostNode == null;
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
     * there is a specified subtree; {@code false} otherwise
     */
    @Override
    public boolean hasSubtree(TreeNode<T> subtree) {
        if (subtree == null || isLeaf() || subtree.isRoot()) {
            return false;
        }
        LeftChildTreeNode<T> nextSubtree = leftMostNode;
        while (nextSubtree != null) {
            if (nextSubtree.equals(subtree)) {
                return true;
            } else {
                nextSubtree = nextSubtree.rightSiblingNode;
            }
        }
        return false;
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
     * (entire tree) contains the specified node; {@code false}
     * otherwise
     */
    @Override
    public boolean contains(TreeNode<T> node) {
        if (node == null || isLeaf() || node.isRoot()) {
            return false;
        }
        LeftChildTreeNode<T> nextSubtree = leftMostNode;
        while (nextSubtree != null) {
            if (nextSubtree.equals(node)) {
                return true;
            }
            if (nextSubtree.contains(node)) {
                return true;
            }
            nextSubtree = nextSubtree.rightSiblingNode;
        }
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
     * the call; {@code false} otherwise
     */
    @Override
    public boolean remove(TreeNode<T> node) {
        if (node == null || isLeaf() || node.isRoot()) {
            return false;
        }
        if (dropSubtree(node)) {
            return true;
        }
        LeftChildTreeNode<T> nextSubtree = leftMostNode;
        while (nextSubtree != null) {
            if (nextSubtree.remove(node)) {
                return true;
            }
            nextSubtree = nextSubtree.rightSiblingNode;
        }
        return false;
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
    @Override
    public void traversePreOrder(TraversalAction<TreeNode<T>> action) {
        if (action.isIncomplete()) {
            action.perform(this);
            if (!isLeaf()) {
                LeftChildTreeNode<T> nextNode = leftMostNode;
                while (nextNode != null) {
                    nextNode.traversePreOrder(action);
                    nextNode = nextNode.rightSiblingNode;
                }
            }
        }
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
    @Override
    public void traversePostOrder(TraversalAction<TreeNode<T>> action) {
        if (action.isIncomplete()) {
            if (!isLeaf()) {
                LeftChildTreeNode<T> nextNode = leftMostNode;
                while (nextNode != null) {
                    nextNode.traversePostOrder(action);
                    nextNode = nextNode.rightSiblingNode;
                }
            }
            action.perform(this);
        }
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
    @Override
    public int height() {
        if (isLeaf()) {
            return 0;
        }
        int height = 0;
        LeftChildTreeNode<T> nextNode = leftMostNode;
        while (nextNode != null) {
            height = Math.max(height, nextNode.height());
            nextNode = nextNode.rightSiblingNode;
        }
        return height + 1;
    }

    /**
     * Returns the collection of nodes, which have the same parent
     * as the current node; {@link Collections#emptyList()} if the current
     * tree node is root or if the current tree node has no subtrees
     * <p>
     * Overridden to have a faster array implementation
     *
     * @return collection of nodes, which have the same parent as
     * the current node; {@link Collections#emptyList()} if the
     * current tree node is root or if the current tree node has
     * no subtrees
     */
    @Override
    public Collection<? extends MultiTreeNode<T>> siblings() {
        if (isRoot()) {
            String message = String.format("Unable to find the siblings. The tree node %1$s is root", root());
            throw new TreeNodeException(message);
        }
        LeftChildTreeNode<T> firstNode = ((LeftChildTreeNode<T>) parent()).leftMostNode;
        if (firstNode.rightSiblingNode == null) {
            return Collections.emptySet();
        }
        Collection<MultiTreeNode<T>> siblings = Collections.synchronizedSet(new LinkedHashSet<>());
        LeftChildTreeNode<T> nextNode = firstNode;
        while (nextNode != null) {
            if (!nextNode.equals(this)) {
                siblings.add(nextNode);
            }
            nextNode = nextNode.rightSiblingNode;
        }
        return siblings;
    }

}