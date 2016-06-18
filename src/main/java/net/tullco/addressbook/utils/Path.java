package net.tullco.addressbook.utils;

public class Path {
    public static class Web {
        public static final String INDEX = "/";
        public static final String LOGIN = "/login/";
        public static final String LOGOUT = "/logout/";
        public static final String ONE_CONTACT = "/contacts/:contact_id/";
    }

    public static class Template {
        public final static String INDEX = "/templates/contact/many.vm";
        public final static String LOGIN = "/velocity/login/login.vm";
        public final static String BOOKS_ALL = "/velocity/book/all.vm";
        public static final String ONE_CONTACT = "/velocity/book/one.vm";
        public static final String NOT_FOUND = "/velocity/notFound.vm";
    }
}
