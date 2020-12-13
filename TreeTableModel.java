package TreeTable;

import javax.swing.tree.TreeModel;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;

/**
 * This interface will provide methods for the JTree to act like a JTable.
 * These methods are not typically found on a Trees Model but usually on a JTables Model.
 * I have pretty much copied the javadoc comments from the upstream documenation.
 *
 */
public interface TreeTableModel extends TreeModel {

    /**
     * Returns the most specific superclass for all the cell values in the column. This is used by the JTable to set up a default renderer and editor for the column.
     * 
     * @param columnIndex - the index of the column
     *
     * @return Class - the common ancestor class of the object values in the model.
     */
    public Class<?> getColumnClass(int columnIndex);

    /**
     * Returns the number of columns in the model. A JTable uses this method to determine how many columns it should create and display by default.
     *
     * @return int - the number of columns in the model
     */
    public int getColumnCount();

    /**
     * Returns the name of the column at columnIndex. This is used to initialize the table's column header name. Note: this name does not need to be unique; two columns in a table can have the same name.
     *
     * @param columnIndex - the index of the column
     * @return String - the name of the column
     */
    public String getColumnName(int columnIndex);

    /**
     * Returns the value for the cell at columnIndex on node object
     *
     * @param node - an object in the tree that contains tree data
     * @param columnIndex - the index of the column
     * @return - value Object at the specified cell
     *
     */
    public Object getValueAt(Object node, int columnIndex);

    /**
     * Returns true if the cell at rowIndex and columnIndex is editable. Otherwise, setValueAt on the cell will not change the value of that cell.
     *
     * @param node - an object in the tree that contains tree data
     * @param columnIndex - the column whose value to be queried
     * @return true if the cell is editable, false if it is not
     */
    public boolean isCellEditable(Object node, int columnIndex);
}
