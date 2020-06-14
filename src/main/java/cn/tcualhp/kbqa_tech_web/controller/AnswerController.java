package cn.tcualhp.kbqa_tech_web.controller;

import cn.tcualhp.kbqa_tech_web.common.Response;
import cn.tcualhp.kbqa_tech_web.initialization.BuildCache;
import cn.tcualhp.kbqa_tech_web.kbqa.MidResultMap;
import cn.tcualhp.kbqa_tech_web.service.*;
import cn.tcualhp.kbqa_tech_web.service.impl.AnswerMultiServiceImpl;
import javafx.util.Pair;
import org.neo4j.cypher.internal.frontend.v2_3.ast.Return;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lihepeng
 * @description 问答
 * @date 2020-06-06 19:07
 **/
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/QA")
public class AnswerController {

    @Autowired
    AnswerService answerService;

    @Autowired
    QuestionIntentionAnalysisService questionIntentionAnalysisService;

    @Autowired
    PutQuestionService putQuestionService;

    @Autowired
    FilterQuestionIntentionAnalysisService filterQuestionIntentionAnalysisService;

    @Autowired
    AnswerMultiService answerMultiService;

    MidResultMap midResultMap = new MidResultMap();

//    @PostMapping(value = "/first")
//    public Response firstAsk(@RequestBody Map<String, String> map){
//        String query = map.getOrDefault("query", "");
//        if (query.isEmpty()){
//            return new Response().failure("查询不能为空");
//        }
//        String answer = answerService.answer(query);
//        return new Response().success(answer);
//    }

    @PostMapping(value = "/first")
    public Response firstAsk(@RequestBody Map<String, String> map){
        String query = map.getOrDefault("query", "");
        if (query.isEmpty()){
            return new Response().failure("查询不能为空");
        }
        Pair<Boolean, Map> result = answerMultiService.first(query);
        HashMap<String, Object> answerMap = new HashMap<>(2);
        // 不需要二次访问
        if (!result.getKey()){
            // 返回前端答案
            answerMap.put("result", "ok");
            String answerString = answerMultiService.getAnswerByAimTypeNum(0, result.getValue());
            answerMap.put("answer", answerString);
            return new Response().success(answerMap);
        }
        HashMap<String, Object> midResMap = (HashMap<String, Object>)result.getValue();
        // 哈希问题结果，作为查询的编号，这样比较简单，而且会话不会重复
        String QID = midResMap.hashCode() + "";
        // 告诉前端需要再次请求
        answerMap.put("result", "needReAsk");
        answerMap.put("QID", QID);
        ArrayList<String> aimListEN = (ArrayList)midResMap.get("qsAim");
        ArrayList<String> aimListCN = new ArrayList<>();
        for (String aimEn: aimListEN){
            aimListCN.add(BuildCache.aimExplanation.get(aimEn));
        }
        answerMap.put("aimList", aimListCN);
        // 保存中间结果
        midResultMap.addResult(QID, (HashMap)midResMap);
        return new Response().success(answerMap);
    }

    @PostMapping(value = "/second")
    public Response second(@RequestBody Map<String, String> map){
        String aimtypeNum = map.getOrDefault("aimTypeNum", "");
        String QID = map.getOrDefault("QID", "");
        if (aimtypeNum.isEmpty() || QID.isEmpty()){
            return new Response().failure("目的序号和问题序号不能为空");
        }
        // 注意这里的是int型，之前忘记转换出现过错误
        Integer aimTypeNumInt = Integer.parseInt(aimtypeNum);
        HashMap<String, String> answerMap = new HashMap<>(2);
        String answerString = answerMultiService.getAnswerByAimTypeNum(aimTypeNumInt, midResultMap.getMidResult(QID));
        if (answerString == null){
            return new Response().failure("问题下标错误");
        }
        answerMap.put("result", "ok");
        answerMap.put("answer", answerString);
        return new Response().success(answerMap);
    }


}
