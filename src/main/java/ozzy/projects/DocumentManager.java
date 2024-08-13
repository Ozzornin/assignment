package ozzy.projects;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;

/**
 * For implement this task focus on clear code, and make this solution as simple
 * readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search,
 * findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private final Map<String, Document> documents = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        Document docToReplace = documents.get(document.getId());
        if (docToReplace == null)
            document.setId(String.valueOf(idGenerator.incrementAndGet()));
        else {
            document.setCreated(docToReplace.getCreated());
        }
        documents.put(document.getId(), document);

        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documents.values().stream()
                .filter(document -> request.getAuthorIds() == null
                        || request.getAuthorIds().contains(document.getAuthor().getId()))
                .filter(document -> request.getTitlePrefixes() == null ||
                        request.getTitlePrefixes().stream().anyMatch(prefix -> document.getTitle().startsWith(prefix)))
                .filter(document -> request.getCreatedFrom() == null
                        || !document.getCreated().isBefore(request.getCreatedFrom()))
                .filter(document -> request.getCreatedTo() == null
                        || !document.getCreated().isAfter(request.getCreatedTo()))
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {

        return Optional.ofNullable(documents.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}