package net.tullco.addressbook.utils;


public class Path {
    public static class Web {
        public static final String INDEX = "/";
        public static final String LOGIN = "/login/";
        public static final String LOGOUT = "/logout/";
        public static final String ONE_CONTACT_NO_ID = "/contacts/";
        public static final String ONE_CONTACT = "/contacts/:contact_id/";
        public final static String SEARCH_POST = "/contacts/search/";
        public final static String SEARCH_RESULTS = "/contacts/search/:search/";
        public final static String STYLESHEET = "/css/style.css";
        public final static String IMAGE_DIRECTORY = "/img/";
        public final static String EDIT_ADDRESS = "/addresses/:address_id/edit/";
        public final static String ADD_ADDRESS = "/addresses/add/:contact_id";
        
        public static String getONE_CONTACT(){
        	return ONE_CONTACT_NO_ID;
        }
        public static String getIMAGE_DIRECTORY(){
        	return IMAGE_DIRECTORY;
        }
        public static String getSTYLESHEET(){
        	return STYLESHEET;
        }
    }

    public static class Template {
        public final static String INDEX = "/templates/contact/list.vm";
        public final static String LOGIN = "/velocity/login/login.vm";
        public static final String ONE_CONTACT = "/templates/contact/details.vm";
        public static final String LIST_CONTACTS = "/templates/contact/list.vm";
        public static final String NOT_FOUND = "/templates/notFound.vm";
        public static final String EDIT_ADDRESS = "/templates/address/edit.vm";
    }
}
