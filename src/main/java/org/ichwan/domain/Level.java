package org.ichwan.domain;

import jakarta.persistence.*;
import org.ichwan.util.LevelType;

@Entity
public class Level {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private LevelType levelType;
}
