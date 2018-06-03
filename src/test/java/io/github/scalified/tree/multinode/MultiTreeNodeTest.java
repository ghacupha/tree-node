/**
 * Copyright © 2018 scalified-tree contributors (info@scalified.com, http://www.scalified.com, mailnjeru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.scalified.tree.multinode;

import io.github.scalified.tree.TreeNodeTest;
import io.github.scalified.tree.TreeNode;
import io.github.scalified.tree.TreeNodeException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author shell
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class MultiTreeNodeTest extends TreeNodeTest {

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

	@Test
	public void testSiblings() {
		// Test the specified tree nodes are the siblings of the current node
		String message = "Tree node siblings were incorrectly determined";

		Collection<TreeNode<String>> mSiblingsLevel1 = new HashSet<>(2);
		mSiblingsLevel1.add(node2);
		mSiblingsLevel1.add(node9);

		Collection<TreeNode<String>> mSiblingsLevel2 = new HashSet<>(2);
		mSiblingsLevel2.add(node3);
		mSiblingsLevel2.add(node7);

		assertEquals(mSiblingsLevel1, ((MultiTreeNode<String>) node1).siblings(), message);
		assertEquals(mSiblingsLevel2, ((MultiTreeNode<String>) node8).siblings(), message);

		assertEquals(Collections.emptySet(), ((MultiTreeNode<String>) node6).siblings(), message);
	}

	@Test
	public void testSiblingsRootNodeException() {
		// Test the root node sibling call throws exception
		assertThrows(TreeNodeException.class, () ->{
			((MultiTreeNode<String>) root).siblings();
		});
	}

	@Test
	public void testAddSubtrees() {
		// Test the specified tree nodes are successfully added
		String messageAddExpected = "The specified tree node was expected to be added but actually was not";
		String messageAddResultTrueExpected =
				"The tree nodes addition result was expected to be true, but actually was false";
		Collection<MultiTreeNode<String>> mSubtreesToAdd = new ArrayList<>(3);
		MultiTreeNode<String> nodeToAdd1 = (MultiTreeNode<String>) createTreeNode("NODE_TO_ADD_1");
		MultiTreeNode<String> nodeToAdd2 = (MultiTreeNode<String>) createTreeNode("NODE_TO_ADD_2");
		MultiTreeNode<String> nodeToAdd3 = (MultiTreeNode<String>) createTreeNode("NODE_TO_ADD_3");
		mSubtreesToAdd.add(nodeToAdd1);
		mSubtreesToAdd.add(nodeToAdd2);
		mSubtreesToAdd.add(nodeToAdd3);
		assertTrue(((MultiTreeNode<String>) node7).addSubtrees(mSubtreesToAdd), messageAddResultTrueExpected);
		assertTrue(node7.hasSubtree(nodeToAdd1), messageAddExpected);
		assertTrue(node7.hasSubtree(nodeToAdd2), messageAddExpected);
		assertTrue(node7.hasSubtree(nodeToAdd3), messageAddExpected);

		// Test the specified tree nodes are not added
		String messageAddResultFalseExpected =
				"The tree nodes addition result was expected to be false, but actually was true";
		assertFalse(((MultiTreeNode<String>) node8).addSubtrees(null), messageAddResultFalseExpected);
		assertFalse(((MultiTreeNode<String>) node1).addSubtrees(Collections.singletonList(null)), messageAddResultFalseExpected);
		assertFalse(((MultiTreeNode<String>) node10).addSubtrees(Collections.emptyList()), messageAddResultFalseExpected);
	}

	@Test
	public void testHasSubtrees() {
		// Test the current tree node contains all of the specified subtrees
		String messageContains =
				"The current tree node was expected to contain all of the subtrees, but actually was not";
		Collection<MultiTreeNode<String>> mSubtreesContain1 = new ArrayList<>(2);
		mSubtreesContain1.add((MultiTreeNode<String>) node2);
		mSubtreesContain1.add((MultiTreeNode<String>) node9);
		assertTrue(((MultiTreeNode<String>) root).hasSubtrees(mSubtreesContain1), messageContains);

		Collection<MultiTreeNode<String>> mSubtreesContain2 = new ArrayList<>(3);
		mSubtreesContain2.add((MultiTreeNode<String>) node3);
		mSubtreesContain2.add((MultiTreeNode<String>) node7);
		mSubtreesContain2.add((MultiTreeNode<String>) node8);
		assertTrue(((MultiTreeNode<String>) node2).hasSubtrees(mSubtreesContain2), messageContains);

		List<MultiTreeNode<String>> mSubtreesContain3 = Collections.singletonList((MultiTreeNode<String>) node6);
		assertTrue(((MultiTreeNode<String>) node5).hasSubtrees(mSubtreesContain3), messageContains);

		// Test the current tree node does not contain at least one of the the specified subtrees
		String messageNotContains =
				"The current tree node was expected to contain all of the subtrees, but actually was not";

		Collection<MultiTreeNode<String>> mSubtreesNotContain1 = new ArrayList<>(4);
		mSubtreesNotContain1.add((MultiTreeNode<String>) node3);
		mSubtreesNotContain1.add((MultiTreeNode<String>) node7);
		mSubtreesNotContain1.add((MultiTreeNode<String>) node8);
		mSubtreesNotContain1.add((MultiTreeNode<String>) node4);
		assertFalse(((MultiTreeNode<String>) root).hasSubtrees(mSubtreesNotContain1), messageNotContains);

		Collection<MultiTreeNode<String>> mSubtreesNotContain2 = new ArrayList<>(2);
		mSubtreesNotContain2.add((MultiTreeNode<String>) node5);
		mSubtreesNotContain2.add((MultiTreeNode<String>) anotherNode);
		assertFalse(((MultiTreeNode<String>) node3).hasSubtrees(mSubtreesNotContain2), messageNotContains);

		assertFalse(((MultiTreeNode<String>) node1).hasSubtrees(mSubtreesContain1), messageNotContains);
		assertFalse(((MultiTreeNode<String>) node2).hasSubtrees(null), messageNotContains);
		assertFalse(((MultiTreeNode<String>) node2).hasSubtrees(Collections.emptyList()), messageNotContains);
		assertFalse(((MultiTreeNode<String>) node2).hasSubtrees(Collections.singletonList(null)), messageNotContains);
	}

	@Test
	public void testDropSubtrees() {
		// Test the specified tree nodes are successfully removed from the current tree node
		String messageRemoveExpected = "The specified subtree was expected to be dropped, but actually was not";
		String messageRemoveResultTrue = "The subtrees removal result was expected to be true, but actually was false";
		Collection<MultiTreeNode<String>> mSubtreesToRemove1 = new ArrayList<>(3);
		mSubtreesToRemove1.add((MultiTreeNode<String>) node3);
		mSubtreesToRemove1.add((MultiTreeNode<String>) node7);
		mSubtreesToRemove1.add((MultiTreeNode<String>) node8);
		assertTrue(((MultiTreeNode<String>) node2).dropSubtrees(mSubtreesToRemove1), messageRemoveResultTrue);
		assertFalse(node2.hasSubtree(node3), messageRemoveExpected);
		assertFalse(node2.hasSubtree(node7), messageRemoveExpected);
		assertFalse(node2.hasSubtree(node8), messageRemoveExpected);

		Collection<MultiTreeNode<String>> mSubtreesToRemove2 = new ArrayList<>(2);
		mSubtreesToRemove2.add((MultiTreeNode<String>) node9);
		mSubtreesToRemove2.add((MultiTreeNode<String>) anotherNode);
		assertTrue(((MultiTreeNode<String>) root).dropSubtrees(mSubtreesToRemove2), messageRemoveResultTrue);
		assertFalse(root.contains(node9), messageRemoveExpected);

		// Test the specified tree nodes are not removed from the current tree node
		String messageRemoveResultFalse =
				"The subtrees removal result was expected to be false, but actually was true";
		assertFalse(((MultiTreeNode<String>) node1).dropSubtrees(Collections.singletonList((MultiTreeNode<String>) node2)), messageRemoveResultFalse);
		assertFalse(((MultiTreeNode<String>) root).dropSubtrees(Collections.emptyList()), messageRemoveResultFalse);
		assertFalse(((MultiTreeNode<String>) node1).dropSubtrees(Collections.singletonList(null)), messageRemoveResultFalse);
	}

}
