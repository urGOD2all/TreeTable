package TreeTable;

import javax.swing.table.AbstractTableModel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.util.EventListener;

/**
 * Glues the JTable and the JTree together.
 *
 * In the spirit of the Swing developers, this class is designed to implement certain functions for the TreeTable in the same way that the javax.swing.table.AbstractTableModel class does for JTables.
 *
 * To create a concrete TreeTabelModel as a subclass of AbstractTreeTableModel you need only provide implementations for the following methods:
 * public Object getChild(Object parent, int index)
 * public int getChildCount(Object parent)
 * public int getIndexOfChild(Object parent, Object child)
 * public Object getRoot()
 * public boolean isLeaf(Object node)
 * public int getColumnCount()
 * public Object getValueAt(Object node, int columnIndex)
 * public Class<?> getColumnClass(int column) - Must return TreeTableModel.class for the column ID that identifies the expandable row. See the TODO below!
 *
 * TODO: Can I make an implementation fail if they dont override getColumClass ? ATM you can implement without which will mean this wont work so I would rather it warn you or should I make the implementation here return TreeModel.clas for the first row and Object.class for everything else.... I think the later makes more sense
 */
public abstract class AbstractTreeTableModel extends AbstractTableModel implements TreeTableModel {
    // Set the version ID for serialized objects
    static final long serialVersionUID = 1L;

    // Reference to the JTree
    private JTree tree;

    /**
     * TODO: describe me
     * TODO: Should this be done in TreeTable ?
     */
    protected void setupJTree(JTree tree) {
        // Store reference to the JTree
        this.tree = tree;

        // Set the expansion listener and get it to fire the table events to update the view
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
            }

            public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
        });
    }

    /**
     * Returns the name of the specified column.  This method generates default
     * names in a sequence (starting with column 0):  A, B, C, ..., Z, AA, AB,
     * AC, ..., AZ, BA, BB, BC, and so on.  Subclasses may override this method
     * to allow column names to be specified on some other basis.
     * Cribbed from AbstractTableModel
     *
     * @param columnIndex  the column index.
     *
     * @return The name of the column.
     */
    public String getColumnName(int columnIndex) {
        StringBuffer buffer = new StringBuffer();
        while (columnIndex >= 0) {
            buffer.insert(0, (char) ('A' + columnIndex % 26));
            columnIndex = columnIndex / 26 - 1;
        }
        return buffer.toString();
    }

    /**
     * Returns the Class for all Object instances
     * in the specified column.
     *
     * @param columnIndex the column index.
     * 
     * @return Object.class
     */
    // TODO: should I implement this or make it part of the interface ? The user does need to implement to get expandable rows so this doenst make sense doing it here like this!
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    /**
     * Returns the number of rows in the TreeTable
     *
     * @return int - number of rows in the TreeTable
     */
    public int getRowCount() {
        // Return the row count from the JTree, this will be equal to the number of rows in the table.
        return tree.getRowCount();
    }

    /**
     * Find the object at position row. This will query the JTree and find the the TreePath for the row, then return the Object
     *
     * @param row - the row in the view being queried
     * @return Object - Object that contains the tree data at the specified row
     */
    public Object nodeForRow(int row) {
        // Get the path for the object at that row
        TreePath treePath = tree.getPathForRow(row);
        // Return the Object at that position in the path
        return treePath.getLastPathComponent();
    }

    /**
     * Get the value from the Tree for the object at the row position and column specified.
     *
     * @param row - the row in the view being queried
     * @param column - the column in the view being queried
     * @return Object - Object at the specified location
     */
    public Object getValueAt(int row, int column) {
        return getValueAt(nodeForRow(row), column);
    }

    /**
     * Returns true if the cell at row and column is editable.
     *
     * @param row - the row in the view being queried
     * @param column - the column in the view being queried
     * @return boolean - true if the cell is editable
     */
    public boolean isCellEditable(int row, int column) {
        return isCellEditable(nodeForRow(row), column);
    }

    /**
     * This implementation returns true for all arguments, subclasses can override the method
     * if necessary but ensure to return true otherwise the tree nodes will not expand!
     *
     * @param node  the node object for this row
     * @param columnIndex  the column index of the cell.
     *
     * @return true
     */
    public boolean isCellEditable(Object node, int columnIndex) {
        return true;
    }

    /**
     * fireTreeStructureChanged (from JTree). Only used internally, if needed to expose
     * publicly, should have the correct name (for TreeTable).
     * 
     * @param source the node where the model has changed
     * @param path the path to the root node
     * @param childIndices the indices of the affected elements
     * @param children the affected elements
     */
    private void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
      TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
      TreeModelListener[] listeners = getTreeModelListeners();

      for (int i = listeners.length - 1; i >= 0; --i) listeners[i].treeStructureChanged(event);
    }

    /**
     * Invoke this is you have changed the columns in the TreeTable
     * calls fireTableStructureChanged() on the JTable component
     */
    public void fireTreeTableStructureChanged() {
        // Should be enough to just reload the table.
        fireTableStructureChanged();
    }

    /**
     * Invoke this method if you've modified the data upon which this model
     * depends. The model will notify all of its listeners that the model has
     * changed. It will fire the events, necessary to update the layout caches and
     * repaint the TreeTable.
     *
     * This method will refresh the information about whole TreeTable from the root object underpinning the model.
     * </p>
     */
    public void reload() {
        int n = getChildCount(getRoot());
        int[] childIdx = new int[n];
        Object[] children = new Object[n];

        for (int i = 0; i < n; i++) {
            childIdx[i] = i;
            children[i] = getChild(getRoot(), i);
        }

        // Reload the JTree
        fireTreeStructureChanged(this, new Object[] { getRoot() }, childIdx, children);
        // Reload the JTable
        fireTableDataChanged();
    }

    /**
     * Invoke this method if you've modified the data upon which this model
     * depends. The model will notify all of its listeners that the model has
     * changed. It will fire the events necessary when adding rows and
     * repaints the TreeTable.
     *
     * This is fine as long as you dont have listeners that want to know where the
     * new data appeared.
     */
    public void nodesWereInserted() {
        // Reload the JTree
        fireTreeStructureChanged(this, new Object[] { getRoot() }, null, null);
        // Reload the JTable
        // TODO: This doesnt work when the table is empty and rows are added. There appears to be a race between the Tree and the Table.
        // What happens is that getRowCount() from the model returns 0 when there is data in the model. The Tree tries to update from the data in the model and checks in with
        // the table to see what the index is in the view. This will fail with index out of bounds because the Table still thinks there are 0 rows.
        fireTableDataChanged();
    }

    /**
     * Adds a listener to the tree model. The listener will receive notification
     * of all changes to the tree model.
     *
     * @param listener the listener.
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    /**
     * Removes a listener from the tree model so that it will no longer receive
     * notification of changes to the tree model.
     *
     * @param listener the listener to remove.
     */
    public void removeTreeModelListener(TreeModelListener l) {
      listenerList.remove(TreeModelListener.class, l);
    }

    /**
     * Returns an array containing the listeners that have been added to the
     * tree model.
     *
     * @return Array of TreeModelListener objects.
     *
     */
    public TreeModelListener[] getTreeModelListeners() {
      return (TreeModelListener[]) listenerList.getListeners(TreeModelListener.class);
    }

    /**
     * Returns an array of listeners of the given type that are registered with
     * this model.
     * 
     * @param listenerType  the listener class.
     * 
     * @return An array of listeners (possibly empty).
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }

    /**
     * Messaged when the user has altered the value for the item identified by path to newValue. If newValue signifies a truly new value the model should post a treeNodesChanged event.
     * Because this abstract implementation is designed not to be editable it does nothing.
     *
     * @param path - path to the node that the user has altered
     * @param newValue - the new value from the TreeCellEditor
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
    }


    /**
     * Cribbed from DefaultTreeModel
     *
     * Notifies all listeners that have registered interest for notification on this event type. The event instance is lazily created using the parameters passed into the fire method.
     *
     * @param source - the source of the TreeModelEvent; typically this
     * @param path - the path to the parent the nodes were added to
     * @param childIndices - the indices of the new elements
     * @param children - the new elements
     *
     */
    private void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }
        }
    }

    /**
     * Cribbed from DefaultTreeModel
     *
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param source the source of the {@code TreeModelEvent};
     *               typically {@code this}
     * @param path the path to the parent the nodes were removed from
     * @param childIndices the indices of the removed elements
     * @param children the removed elements
     */
    private void fireTreeNodesRemoved(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
            }
        }
    }

    /**
     * Cribbed from DefaultTreeModel
     *
     * Invoke this method after you've removed some TreeNodes from node.
     * childIndices should be the index of the removed elements and must be sorted in ascending order.
     * removedChildren should be the array of the children objects that were removed.
     *
     */
    public void nodesWereInserted(TreeTableNode node, int[] childIndices) {
        if(listenerList != null && node != null && childIndices != null && childIndices.length > 0) {
            int               cCount = childIndices.length;
            Object[]          newChildren = new Object[cCount];

            for(int counter = 0; counter < cCount; counter++)
                newChildren[counter] = node.getChildAt(childIndices[counter]);

            fireTreeNodesInserted(this, getPathToRoot(node, 0), childIndices, newChildren);
            // Inform the JTable of the change
            fireTableDataChanged();
        }
    }

    /**
     * Cribbed from DefaultTreeModel
     *
     * Invoke this method after you've removed some TreeNodes from
     * node.  childIndices should be the index of the removed elements and
     * must be sorted in ascending order. And removedChildren should be
     * the array of the children objects that were removed.
     *
     * @param node             parent node which childred were removed
     * @param childIndices     indexes of removed childs
     * @param removedChildren  array of the children objects that were removed
     */
    public void nodesWereRemoved(TreeTableNode node, int[] childIndices, Object[] removedChildren) {
        if(node != null && childIndices != null) {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices,
                                 removedChildren);
            // Inform the JTable of the change
            fireTableDataChanged();
        }
    }

    /**
     * Cribbed from DefaultTreeModel
     *
     * Message this to remove node from its parent. This will message
     * nodesWereRemoved to create the appropriate event. This is the
     * preferred way to remove a node as it handles the event creation
     * for you.
     *
     * @param node the node to be removed from it's parrent
     */
    public void removeNodeFromParent(TreeTableNode node) {
        TreeTableNode         parent = (TreeTableNode)node.getParent();

        if(parent == null)
            throw new IllegalArgumentException("node does not have a parent.");

        int[]            childIndex = new int[1];
        Object[]         removedArray = new Object[1];

        childIndex[0] = parent.getIndex(node);
        parent.remove(childIndex[0]);
        removedArray[0] = node;
        nodesWereRemoved(parent, childIndex, removedArray);
    }

    /**
     * Cribbed from DefaultTreeModel
     *
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     *
     * @param aNode the TreeNode to get the path for
     * @return an array of TreeNodes giving the path from the root
     */
    public TreeTableNode[] getPathToRoot(TreeTableNode aNode) {
        return getPathToRoot(aNode, 0);
    }

    /**
     * Cribbed from DefaultMutableTreeNode
     *
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     *
     * @param aNode  the TreeNode to get the path for
     * @param depth  an int giving the number of steps already taken towards
     *        the root (on recursive calls), used to size the returned array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node
     */
    private TreeTableNode[] getPathToRoot(TreeTableNode aNode, int depth) {
        TreeTableNode[]              retNodes;
        // This method recurses, traversing towards the root in order
        // size the array. On the way back, it fills in the nodes,
        // starting from the root and working back to the original node.

        /* Check for null, in case someone passed in a null node, or
           they passed in an element that isn't rooted at root. */
        if(aNode == null) {
            if(depth == 0)
                return null;
            else
                retNodes = new TreeTableNode[depth];
        }
        else {
            depth++;
            if(aNode == getRoot())
                retNodes = new TreeTableNode[depth];
            else
                retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }
}
