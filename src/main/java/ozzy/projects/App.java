package ozzy.projects;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ozzy.projects.DocumentManager.Author;
import ozzy.projects.DocumentManager.Document;
import ozzy.projects.DocumentManager.SearchRequest;

/**
 * Hello world!
 *
 */
public class App {
        public static void main(String[] args) {
                Author author = Author.builder().id("1").name("Ozzy").build();
                Author author1 = Author.builder().id("2").name("Ozzy").build();
                Author author2 = Author.builder().id("3").name("Ozzy").build();
                Author author3 = Author.builder().id("4").name("Ozzy").build();
                Author author4 = Author.builder().id("5").name("Ozzy").build();

                Document document = Document.builder().id("2").title("Title1").content("Content").author(author)
                                .created(Instant.now()).build();
                Document document1 = Document.builder().id("1").title("Title").content("Content1").author(author1)
                                .created(Instant.now()).build();
                Document document2 = Document.builder().id("3").title("Title2").content("Content2").author(author1)
                                .created(Instant.now()).build();
                Document document3 = Document.builder().id("4").title("Title3").content("Content3").author(author2)
                                .created(Instant.now()).build();
                Document document4 = Document.builder().id("5").title("Title4").content("Content4").author(author3)
                                .created(Instant.now()).build();

                DocumentManager documentManager = new DocumentManager();
                documentManager.save(document);
                documentManager.save(document1);
                documentManager.save(document2);
                documentManager.save(document3);
                documentManager.save(document4);

                Optional<Document> docById = documentManager.findById("10");
                if (docById.isPresent())
                        System.out.println(docById.get().getTitle());

                SearchRequest searchRequest = SearchRequest.builder().authorIds(Arrays.asList("5"))
                                .titlePrefixes(null).build();

                List<Document> documents = documentManager.search(searchRequest);
                System.out.println(documents.toString());
        }
}
