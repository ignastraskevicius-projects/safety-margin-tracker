package org.ignast.stockinvesting.api.acceptance;

public final class Uris {
    private Uris() {

    }

    public static String rootResourceOn(final int port) {
        return "http://localhost:" + port + "/";
    }
}
