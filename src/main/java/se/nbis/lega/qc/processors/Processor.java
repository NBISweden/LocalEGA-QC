package se.nbis.lega.qc.processors;

import se.nbis.lega.qc.pojo.QCMessage;

import java.util.function.Consumer;

public interface Processor extends Consumer<QCMessage> {

    String getName();

}
