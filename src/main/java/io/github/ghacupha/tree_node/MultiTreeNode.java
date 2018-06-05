/**
 * tree-node - Implementation of tree structures in java
 * Copyright © 2018 Edwin Njeru (mailnjeru@gmail.com)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.ghacupha.tree_node;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * This class represents the K-ary (multiple node) tree data
 * structure
 * <h1>Definition</h1>
 * <p>
 * <b>K-ary tree</b> - tree, in which each node has no more than k subtrees
 *
 * @param <T> Type of data being carried in the node
 */
public abstract class MultiTreeNode<T> extends TreeNode<T> {

    /**
     * Creates an instance of this class
     *
     * @param data data to store in the current tree node
     */
    MultiTreeNode(T data) {
        super(data);
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
     * result of the call; {@code false} otherwise
     */
    public boolean addSubtrees(Collection<? extends MultiTreeNode<T>> subtrees) {
        if (areAllNulls(subtrees)) {
            return false;
        }

        for (MultiTreeNode<T> subtree : subtrees) {
            linkParent(subtree, this);
            if (!add(subtree)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the collection of nodes, which have the same parent
     * as the current node; {@link Collections#emptyList()} if the current
     * tree node is root or if the current tree node has no subtrees
     *
     * @return collection of nodes, which have the same parent as
     * the current node; {@link Collections#emptyList()} if the
     * current tree node is root or if the current tree node has
     * no subtrees
     */
    public Collection<? extends MultiTreeNode<T>> siblings() {
        if (isRoot()) {
            String message = String.format("Unable to find the siblings. The tree node %1$s is root", root());
            throw new TreeNodeException(message);
        }
        Collection<? extends TreeNode<T>> parentSubtrees = super.parent().subtrees();
        int parentSubtreesSize = parentSubtrees.size();
        if (parentSubtreesSize == 1) {
            return Collections.emptySet();
        }
        Collection<MultiTreeNode<T>> siblings =
            parentSubtrees.stream().filter(parentSubtree -> !parentSubtree.equals(this)).map(parentSubtree -> (MultiTreeNode<T>) parentSubtree).collect(toSafeSet(parentSubtreesSize - 1));
        return siblings;
    }

    /**
     * Checks whether among the current tree node subtrees there are
     * all of the subtrees from the specified collection
     *
     * @param subtrees collection of subtrees to be checked for containment
     *                 within the current tree node subtrees
     * @return {@code true} if among the current tree node subtrees
     * there are all of the subtrees from the specified collection;
     * {@code false} otherwise
     */
    public boolean hasSubtrees(Collection<? extends MultiTreeNode<T>> subtrees) {
        if (isLeaf() || areAllNulls(subtrees)) {
            return false;
        }
        return subtrees.stream().allMatch(this::hasSubtree);
    }

    /**
     * Removes all of the collection's subtrees from the current tree node
     * <p>
     * Checks whether the current tree node was changed as a result of
     * the call
     *
     * @param subtrees collection containing subtrees to be removed from the
     *                 current tree node
     * @return {@code true} if the current tree node was changed as a result
     * of the call; {@code false} otherwise
     */
    public boolean dropSubtrees(Collection<? extends MultiTreeNode<T>> subtrees) {
        if (isLeaf() || areAllNulls(subtrees)) {
            return false;
        }
        boolean result = false;
        for (MultiTreeNode<T> subtree : subtrees) {
            boolean currentResult = dropSubtree(subtree);
            if (!result && currentResult) {
                result = true;
            }
        }
        return result;
    }

    Collector<MultiTreeNode<T>, ?, Set<MultiTreeNode<T>>> toSafeSet(int initialCapicity) {
        return Collectors.toCollection(() -> Collections.synchronizedSet(new LinkedHashSet<>(initialCapicity)));
    }

}
