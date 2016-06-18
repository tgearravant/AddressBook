package net.tullco.addressbook.utils;

public class Path {
    public static class Web {
        public static final String INDEX = "/";
        public static final String LOGIN = "/login/";
        public static final String LOGOUT = "/logout/";
        public static final String ONE_CONTACT = "/contacts/:contact_id/";
        public final static String SEARCH_POST = "/contacts/search/:search";
        public final static String SEARCH_RESULTS = "/contacts/search/:search";
    }

    public static class Template {
        public final static String INDEX = "/templates/contact/list.vm";
        public final static String LOGIN = "/velocity/login/login.vm";
        public static final String ONE_CONTACT = "/templates/contact/details.vm";
        public static final String LIST_CONTACTS = "/templates/contact/list.vm";
        public static final String NOT_FOUND = "/templates/notFound.vm";
    }
}
