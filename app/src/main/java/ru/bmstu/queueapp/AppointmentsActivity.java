package ru.bmstu.queueapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.HashMap;

import ru.bmstu.queueapp.adapters.AppointmentItemAdapter;
import ru.bmstu.queueapp.http.ApiHttpClient;
import ru.bmstu.queueapp.http.ResponseHandler;
import ru.bmstu.queueapp.http.data.Appointment;
import ru.bmstu.queueapp.http.data.AppointmentInterval;
import ru.bmstu.queueapp.http.data.Schedule;
import ru.bmstu.queueapp.http.data.UserData;
import ru.bmstu.queueapp.http.request.CreateAppointmentRequest;

public class AppointmentsActivity extends AppCompatActivity {

    public static final String DATE_EXTRA = "DATE";
    public static final String HOST_EXTRA = "HOST";
    public static final String SCHEDULE_EXTRA = "SCHEDULE";

    private ListView appointmentsListView;

    private LocalDate date;
    private UserData host;
    private Schedule schedule;
    private HashMap<LocalTime, Appointment> appointmentMap;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        date = (LocalDate) getIntent().getSerializableExtra(DATE_EXTRA);
        host = (UserData) getIntent().getSerializableExtra(HOST_EXTRA);
        schedule = (Schedule) getIntent().getSerializableExtra(SCHEDULE_EXTRA);
        appointmentMap = generateAppointments(schedule);

        ((TextView) findViewById(R.id.appointmentDateText)).setText(date.toString());
        ((TextView) findViewById(R.id.appointmentHostText)).setText(host.fullName());

        appointmentsListView = findViewById(R.id.appointmentsView);
        appointmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment appointment = (Appointment) parent.getItemAtPosition(position);
                //todo check if not taken
        //            createAppointment(appointment);
                appointmentsListView.setEnabled(false);

                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.appointment_item_popup,null);

                popupWindow = new PopupWindow(popupView, 600, LayoutParams.WRAP_CONTENT);
                popupView.setElevation(5.0f);

                ((TextView) popupView.findViewById(R.id.appointmentPopupCaption)).setText(appointment.timeInterval());
                Button appointmentButton = popupView.findViewById(R.id.appointmentPopupButton);

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        appointmentsListView.setEnabled(true);
                        popupWindow = null;
                    }
                });

                appointmentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });

                popupWindow.showAtLocation(appointmentsListView, Gravity.CENTER,0,0);
            }
        });

        requestAppointments();
        Log.d("MY_CUSTOM_LOG", schedule.toString());
    }

    private HashMap<LocalTime, Appointment> generateAppointments(Schedule schedule) {
        HashMap<LocalTime, Appointment> appointments = new HashMap<>();
        Period duration = schedule.appointmentDuration;
        for (AppointmentInterval i: schedule.appointmentIntervals) {
            for (LocalTime curr = i.start; curr.isBefore(i.end); curr = curr.plus(duration)) {
                appointments.put(curr, Appointment.Empty(schedule.id, curr, curr.plus(duration)));
            }
        }
        return appointments;
    }

    private void requestAppointments() {
        ApiHttpClient.instance().getAppointments(schedule.id, new ResponseHandler<ArrayList<Appointment>>() {
            @Override
            public void handle(ArrayList<Appointment> result) {
            Log.d("MY_CUSTOM_LOG", String.valueOf(result.size()));
            Log.d("MY_CUSTOM_LOG", result.toString());
            HashMap<LocalTime, Appointment> clone = (HashMap<LocalTime, Appointment>) appointmentMap.clone();
            for (Appointment a: result) {
                clone.put(a.start, a);
            }
            AppointmentItemAdapter adapter = new AppointmentItemAdapter(getApplicationContext(), new ArrayList<>(clone.values()));
            appointmentsListView.setAdapter(adapter);
            }
        });
    }

    public void createAppointment(Appointment selected) {
        CreateAppointmentRequest req = new CreateAppointmentRequest(
            selected.scheduleId,
            QueueApp.getUser().id,
            date,
            selected.start,
            selected.end
        );

        ApiHttpClient.instance().createAppointment(req, new ResponseHandler<Boolean>() {
            @Override
            public void handle(Boolean result) {
            //todo false case
            Log.i("MY_CUSTOM_LOG", String.valueOf(result));
            if (result) {
                finish();
                startActivity(getIntent()); //todo put sorted stuff in intent?
            }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }
}
