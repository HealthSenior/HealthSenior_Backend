package out4ider.healthsenior.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import out4ider.healthsenior.service.RefreshTokenService;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilter {

    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    public CustomLogoutFilter(RefreshTokenService refreshTokenService, JWTUtil jwtUtil) {
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }
        String refresh = null;
        refresh = request.getHeader("Refresh");

        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        boolean isExist= refreshTokenService.existRefreshToken(refresh);
        if (!isExist) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        refreshTokenService.deleteRefreshToken(refresh);
        SecurityContextHolder.clearContext();
        //200 응답 받으면 프론트에서 액세스 토큰 삭제하도록
        response.setHeader("Refresh", null);
        response.setHeader("Authorization",null);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
