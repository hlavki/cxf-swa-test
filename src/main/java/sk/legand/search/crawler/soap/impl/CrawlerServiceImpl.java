package sk.legand.search.crawler.soap.impl;

import javax.activation.DataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.legand.search.crawler.soap.AddEntry;
import sk.legand.search.crawler.soap.AddEntryResponse;
import sk.legand.search.crawler.soap.CrawlerServicePort;
import sk.legand.search.crawler.soap.SystemFault;

public class CrawlerServiceImpl implements CrawlerServicePort {

    private static final Logger log = LoggerFactory.getLogger(CrawlerServiceImpl.class);

    public CrawlerServiceImpl() {
    }

    @Override
    public AddEntryResponse addEntry(AddEntry parameters, DataHandler content) throws SystemFault {
        log.info("Reading content from " + content.getName());
        return new AddEntryResponse();
    }
}
