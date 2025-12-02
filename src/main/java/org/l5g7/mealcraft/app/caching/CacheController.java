package org.l5g7.mealcraft.app.caching;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/cache")
public class CacheController {
    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/clear")
    public ResponseEntity<String> clearCache() {
        return ResponseEntity.ok(cacheService.clearCache());
    }
}
