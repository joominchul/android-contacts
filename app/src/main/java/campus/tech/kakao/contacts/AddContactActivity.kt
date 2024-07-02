package campus.tech.kakao.contacts

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import campus.tech.kakao.contacts.Contact.Companion.MIGRATION_1_2
import campus.tech.kakao.contacts.Contact.Companion.MIGRATION_2_3
import java.util.Calendar

class AddContactActivity : AppCompatActivity() {
    // Declare UI elements
    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var mail: EditText
    private lateinit var birth: TextView
    private var sex:String? = null
    private lateinit var female: RadioButton
    private lateinit var male: RadioButton
    private lateinit var memo: EditText
    private lateinit var save: TextView
    private lateinit var cancel: TextView
    private lateinit var moreText: TextView
    private lateinit var birthSexMemo: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcontact)
        setupUI()
        birth.setOnClickListener {
            showDatePickerDialog()
        }
        // 더보기 버튼 클릭 시
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
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).allowMainThreadQueries().build()

        // 저장 버튼 클릭 시
        save.setOnClickListener {
            if (name.text.isEmpty()) {
                Toast.makeText(this, "이름은 필수 값입니다", Toast.LENGTH_SHORT).show()
            }
            else if (phone.text.isEmpty()) {
                Toast.makeText(this, "전화 번호는 필수 값입니다", Toast.LENGTH_SHORT).show()
            }
            else{
                if (female.isChecked) {
                    sex = "여자"
                }
                else if (male.isChecked) {
                    sex = "남자"
                }
                // Insert data into database
                val contact = Contact(
                    name.text.toString(),
                    phone.text.toString(),
                    mail.text.toString(),
                    birth.text.toString(),
                    sex,
                    memo.text.toString()
                )
                database.contactDao().insert(contact)
                Toast.makeText(this, "저장이 완료 되었습니다", Toast.LENGTH_SHORT).show()
                finish()
            }

        }
        // 연락처 데이터 로그에 표시
        fun loadAllData(){
            if (database.contactDao() == null){
                Log.d("contacttest", "null")
            }
            else{
                val list = database.contactDao().getAll()
                list.forEach {
                    Log.d("contacttest", ""+it.id+", "+it.name+", "+it.phone+", "+it.email+", "+it.birth+", "+it.sex+", "+it.memo)
                }
            }
        }
        //취소 버튼 클릭 시
        cancel.setOnClickListener {
            Toast.makeText(this, "취소 되었습니다", Toast.LENGTH_SHORT).show()
            loadAllData()
            finish()
        }




    }

    private fun setupUI() {
        name = findViewById(R.id.name)
        phone = findViewById(R.id.phone)
        mail = findViewById(R.id.mail)
        birth = findViewById(R.id.birth)
        female = findViewById(R.id.female)
        male = findViewById(R.id.male)
        memo = findViewById(R.id.memo)
        save = findViewById(R.id.save)
        cancel = findViewById(R.id.cancel)
        birthSexMemo = findViewById(R.id.birthSexMemo)
        moreText = findViewById(R.id.moreText)
    }

    // 생일 선택 팝업창
    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this, {DatePicker, year:Int, month:Int, dayOfMonth:Int ->
                //월과 일 포맷을 00으로 설정
                var Month = "${month + 1}"
                if (month < 9) {
                    Month = "0${Month}"
                }
                var Day = "${dayOfMonth}"
                if (dayOfMonth < 10) {
                    Day = "0${Day}"
                }
                birth.text = "${year}.${Month}.${Day}"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }
    //팝업창 설정
    fun popupMessage(){
        //팝업 UI 설정
        val popupView = layoutInflater.inflate(R.layout.popup_message, null)
        val write = popupView.findViewById<TextView>(R.id.write)
        val exit = popupView.findViewById<TextView>(R.id.exit)

        val popupBuilder = AlertDialog.Builder(this).setView(popupView)
        val popupWindow = popupBuilder.show()
        //작성하기 버튼 클릭 시 팝업창 닫기
        write.setOnClickListener {
            popupWindow.dismiss()
        }
        //나가기 버튼 클릭 시 연락처 목록으로 이동
        exit.setOnClickListener {
            finish()
        }
    }

    //뒤로 가기 버튼 클릭 시
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //뒤로 가기 버튼 클릭하고 나머지 텍스트가 있을 경우
        if (keyCode == KeyEvent.KEYCODE_BACK
            && (name.text.isNotEmpty() || phone.text.isNotEmpty()
                    || mail.text.isNotEmpty() || birth.text.isNotEmpty()
                    || (female.isChecked || male.isChecked) || memo.text.isNotEmpty())) {
            popupMessage()
            return true
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK){
            finish()
        }
        return false
    }
}
