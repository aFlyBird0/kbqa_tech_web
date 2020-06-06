package cn.tcualhp.kbqa_tech_web.utils;

import java.io.File;

/**
 * @author lihepeng
 * @description 获取与修改resources文件夹内部的文件，
 * @date 2020-06-06 18:43
 **/
public class ResourceFileUtil {
    /**
     * 读取，先暂时写成windows可跑的程序，以后如果要部署，读取部分只要修改这个程序就行
     * @param filename
     * @return java.io.File
     * @author lihepeng
     * @description //TODO 改写成springboot内部也能读取的形式，即部署后仍能读取
     * @date 18:46 2020/6/6
     **/
    public static File getResourceFile(String filename){
        File file = new File("src/main/resources/" + filename);
        return file;
    }
}
