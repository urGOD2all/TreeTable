package TreeTable.examples;

import TreeTable.AbstractTreeTableModel;
import TreeTable.TreeTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class ExampleTreeTableModel extends AbstractTreeTableModel {
    // This line is a must. You must make sure that you return the interface class on any expandable items
    static protected Class<?>[] columnTypes = { TreeTableModel.class, String.class };
    String cols[];
    Object root;


    public ExampleTreeTableModel(Object root) {
        this.root = root;
    }

// TODO: Put this in the right place
    public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }

// TODO: I think the object here could be changed to row (this needs to be changed down in the interface) because I think it could work like a table model. The object comes from the hamster code which is throwing this off
    public Object getValueAt(Object node, int columnIndex) {
        return node;
    }

    public int getColumnCount() {
        return 2;
    }

// TODO: Rename this in the model to getIndex
    public int getIndexOfChild(Object parent, Object child) {
        DefaultMutableTreeNode p = (DefaultMutableTreeNode) parent;
        DefaultMutableTreeNode c = (DefaultMutableTreeNode) child;
        return p.getIndex(c);
    }

    public boolean isLeaf(Object node) {
        //System.out.println(node);
        DefaultMutableTreeNode d = (DefaultMutableTreeNode) node;
//        System.out.println(d.isLeaf());
        return d.isLeaf();
    }

// TODO: Rename this in the model to getLeafCount
    public int getChildCount(Object parent) {
        DefaultMutableTreeNode p = (DefaultMutableTreeNode) parent;
//        System.out.println(p.getLeafCount());
        return p.getLeafCount()-1;
    }

// TODO: Rename this in the model to getChildAt
    public Object getChild(Object parent, int index) {
        DefaultMutableTreeNode d = (DefaultMutableTreeNode) parent;
//        System.out.println(d.getChildAt(index));
        return d.getChildAt(index);
    }

    public Object getRoot() {
        return root;
    }
}
