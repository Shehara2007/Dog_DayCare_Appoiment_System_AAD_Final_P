package org.example.backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class QRCodeService {

    @Value("${app.qrcode.dir}")
    private String qrCodeDir;

    public String generateQRCode(Long dogId, String baseUrl) throws WriterException, IOException {
        String content = baseUrl + "/api/dogs/" + dogId + "/qr-public";

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);

        Path qrDir = Paths.get(qrCodeDir);
        if (!Files.exists(qrDir)) {
            Files.createDirectories(qrDir);
        }

        String fileName = "dog_" + dogId + "_qr.png";
        Path filePath = qrDir.resolve(fileName);

        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
        log.info("QR code generated for dog ID: {}", dogId);

        return filePath.toString();
    }

    public String getQRCodeContent(Long dogId, String baseUrl) {
        return baseUrl + "/api/dogs/" + dogId + "/qr-public";
    }
}