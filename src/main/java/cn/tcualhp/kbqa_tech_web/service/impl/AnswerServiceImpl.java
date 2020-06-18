package cn.tcualhp.kbqa_tech_web.service.impl;

import cn.tcualhp.kbqa_tech_web.kbqa.AnswerBeautifier;
import cn.tcualhp.kbqa_tech_web.kbqa.AnswerSeek;
import cn.tcualhp.kbqa_tech_web.kbqa.QuestionIntentionAnalysis;
import cn.tcualhp.kbqa_tech_web.kbqa.QuestionParserG;
import cn.tcualhp.kbqa_tech_web.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Scanner;

/**
 * @author lihepeng
 * @description 回答类实现
 * @date 2020-06-06 19:20
 **/
@Service
public class AnswerServiceImpl implements AnswerService {
    /**
     * 根据问题返回结果
     * @param query
     * @return java.lang.String
     * @author lihepeng
     * @description 根据问题返回结果
     * //TODO 多轮询问目前未设计
     * @date 19:32 2020/6/6
     **/

//    QuestionIntentionAnalysis qia = new QuestionIntentionAnalysis();
//    QuestionParserG qpg = new QuestionParserG();
//    AnswerSeek as = new AnswerSeek();

    @Override
    public String answer(String query){

//        String beautifiedAnswer = "";
//        Scanner scanner = new Scanner(System.in);
////        System.out.println("请输入问题：");
////        String qs = scanner.nextLine();
////        if (query.equals("-1")) {break;}
//        Map analysis = qia.intentionAnalysis(query);
//        System.out.printf("分析: %s\n", analysis.toString());
//        String cql = qpg.questionParser(analysis);
//        System.out.printf("CQL: %s\n", cql);
//        String answer = as.answerSeek(cql);
//        System.out.printf("result: %s\n", answer);
//        analysis.put("answer", answer);
//        beautifiedAnswer = new AnswerBeautifier().generateAnswer(analysis);
//        System.out.printf("[答案]: %s\n", beautifiedAnswer);
//        System.out.println(beautifiedAnswer);

//        scanner.close();
//        as.closeSessionAndDriver();
//        return beautifiedAnswer;
        return "";

//        return "jdakl";
    }
}
