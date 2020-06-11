package cn.tcualhp.kbqa_tech_web.service;

import java.util.Map;

/**
 * @author yukan
 * @description 过滤多个目的词的情况
 * @date 2020-6-10 3:20
 */

public interface FilterQuestionIntentionAnalysisService {
    public Map questionAccurateIntentionAnalysis(Map uncleanAnalysisMap, int aimTypeNum);
}
