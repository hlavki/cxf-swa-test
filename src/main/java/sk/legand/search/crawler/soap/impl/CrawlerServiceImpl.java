package sk.legand.search.crawler.soap.impl;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sk.legand.search.crawler.soap.AddEntry;
import sk.legand.search.crawler.soap.AddEntryResponse;
import sk.legand.search.crawler.soap.CrawlerServicePort;
import sk.legand.search.crawler.soap.SystemFault;

@WebService(
    wsdlLocation = "META-INF/wsdl/crawler.wsdl",
    targetNamespace = "http://search.legand.sk/crawler/soap/",
    serviceName = "CrawlerService",
    portName = "CrawlerServicePort"
)
public class CrawlerServiceImpl implements CrawlerServicePort {

    private static final Logger LOG = LoggerFactory.getLogger(CrawlerServiceImpl.class);


    @Override
    public AddEntryResponse addEntry(AddEntry parameters, DataHandler content) throws SystemFault {
        try {
            LOG.info("Reading content from " + content.getContent());
            content.writeTo(System.out);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return new AddEntryResponse();
    }
}
