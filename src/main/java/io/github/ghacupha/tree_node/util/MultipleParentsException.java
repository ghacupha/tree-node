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
package io.github.ghacupha.tree_node.util;

import io.github.ghacupha.tree_node.TreeNode;

/**
 * This exception is encountered when you add a node that already has a parent as a subtree to
 * another node.
 * TODO Do tests for this exception
 */
public class MultipleParentsException extends RuntimeException {


    /**
     *
     * @param proposedChild The node being added that already has another parent
     * @param proposedParent The Parent to which we are proposing to add a child node
     * @param <T> Type of data being carried around in the tree
     */
    public <T> MultipleParentsException(TreeNode<T> proposedChild, TreeNode<T> proposedParent) {
        super(String.format("The node: %s could not be added to %s since it already has a parent : %s", proposedChild, proposedParent, proposedChild.parent()));
    }
}
