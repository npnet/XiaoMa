package com.xiaoma.systemui.topbar.controller;

import android.content.ComponentName;
import android.graphics.Rect;
import android.hardware.biometrics.IBiometricPromptReceiver;
import android.os.Bundle;
import android.os.IBinder;

import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.common.util.LogUtil;

/**
 * Created by LKF on 2018/11/1 0001.
 */
public class SimpleStatusBar extends IStatusBar.Stub {
    protected void logI(String format, Object... args) {
        LogUtil.logI(getClass().getSimpleName(), format, args);
    }

    @Override
    public void setIcon(String slot, StatusBarIcon icon) {
        logI("setIcon -> [ slot: %s, icon: %s ]", slot, icon);
    }

    @Override
    public void removeIcon(String slot) {
        logI("removeIcon -> [ slot: %s ]", slot);
    }

    @Override
    public void disable(int state1, int state2) {
        //logI("disable -> [ state1: %s, state2: %s ]", state1, state2);
    }

    /**
     * 动态展开通知面板
     */
    @Override
    public void animateExpandNotificationsPanel() {
//        logI("animateExpandNotificationsPanel ->");
    }

    /**
     * 动态展开设置面板
     */
    @Override
    public void animateExpandSettingsPanel(String subPanel) {
//        logI("animateExpandSettingsPanel -> [ subPanel: %s ]", subPanel);
    }

    /**
     * 动态折叠面板
     */
    @Override
    public void animateCollapsePanels() {
//        logI("animateCollapsePanels ->");
    }

    @Override
    public void togglePanel() {
//        logI("togglePanel ->");
    }

    @Override
    public void showWirelessChargingAnimation(int i) {

    }

    @Override
    public void setSystemUiVisibility(int vis, int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenBounds, Rect dockedBounds) {
        logI("setSystemUiVisibility -> [ vis: %s, fullscreenStackVis: %s, dockedStackVis: %s, mask: %s, fullscreenBounds: %s, dockedBounds: %s ]",
                vis, fullscreenStackVis, dockedStackVis, mask, fullscreenBounds, dockedBounds);
    }

    @Override
    public void topAppWindowChanged(boolean menuVisible) {
        //logI("topAppWindowChanged -> [ menuVisible: %s ]", menuVisible);
    }

    @Override
    public void setImeWindowStatus(IBinder token, int vis, int backDisposition, boolean showImeSwitcher) {
        //logI("setImeWindowStatus -> [ token: %s, vis: %s, backDisposition: %s, showImeSwitcher: %s ]", token, vis, backDisposition, showImeSwitcher);
    }

    @Override
    public void setWindowState(int window, int state) {
        //logI("setWindowState -> [ window: %s, state: %s ]", window, state);
    }

    @Override
    public void showRecentApps(boolean b) {

    }

    @Override
    public void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) {
        //logI("hideRecentApps -> [ triggeredFromAltTab: %s, triggeredFromHomeKey: %s ]", triggeredFromAltTab, triggeredFromHomeKey);
    }

    @Override
    public void toggleRecentApps() {
//        logI("toggleRecentApps ->");
    }

    @Override
    public void toggleSplitScreen() {
//        logI("toggleSplitScreen ->");
    }

    @Override
    public void preloadRecentApps() {
//        logI("preloadRecentApps ->");
    }

    @Override
    public void cancelPreloadRecentApps() {
//        logI("cancelPreloadRecentApps ->");
    }

    @Override
    public void showScreenPinningRequest(int taskId) {
//        logI("showScreenPinningRequest ->[ taskId: %s ]", taskId);
    }

    @Override
    public void dismissKeyboardShortcutsMenu() {
//        logI("dismissKeyboardShortcutsMenu ->");
    }

    @Override
    public void toggleKeyboardShortcutsMenu(int deviceId) {
//        logI("toggleKeyboardShortcutsMenu -> [ deviceId: %s ]", deviceId);
    }

    @Override
    public void appTransitionPending() {
//        logI("appTransitionPending ->");
    }

    @Override
    public void appTransitionCancelled() {
//        logI("appTransitionCancelled ->");
    }

    @Override
    public void appTransitionStarting(long statusBarAnimationsStartTime, long statusBarAnimationsDuration) {
//        logI("appTransitionStarting -> [ statusBarAnimationsStartTime: %s, statusBarAnimationsDuration: %s ]", statusBarAnimationsStartTime, statusBarAnimationsDuration);
    }

    @Override
    public void appTransitionFinished() {
//        logI("appTransitionFinished ->");
    }

    @Override
    public void showAssistDisclosure() {
//        logI("showAssistDisclosure ->");
    }

    @Override
    public void startAssist(Bundle bundle) {
//        logI("startAssist -> [ bundle: %s ]", bundle);
    }

    @Override
    public void onCameraLaunchGestureDetected(int source) {
//        logI("onCameraLaunchGestureDetected -> [ source: %s ]", source);
    }

    @Override
    public void showPictureInPictureMenu() {
//        logI("showPictureInPictureMenu ->");
    }

    @Override
    public void showGlobalActionsMenu() {
//        logI("showGlobalActionsMenu ->");
    }

    @Override
    public void onProposedRotationChanged(int i, boolean b) {

    }

    @Override
    public void setTopAppHidesStatusBar(boolean hidesStatusBar) {
//        logI("setTopAppHidesStatusBar -> [ hidesStatusBar: %s ]", hidesStatusBar);
    }

    @Override
    public void addQsTile(ComponentName tile) {
//        logI("addQsTile -> [ tile: %s ]", tile);
    }

    @Override
    public void remQsTile(ComponentName tile) {
//        logI("remQsTile -> [ tile: %s ]", tile);
    }

    @Override
    public void clickQsTile(ComponentName tile) {
//        logI("clickQsTile -> [ tile: %s ]", tile);
    }

    @Override
    public void handleSystemKey(int key) {
//        logI("handleSystemKey -> [ key: %s ]", key);
    }

    @Override
    public void showPinningEnterExitToast(boolean b)  {

    }

    @Override
    public void showPinningEscapeToast() {

    }

    @Override
    public void showShutdownUi(boolean isReboot, String reason) {
//        logI("showShutdownUi -> [ isReboot: %s, reason: %s ]", isReboot, reason);
    }

    @Override
    public void showFingerprintDialog(Bundle bundle, IBiometricPromptReceiver iBiometricPromptReceiver)  {

    }

    @Override
    public void onFingerprintAuthenticated()  {

    }

    @Override
    public void onFingerprintHelp(String s)  {

    }

    @Override
    public void onFingerprintError(String s)  {

    }

    @Override
    public void hideFingerprintDialog()  {

    }
}