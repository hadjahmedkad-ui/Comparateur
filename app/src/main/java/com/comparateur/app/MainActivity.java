package com.comparateur.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.content.Intent;
import android.net.Uri;
import android.print.PrintManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.ValueCallback;

public class MainActivity extends Activity {
    private WebView web;
    private ValueCallback<Uri[]> pendingFiles;
    private static final int FILE_REQUEST = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        setContentView(web);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);

        web.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void printPage() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    PrintManager manager =
                        (PrintManager) getSystemService(Context.PRINT_SERVICE);

                    manager.print(
                        "Comparateur",
                        web.createPrintDocumentAdapter("Comparateur"),
                        null
                    );
                });
            }
        }, "AndroidPrint");

        web.setWebViewClient(new WebViewClient());

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                WebView view,
                ValueCallback<Uri[]> callback,
                FileChooserParams params
            ) {
                if (pendingFiles != null) {
                    pendingFiles.onReceiveValue(null);
                }

                pendingFiles = callback;

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                startActivityForResult(intent, FILE_REQUEST);
                return true;
            }
        });

        web.loadUrl("file:///android_asset/comparateur.html");
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != FILE_REQUEST || pendingFiles == null) return;

        Uri[] selected = null;

        if (resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                selected = new Uri[count];

                for (int i = 0; i < count; i++) {
                    selected[i] = data.getClipData()
                        .getItemAt(i).getUri();
                }
            } else if (data.getData() != null) {
                selected = new Uri[]{data.getData()};
            }
        }

        pendingFiles.onReceiveValue(selected);
        pendingFiles = null;
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
