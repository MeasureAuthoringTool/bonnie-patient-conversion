package gov.cms.mat.patients.conversion.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.ServletException;
import java.io.IOException;

import static gov.cms.mat.patients.conversion.config.SecurityFilter.HEADER_MAT_API_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {
    private static final String MAT_API_KEY_VALUE = "GOLDEN-TICKET";

    @InjectMocks
    SecurityFilter securityFilter;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    MockFilterChain filterChain;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @Test
    void doFilterSetHeaderAndSetKey() throws IOException, ServletException {
        securityFilter.init(null);

        ReflectionTestUtils.setField(securityFilter, "matApiKey", "DISABLED");

        securityFilter.doFilter(request, response, filterChain);
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        securityFilter.destroy();
    }

    @Test
    void doFilterHeaderNullAndSetKey() throws IOException, ServletException {
        ReflectionTestUtils.setField(securityFilter, "matApiKey", MAT_API_KEY_VALUE);

        securityFilter.doFilter(request, response, filterChain);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void doFilterHeaderSetAndSetKey() throws IOException, ServletException {
        ReflectionTestUtils.setField(securityFilter, "matApiKey", MAT_API_KEY_VALUE);
        request.addHeader(HEADER_MAT_API_KEY, MAT_API_KEY_VALUE);

        securityFilter.doFilter(request, response, filterChain);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void doFilterHeaderBADAndSetKey() throws IOException, ServletException {
        ReflectionTestUtils.setField(securityFilter, "matApiKey", MAT_API_KEY_VALUE);
        request.addHeader(HEADER_MAT_API_KEY, "Mickey_Mouse");

        securityFilter.doFilter(request, response, filterChain);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void doFilterHeaderBADAndSetKeyCheckWhiteListUrl() throws IOException, ServletException {
        ReflectionTestUtils.setField(securityFilter, "matApiKey", MAT_API_KEY_VALUE);
        request.addHeader(HEADER_MAT_API_KEY, "Mickey_Mouse");

        request.setMethod("GET");
        request.setRequestURI("/actuator/health");

        securityFilter.doFilter(request, response, filterChain);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void doFilterSetHeaderNullAndKeyNull() throws IOException, ServletException {
        ReflectionTestUtils.setField(securityFilter, "matApiKey", null);

        securityFilter.doFilter(request, response, filterChain);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }
}