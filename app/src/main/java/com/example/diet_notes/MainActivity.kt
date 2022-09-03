package com.example.diet_notes

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    val dataModellist = mutableListOf<DataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = Firebase.database
        val myRef = database.getReference("MyMemo")

        val listView = findViewById<ListView>(R.id.mainLV)
        val adapterList = listViewAdapter(dataModellist)

        listView.adapter = adapterList

        myRef.child(Firebase.auth.currentUser!!.uid).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dataModellist.clear()

                for (dataModel in snapshot.children) {
                    Log.d("Data", dataModel.toString())
                    dataModellist.add(dataModel.getValue(DataModel::class.java)!!)
                }
                adapterList.notifyDataSetChanged()
               Log.d("DataModel",dataModellist.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        val writeButton = findViewById<ImageView>(R.id.writeBTN)
        writeButton.setOnClickListener {

            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("음식 메모 다이얼로그")
            val mAlerDialog = mBuilder.show()

            val DateSelectBTN = mAlerDialog.findViewById<Button>(R.id.dateSelectBTN)

            var DateText = ""

            DateSelectBTN?.setOnClickListener {
                val today = GregorianCalendar()
                val year : Int = today.get(Calendar.YEAR)
                val month : Int = today.get(Calendar.MONTH)
                val date : Int = today.get(Calendar.DATE)

                val dlg = DatePickerDialog(this,object : DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, date: Int) {
                        Log.d("Main","${year},${month+1},${date}")
                        DateSelectBTN.setText("${year},${month+1},${date}")

                        DateText = "${year},${month+1},${date}"
                    }

                },year,month,date)
                dlg.show()

            }
            val saveBTN = mAlerDialog.findViewById<Button>(R.id.saveBTN)
            saveBTN?.setOnClickListener {

                val memo = mAlerDialog.findViewById<EditText>(R.id.memo)?.text.toString()

                val database = Firebase.database
                val myRef = database.getReference("MyMemo").child(Firebase.auth.currentUser!!.uid)

                val model = DataModel(DateText,memo)

                myRef
                    .push()
                    .setValue(model)
            }
        }
    }
}
