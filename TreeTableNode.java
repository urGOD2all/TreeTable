package TreeTable;

public interface TreeTableNode {
    TreeTableNode getChildAt(int position);
    TreeTableNode getParent();
    void insert(TreeTableNode newChild, int position);
    void remove(int position);
    int getIndex(TreeTableNode node);
}
