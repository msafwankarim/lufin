# LuFIn (Lucene File Indexer)

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Overview

LuFIn is a small yet powerful library that simplifies the process of indexing various types of files (including PDFs) as Lucene documents. It leverages the capabilities of [Apache Lucene](https://lucene.apache.org/) and [Apache Tika](https://tika.apache.org/) to help you efficiently create search indexes for your files, making it easier to search and retrieve content within them.

## Features

- **File Type Agnostic**: Supports indexing a wide range of file types, including PDF, Word documents, plain text, and more.
- **Easy Integration**: Provides a simple and intuitive API for integrating file indexing into your applications.
- **Full-Text Search**: Enables full-text search capabilities on your indexed files using Apache Lucene's powerful search features.
- **Open Source**: Distributed under the MIT License, allowing you to use and modify the library for your specific needs.

## Getting Started

To get started with the Lucene File Indexing Library, follow these steps:

1. **Installation**: Download the [jar](https://github.com/msafwankarim/lufin/releases/download/v1.0/lufin-1.0.jar) file & add the library to your project as a dependency.
    ``` groovy
    implementation files("lufin-1.0.jar");
    ```
2. **Usage**: Use the library to index your files. Here's a simple example in Java:
    ``` java 
   File sourceDirectory = Path.of("data", "data-source").toFile();
   Path indexPath = Path.of("data", "index");
   
   Directory dir = FSDirectory.open(indexPath);
   IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));

   writer.deleteAll(); 
   writer.commit();

   FileIndexer indexer = new FileIndexer(sourceDirectory.toPath()); 
   indexer.indexDocuments(writer);

   writer.commit();
   ```
## License:

LuFIn is open source and distributed under the [MIT License](LICENSE).

Thank you for considering LuFIn for your file indexing needs. We look forward to your feedback and support as we continue to develop and improve the library.