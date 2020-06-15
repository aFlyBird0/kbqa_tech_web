package cn.tcualhp.kbqa_tech_web.service.impl;

import cn.tcualhp.kbqa_tech_web.service.PutQuestionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author yukan
 * @description 如果问题存在多个意图 则发问
 * @Date 2020-06-10 3:27
 */

@Service
public class PutQuestionImpl implements PutQuestionService{

    private Map<String, String> aimExplanation = new HashMap<String, String>(){{
        put("aimWord_field", "领域");
        put("aimWord_journal", "期刊");
        put("aimWord_paper", "论文");
        put("aimWord_patent", "专利");
        put("aimWord_project", "项目");
        put("aimWord_researcher", "研究员");
        put("aimWord_unit_organization", "组织单位");
    }};

    @Override
    public String putQuestion(Map uncleanAnalysisMap) {
        List<String> qsAimList = (List<String>) uncleanAnalysisMap.get("qsAim");
        String result = null;
        if (qsAimList.size() == 1) {
            result = "ok";
        }
        else {
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
            result = stringBuffer.toString();
        }
        return result;
    }
}
