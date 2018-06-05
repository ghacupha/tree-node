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
package io.github.ghacupha.tree_node.util;

import io.github.ghacupha.tree_node.TreeNode;

/**
 * Thrown when Node is not found on the root of the current node
 */
public class NodeNotFoundException extends RuntimeException {

    public <T> NodeNotFoundException(String message, TreeNode thisNode, TreeNode testNode) {

        super(message + String.format(" The node: %s was not found on the root of current node : %s", testNode, thisNode.root()));

    }

}
