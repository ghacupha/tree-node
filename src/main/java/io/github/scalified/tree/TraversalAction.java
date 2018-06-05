/**
 * Copyright © 2018 Edwin Njeru (mailnjeru@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.scalified.tree;

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
