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
package io.github.ghacupha.tree_node.multinode;

import io.github.ghacupha.tree_node.ArrayTreeNode;
import io.github.ghacupha.tree_node.TreeNode;
import org.junit.Test;

/**
 * @author shell
 * @version 1.0.0
 * @since 1.0.0
 */
public class ArrayTreeNodeTest extends MultiTreeNodeTest {

	/*
	 * Test tree structure
	 *
	 *   +- root(ROOT_DATA)
	 *      +- node_1(NODE_DATA_1)
	 *      +- node_2(NODE_DATA_2)
	 *      |  +- node_3(NODE_DATA_3)
	 *      |  |  +- node_4(NODE_DATA_4)
	 *      |  |  +- node_5(NODE_DATA_1)
	 *      |  |  |  +- node6 (NODE_DATA4)
	 *      |  +- node_7(null)
	 *      |  +- node_8(NODE_DATA_1)
	 *      +- node9(NODE_DATA_4)
	 *      |  +- node10(null)
	 *
	 */

	@Override
	protected <T> TreeNode<T> createTreeNode(T data) {
		return new ArrayTreeNode<>(data);
	}

	/*private static <T> TreeNode<T> createTreeNode(T data, int branchingFactor) {
		return new ArrayTreeNode<>(data, branchingFactor);
	}*/

	@Test(expected = IllegalArgumentException.class)
	public void testIncorrectBranchingFactor() {
		int incorrectBranchingFactor = -10;

		//createTreeNode(ROOT_DATA, incorrectBranchingFactor);

		new ArrayTreeNode<>(ROOT_DATA, incorrectBranchingFactor);
	}

}
