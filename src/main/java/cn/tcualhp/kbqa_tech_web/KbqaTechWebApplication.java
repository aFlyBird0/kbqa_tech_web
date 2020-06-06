package cn.tcualhp.kbqa_tech_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;

@SpringBootApplication
        //(exclude= Neo4jDataAutoConfiguration.class)
public class KbqaTechWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(KbqaTechWebApplication.class, args);
    }

}
