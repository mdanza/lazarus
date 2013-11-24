package com.lazarus.busclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String USER = "bus";
	private static final String PWD = "superBusesSiQueSi";
	private String token = "";

	private Button actionBtn;
	private EditText variantCodeField;
	private EditText subLineCodeField;
	private boolean active;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		actionBtn = (Button) findViewById(R.id.pushToStartStopBtn);
		variantCodeField = (EditText) findViewById(R.id.variantCodeField);
		subLineCodeField = (EditText) findViewById(R.id.subLineCodeField);
		active = false;
		actionBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (active) {
					variantCodeField.setEnabled(true);
					subLineCodeField.setEnabled(true);
					actionBtn.setText("enviar datos de ubicación");
				} else {
					String inputVariantCode = variantCodeField.getText()
							.toString();
					String inputSubLineCode = subLineCodeField.getText()
							.toString();
					if (inputVariantCode.equals("")
							|| inputSubLineCode.equals(""))
						Toast.makeText(getApplicationContext(),
								"Códigos vacíos", Toast.LENGTH_SHORT).show();
					else {
						variantCodeField.setEnabled(false);
						subLineCodeField.setEnabled(false);
						actionBtn.setText("parar envío de datos");
						sendData(Integer.parseInt(inputVariantCode),
								Integer.parseInt(inputSubLineCode));
					}
				}
				active = !active;
			}
		});
	}

	private class HttpHelper implements Runnable {

		@Override
		public void run() {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(
					"http://10.0.2.2:8080/services-1.0-SNAPSHOT/api/users/login");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", USER));
				nameValuePairs.add(new BasicNameValuePair("password", PWD));
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = client.execute(request);
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				token = rd.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void sendData(int variantCode, int subLineCode) {
		new Thread(new HttpHelper()).start();
	}
}
