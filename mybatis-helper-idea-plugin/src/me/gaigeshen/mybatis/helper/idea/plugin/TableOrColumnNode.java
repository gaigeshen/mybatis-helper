package me.gaigeshen.mybatis.helper.idea.plugin;

import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * 展示表或者列数据的树节点
 *
 * @author gaigeshen
 */
public class TableOrColumnNode implements TreeNode {

  private Object data;
  private TableOrColumnNode parent;
  private List<TableOrColumnNode> children;

  /**
   *
   *
   * @param data 关联的数据
   * @param parent 父节点
   * @param children 子节点
   */
  public TableOrColumnNode(Object data, TableOrColumnNode parent, List<TableOrColumnNode> children) {
    this.data = data;
    this.parent = parent;
    this.children = children;
  }

  public Object getData() {
    return data;
  }

  @Override
  public TreeNode getChildAt(int childIndex) {
    return children.get(childIndex);
  }

  @Override
  public int getChildCount() {
    return children.size();
  }

  @Override
  public TreeNode getParent() {
    return parent;
  }

  @Override
  public int getIndex(TreeNode node) {
    return children.indexOf(node);
  }

  @Override
  public boolean getAllowsChildren() {
    return true;
  }

  @Override
  public boolean isLeaf() {
    return children == null || children.isEmpty();
  }

  @Override
  public Enumeration children() {
    return Collections.enumeration(children);
  }

  @Override
  public String toString() {
    return data != null ? data.toString() : "";
  }
}
