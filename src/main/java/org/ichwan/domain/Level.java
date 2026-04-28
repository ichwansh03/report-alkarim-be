package org.ichwan.domain;

import jakarta.persistence.*;
import org.ichwan.util.LevelType;

@Entity
@Table(name = "levels")
public class Level {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column(name = "level_type", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private LevelType levelType;
}
