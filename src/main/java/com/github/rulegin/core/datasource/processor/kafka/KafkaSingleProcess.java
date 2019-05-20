package com.github.rulegin.core.datasource.processor.kafka;

import com.github.rulegin.common.component.Rule;
import com.github.rulegin.core.filter.JsEvaluator;
import com.github.rulegin.core.filter.SingleJsEvaluator;
import com.github.rulegin.core.rule.RuleDataSource;
import com.github.rulegin.utils.KafkaProp;
import com.github.rulegin.core.datasource.conf.KafkaSingleConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.script.ScriptException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Jary on 2017/10/9 0009.
 */
@Slf4j
@Rule(name = "Kafka single process",configuration = KafkaSingleConfiguration.class)
public class KafkaSingleProcess implements RuleDataSource<KafkaSingleConfiguration> {

    KafkaSingleConfiguration configuration;



    @Override
    public void run(KafkaSingleConfiguration configuration) {
        this.configuration = configuration;
        JsEvaluator evaluator = new SingleJsEvaluator(configuration.getCondition());
        KafkaConsumer<Integer, String> consumer;
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProp.BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        props.put("auto.commit.interval.ms", "1000");

        //props.put("linger.ms", 1); // it will failed when set this to 10000.


        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(configuration.getTopic()));
        System.out.println("" + consumer.listTopics().toString());
        while (true) {
            ConsumerRecords<Integer, String> records = consumer.poll(100);
            //System.out.println(records.count());
            try {
                Thread.sleep(1000);
                //System.out.println("Received message:");
                for (ConsumerRecord<Integer, String> record : records) {
                    //k1=1 k2=3
                    //System.out.println("Received message: (" + record.value() + ")   offset:" + record.offset());
                    String[] key =record.value().trim().split("=");
                    Map<String ,Integer> map = new HashMap<>();
                    map.put(key[0],Integer.parseInt(key[1]));
                    evaluator.execute(SingleJsEvaluator.toBindings(map));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ScriptException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public void resume() {

    }

    @Override
    public void suspend() {

    }

    @Override
    public void stop() {

    }
    @Override
    public boolean some() {
        return false;
    }

}
