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


import io.github.ghacupha.tree_node.util.NodeNotFoundException;
import io.github.ghacupha.tree_node.util.NullNodeException;
import io.github.ghacupha.tree_node.util.TreeNodeException;
import io.github.ghacupha.tree_node.util.UnCloneableNodeException;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static io.github.ghacupha.tree_node.util.TreeNodeUtils.ensureSingleParent;

/**
 * This interface represents the basic tree data structure
 * <h1>Definition</h1>
 * A tree data structure can be defined recursively (locally) as a collection of nodes
 * (starting at a root node), where each node is a data structure consisting of a value,
 * together with a list of references to nodes (the children), with the constraints that
 * no reference is duplicated, and none points to the root
 * <p>
 * A tree is a (possibly non-linear) data structure made up of nodes or vertices and edges
 * without having any cycle. The tree with no nodes is called the <b>null</b> or
 * <b>empty</b> tree. A tree that is not empty consists of a root node and potentially many
 * levels of additional nodes that form a hierarchy
 * <h1>Terminology</h1>
 * <ul>
 * <li><b>Node</b> - a single point of a tree</li>
 * <li><b>Edge</b> - line, which connects two distinct nodes</li>
 * <li><b>Root</b> - top node of the tree, which has no parent</li>
 * <li><b>Parent</b> - a node, other than the root, which is connected to other successor
 * nodes</li>
 * <li><b>Child</b> - a node, other than the root, which is connected to predecessor</li>
 * <li><b>Leaf</b> - a node without children</li>
 * <li><b>Path</b> - a sequence of nodes and edges connecting a node with a
 * descendant</li>
 * <li><b>Path Length</b> - number of nodes in the path - 1</li>
 * <li><b>Ancestor</b> - the top parent node of the path</li>
 * <li><b>Descendant</b> - the bottom child node of the path</li>
 * <li><b>Siblings</b> - nodes, which have the same parent</li>
 * <li><b>Subtree</b> - a node in a tree with all of its proper descendants, if any</li>
 * <li><b>Node Height</b> - the number of edges on the longest downward path between that
 * node and a leaf</li>
 * <li><b>Tree Height</b> - the number of edges on the longest downward path between the
 * root and a leaf (root height)</li>
 * <li><b>Depth (Level)</b> - the path length between the root and the current node</li>
 * <li><b>Ordered Tree</b> - tree in which nodes has the children ordered</li>
 * <li><b>Labeled Tree</b> - tree in which a label or value is associated with each node
 * of the tree</li>
 * <li><b>Expression Tree</b> - tree which specifies the association of an expression�s
 * operands and its operators in a uniform way, regardless
 * of whether the association is required by the placement
 * of parentheses in the expression or by the precedence and
 * associativity rules for the operators involved</li>
 * <li><b>Branching Factor</b> - maximum number of children a node can have</li>
 * <li><b>Pre order</b> - a form of tree traversal, where the action is called firstly on
 * the current node, and then the pre order function is called again
 * recursively on each of the subtree from left to right</li>
 * <li><b>Post order</b> - a form of tree traversal, where the post order function is called
 * recursively on each subtree from left to right and then the
 * action is called</li>
 * </ul>
 *
 * @param <T> Type of data being carried in the node
 */
public abstract class TreeNode<T> implements Iterable<TreeNode<T>>, Serializable, Cloneable {

    private static final Logger log = LoggerFactory.getLogger(TreeNode.class);

    /**
     * Identifier generator, used to get a unique id for each created tree node
     */
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    /**
     * A unique identifier, used to distinguish or compare the tree nodes
     */
    private final long id = ID_GENERATOR.getAndIncrement();

    /**
     * Reference to the parent tree node. Is {@code null} if the current tree node is root
     */
    private TreeNode<T> parent;

    /**
     * Data store in the current tree node
     */
    private T data;

    /**
     * Creates an instance of this class
     *
     * @param data data to store in the current tree node
     */
    public TreeNode(T data) {
        this.data = data;
    }

    /**
     * Creates an instance of this class without setting the {@link #data}
     */
    public TreeNode() {
    }

    /**
     * Populates the input collection with the tree nodes, while traversing the tree
     *
     * @param collection input collection to populate
     * @param <T>        type of the tree node
     * @return traversal action, which populates the input collection with the tree nodes
     */
    private static <T> TraversalAction<TreeNode<T>> populateAction(final Collection<TreeNode<T>> collection) {
        return new TraversalAction<TreeNode<T>>() {
            @Override
            public void perform(TreeNode<T> node) {
                collection.add(node);
            }

            @Override
            public boolean isIncomplete() {
                return true;
            }
        };
    }

    /**
     * Links the specified parent tree node reference as the parent to the
     * specified tree node
     *
     * @param node   tree node to assign the parent tree node reference to
     * @param parent tree node to assign as a parent reference
     * @param <T>    type of the data stored in the tree nodes
     */
    protected static <T> void linkParent(TreeNode<T> node, TreeNode<T> parent) {
        log.debug("Linking node : {} to the parent : {}", node, parent);
        if (node != null) {
            ensureSingleParent(node, parent);
            node.parent = parent;
        }
    }


    /**
     * Removes the parent tree node reference link from the specified tree node
     *
     * @param node tree node to remove the parent tree node reference assignment from
     * @param <T>  type of the data store in the tree node
     */
    protected static <T> void unlinkParent(TreeNode<T> node) {
        node.parent = null;
    }

    /**
     * Checks whether there is at least one not {@code null} element within
     * the input collection
     * <pre>
     *     Validator.isAnyNotNull(Arrays.asList("foo", null))   = true
     *     Validator.isAnyNotNull(null)                         = false
     *     Validator.isAnyNotNull(Collections.emptyList())      = false
     *     Validator.isAnyNotNull(Arrays.asList(null, null))    = false
     * </pre>
     *
     * @param collection input collection to check
     * @param <T>        type of the data, which parametrises collection
     * @return {@code true} if there is at least one not {@code null} element within
     * the input collection; {@code false} otherwise
     */
    private static <T> boolean isAnyNotNull(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }
        return collection.stream().anyMatch(Objects::nonNull);
    }

    /**
     * Checks whether the specified collection is @{code null}, empty or if
     * all of its elements are {@code null}
     * <pre>
     *     areAllNulls(null)                          = true
     *     areAllNulls(Collections.emptyList())       = true
     *     areAllNulls(Arrays.asList(null, null))     = true
     *     areAllNulls(Arrays.asList("foo", null))    = false
     * </pre>
     *
     * @param collection input collection to check
     * @param <T>        type of the data, which parametrises collection
     * @return {@code true} if the specified collection is {@code null}, empty
     * or if all of its elements are {@code null}; {@code false} otherwise
     */
    protected static <T> boolean areAllNulls(Collection<T> collection) {
        return !isAnyNotNull(collection);
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
    public abstract Collection<? extends TreeNode<T>> subtrees();

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
    public abstract boolean add(TreeNode<T> subtree);

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
    public abstract boolean dropSubtree(TreeNode<T> subtree);

    /**
     * Removes all the subtrees with all of its descendants from the current
     * tree node
     */
    public abstract void clear();

    /**
     * Returns an iterator over the elements in this tree in proper sequence
     * <p>
     * The returned iterator is <b>fail-fast</b>
     *
     * @return an iterator over the elements in this tree in proper sequence
     */
    @Override
    @Nonnull
    public abstract TreeNodeIterator iterator();

    /**
     * Returns the data object stored in the current tree node
     *
     * @return data object stored in the current tree node
     */
    public T data() {
        return data;
    }

    /**
     * Stores the data object into the current tree node
     *
     * @param data data object to store into the current tree node
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Checks whether the current tree node is the root of the tree
     *
     * @return {@code true} if the current tree node is root of the tree;
     * {@code false} otherwise
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Returns the root node of the current node
     * <p>
     * Returns itself if the current node is root
     *
     * @return root node of the current node; itself,
     * if the current node is root
     */
    public TreeNode<T> root() {
        log.debug("root enquiry has been raised on the node: {} \n\n", this);
        if (isRoot()) {
            return this;
        }
        TreeNode<T> node = this;
        do {
            node = node.parent();
        } while (!node.isRoot());
        log.debug("the node : {} is the root to the node : {}\n\n", node, this);
        return node;
    }

    /**
     * Returns the parent node of the current node
     * <p>
     * Returns {@code null} if the current node is root
     *
     * @return parent node of the current node; {@code null}
     * if the current node is root
     */
    public TreeNode<T> parent() {
        return parent;
    }

    /**
     * Checks whether the current tree node is a leaf, e.g. does not have any
     * subtrees
     *
     * @return {@code true} if the current tree node is a leaf, e.g. does not
     * have any subtrees; {@code false} otherwise
     */
    public boolean isLeaf() {
        return subtrees().isEmpty();
    }

    /**
     * Searches the tree node within the tree, which has the specified data,
     * starting from the current tree node and returns the first occurrence of it
     *
     * @param data data to find the tree node with
     * @return first occurrence of the searched tree node with data specified
     */
    @SuppressWarnings("unchecked")
    public TreeNode<T> find(final T data) {
        log.debug("Inquiry for data : {} raised on the node : {}\n\n", data, this);
        if (isLeaf()) {
            return (data() == null ? data == null : data().equals(data)) ? this : null;
        }
        final TreeNode<T>[] searchedNode = (TreeNode<T>[]) Array.newInstance(getClass(), 1);
        traversePreOrder(new TraversalAction<TreeNode<T>>() {
            @Override
            public void perform(TreeNode<T> node) {
                if ((node.data() == null ? data == null : node.data().equals(data))) {
                    searchedNode[0] = node;
                }
            }

            @Override
            public boolean isIncomplete() {
                return searchedNode[0] == null;
            }
        });
        log.debug("Data {} has been found on the node : {}\n\n", data, this);
        return searchedNode[0];
    }

    /**
     * Searches the tree nodes within the tree, which have the specified data,
     * starting from the current tree node and returns the collection of the found
     * tree nodes
     *
     * @param data data to find the tree nodes with
     * @return collection of the searched tree nodes with data specified
     */
    public Collection<? extends TreeNode<T>> findAll(final T data) {
        if (isLeaf()) {
            return (data() == null ? data == null : data().equals(data)) ? Collections.singleton(this) : Collections.emptySet();
        }
        final Collection<TreeNode<T>> searchedNodes = Collections.newSetFromMap(new ConcurrentHashMap<>());
        traversePreOrder(new TraversalAction<TreeNode<T>>() {
            @Override
            public void perform(TreeNode<T> node) {
                if ((node.data() == null ? data == null : node.data().equals(data))) {
                    searchedNodes.add(node);
                }
            }

            @Override
            public boolean isIncomplete() {
                return true;
            }
        });
        return searchedNodes;
    }

    /**
     * Checks whether among the current tree node subtrees there is
     * a specified subtree
     *
     * @param subtree subtree whose presence within the current tree
     *                node children is to be checked
     * @return {@code true} if among the current tree node subtrees
     * there is a specified subtree; {@code false} otherwise
     */
    public boolean hasSubtree(TreeNode<T> subtree) {
        if (subtree == null || isLeaf() || subtree.isRoot()) {
            return false;
        }
        return subtrees().stream().anyMatch(mSubtree -> mSubtree.equals(subtree));
    }

    /**
     * Checks whether the current tree node with all of its descendants
     * (entire tree) contains the specified node
     *
     * @param node node whose presence within the current tree node with
     *             all of its descendants (entire tree) is to be checked
     * @return {@code true} if the current node with all of its descendants
     * (entire tree) contains the specified node; {@code false}
     * otherwise
     */
    public boolean contains(TreeNode<T> node) {
        log.debug("Checking if the node: {} contains the node: {}\n\n", this, node);
        if (node == null || isLeaf() || node.isRoot()) {
            return false;
        }
        return subtrees().stream().anyMatch(subtree -> subtree.equals(node) || subtree.contains(node));
    }

    /**
     * Checks whether the current tree node with all of its descendants
     * (entire tree) contains all of the nodes from the specified collection
     * (the place of nodes within a tree is not important)
     *
     * @param nodes collection of nodes to be checked for containment
     *              within the current tree node with all of its descendants
     *              (entire tree)
     * @return {@code true} if the current tree node with all of its
     * descendants (entire tree) contains all of the nodes from the
     * specified collection; {@code false} otherwise
     */
    public boolean containsAll(Collection<TreeNode<T>> nodes) {
        if (isLeaf() || areAllNulls(nodes)) {
            return false;
        }
        return nodes.stream().allMatch(this::contains);
    }

    /**
     * Removes the first occurrence of the specified node from the entire tree,
     * starting from the current tree node and traversing in a pre order manner
     * <p>
     * Checks whether the current tree node was changed as a result of the call
     *
     * @param node node to remove from the entire tree
     * @return {@code true} if the current tree node was changed as a result of
     * the call; {@code false} otherwise
     */
    public boolean remove(TreeNode<T> node) {
        if (node == null || isLeaf() || node.isRoot()) {
            return false;
        }
        if (dropSubtree(node)) {
            return true;
        }
        return subtrees().stream().anyMatch(subtree -> subtree.remove(node));
    }

    /**
     * Removes all of the collection's nodes from the entire tree, starting from
     * the current tree node and traversing in a pre order manner
     * <p>
     * Checks whether the current tree node was changed as a result of the call
     *
     * @param nodes collection containing nodes to be removed from the entire tree
     * @return {@code true} if the current tree node was changed as a result
     * of the call; {@code false} otherwise
     */
    public boolean removeAll(Collection<TreeNode<T>> nodes) {
        if (isLeaf() || areAllNulls(nodes)) {
            return false;
        }
        boolean result = false;

        for (TreeNode<T> node : nodes) {
            boolean currentResult = remove(node);
            if (!result && currentResult) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Traverses the tree in a pre ordered manner starting from the
     * current tree node and performs the traversal action on each
     * traversed tree node
     *
     * @param action action, which is to be performed on each tree
     *               node, while traversing the tree
     */
    public void traversePreOrder(TraversalAction<TreeNode<T>> action) {
        if (action.isIncomplete()) {
            action.perform(this);
            if (!isLeaf()) {
                subtrees().forEach(subtree -> subtree.traversePreOrder(action));
            }
        }
    }

    /**
     * Traverses the tree in a post ordered manner starting from the
     * current tree node and performs the traversal action on each
     * traversed tree node
     *
     * @param action action, which is to be performed on each tree
     *               node, while traversing the tree
     */
    public void traversePostOrder(TraversalAction<TreeNode<T>> action) {
        if (action.isIncomplete()) {
            if (!isLeaf()) {
                subtrees().forEach(subtree -> subtree.traversePostOrder(action));
            }
            action.perform(this);
        }
    }

    /**
     * Returns the pre ordered collection of nodes of the current tree
     * starting from the current tree node
     *
     * @return pre ordered collection of nodes of the current tree starting
     * from the current tree node
     */
    public Collection<TreeNode<T>> preOrdered() {
        if (isLeaf()) {
            return Collections.singleton(this);
        }
        final Collection<TreeNode<T>> mPreOrdered = new CopyOnWriteArrayList<>();
        TraversalAction<TreeNode<T>> action = populateAction(mPreOrdered);
        traversePreOrder(action);
        return mPreOrdered;
    }

    /**
     * Returns the post ordered collection of nodes of the current tree
     * starting from the current tree node
     *
     * @return post ordered collection of nodes of the current tree starting
     * from the current tree node
     */
    public Collection<TreeNode<T>> postOrdered() {
        if (isLeaf()) {
            return Collections.singleton(this);
        }
        final Collection<TreeNode<T>> mPostOrdered = new CopyOnWriteArrayList<>();
        TraversalAction<TreeNode<T>> action = populateAction(mPostOrdered);
        traversePostOrder(action);
        return mPostOrdered;
    }

    /**
     * Returns the collection of nodes, which connect the current node
     * with its descendants
     *
     * @param descendant the bottom child node for which the path is calculated
     * @return collection of nodes, which connect the current node with its descendants
     * @throws TreeNodeException exception that may be thrown in case if the
     *                           current node does not have such descendant or if the
     *                           specified tree node is root
     */
    public Collection<? extends TreeNode<T>> path(TreeNode<T> descendant) {
        log.debug("Path query for the descendant :{} from the node {}\n\n", descendant, this);
        if (descendant == null || isLeaf() || this.equals(descendant)) {
            return Collections.singletonList(this);
        }
        String errorMessage = "Unable to build the path between tree nodes. ";
        if (descendant.isRoot()) {
            String message = String.format(errorMessage + "Current node %1$s is root", descendant);
            throw new TreeNodeException(message);
        }
        List<TreeNode<T>> path = new CopyOnWriteArrayList<>();
        TreeNode<T> node = descendant;
        path.add(node);
        while (!node.isRoot()) {
            node = node.parent();
            path.add(0, node);
            if (this.equals(node)) {
                return path;
            }
        }
        String message = String.format(errorMessage + "The specified tree node %1$s is not the descendant of tree node %2$s", descendant, this);
        throw new TreeNodeException(message);
    }

    /**
     * Returns the common ancestor of the current node and the node specified
     *
     * @param node node, which the common ancestor is determined for,
     *             along with the current node
     * @return common ancestor of the current node and the node specified
     * @throws TreeNodeException exception that may be thrown in case if the
     *                           specified tree node is null or the specified tree node
     *                           does not belong to the current tree or if any of the tree
     *                           nodes either the current one or the specified one is root
     */
    public TreeNode<T> commonAncestor(TreeNode<T> node) {
        log.debug("Checking if the node : {} has a common ancestor with the node {} \n\n", node, this);
        String errorMessage = "Unable to find the common ancestor between tree nodes: ";
        if (node == null) {
            throw new NullNodeException(errorMessage);
        }
        if (!this.root().contains(node)) {
            throw new NodeNotFoundException(errorMessage, this, node);
        }
        if (this.isRoot() || node.isRoot()) {
            String message = String.format(errorMessage + "The tree node %1$s is root", this.isRoot() ? this : node);
            throw new TreeNodeException(message);
        }
        if (this.equals(node) || node.isSiblingOf(this)) {
            return parent();
        }
        int thisNodeLevel = this.level();
        int thatNodeLevel = node.level();
        return thisNodeLevel > thatNodeLevel ? node.parent() : this.parent();
    }

    /**
     * Checks whether the current tree node is a sibling of the specified node,
     * e.g. whether the current tree node and the specified one both have the
     * same parent
     *
     * @param node node, which sibling with the current tree node is to be checked
     * @return {@code true} if the current tree node is a sibling of the specified
     * node, e.g. whether the current tree node and the specified one both
     * have the same parent; {@code false} otherwise
     */
    public boolean isSiblingOf(TreeNode<T> node) {
        log.debug("Checking if the node : {} is the sibling of the node : {}\n\n", this, node);
        return node != null && !isRoot() && !node.isRoot() && this.parent().equals(node.parent());
    }

    /**
     * Checks whether the current tree node is the ancestor of the node specified
     *
     * @param node node, which is checked to be the descendant of the current tree
     *             node
     * @return {@code true} if the current tree node is the ancestor of the node
     * specified; {@code false} otherwise
     */
    public boolean isAncestorOf(TreeNode<T> node) {
        log.debug("Checking if the node: {} is the ancestor of : {}\n\n", this, node);
        if (node == null || isLeaf() || node.isRoot() || this.equals(node)) {
            return false;
        }
        TreeNode<T> mNode = node;
        while (!mNode.isRoot()) {
            mNode = mNode.parent();
            if (this.equals(mNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the current tree node is the descendant of the node specified
     *
     * @param node node, which is checked to be the ancestor of the current tree
     *             node
     * @return {@code true} if the current tree node is the ancestor of the node
     * specified; {@code false} otherwise
     */
    public boolean isDescendantOf(TreeNode<T> node) {
        log.debug("Checking if the node: {} is the descendant of : {}\n\n", this, node);
        if (node == null || this.isRoot() || node.isLeaf() || this.equals(node)) {
            return false;
        }
        TreeNode<T> mNode = this;
        while (!mNode.isRoot()) {
            mNode = mNode.parent();
            if (node.equals(mNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of nodes in the entire tree, including the current tree node
     *
     * @return number of nodes in the entire tree, including the current tree node
     */
    public long size() {
        log.debug("Checking the size of the node : {}\n\n", this);
        if (isLeaf()) {
            return 1;
        }
        final long[] count = {0};
        TraversalAction<TreeNode<T>> action = new TraversalAction<TreeNode<T>>() {
            @Override
            public void perform(TreeNode<T> node) {
                count[0]++;
            }

            @Contract(pure = true)
            @Override
            public boolean isIncomplete() {
                return true;
            }
        };
        traversePreOrder(action);
        log.debug("The size of the node : {}, determined to be {}\n\n", this, count[0]);
        return count[0];
    }

    /**
     * Returns the height of the current tree node, e.g. the number of edges
     * on the longest downward path between that node and a leaf
     *
     * @return height of the current tree node, e.g. the number of edges
     * on the longest downward path between that node and a leaf
     */
    public int height() {

        if (isLeaf()) {
            return 0;
        }
        final int[] height = {0};

        subtrees().forEach(subTree -> height[0] = Math.max(height[0], subTree.height()));

        return height[0] + 1;
    }

    /**
     * Returns the depth (level) of the current tree node within the entire tree,
     * e.g. the number of edges between the root tree node and the current one
     *
     * @return depth (level) of the current tree node within the entire tree,
     * e.g. the number of edges between the root tree node and the current
     * one
     */
    public int level() {
        log.debug("Checking the level of the node : {}\n\n", this);
        if (isRoot()) {
            return 0;
        }
        int level = 0;
        TreeNode<T> node = this;
        while (!node.isRoot()) {
            node = node.parent();
            level++;
        }
        log.debug("The level of the node : {} determined to be {}", this, level);
        return level;
    }

    /**
     * Creates and returns a copy of this object
     *
     * @return a clone of this instance
     */
    @SuppressWarnings("unchecked")
    @Override
    public TreeNode<T> clone() {
        try {
            return (TreeNode<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnCloneableNodeException(e);
        }
    }

    /**
     * Indicates whether some object equals to this one
     *
     * @param obj the reference object with which to compare
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TreeNode<T> that = (TreeNode<T>) obj;
        return this.id == that.id;
    }

    /**
     * Returns the hash code value of this object
     *
     * @return hash code value of this object
     */
    @Override
    public int hashCode() {
        return (int) (this.id ^ (this.id >>> 32));
    }

    /**
     * Returns the string representation of this object
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        final int topNodeLevel = level();
        TraversalAction<TreeNode<T>> action = new TraversalAction<TreeNode<T>>() {
            @Override
            public void perform(TreeNode<T> node) {
                int nodeLevel = node.level() - topNodeLevel;
                IntStream.range(0, nodeLevel).mapToObj(i -> "|  ").forEachOrdered(builder::append);
                builder.append("+- ").append(node.data()).append("\n");
            }

            @Override
            public boolean isIncomplete() {
                return true;
            }
        };
        traversePreOrder(action);
        return builder.toString();
    }

    /**
     * Base tree node iterator, which is expected to be extended by {@link TreeNode}
     * subclasses in order to perform custom implementation and return it in
     * {@link #iterator()}
     */
    protected abstract class TreeNodeIterator implements Iterator<TreeNode<T>> {

        /**
         * An expected size of the tree node required to check
         * whether the tree node was changed during <b>foreach</b>
         * iteration
         */
        private long expectedSize = size();

        /**
         * Reference to the current tree node within iteration
         */
        private TreeNode<T> currentNode;

        /**
         * Reference to the next tree node within iteration
         */
        private TreeNode<T> nextNode = TreeNode.this;

        /**
         * Indicates whether there is a next tree node available
         * within iteration
         */
        private boolean nextNodeAvailable = true;

        /**
         * Returns the leftmost node of the current tree node if the
         * current tree node is not a leaf
         *
         * @return leftmost node of the current tree node if the current
         * tree node is not a leaf
         * @throws TreeNodeException an exception that is thrown in case
         *                           if the current tree node is a leaf
         */
        protected abstract TreeNode<T> leftMostNode();

        /**
         * Returns the right sibling node of the current tree node if the
         * current tree node is not root
         *
         * @return right sibling node of the current tree node if the current
         * tree node is not root
         * @throws TreeNodeException an exception that may be thrown in case if
         *                           the current tree node is root
         */
        protected abstract TreeNode<T> rightSiblingNode();

        /**
         * Checks whether the current tree node is not a leaf and returns the
         * leftmost node from {@link #leftMostNode()}
         *
         * @return leftmost node of the current tree node if the current tree
         * node is not a leaf
         * @throws TreeNodeException an exception that is thrown in case
         *                           if the current tree node is a leaf
         */
        private TreeNode<T> checkAndGetLeftMostNode() {
            if (isLeaf()) {
                throw new TreeNodeException("Leftmost node can't be obtained. Current tree node is a leaf");
            } else {
                return leftMostNode();
            }
        }

        /**
         * Checks whether the current tree node is not root and returns the
         * right sibling node from {@link #rightSiblingNode()}
         *
         * @return right sibling node of the current tree node if the current
         * tree node is not root
         * @throws TreeNodeException an exception that may be thrown in case if
         *                           the current tree node is root
         */
        private TreeNode<T> checkAndGetRightSiblingNode() {
            if (isRoot()) {
                throw new TreeNodeException("Right sibling node can't be obtained. Current tree node is root");
            } else {
                return rightSiblingNode();
            }
        }

        /**
         * Returns {@code true} if the iteration has more elements;
         * otherwise returns {@code false}
         *
         * @return {@code true} if the iteration has more elements;
         * {@code false} otherwise
         */
        @Override
        public boolean hasNext() {
            return nextNodeAvailable;
        }

        /**
         * Returns the next element in the iteration
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public TreeNode<T> next() {
            checkForConcurrentModification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            currentNode = nextNode;
            if (nextNode.isLeaf()) {
                if (nextNode.isRoot()) {
                    nextNodeAvailable = false;
                } else {
                    do {
                        TreeNode<T> currentNode = nextNode;
                        nextNode = nextNode.parent();
                        if (currentNode.equals(TreeNode.this)) {
                            nextNodeAvailable = false;
                            break;
                        }
                        TreeNode<T> nextSibling = Objects.requireNonNull(currentNode.iterator()).checkAndGetRightSiblingNode();
                        if (nextSibling != null) {
                            nextNode = nextSibling;
                            break;
                        }
                    } while (true);
                }
            } else {
                nextNode = Objects.requireNonNull(nextNode.iterator()).checkAndGetLeftMostNode();
            }
            return currentNode;
        }

        /**
         * Checks whether tree node was changed during <b>foreach</b>
         * iteration and throws {@link ConcurrentModificationException}
         * exception if so
         */
        private void checkForConcurrentModification() {

            // really clever code
            if (expectedSize != size()) {
                throw new ConcurrentModificationException();
            }
        }

        /**
         * Removes from the underlying tree the last element returned by this
         * iterator (optional operation)
         * <p>
         * This method can be called only once per call to {@link #next}.
         * The behavior of an iterator is unspecified if the underlying tree
         * is modified while the iteration is in progress in any way other
         * than by calling this method
         *
         * @throws IllegalStateException an exception that may be thrown in case
         *                               if remove was performed without any
         *                               iteration
         * @throws TreeNodeException     an exception that may be thrown in case if
         *                               remove was performed on a root node
         */
        @Override
        public void remove() {
            String errorMessage = "Failed to remove the tree node. ";
            if (!isIterationStarted()) {
                throw new IllegalStateException(errorMessage + "The iteration has not been performed yet");
            }
            if (currentNode.isRoot()) {
                String message = String.format(errorMessage + "The tree node %1$s is root", currentNode);
                throw new TreeNodeException(message);
            }
            if (currentNode.equals(TreeNode.this)) {
                throw new TreeNodeException(errorMessage + "The starting node can't be removed");
            }
            checkForConcurrentModification();
            TreeNode<T> currentNode = this.currentNode;
            while (true) {
                if (currentNode.isRoot()) {
                    nextNodeAvailable = false;
                    break;
                }
                TreeNode<T> rightSiblingNode = currentNode.iterator().checkAndGetRightSiblingNode();
                if (rightSiblingNode != null) {
                    nextNode = rightSiblingNode;
                    break;
                }
                currentNode = currentNode.parent;
            }
            TreeNode<T> parent = this.currentNode.parent();
            parent.dropSubtree(this.currentNode);
            this.currentNode = parent;
            expectedSize = size();
        }

        /**
         * Returns whether iteration has been started
         *
         * @return {@code true} if iteration has been started; {@code false} otherwise
         */
        private boolean isIterationStarted() {
            return currentNode != null;
        }

    }

}
