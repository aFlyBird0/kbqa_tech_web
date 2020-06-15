package cn.tcualhp.kbqa_tech_web.service.impl;

import cn.tcualhp.kbqa_tech_web.initialization.BuildCache;
import cn.tcualhp.kbqa_tech_web.kbqa.AC.ACFilter;
import cn.tcualhp.kbqa_tech_web.kbqa.AnswerBeautifier;
import cn.tcualhp.kbqa_tech_web.service.AnswerMultiService;
import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Graph;
import javafx.util.Pair;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lihepeng
 * @description
 * @date 2020-06-11 16:06
 **/
@Service
public class AnswerMultiServiceImpl implements AnswerMultiService {



    public Map questionIntenionAnalysis(String qs) {

        Map<String, List<String>> entityWordMap = BuildCache.entityWordMap;
        Map<String, List<String>> aimWordMap = BuildCache.aimWordMap;
        ACFilter acFilterEntity = BuildCache.acFilterEntity;
        ACFilter acFilterAim = BuildCache.acFilterAim;

        //用一个Map保存一个问题的意图分类结果，包括qs、qsEntityTypeList、qsAim、qsType
        //即：resultMap = {qs="问题", qsEntityTypeList=[{实体词=[实体词类型1, 实体词类型2, ...]}], qsAim="问题目的"}

        //加入问题原句
        Map resultMap = new HashMap();
        resultMap.put("qs", qs);

        // 确定问题目的
//        String qsAim = null;
        List<String> qsAimList = new ArrayList<String>();
        // 通过ac树获取问题目的词 并映射到目的词类别保存在qsAimList中
        for (String aimWord : acFilterAim.meticulousFilter(acFilterAim.filter(qs))) {
            for (Map.Entry<String, List<String>> aimWordEntry : aimWordMap.entrySet()) {
                if (aimWordEntry.getValue().contains(aimWord) && !qsAimList.contains(aimWordEntry.getKey())) { //目的词类型确定 且 还未被加入qsAimList
                    qsAimList.add(aimWordEntry.getKey());
                }
            }
        }
        resultMap.put("qsAim", qsAimList);
//        }

        //过滤出问题中的实体词
        List<String> qsEntityList = acFilterEntity.meticulousFilter(acFilterEntity.filter(qs));
        //匹配每个实体词的所属类比
        List<Map<String, String>> qsEntityTypeList = new ArrayList<Map<String, String>>();
        for (String entityWord : qsEntityList) {
            Map<String, String> oneEntityTypeMap = new HashMap<String, String>();
//            List<String> typeList = new ArrayList<String>();
            for (Map.Entry<String, List<String>> entityWordEntry : entityWordMap.entrySet()) {
                if (entityWordEntry.getValue().contains(entityWord)) {
                    oneEntityTypeMap.put(entityWord, entityWordEntry.getKey());
                    break;
                }
            }
            qsEntityTypeList.add(oneEntityTypeMap);
        }
        resultMap.put("qsEntityTypeList", qsEntityTypeList);

        return resultMap;
    }
    @Override
    public Pair<Boolean, Map> first(String query){
        // 获取问题分析初步结果
        Map resultMap = questionIntenionAnalysis(query);

        // 哈希问题结果，作为查询的编号，这样比较简单，而且会话不会重复
        // 这句放到了Controller里面
//        String QID = resultMap.hashCode() + "";

        // 判断是否需要二次询问
        ArrayList<String> aimList = (ArrayList)resultMap.get("qsAim");
        Boolean reAsk = aimList.size() != 1;

        return new Pair<Boolean, Map>(reAsk, resultMap);
    }

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
        StatementResult result = BuildCache.session.run(cql);
        while ( result.hasNext() )
        {
            Record record = result.next();
            stringBuffer.append(record.get("answer").asString()).append(" ");
        }
        return stringBuffer.toString();
    }

    private Graph g = BuildCache.g;
    private static String[] aimWords = {"aimWord_field", "aimWord_journal", "aimWord_paper", "aimWord_researcher", "aimWord_unit_organization", "aimWord_project", "aimWord_patent"};
    private static String[] entryWords = {"field", "journal", "paper", "researcher", "unit", "organization", "project", "patent"};
    private static String[] nodeLabels = {"Keyword", "Journal", "Paper", "Expert", "Unit", "Project", "Patent"};
    private static String[] nodeAttributes = {"keyword", "journalName", "name", "name", "unit", "name", "name"};
    private static Map<String, String> relationshipMap = new HashMap<String, String>(){{
        put("Keyword-Paper", "About_to_Keyword");
        put("Paper-Keyword", "About_to_Keyword");
        put("Keyword-Project", "About_to_Keyword");
        put("Project-Keyword", "About_to_Keyword");
        put("Expert-Expert", "Cooperate");
        put("Paper-Journal", "Publish");
        put("Journal-Paper", "Publish");
        put("Paper-Unit", "PublishUnit");
        put("Unit-Paper", "PublishUnit");
        put("Project-Unit", "PublishUnit");
        put("Unit-Project", "PublishUnit");
        put("Patent-Unit", "PublishUnit");
        put("Unit-Patent", "PublishUnit");
        put("Expert-Unit", "WorkIN");
        put("Unit-Expert", "WorkIN");
        put("Paper-Expert", "Expert_Of");
        put("Expert-Paper", "Expert_Of");
        put("Project-Expert", "Expert_Of");
        put("Expert-Project", "Expert_Of");
        put("Patent-Expert", "Expert_Of");
        put("Expert-Patent", "Expert_Of");
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

    /**
     * 根据目的词下标与之前保存的map获取答案
     * @param aimTypeNum
     * @param uncleanAnalysisMap
     * @return java.lang.String
     * @author lihepeng
     * @description //TODO
     * @date 19:48 2020/6/11
     **/
    @Override
    public String getAnswerByAimTypeNum(int aimTypeNum, Map uncleanAnalysisMap){
        Map oneAimMap = questionAccurateIntentionAnalysis(uncleanAnalysisMap, aimTypeNum);
        String cql = questionParserG(oneAimMap);
        String answer = answerSeek(cql);
        oneAimMap.put("answer", answer);
        String beatifiedAnswer = new AnswerBeautifier().generateAnswer(oneAimMap);
        return beatifiedAnswer;
    }
}
