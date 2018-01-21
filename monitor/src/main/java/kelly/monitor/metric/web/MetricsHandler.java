package kelly.monitor.metric.web;

import kelly.monitor.metric.MetricsReportor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;


public class MetricsHandler implements RequestHandler {
    public static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";
    MetricsReportor reportor = new MetricsReportor();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE_TEXT);
        response.setHeader("Content-Encoding", "gzip");
        GZIPOutputStream out = new GZIPOutputStream(response.getOutputStream());
        PrintWriter writer = new PrintWriter(out, true);
        try {

            reportor.report(writer);
        } catch (Exception e) {

        } finally {
            out.finish();//结束压缩
            out.flush();//zip流刷入网络层
            writer.close();//结束输出
        }
    }

}
