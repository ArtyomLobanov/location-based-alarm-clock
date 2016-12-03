package ru.spbau.mit.placenotifier;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import ru.spbau.mit.placenotifier.customizers.ActivityProducer;
import ru.spbau.mit.placenotifier.customizers.AddressPickerCustomizeEngine;
import ru.spbau.mit.placenotifier.customizers.AlternativeCustomizeEngine;
import ru.spbau.mit.placenotifier.customizers.ConstantCustomizeEngine;
import ru.spbau.mit.placenotifier.customizers.CustomizeEngine;
import ru.spbau.mit.placenotifier.customizers.NumericalValueCustomizeEngine;
import ru.spbau.mit.placenotifier.customizers.PlacePickerCustomizeEngine;
import ru.spbau.mit.placenotifier.customizers.PlacePredicateCustomizeEngine;
import ru.spbau.mit.placenotifier.customizers.StringCustomizeEngine;
import ru.spbau.mit.placenotifier.customizers.TimeIntervalCustomizeEngine;
import ru.spbau.mit.placenotifier.predicates.Beacon;
import ru.spbau.mit.placenotifier.predicates.BeaconPredicate;
import ru.spbau.mit.placenotifier.predicates.SerializablePredicate;

public class NotificationEditor extends AppCompatActivity implements ActivityProducer{

    private CustomizeEngine<String> name;
    private ArrayList<ResultListener> listeners;
    private CustomizeEngine<SerializablePredicate<Long>> timeCustomizer;
    private CustomizeEngine<SerializablePredicate<Location>> placeCustomizer;
    private CustomizeEngine<Object> otherCustomizer;
    private NumericalValueCustomizeEngine numCustomizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listeners = new ArrayList<>();

        setContentView(R.layout.activity_notification_editor);

        // just a rough draft
        timeCustomizer = new AlternativeCustomizeEngine<>("Time settings",
                new ConstantCustomizeEngine<>("no matter 1", null),
                new ConstantCustomizeEngine<>("no matter 2", null),
                new TimeIntervalCustomizeEngine(this, "Choose time interval"));
        timeCustomizer.observe(findViewById(R.id.time_settings_bar));;
        otherCustomizer = new AlternativeCustomizeEngine<>("Others settings",
                new ConstantCustomizeEngine<>("option 1", 1),
                new ConstantCustomizeEngine<>("option 2", 2),
                new ConstantCustomizeEngine<>("option 3", 3));
        otherCustomizer.observe(findViewById(R.id.other_settings_bar));
        numCustomizer = new NumericalValueCustomizeEngine("Choose number", "m",
                NumericalValueCustomizeEngine.EXPONENTIAL_TRANSFORMER, 10, 1000, 100000);
        numCustomizer.observe(findViewById(R.id.num_bar));
        numCustomizer.setValue(566.0);

        CustomizeEngine<Beacon> placeCustomizer2 = new AlternativeCustomizeEngine<>("Place settings",
                new PlacePickerCustomizeEngine("Choose point on map", this, 1),
                new PlacePickerCustomizeEngine("Choose point on map", this, 2),
                new AddressPickerCustomizeEngine(this, "Write down your address here"));
        placeCustomizer = new PlacePredicateCustomizeEngine(placeCustomizer2);
        placeCustomizer.observe(findViewById(R.id.place_bar));
        Button okButton = (Button) findViewById(R.id.editor_ok_button);
        okButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });
        name = new StringCustomizeEngine("Insert some String", 0);
        name.observe(findViewById(R.id.name_input));
        if (savedInstanceState == null) {
            loadNotification();
        }
    }

    // only for demo
    private void loadNotification() {
        Notification n = (Notification) getIntent().getSerializableExtra("notification");
        if (n == null) return;
        name.setValue(n.getName());
        BeaconPredicate pr = (BeaconPredicate) n.getPlacePredicate();
        placeCustomizer.setValue(pr);
        timeCustomizer.setValue(n.getTimePredicate());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            timeCustomizer.restoreState(savedInstanceState.getBundle("time_state"));
            placeCustomizer.restoreState(savedInstanceState.getBundle("place_state"));
            otherCustomizer.restoreState(savedInstanceState.getBundle("others_state"));
            numCustomizer.restoreState(savedInstanceState.getBundle("num_state"));
            name.restoreState(savedInstanceState.getBundle("n_state"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("time_state", timeCustomizer.saveState());
        outState.putBundle("place_state", placeCustomizer.saveState());
        outState.putBundle("others_state", otherCustomizer.saveState());
        outState.putBundle("num_state", numCustomizer.saveState());
        outState.putBundle("n_state", name.saveState());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void startActivity(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void addResultListener(ResultListener listener) {
        listeners.add(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (ResultListener listener : listeners) {
            if (listener.getID() == requestCode) {
                listener.onResult(resultCode, data);
            }
        }
    }
}


