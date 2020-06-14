package cn.tcualhp.kbqa_tech_web.kbqa;

import cn.tcualhp.kbqa_tech_web.utils.ResourceFileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lihepeng
 * @description 回答美化器
 * @date 2020-05-25 23:16
 **/
public class AnswerBeautifier {


    private static String configPath = "data/config/answerBeautifySingle.json";

    /***
     * 获取答案美化器（单）配置
     * @param
     * @return java.util.List<java.util.HashMap<java.lang.String,java.lang.String>>
     * @author lihepeng
     * @description
     * @date 17:52 2020/5/27
     **/
    private List<HashMap<String, String>> getAnswerConfigSingle(){
        // 配置的String形式
        String configString = "";
//        File file = ResourceFileUtil.getResourceFile(configPath);
//        BufferedReader reader = null;
//        try {
////            System.out.println("以行为单位读取文件内容，一次读一整行：");
//            reader = new BufferedReader(new FileReader(file));
//            String tempString = null;
//            int line = 1;
//            // 一次读入一行，直到读入null为文件结束
//            while ((tempString = reader.readLine()) != null) {
//                // 显示行号
////                System.out.println("line " + line + ": " + tempString);
//                configString += tempString;
//                line++;
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//            }
//        }
        try {
            configString = ResourceFileUtil.getResourceString(configPath);
        }
        catch (Exception e){
            System.out.println("读取美化器配置失败");
            e.printStackTrace();
        }
        // 待返回的配置（数组，字典）
        List answerConfig = JSON.parseArray(configString, HashMap.class);
        return (List<HashMap<String, String>>)answerConfig;
    }

    // 生成实体、目的和答案词典
    // 注意目前只针对一个实体的问题
    private HashMap<String, String> getSingleEntityAimAnswer(Map analysis){
        HashMap<String, String> entityAimAnswerMap = new HashMap<String, String>(2);
        entityAimAnswerMap.put("answer", (String)analysis.get("answer"));
        entityAimAnswerMap.put("aim", (String)analysis.get("qsAim"));
        ArrayList entities = (ArrayList)analysis.get("qsEntityTypeList");
        Map<String, String> entityMap = (Map)entities.get(0);
        // 获取实体属性和值
        for(Map.Entry<String, String> entry:entityMap.entrySet()){
            entityAimAnswerMap.put("entityType", entry.getValue());
            entityAimAnswerMap.put("entityValue", entry.getKey());
        }
//        System.out.println(entityAimAnswerMap);
        return entityAimAnswerMap;
    }

    /**
     * 生成单实体单目的的美化答案
     * @param entityAimAnswerMap 实体、目的、答案信息
     * @param answerConfig 美化器配置文件的列表字典
     * @return java.lang.String
     * @author lihepeng
     * @description
     * @date 0:48 2020/5/26
     **/
    public static String generateAnswerSingle(HashMap<String, String> entityAimAnswerMap, List<HashMap<String, String>> answerConfig) {
        String entityType = (String)entityAimAnswerMap.get("entityType");
        String aim = (String)entityAimAnswerMap.get("aim");
        String entityValue = (String)entityAimAnswerMap.get("entityValue");
        String pureAnswer = (String)entityAimAnswerMap.get("answer");
        String beautifiedAnswer = pureAnswer;
        //根据配置信息美化回答
        for (HashMap<String, String> oneConfig: answerConfig){
            // 配置中筛选aim和实体类型一样的
            if (oneConfig.getOrDefault("aim", "").equals(aim)
                    && oneConfig.getOrDefault("entityType", "").equals(entityType)){
                // 替换答案模板中的关键信息
                beautifiedAnswer = oneConfig.getOrDefault("answerModel", "")
                        .replace("#{entityValue}", entityValue).replace("#{pureAnswer}", pureAnswer);
            }
        }

        return beautifiedAnswer;
    }

    private String generateAnswerMulti(Map analysis) {
        // 如果实体超过两个，就不处理，直接用原答案
        String beautifiedAnswer = (String)analysis.get("answer");
        return beautifiedAnswer;
    }

    /**
     * 生成答案
     * @param analysis
     * @return java.lang.String
     * @author lihepeng
     * @description
     * @date 0:48 2020/5/26
     **/
    public String generateAnswer(Map analysis){
//        System.out.println(analysis);
        ArrayList entities = (ArrayList)analysis.get("qsEntityTypeList");
        if (entities.size() > 1){
            return generateAnswerMulti(analysis);
        }
        else {
            HashMap<String, String> entityAimAnswerMap = getSingleEntityAimAnswer(analysis);
            return generateAnswerSingle(entityAimAnswerMap, getAnswerConfigSingle());
        }
    }
}
