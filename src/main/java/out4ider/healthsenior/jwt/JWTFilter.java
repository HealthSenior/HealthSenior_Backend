package out4ider.healthsenior.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import out4ider.healthsenior.domain.CustomUserDetails;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.UserAuthDto;
import out4ider.healthsenior.enums.Role;

import java.io.IOException;
import java.io.PrintWriter;

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
            if(!request.getContextPath().equals("/reissue")) {
                System.out.println("access token null");
                filterChain.doFilter(request, response);
            }
            else{
                System.out.println("access token empty");
                PrintWriter writer = response.getWriter();
                writer.print("access token empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }
        String token = authorization.substring("Bearer ".length());
        String username = jwtUtil.getUsername(token);
        try{
            jwtUtil.isExpired(token);
        }
        catch(ExpiredJwtException e){
            if(!request.getContextPath().equals("/reissue")) {
                System.out.println("access token expired");
                PrintWriter writer = response.getWriter();
                writer.print("access token expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            else {
                filterChain.doFilter(request, response);
            }
            return;
        }

        String category = jwtUtil.getCategory(token);
        if(!category.equals("access")) {
            System.out.println("category not access");
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String role = jwtUtil.getRole(token);
        UserAuthDto userAuthDto = new UserAuthDto(username, Role.USER);
        CustomUserDetails customOAuth2User = new CustomUserDetails(userAuthDto);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
