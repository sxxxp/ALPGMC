package com.sxxxp.ALPGMC.client;

import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;

public class ClientClassData {
    private static boolean synced = false; // 서버에서 직업 정보를 받았는지 여부
    private static PlayerClass currentClass = null;

    public static void set(String classId) {
        currentClass = PlayerClass.fromId(classId);
        synced = true;
    }

    // 다른 월드에 접속할 때 이전 직업이 남지 않도록 연결이 끊기면 초기화합니다.
    public static void reset() {
        currentClass = null;
        synced = false;
    }

    public static boolean isSynced() {
        return synced;
    }

    public static PlayerClass get() {
        return currentClass;
    }
}
