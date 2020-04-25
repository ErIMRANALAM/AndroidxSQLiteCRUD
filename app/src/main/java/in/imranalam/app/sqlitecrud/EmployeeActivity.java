package in.imranalam.app.sqlitecrud;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IMRAN ALAM on 01/01/2020.
 */

public class EmployeeActivity extends AppCompatActivity {

    List<Employee> mEmployeeList;
    SQLiteDatabase mDatabase;
    ListView eListViewEmployees;
    EmployeeAdapter eAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        eListViewEmployees = findViewById(R.id.listViewEmployees);
        mEmployeeList = new ArrayList<>();

        //opening the database
        mDatabase = openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);
        //this method will display the employees in the list
        mShowEmployeesFromDatabase();
    }

    private void mShowEmployeesFromDatabase() {

        Cursor mCursorEmployees = mDatabase.rawQuery("SELECT * FROM employees", null);

        //if the cursor has some data
        if (mCursorEmployees.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                mEmployeeList.add(new Employee(
                        mCursorEmployees.getInt(0),
                        mCursorEmployees.getString(1),
                        mCursorEmployees.getString(2),
                        mCursorEmployees.getString(3),
                        mCursorEmployees.getString(4)
                ));
            } while (mCursorEmployees.moveToNext());
        }
        //closing the cursor
        mCursorEmployees.close();
        //creating the adapter object
        eAdapter = new EmployeeAdapter(this, R.layout.list_layout_employee, mEmployeeList, mDatabase);
        //adding the adapter to listView
        eListViewEmployees.setAdapter(eAdapter);
    }

}
