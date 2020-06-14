package cn.tcualhp.kbqa_tech_web.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;

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

    /**
     * 读取resources下的文件，返回文件流
     * @param filename
     * @return java.io.InputStream
     * @author lihepeng
     * @description
     * @date 19:26 2020/6/14
     **/
    public static InputStream getResourceInputStream(String filename) throws IOException {
        return new ClassPathResource(filename).getInputStream();
    }

    /**
     * 返回某文件夹下的所有resource
     * @param dirname
     * @return org.springframework.core.io.Resource[]
     * @author lihepeng
     * @description //TODO
     * @date 20:01 2020/6/14
     **/
    public static Resource[] getDirResources(String dirname) throws IOException {
        // 获取文件夹下所有resource, /*代表文件夹下所有文件
        return new PathMatchingResourcePatternResolver().getResources(dirname + "/*");
    }

    public static String convertResource2String(Resource resource) throws IOException{
        InputStream inputStream = resource.getInputStream();
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String str = sb.toString();
        return str;
    }

    /**
     * 读取resource下的文件，返回字符串
     * @param filename
     * @return java.lang.String
     * @author lihepeng
     * @description
     * @date 19:28 2020/6/14
     **/
    public static String getResourceString(String filename) throws IOException{
        Resource resource = new ClassPathResource(filename);
        return convertResource2String(resource);
    }
}
