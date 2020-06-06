package cn.tcualhp.kbqa_tech_web.utils;

import java.io.*;

/**
 * @author lihepeng
 * @description 有问题的查询记录
 * @date 2020-05-12 17:43
 **/
public class QuestionRecordUtil {
    public static void recordQuestion(String query, String detail) {

        FileOutputStream fileOutputStream;
        String pathName = "src/main/resources/data/" + "questionRecord.txt";
        File file = new File(pathName);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write("问题:"+query +",详情:" + detail + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
