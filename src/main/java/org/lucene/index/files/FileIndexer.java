package org.lucene.index.files;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.tika.exception.TikaException;
import org.codelibs.jhighlight.fastutil.Hash;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileIndexer {
    private final File file;

    public FileIndexer(Path filePath) {
        this.file = filePath.toFile();
    }



    public List<Document> getLuceneDocuments() {
        return getLuceneFSDocuments()
                .stream()
                .map(fsDoc -> {
                    try {
                        return fsDoc.toLuceneDocument();
                    } catch (TikaException | IOException | SAXException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<LuceneFileSystemDocument> getLuceneFSDocuments() {
        if(this.file.isFile())
            return List.of(new LuceneFileSystemDocument(this.file));

        return getLuceneFSDocs(this.file);
    }

    private List<LuceneFileSystemDocument> getLuceneFSDocs(File path) {
        List<LuceneFileSystemDocument> results = new ArrayList<>();

        for(var file : Objects.requireNonNull(path.listFiles())) {
            if(file.isDirectory()) {
                results.addAll(getLuceneFSDocs(file));
            } else {
                results.add(new LuceneFileSystemDocument(file));
            }
        }

        return results;
    }

    public void indexDocuments(IndexWriter writer) throws TikaException, IOException, SAXException {
        Objects.requireNonNull(writer);

        if(!writer.isOpen())
            throw new IllegalStateException("writer is closed");

        for(var doc : getLuceneFSDocuments()) {
            var document = doc.toLuceneDocument();
            writer.addDocument(document);
        }
    }
}
