package cn.tcualhp.kbqa_tech_web.service.impl;

import cn.tcualhp.kbqa_tech_web.service.FilterQuestionIntentionAnalysisService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yukan
 * @description 过滤多个目的词的情况
 * @date 2020-6-10 3:20
 */

@Service
public class FilterQuestionIntentionAnalysisImpl implements FilterQuestionIntentionAnalysisService {

    @Override
    public Map questionAccurateIntentionAnalysis(Map uncleanAnalysisMap, int aimTypeNum) {
        Map resultMap = new HashMap();
        List<String> qsAimList = (List<String>) uncleanAnalysisMap.get("qsAim");
        String qsAim = qsAimList.get(aimTypeNum-1);
        resultMap.put("qs", uncleanAnalysisMap.get("qs"));
        resultMap.put("qsEntityTypeList", uncleanAnalysisMap.get("qsEntityTypeList"));
        resultMap.put("qsAim", qsAim);
        return resultMap;

    }
}
