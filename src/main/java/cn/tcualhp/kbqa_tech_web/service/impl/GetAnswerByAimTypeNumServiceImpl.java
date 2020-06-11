package cn.tcualhp.kbqa_tech_web.service.impl;

import cn.tcualhp.kbqa_tech_web.initialization.BuildCache;
import cn.tcualhp.kbqa_tech_web.service.GetAnswerByAimTypeNumService;
import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Graph;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lihepeng
 * @description 通过目的词的下标查询结果
 * @date 2020-06-11 19:24
 **/
@Service
public class GetAnswerByAimTypeNumServiceImpl implements GetAnswerByAimTypeNumService {

    private Session session = BuildCache.session;

    @Override
    public Map questionAccurateIntentionAnalysis(Map uncleanAnalysisMap, int aimTypeNum) {
        Map resultMap = new HashMap();
        List<String> qsAimList = (List<String>) uncleanAnalysisMap.get("qsAim");
        String qsAim = qsAimList.get(aimTypeNum);
        resultMap.put("qs", uncleanAnalysisMap.get("qs"));
        resultMap.put("qsEntityTypeList", uncleanAnalysisMap.get("qsEntityTypeList"));
        resultMap.put("qsAim", qsAim);
        return resultMap;
    }

    @Override
    public String answerSeek(String cql) {
        StringBuffer stringBuffer = new StringBuffer("");
        StatementResult result = session.run(cql);
        while ( result.hasNext() )
        {
            Record record = result.next();
            stringBuffer.append(record.get("answer").asString()).append(" ");
        }
        return stringBuffer.toString();
    }

    private Graph g = BuildCache.g;
    private String[] aimWords = {"aimWord_field", "aimWord_journal", "aimWord_paper", "aimWord_researcher", "aimWord_unit_organization"};
    private String[] entryWords = {"field", "journal", "paper", "researcher", "unit", "organization"};
    private String[] nodeLabels = {"Keyword", "Journal", "Paper", "Author", "Unit"};
    private String[] nodeAttributes = {"keyword", "journalName", "name", "authorName", "unit"};
    private Map<String, String> relationshipMap = new HashMap<String, String>(){{
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

    // 广度优先搜索
    private Iterator<Integer> BFPshortestPath(Graph g, int start, int end) {
        BreadthFirstPaths bfPath = new BreadthFirstPaths(g, start);
        if (!bfPath.hasPathTo(end)) return null;
        Iterator<Integer> path = bfPath.pathTo(end).iterator();
        return path;
    }

    //    四位随机字符组成的String
    private String randString() {
        StringBuffer sb = new StringBuffer("");
        Random rand = new Random();
        char a = (char) (rand.nextInt(26) + 'a');
        char b = (char) (rand.nextInt(26) + 'a');
        char c = (char) (rand.nextInt(26) + 'a');
        char d = (char) (rand.nextInt(26) + 'a');
        sb.append(a).append(b).append(c).append(d);
        return sb.toString();
    }

    private int search(String[] ss, String key) {
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

    private String aimWord2nodeLabel(String aimWord) {
        String nodeLabel = nodeLabels[search(aimWords, aimWord)];
        return nodeLabel;
    }

    private String aimWord2nodeAttribute(String aimWord) {
        String nodeAttribute = nodeAttributes[search(aimWords, aimWord)];
        return nodeAttribute;
    }

    private String entryWord2nodeLabel(String entryWord) {
        int index = search(entryWords, entryWord);
        if (index == entryWords.length-1) index--;
        String nodeLabel = nodeLabels[index];
        return nodeLabel;
    }

    private String entryWord2nodeAttribute(String entryWord) {
        int index = search(entryWords, entryWord);
        if (index == entryWords.length-1) index--;
        String nodeAttribute = nodeAttributes[index];
        return nodeAttribute;
    }

    private int getIndex(String[] ss, String s) {
        for (int i=0; i<ss.length; i++) {
            if (ss[i].equals(s)) return i;
        }
        return -1;
    }

    @Override
    public String questionParserG(Map questinAnalysis) {
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
}
