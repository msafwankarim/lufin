package org.lucene.index.files;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public class LuceneFileSystemDocument {

    private Metadata metadata;
    private AutoDetectParser parser;
    private BodyContentHandler handler;
    private final File file;


    public LuceneFileSystemDocument(File file) {
        if(!file.isFile())
            throw new UnsupportedOperationException("Directory is not supported by LuceneFileSystemDocument");

        this.file     = file;
        this.metadata = new Metadata();
        this.parser   = new AutoDetectParser();
        this.handler  = new BodyContentHandler();

        this.metadata.add(TikaCoreProperties.RESOURCE_NAME_KEY, file.getName());
    }

    public Document toLuceneDocument() throws TikaException, IOException, SAXException {
        return toLuceneDocument(this.file);
    }

    private Document toLuceneDocument(File file) throws TikaException, IOException, SAXException {
        try (InputStream stream = new FileInputStream(file)) {
            parser.parse(stream, handler, metadata);
            return new TikaFileParser(metadata, handler).toLuceneDocument();
        }
    }

    public Map<String, String> getFileData() throws IOException, TikaException, SAXException {
        try (InputStream stream = new FileInputStream(file)) {
            parser.parse(stream, handler, metadata);
            return new TikaFileParser(metadata, handler).getData();
        }
    }

    public Document writeToIndex(IndexWriter writer) throws TikaException, IOException, SAXException {
        if(writer == null)
            throw new IllegalArgumentException("writer is null");

        var document = toLuceneDocument();
        writer.addDocument(document);
        return document;
    }

    // region Getters / Setters
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public AutoDetectParser getParser() {
        return parser;
    }

    public void setParser(AutoDetectParser parser) {
        this.parser = parser;
    }

    public BodyContentHandler getHandler() {
        return handler;
    }

    public void setHandler(BodyContentHandler handler) {
        this.handler = handler;
    }

    public File getFile() {
        return file;
    }
    //endregion
}
