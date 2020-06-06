package cn.tcualhp.kbqa_tech_web.kbqa;

import cn.tcualhp.kbqa_tech_web.utils.ResourceFileUtil;
import edu.princeton.cs.algs4.*;

import java.io.File;
import java.util.*;


public class QuestionParserG {

    private Graph g;
//    private static String txtPath = System.getProperty("user.dir") + "/src/main/java/Neo4jTinyG.txt";
    private static String txtPath = "data/graph/Neo4jTinyG.txt";
    private static String[] aimWords = {"aimWord_field", "aimWord_journal", "aimWord_paper", "aimWord_researcher", "aimWord_unit_organization"};
    private static String[] entryWords = {"field", "journal", "paper", "researcher", "unit", "organization"};
    private static String[] nodeLabels = {"Keyword", "Journal", "Paper", "Author", "Unit"};
    private static String[] nodeAttributes = {"keyword", "journalName", "name", "authorName", "unit"};
    private static Map<String, String> relationshipMap = new HashMap<String, String>(){{
        put("Keyword-Paper", "About_to_Keyword");
        put("Paper-Keyword", "About_to_Keyword");
        put("Author-Author", "Author_Coperate");
        put("Paper-Journal", "Publish");
        put("Journal-Paper", "Publish");
        put("Paper-Unit", "PublishUnit");
        put("Unit-Paper", "PublishUnit");
        put("Author-Unit", "WorkIN");
        put("Unit-Author", "WorkIN");
        put("Paper-Author", "Author_Of");
        put("Author-Paper", "Author_Of");
    }};

    public QuestionParserG(){
        g = neo4j2TinyG(txtPath);
    }

    // 构建neo4j数据库的结构映射图
    Graph neo4j2TinyG(String txtPath) {
//        In in = new In(new File(txtPath));
        In in = new In(ResourceFileUtil.getResourceFile(txtPath));
        Graph g = new Graph(in);
        System.out.println(txtPath);
        System.out.println(g.toString());
        return g;
    }

    // 广度优先搜索
    Iterator<Integer> BFPshortestPath(Graph g, int start, int end) {
        BreadthFirstPaths bfPath = new BreadthFirstPaths(g, start);
        if (!bfPath.hasPathTo(end)) return null;
        Iterator<Integer> path = bfPath.pathTo(end).iterator();
        return path;
    }

//    四位随机字符组成的String
    static String randString() {
        StringBuffer sb = new StringBuffer("");
        Random rand = new Random();
        char a = (char) (rand.nextInt(26) + 'a');
        char b = (char) (rand.nextInt(26) + 'a');
        char c = (char) (rand.nextInt(26) + 'a');
        char d = (char) (rand.nextInt(26) + 'a');
        sb.append(a).append(b).append(c).append(d);
        return sb.toString();
    }

    static int search(String[] ss, String key) {
        int i=0;
        for (String s:ss) {
            if (key.equals(s)) {
                return i;
            }
            else {
                i++;
            }
        }
        return -1;
    }

    static String aimWord2nodeLabel(String aimWord) {
        String nodeLabel = nodeLabels[search(aimWords, aimWord)];
        return nodeLabel;
    }

    static String aimWord2nodeAttribute(String aimWord) {
        String nodeAttribute = nodeAttributes[search(aimWords, aimWord)];
        return nodeAttribute;
    }

    static String entryWord2nodeLabel(String entryWord) {
        int index = search(entryWords, entryWord);
        if (index == entryWords.length-1) index--;
        String nodeLabel = nodeLabels[index];
        return nodeLabel;
    }

    static String entryWord2nodeAttribute(String entryWord) {
        int index = search(entryWords, entryWord);
        if (index == entryWords.length-1) index--;
        String nodeAttribute = nodeAttributes[index];
        return nodeAttribute;
    }

    static int getIndex(String[] ss, String s) {
        for (int i=0; i<ss.length; i++) {
            if (ss[i].equals(s)) return i;
        }
        return -1;
    }

    public String questionParser(Map questinAnalysis) {

        //构建CQL查询语句
        StringBuffer cql = new StringBuffer("");
        cql.append("MATCH ");
        // 获取目标标签以及目标属性
        String aimNodeLabel = aimWord2nodeLabel( (String) questinAnalysis.get("qsAim") );
        String aimNodeAttrbute = aimWord2nodeAttribute( (String) questinAnalysis.get("qsAim") );
        // 获取问题的限定词
        List<Map<String, String>> entryWordList = (List<Map<String, String>>) questinAnalysis.get("qsEntityTypeList");
        int i=0;
        for (Map<String, String> map : entryWordList) {
            String entryWord = (String) map.keySet().toArray()[0];
            String entryNodeLabel = entryWord2nodeLabel( (String) map.values().toArray()[0] );
            String entryNodeAttrbute = entryWord2nodeAttribute( (String) map.values().toArray()[0] );
            // 如果限定词与目的词仅是一条关系 直接构建CQL MATCH语句
            if (relationshipMap.containsKey( (aimNodeLabel+"-"+entryNodeLabel) )) {
                String relationship = relationshipMap.get(aimNodeLabel+"-"+entryNodeLabel);
                char c = (char)('a' + i);
                cql.append("(aim:").append(aimNodeLabel).append(")");
                cql.append("-");
                cql.append("[").append(randString()).append(":").append(relationship).append("]");
                cql.append("-");
                cql.append("(").append(randString()).append(":").append(entryNodeLabel).append("{").append(entryNodeAttrbute).append(":\"").append(entryWord).append("\"})");
                cql.append(",");
            }
            // 限定词与目的词之间存在多跳关系 通过BFS找到限定词与目的词之间的最短路径后 构建CQL MATCH语句
            else {
                int start = getIndex(nodeLabels, aimNodeLabel);
                int end = getIndex(nodeLabels, entryNodeLabel);
                Iterator<Integer> iterator = BFPshortestPath(g, start, end);
                List<Integer> path = new ArrayList<Integer>();
                while (iterator.hasNext()) {
                    path.add(iterator.next());
                }
                String preAimNodeLabel = nodeLabels[path.get(0)];
                String preEntryNodeLabel = nodeLabels[path.get(1)];
                String preRelationship = relationshipMap.get(preAimNodeLabel+"-"+preEntryNodeLabel);
                cql.append("(aim:").append(aimNodeLabel).append(")");
                cql.append("-");
                cql.append("[").append(randString()).append(":").append(preRelationship).append("]");
                cql.append("-");
                for (int index=1; index<path.size()-1; index++) {
                    preAimNodeLabel = nodeLabels[path.get(index)];
                    preEntryNodeLabel = nodeLabels[path.get(index+1)];
                    preRelationship = relationshipMap.get(preAimNodeLabel+"-"+preEntryNodeLabel);
                    cql.append("(").append(randString()).append(":").append(preAimNodeLabel).append(")");
                    cql.append("-");
                    cql.append("[").append(randString()).append(":").append(preRelationship).append("]");
                    cql.append("-");
                }
                cql.append("(").append(randString()).append(":").append(entryNodeLabel).append("{").append(entryNodeAttrbute).append(":\"").append(entryWord).append("\"})");
                cql.append(",");
            }
        }
        cql.deleteCharAt(cql.length()-1).append(" ");
        cql.append("return aim.").append(aimNodeAttrbute).append(" AS answer");
        return cql.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        QuestionIntentionAnalysis qia = new QuestionIntentionAnalysis();
        QuestionParserG qp = new QuestionParserG();
        while(true) {
            System.out.println("请输入问题：");
//            while(!scanner.hasNext());
            String qs = scanner.nextLine();
            if (qs.equals("-1")) {break;}
//            qia.intentionAnalysis(qs);
//            System.out.println(qia.intentionAnalysis(qs));
            System.out.println(qp.questionParser(qia.intentionAnalysis(qs)));
        }
        scanner.close();
    }

}
