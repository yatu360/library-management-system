package com.example.library.service;

import com.example.library.dto.BorrowRequest;
import com.example.library.dto.ReturnRequest;
import com.example.library.model.Book;
import com.example.library.model.BorrowRecord;
import com.example.library.model.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowServiceTest {

    private UserRepository userRepository;
    private BookRepository bookRepository;
    private BorrowRecordRepository borrowRecordRepository;
    private BorrowService borrowService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        bookRepository = mock(BookRepository.class);
        borrowRecordRepository = mock(BorrowRecordRepository.class);
        borrowService = new BorrowService(userRepository, bookRepository, borrowRecordRepository);
    }

    @Test
    void borrowBook_shouldSucceed_whenBookAvailable() {
        User user = User.builder().id(1L).name("John").build();
        Book book = Book.builder().id(1L).title("Java").availableCopies(1).totalCopies(2).build();

        BorrowRequest request = new BorrowRequest();
        request.setUserId(1L);
        request.setBookId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(i -> i.getArgument(0));

        String result = borrowService.borrowBook(request);

        assertEquals("Book borrowed successfully.", result);
        assertEquals(0, book.getAvailableCopies()); // should decrement
        verify(bookRepository).save(book);
        verify(borrowRecordRepository).save(any());
    }

    @Test
    void borrowBook_shouldFail_whenBookNotFound() {
        BorrowRequest request = new BorrowRequest();
        request.setUserId(1L);
        request.setBookId(999L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> borrowService.borrowBook(request));
        assertEquals("Book not found", ex.getMessage());
    }

    @Test
    void borrowBook_shouldFail_whenBookUnavailable() {
        User user = new User();
        Book book = Book.builder().id(1L).availableCopies(0).build();

        BorrowRequest request = new BorrowRequest();
        request.setUserId(1L);
        request.setBookId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        String result = borrowService.borrowBook(request);
        assertEquals("Book is not available.", result);
        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void returnBook_shouldSucceed_whenRecordExists() {
        User user = User.builder().id(1L).build();
        Book book = Book.builder().id(2L).availableCopies(0).build();
        BorrowRecord record = BorrowRecord.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .returnDate(null)
                .build();

        ReturnRequest request = new ReturnRequest();
        request.setUserId(1L);
        request.setBookId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(borrowRecordRepository.findByUser(user)).thenReturn(List.of(record));

        String result = borrowService.returnBook(request);

        assertEquals("Book returned successfully.", result);
        assertNotNull(record.getReturnDate());
        assertEquals(1, book.getAvailableCopies());
        verify(bookRepository).save(book);
        verify(borrowRecordRepository).save(record);
    }

    @Test
    void returnBook_shouldFail_whenNoRecordFound() {
        User user = User.builder().id(1L).build();
        Book book = Book.builder().id(2L).build();

        ReturnRequest request = new ReturnRequest();
        request.setUserId(1L);
        request.setBookId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(borrowRecordRepository.findByUser(user)).thenReturn(List.of()); // empty list

        String result = borrowService.returnBook(request);

        assertEquals("No active borrow record found for this book and user.", result);
        verify(bookRepository, never()).save(any());
        verify(borrowRecordRepository, never()).save(any());
    }
}
