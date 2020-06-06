package cn.tcualhp.kbqa_tech_web.kbqa.AC;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 结点类 
 *
 * @author buddie
 *
 */
public class Node {
    // 当前结点的层级  
    private int level;
    // 当前结点后子结点，Key为小写字母  
    private Map<Character, Node> subNodes;
    // 当前结果匹配失败时的跳转结点  
    private Node failNode;
    // 当前结点是否是终结结点  
    private boolean terminal;

    /**
     * 当前结点是否已包含指定Key值的子结点 
     *
     * @param c
     * @return
     */
    public boolean containSubNode(Character c) {
        if (this.subNodes == null || this.subNodes.isEmpty()) {
            return false;
        }
        return subNodes.containsKey(c);
    }

    /**
     * 获取指定Key值的子结点 
     *
     * @param c
     * @return
     */
    public Node getSubNode(Character c) {
        if (this.subNodes == null || this.subNodes.isEmpty()) {
            return null;
        }
        return subNodes.get(c);
    }

    /**
     * 添加子结点 
     *
     * @param c
     * @param node
     */
    public void addSubNode(Character c, Node node) {
        if (this.subNodes == null) {
            this.subNodes = new HashMap<Character, Node>();
        }
        this.subNodes.put(c, node);
    }

    // getter & setter  
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Map<?, ?> getSubNodes() {
        return subNodes == null ? Collections.emptyMap() : subNodes;
    }

    public void setSubNodes(Map<Character, Node> subNodes) {
        this.subNodes = subNodes;
    }

    public Node getFailNode() {
        return failNode;
    }

    public void setFailNode(Node failNode) {
        this.failNode = failNode;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

}  