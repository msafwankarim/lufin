package org.lucene.index.files;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, TikaException, SAXException {
        File sourceDirectory = Path.of("data", "data-source").toFile();
        Path indexPath = Path.of("data", "index");

        Directory dir = FSDirectory.open(indexPath);
        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));

        writer.deleteAll();
        writer.commit();

        FileIndexer indexer = new FileIndexer(sourceDirectory.toPath());
        indexer.indexDocuments(writer);

        writer.commit();

        printAllDocs(dir);
    }

    private static void printAllDocs(Directory dir) throws IOException {
        try (IndexReader reader = DirectoryReader.open(dir)) {
            var searcher = new IndexSearcher(reader);
            var topDocs = searcher.search(new MatchAllDocsQuery(), 1000);

            for(var doc : topDocs.scoreDocs) {
                Document document = searcher.doc(doc.doc);
                for(var field : document.getFields()) {
                    System.out.println(field.name() + ": " + field.stringValue());
                }
            }
        }
    }
}