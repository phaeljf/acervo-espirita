package com.acervo.acervoespirita.service.backup;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    private static final String DATABASE_PATH = "./database/acervo.db";

    private static final String BACKUP_FOLDER = "./backup/";

    public String createBackup() throws IOException {

        Path source = Paths.get(DATABASE_PATH);

        if (!Files.exists(source)) {
            throw new IllegalStateException("Banco de dados não encontrado.");
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));

        String backupFileName = "acervo-backup-" + timestamp + ".db";

        Path target = Paths.get(BACKUP_FOLDER + backupFileName);

        Files.createDirectories(target.getParent());

        Files.copy(source, target);

        return backupFileName;
    }
}