/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaoma.launcher.square.control;

import android.app.Service;
//import android.car.input.CarInputHandlingService;
//import android.car.input.HardKeyEvent;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

///*
// * Copyright (C) 2016 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.xiaoma.launcher.square.control;
//
//import android.car.input.CarInputHandlingService;
//import android.car.input.HardKeyEvent;
//import android.util.Log;
//import android.view.KeyEvent;
//
///**
// * Default implementation of {@link CarInputHandlingService}.
// * <p>
// * This implementation does nothing, just serves as showcase how input service should be
// * implemented.
// */
//public class SquareControlService extends CarInputHandlingService {
//    private static final String TAG = SquareControlService.class.getSimpleName();
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.d(TAG, "onCreate: ");
//    }
//
//    public SquareControlService() {
//        super(new InputFilter[0]);
//    }
//
//    @Override
//    protected void onKeyEvent(KeyEvent event, int targetDisplay) {
//        // Implement your handling here
//        Log.d(TAG, "onKeyEvent(" + event + ", " + targetDisplay + ")");
//    }
//
//    @Override
//    protected void onHardKeyEvent(HardKeyEvent keyEvent) {
//        Log.d(TAG, "onHardKeyEvent(" + keyEvent.toString() + ")");
//    }
//}
public class SquareControlService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
