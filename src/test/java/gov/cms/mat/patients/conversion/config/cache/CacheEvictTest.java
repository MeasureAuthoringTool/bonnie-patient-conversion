package gov.cms.mat.patients.conversion.config.cache;

import gov.cms.mat.patients.conversion.service.GoogleDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheEvictTest {
    @Mock
    private CacheManager cacheManager;
    @Mock
    private GoogleDataService googleDataService;
    @InjectMocks
    private CacheEvict cacheEvict;

    @Mock
    private Cache cache;


    @Test
    void evictAll() {
        when(cacheManager.getCacheNames()).thenReturn(List.of("MyCache"));
        when(cacheManager.getCache("MyCache")).thenReturn(cache);

        cacheEvict.evictAll();

        verify(cacheManager).getCacheNames();
        verify(cacheManager).getCache("MyCache");
        verifyNoMoreInteractions(cacheManager);

        verify(cache).clear();
        verifyNoMoreInteractions(cache);

        verify(googleDataService).getCodeSystemEntries();
        verifyNoMoreInteractions(googleDataService);
    }

    @Test
    void evictAllCacheNotFound() {
        when(cacheManager.getCacheNames()).thenReturn(List.of("MyCache"));
        when(cacheManager.getCache("MyCache")).thenReturn(null);

        cacheEvict.evictAll();

        verify(cacheManager).getCacheNames();
        verify(cacheManager).getCache("MyCache");
        verifyNoMoreInteractions(cacheManager);

        verifyNoInteractions(cache);

        verify(googleDataService).getCodeSystemEntries();
        verifyNoMoreInteractions(googleDataService);
    }
}