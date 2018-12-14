package com.dr.sweetie.service.impl;

import com.dr.sweetie.domain.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qewli12
 */
@Service
public class BookService {

    private static Map<Long, Book> BOOK_DB = new HashMap<>();

    public List<Book> findAll() {
        return new ArrayList<>(BOOK_DB.values());
    }

    public Book insertByBook(Book book) {
        book.setId(BOOK_DB.size() + 1L);
        BOOK_DB.put(book.getId(), book);
        return book;
    }

    public Book update(Book book) {
        BOOK_DB.put(book.getId(), book);
        return book;
    }

    public Book delete(Long id) {
        return BOOK_DB.remove(id);
    }

    public Book findById(Long id) {
        return BOOK_DB.get(id);
    }

}
