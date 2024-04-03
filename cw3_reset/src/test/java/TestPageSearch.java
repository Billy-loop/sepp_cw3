
import model.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class TestPageSearch {
    private PageSearch pageSearch;
    private ArrayList<Page> pages;

    @BeforeEach
    public void setUp() throws IOException {
        pages = new ArrayList<Page>();
    }

    @Test
    public void coreTest() throws IOException, ParseException {
        this.pages.add(new Page("file1", "test1", false));
        this.pages.add(new Page("file2", "test1", false));
        this.pageSearch = new PageSearch(this.pages);
        assertEquals(2, this.pageSearch.search("test1").size());
    }

}



