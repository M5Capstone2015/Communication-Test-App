package com.apps.hgb.capstonecommunicationtesting;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;


public class Main extends Activity {

    TextView textView = null;
    TextView dataTextView = null;
    private static int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};
    AudioRecord _aru = null;
    short[] buffer = null;


    public void readClick(View v) {
        // Run AudioRecord and Save to file. Print average to and num read.
        if (_aru != null) {
            _aru.startRecording();
            int shortsRead = _aru.read(buffer, 0, buffer.length);
            double average = Average(buffer);
            textView.setText(String.valueOf(shortsRead) + "\nAverage: " + String.valueOf(average));
        }
        else {
            textView.setText("AudioRecord is null");
        }

        String text = "";
        if (buffer.length > 10)
            for (int i = 0; i < 300; i++) {
                String num = "" + i;
                String val = Short.toString(buffer[i]);
                text += "\n" + num + ", " + val;
            }
        //processInputBuffer(shortsRead2);
        dataTextView.setText(text);
        WriteToFile(text);


    }

    public void clearClick(View v) {
        textView.setText("");
    }

    private void initialize()
    {
        int recBufferSize =
                AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        buffer = new short[recBufferSize * 10];
        _aru = findAudioRecord();
        textView = (TextView) findViewById(R.id.textView);
        dataTextView = (TextView) findViewById(R.id.dataText);
    }

    private double Average(short[] bytes)
    {
        double sum = 0;
        for (int i = 0; i < bytes.length; i++) {
            Short s = bytes[i];
            sum = sum + s.doubleValue();
        }
        return sum / bytes.length;
    }

    public void WriteToFile(String content)
    {
        OutputStream fos;
        try {
            fos = openFileOutput("dump.txt", Context.MODE_WORLD_READABLE);
            fos.write(content.getBytes());
            fos.close();
        }
        catch (FileNotFoundException e)
        {
        }
        catch (IOException e)
        {
        }
    }

    private AudioRecord findAudioRecord()
    {
        for (int rate : mSampleRates)
        {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT })
            {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO })
                {
                    try {
                        //Log.d(C.TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                        //+ channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        //Log.e(C.TAG, rate + "Exception, keep trying.",e);
                    }
                }
            }
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
