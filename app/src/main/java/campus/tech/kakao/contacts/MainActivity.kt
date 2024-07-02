package campus.tech.kakao.contacts

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import campus.tech.kakao.contacts.Contact.Companion.MIGRATION_1_2
import campus.tech.kakao.contacts.Contact.Companion.MIGRATION_2_3
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

	private lateinit var addButton: FloatingActionButton
	private lateinit var guidePhrase: TextView

	lateinit var database: ContactDatabase

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		guidePhrase = findViewById(R.id.guidePhrase)

		// Initialize database
		database = Room.databaseBuilder(
			applicationContext,
			ContactDatabase::class.java, "database-name"
		).addMigrations(MIGRATION_1_2, MIGRATION_2_3).allowMainThreadQueries().build()

		database.contactDao().deleteAll()

		//연락처 추가 버튼
		addButton = findViewById(R.id.addButton)
		addButton.setOnClickListener {
			startActivity(
				Intent(this, AddContactActivity::class.java)
			)
		}

	}
	//연락처 추가 후 연락처 목록 갱신
	override fun onResume() {
		super.onResume()
		val contactList = database.contactDao().getAll()
		if (contactList.isNotEmpty()) {
			guidePhrase.visibility = View.GONE
		}
		else{
			guidePhrase.visibility = View.VISIBLE
		}
		val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
		recyclerView.adapter = RecyclerViewAdapter(
			contactList, LayoutInflater.from(this), this)
		recyclerView.layoutManager = LinearLayoutManager(this)
	}
	//뒤로 가기 버튼 클릭 시 연락처 초기화
	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			database.contactDao().deleteAll()
		}
		return super.onKeyDown(keyCode, event)
	}
}