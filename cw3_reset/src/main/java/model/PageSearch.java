package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageSearch {
    private StandardAnalyzer analyzer;
    private Directory index;

    public PageSearch(Collection<Page> pages) throws IOException {
        this.analyzer = new StandardAnalyzer();
        Path indexPath = Files.createTempDirectory("tempIndex");
        this.index = FSDirectory.open(indexPath);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(this.index, config);
        for (Page page: pages){
            this.addDoc(iwriter, page.getTitle(), page.getContent());
        }
    }

    public void addDoc(IndexWriter writer, String title, String content) throws IOException {
        Document doc = new Document();
        doc.add(new Field("Title" , title, TextField.TYPE_STORED));
        doc.add(new Field("Content" , content,TextField.TYPE_STORED));
        writer.addDocument(doc);
    }
    public Collection<PageSearchResult> search(String question) throws IOException, ParseException {
        //Search the index
        DirectoryReader ireader = DirectoryReader.open(this.index);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        Set<ScoreDoc> hits = new HashSet<ScoreDoc>();

        QueryParser parserTitle = new QueryParser("Title", analyzer);
        Query queryTitle = parserTitle.parse(question);
        ScoreDoc[] hitTitle = isearcher.search(queryTitle, 10).scoreDocs;

        QueryParser parserContent = new QueryParser("Content",analyzer);
        Query queryContent = parserContent.parse(question);
        ScoreDoc[] hitContent = isearcher.search(queryContent, 10).scoreDocs;

        hits.addAll(new ArrayList<ScoreDoc>(List.of(hitTitle)));
        hits.addAll(new ArrayList<ScoreDoc>(List.of(hitContent)));

        // Iterate through the results:

        StoredFields storedFields = isearcher.storedFields();
        Collection<PageSearchResult> res = new ArrayList<PageSearchResult>();
        for (ScoreDoc hit : hits) {
            Document hitDoc = storedFields.document(hit.doc);
            String title = hitDoc.get("Title"); // Assuming 'title' is a stored field
            String content = hitDoc.get("Content");
            PageSearchResult result = new PageSearchResult(title+":"+content);
            res.add(result);
        }
        ireader.close();
        return res;
    }
}
