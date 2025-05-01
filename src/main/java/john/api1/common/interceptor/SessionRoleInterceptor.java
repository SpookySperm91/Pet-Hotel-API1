package john.api1.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import john.api1.application.ports.services.ISessionService;
import john.api1.common.session.SessionRole;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionRoleInterceptor implements HandlerInterceptor {
    private ISessionService sessionService;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler)
            throws Exception {

        if (!(handler instanceof HandlerMethod method)) return true;

        String token = request.getHeader("Authorization");

        if (method.hasMethodAnnotation(InterceptorAdmin.class)) {
            if (!sessionService.validateSessionToken(token, SessionRole.ADMIN)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Admin access required.");
                return false;
            }
        } else if (method.hasMethodAnnotation(InterceptorUser.class)) {
            if (!sessionService.validateSessionToken(token, SessionRole.PET_OWNER)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Pet-owner access required.");
                return false;
            }
        }

        return true;
    }
}
