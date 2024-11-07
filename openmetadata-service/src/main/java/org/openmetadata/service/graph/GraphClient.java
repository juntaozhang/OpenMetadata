package org.openmetadata.service.graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface GraphClient {
  GraphTraversalSource getReadGraphTraversalSource();
  GraphTraversalSource getWriteGraphTraversalSource();
}
