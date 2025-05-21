package com.example.library.service;

import com.example.library.dto.BorrowRequest;
import com.example.library.model.Book;
import com.example.library.model.BorrowRecord;
import com.example.library.model.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public String borrowBook(BorrowRequest borrowRequest) {
        User user = userRepository.findById(borrowRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(borrowRequest.getBookId())
                .orElseThrow(()-> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            return "Book is not available";
        }

        book.setAvailableCopies(book.getAvailableCopies() -1);
        bookRepository.save(book);

        BorrowRecord record = BorrowRecord.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .build();
        borrowRecordRepository.save(record);

        return "Book borrowed successfully";
    }
}
