package ru.astondevs.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserEmailEvent {
    private String emailAddress;
    private String subject;
    private String message;
}


