//package org.example.backend.Entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "qr_codes")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class QRCode {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "dog_id", nullable = false, unique = true)
//    private Dog dog;
//
//    @Column(nullable = false)
//    private String qrCodePath;
//
//    @Column(nullable = false)
//    private String qrCodeContent;
//
//    @Column(updatable = false)
//    @Builder.Default
//    private LocalDateTime generatedAt = LocalDateTime.now();
//}