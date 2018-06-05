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

/**
 * An interface, which defines the action to perform while traversing
 * the tree
 *
 * @param <T> Type of data being carried in the node
 */
public interface TraversalAction<T extends TreeNode> {

    /**
     * Is called on each node, while traversing the tree
     *
     * @param node reference to the current node during tree traversal
     */
    void perform(T node);

    /**
     * Checks whether the traversal is completed and no more required
     *
     * @return {@code false} if traversal is completed and no more required,
     * {@code false} otherwise
     */
    boolean isIncomplete();

}
