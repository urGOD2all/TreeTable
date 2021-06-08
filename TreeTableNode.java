package TreeTable;

public interface TreeTableNode {
    TreeTableNode getChildAt(int position);
    TreeTableNode getParent();
    void remove(int position);
    int getIndex(TreeTableNode node);
}
