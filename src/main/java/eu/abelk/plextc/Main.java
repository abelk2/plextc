package eu.abelk.plextc;

import eu.abelk.plextc.remover.TranscodedOriginalMovieRemover;
import eu.abelk.plextc.remover.TranscodedOriginalSeriesEpisodeRemover;

public class Main {

    public static void main(String[] args) {
        new Thread(new TranscodedOriginalMovieRemover()::start).start();
        new Thread(new TranscodedOriginalSeriesEpisodeRemover()::start).start();
    }

}
