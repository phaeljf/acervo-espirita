package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.service.backup.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @PostMapping("/backup")
    public String createBackup(RedirectAttributes redirectAttributes) {

        try {

            String backupFile = backupService.createBackup();

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Backup criado com sucesso: " + backupFile
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Erro ao gerar backup: " + e.getMessage()
            );
        }

        return "redirect:/configuration";
    }
}