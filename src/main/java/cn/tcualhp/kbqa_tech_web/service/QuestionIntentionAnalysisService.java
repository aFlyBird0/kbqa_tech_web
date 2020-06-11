package cn.tcualhp.kbqa_tech_web.service;

import java.util.Map;

/**
 * @author yukan
 * @description 对问句做解析处理
 * @date 2020-06-10 2:50
 **/
public interface QuestionIntentionAnalysisService {
    public Map questionIntenionAnalysis(String question);
}
