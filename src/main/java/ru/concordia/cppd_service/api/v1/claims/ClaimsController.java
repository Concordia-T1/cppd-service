package ru.concordia.cppd_service.api.v1.claims;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.concordia.cppd_service.service.exceptions.EcdhContextExpiredException;
import ru.concordia.cppd_service.api.v1.model.SuccessResponse;
import ru.concordia.cppd_service.api.v1.claims.model.ClaimResponse;
import ru.concordia.cppd_service.api.v1.claims.model.ClaimsCollectionResponse;
import ru.concordia.cppd_service.api.v1.claims.model.IssueResponse;

@Slf4j
@RestController
@RequestMapping("/api/cppd-service/v1/claims")
public class ClaimsController {
    private final ClaimsService claimsService;

    public ClaimsController(ClaimsService claimsService) {
        this.claimsService = claimsService;
    }

    @GetMapping({"/", ""})
    public ResponseEntity<ClaimsCollectionResponse> collection(
            @PageableDefault(size = 50, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("X-User-Role") String principalRole
    ) {
        return claimsService.collection(principalRole, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> scalar(
            @Valid @PathVariable Long id,
            @RequestHeader("X-User-ID") Long principalId,
            @RequestHeader("X-User-Role") String principalRole
    ) {
        return claimsService.scalar(id, principalId, principalRole);
    }

    @GetMapping("/my")
    public ResponseEntity<ClaimsCollectionResponse> myCollection(
            @PageableDefault(size = 50, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("X-User-ID") Long principalId
    ) {
        log.info("X-User-ID: {}", principalId);
        return claimsService.myCollection(principalId, pageable);
    }

    @GetMapping("/issue")
    public ResponseEntity<IssueResponse> issue() {
        return claimsService.issue();
    }

    @GetMapping("/act")
    public ResponseEntity<SuccessResponse> act(
            @Valid @RequestParam String epk,
            @Valid @RequestParam String ctx,
            @Valid @RequestParam String sig
    ) throws EcdhContextExpiredException {
        return claimsService.act(epk, ctx, sig);
    }
}
