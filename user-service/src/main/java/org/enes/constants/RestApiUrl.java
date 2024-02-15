package org.enes.constants;

public class RestApiUrl {
    private static final String VERSION = "/v1";
    private static final String API = "/api";
    private static final String TEST = "/test";
    private static final String DEV = "/dev";
    private static final String PROD = "/prod";
    private static final String ROOT = DEV+VERSION;

    public static final String AUTH = ROOT + "/auth";
    public static final String USER = ROOT + "/user";

    public static final String CREATE = "/create";
    public static final String UPDATE = "/update";
    public static final String FIND_ALL = "/find-all";
    public static final String FIND_BY_ID = "/find-by-id";
    public static final String DELETE_BY_ID = "/delete-by-id";
    public static final String ACTIVATION = "/activation";

}
