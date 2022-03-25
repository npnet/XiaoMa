//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.car.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ICarVolumeCallback extends IInterface {
    void onGroupVolumeChanged(int var1, int var2) throws RemoteException;

    void onMasterMuteChanged(int var1) throws RemoteException;

    void onCarVolumeChanged(int var1, int var2, int var3) throws RemoteException;

    public abstract static class Stub extends Binder implements ICarVolumeCallback {
        private static final String DESCRIPTOR = "android.car.media.ICarVolumeCallback";
        static final int TRANSACTION_onGroupVolumeChanged = 1;
        static final int TRANSACTION_onMasterMuteChanged = 2;
        static final int TRANSACTION_onCarVolumeChanged = 3;

        public Stub() {
            this.attachInterface(this, "android.car.media.ICarVolumeCallback");
        }

        public static ICarVolumeCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface("android.car.media.ICarVolumeCallback");
                return (ICarVolumeCallback) (iin != null && iin instanceof ICarVolumeCallback ? (ICarVolumeCallback) iin : new ICarVolumeCallback.Stub.Proxy(obj));
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = "android.car.media.ICarVolumeCallback";
            int _arg0;
            int _arg1;
            switch (code) {
                case 1:
                    data.enforceInterface(descriptor);
                    _arg0 = data.readInt();
                    _arg1 = data.readInt();
                    this.onGroupVolumeChanged(_arg0, _arg1);
                    return true;
                case 2:
                    data.enforceInterface(descriptor);
                    _arg0 = data.readInt();
                    this.onMasterMuteChanged(_arg0);
                    return true;
                case 3:
                    data.enforceInterface(descriptor);
                    _arg0 = data.readInt();
                    _arg1 = data.readInt();
                    int _arg2 = data.readInt();
                    this.onCarVolumeChanged(_arg0, _arg1, _arg2);
                    return true;
                case 1598968902:
                    reply.writeString(descriptor);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements ICarVolumeCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "android.car.media.ICarVolumeCallback";
            }

            public void onGroupVolumeChanged(int groupId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("android.car.media.ICarVolumeCallback");
                    _data.writeInt(groupId);
                    _data.writeInt(flags);
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void onMasterMuteChanged(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("android.car.media.ICarVolumeCallback");
                    _data.writeInt(flags);
                    this.mRemote.transact(2, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void onCarVolumeChanged(int groupId, int volume, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("android.car.media.ICarVolumeCallback");
                    _data.writeInt(groupId);
                    _data.writeInt(volume);
                    _data.writeInt(flags);
                    this.mRemote.transact(3, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }

            }
        }
    }
}
