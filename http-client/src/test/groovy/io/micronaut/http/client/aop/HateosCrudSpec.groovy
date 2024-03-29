package io.micronaut.http.client.aop

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.hateoas.AbstractResource
import io.micronaut.http.hateoas.Link
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicLong

/**
 * @author graemerocher
 * @since 1.0
 */

class HateoasCrudSpec extends Specification {

    @AutoCleanup
    EmbeddedServer server = ApplicationContext.run(EmbeddedServer, [
            'spec.name':'HateoasCrudSpec',
    ])

    BookClient client = server.applicationContext.getBean(BookClient)

    void "test CRUD operations on generated client that returns blocking responses"() {
        when:
        Book book = client.get(99)
        List<Book> books = client.list()

        then:
        book == null
        books.size() == 0

        when:
        book = client.save("The Stand")

        then:
        book != null
        book.title == "The Stand"
        book.id == 1
        book.getLinks().getFirst("self").get().href.toString() == "/book/1"

        when:
        book = client.get(book.id)

        then:
        book != null
        book.title == "The Stand"
        book.id == 1

        when:
        book = client.update(book.id, "The Shining")

        then:
        book != null
        book.title == "The Shining"
        book.id == 1

        when:
        client.delete(book.id)

        then:
        client.get(book.id) == null
    }

    @Client('/hateoas/books')
    @Requires(property = 'spec.name', value = 'HateoasCrudSpec')
    static interface BookClient extends BookApi {
    }

    @Controller("/hateoas/books")
    @Requires(property = 'spec.name', value = 'HateoasCrudSpec')
    static class BookController implements BookApi {

        Map<Long, Book> books = new LinkedHashMap<>()
        AtomicLong currentId = new AtomicLong(0)

        @Override
        Book get(Long id) {
            return books.get(id)
        }

        @Override
        List<Book> list() {
            return books.values().toList()
        }

        @Override
        void delete(Long id) {
            books.remove(id)
        }

        @Override
        Book save(String title) {
            Book book = new Book(title: title, id:currentId.incrementAndGet())
            book.link('self', Link.of("/book/$book.id"))
            books[book.id] = book
            return book
        }

        @Override
        Book save(Long id, String embedded) {
            Book book = books[id]
            if(book != null) {
                book.embedded(embedded, new Book(title: embedded))
            }
            return book

        }

        @Override
        Book update(Long id, String title) {
            Book book = books[id]
            if(book != null) {
                book.title = title
            }
            return book
        }
    }

    static interface BookApi {

        @Get(value = "/{id}", processes = MediaType.APPLICATION_HAL_JSON)
        Book get(Long id)

        @Get
        List<Book> list()

        @Delete("/{id}")
        void delete(Long id)

        @Post(processes = MediaType.APPLICATION_HAL_JSON)
        Book save(String title)

        @Post(value= '/{id}/{embedded}', processes = MediaType.APPLICATION_HAL_JSON)
        Book save(Long id, String embedded)

        @Patch(value = "/{id}", processes = MediaType.APPLICATION_HAL_JSON)
        Book update(Long id, String title)
    }


    static class Book extends AbstractResource<Book> {
        Long id
        String title
    }
}
