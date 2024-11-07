package org.openmetadata.service.graph;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.util.system.ConfigurationUtil;
import org.openmetadata.service.exception.GraphException;

import java.util.stream.Stream;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

/**
 * docker network create --driver bridge mynet
 * <p>
 * docker run -it -p 8182:8182 --name janusgraph  --network mynet janusgraph/janusgraph:latest
 * <p>
 * UI
 * <ul>
 *  <li> docker run -d --rm -p 8081:8081 --name puppygraph  -e PUPPYGRAPH_USERNAME=puppygraph -e PUPPYGRAPH_PASSWORD=puppygraph  -e PORT=8081 -e  USE_GREMLIN_AUTH=false  -e GREMLINSERVER_HOST=janusgraph:8182 --network mynet puppygraph/puppygraph-query:latest
 *  <li> docker run --rm -d -p 3002:3002 -p 3001:3001 --name=janusgraph-visualizer --network=mynet janusgraph/janusgraph-visualizer:latest
 * </ul>
 */
@Slf4j
public class JanusGraphClient implements GraphClient {

  public static void main(String[] args) throws Exception {
    // PropertiesConfiguration conf =
    //     ConfigurationUtil.loadPropertiesConfig("conf/remote-graph.properties");
    // Cluster cluster = Cluster.open(conf.getString("gremlin.remote.driver.clusterFile"));
    // Client client = cluster.connect();

    buildVertexIndex();
    GraphTraversalSource g = traversal().withRemote("conf/remote-graph.properties");
    long numVertices = g.V().count().next();
    long numEdges = g.E().count().next();
    System.out.println("Number of vertices: " + numVertices);
    System.out.println("Number of edges: " + numEdges);
    System.out.println("V label: " + g.V().label().dedup().toList());
    System.out.println("E label: " + g.E().label().dedup().toList());
    System.out.println("V properties: " + g.V().properties().key().dedup().toList());
    System.out.println("E properties: " + g.E().properties().key().dedup().toList());
    g.close();
  }

  public static void buildVertexIndex() throws Exception {
    PropertiesConfiguration conf = ConfigurationUtil.loadPropertiesConfig("conf/remote-graph.properties");
    Cluster cluster = Cluster.open(conf.getString("gremlin.remote.driver.clusterFile"));
    Client client = cluster.connect();
    final StringBuilder req = new StringBuilder();
    req.append("JanusGraphManagement mgmt = graph.openManagement();");
    req.append("graph.tx().rollback();");


    req.append("mgmt.makePropertyKey(\"roles\").dataType(String.class).make();");
    req.append("mgmt.makePropertyKey(\"json\").dataType(String.class).make();");
    req.append("mgmt.makePropertyKey(\"href\").dataType(String.class).make();");
    req.append("mgmt.makePropertyKey(\"changeDescription\").dataType(String.class).make();");
    req.append("mgmt.makePropertyKey(\"votes\").dataType(String.class).make();");
    req.append("mgmt.makePropertyKey(\"lifeCycle\").dataType(String.class).make();");

    req.append("PropertyKey uid = mgmt.makePropertyKey(\"uid\").dataType(String.class).make();");
    req.append("PropertyKey name = mgmt.makePropertyKey(\"name\").dataType(String.class).make();");
    req.append("PropertyKey fqnHash = mgmt.makePropertyKey(\"fqnHash\").dataType(String.class).make();");
    req.append("PropertyKey nameHash = mgmt.makePropertyKey(\"nameHash\").dataType(String.class).make();");

    req.append("mgmt.buildIndex(\"uid_idx\", Vertex.class).addKey(uid).buildCompositeIndex();");
    req.append("mgmt.buildIndex(\"name_idx\", Vertex.class).addKey(name).buildCompositeIndex();");
    req.append("mgmt.buildIndex(\"fqnHash_idx\", Vertex.class).addKey(fqnHash).buildCompositeIndex();");
    req.append("mgmt.buildIndex(\"nameHash_idx\", Vertex.class).addKey(nameHash).buildCompositeIndex();");

    req.append("mgmt.commit();");
    req.append("graph.tx().commit();");
    final ResultSet resultSet = client.submit(req.toString());
    Stream<Result> futureList = resultSet.stream();
    futureList.map(Result::toString).forEach(System.out::println);
  }

  @Override
  public GraphTraversalSource getReadGraphTraversalSource() {
    try {
      return traversal().withRemote("conf/remote-graph.properties");
    } catch (Exception e) {
      throw new GraphException(e);
    }
  }

  @Override
  public GraphTraversalSource getWriteGraphTraversalSource() {
    try {
      return traversal().withRemote("conf/remote-graph.properties");
    } catch (Exception e) {
      throw new GraphException(e);
    }
  }
}
