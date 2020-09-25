package TreeTable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import java.util.EventListener;
import javax.swing.table.AbstractTableModel;

/**
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
 * public Class<?> getColumnClass(int column) - Must return TreeTableModel.class for the column ID that identifies the expandable row.
 *
 * TODO: Can I make an implementation fail if they dont override getColumClass ? ATM you can implement without which will mean this wont work so I would rather it warn you
 */
public abstract class AbstractTreeTableModel implements TreeTableModel {
    // Set the version ID for serialized objects
    static final long serialVersionUID = 1L;

    // Storage for the listeners registered with this model. Instantiated here because it means users don't need to call this constructor in their implementation (like JTable)
    protected EventListenerList listenerList = new EventListenerList();
    // Store a reference to the TableModel part of the TreeTable. This will get initialized when passed to the TreeTable constructor and grants access to table only calls
    private AbstractTableModel tableModel;

    /**
     * Default constructor, nothing to do here
     */
    public AbstractTreeTableModel() {
    }

    public void setTableModel(AbstractTableModel tm) {
        tableModel = tm;
    }

    public void fireTableStructureChanged() {
        tableModel.fireTableStructureChanged();
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
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
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
     * Sets the value of the given cell.  This implementation ignores all 
     * arguments and does nothing, subclasses should override the 
     * method if necessary. Useful if the data is not editable.
     *
     * @param value  the new value (null permitted).
     * @param rowIndex  the row index of the cell.
     * @param columnIndex  the column index of the cell.
     */
    public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
      // Do nothing...
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
    public <T extends EventListener> T[] getListeners(Class<T> listenerType)
    {
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
}