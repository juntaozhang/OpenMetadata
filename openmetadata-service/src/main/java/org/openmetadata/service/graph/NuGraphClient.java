package org.openmetadata.service.graph;

import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

// TODO - Implement NuGraphClient
@Slf4j
public class NuGraphClient implements GraphClient {

  @Override
  public GraphTraversalSource getReadGraphTraversalSource() {
    return null;
  }

  @Override
  public GraphTraversalSource getWriteGraphTraversalSource() {
    return null;
  }
}
