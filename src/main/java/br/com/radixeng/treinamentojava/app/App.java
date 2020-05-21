package br.com.radixeng.treinamentojava.app;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class App {

    public static void main(String[] args) {

        URI uri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        ResourceConfig config = new ResourceConfig(Service.class);
        Server server = JettyHttpContainerFactory.createServer(uri, config);
    
    }

}
