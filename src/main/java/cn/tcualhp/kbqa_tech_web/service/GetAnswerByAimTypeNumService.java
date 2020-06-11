package cn.tcualhp.kbqa_tech_web.service;

import java.util.Map;

public interface GetAnswerByAimTypeNumService {
//    String putQuestion(Map uncleanAnalysisMap);
    Map questionAccurateIntentionAnalysis(Map uncleanAnalysisMap, int aimTypeNum);
    String questionParserG(Map analysisMap);
    String answerSeek(String cql);
}
