package org.ignast.stockinvesting.quotes.acceptance;

public final class Uris {
    private Uris() {

    }

    public static String rootResourceOn(final int port) {
        return "http://localhost:" + port + "/";
    }
}
