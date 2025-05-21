package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    void searchBooks_byTitle_shouldReturnMatchingBooks() {
        Book mockBook = Book.builder().title("Java 101").build();
        when(bookRepository.findByTitleContainingIgnoreCase("java"))
                .thenReturn(List.of(mockBook));

        List<Book> result = bookService.searchBooks("java", null, null);

        assertEquals(1, result.size());
        assertEquals("Java 101", result.get(0).getTitle());
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase("java");
    }

    @Test
    void searchBooks_byAuthor_shouldReturnMatchingBooks() {
        Book mockBook = Book.builder().author("Joshua Bloch").build();
        when(bookRepository.findByAuthorContainingIgnoreCase("bloch"))
                .thenReturn(List.of(mockBook));

        List<Book> result = bookService.searchBooks(null, "bloch", null);

        assertEquals(1, result.size());
        assertEquals("Joshua Bloch", result.get(0).getAuthor());
        verify(bookRepository, times(1)).findByAuthorContainingIgnoreCase("bloch");
    }

    @Test
    void searchBooks_byGenre_shouldReturnMatchingBooks() {
        Book mockBook = Book.builder().genre("Technology").build();
        when(bookRepository.findByGenreContainingIgnoreCase("tech"))
                .thenReturn(List.of(mockBook));

        List<Book> result = bookService.searchBooks(null, null, "tech");

        assertEquals(1, result.size());
        assertEquals("Technology", result.get(0).getGenre());
        verify(bookRepository, times(1)).findByGenreContainingIgnoreCase("tech");
    }

    @Test
    void searchBooks_noParams_shouldReturnAllBooks() {
        Book mockBook = Book.builder().title("Clean Code").build();
        when(bookRepository.findAll()).thenReturn(List.of(mockBook));

        List<Book> result = bookService.searchBooks(null, null, null);

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findAll();
    }
}