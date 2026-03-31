package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.HealthReport;
import org.example.backend.dto.CreateHealthReportRequest;
import org.example.backend.service.DogService;
import org.example.backend.service.HealthReportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health-reports")
@RequiredArgsConstructor
public class HealthReportController {

    private final HealthReportService healthReportService;
    private final DogService dogService;

    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody CreateHealthReportRequest request) {
        return mapHealthReport(healthReportService.create(request));
    }

    @GetMapping
    public List<Map<String, Object>> byDog(@RequestParam Long dogId) {
        return healthReportService.getByDog(dogId).stream().map(this::mapHealthReport).toList();
    }

    @GetMapping("/qr/{qrToken}")
    public Map<String, Object> byQrToken(@PathVariable String qrToken) {
        Dog dog = dogService.getByQrAccessToken(qrToken);
        List<Map<String, Object>> reports = healthReportService.getByQrToken(qrToken)
                .stream()
                .map(this::mapHealthReport)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("dogId", dog.getId());
        response.put("dogName", dog.getName());
        response.put("breed", dog.getBreed());
        response.put("reports", reports);
        return response;
    }

    @GetMapping(value = "/qr-ui/{qrToken}", produces = MediaType.TEXT_HTML_VALUE)
    public String byQrTokenUi(@PathVariable String qrToken) {
        Dog dog = dogService.getByQrAccessToken(qrToken);
        List<HealthReport> reports = healthReportService.getByQrToken(qrToken);

        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><meta charset='utf-8'>")
                .append("<meta name='viewport' content='width=device-width,initial-scale=1'>")
                .append("<title>Dog Health Reports</title>")
                .append("<style>")
                .append("body{font-family:Arial,sans-serif;background:#f5f7fb;margin:0;padding:20px;color:#1f2937;}")
                .append(".card{max-width:900px;margin:0 auto;background:#fff;border-radius:12px;padding:20px;box-shadow:0 2px 14px rgba(0,0,0,.08);}")
                .append("h1{margin:0 0 8px;font-size:24px;} .meta{color:#6b7280;margin-bottom:16px;}")
                .append("table{width:100%;border-collapse:collapse;}th,td{padding:10px;border-bottom:1px solid #e5e7eb;text-align:left;}")
                .append("th{background:#f9fafb;} .chip{padding:2px 8px;border-radius:999px;font-size:12px;font-weight:700;}")
                .append(".good{background:#dcfce7;color:#166534;} .bad{background:#fee2e2;color:#991b1b;}")
                .append(".muted{color:#6b7280;}")
                .append("</style></head><body><div class='card'>");

        html.append("<h1>").append(escapeHtml(dog.getName())).append(" - Health Reports</h1>")
                .append("<div class='meta'>Dog ID: ").append(dog.getId())
                .append(" | Breed: ").append(escapeHtml(dog.getBreed()))
                .append("</div>")
                .append("<p>While ").append(escapeHtml(dog.getName())).append(" remains behaviorally active, his clinical health indicators are currently poor. He may be experiencing minor internal discomfort or digestive sensitivity that requires immediate attention.</p>")
                .append("<ul>")
                .append("<li>Transition to Light Foods: Avoid heavy proteins or high-fat treats</li>")
                .append("<li>Recommended Diet: Boiled chicken or white fish with plain rice or pumpkin</li>")
                .append("<li>Hydration: Ensure constant access to fresh, clean water</li>")
                .append("</ul>")
                .append("<h2>🛠️ PawCare Service Summary</h2>")
                .append("<ul>")
                .append("<li>✔ Regular Health Check – Vital signs and physical examination completed</li>")
                .append("<li>✔ Nutrition Guidance – Specialized Light Diet plan provided</li>")
                .append("<li>✔ Body Care – Basic paw and coat inspection done</li>")
                .append("<li>✔ Monitoring – Continuous health tracking initiated</li>")
                .append("</ul>")
                .append("<p><strong>Strict Monitoring:</strong> Observe energy levels and stool condition for the next 48 hours</p>")
                .append("<p><strong>Diet Compliance:</strong> Follow the light food recommendation strictly</p>")
                .append("<p><strong>Veterinary Care:</strong> If condition worsens or activity decreases, consult a veterinarian immediately</p>");

        if (reports.isEmpty()) {
            html.append("<p class='muted'>No health reports available yet.</p>");
        } else {
            html.append("<table><thead><tr><th>Date</th><th>Behaviour</th><th>Health</th><th>Notes</th></tr></thead><tbody>");
            for (HealthReport report : reports) {
                String chipClass = report.getHealthStatus() != null && "BAD".equals(report.getHealthStatus().name()) ? "bad" : "good";
                html.append("<tr>")
                        .append("<td>").append(report.getCreatedAt()).append("</td>")
                        .append("<td>").append(report.getBehaviour() == null ? "-" : report.getBehaviour().name()).append("</td>")
                        .append("<td><span class='chip ").append(chipClass).append("'>")
                        .append(report.getHealthStatus() == null ? "-" : report.getHealthStatus().name())
                        .append("</span></td>")
                        .append("<td>").append(escapeHtml(report.getNotes() == null ? "" : report.getNotes())).append("</td>")
                        .append("</tr>");
            }
            html.append("</tbody></table>");
        }

        html.append("</div></body></html>");
        return html.toString();
    }

    private Map<String, Object> mapHealthReport(HealthReport report) {
        return Map.of(
                "id", report.getId(),
                "dogId", report.getDog().getId(),
                "createdById", report.getCreatedBy().getId(),
                "behaviour", report.getBehaviour(),
                "healthStatus", report.getHealthStatus(),
                "notes", report.getNotes() == null ? "" : report.getNotes(),
                "createdAt", report.getCreatedAt()
        );
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
