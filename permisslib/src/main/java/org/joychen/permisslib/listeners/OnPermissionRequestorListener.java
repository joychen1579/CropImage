package org.joychen.permisslib.listeners;

import java.util.List;

/**
 * OnPermissionRequestorListener
 *
 * @author joychen
 * @version 1.0
 */
public interface OnPermissionRequestorListener {

    void gotPermissions();

    void rejectPermissions(List<String> rejects);
}
