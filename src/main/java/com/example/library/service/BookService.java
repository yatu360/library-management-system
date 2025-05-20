package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@XSlf4j
@RequiredArgsConstructor
public class BookService {


    private final BookRepository bookRepository;

    public List<Book> searchBooks(String title, String author, String genre) {
        if (title != null && !title.isBlank()) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null && !author.isBlank()) {
            return bookRepository.findByAuthorContainingIgnoreCase(author);
        } else if (genre != null && !genre.isBlank()) {
            return bookRepository.findByGenreContainingIgnoreCase(genre);
        } else {
            return bookRepository.findAll();
        }
    }


}
