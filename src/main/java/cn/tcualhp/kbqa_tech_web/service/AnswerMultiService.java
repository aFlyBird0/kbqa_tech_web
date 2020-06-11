package cn.tcualhp.kbqa_tech_web.service;

import javafx.util.Pair;

import java.util.Map;

/**
 * @author lihepeng
 * @description 多轮问答
 * @date 2020-06-11 16:05
 **/
public interface AnswerMultiService {
    public Pair<Boolean, Map> first(String query);
    Map questionAccurateIntentionAnalysis(Map uncleanAnalysisMap, int aimTypeNum);
    String questionParserG(Map analysisMap);
    String answerSeek(String cql);
    String getAnswerByAimTypeNum(int aimTypeNum, Map uncleanAnalysisMap);
}
