package cn.tcualhp.kbqa_tech_web.kbqa.AC;

import java.util.List;

public class ACMain {

    public static void main(String[] args) {
        String[] keyWords = new String[] { "他们", "她们的", "他们的", "小文", "她们" };
        ACTree tree = new ACTree(keyWords);
        ACFilter filter = new ACFilter(tree);
        String str = "小文他们的她们的年号她们";
        List<String> result = filter.filter(str);
        System.out.println(result.toString());
        result = filter.meticulousFilter(result);
        System.out.println(result.toString());
    }
}
