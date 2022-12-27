package eu.abelk.plextc;

import eu.abelk.plextc.replacer.TranscodedOriginalMovieReplacer;
import eu.abelk.plextc.replacer.TranscodedOriginalSeriesEpisodeReplacer;

public class Main {

    public static void main(String[] args) {
        new Thread(new TranscodedOriginalMovieReplacer()::start).start();
        new Thread(new TranscodedOriginalSeriesEpisodeReplacer()::start).start();
    }

}
