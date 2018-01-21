package kelly.monitor.core;

import com.google.common.collect.ImmutableMap;
import kelly.monitor.core.servlet.RequestHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.zip.GZIPOutputStream;


// 应用指标信息 meta
public class MetricsHandler implements RequestHandler {
    public static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // specify metric name
        final String name = request.getParameter("name");
        final String format = request.getParameter("format");

        response.setContentType(CONTENT_TYPE_TEXT);
        response.setHeader("Content-Encoding", "gzip");
        GZIPOutputStream out = new GZIPOutputStream(response.getOutputStream());
        PrintWriter writer = new PrintWriter(out, true);

        MetricsReportor reportor = pullValueOf(format);     // 编码

        reportor.report(writer, name);

        out.finish();//结束压缩
        out.flush();//zip流刷入网络层
        writer.close();//结束输出
    }


    private static MetricsReportor defaultReportor = new PullProcessorMetricsReportor();

    private static Map<String, MetricsReportor> reportors = ImmutableMap.<String, MetricsReportor>builder()
            .put("cacti", new CactiProcessorMetricsReportor())
            .put("default", defaultReportor).build();

    public static MetricsReportor pullValueOf(String format) {
        MetricsReportor reportor = reportors.get(format);
        if (reportor == null) {
            return defaultReportor;
        }
        return reportor;
    }

    public static MetricsReportor pushValueOf(String format, String appCode, String host) {
        // 暂只支持的推数据格式
        return new PushProcessorMetricsReportor(appCode, host);
    }

}
