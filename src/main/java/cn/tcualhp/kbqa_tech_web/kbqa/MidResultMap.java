package cn.tcualhp.kbqa_tech_web.kbqa;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lihepeng
 * @description 建立一个类来保存多轮询问产生的中间变量
 * @date 2020-06-11 15:53
 **/
public class MidResultMap {
    private HashMap<String, Map> midResults;

    public MidResultMap(HashMap<String, Map> midResults) {
        this.midResults = midResults;
    }

    public MidResultMap() {
        this.midResults = new HashMap<>();
    }

    /**
     * 根据对话编号获取中间结果
     * @param number
     * @return java.util.HashMap<java.lang.String,java.util.Map>
     * @author lihepeng
     * @description
     * @date 15:57 2020/6/11
     **/
    public HashMap<String, Map> getMidResult(String number){
        return (HashMap<String, Map>)midResults.get(number);
    }

    /**
     * 删除不用的临时变量
     * @param number
     * @return void
     * @author lihepeng
     * @description
     * @date 16:01 2020/6/11
     **/
    public void delResult(String number){
        midResults.keySet().removeIf(key -> key.equals(number));
    }

    public HashMap<String, Map> getMidResultThenDel(String number){
        HashMap<String, Map> map = (HashMap<String, Map>)midResults.get(number);
        midResults.keySet().removeIf(key -> key.equals(number));
        return map;
    }

    public void addResult(String number, HashMap<String, Map> midResult){
        midResults.put(number, midResult);
    }
}
