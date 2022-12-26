package eu.abelk.plextc.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;

public class LoggingConfigurer {

    private LoggingConfigurer() {
    }

    @SneakyThrows
    public static void configureLogging(Config config) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(context);
        context.reset();
        context.putProperty("plextc.loggingPath", config.loggingPath());
        context.putProperty("plextc.loggingLevel", config.loggingLevel());
        joranConfigurator.doConfigure(LoggingConfigurer.class.getClassLoader().getResource("plextc_logback.xml"));
    }

}
