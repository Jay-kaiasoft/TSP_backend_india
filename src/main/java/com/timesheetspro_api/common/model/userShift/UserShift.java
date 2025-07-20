package com.timesheetspro_api.common.model.userShift;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_shift")
@Setter
@Getter
@NoArgsConstructor
public class UserShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "shift_name", columnDefinition = "Char")
    private String shiftName;
}
