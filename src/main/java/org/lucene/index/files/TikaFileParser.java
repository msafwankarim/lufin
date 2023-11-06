package org.lucene.index.files;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

public class TikaFileParser {
    private final Map<String, String> data;
    private final static String CONTENT_KEY = "content";

    TikaFileParser(Metadata metadata, ContentHandlerDecorator handler) {
        data = new TreeMap<>();

        if(metadata != null) {
            for(var prop : metadata.names()) {
                StringBuilder builder = new StringBuilder();
                for(var value : metadata.getValues(prop)) {
                    builder.append(value);
                    builder.append(" ");
                }
                data.put(prop, builder.toString());
            }

        }

        String body = handler.toString();

        if(!isNullOrEmpty(body))
            data.put(CONTENT_KEY, body);
    }


    public static Document getLuceneDocument(LuceneFileSystemDocument fsDoc) throws TikaException, IOException, SAXException {
        return getFileParser(fsDoc).toLuceneDocument();
    }

    private static TikaFileParser getFileParser(LuceneFileSystemDocument fsDoc) throws TikaException, IOException, SAXException {
        try (InputStream stream = new FileInputStream(fsDoc.getFile())) {
            fsDoc.getParser().parse(stream, fsDoc.getHandler(), fsDoc.getMetadata());
            return new TikaFileParser(fsDoc.getMetadata(), fsDoc.getHandler());
        }
    }

    private boolean isNullOrEmpty(String str) {
        if(str == null)
            return true;

        return str.length() == 0;
    }

    public Document toLuceneDocument() {
        Document document = new Document();

        for (var prop : data.entrySet()) {
            if(!prop.getKey().equals(CONTENT_KEY))
                document.add(new TextField(prop.getKey(), prop.getValue(), Field.Store.YES));
        }

        document.add(new TextField(CONTENT_KEY, data.get(CONTENT_KEY), Field.Store.YES));

        return document;
    }

    public Map<String, String> getData() {
        return data;
    }

}
