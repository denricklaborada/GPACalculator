package dev.denrick.gpacalculator;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Denrick C. Laborada
 * @version 1.7
 * @since May 10, 2019
 */
public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyPref";
    private boolean doubleBackToExitPressedOnce = false;
    private List<EditText> courseList, gradeList, unitsList;
    private Button addBtn = null, deleteBtn = null, calculateBtn = null;
    private TextView gpaLbl = null;
    private SharedPreferences pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBtn = (Button) findViewById(R.id.addBtn);
        deleteBtn = (Button) findViewById(R.id.delBtn);
        calculateBtn = (Button) findViewById(R.id.calculateBtn);
        gpaLbl = (TextView) findViewById(R.id.gpaLbl);

        courseList = new ArrayList<EditText>();
        gradeList = new ArrayList<EditText>();
        unitsList = new ArrayList<EditText>();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCourse();
            }
        });
        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateGPA();
            }
        });

        pref = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int count = pref.getInt("count", -1);

        if (count != -1) {
            for (int i = 0; i < count; i++) {
                String course = pref.getString("course" + i, "");
                float grade = pref.getFloat("grade" + i, -1);
                int units = pref.getInt("units" + i, -1);

                addCourse(course, grade, units);
            }

            DecimalFormat form = new DecimalFormat("0.000");
            form.setRoundingMode(RoundingMode.HALF_UP);

            try {
                gpaLbl.setText(form.format(form.parse(Float.toString(pref.getFloat("gpa", 0.000f)))));
            } catch (Exception e) { e.printStackTrace(); }

        } else {
            addCourse();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveCurrentState();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveCurrentState();
    }

    public void saveCurrentState() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();

        int count = this.gradeList.size();

        editor.putInt("count", count);

        for (int i = 0; i < count; i++) {
            editor.putString("course" + i, this.courseList.get(i).getText().toString());

            try {
                editor.putFloat("grade" + i, Float.parseFloat(this.gradeList.get(i).getText().toString()));
            } catch (NumberFormatException e) {
                editor.putFloat("grade" + i, -1);
            }

            try {
                editor.putInt("units" + i, Integer.parseInt(this.unitsList.get(i).getText().toString()));
            } catch (NumberFormatException e) {
                editor.putInt("units" + i, -1);
            }
        }
        editor.putFloat("gpa", Float.parseFloat(this.gpaLbl.getText().toString()));
        editor.apply();
    }

    public void addCourse() {
        TableLayout tl = (TableLayout) findViewById(R.id.table);
        TextView count = (TextView) findViewById(R.id.numCoursesLbl);

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        EditText course = new EditText(this);
        course.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3));
        course.setHint("COURSE" + (this.courseList.size() + 1));
        tr.addView(course);
        this.courseList.add(course);

        EditText grade = new EditText(this);
        grade.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        grade.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        grade.setHint(R.string.gradehint);
        tr.addView(grade);
        this.gradeList.add(grade);

        EditText units = new EditText(this);
        units.setInputType(InputType.TYPE_CLASS_NUMBER);
        units.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        units.setHint(R.string.unitshint);
        tr.addView(units);
        this.unitsList.add(units);

        tl.addView(tr);
        count.setText(Integer.toString(this.courseList.size()));

        if (Integer.parseInt(count.getText().toString()) > 1) {
            this.deleteBtn.setText(R.string.delete);
        } else {
            this.deleteBtn.setText(R.string.clear);
        }
    }

    public void addCourse(String courseText, float gradeText, int unitsText) {
        TableLayout tl = (TableLayout) findViewById(R.id.table);
        TextView count = (TextView) findViewById(R.id.numCoursesLbl);

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        EditText course = new EditText(this);
        course.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3));
        course.setHint("COURSE" + (this.courseList.size() + 1));
        course.setText(courseText);
        tr.addView(course);
        this.courseList.add(course);

        EditText grade = new EditText(this);
        grade.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        grade.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        grade.setHint(R.string.gradehint);
        if (gradeText == -1) {
            grade.setText("");
        } else {
            grade.setText(Float.toString(gradeText));
        }
        tr.addView(grade);
        this.gradeList.add(grade);

        EditText units = new EditText(this);
        units.setInputType(InputType.TYPE_CLASS_NUMBER);
        units.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        units.setHint(R.string.unitshint);
        if (unitsText == -1) {
            units.setText("");
        } else {
            units.setText(Integer.toString(unitsText));
        }

        tr.addView(units);
        this.unitsList.add(units);

        tl.addView(tr);
        count.setText(Integer.toString(this.courseList.size()));

        if (Integer.parseInt(count.getText().toString()) > 1) {
            this.deleteBtn.setText(R.string.delete);
        } else {
            this.deleteBtn.setText(R.string.clear);
        }
    }

    public void deleteCourse() {
        if (this.courseList.size() > 1) {
            TableLayout tl = (TableLayout) findViewById(R.id.table);
            TextView count = (TextView) findViewById(R.id.numCoursesLbl);

            tl.removeView(tl.getChildAt(tl.getChildCount() - 1));

            this.courseList.remove(this.courseList.size() - 1);
            this.gradeList.remove(this.gradeList.size() - 1);
            this.unitsList.remove(this.unitsList.size() - 1);

            count.setText(Integer.toString(this.courseList.size()));

            if (this.courseList.size() == 1) {
                this.deleteBtn.setText(R.string.clear);
            }
        } else {
            this.courseList.get(0).setText("");
            this.gradeList.get(0).setText("");
            this.unitsList.get(0).setText("");

            this.gpaLbl.setText(R.string.gpa);
        }
    }

    public void calculateGPA() {
        int unitSum = 0;
        float product = 0.0f;

        Iterator gradeListIterator = this.gradeList.iterator();
        Iterator unitsListIterator = this.unitsList.iterator();

        while (gradeListIterator.hasNext() && unitsListIterator.hasNext()) {
            try {
                int unitTemp = Integer.parseInt(((EditText) unitsListIterator.next()).getText().toString());
                unitSum += unitTemp;
                product += ((float) unitTemp)* Float.parseFloat(((EditText) gradeListIterator.next()).getText().toString());
            } catch (NumberFormatException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Please fill out all required input fields.").setCancelable(false).setPositiveButton("OK", null);
                builder.create().show();
                return;
            } catch (Exception e2) { e2.printStackTrace(); }
        }

        float gpa = product / ((float) unitSum);
        DecimalFormat form = new DecimalFormat("0.000");
        form.setRoundingMode(RoundingMode.HALF_UP);
        try {
            gpaLbl.setText(form.format(form.parse(Float.toString(gpa))));
        } catch (ParseException e) { e.printStackTrace(); }
    }
}
