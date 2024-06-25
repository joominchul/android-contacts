package campus.tech.kakao.contacts

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    // Declare UI elements
    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var mail: EditText
    private lateinit var birth: TextView
    private lateinit var sex: TextView
    private lateinit var female: RadioButton
    private lateinit var male: RadioButton
    private lateinit var memo: EditText
    private lateinit var save: TextView
    private lateinit var cancel: TextView
    private lateinit var moreText: TextView
    private lateinit var birthSexMemo: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize UI elements
        name = findViewById(R.id.name)
        phone = findViewById(R.id.phone)
        mail = findViewById(R.id.mail)
        birth = findViewById(R.id.birth)
        sex = findViewById(R.id.sex)
        female = findViewById(R.id.female)
        male = findViewById(R.id.male)
        memo = findViewById(R.id.memo)
        save = findViewById(R.id.save)
        cancel = findViewById(R.id.cancel)
        birthSexMemo = findViewById(R.id.birthSexMemo)
        moreText = findViewById(R.id.moreText)
        birth.setOnClickListener {
            showDatePickerDialog()
        }
        moreText.setOnClickListener {
            if (moreText.visibility == View.VISIBLE) {
                moreText.visibility = View.GONE
                birthSexMemo.visibility = View.VISIBLE
            } else {
                moreText.visibility = View.VISIBLE
                birthSexMemo.visibility = View.GONE
            }
        }
        // Initialize database
        val database = Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()
        // Set click listener for save button
        save.setOnClickListener {
            if (name.text.isEmpty()) {
                Toast.makeText(this, "이름은 필수 값입니다", Toast.LENGTH_SHORT).show()
            }
            else if (phone.text.isEmpty()) {
                Toast.makeText(this, "전화 번호는 필수 값입니다", Toast.LENGTH_SHORT).show()
            }
            else{
                if (female.isChecked)
                // Insert data into database
                val contact = Contact(
                    name.text.toString(),
                    phone.text.toString().toInt(),
                    mail.text.toString(),
                    birth.text.toString(),
                    sex.text.toString(),
                    memo.text.toString()
                )
                database.contactDao().insert(contact)
                Toast.makeText(this, "저장이 완료 되었습니다", Toast.LENGTH_SHORT).show()
            }

        }
        // Set click listener for cancel button
        cancel.setOnClickListener {
            Toast.makeText(this, "취소 되었습니다", Toast.LENGTH_SHORT).show()
        }

    }

    // Show date picker dialog
    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this, {DatePicker, year:Int, month:Int, dayOfMonth:Int ->
                  val selectedDate = "$year.${month+1}.$dayOfMonth"
                birth.text = selectedDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }
}
