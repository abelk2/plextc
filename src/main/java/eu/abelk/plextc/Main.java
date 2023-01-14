package eu.abelk.plextc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import eu.abelk.plextc.replacer.TranscodedOriginalMovieReplacer;
import eu.abelk.plextc.replacer.TranscodedOriginalSeriesEpisodeReplacer;

public class Main {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) {
        Main main = new Main();
        Runtime.getRuntime()
            .addShutdownHook(new Thread(main::shutdown));
        main.start();
    }

    public void start() {
        new Thread(new TranscodedOriginalMovieReplacer(executorService)::start).start();
        new Thread(new TranscodedOriginalSeriesEpisodeReplacer(executorService)::start).start();
    }

    public void shutdown() {
        executorService.shutdownNow();
    }

}
