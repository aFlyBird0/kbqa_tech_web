package cn.tcualhp.kbqa_tech_web.kbqa.AC;//import Node;
//import ACTree;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ACFilter {

    private ACTree tree;

    public ACFilter(){};

    public ACFilter(ACTree tree) {
        this.tree = tree;
    }

    public void setTree(ACTree tree) {
        this.tree = tree;
    }

    /**
     * 过滤
     *
     * @param word
     * @return
     */
    public List<String> filter(String word) {
        List<String> result = new ArrayList<String>();
        if (StringUtils.isEmpty(word)) {
            return result;
        }
        char[] words = word.toLowerCase().toCharArray();
        Node curNode = tree.getRootNode();
        Node subNode;
        Character c;
        for (int i = 0; i < words.length; i++) {
            c = words[i];
            subNode = curNode.getSubNode(c);
            // 当前节点非根节点且当前节点的后子节点为空
            while (subNode == null && curNode != tree.getRootNode()) {
                // 跳向转跳节点
                curNode = curNode.getFailNode();
                subNode = curNode.getSubNode(c);
            }
            // 当前节点为根节点且当前节点的后子节点为空
            if (subNode != null) {
                curNode = subNode;
            }
            // 当前节点为终结节点
            if (curNode.isTerminal()) {
                int pos = i - curNode.getLevel() + 1;
                result.add(word.substring(pos, i+1));
            }
        }
        return result;
    }


    /**
     * 更加细致的过滤
     * 去除具有被包含关系的实体词  eg："他们"和“他们的” 经过细致过滤 “他们”被滤除
     * @param entityWordList
     * @return
     */
    public List<String> meticulousFilter(List<String> entityWordList){
        Collections.sort(entityWordList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.length() > o2.length()){
                    return -1;
                }
                else {
                    return 1;
                }
            }
        });
//        System.out.println(entityWordList);
        for (int i=0; i<entityWordList.size(); i++) {
            List<String> removeList = new ArrayList<String>();
            for (int j=i+1; j<entityWordList.size(); j++) {
                if (entityWordList.get(i).indexOf(entityWordList.get(j)) != -1) {
                    removeList.add(entityWordList.get(j));
                }
            }
            entityWordList.removeAll(removeList);
        }
        return entityWordList;
    }

}
