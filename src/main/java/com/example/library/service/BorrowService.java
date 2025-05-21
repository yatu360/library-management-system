package com.example.library.service;

import com.example.library.dto.BorrowHistoryResponse;
import com.example.library.dto.BorrowRequest;
import com.example.library.dto.ReturnRequest;
import com.example.library.model.Book;
import com.example.library.model.BorrowRecord;
import com.example.library.model.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {


    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public String borrowBook(BorrowRequest borrowRequest) {
        User user = userRepository.findById(borrowRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(borrowRequest.getBookId())
                .orElseThrow(()-> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            return "Book is not available.";
        }

        book.setAvailableCopies(book.getAvailableCopies() -1);
        bookRepository.save(book);

        BorrowRecord record = BorrowRecord.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .build();
        borrowRecordRepository.save(record);

        return "Book borrowed successfully.";
    }

    public String returnBook(ReturnRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(()-> new RuntimeException(("User not found")));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(()-> new RuntimeException("Cannot find book"));

        BorrowRecord record = borrowRecordRepository.findByUser(user).stream()
                .filter(r->r.getBook().getId().equals(book.getId()) && r.getReturnDate() == null)
                .findFirst()
                .orElse(null);

        if (record == null) {
            return "No active borrow record found for this book and user.";
        }

        record.setReturnDate(LocalDate.now());
        borrowRecordRepository.save(record);

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return "Book returned successfully.";

    }

    public List<BorrowHistoryResponse> getBorrowHistory(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));

        return borrowRecordRepository.findByUser(user).stream()
                .map(record -> BorrowHistoryResponse.builder()
                        .bookTitle(record.getBook().getTitle())
                        .borrowDate(record.getBorrowDate())
                        .returnDate(record.getReturnDate()).build()).toList();
    }



}
