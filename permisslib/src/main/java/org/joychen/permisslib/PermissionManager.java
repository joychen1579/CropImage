package org.joychen.permisslib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;


import org.joychen.permisslib.listeners.CustomRequestorDialogListener;
import org.joychen.permisslib.listeners.OnPermissionRequestorListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PermissionManager
 *
 * @author joychen
 * @version 1.0
 */
public class PermissionManager {

    //request map
    private static Map<Integer, PermissionRequest> integerRequestMap = new HashMap<Integer, PermissionRequest>();

    /**
     * requestPermission
     *
     * @param activity           activity
     * @param requestId          requestId
     * @param message            message
     * @param permissionsNeeded  permissionsNeeded
     * @param permissionListener permissionListener
     */
    public static void requestPermission(final Activity activity, int requestId, String message, String[] permissionsNeeded, OnPermissionRequestorListener permissionListener) {
        requestPermission(activity, new PermissionRequest(requestId, message, permissionsNeeded, permissionListener));
    }

    /**
     * requestPermission
     *
     * @param activity activity
     */
    public static void requestPermission(final Activity activity, final PermissionRequest request) {
        if (request == null) {
            return;
        }
        if (request.getPermissionListener() == null) {
            return;
        }
        if (request.getPermissionsNeeded() == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            request.getPermissionListener().gotPermissions();
            return;
        }
        boolean notHasPermissions = notHasPermissions(activity, request);
        if (notHasPermissions) {
            boolean shouldShowRequest = false;
            List<String> rejectPermissions = new ArrayList<String>();
            for (int i = 0; i < request.getPermissionsNeeded().length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean should = !activity.shouldShowRequestPermissionRationale(request.getPermissionsNeeded()[i]);
                    shouldShowRequest = should || shouldShowRequest;
                    if (!should) {
                        if ((activity.checkSelfPermission(request.getPermissionsNeeded()[i]) != PackageManager.PERMISSION_GRANTED)) {
                            rejectPermissions.add(request.getPermissionsNeeded()[i]);
                        }
                    }
                }
            }
            if (shouldShowRequest) {
                integerRequestMap.put(request.getRequestId(), request);
                if (requestorrDialogListener != null) {
                    requestorrDialogListener.showRequestDialog(activity, request.getPermissionsNeeded(), request.getRequestId(), title, request.getMessage(), sureName, cancelName);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                if (!"".equals(title) && !"null".equals(title)) {
                    builder.setTitle(title);
                }
                builder.setMessage(request.getMessage());
                builder.setPositiveButton(sureName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            activity.requestPermissions(request.getPermissionsNeeded(),
                                    request.getRequestId());
                        }
                    }
                });
                builder.setNegativeButton(cancelName, null);
                builder.create().show();
            } else {
                request.getPermissionListener().rejectPermissions(rejectPermissions);
            }
        } else {
            request.getPermissionListener().gotPermissions();
        }

    }


    /**
     * onRequestPermissionsResult
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (integerRequestMap.containsKey(requestCode)) {
            PermissionRequest request = integerRequestMap.get(requestCode);
            if (request.getPermissionListener() != null) {
                boolean gotPermissions = true;
                List<String> rejectPermissions = new ArrayList<String>();
                for (int i = 0; i < permissions.length; i++) {
                    gotPermissions = (grantResults[i] == PackageManager.PERMISSION_GRANTED) && gotPermissions;
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        rejectPermissions.add(permissions[i]);
                    }
                }
                if (gotPermissions) {
                    request.getPermissionListener().gotPermissions();
                } else {
                    request.getPermissionListener().rejectPermissions(rejectPermissions);
                }
            }
            integerRequestMap.remove(request);
        }
    }


    /**
     * notHasPermissions
     *
     * @param activity activity
     * @return true or false
     */
    private static boolean notHasPermissions(Activity activity, PermissionRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = false;
            for (int i = 0; i < request.getPermissionsNeeded().length; i++) {
                hasPermission = (activity.checkSelfPermission(request.getPermissionsNeeded()[i]) != PackageManager.PERMISSION_GRANTED) || hasPermission;
            }
            return hasPermission;
        }
        return false;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////// config /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static CustomRequestorDialogListener requestorrDialogListener;
    private static String title = "";
    private static String sureName = "get", cancelName = "cancel";

    /**
     * config
     *
     * @param title                    title
     * @param sureName                 sureName
     * @param cancelName               cancelName
     * @param requestorrDialogListener requestorrDialogListener
     */
    public static void config(String title, String sureName, String cancelName, CustomRequestorDialogListener requestorrDialogListener) {
        PermissionManager.title = title;
        PermissionManager.sureName = sureName;
        PermissionManager.cancelName = cancelName;
        PermissionManager.requestorrDialogListener = requestorrDialogListener;
    }
}
