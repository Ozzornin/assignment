package ozzy.projects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import ozzy.projects.DocumentManager.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class DocumentManagerTest {

    private DocumentManager documentManager;

    @Before
    public void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    public void saveNewDocument_ShouldGenerateIdAndSave() {
        Author author = Author.builder()
                .id("1")
                .name("Author Name")
                .build();

        Document document = Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(author)
                .created(Instant.now())
                .build();

        Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals(document, savedDocument);
        assertEquals(savedDocument, documentManager.findById(savedDocument.getId()).get());
    }

    @Test
    public void saveExistingDocument_ShouldUpdateDocument() {
        Author author = Author.builder()
                .id("1")
                .name("Author Name")
                .build();

        Document document = Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(author)
                .created(Instant.now())
                .build();

        Document savedDocument = documentManager.save(document);
        String savedId = savedDocument.getId();
        Instant originalCreatedDate = savedDocument.getCreated();

        Document updatedDocument = Document.builder()
                .id(savedId)
                .title("Updated Title")
                .content("Updated Content")
                .author(author)
                .created(Instant.now())
                .build();

        Document resultDocument = documentManager.save(updatedDocument);

        assertEquals(savedId, resultDocument.getId());
        assertEquals("Updated Title", resultDocument.getTitle());
        assertEquals("Updated Content", resultDocument.getContent());
        assertEquals(originalCreatedDate, resultDocument.getCreated());
    }

    @Test
    public void findById_ShouldReturnDocument() {

        Author author = Author.builder()
                .id("1")
                .name("Author Name")
                .build();

        Document document = Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(author)
                .created(Instant.now())
                .build();

        Document savedDocument = documentManager.save(document);

        Optional<Document> foundDocument = documentManager.findById(savedDocument.getId());

        assertTrue(foundDocument.isPresent());
        assertEquals(savedDocument, foundDocument.get());
    }

    @Test
    public void findById_ShouldReturnEmptyOptionalIfNotFound() {

        Optional<Document> foundDocument = documentManager.findById("non-existent-id");

        assertFalse(foundDocument.isPresent());
    }

    @Test
    public void search_ShouldReturnDocumentsByAuthorId() {

        Author author1 = Author.builder()
                .id("1")
                .name("Author One")
                .build();

        Author author2 = Author.builder()
                .id("2")
                .name("Author Two")
                .build();

        Document document1 = Document.builder()
                .title("Document 1")
                .content("Content 1")
                .author(author1)
                .created(Instant.now())
                .build();

        Document document2 = Document.builder()
                .title("Document 2")
                .content("Content 2")
                .author(author2)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        SearchRequest request = SearchRequest.builder()
                .authorIds(List.of("1"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("Document 1", results.get(0).getTitle());
    }

    @Test
    public void search_ShouldReturnDocumentsByTitlePrefix() {

        Author author = Author.builder()
                .id("1")
                .name("Author One")
                .build();

        Document document1 = Document.builder()
                .title("ABC Document")
                .content("Content 1")
                .author(author)
                .created(Instant.now())
                .build();

        Document document2 = Document.builder()
                .title("XYZ Document")
                .content("Content 2")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(List.of("ABC"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("ABC Document", results.get(0).getTitle());
    }

    @Test
    public void search_ShouldReturnDocumentsByCreatedDateRange() {

        Author author = Author.builder()
                .id("1")
                .name("Author One")
                .build();

        Instant now = Instant.now();

        Document document1 = Document.builder()
                .title("Document 1")
                .content("Content 1")
                .author(author)
                .created(now.minusSeconds(3600))
                .build();

        Document document2 = Document.builder()
                .title("Document 2")
                .content("Content 2")
                .author(author)
                .created(now.minusSeconds(1800))
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        SearchRequest request = SearchRequest.builder()
                .createdFrom(now.minusSeconds(4000))
                .createdTo(now.minusSeconds(1000))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(2, results.size());
    }

    @Test
    public void search_ShouldReturnEmptyListWhenNoDocumentsMatch() {

        Author author = Author.builder()
                .id("1")
                .name("Author One")
                .build();

        Document document1 = Document.builder()
                .title("Document 1")
                .content("Content 1")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);

        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(List.of("XYZ"))
                .build();

        List<Document> results = documentManager.search(request);

        assertTrue(results.isEmpty());
    }
}
