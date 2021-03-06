package yanagishima.servlet;

import static java.lang.String.format;
import static yanagishima.util.JsonUtil.writeJSON;
import static yanagishima.util.PrestoQueryProvider.listToValues;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

import yanagishima.config.YanagishimaConfig;

@Singleton
public class ToValuesQueryServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToValuesQueryServlet.class);
    private static final long serialVersionUID = 1L;
    private static final Splitter SPLITTER = Splitter.on('\n');

    private final YanagishimaConfig config;

    @Inject
    public ToValuesQueryServlet(YanagishimaConfig config) {
        this.config = config;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Optional.ofNullable(request.getParameter("csv")).ifPresent(csv -> {
                List<String> lines = SPLITTER.splitToList(csv);
                String valuesQuery = listToValues(lines, config.getToValuesQueryLimit());
                LOGGER.info(format("query=%s", valuesQuery));
                writeJSON(response, Map.of("query", valuesQuery));
            });
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            writeJSON(response, Map.of("error", e.getMessage()));
        }
    }
}
