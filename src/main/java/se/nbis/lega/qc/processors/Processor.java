package se.nbis.lega.qc.processors;

import no.ifi.uio.crypt4gh.stream.Crypt4GHInputStream;

import java.util.function.Function;

public interface Processor extends Function<Crypt4GHInputStream, Boolean> {

    String getName();

}
