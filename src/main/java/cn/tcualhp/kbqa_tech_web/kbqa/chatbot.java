package cn.tcualhp.kbqa_tech_web.kbqa;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class chatbot {

    public static void main(String[] args) {
        QuestionIntentionAnalysis4newDatabase qia = new QuestionIntentionAnalysis4newDatabase();
        QuestionParserG4newDatabase qpg = new QuestionParserG4newDatabase();
        AnswerSeek as = new AnswerSeek();

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("请输入问题：");
            String qs = scanner.nextLine();
            if (qs.equals("-1")) {break;}
            Map analysis = qia.intentionAnalysis(qs);
            System.out.printf("分析: %s\n", analysis.toString());
            String cql = qpg.questionParser(analysis);
            System.out.printf("CQL: %s\n", cql);
            String answer = as.answerSeek(cql);
            System.out.printf("result: %s\n", answer);
            analysis.put("answer", answer);
            String beautifiedAnswer = new AnswerBeautifier().generateAnswer(analysis);
            System.out.printf("[答案]: %s\n", beautifiedAnswer);
//            System.out.println(beautifiedAnswer);

        }
        scanner.close();

        as.session.close();
        as.driver.close();
    }

}
