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
import com.nearinfinity.lucene.compressed.DeflaterCompressionCodec;

public class UsingSimple {

    public static void main(String[] args) throws CorruptIndexException, IOException {
        
        Directory fileDir = FSDirectory.open(new File("./dir"));
        
        DeflaterCompressionCodec compressionCodec = new DeflaterCompressionCodec();
        
        CompressedFieldDataDirectory dir = new CompressedFieldDataDirectory(fileDir, compressionCodec, 8192);
        
        IndexWriter writer = new IndexWriter(dir, new KeywordAnalyzer(), MaxFieldLength.UNLIMITED);
        writer.setUseCompoundFile(false);
        
        //create index here
        
        writer.close();
        
        IndexReader reader = IndexReader.open(dir);
        
        //use index here
        
        reader.close();
    }
}
