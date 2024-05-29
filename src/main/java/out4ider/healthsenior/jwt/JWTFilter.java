package out4ider.healthsenior.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import out4ider.healthsenior.domain.CustomUserDetails;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.UserAuthDto;
import out4ider.healthsenior.enums.Role;

import java.io.IOException;

@Slf4j
public class JWTFilter extends OncePerRequestFilter
{
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if(authorization == null || !authorization.startsWith("Bearer ")){
            log.info("token null");
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorization.substring("Bearer ".length());
        if (jwtUtil.isExpired(token)) {
            //trt-catch 문으로 잡아줘야 하고 토큰 재생성 해줘야 함
            log.info("token expired");
            filterChain.doFilter(request, response);
            return;
        }
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        UserAuthDto userAuthDto = new UserAuthDto(username, Role.USER);
        CustomUserDetails customOAuth2User = new CustomUserDetails(userAuthDto);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
