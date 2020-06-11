package cn.tcualhp.kbqa_tech_web.initialization;

import cn.tcualhp.kbqa_tech_web.kbqa.AC.ACFilter;
import cn.tcualhp.kbqa_tech_web.kbqa.AC.ACTree;
import cn.tcualhp.kbqa_tech_web.utils.ResourceFileUtil;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yukan
 * @description 构建 词典Map & AC过滤器
 * @data 2020-06-09 23:48
 */

@Component
public class BuildCache {

    public static Map<String, List<String>> entityWordMap;
    public static Map<String, List<String>> aimWordMap;
    public static ACFilter acFilterEntity;
    public static ACFilter acFilterAim;
    public static Graph g;
    public static Session session;

    private static String entityDirPath = "/entityWords";
    private static String aimDirPath = "/aimWords";
    private static String graphPath = "data/graph/Neo4jTinyG.txt";
    private Driver driver;

    // 将英文转化为文字的字典
    public static Map<String, String> aimExplanation = new HashMap<String, String>(){{
        put("aimWord_field", "领域");
        put("aimWord_field", "期刊");
        put("aimWord_paper", "论文");
        put("aimWord_researcher", "研究员");
        put("aimWord_unit_organization", "组织单位");
    }};

    /**
     * 构建实体词的 词典Map & AC过滤器
     */
    @PostConstruct
    public void entityWordCache() {
        entityWordMap = new HashMap<String, List<String>>();
        String[] words = readtxtFile(entityDirPath, entityWordMap);
        ACTree acTree = new ACTree(words);
        acFilterEntity = new ACFilter(acTree);
    }

    /**
     * 构建目的词的 词典Map & AC过滤器
     */
    @PostConstruct
    public void aimWordCache() {
        aimWordMap = new HashMap<String, List<String>>();
        String[] words = readtxtFile(aimDirPath, aimWordMap);
        ACTree acTree = new ACTree(words);
        acFilterAim = new ACFilter(acTree);
    }

    /**
     * 构建映射neo4j数据库结构的无向图
     */
    @PostConstruct
    public void neo4j2TinyG() {
//        In in = new In(new File(txtPath));
        In in = new In(ResourceFileUtil.getResourceFile(graphPath));
        g = new Graph(in);
        System.out.println(graphPath);
        System.out.println(g.toString());
    }

    /**
     * 建立neo4j会话
     */
    @PostConstruct
    public void connectNeo4j(){
        // 远程仓库
        driver = GraphDatabase.driver( "bolt://120.26.175.63:7687", AuthTokens.basic( "neo4j", "Neo4j" ) );
        // 本地仓库
//        driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "Neo4j3350" ) );
        session = driver.session();
        System.out.println("session: " + session);
    }

    /**
     * 关闭neo4j会话
     */
    @PreDestroy
    public void closeNeoconj() {
        session.close();
        driver.close();
        System.out.println("session 与 driver 已关闭");
    }




    /**
     * 读取txt文件中的实体词和目的词，用于构建ac树
     * @param dirName
     * @param wordsMap
     * @return
     */
    static String[] readtxtFile(String dirName, Map<String, List<String>> wordsMap) {
        // 引用工具类 读取文件
        File dir = ResourceFileUtil.getResourceFile("data/" + dirName);
        if (!dir.exists()) {
            System.out.println("文件夹不存在");
            System.exit(0);
        }
        File[] txtFiles = dir.listFiles();
        // 读入文件中的所有实体 Map<String, List<String>>={实体类型=[实体1, 实体2, 实体3, ...]}
//        wordsMap = new HashMap<String, List<String>>();
        List<String> allWordsList = new ArrayList<String>(); //包含所有实体的list 用于构造actree
        for (File txtFile : txtFiles) {
            List<String> wordsList = new ArrayList<String>();
            try {
                BufferedReader bfReader = new BufferedReader(new FileReader(txtFile));
                String word;
                while ((word = bfReader.readLine()) != null)
                    wordsList.add(word);
                bfReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String wordsName = txtFile.getName().substring(0, txtFile.getName().length()-4);
            wordsMap.put(wordsName, wordsList);
            allWordsList.addAll(wordsList);
            // 控制台输出 start
            System.out.printf("%-10s%-30s\t%s\n", wordsList.size(),wordsName,wordsList.toString());
            // 控制台输出 end
        }
        // 控制台输出 start
        System.out.printf("%-10s%-30s\t%s\n", allWordsList.size(),"summary",allWordsList.toString());
        // 控制台输出 end
        String[] words = allWordsList.toArray(new String[allWordsList.size()]); // 将包含所有实体的list转化为String[]

        return words;
    }
}
