package cn.tcualhp.kbqa_tech_web.kbqa;

import org.neo4j.driver.v1.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 检索neo4j数据库
 *
 */
public class AnswerSeek {

    Driver driver;
    Session session;


    public AnswerSeek(){
        // 远程仓库
//        Driver driver = GraphDatabase.driver( "bolt://120.26.175.63:7687", AuthTokens.basic( "neo4j", "Neo4j" ) );
        // 本地仓库
        driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "Neo4j3350" ) );
        session = driver.session();
    }

    public String answerSeek(String cql) {
        StringBuffer stringBuffer = new StringBuffer("");
        StatementResult result = session.run(cql);
        while ( result.hasNext() )
        {
            Record record = result.next();
            stringBuffer.append(record.get("answer").asString()).append(" ");
        }
//        session.close();
//        driver.close();
        return stringBuffer.toString();
    }

    public static void main( String[] args ) {
        StringBuffer stringBuffer = new StringBuffer("");
        // 远程仓库
        Driver driver = GraphDatabase.driver( "bolt://120.26.175.63:7687", AuthTokens.basic( "neo4j", "Neo4j" ) );
        // 本地仓库
//        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "Neo4j3350" ) );
        Session session = driver.session();
        StatementResult result = session.run("MATCH (aim:Paper)-[rf1:First_Author]-(ma:Author{authorName:\"吕艳蕊\"}), (aim:Paper)-[ra:About_to_Keyword]-(mb:Keyword{keyword:\"镇痛泵\"})  return aim.name AS answer");
        while ( result.hasNext() )
        {
            Record record = result.next();
            stringBuffer.append(record.get("answer").asString()).append(" ");
        }
        System.out.println(stringBuffer.toString());
        session.close();
        driver.close();
    }

    public void closeSessionAndDriver(){
        session.close();
        driver.close();
    }
}