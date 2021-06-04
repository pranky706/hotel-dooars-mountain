package com.dooars.mountain.constants;

/**
 * @author Prantik Guha on 23-05-2021
 **/
public class AllEndPoints {

    public static final String AUTHENTICATION = "/authentication";
    public static final String ADD_CUSTOMER = "/customer-service/addCustomer";
    public static final String LOGIN_URL = "/api/admin/login-service/login";
    public static final String ITEM_SERVICE = "/api/admin/item-service/";
    public static final String ADD_ITEM = ITEM_SERVICE + "addItem";
    public static final String CHANGE_AVAILABILITY = ITEM_SERVICE + "changeItemAvailability";
    public static final String ADD_ITEMS = ITEM_SERVICE + "addItems";
    public static final String UPDATE_OFFER_ITEM = ITEM_SERVICE + "updateOffer";
    public static final String UPDATE_ITEM = ITEM_SERVICE + "updateItem";
    public static final String DELETE_ITEM = ITEM_SERVICE + "deleteItem";
    public static final String MENU_GROUP_SERVICE = "/api/admin/menu-group-service/";
    public static final String ADD_MENU_GROUP = MENU_GROUP_SERVICE + "addMenuGroup";
    public static final String DELETE_MENU_GROUP = MENU_GROUP_SERVICE + "deleteMenuGroup";
    public static final String UPDATE_MENU_GROUP = MENU_GROUP_SERVICE + "updateMenuGroup";
    public static final String OFFER_SERVICE = "/api/admin/offer-service/";
    public static final String ADD_OFFER = OFFER_SERVICE + "addOffer";
    public static final String UPDATE_OFFER = OFFER_SERVICE + "updateOffer";
    public static final String DELETE_OFFER = OFFER_SERVICE + "deleteOffer";
    public static final String GET_OFFER = OFFER_SERVICE + "getOffer";
    public static final String FILE_SERVICE = "/api/admin/file-service/";
    public static final String UPLOAD_FILE = FILE_SERVICE + "upload";

}
