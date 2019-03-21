package sk.legand.search.crawler.soap.impl;

import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sk.legand.search.crawler.soap.AddEntry;
import sk.legand.search.crawler.soap.CrawlerService;
import sk.legand.search.crawler.soap.CrawlerServicePort;
import sk.legand.search.crawler.soap.InjectionEntry;

public class SoapTest {

    static URL wsdlURL;
    static QName serviceName;
    static QName portName;

    static {
        serviceName = new QName("http://search.legand.sk/crawler/soap/",
                "CrawlerService");
        portName = new QName("http://search.legand.sk/crawler/soap/", "CrawlerServicePort");
    }

    private static Server server;

    @BeforeClass
    public static void setUp() throws Exception {
        JaxWsServerFactoryBean srv = new JaxWsServerFactoryBean();
        String address = "http://localhost:8181/cxf/soap";
        srv.setAddress(address);
        srv.setServiceClass(CrawlerServicePort.class);
        srv.setServiceBean(new CrawlerServiceImpl());
        srv.setWsdlLocation("/META-INF/wsdl/crawler.wsdl");
        server = srv.create();
    }

    @AfterClass
    public static void tearDown() {
        try {
            server.stop();
        } catch (Throwable t) {
            System.out.println("Error thrown: " + t.getMessage());
        }
    }

    @Test
    public void base() throws Exception {
        CrawlerServicePort port = new CrawlerService(new URL("http://localhost:8181/cxf/soap?wsdl")).getCrawlerServicePort();

        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8181/cxf/soap");

//        bp.getRequestContext().put("mtom-enabled", Boolean.TRUE);
////        bp.getRequestContext().put("write.attachments", Boolean.FALSE);
//
//        SOAPBinding sbinding = (SOAPBinding) bp.getBinding();
//        sbinding.setMTOMEnabled(true);
        AddEntry params = new AddEntry();

        InjectionEntry entry = new InjectionEntry();
        entry.setUri("id:1");

        params.setEntry(entry);

        port.addEntry(params, new DataHandler(new FileDataSource("src/test/resources/sample.pdf")));
    }
}
