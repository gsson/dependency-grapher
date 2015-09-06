package se.fnord.graph.orientdb;

import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import se.fnord.graph.GraphConnection;

public interface OrientConnection extends GraphConnection<OrientEdge, OrientVertex> {
}
