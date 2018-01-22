package kelly.monitor.metric.web;

import com.google.common.base.Strings;
import kelly.monitor.metric.Metrics;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kelly-lee on 2018/1/16.
 */
@Order(1)
@WebFilter(filterName = "ServletWatcher", urlPatterns = "/*")
public class ServletWatcher implements Filter {

    private final ConcurrentMap<Pattern, RequestHandler> handlers = new ConcurrentHashMap<Pattern, RequestHandler>();
    private static final String CARET = "^";
    private String contextPath = "";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Metrics.INSTANCE.afterPropertiesSet();
        handlers.put(Pattern.compile(addContextPath("^/_/metrics([^?]*)(.*)")), new MetricsHandler());
    }


    private String addContextPath(final String pathRegex) {
        final String newPathRegex = removeLeadingCaret(pathRegex);
        if (isBeginWithSlash(newPathRegex)) {
            return CARET + contextPath + newPathRegex;
        } else {
            return pathRegex;
        }
    }

    private boolean isBeginWithSlash(final String regex) {
        return !Strings.isNullOrEmpty(regex) && regex.startsWith("/");
    }

    private String removeLeadingCaret(final String regex) {
        if (!Strings.isNullOrEmpty(regex) && regex.startsWith(CARET)) {
            return regex.substring(CARET.length());
        } else {
            return regex;
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            if (handle(request, response, filterChain)) {
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (Exception e) {

        }
    }

    public boolean handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        RequestHandler handler = null;
        Matcher matcher = null;

        for (Map.Entry<Pattern, RequestHandler> e : handlers.entrySet()) {
            matcher = e.getKey().matcher(requestURI);
            if (matcher.find()) {
                handler = e.getValue();
                break;
            }
        }

        if (handler == null) {
            return false;
        }

        handler.handle(request, response, chain);
        return true;
    }

    @Override
    public void destroy() {

    }
}
