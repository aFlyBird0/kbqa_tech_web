package cn.tcualhp.kbqa_tech_web.kbqa.AC;

import java.util.*;

//import Node;

public class ACTree {
    private Node rootNode;

    public ACTree(String[] keyWords) {
        // 初始树  
        initTree(keyWords);
        // 构建失败跳转  
        buildFailLink();
    }

    /**
     * 初始树 
     *
     * @param keyWords
     */
    private void initTree(String[] keyWords) {
        rootNode = new Node();
        rootNode.setSubNodes(new HashMap<Character, Node>());
        char[] charArray;
        for (String keyWord : keyWords) {
            if (keyWord.isEmpty()) {
                continue;
            }
            charArray = keyWord.toLowerCase().toCharArray();
            buildKeyMap(charArray);
        }
    }

    /**
     * 构建指定字符数组的结点 
     *
     * @param charArray
     */
    private void buildKeyMap(char[] charArray) {
        Character c;
        Node curNode = rootNode;
        Node node;
        for (int i = 0; i < charArray.length; i++) {
            c = charArray[i];
            if (curNode.containSubNode(c)) {
                node = curNode.getSubNode(c);
            } else {
                node = new Node();
                node.setLevel(i + 1);
                curNode.addSubNode(c, node);
            }
            if (i == charArray.length - 1) {
                node.setTerminal(true);
            }
            curNode = node;
        }
    }

    /**
     * 构建失败跳转 
     */
    private void buildFailLink() {
        buildFirstLevelFailLink();
        buildOtherLevelFailLink();
    }

    /**
     * 根结点的所有第一级子结点，失败跳转均为根结点 
     */
    private void buildFirstLevelFailLink() {
        Collection<Node> nodes = (Collection<Node>) rootNode.getSubNodes().values();
        for (Node node : nodes) {
            node.setFailNode(rootNode);
        }
    }

    /**
     * 根结点、第一级结点以外的所有结点，失败跳转均为其父结点的失败结点的对应子结点 
     */
    private void buildOtherLevelFailLink() {
        Queue<Node> queue = new LinkedList<Node>((Collection<? extends Node>) rootNode.getSubNodes().values());
        Node node;
        while (!queue.isEmpty()) {
            node = queue.remove();
            buildNodeFailLink(node, queue);
        }
    }

    /**
     * 构建指定结点的下一层结点的失败跳转 
     *
     * @param node
     */
    private void buildNodeFailLink(Node node, Queue<Node> queue) {
        if (node.getSubNodes().isEmpty()) {
            return;
        }
        queue.addAll((Collection<? extends Node>) node.getSubNodes().values());
        Node failNode = node.getFailNode();
        Set<Character> subNodeKeys = (Set<Character>) node.getSubNodes().keySet();
        Node subFailNode;
        for (Character key : subNodeKeys) {
            subFailNode = failNode;
            while (subFailNode != rootNode && !subFailNode.containSubNode(key)) {
                subFailNode = subFailNode.getFailNode();
            }
            subFailNode = subFailNode.getSubNode(key);
            if (subFailNode == null) {
                subFailNode = rootNode;
            }
            node.getSubNode(key).setFailNode(subFailNode);
        }
    }

    // getter  
    public Node getRootNode() {
        return rootNode;
    }
}  