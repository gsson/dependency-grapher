package se.fnord.graph;

public interface GraphConnector<EDGE, VERTEX> {
    GraphConnection<EDGE, VERTEX> connect() throws Exception;
}
