package com.nlp.nlptest;

import android.content.res.Resources;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class LuceneIndexOnAndroid {

    private IndexWriter writer;
    private ArrayList<File> files = new ArrayList<File>();
    public Directory dir;
    private StandardAnalyzer analyzer;
    private IndexWriterConfig config;
//	private static final int env = 1; // 0 - IDE, 1 - exported-JAR

    public LuceneIndexOnAndroid() {
        this.analyzer = new StandardAnalyzer(Version.LUCENE_40);
        this.config = new IndexWriterConfig(Version.LUCENE_40, this.analyzer);
        this.dir = new RAMDirectory();
        try {
            writer = new IndexWriter(this.dir, this.config);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class Data {
        ArrayList<String> arr;
        String chapter;
        public Data(ArrayList<String> arr, String chapter){
            this.arr = arr;
            this.chapter = chapter;
        }
    }

    public void readIS(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String buf = br.readLine();
        while (buf != null){
            System.out.println(buf);
            buf = br.readLine();
        }
        br.close();
        isr.close();
        is.close();
    }

    public void test() throws IOException {
//        String[] list = getClass().getAs
        // Work
//        InputStream is = getClass().getResourceAsStream("/assets/truyen/14/");
        // work
//        System.out.println("is");
//        System.out.println(is);
//        System.out.println("end is");

//        File f = new File("/assets/truyen/14");
//        if (f.isDirectory()){
//            System.out.println();
//        }
//        is = getClass().getResourceAsStream("truyen/14/truyen14-chuong1.txt");
//        System.out.println(is);
//        Resources res = Resources.getSystem();
//        res.openRawResource()
//        File f = new File("/assets/truyen/14/truyen14-chuong1.txt");
//        System.out.println("file");
//        System.out.println(f);
//        System.out.println("end file");
//        FileInputStream fis = new FileInputStream(f);
//        InputStreamReader isr = new InputStreamReader(fis);
//        BufferedReader br = new BufferedReader(isr);
//        String buf = br.readLine();
//        while (buf != null){
//            System.out.println(buf);
//            buf = br.readLine();
//        }
//        br.close();
//        isr.close();
//        fis.close();

    }

    public static void main(String[] args) throws IOException, ParseException, URISyntaxException {
        // TODO Auto-generated method stub
//		String path = "src/main/resources/truyen/14/truyen14-chuong1.txt";
        LuceneIndexOnAndroid li = new LuceneIndexOnAndroid();
        li.run(0, "14", "ngọn cờ xanh cao đến hơn");

    }

    public void run(int location, String truyenId, String query) throws IOException, URISyntaxException, ParseException{
        switch (location) {
            case 0:
                // IDE
                break;
            case 1:
                // JAR
                break;
            default:
                System.out.println("Invalid location");
                return;
        }
        String path = ((location == 1) ? "resources" : "") + "/truyen/" + truyenId;
//		String path = "D:/Garbage/14";
//		dir = new RAMDirectory();
        writer.deleteAll();
        System.out.println("Index cleared.");
        ArrayList<Data> arr = this.folderToArrStrings(path);
        System.out.println("parts: " + arr.size());
//		System.out.println(arr.get(0));
        for(int i = 0; i < arr.size(); i++){
            this.processIndex(arr.get(i));
        }
        writer.close();
        System.out.println("search for: " + query);
        this.search(query);
    }

    public void runFromData(ArrayList<Data> arr, String query) throws IOException, ParseException {
        writer.deleteAll();
        System.out.println("parts: " + arr.size());
//		System.out.println(arr.get(0));
        for(int i = 0; i < arr.size(); i++){
            this.processIndex(arr.get(i));
        }
        writer.close();
        System.out.println("search for: " + query);
        this.search(query);
    }

    private void search(String dataSearch) throws IOException, ParseException{
//		FSDirectory dir = FSDirectory.open(Paths.get("resources/index"));
//		 = new RAMDirectory();
        IndexReader reader = DirectoryReader.open(dir);
//		IndexReader reader = DirectoryReader.open((Directory) Paths.get("src/main/resources/index"));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
        String field = "content";
        QueryParser parser = new QueryParser(Version.LUCENE_40, field, analyzer);
        Query query = parser.parse(dataSearch);
        TopDocs results = searcher.search(query, 10);
        int numTotalHits = results.totalHits;
        System.out.println("numTotalHits = " + numTotalHits);
        //System.out.println(results);
        ScoreDoc[] hits = results.scoreDocs;
        System.out.println("hits: " + hits.length);

        for(int i = 0; (i < hits.length) && (i < 2); i++){
            System.out.println("======= " + i + " ======");
            ScoreDoc sd = hits[i];
            System.out.println(i + ": doc = " + sd.doc + ", score = " + sd.score);
            Document doc = searcher.doc(sd.doc);
            String name = doc.get("name");
            System.out.println(name);
            String chapter = doc.get("chapter");
            System.out.println("chapter: " + chapter);
            String originalContent = doc.get("originalContent");
            System.out.println(originalContent);
            System.out.println("------------------");
        }
        System.out.println("finish search");

    }

    private void processIndex(Data d) throws IOException{

//		FSDirectory dir = FSDirectory.open(Paths.get("resources/index"));
//		Directory dir = new RAMDirectory();
//		MemoryIndex index = new MemoryIndex();

        for(int i = 0; i < d.arr.size(); i++){
            String line = d.arr.get(i);
            Document doc = new Document();
            doc.add(new TextField("content", line, Field.Store.NO));
//			doc.add(new StringField("name", "don_" + i, Field.Store.YES));
            doc.add(new StringField("chapter", d.chapter, Field.Store.YES));
            doc.add(new StringField("originalContent", line, Field.Store.YES));
            writer.addDocument(doc);
        }
        System.out.println(writer.numDocs() + " documents created.");
//		writer.commit();


    }

    public Data ISToArrStrings(InputStream is, String chapter){
        try {
//			File f = new File(getClass().getResource(path));
//			getClass().getRe
//			FileInputStream fis = new FileInputStream(f);
//			System.out.println(0);
//			InputStream is = getClass().getResourceAsStream(filePath);
//			System.out.println(1);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
//			System.out.println(2);
            BufferedReader br = new BufferedReader(isr);
            //System.out.println(3);
            String buf = br.readLine();
            //System.out.println(4);
            //System.out.println(buf);
            StringBuilder data = new StringBuilder();
            ArrayList<String> arr = new ArrayList<String>();
            int line = 1;
            while (buf != null){
//				System.out.println(buf);
                data.append(buf.trim() + "\n");
//				arr.add(buf.trim());
                buf = br.readLine();
            }
            String[] paragraphs = data.toString().split("\\.\\s*\n");
            //String[] paragraphs = data.toString().split("[^a-zA-Z]\\s*\n");
            for(int i = 0; i < paragraphs.length; i++){
                arr.add(paragraphs[i]);
            }
//			arr.add(data.toString());
            br.close();
            isr.close();
            is.close();
            return new Data(arr, chapter);

        }
        catch (Exception e){
            System.out.println("err in fileToDocuments");
            e.printStackTrace();
            return new Data(new ArrayList<String>(), "");
        }
    }

    private ArrayList<Data> folderToArrStrings(String folderPath) throws IOException, URISyntaxException{

//		File folder = new File(folderPath);
        ArrayList<Data> result = new ArrayList<Data>();
//		if (folder.isDirectory()){
//			System.out.println("directory");
//			File[] files = folder.listFiles();
//			System.out.println("Files: " + files.length);
//			for(File file : files){
//				System.out.println("processing " + folderPath + "/" + file.getName());
//				result.addAll(this.fileToArrStrings(folderPath + "/" + file.getName()));
//			}
//			return result;
//		}
//		else {
//			System.out.println("file");
//			return this.fileToArrStrings(folderPath);
//		}
//		System.out.println(folderPath);
//		URL url = getClass().getResource(folderPath);
//		System.out.println(url);
//		InputStream is = url.openStream();
//		InputStream is = getClass().getResourceAsStream(folderPath);
//		InputStream is = new FileInputStream(folder);

//		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(folderPath);
//		InputStreamReader isr = new InputStreamReader(is);
//		BufferedReader br = new BufferedReader(isr);
//		String buf = br.readLine();
//		ArrayList<String> fileNames = new ArrayList<String>();
//		while (buf != null){
//			System.out.println(buf);
//			fileNames.add(buf);
//			buf = br.readLine();
//		}
//		br.close();
//		isr.close();
//		is.close();
//		buf = null;
//		for(int i = 0; i < fileNames.size(); i++){
//			String fileName = fileNames.get(i);
//			System.out.println("reading " + folderPath + "/" + fileName);
//			is = getClass().getResourceAsStream(folderPath + "/" + fileName);
//			result.addAll(this.ISToArrStrings(is));
//			is.close();
//		}

        File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        if (jarFile.isFile()){
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();
            String base = folderPath + "/";
            while (entries.hasMoreElements()){
                String name = entries.nextElement().getName();
                if ((name.startsWith(base)) && (name.length() > base.length())){
                    name = "/" + name;
//					System.out.println(name);
//					System.out.println("reading " + name);
                    InputStream is = getClass().getResourceAsStream(name); // Phải có dấu / ở đầu thì mới đọc được
                    result.add(this.ISToArrStrings(is, name));
                    is.close();
                }
            }
            jar.close();
        }
        else {
            System.out.println("IDE");
            InputStream is = getClass().getResourceAsStream(folderPath);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String buf = br.readLine();
            ArrayList<String> fileNames = new ArrayList<String>();
            while (buf != null){
//				System.out.println(buf);
                fileNames.add(buf);
                buf = br.readLine();
            }
            br.close();
            isr.close();
            is.close();
            buf = null;
            for(int i = 0; i < fileNames.size(); i++){
                String fileName = fileNames.get(i);
//				System.out.println("reading " + folderPath + "/" + fileName);
                is = getClass().getResourceAsStream(folderPath + "/" + fileName);
                result.add(this.ISToArrStrings(is, fileName));
                is.close();
            }
        }

        return result;
    }


}

