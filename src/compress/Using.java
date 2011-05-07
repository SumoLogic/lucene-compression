package compress;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.nearinfinity.lucene.compressed.CompressedFieldDataDirectory;
import com.nearinfinity.lucene.compressed.QuickLZCompressionCodec;

public class Using {

    public static void main(String[] args) throws CorruptIndexException, IOException {
        Random random = new Random(1);
        File fileComp = new File("./index-comp");
        File fileNoComp = new File("./index-nocomp");
        rm(fileNoComp);
        rm(fileComp);
        Directory dirFsComp = FSDirectory.open(fileComp);
        Directory dirFsNoComp = FSDirectory.open(fileNoComp);
//        DeflaterCompressionCodec compressionCodec = new DeflaterCompressionCodec();
        QuickLZCompressionCodec compressionCodec = new QuickLZCompressionCodec();
        CompressedFieldDataDirectory dir = new CompressedFieldDataDirectory(dirFsComp, compressionCodec, 8192);
        IndexWriter writerComp = new IndexWriter(dir, new KeywordAnalyzer(), MaxFieldLength.UNLIMITED);
        IndexWriter writerNoComp = new IndexWriter(dirFsNoComp, new KeywordAnalyzer(), MaxFieldLength.UNLIMITED);
        writerNoComp.setUseCompoundFile(false);
        writerComp.setUseCompoundFile(false);
        long s = System.currentTimeMillis();
        long total = 0;
        for (int i = 0; i < 500; i++) {
            total += addDocNormal(writerNoComp,writerComp,random);
        }
        System.out.println(System.currentTimeMillis() - s + " Docs indexed:" + total);
        writerNoComp.close();
        writerComp.close();
        
        for (String f : dir.listAll()) {
            if (f.endsWith(".fdt")) {
                System.out.println("Compressed     " + f + "=" + dir.fileLength(f));
            }
        }
        
        for (String f : dirFsNoComp.listAll()) {
            if (f.endsWith(".fdt")) {
                System.out.println("Not Compressed " + f + "=" + dirFsNoComp.fileLength(f));
            }
        }
        
        randomAccess(dir);
//        randomAccess(dirFsNoComp);
//        randomAccess(dir);
//        randomAccess(dirFsNoComp);
    }

    private static void randomAccess(Directory dir) throws CorruptIndexException, IOException {
        IndexReader reader = IndexReader.open(dir);
        Random random = new Random();
        int maxDoc = reader.maxDoc();
        long s = System.nanoTime();
        long hash = 0;
        int size = 10000;
        int fetches = 10;
        for (int i = 0; i < size; i++) {
            int nextInt = random.nextInt(maxDoc - fetches);
            for (int d = 0; d < fetches; d++) {
                Document document = reader.document(nextInt + d);
                hash += document.hashCode();
                
                System.out.println(document);
            }
        }
        long e = System.nanoTime();
        System.out.println("Time:" + ((e-s) / 1000000.0) / (size * fetches) + " ms");
        reader.close();
    }

    private static void rm(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                rm(f);
            }
        }
        file.delete();
    }

    private static int addDocNormal(IndexWriter writerNoComp, IndexWriter writerComp, Random random) throws CorruptIndexException, IOException {
        int length = random.nextInt(100);
        Document doc = getDoc();
        doc.add(new Field("_primdoc_","TRUE",Store.NO,Index.NOT_ANALYZED_NO_NORMS));
        writerNoComp.addDocument(doc);
        writerComp.addDocument(doc);
        for (int i = 0; i < length; i++) {
            doc = getDoc();
            writerNoComp.addDocument(doc);
            writerComp.addDocument(doc);
        }
        return length;
    }

    private static Document getDoc() {
        Document document = new Document();
        String value = UUID.randomUUID().toString();
        document.add(new Field("rowid",value,Store.YES,Index.NOT_ANALYZED_NO_NORMS));
        document.add(new Field("recordid",value,Store.YES,Index.NOT_ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 1,Store.YES,Index.ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 2,Store.YES,Index.ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 3,Store.YES,Index.ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 4,Store.YES,Index.ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 5,Store.YES,Index.ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 6,Store.YES,Index.ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 7,Store.YES,Index.ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 8,Store.YES,Index.ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 9,Store.YES,Index.ANALYZED_NO_NORMS));
        document.add(new Field("cf.test",value + 10,Store.YES,Index.ANALYZED_NO_NORMS));
        return document;
    }

}
