package in.imranalam.app.sqlitecrud;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Created by IMRAN ALAM on 01/01/2020.
 */

public class EmployeeAdapter extends ArrayAdapter<Employee> {

    Context mCtx;
    int listLayoutRes;
    List<Employee> mEmployeeList;
    SQLiteDatabase mDatabase;

    public EmployeeAdapter(Context mCtx, int listLayoutRes, List<Employee> mEmployeeList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, mEmployeeList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.mEmployeeList = mEmployeeList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("ViewHolder") View view = inflater.inflate(listLayoutRes, null);

        final Employee mEmployee = mEmployeeList.get(position);


        TextView mTextViewName = view.findViewById(R.id.textViewName);
        TextView mTextViewDept = view.findViewById(R.id.textViewDepartment);
        TextView mTextViewSalary = view.findViewById(R.id.textViewSalary);
        TextView mTextViewJoiningDate = view.findViewById(R.id.textViewJoiningDate);

        mTextViewName.setText(mEmployee.getName());
        mTextViewDept.setText(mEmployee.getDept());
        mTextViewSalary.setText(String.format("$ %s", mEmployee.getSalary()));
        mTextViewJoiningDate.setText(mEmployee.getJoiningDate());

        MaterialButton mButtonDelete = view.findViewById(R.id.buttonDeleteEmployee);
        MaterialButton mButtonEdit = view.findViewById(R.id.buttonEditEmployee);

        //adding a click listener to button
        mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpdateEmployee(mEmployee);
            }
        });

        //the delete operation
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM employees WHERE id = ?";
                        mDatabase.execSQL(sql, new Integer[]{mEmployee.getId()});
                        mReloadEmployeesFromDatabase();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private void mUpdateEmployee(final Employee employee) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_update_employee, null);
        builder.setView(view);


        final EditText mEditTextName = view.findViewById(R.id.editTextName);
        final EditText mEditTextSalary = view.findViewById(R.id.editTextSalary);
        final Spinner mSpinnerDepartment = view.findViewById(R.id.spinnerDepartment);

        mEditTextName.setText(employee.getName());
        mEditTextSalary.setText(String.valueOf(employee.getSalary()));

        final AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.buttonUpdateEmployee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditTextName.getText().toString().trim();
                String salary = mEditTextSalary.getText().toString().trim();
                String dept = mSpinnerDepartment.getSelectedItem().toString();

                if (name.isEmpty()) {
                    mEditTextName.setError("Name can't be blank");
                    mEditTextName.requestFocus();
                    return;
                }

                if (salary.isEmpty()) {
                    mEditTextSalary.setError("Salary can't be blank");
                    mEditTextSalary.requestFocus();
                    return;
                }

                String sql = "UPDATE employees \n" +
                        "SET name = ?, \n" +
                        "department = ?, \n" +
                        "salary = ? \n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{name, dept, salary, String.valueOf(employee.getId())});
                Toast.makeText(mCtx, "Employee Updated", Toast.LENGTH_SHORT).show();
                mReloadEmployeesFromDatabase();

                dialog.dismiss();
            }
        });
    }

    private void mReloadEmployeesFromDatabase() {
        Cursor cursorEmployees = mDatabase.rawQuery("SELECT * FROM employees", null);
        if (cursorEmployees.moveToFirst()) {
            mEmployeeList.clear();
            do {
                mEmployeeList.add(new Employee(
                        cursorEmployees.getInt(0),
                        cursorEmployees.getString(1),
                        cursorEmployees.getString(2),
                        cursorEmployees.getString(3),
                        cursorEmployees.getString(4)
                ));
            } while (cursorEmployees.moveToNext());
        }
        cursorEmployees.close();
        notifyDataSetChanged();
    }
}
