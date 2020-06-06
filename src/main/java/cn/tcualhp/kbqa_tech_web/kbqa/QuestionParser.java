package cn.tcualhp.kbqa_tech_web.kbqa;
import cn.tcualhp.kbqa_tech_web.kbqa.QuestionIntentionAnalysis;

import java.util.*;

public class QuestionParser {

    private static String[] aimWords = {"aimWord_field", "aimWord_journal", "aimWord_paper", "aimWord_researcher", "aimWord_unit_organization"};
    private static String[] entryWords = {"field", "journal", "paper", "researcher", "unit", "organization"};
    private static String[] nodeLabels = {"Keyword", "Journal", "Paper", "Author", "Unit"};
    private static String[] nodeAttributes = {"keyword", "journalName", "name", "authorName", "unit"};
    private static Map<String, String> relationshipMap = new HashMap<String, String>(){{
        put("Keyword-Paper", "[ra:About_to_Keyword]");
        put("Paper-Keyword", "[ra:About_to_Keyword]");
        put("Author-Author", "[rb:Author_Coperate]");
        put("Paper-Journal", "[rc:Publish]");
        put("Journal-Paper", "[rc:Publish]");
        put("Paper-Unit", "[rd:PublishUnit]");
        put("Unit-Paper", "[rd:PublishUnit]");
        put("Author-Unit", "[re:WorkIN]");
        put("Unit-Author", "[re:WorkIN]");
        put("Paper-Author", "[rf1:Author_Of]");
        put("Author-Paper", "[rf1:Author_Of]");
    }};

    static int search(String[] ss, String key) {
        int i=0;
        for (String s:ss) {
            if (key.equals(s)) {
                return i;
            }
            else {
                i++;
            }
        }
        return -1;
    }

    static String aimWord2nodeLabel(String aimWord) {
        String nodeLabel = nodeLabels[search(aimWords, aimWord)];
        return nodeLabel;
    }

    static String aimWord2nodeAttribute(String aimWord) {
        String nodeAttribute = nodeAttributes[search(aimWords, aimWord)];
        return nodeAttribute;
    }

    static String entryWord2nodeLabel(String entryWord) {
        int index = search(entryWords, entryWord);
        if (index == entryWords.length-1) index--;
        String nodeLabel = nodeLabels[index];
        return nodeLabel;
    }

    static String entryWord2nodeAttribute(String entryWord) {
        int index = search(entryWords, entryWord);
        if (index == entryWords.length-1) index--;
        String nodeAttribute = nodeAttributes[index];
        return nodeAttribute;
    }

    public String questionParser(Map questinAnalysis) {
        StringBuffer cql = new StringBuffer("");
        cql.append("MATCH ");

        String aimNodeLabel = aimWord2nodeLabel( (String) questinAnalysis.get("qsAim") );
        String aimNodeAttrbute = aimWord2nodeAttribute( (String) questinAnalysis.get("qsAim") );

        List<Map<String, String>> entryWordList = (List<Map<String, String>>) questinAnalysis.get("qsEntityTypeList");
        int i=0;
        for (Map<String, String> map : entryWordList) {
            String entryWord = (String) map.keySet().toArray()[0];
            String entryNodeLabel = entryWord2nodeLabel( (String) map.values().toArray()[0] );
            String entryNodeAttrbute = entryWord2nodeAttribute( (String) map.values().toArray()[0] );
            if (relationshipMap.containsKey( (aimNodeLabel+"-"+entryNodeLabel) ) ) {
                // 因为图数据库中author被分为一作二作...分开存储，所以在此处单独处理
//                if ((aimNodeLabel+"-"+entryNodeLabel).equals("Paper-Author")) {
//                    String[] relationships = relationshipMap.get( (aimNodeLabel + "-" + entryNodeLabel) ).split(" ");
//                    for (String relationship : relationships) {
//                        char c = (char)('a' + i);
//                        String tempQCL = "(aim:" + aimNodeLabel + ")-" + relationship + "-(m" + c + ":" + entryNodeLabel + "{" + entryNodeAttrbute + ":\"" + entryWord + "\"})";
//                        cql.append(tempQCL).append(", ");
//                        i++;
//                    }
//                }
//                else {
//                    String relationship = relationshipMap.get(aimNodeLabel+"-"+entryNodeLabel);
//                    char c = (char)('a' + i);
//                    String tempQCL = "(aim:" + aimNodeLabel + ")-" + relationship + "-(m" + c + ":" + entryNodeLabel + "{" + entryNodeAttrbute + ":\"" + entryWord + "\"})";
//                    cql.append(tempQCL).append(", ");
//                }
                String relationship = relationshipMap.get(aimNodeLabel+"-"+entryNodeLabel);
                char c = (char)('a' + i);
                String tempQCL = "(aim:" + aimNodeLabel + ")-" + relationship + "-(m" + c + ":" + entryNodeLabel + "{" + entryNodeAttrbute + ":\"" + entryWord + "\"})";
                cql.append(tempQCL).append(", ");
            }
            else {
                break;
            }
            i++;
        }
        cql.delete(cql.length()-2, cql.length()-1).append(" ");
        cql.append("return aim.").append(aimNodeAttrbute).append(" AS answer");
        return cql.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        QuestionIntentionAnalysis qia = new QuestionIntentionAnalysis();
        QuestionParser qp = new QuestionParser();
        System.out.println(entryWords.length);
        System.out.println(entryWords[5]);
        System.out.println(Arrays.binarySearch(nodeAttributes, "unit"));
        while(true) {
            System.out.println("请输入问题：");
//            while(!scanner.hasNext());
            String qs = scanner.nextLine();
            if (qs.equals("-1")) {break;}
//            qia.intentionAnalysis(qs);
//            System.out.println(qia.intentionAnalysis(qs));
            System.out.println(qp.questionParser(qia.intentionAnalysis(qs)));
        }
        scanner.close();
    }

}
