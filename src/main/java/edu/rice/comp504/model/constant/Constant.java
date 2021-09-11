package edu.rice.comp504.model.constant;

import com.google.gson.Gson;

/**
 * This class keeps some important constant values.
 */
public class Constant {

    public static final Gson gson = new Gson();
    public static final String ALL = "all";
    public static final String REGISTER = "register";

    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    public static final String CONFLICT = "conflict";
    public static final String SYSTEM = "system";
    public static final String EMPTY = "";
    public static final String HATE = "hate";

    public static final String CREATE_GROUP_NOTIFICATION = "%s creates chat room %s";
    public static final String JOIN_GROUP_NOTIFICATION = "%s joins chat room %s";
    public static final String LEAVE_GROUP_NOTIFICATION = "%s leaves chat room %s because of %s";
    public static final String LEAVE_REASON_GROUP_DISSOLVED = "group %s is dissolved.";

    public static final String LEAVE_REASON_FORCE_TO_LEAVE = "forcible leave";
    public static final String LEAVE_REASON_CLOSE_CONNECTION = "connection closed";
    public static final String LEAVE_REASON_REGULAR_LEAVE = "normal leave";

}
