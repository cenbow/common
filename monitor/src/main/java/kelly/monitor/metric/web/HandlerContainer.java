package kelly.monitor.metric.web;


import com.google.common.base.Strings;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HandlerContainer {

    private static final String CARET = "^";

    private final String contextPath;
    private final ConcurrentMap<Pattern, RequestHandler> handlers = new ConcurrentHashMap<Pattern, RequestHandler>();

    public HandlerContainer() {
        this.contextPath = "";
    }

    public HandlerContainer(final String contextPath) {
        this.contextPath = contextPath;
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


    public void addRequestHandler(String pathRegex, RequestHandler handler) {
        handlers.put(Pattern.compile(addContextPath(pathRegex)), handler);
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
}
