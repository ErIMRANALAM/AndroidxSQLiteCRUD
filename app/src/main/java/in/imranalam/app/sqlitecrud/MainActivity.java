package in.imranalam.app.sqlitecrud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IMRAN ALAM on 01/01/2020.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DATABASE_NAME = "employee_database";
    MaterialButton mAddEmp, mViewEmp;
    EditText mTextName, mTextSalary;
    Spinner mSpinnerDepartment;

    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewEmp = findViewById(R.id.btnViewEmployees);
        mAddEmp = findViewById(R.id.btnAddEmployee);
        mTextName = findViewById(R.id.editTextName);
        mTextSalary = findViewById(R.id.editTextSalary);
        mSpinnerDepartment = findViewById(R.id.spinnerDepartment);

        mAddEmp.setOnClickListener(this);
        mViewEmp.setOnClickListener(this);

        //creating a database
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        mCreateEmployeeTable();
    }

    private void mCreateEmployeeTable() {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS employees (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT employees_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    name varchar(200) NOT NULL,\n" +
                        "    department varchar(200) NOT NULL,\n" +
                        "    joiningdate datetime NOT NULL,\n" +
                        "    salary decimal(5,2) NOT NULL\n" +
                        ");"
        );
    }

    //this method will validate the name and salary
    //dept does not need validation as it is a spinner and it cannot be empty
    private boolean inputsAreCorrect(String name, String salary) {
        if (name.isEmpty()) {
            mTextName.setError("Please enter a name");
            mTextName.requestFocus();
            return false;
        }

        if (salary.isEmpty() || Integer.parseInt(salary) <= 0) {
            mTextSalary.setError("Please enter salary");
            mTextSalary.requestFocus();
            return false;
        }
        return true;
    }

    //In this method we will do the create operation
    private void addEmployee() {

        String eName = mTextName.getText().toString().trim();
        String eSalary = mTextSalary.getText().toString().trim();
        String eDept = mSpinnerDepartment.getSelectedItem().toString();

        //getting the current time for joining date
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String eJoiningDate = sdf.format(cal.getTime());

        //validating the inputs
        if (inputsAreCorrect(eName, eSalary)) {

            String insertSQL = "INSERT INTO employees \n" +
                    "(name, department, joiningdate, salary)\n" +
                    "VALUES \n" +
                    "(?, ?, ?, ?);";

            //using the same method execsql for inserting values
            //this time it has two parameters
            //first is the sql string and second is the parameters that is to be bind with the query
            mDatabase.execSQL(insertSQL, new String[]{eName, eDept, eJoiningDate, eSalary});

            Toast.makeText(this, "Employee Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddEmployee:
                addEmployee();
                break;
            case R.id.btnViewEmployees:
                startActivity(new Intent(this, EmployeeActivity.class));
                break;
        }
    }
}