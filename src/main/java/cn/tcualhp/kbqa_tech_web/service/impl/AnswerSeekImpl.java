package cn.tcualhp.kbqa_tech_web.service.impl;

import cn.tcualhp.kbqa_tech_web.initialization.BuildCache;
import cn.tcualhp.kbqa_tech_web.kbqa.AC.ACFilter;
import cn.tcualhp.kbqa_tech_web.service.AnswerSeekService;
import jdk.nashorn.internal.objects.NativeUint8Array;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.stereotype.Service;

import java.nio.channels.SeekableByteChannel;
import java.util.List;
import java.util.Map;

/**
 * @author yukan
 * @@description 答案检索
 * @Date 2020-6-10 4:09
 */

@Service
public class AnswerSeekImpl implements AnswerSeekService {


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
