package tb.rulegin.server.core.datasource.processor.kafka;

import tb.rulegin.server.common.component.Rule;
import tb.rulegin.server.common.exception.UnauthorizedException;
import tb.rulegin.server.core.datasource.conf.KafkaStreamConfiguration;
import tb.rulegin.server.core.filter.JsEvaluator;
import tb.rulegin.server.core.filter.MultJsEvaluator;
import tb.rulegin.server.core.rule.RuleDataSource;
import tb.rulegin.server.utils.KafkaProp;
import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Rule(name = "Kafkastream", filtersType = "kafkaWindow", configuration = KafkaStreamConfiguration.class)
@Slf4j
public class KafkaStreamsProcess implements RuleDataSource<KafkaStreamConfiguration> {

    KafkaStreamConfiguration configuration;
    private class MyProcessorSupplier implements ProcessorSupplier<String, String> {
        @Override
        public Processor<String, String> get() {
            return new Processor<String, String>() {
                private ProcessorContext context;
                private KeyValueStore<String, String> kvStore;

                TimeBasedGenerator gen;
                String configuration_getFilter =null;// " Sum("; //String sum = "sum(a,temperature) ";
                JsEvaluator jsexe;
                String key;
                //k1=2 k2=3 k3=5
                String splitTM = " ";
                String splitKV = "=";

                @Override
                @SuppressWarnings("unchecked")
                public void init(ProcessorContext context) {
                    this.context = context;
                    this.context.schedule(configuration.getSize());
                    this.kvStore = (KeyValueStore<String, String>) context.getStateStore("Counts");
                    jsexe = new MultJsEvaluator();
                    try {
                        key =ValiCondition();
                        configuration_getFilter = configuration.getCondition();//Sum(k1)>5
                    } catch (UnauthorizedException e) {
                        e.printStackTrace();
                    }
                    gen = Generators.timeBasedGenerator(EthernetAddress.fromInterface());

                }

                @Override
                public void process(String key, String line) {
                    UUID uuid = gen.generate();
                    kvStore.put(uuid.toString().replaceAll("-", ""), line);
                }

                @Override
                public void punctuate(long timestamp) {
                    //System.out.println("\n\n----------- " + timestamp + " ----------- ");

                    Map<String, String> kvMap = new HashMap<>();
                    // uuid k=23
                    KeyValueIterator<String, String> iter = (KeyValueIterator<String, String>) this.kvStore.all();

                    iter.forEachRemaining(entry -> {
                        // System.out.println("key:" + entry.key + " msg: " + entry.value);
                        String[] msg = entry.value.split(splitTM); //[0]pre=23  [1]pre2=90

                        for (int i = 0; i < msg.length; i++) {
                            String[] kv = msg[i].split(splitKV);//pre=23 => [0]pre [1]23
                            String k = kv[0];//pre

                            if (key.equals(k)){
                                //System.out.println("k = " +k);
                                int v = Integer.parseInt(kv[1]);//23
                                String key = "J" + entry.key;
                                String msg2 = kvMap.get(k);// "Jasdfadfe4rd=23"
                                if (msg2 != null) {
                                    kvMap.put(k, msg2 + splitTM + key + splitKV + v);
                                } else {
                                    kvMap.put(k, key + splitKV + v);
                                }
                            }
                        }
                        context.forward(entry.key, entry.value);
                        this.kvStore.delete(entry.key);
                    });

                    Iterator iterMap = kvMap.keySet().iterator();

                    iterMap.forEachRemaining(preK -> {

                        //System.out.println("jaryzhen "+preK);
                        String value = kvMap.get(preK);//Jadfasda=23 Jadfasda=22
                        StringBuffer sb = new StringBuffer();

                        Map<String, Integer> bindingMap = new HashMap<>();
                        String[] msg = value.split(splitTM); //[0]Jadfasda=23 ; [1]Jadfasda=90
                        for (int i = 0; i < msg.length; i++) {
                            String[] kv = msg[i].split(splitKV);//[0]Jadfasda=23
                            String k = kv[0];//Jadfasda
                            Integer v = Integer.parseInt(kv[1]);
                            bindingMap.put(k, v);
                            sb.append(k + ",");
                        }

                        if (bindingMap.size() > 0) {
                            //Sum(k1)>23  => Sum(Jasdf,Jasdfasdf)>23
                            String contest = sb.toString().substring(0, sb.lastIndexOf(","));
                            String sum = configuration_getFilter.replace(key,contest);
                            //System.out.println("JS Func= " + sum);//
                            //System.out.println("Telemetry= " + preK);

                            try {
                                jsexe.execute(sum,MultJsEvaluator.toBindings(bindingMap));
                            } catch (ScriptException e) {
                                e.printStackTrace();
                            }
                        }

                    });
                    context.commit();
                }

                @Override
                public void close() {
                    this.kvStore.close();
                }
            };
        }
    }

    @Override
    public void run(KafkaStreamConfiguration configuration) {
        this.configuration = configuration;
        log.info("starting ",this.getClass().getSimpleName());
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProp.BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded dataSource
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        TopologyBuilder builder = new TopologyBuilder();

        builder.addSource("Source", configuration.getTopic());

        builder.addProcessor("Process", new MyProcessorSupplier(), "Source");
        builder.addStateStore(Stores.create("Counts").withStringKeys().withStringValues().inMemory().build(), "Process");


        builder.addSink("Sink", "test", "Process");

        final KafkaStreams streams = new KafkaStreams(builder, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.out.println("shutdown");
            System.exit(1);
        }
        System.exit(0);
    }

    public String ValiCondition() throws UnauthorizedException {
        String condition = configuration.getCondition();//sum(k1)>50
        String [] keys = configuration.getKeys();//[k1,k2,k3]
        String key = null;
        for (String k :keys){
            if (condition.contains(k)){
                key = k;
            }
        }
        if (key==null) throw new UnauthorizedException("key is not set");
        return key;
    }

    @Override
    public boolean some() {
        return false;
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

}
