package se.fnord.graph.orientdb;

import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import se.fnord.graph.GraphConnector;

public interface OrientConnector extends GraphConnector<OrientEdge, OrientVertex> {
    OrientConnection connect() throws Exception;
}
