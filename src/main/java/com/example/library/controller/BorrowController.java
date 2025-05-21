package com.example.library.controller;

import com.example.library.dto.BorrowHistoryResponse;
import com.example.library.dto.BorrowRequest;
import com.example.library.dto.ReturnRequest;
import com.example.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping
    public String borrowBook(@RequestBody BorrowRequest request) {
        return borrowService.borrowBook(request);
    }

    @PostMapping("/return")
    public String returnBook(@RequestBody ReturnRequest request) {
        return borrowService.returnBook(request);
    }

    @GetMapping("/history/{userId}")
    public List<BorrowHistoryResponse> getHistory(@PathVariable Long userId) {
        return borrowService.getBorrowHistory(userId);
    }


}
