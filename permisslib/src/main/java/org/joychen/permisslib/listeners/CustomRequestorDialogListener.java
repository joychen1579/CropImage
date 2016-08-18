package org.joychen.permisslib.listeners;

import android.app.Activity;

/**
 * CustomRequestorDialogListener
 *
 * @author joychen
 * @version 1.0
 */
public interface CustomRequestorDialogListener {
    void showRequestDialog(final Activity activity, String[] needPermissions, int requestId, String title, String message, String sureName, String cancelName);
}
