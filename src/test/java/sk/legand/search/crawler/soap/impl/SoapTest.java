package sk.legand.search.crawler.soap.impl;

import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sk.legand.search.crawler.soap.AddEntry;
import sk.legand.search.crawler.soap.CrawlerService;
import sk.legand.search.crawler.soap.CrawlerServicePort;
import sk.legand.search.crawler.soap.InjectionEntry;

public class SoapTest {

    private static Endpoint ep;


    @BeforeClass
    public static void setUp() throws Exception {
        ep = Endpoint.publish("http://localhost:8181/cxf/soap", new CrawlerServiceImpl());
        Binding binding = ep.getBinding();
        ((SOAPBinding)binding).setMTOMEnabled(true);
    }

    @AfterClass
    public static void tearDown() {
        try {
            ep.stop();
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
