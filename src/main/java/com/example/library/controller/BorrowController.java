package com.example.library.controller;

import com.example.library.dto.BorrowRequest;
import com.example.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping
    public String borrowBook(@RequestBody BorrowRequest request) {
        return borrowService.borrowBook(request);
    }

}
