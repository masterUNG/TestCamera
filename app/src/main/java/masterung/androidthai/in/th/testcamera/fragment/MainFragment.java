package masterung.androidthai.in.th.testcamera.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import masterung.androidthai.in.th.testcamera.R;

public class MainFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        takePhoto();

    }   // Main Method

    public class MyFTPDataTransferListener implements FTPDataTransferListener{
        @Override
        public void started() {
            Toast.makeText(getActivity(), "Upload Start", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void transferred(int i) {
            Toast.makeText(getActivity(), "Continue...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void completed() {
            Toast.makeText(getActivity(), "Upload Complete", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void aborted() {

        }

        @Override
        public void failed() {

        }
    }


    private void takePhoto() {
        Button button = getView().findViewById(R.id.btnCamera);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            ImageView imageView = getView().findViewById(R.id.imvAvata);
            Uri uri = data.getData();

            FTPClient ftpClient = new FTPClient();
            try {

                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));

                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 800, 600, true);

                imageView.setImageBitmap(bitmap1);

                String pathImageString = null;
                String[] strings = new String[]{MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(uri, strings, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int i = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    pathImageString = cursor.getString(i);
                } else {
                    pathImageString = uri.getPath();
                }
                Log.d("14AugV1", "Path ==> " + pathImageString);

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy
                        .Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                File file = new File(pathImageString);


                ftpClient.connect("ftp.androidthai.in.th", 21);
                ftpClient.login("sun@androidthai.in.th", "Abc12345");
                ftpClient.setType(FTPClient.TYPE_BINARY);
                ftpClient.changeDirectory("TestCamera");
                ftpClient.upload(file, new MyFTPDataTransferListener());


            } catch (Exception e) {
                Log.d("14AugV1", "Error e==> " + e.toString());
                try {
                    ftpClient.disconnect(true);
                } catch (Exception e1) {
                    Log.d("14AugV1", "Error e1==> " + e1.toString());
                }
            }

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }
}
