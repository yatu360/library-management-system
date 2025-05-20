package com.example.library.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class BorrowRequest {

    private Long userId;
    private Long bookId;

}
