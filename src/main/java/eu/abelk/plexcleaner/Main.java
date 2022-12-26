package eu.abelk.plexcleaner;

import eu.abelk.plexcleaner.remover.TranscodedOriginalMovieRemover;

public class Main {

    public static void main(String[] args) {
        new TranscodedOriginalMovieRemover().start();
    }

}
