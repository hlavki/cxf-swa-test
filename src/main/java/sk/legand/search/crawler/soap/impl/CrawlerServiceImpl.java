package sk.legand.search.crawler.soap.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.legand.search.crawler.soap.AddEntry;
import sk.legand.search.crawler.soap.AddEntryResponse;
import sk.legand.search.crawler.soap.Attachment;
import sk.legand.search.crawler.soap.Attribute;
import sk.legand.search.crawler.soap.CrawlerServicePort;
import sk.legand.search.crawler.soap.InjectionEntry;
import sk.legand.search.crawler.soap.Metadata;
import sk.legand.search.crawler.soap.ObjectFactory;
import sk.legand.search.crawler.soap.SystemFault;

public class CrawlerServiceImpl implements CrawlerServicePort {

    private static final Logger log = LoggerFactory.getLogger(CrawlerServiceImpl.class);

    private static final String DEFAULT_FOLDER = "/tmp/crawler/soap";
    private static final String CONTENT_FILENAME_ATTR = "Soubor";

    private final File outputFolder;

    public CrawlerServiceImpl() {
        this.outputFolder = new File(DEFAULT_FOLDER);
        if (!this.outputFolder.exists()) this.outputFolder.mkdirs();
    }

    @Override
    public AddEntryResponse addEntry(AddEntry parameters, DataHandler content) throws SystemFault {
        storeEntry(parameters.getEntry(), content);
//        Optional<Injector> injector = getInjector();
//        injector.ifPresent(i -> i.inject(toCrawlerEntry(entry)));
        return new AddEntryResponse();
    }

    private void storeEntry(InjectionEntry entry, DataHandler content) {
        File entryFolder = new File(outputFolder, String.valueOf(entry.getUri().hashCode()));
        entryFolder.mkdirs();
        storeMetadata(entry.getMetadata(), entryFolder);
        storeAllFiles(entry, entryFolder, content);
        log.info("Data for {} stored to {}", entry.getUri(), entryFolder.getAbsolutePath());
    }

//    private String mapMetadata(Metadata metadata) {
//        BaseDataConnectorData result = new BaseDataConnectorData();
//        metadata.getAttribute().forEach(attr -> result.putParameter(attr.getName(), attr.getValue()));
//        return result.toXml();
//    }
    private void storeAllFiles(InjectionEntry entry, File entryFolder, DataHandler content) {
        storeContent(entryFolder, getMetadataValue(entry, CONTENT_FILENAME_ATTR), content);
        entry.getAttachments().stream().forEach(a -> storeAttachment(a, entryFolder));
    }

    private void storeAttachment(Attachment attachment, File entryFolder) {
        storeContent(entryFolder, Optional.ofNullable(attachment.getFilename()), attachment.getContent());
    }

    private void storeContent(File folder, Optional<String> filenameOpt, byte[] content) {
        String filename = filenameOpt.orElse("content.pdf");
        File outputFile = new File(folder, filename);
        try (OutputStream out = new FileOutputStream(outputFile)) {
            out.write(content);
        } catch (IOException e) {
            log.error("Cannot write file " + outputFile.getAbsolutePath(), e);
        }
    }

    private void storeContent(File folder, Optional<String> filenameOpt, DataHandler dataHandler) {
        String filename = filenameOpt.orElse("content.pdf");
        File outputFile = new File(folder, filename);
        try (InputStream in = dataHandler.getInputStream(); OutputStream out = new FileOutputStream(outputFile)) {
            copyData(in, out);
        } catch (IOException e) {
            log.error("Cannot write file " + outputFile.getAbsolutePath(), e);
        }
    }

    private void storeMetadata(Metadata metadata, File entryFolder) {
        File metadataFile = new File(entryFolder, "metadata.xml");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Metadata.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Marshal the employees list in file
            ObjectFactory of = new ObjectFactory();
            jaxbMarshaller.marshal(of.createMetadata(metadata), metadataFile);
        } catch (JAXBException e) {
            log.error("Cannot store watchings", e);
        }
    }

    private Optional<String> getMetadataValue(InjectionEntry entry, String attr) {
        List<Attribute> attrs = entry.getMetadata().getAttribute();
        Optional<Attribute> optVal = attrs.stream().filter(a -> a.getName().equals(attr)).findAny();
        return optVal.map(v -> v.getValue().stream().findAny()).orElse(Optional.empty());
    }

    private URI uri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }

    public void copyData(InputStream source, OutputStream target) throws IOException {
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = source.read(buffer)) != -1) {
            target.write(buffer, 0, bytesRead);
        }
    }
}
