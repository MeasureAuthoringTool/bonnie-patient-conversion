package gov.cms.mat.patients.conversion.config.cache;

import gov.cms.mat.patients.conversion.service.GoogleDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
public class CacheEvict implements ApplicationListener<ApplicationReadyEvent> {
    private final CacheManager cacheManager;

    private final GoogleDataService googleDataService;

    public CacheEvict(CacheManager cacheManager, GoogleDataService googleDataService) {
        this.cacheManager = cacheManager;
        this.googleDataService = googleDataService;
    }

    // On the hour every hour
    @Scheduled(cron = "0 0 * * * *")
    public void evictAll() {
        cacheManager.getCacheNames().forEach(this::evict);

        update();
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent applicationReadyEvent) {
        update(); // pre load cache - without this and with massive threading the cached method can be called may times
    }

    private void update() {
        googleDataService.getCodeSystemEntries();
    }

    private void evict(String name) {
        var cache = cacheManager.getCache(name);

        if (cache == null) {
            log.error("Cache is null: {}", name); // Should never happen
        } else {
            cache.clear();
            log.info("Cleared cache: {}", name);
        }
    }
}
