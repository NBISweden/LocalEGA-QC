package se.nbis.lega.qc.listenerts;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.nbis.lega.qc.pojo.QCMessage;
import se.nbis.lega.qc.processors.Processor;

import java.util.Collection;

@Slf4j
@Component
public class QCMessageListener implements MessageListener {

    private Gson gson;
    private Collection<Processor> processors;

    @Override
    public void onMessage(org.springframework.amqp.core.Message message) {
        QCMessage qcMessage = gson.fromJson(new String(message.getBody()), QCMessage.class);
        for (Processor processor : processors) {
            processor.accept(qcMessage);
        }
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    public void setProcessors(Collection<Processor> processors) {
        this.processors = processors;
    }

}
