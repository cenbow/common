package kelly.monitor.core.servlet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface RequestHandler {

    void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException,
            IOException;
}