package com.comparateur.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int PICK_PDF = 100;
    private int selected = 0;
    private TextView status;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 55, 35, 35);

        TextView title = new TextView(this);
        title.setText("Comparateur PDF");
        title.setTextSize(26);
        layout.addView(title);

        status = new TextView(this);
        status.setText("Aucun PDF sélectionné");
        status.setTextSize(17);
        layout.addView(status);

        Button first = new Button(this);
        first.setText("Choisir PDF 1");
        layout.addView(first);

        Button second = new Button(this);
        second.setText("Choisir PDF 2");
        layout.addView(second);

        first.setOnClickListener(v -> choosePdf(1));
        second.setOnClickListener(v -> choosePdf(2));

        setContentView(layout);
    }

    private void choosePdf(int number) {
        selected = number;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_PDF);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);

        if (request == PICK_PDF && result == RESULT_OK
                && data != null) {
            status.setText("PDF " + selected + " sélectionné : "
                    + data.getData().getLastPathSegment());
        }
    }
}
