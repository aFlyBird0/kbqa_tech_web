package cn.tcualhp.kbqa_tech_web.service.impl;


import cn.tcualhp.kbqa_tech_web.initialization.BuildCache;
import cn.tcualhp.kbqa_tech_web.kbqa.AC.ACFilter;
import cn.tcualhp.kbqa_tech_web.service.QuestionIntentionAnalysisService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yukan
 * @description 对问句做解析处理
 * @date 2020-06-10 2:50
 */

@Service
public class QuestionIntentionAnalysisImpl implements QuestionIntentionAnalysisService {

    private static Map<String, List<String>> entityWordMap = BuildCache.entityWordMap;
    private static Map<String, List<String>> aimWordMap = BuildCache.aimWordMap;
    private static ACFilter acFilterEntity = BuildCache.acFilterEntity;
    private static ACFilter acFilterAim = BuildCache.acFilterAim;

    @Override
    public Map questionIntenionAnalysis(String qs) {

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
//        // 如果qsAim只有一个，认为qs目的明确
//        if (qsAimList.size() == 1) {
//            qsAim = qsAimList.get(0);
//            resultMap.put("qsAim", qsAim);
//        }
//        // 如果qsAim有多个 或 零个，认为qs目的不明确，系统向用户询问
//        else {
//            Map<String, String> aimExplanation = new HashMap<String, String>(){{
//                put("aimWord_field", "领域");
//                put("aimWord_field", "期刊");
//                put("aimWord_paper", "论文");
//                put("aimWord_researcher", "研究员");
//                put("aimWord_unit_organization", "组织单位");
//            }};
//            StringBuffer stringBuffer = new StringBuffer("你是查找");
//            // 目的词多余一个
//            if (qsAimList.size() > 1){
//                int i = 1;
//                for (String aimWord : qsAimList) {
//                    // 提示可能的目的词，供选择，同时给选择标号，方便输入
//                    stringBuffer.append(aimExplanation.get(aimWord)).append("(" + i +"),");
//                    i += 1;
//                }
//            }
//            // 目的词为零个
//            else {
//                String allaim = aimExplanation.values().toString();
//                stringBuffer.append(allaim.substring(1, allaim.length()));
//            }
//            stringBuffer.deleteCharAt(stringBuffer.length()-1).append("中的哪个呢？");
//            System.out.println(stringBuffer);
//            Scanner scanner = new Scanner(System.in);
//            String answer = scanner.nextLine();
//            String trueAim;
//            // 如果输入的是序号，转化成目的字符串
//            try {
//                trueAim = aimExplanation.get(qsAimList.get(Integer.parseInt(answer)-1));
//            }
//            catch (NumberFormatException e){
//                trueAim = answer;
//            }
//            while(!aimExplanation.containsValue(trueAim)) {
//                System.out.println(stringBuffer+" (请提示回答)");
//                answer = scanner.nextLine();
//                try {
//                    trueAim = aimExplanation.get(qsAimList.get(Integer.parseInt(answer)-1));
//                }
//                catch (NumberFormatException e){
//                    trueAim = answer;
//                }
//            }
////            scanner.close();
//            for (Map.Entry<String, String> entityAimExplaination : aimExplanation.entrySet()) {
//                if (entityAimExplaination.getValue().equals(trueAim)) {
//                    qsAim = entityAimExplaination.getKey();
//                    break;
//                }
//            }
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
}