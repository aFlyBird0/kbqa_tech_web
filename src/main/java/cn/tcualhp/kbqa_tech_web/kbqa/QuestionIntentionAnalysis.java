package cn.tcualhp.kbqa_tech_web.kbqa;



import cn.tcualhp.kbqa_tech_web.kbqa.AC.ACFilter;
import cn.tcualhp.kbqa_tech_web.kbqa.AC.ACTree;
import cn.tcualhp.kbqa_tech_web.utils.ResourceFileUtil;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class QuestionIntentionAnalysis {

    private static String dirNameEntity = "/entityWords";
    private static String dirNameAim = "/aimWords";
    private ACTree acTreeEntity = null;
    private ACTree acTreeAim = null;
    private ACFilter acFilterEntity = null;
    private ACFilter acFilterAim = null;
    private Map<String, List<String>> entityWordMap = null;
    private Map<String, List<String>> aimWordMap = null;

    public QuestionIntentionAnalysis() {
        // 构建实体词ac树 & ac过滤器
        entityWordMap = new HashMap<String, List<String>>();
        String[] entityWords = readtxtFile(dirNameEntity, entityWordMap);
        this.acTreeEntity = new ACTree(entityWords);
        this.acFilterEntity = new ACFilter(acTreeEntity);
        // 构建目的词ac树 & ac过滤器
        aimWordMap = new HashMap<String, List<String>>();
        String[] aimWords = readtxtFile(dirNameAim, aimWordMap);
        this.acTreeAim = new ACTree(aimWords);
        this.acFilterAim = new ACFilter(acTreeAim);
    }

    /**
     * 读取txt文件中的实体词和目的词，用于构建ac树
     * @param dirName
     * @param wordsMap
     * @return
     */
    static String[] readtxtFile(String dirName, Map<String, List<String>> wordsMap) {
        // 引用工具类 读取文件
//        File dir = ResourceFileUtil.getResourceFile("data/" + dirName);
//        if (!dir.exists()) {
//            System.out.println("文件夹不存在");
//            System.exit(0);
//        }
//        File[] txtFiles = dir.listFiles();
//        // 读入文件中的所有实体 Map<String, List<String>>={实体类型=[实体1, 实体2, 实体3, ...]}
////        wordsMap = new HashMap<String, List<String>>();
//        List<String> allWordsList = new ArrayList<String>(); //包含所有实体的list 用于构造actree
//        for (File txtFile : txtFiles) {
//            List<String> wordsList = new ArrayList<String>();
//            try {
//                BufferedReader bfReader = new BufferedReader(new FileReader(txtFile));
//                String word;
//                while ((word = bfReader.readLine()) != null)
//                    wordsList.add(word);
//                bfReader.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String wordsName = txtFile.getName().substring(0, txtFile.getName().length()-4);
//            wordsMap.put(wordsName, wordsList);
//            allWordsList.addAll(wordsList);
//            // 控制台输出 start
//            System.out.printf("%-10s%-30s\t%s\n", wordsList.size(),wordsName,wordsList.toString());
//            // 控制台输出 end
//        }
        List<String> allWordsList = new ArrayList<String>(); //包含所有实体的list 用于构造actree

        try {
            Resource[] resources = ResourceFileUtil.getDirResources("/data" + dirName);
            for (Resource resource: resources){
                // 读入文件中的所有实体 Map<String, List<String>>={实体类型=[实体1, 实体2, 实体3, ...]}
//        wordsMap = new HashMap<String, List<String>>();
                List<String> wordsList = new ArrayList<String>();
                InputStream inputStream = resource.getInputStream();
                String word;
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                while ((word = br.readLine()) != null){
                    wordsList.add(word);
                }
                String filename = resource.getFilename();
                String wordsName = resource.getFilename().substring(0, resource.getFilename().length()-4);
                wordsMap.put(wordsName, wordsList);
                allWordsList.addAll(wordsList);
                // 控制台输出 start
                System.out.printf("%-10s%-30s\t%s\n", wordsList.size(),wordsName,wordsList.toString());
                // 控制台输出 end

            }
        }
        catch (IOException e){
            System.out.println("读取词典错误");
            e.printStackTrace();
        }
        // 控制台输出 start
        System.out.printf("%-10s%-30s\t%s\n", allWordsList.size(),"summary",allWordsList.toString());
        // 控制台输出 end
        String[] words = allWordsList.toArray(new String[allWordsList.size()]); // 将包含所有实体的list转化为String[]

        return words;
    }

    /**
     * 抓取问题的实体词和目的词，当目的词不确定时，通过交互的方式询问用户。
     * @param qs
     * @return
     */
    public Map intentionAnalysis (String qs) {
        //用一个Map保存一个问题的意图分类结果，包括qs、qsEntityTypeList、qsAim、qsType
        //即：resultMap = {qs="问题", qsEntityTypeList=[{实体词=[实体词类型1, 实体词类型2, ...]}], qsAim="问题目的"}

        //加入问题原句
        Map resultMap = new HashMap();
        resultMap.put("qs", qs);

        // 确定问题目的
        String qsAim = null;
        List<String> qsAimList = new ArrayList<String>();
        // 通过ac树获取问题目的词 并映射到目的词类别保存在qsAimList中
        for (String aimWord : acFilterAim.meticulousFilter(acFilterAim.filter(qs))) {
            for (Map.Entry<String, List<String>> aimWordEntry : aimWordMap.entrySet()) {
                if (aimWordEntry.getValue().contains(aimWord) && !qsAimList.contains(aimWordEntry.getKey())) {
                    qsAimList.add(aimWordEntry.getKey());
                }
            }
        }
        // 如果qsAim只有一个，认为qs目的明确
        if (qsAimList.size() == 1) {
            qsAim = qsAimList.get(0);
            resultMap.put("qsAim", qsAim);
        }
        // 如果qsAim有多个 或 零个，认为qs目的不明确，系统向用户询问
        else {
            Map<String, String> aimExplanation = new HashMap<String, String>(){{
                put("aimWord_field", "领域");
                put("aimWord_field", "期刊");
                put("aimWord_paper", "论文");
                put("aimWord_researcher", "研究员");
                put("aimWord_unit_organization", "组织单位");
            }};
            StringBuffer stringBuffer = new StringBuffer("你是查找");
            // 目的词多余一个
            if (qsAimList.size() > 1){
                int i = 1;
                for (String aimWord : qsAimList) {
                    // 提示可能的目的词，供选择，同时给选择标号，方便输入
                    stringBuffer.append(aimExplanation.get(aimWord)).append("(" + i +"),");
                    i += 1;
                }
            }
            // 目的词为零个
            else {
                String allaim = aimExplanation.values().toString();
                stringBuffer.append(allaim.substring(1, allaim.length()));
            }
            stringBuffer.deleteCharAt(stringBuffer.length()-1).append("中的哪个呢？");
            System.out.println(stringBuffer);
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine();
            String trueAim;
            // 如果输入的是序号，转化成目的字符串
            try {
                trueAim = aimExplanation.get(qsAimList.get(Integer.parseInt(answer)-1));
            }
            catch (NumberFormatException e){
                trueAim = answer;
            }
            while(!aimExplanation.containsValue(trueAim)) {
                System.out.println(stringBuffer+" (请提示回答)");
                answer = scanner.nextLine();
                try {
                    trueAim = aimExplanation.get(qsAimList.get(Integer.parseInt(answer)-1));
                }
                catch (NumberFormatException e){
                    trueAim = answer;
                }
            }
//            scanner.close();
            for (Map.Entry<String, String> entityAimExplaination : aimExplanation.entrySet()) {
                if (entityAimExplaination.getValue().equals(trueAim)) {
                    qsAim = entityAimExplaination.getKey();
                    break;
                }
            }
            resultMap.put("qsAim", qsAim);
        }

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

    public static void main(String[] args) {
//        String str = "河南广播电视大学刘传教授写的有关石墨烯的论文？";
//        String str = "何年";
        Scanner scanner = new Scanner(System.in);
        QuestionIntentionAnalysis qia = new QuestionIntentionAnalysis();
        while(true) {
            System.out.println("请输入问题：");
//            while(!scanner.hasNext());
            String qs = scanner.nextLine();
            if (qs.equals("-1")) {break;}
//            qia.intentionAnalysis(qs);
            System.out.println(qia.intentionAnalysis(qs));
        }
        scanner.close();
    }
}
