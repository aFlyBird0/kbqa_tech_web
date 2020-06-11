package cn.tcualhp.kbqa_tech_web.service;

import java.util.Map;

/**
 * @author yukan
 * @description 如果问题存在多个意图 则发问
 * @Date 2020-06-10 3:27
 */

public interface PutQuestionService {
    public String putQuestion(Map uncleanAnalysisMap);
}
