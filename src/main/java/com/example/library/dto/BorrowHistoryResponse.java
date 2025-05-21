package com.example.library.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Builder
public class BorrowHistoryResponse {

    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate returnDate;

}
