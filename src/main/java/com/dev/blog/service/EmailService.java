package com.dev.blog.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.admin}")
    private String adminEmail;

    public void envoyerConfirmationDon(String to, String nomDonateur, BigDecimal montant, String nomProjet) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Confirmation de votre don - " + nomProjet);
            helper.setText(buildConfirmationDonHtml(nomDonateur, montant, nomProjet), true);
            mailSender.send(message);
            log.info("Email de confirmation envoyé à {}", to);
        } catch (Exception e) {
            log.warn("Impossible d'envoyer l'email de confirmation à {} : {}", to, e.getMessage());
        }
    }

    public void envoyerObjectifAtteint(String nomProjet, BigDecimal objectif) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("Objectif atteint - " + nomProjet);
            helper.setText(buildObjectifAtteintHtml(nomProjet, objectif), true);
            mailSender.send(message);
            log.info("Email objectif atteint envoyé pour le projet {}", nomProjet);
        } catch (Exception e) {
            log.warn("Impossible d'envoyer l'email objectif atteint pour {} : {}", nomProjet, e.getMessage());
        }
    }

    public void envoyerMiseAJourProjet(List<String> emails, String nomProjet,
                                        String ancienStatut, String nouveauStatut) {
        if (emails == null || emails.isEmpty()) return;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(emails.toArray(new String[0]));
            helper.setSubject("Mise a jour du projet - " + nomProjet);
            helper.setText(buildMiseAJourProjetHtml(nomProjet, ancienStatut, nouveauStatut), true);
            mailSender.send(message);
            log.info("Email mise à jour envoyé pour {} à {} destinataire(s)", nomProjet, emails.size());
        } catch (Exception e) {
            log.warn("Impossible d'envoyer l'email de mise à jour pour {} : {}", nomProjet, e.getMessage());
        }
    }

    private String buildConfirmationDonHtml(String nomDonateur, BigDecimal montant, String nomProjet) {
        String body = ("<p>Bonjour <strong>%s</strong>,</p>" +
                "<p>Nous avons bien re&#231;u votre don de " +
                "<strong style=\"color:#4CAF50;font-size:18px;\">%.2f MAD</strong> " +
                "pour le projet <strong>%s</strong>.</p>" +
                "<p>Votre g&#233;n&#233;rosit&#233; contribue &#224; changer des vies. " +
                "Merci infiniment pour votre soutien&nbsp;!</p>")
                .formatted(nomDonateur, montant, nomProjet);
        return buildEmailHtml("#4CAF50", "Don confirm&#233;", body);
    }

    private String buildObjectifAtteintHtml(String nomProjet, BigDecimal objectif) {
        String body = ("<p>Le projet <strong>%s</strong> a atteint son objectif de collecte de " +
                "<strong style=\"color:#FF9800;font-size:18px;\">%.2f MAD</strong>&nbsp;!</p>" +
                "<p>Merci &#224; tous les g&#233;n&#233;reux donateurs qui ont rendu cela possible.</p>")
                .formatted(nomProjet, objectif);
        return buildEmailHtml("#FF9800", "Objectif atteint !", body);
    }

    private String buildMiseAJourProjetHtml(String nomProjet, String ancienStatut, String nouveauStatut) {
        String body = ("<p>Le statut du projet <strong>%s</strong> vient d&#8217;&#234;tre mis &#224; jour&nbsp;:</p>" +
                "<p style=\"text-align:center;\">" +
                "<span style=\"background:#eee;padding:4px 12px;border-radius:4px;\">%s</span>" +
                "&nbsp;&rarr;&nbsp;" +
                "<span style=\"background:#2196F3;color:#fff;padding:4px 12px;border-radius:4px;\">%s</span></p>" +
                "<p>Merci de votre soutien continu &#224; cette action caritative.</p>")
                .formatted(nomProjet, ancienStatut, nouveauStatut);
        return buildEmailHtml("#2196F3", "Mise &#224; jour du projet", body);
    }

    private String buildEmailHtml(String color, String title, String body) {
        return ("<!DOCTYPE html><html lang=\"fr\"><body style=\"font-family:Arial,sans-serif;" +
                "max-width:600px;margin:0 auto;padding:20px;\">" +
                "<div style=\"background:%s;color:#fff;padding:24px;border-radius:8px 8px 0 0;text-align:center;\">" +
                "<h1 style=\"margin:0;font-size:22px;\">%s</h1></div>" +
                "<div style=\"background:#f9f9f9;padding:24px;border-radius:0 0 8px 8px;border:1px solid #e0e0e0;\">" +
                "%s" +
                "<hr style=\"border:none;border-top:1px solid #e0e0e0;margin:20px 0;\">" +
                "<p style=\"color:#aaa;font-size:12px;\">&#201;quipe Charity</p>" +
                "</div></body></html>").formatted(color, title, body);
    }
}
