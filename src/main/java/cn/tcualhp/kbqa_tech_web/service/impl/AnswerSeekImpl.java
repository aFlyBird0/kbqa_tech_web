package cn.tcualhp.kbqa_tech_web.service.impl;

import cn.tcualhp.kbqa_tech_web.initialization.BuildCache;
import cn.tcualhp.kbqa_tech_web.service.AnswerSeekService;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yukan
 * @@description 答案检索
 * @Date 2020-6-10 4:09
 */

@Service
public class AnswerSeekImpl implements AnswerSeekService {

//    private Session session = BuildCache.session;

    @Override
    public String answerSeek(String cql) {
        StringBuffer stringBuffer = new StringBuffer("");
        StatementResult result = BuildCache.session.run(cql);
        while ( result.hasNext() )
        {
            Record record = result.next();
            stringBuffer.append(record.get("answer").asString()).append(" ");
        }
        return stringBuffer.toString();
    }
}
