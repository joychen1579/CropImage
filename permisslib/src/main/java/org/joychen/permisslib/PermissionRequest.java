package org.joychen.permisslib;


import org.joychen.permisslib.listeners.OnPermissionRequestorListener;

/**
 * PermissionRequest
 *
 * @author joychen
 * @version 1.0
 */
public class PermissionRequest {

    private int requestId;
    private String[] permissionsNeeded;
    private String message = "request permission ?";
    private OnPermissionRequestorListener permissionListener;

    public PermissionRequest(int requestId, String message, String[] permissionsNeeded, OnPermissionRequestorListener permissionListener) {
        this.requestId = requestId;
        this.message = message;
        this.permissionsNeeded = permissionsNeeded;
        this.permissionListener = permissionListener;
    }

    public String[] getPermissionsNeeded() {
        return permissionsNeeded;
    }

    public String getMessage() {
        return message;
    }

    public int getRequestId() {
        return requestId;
    }

    public OnPermissionRequestorListener getPermissionListener() {
        return permissionListener;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setPermissionsNeeded(String[] permissionsNeeded) {
        this.permissionsNeeded = permissionsNeeded;
    }

    public void setPermissionListener(OnPermissionRequestorListener permissionListener) {
        this.permissionListener = permissionListener;
    }
}
