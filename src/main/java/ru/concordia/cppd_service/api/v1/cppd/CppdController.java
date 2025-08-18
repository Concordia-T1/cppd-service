package ru.concordia.cppd_service.api.v1.cppd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.concordia.cppd_service.api.v1.cppd.service.CppdService;
import ru.concordia.cppd_service.dto.CPPDResponse;
import ru.concordia.cppd_service.dto.CPPDUpdateRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cppd")
public class CppdController {

    private final CppdService cppdService;

    @GetMapping
    public ResponseEntity<CPPDResponse> getCppdTemplate() {
        log.info("Request to get CPPD template");
        return cppdService.getCppdTemplate();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<CPPDResponse> updateCppdTemplate(@RequestBody CPPDUpdateRequest request) {
        log.info("Request to update CPPD template");
        return cppdService.updateCppdTemplate(request);
    }
}
