package com.nearinfinity.lucene.compressed;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class CompressionCodecTest {
    
    @Test
    public void testDeflaterCompressionCodec() throws IOException {
        DeflaterCompressionCodec codec = new DeflaterCompressionCodec();
        String string = "ab";
        byte[] src = string.getBytes();
        byte[] dest = new byte[100];
        int compress = codec.compress(src, 2, dest);
        byte[] original = new byte[100];
        int decompress = codec.decompress(dest, compress, original);
        assertEquals(string,new String(original,0,decompress));
    }
    
    @Test
    public void testQuickLZCompressionCodec() throws IOException {
        QuickLZCompressionCodec codec = new QuickLZCompressionCodec();
        String string = "ab";
        byte[] src = string.getBytes();
        byte[] dest = new byte[100];
        int compress = codec.compress(src, 2, dest);
        byte[] original = new byte[100];
        int decompress = codec.decompress(dest, compress, original);
        assertEquals(string,new String(original,0,decompress));
    }

}
