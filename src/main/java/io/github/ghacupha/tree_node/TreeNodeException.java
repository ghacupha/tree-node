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

/**
 * The class is responsible for different exceptional cases,
 * that may be caused by user actions while working with {@link TreeNode}
 *
 */
public class TreeNodeException extends RuntimeException {

    /**
     * Constructs a new tree node exception with the specified detail message
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method
     */
    public TreeNodeException(String message) {
        super(message);
    }

    /**
     * Constructs a new tree node exception with the specified detail message and cause
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent
     *                or unknown
     */
    public TreeNodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
