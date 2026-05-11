package com.mogak.spring.seed;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SeedFileTest {

    @Test
    void addressSeedContainsGyeonggiDoAfterSeoul() throws IOException {
        String seed = Files.readString(Path.of("sql", "seed", "00_initial_data.sql"));

        assertThat(seed).contains("('서울특별시'),\n        ('경기도')");
    }
}
