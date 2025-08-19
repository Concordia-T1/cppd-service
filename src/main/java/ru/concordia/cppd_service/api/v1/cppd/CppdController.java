package ru.concordia.cppd_service.api.v1.cppd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.concordia.cppd_service.api.v1.cppd.model.CppdResponse;
import ru.concordia.cppd_service.api.v1.cppd.model.CppdUpdateRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cppd-service/v1/cppd")
public class CppdController {
    private final CppdService cppdService;

    @GetMapping({"/", ""})
    public ResponseEntity<CppdResponse> getCppdTemplate() {
        log.info("Request to get CPPD templates");
        return cppdService.getCppdTemplate();
    }

    @PutMapping("/update")
    public ResponseEntity<CppdResponse> updateCppdTemplate(
            @RequestBody CppdUpdateRequest request,
            @RequestHeader("X-User-Role") String principalRole
    ) {
        log.info("Request to update CPPD templates");
        return cppdService.updateCppdTemplate(request, principalRole);
    }
}
